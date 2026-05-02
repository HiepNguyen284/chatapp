package com.group4.chatapp.unit.service

import com.group4.chatapp.models.Attachment
import com.group4.chatapp.services.FileTypeService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals

class FileTypeServiceTest {

    private val fileTypeService = FileTypeService()

    @Nested
    @DisplayName("getFileExtension")
    inner class GetFileExtension {

        @Test
        fun `should extract extension from filename`() {
            assertEquals("png", fileTypeService.getFileExtension("photo.png"))
            assertEquals("jpg", fileTypeService.getFileExtension("image.jpg"))
            assertEquals("pdf", fileTypeService.getFileExtension("doc.pdf"))
        }

        @Test
        fun `should return empty for filename without extension`() {
            assertEquals("", fileTypeService.getFileExtension("noextension"))
        }

        @Test
        fun `should return empty for null or blank filename`() {
            assertEquals("", fileTypeService.getFileExtension(null))
            assertEquals("", fileTypeService.getFileExtension(""))
            assertEquals("", fileTypeService.getFileExtension("   "))
        }

        @Test
        fun `should handle multiple dots`() {
            assertEquals("gz", fileTypeService.getFileExtension("archive.tar.gz"))
        }

        @Test
        fun `should return lowercase extension`() {
            assertEquals("png", fileTypeService.getFileExtension("PHOTO.PNG"))
        }
    }

    @Nested
    @DisplayName("getMimeType")
    inner class GetMimeType {

        @Test
        fun `should return image for image content type`() {
            assertEquals("image", fileTypeService.getMimeType("image/png"))
            assertEquals("image", fileTypeService.getMimeType("image/jpeg"))
        }

        @Test
        fun `should return video for video content type`() {
            assertEquals("video", fileTypeService.getMimeType("video/mp4"))
        }

        @Test
        fun `should return raw for other content types`() {
            assertEquals("raw", fileTypeService.getMimeType("application/pdf"))
            assertEquals("raw", fileTypeService.getMimeType("text/plain"))
        }

        @Test
        fun `should throw when content type is null`() {
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                fileTypeService.getMimeType(null)
            }
        }
    }

    @Nested
    @DisplayName("checkTypeInFileType")
    inner class CheckTypeInFileType {

        @Test
        fun `should return IMAGE for image resource type`() {
            assertEquals(Attachment.FileType.IMAGE, fileTypeService.checkTypeInFileType("image", "png"))
        }

        @Test
        fun `should return VIDEO for video resource type`() {
            assertEquals(Attachment.FileType.VIDEO, fileTypeService.checkTypeInFileType("video", "mp4"))
        }

        @Test
        fun `should return DOCUMENT for raw document format`() {
            assertEquals(Attachment.FileType.DOCUMENT, fileTypeService.checkTypeInFileType("raw", "pdf"))
            assertEquals(Attachment.FileType.DOCUMENT, fileTypeService.checkTypeInFileType("raw", "docx"))
        }

        @Test
        fun `should return AUDIO for raw audio format`() {
            assertEquals(Attachment.FileType.AUDIO, fileTypeService.checkTypeInFileType("raw", "mp3"))
            assertEquals(Attachment.FileType.AUDIO, fileTypeService.checkTypeInFileType("raw", "wav"))
        }

        @Test
        fun `should return IMAGE for raw image format`() {
            assertEquals(Attachment.FileType.IMAGE, fileTypeService.checkTypeInFileType("raw", "jpg"))
            assertEquals(Attachment.FileType.IMAGE, fileTypeService.checkTypeInFileType("raw", "png"))
        }

        @Test
        fun `should return RAW for unknown raw format`() {
            assertEquals(Attachment.FileType.RAW, fileTypeService.checkTypeInFileType("raw", "xyz"))
        }

        @Test
        fun `should throw for unsupported resource type`() {
            org.junit.jupiter.api.assertThrows<com.group4.chatapp.exceptions.ApiException> {
                fileTypeService.checkTypeInFileType("unknown", "bin")
            }
        }

        @Test
        fun `should throw when parameters are null`() {
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                fileTypeService.checkTypeInFileType(null, "png")
            }
            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                fileTypeService.checkTypeInFileType("image", null)
            }
        }
    }
}
