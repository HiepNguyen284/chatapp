package com.group4.chatapp.services

import com.group4.chatapp.dtos.messages.MessageSendDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.Attachment
import com.group4.chatapp.repositories.AttachmentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AttachmentService(
    private val fileTypeService: FileTypeService,
    private val rustfsService: RustfsService,
    private val attachmentRepository: AttachmentRepository
) {

    fun getAttachments(dto: MessageSendDto): List<Attachment> {
        val multipartFiles = dto.attachmentsOrEmpty()
        if (multipartFiles.isEmpty()) {
            return emptyList()
        }

        val uploadedResults = rustfsService.uploadMany(multipartFiles)
        if (uploadedResults.isEmpty()) {
            return emptyList()
        }

        val hasSuccess = uploadedResults.any { it is RustfsService.UploadResult.Success }
        if (!hasSuccess) {

            val firstError = uploadedResults
                .filterIsInstance<RustfsService.UploadResult.Failure>()
                .firstOrNull()
                ?.message
                ?: "Upload failed"

            throw ApiException(HttpStatus.BAD_REQUEST, firstError)
        }

        return uploadedResults
            .asSequence()
            .filterIsInstance<RustfsService.UploadResult.Success>()
            .map { result ->
                val attachmentType = fileTypeService.checkTypeInFileType(
                    result.resourceType,
                    result.format
                )

                Attachment.of(result.secureUrl, attachmentType)
            }
            .map(attachmentRepository::save)
            .toList()
    }
}
