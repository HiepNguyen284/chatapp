package com.group4.chatapp.unit.service

import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.Attachment
import com.group4.chatapp.dtos.messages.MessageSendDto
import com.group4.chatapp.repositories.AttachmentRepository
import com.group4.chatapp.services.AttachmentService
import com.group4.chatapp.services.FileTypeService
import com.group4.chatapp.services.S3Service
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class AttachmentServiceTest {

    @Mock lateinit var fileTypeService: FileTypeService
    @Mock lateinit var s3Service: S3Service
    @Mock lateinit var attachmentRepository: AttachmentRepository

    @InjectMocks
    lateinit var attachmentService: AttachmentService

    @Nested
    @DisplayName("getAttachmentOrThrow")
    inner class GetAttachmentOrThrow {

        @Test
        fun `should return attachment when found`() {
            val attachment = Attachment.of("https://example.com/file.png", Attachment.FileType.IMAGE)
            attachment.id = 1L

            whenever(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment))

            val result = attachmentService.getAttachmentOrThrow(1L)

            assertNotNull(result)
            assertEquals(1L, result.id)
        }

        @Test
        fun `should throw when not found`() {
            whenever(attachmentRepository.findById(999L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                attachmentService.getAttachmentOrThrow(999L)
            }
        }
    }

    @Nested
    @DisplayName("getAttachments")
    inner class GetAttachments {

        @Test
        fun `should return empty list when no attachments`() {
            val dto = MessageSendDto(null, "text", null)

            val result = attachmentService.getAttachments(dto)

            assertTrue(result.isEmpty())
        }

        @Test
        fun `should process and save uploaded files`() {
            val file = mock<MultipartFile>()

            val dto = MessageSendDto(null, "text", listOf(file))

            val uploadResult = S3Service.UploadResult.Success(
                fileName = "photo.png",
                secureUrl = "https://s3.example.com/photo.png",
                resourceType = "image",
                format = "png"
            )

            whenever(s3Service.uploadMany(listOf(file))).thenReturn(listOf(uploadResult))
            whenever(fileTypeService.checkTypeInFileType("image", "png")).thenReturn(Attachment.FileType.IMAGE)
            whenever(attachmentRepository.save(any<Attachment>())).thenAnswer {
                val att = it.arguments[0] as Attachment
                att.id = 1L
                att
            }

            val result = attachmentService.getAttachments(dto)

            assertEquals(1, result.size)
            verify(attachmentRepository).save(any<Attachment>())
        }

        @Test
        fun `should throw when all uploads fail`() {
            val file = mock<MultipartFile>()

            val dto = MessageSendDto(null, "text", listOf(file))

            val uploadResult = S3Service.UploadResult.Failure(
                fileName = "fail.bin",
                message = "Upload failed"
            )

            whenever(s3Service.uploadMany(listOf(file))).thenReturn(listOf(uploadResult))

            org.junit.jupiter.api.assertThrows<ApiException> {
                attachmentService.getAttachments(dto)
            }
        }
    }

    @Nested
    @DisplayName("uploadAvatar")
    inner class UploadAvatar {

        @Test
        fun `should upload avatar image successfully`() {
            val file = mock<MultipartFile> {
                on { isEmpty } doReturn false
                on { contentType } doReturn "image/png"
                on { originalFilename } doReturn "avatar.png"
            }

            whenever(fileTypeService.getMimeType("image/png")).thenReturn("image")
            whenever(fileTypeService.getFileExtension("avatar.png")).thenReturn("png")
            whenever(fileTypeService.checkTypeInFileType("image", "png")).thenReturn(Attachment.FileType.IMAGE)
            whenever(s3Service.uploadAvatar(file)).thenReturn("https://s3.example.com/avatar.png")
            whenever(attachmentRepository.save(any<Attachment>())).thenAnswer {
                val att = it.arguments[0] as Attachment
                att.id = 1L
                att
            }

            val result = attachmentService.uploadAvatar(file)

            assertNotNull(result)
            verify(s3Service).uploadAvatar(file)
        }

        @Test
        fun `should throw when avatar is not an image`() {
            val file = mock<MultipartFile> {
                on { isEmpty } doReturn false
                on { contentType } doReturn "video/mp4"
                on { originalFilename } doReturn "video.mp4"
            }

            whenever(fileTypeService.getMimeType("video/mp4")).thenReturn("video")
            whenever(fileTypeService.getFileExtension("video.mp4")).thenReturn("mp4")
            whenever(fileTypeService.checkTypeInFileType("video", "mp4")).thenReturn(Attachment.FileType.VIDEO)

            org.junit.jupiter.api.assertThrows<ApiException> {
                attachmentService.uploadAvatar(file)
            }
        }

        @Test
        fun `should throw when file is empty`() {
            val file = mock<MultipartFile> {
                on { isEmpty } doReturn true
            }

            org.junit.jupiter.api.assertThrows<ApiException> {
                attachmentService.uploadAvatar(file)
            }
        }
    }
}
