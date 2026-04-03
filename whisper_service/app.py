import logging
import os
import re
import subprocess
import tempfile
from threading import Lock
from pathlib import Path

import whisper
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from pydantic import BaseModel

LOGGER = logging.getLogger("whisper-service")
logging.basicConfig(level=logging.INFO)

WHISPER_MODEL = os.getenv("WHISPER_MODEL", "small").strip() or "small"
WHISPER_CACHE_DIR = os.getenv("WHISPER_CACHE_DIR", "/cache/whisper").strip() or "/cache/whisper"
DEFAULT_LANGUAGE = (
    os.getenv("WHISPER_DEFAULT_LANGUAGE")
    or os.getenv("WHISPER_LANGUAGE")
    or "auto"
).strip().lower()
MAX_AUDIO_SIZE_BYTES = int(os.getenv("WHISPER_MAX_AUDIO_SIZE_BYTES", "12582912"))
WHISPER_FP16 = os.getenv("WHISPER_FP16", "false").strip().lower() == "true"

MULTILINGUAL_INITIAL_PROMPT = (
    "This is a chat voice message. "
    "The language may be Vietnamese, English, or Japanese. "
    "Transcribe exactly in the original language."
)

LANGUAGE_INITIAL_PROMPTS = {
    "vi": "This is a Vietnamese chat voice message. Transcribe exactly with proper diacritics.",
    "en": "This is an English chat voice message. Transcribe exactly as spoken.",
    "ja": "This is a Japanese chat voice message. Transcribe exactly as spoken.",
}

SUPPORTED_LANGUAGE_CODES = {"vi", "en", "ja"}

LANGUAGE_ALIASES = {
    "vi-vn": "vi",
    "vietnamese": "vi",
    "en-us": "en",
    "en-gb": "en",
    "english": "en",
    "ja-jp": "ja",
    "jp": "ja",
    "japanese": "ja",
}

_AUDIO_NAME_RE = re.compile(r"[^a-zA-Z0-9._-]")

app = FastAPI(title="Whisper Speech To Text", version="1.0.0")
_model = None
_model_lock = Lock()


class SpeechToTextResponse(BaseModel):
    text: str


class HealthResponse(BaseModel):
    status: str
    model: str


@app.on_event("startup")
def load_model() -> None:
    _get_model()


def _get_model():
    global _model
    if _model is not None:
        return _model

    with _model_lock:
        if _model is None:
            cache_dir = Path(WHISPER_CACHE_DIR)
            cache_dir.mkdir(parents=True, exist_ok=True)

            LOGGER.info("Loading Whisper model: %s (cache: %s)", WHISPER_MODEL, cache_dir)
            _model = _load_model_with_cache_recovery(cache_dir)
            LOGGER.info("Whisper model loaded")

    return _model


def _load_model_with_cache_recovery(cache_dir: Path):
    try:
        return whisper.load_model(WHISPER_MODEL, download_root=str(cache_dir))
    except RuntimeError as exc:
        detail = str(exc).lower()
        if "checksum" not in detail and "sha256" not in detail:
            raise

        LOGGER.warning("Whisper cache checksum mismatch detected, clearing cache and retrying once")
        _clear_cached_model_file(cache_dir)
        return whisper.load_model(WHISPER_MODEL, download_root=str(cache_dir))


def _clear_cached_model_file(cache_dir: Path) -> None:
    candidate = cache_dir / f"{WHISPER_MODEL}.pt"
    if candidate.exists():
        candidate.unlink(missing_ok=True)


@app.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    return HealthResponse(status="ok", model=WHISPER_MODEL)


@app.post("/speech-to-text", response_model=SpeechToTextResponse)
async def speech_to_text(
    audio: UploadFile = File(...),
    language: str = Form(DEFAULT_LANGUAGE),
    prompt: str | None = Form(None),
) -> SpeechToTextResponse:
    if audio.filename is None or audio.filename.strip() == "":
        raise HTTPException(status_code=400, detail="audio filename is required")

    safe_name = _safe_filename(audio.filename)
    normalized_language = _normalize_language(language)
    normalized_prompt = _normalize_prompt(prompt)

    with tempfile.TemporaryDirectory(prefix="whisper-stt-") as tmp:
        source_path = Path(tmp) / safe_name
        total_size = 0

        with source_path.open("wb") as output:
            while True:
                chunk = await audio.read(1024 * 1024)
                if not chunk:
                    break

                total_size += len(chunk)
                if total_size > MAX_AUDIO_SIZE_BYTES:
                    raise HTTPException(status_code=413, detail="audio file is too large")

                output.write(chunk)

        await audio.close()

        if total_size == 0:
            raise HTTPException(status_code=400, detail="audio file is empty")

        normalized_audio = Path(tmp) / "normalized.wav"
        _convert_to_wav(source_path, normalized_audio)

        decode_options = {
            "task": "transcribe",
            "fp16": WHISPER_FP16,
            "temperature": 0.0,
        }

        if normalized_language is not None:
            decode_options["language"] = normalized_language

        if normalized_prompt:
            decode_options["initial_prompt"] = normalized_prompt
        elif normalized_language is None:
            decode_options["initial_prompt"] = MULTILINGUAL_INITIAL_PROMPT
        elif normalized_language in LANGUAGE_INITIAL_PROMPTS:
            decode_options["initial_prompt"] = LANGUAGE_INITIAL_PROMPTS[normalized_language]

        try:
            model = _get_model()
            result = model.transcribe(str(normalized_audio), **decode_options)
        except Exception as exc:
            LOGGER.exception("Whisper transcription failed")
            raise HTTPException(status_code=502, detail=f"whisper transcription failed: {exc}")

        text = str(result.get("text", "")).strip()
        return SpeechToTextResponse(text=text)


def _convert_to_wav(source_path: Path, target_path: Path) -> None:
    command = [
        "ffmpeg",
        "-y",
        "-i",
        str(source_path),
        "-ac",
        "1",
        "-ar",
        "16000",
        "-vn",
        str(target_path),
    ]

    process = subprocess.run(
        command,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        check=False,
    )

    if process.returncode != 0:
        LOGGER.error("ffmpeg conversion failed: %s", process.stderr)
        raise HTTPException(status_code=422, detail="unable to decode audio input")


def _safe_filename(filename: str) -> str:
    candidate = _AUDIO_NAME_RE.sub("_", filename)
    candidate = candidate.strip("._")
    if not candidate:
        candidate = "audio_upload.webm"

    if "." not in candidate:
        candidate += ".webm"

    return candidate


def _normalize_language(language: str | None) -> str | None:
    value = _canonicalize_language(language)
    if not value:
        value = _canonicalize_language(DEFAULT_LANGUAGE)

    if not value:
        value = "auto"

    if value == "auto":
        return None

    if value not in SUPPORTED_LANGUAGE_CODES:
        raise HTTPException(
            status_code=400,
            detail="invalid language: supported values are auto, vi, en, ja",
        )

    return value


def _canonicalize_language(language: str | None) -> str:
    value = (language or "").strip().lower().replace("_", "-")
    if not value:
        return ""

    return LANGUAGE_ALIASES.get(value, value)


def _normalize_prompt(prompt: str | None) -> str:
    if prompt is None:
        return ""

    value = prompt.strip()
    if len(value) > 240:
        value = value[:240]

    return value
