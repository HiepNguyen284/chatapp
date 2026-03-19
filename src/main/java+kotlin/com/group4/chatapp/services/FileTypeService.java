package com.group4.chatapp.services;

import com.group4.chatapp.models.Attachment;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileTypeService {

    private static final Set<String> IMAGE_FORMATS = Set.of(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "heic", "heif"
    );

    public String getFileExtension(String fileName) {

        if (!StringUtils.hasText(fileName)) {
            return "";
        }

        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }

        var ext = fileName.substring(index + 1).toLowerCase();
        log.debug("ext: {}", ext);

        return ext;
    }

    public String getMimeType(String contentType) {

        if (contentType == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Missing content type"
            );
        }

        if (contentType.startsWith("image/")) {
            return "image";
        }

        if (contentType.startsWith("video/")) {
            return "video";
        }

        return "raw";
    }

    public Attachment.FileType checkTypeInFileType(String resourceType, String format) {

        if (resourceType == null || format == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Missing file metadata"
            );
        }

        switch (resourceType.toLowerCase()) {
            case "image":
                return Attachment.FileType.IMAGE;

            case "video":
                return Attachment.FileType.VIDEO;

            case "raw":

                if (IMAGE_FORMATS.contains(format.toLowerCase())) {
                    return Attachment.FileType.IMAGE;
                }

                if (Attachment.isDocumentFormat(format)) {
                    return Attachment.FileType.DOCUMENT;
                }

                if (Attachment.isAudioFormat(format)) {
                    return Attachment.FileType.AUDIO;
                }

                return Attachment.FileType.RAW;

            default:
                throw new com.group4.chatapp.exceptions.ApiException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "File type is not supported!"
                );
        }
    }
}
