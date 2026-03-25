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
    private val s3Service: S3Service,
    private val attachmentRepository: AttachmentRepository
) {

    fun getAttachmentOrThrow(id: Long): Attachment =
        attachmentRepository.findById(id).orElseThrow {
            ApiException(HttpStatus.NOT_FOUND, "Attachment not found")
        }

    fun getAttachments(dto: MessageSendDto): List<Attachment> {
        val multipartFiles = dto.attachmentsOrEmpty()
        if (multipartFiles.isEmpty()) {
            return emptyList()
        }

        val uploadedResults = s3Service.uploadMany(multipartFiles)
        if (uploadedResults.isEmpty()) {
            return emptyList()
        }

        val hasSuccess = uploadedResults.any { it is S3Service.UploadResult.Success }
        if (!hasSuccess) {

            val firstError = uploadedResults
                .filterIsInstance<S3Service.UploadResult.Failure>()
                .firstOrNull()
                ?.message
                ?: "Upload failed"

            throw ApiException(HttpStatus.BAD_REQUEST, firstError)
        }

        return uploadedResults
            .asSequence()
            .filterIsInstance<S3Service.UploadResult.Success>()
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
