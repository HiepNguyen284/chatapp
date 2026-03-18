package com.group4.chatapp.services

import com.group4.chatapp.exceptions.ApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

@Service
class RustfsService(
    private val s3Client: S3Client,
    private val fileTypeService: FileTypeService
) {

    sealed interface UploadResult {
        val fileName: String

        data class Success(
            override val fileName: String,
            val secureUrl: String,
            val resourceType: String,
            val format: String
        ) : UploadResult

        data class Failure(
            override val fileName: String,
            val message: String
        ) : UploadResult
    }

    private val bucketReady = AtomicBoolean(false)

    @Value("\${rustfs.public-url}")
    private lateinit var rustfsPublicUrl: String

    @Value("\${rustfs.bucket-name}")
    private lateinit var bucketName: String

    fun uploadAvatar(file: MultipartFile): String {
        if (file.isEmpty) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Uploaded file is empty")
        }

        return runCatching {
            val key = generateKey(folder = "avatars", filename = file.originalFilename)
            uploadToS3(file, key)
            buildUrl(key)
        }.getOrElse { ex ->
            throw ApiException(HttpStatus.BAD_REQUEST, ex.message ?: "Upload avatar failed")
        }
    }

    fun uploadMany(files: List<MultipartFile>): List<UploadResult> {
        if (files.isEmpty()) {
            return emptyList()
        }

        return files.mapIndexed { index, file ->
            val fallbackName = "file_$index"
            val originalName = file.originalFilename?.takeIf { it.isNotBlank() } ?: fallbackName

            if (file.isEmpty) {
                return@mapIndexed UploadResult.Failure(originalName, "Uploaded file is empty")
            }

            runCatching {
                val resourceType = fileTypeService.getMimeType(file.contentType)
                val format = fileTypeService.getFileExtension(file.originalFilename)
                val key = generateKey(folder = resourceType, filename = file.originalFilename)

                uploadToS3(file, key)

                UploadResult.Success(
                    fileName = originalName,
                    secureUrl = buildUrl(key),
                    resourceType = resourceType,
                    format = format
                )
            }.getOrElse { ex ->
                UploadResult.Failure(
                    fileName = originalName,
                    message = ex.message ?: "Unknown upload error"
                )
            }
        }
    }

    private fun generateKey(folder: String, filename: String?): String {
        val sanitizedFilename = sanitizeFilename(filename)
        val timestamp = System.currentTimeMillis()
        return "$folder/${UUID.randomUUID()}_${timestamp}_$sanitizedFilename"
    }

    private fun sanitizeFilename(filename: String?): String {
        if (!StringUtils.hasText(filename)) {
            return "file"
        }

        return filename!!
            .trim()
            .replace(" ", "_")
            .replace("..", "_")
            .replace("/", "_")
            .replace("\\", "_")
    }

    private fun uploadToS3(file: MultipartFile, key: String) {
        ensureBucketExists()

        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType ?: "application/octet-stream")
            .build()

        file.inputStream.use { stream ->
            s3Client.putObject(request, RequestBody.fromInputStream(stream, file.size))
        }
    }

    @Synchronized
    private fun ensureBucketExists() {
        if (bucketReady.get()) {
            return
        }

        try {
            s3Client.headBucket(
                HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build()
            )
        } catch (_: NoSuchBucketException) {
            createBucket()
        } catch (ex: S3Exception) {
            if (ex.statusCode() == 404 || ex.statusCode() == 301) {
                createBucket()
            } else {
                throw ex
            }
        }

        ensurePublicReadPolicy()

        bucketReady.set(true)
    }

    private fun createBucket() {
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucketName)
                .build()
        )
    }

    private fun ensurePublicReadPolicy() {
        val policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Sid": "PublicReadGetObject",
                  "Effect": "Allow",
                  "Principal": "*",
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::$bucketName/*"]
                }
              ]
            }
        """.trimIndent()

        s3Client.putBucketPolicy(
            PutBucketPolicyRequest.builder()
                .bucket(bucketName)
                .policy(policy)
                .build()
        )
    }

    private fun buildUrl(key: String): String {
        val baseUrl = rustfsPublicUrl.trimEnd('/')
        return "$baseUrl/$bucketName/$key"
    }
}
