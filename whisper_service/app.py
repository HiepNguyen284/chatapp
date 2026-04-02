import logging
import os
import re
import subprocess
import tempfile
from pathlib import Path

import whisper
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from pydantic import BaseModel

LOGGER = logging.getLogger("whisper-service")
logging.basicConfig(level=logging.INFO)

WHISPER_MODEL = os.getenv("WHISPER_MODEL", "small").strip() or "small"
DEFAULT_LANGUAGE = (
    os.getenv("WHISPER_DEFAULT_LANGUAGE")
    or os.getenv("WHISPER_LANGUAGE")
    or "vi"
).strip().lower()
MAX_AUDIO_SIZE_BYTES = int(os.getenv("WHISPER_MAX_AUDIO_SIZE_BYTES", "12582912"))
WHISPER_FP16 = os.getenv("WHISPER_FP16", "false").strip().lower() == "true"

VIETNAMESE_INITIAL_PROMPT = (
    "Đây là tin nhắn thoại trong ứng dụng chat. "
    "Ưu tiên nhận dạng chính xác tiếng Việt có dấu, tên riêng, số điện thoại và địa danh."
)

_AUDIO_NAME_RE = re.compile(r"[^a-zA-Z0-9._-]")
_LANGUAGE_RE = re.compile(r"^[a-z]{2,8}(?:-[a-z]{2,8})?$")

app = FastAPI(title="Whisper Speech To Text", version="1.0.0")
_model = None


class SpeechToTextResponse(BaseModel):
    text: str


class HealthResponse(BaseModel):
    status: str
    model: str


@app.on_event("startup")
def load_model() -> None:
    global _model

    LOGGER.info("Loading Whisper model: %s", WHISPER_MODEL)
    _model = whisper.load_model(WHISPER_MODEL)
    LOGGER.info("Whisper model loaded")


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
        elif normalized_language == "vi":
            decode_options["initial_prompt"] = VIETNAMESE_INITIAL_PROMPT

        try:
            result = _model.transcribe(str(normalized_audio), **decode_options)
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
    value = (language or DEFAULT_LANGUAGE).strip().lower().replace("_", "-")
    if value == "auto":
        return None

    if not _LANGUAGE_RE.match(value):
        raise HTTPException(status_code=400, detail="invalid language")

    return value


def _normalize_prompt(prompt: str | None) -> str:
    if prompt is None:
        return ""

    value = prompt.strip()
    if len(value) > 240:
        value = value[:240]

    return value
