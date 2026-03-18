package com.group4.chatapp.services;

import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.repositories.AttachmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileTypeService fileTypeService;
    private final FileStorageService fileStorageService;

    private final AttachmentRepository attachmentRepository;

    public List<Attachment> getAttachments(MessageSendDto dto) {

        var attachments = dto.getAttachments();
        if (CollectionUtils.isEmpty(attachments)) {
            return List.of();
        }

        var uploadedFiles = fileStorageService.uploadMultipleFiles(attachments);

        if (CollectionUtils.isEmpty(uploadedFiles)) {
            return List.of();
        }

        var anySuccess = uploadedFiles.stream()
            .anyMatch(file -> "success".equals(file.get("status")));

        if (!anySuccess) {
            var firstError = uploadedFiles.stream()
                .map(file -> file.get("message"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .findFirst()
                .orElse("Upload failed");

            throw new ApiException(HttpStatus.BAD_REQUEST, firstError);
        }

        return uploadedFiles
            .stream()
            .map((file) -> {

                var isSuccess = file.get("status").equals("success");
                if (!isSuccess) {
                    return null;
                }

                var resourceType = (String) file.get("resource_type");
                var source = (String) file.get("secure_url");
                var format = (String) file.get("format");

                var type = fileTypeService.checkTypeInFileType(resourceType, format);

                var attachment = Attachment.builder()
                    .source(source)
                    .type(type)
                    .build();

                return attachmentRepository.save(attachment);

            })
            .filter(Objects::nonNull)
            .toList();
    }
}
