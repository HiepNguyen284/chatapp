package com.group4.chatapp.services

import com.group4.chatapp.exceptions.ApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.NoSuchBucketException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Service
class RustfsService(
    private val s3Client: S3Client,
    private val fileTypeService: FileTypeService
) : FileStorageService {

    private val bucketReady = AtomicBoolean(false)

    @Value("\${rustfs.url}")
    private lateinit var rustfsUrl: String

    @Value("\${rustfs.bucket-name}")
    private lateinit var bucketName: String

    override fun uploadFile(file: MultipartFile): String {
        return try {
            val key = generateKey("avatars", file.originalFilename)
            uploadToS3(file, key)
            buildUrl(key)
        } catch (e: Exception) {
            throw ApiException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    override fun uploadMultipleFiles(files: List<MultipartFile>?): List<Map<String, Any>>? {
        if (CollectionUtils.isEmpty(files)) {
            return null
        }

        val executor = Executors.newFixedThreadPool(files!!.size)
        val futures = getFutures(executor, files)
        val results = collectUploadResults(files, futures)

        executor.shutdown()
        val isTerminated = executor.awaitTermination(1, TimeUnit.MINUTES)

        return results
    }

    private fun getFutures(
        executor: ExecutorService,
        files: List<MultipartFile>
    ) = files.mapIndexed { index, file ->
        executor.submit<Map<String, Any>> {
            try {

                val resourceType = fileTypeService.getMimeType(file.contentType)
                val key = generateKey(resourceType, file.originalFilename)
                uploadToS3(file, key)

                mapOf(
                    "filename" to (file.originalFilename ?: "file_$index"),
                    "status" to "success",
                    "secure_url" to buildUrl(key),
                    "resource_type" to resourceType,
                    "format" to (fileTypeService.getFileExtension(file.originalFilename) ?: "")
                )

            } catch (e: Exception) {
                mapOf(
                    "filename" to (file.originalFilename ?: "file_$index"),
                    "status" to "error",
                    "message" to (e.message ?: "Unknown error")
                )
            }
        }
    }

    private fun collectUploadResults(
        files: List<MultipartFile>,
        futures: List<Future<Map<String, Any>>>
    ) = futures.mapIndexed { index, future ->
        try {
            future.get() ?: mapOf(
                "filename" to (files[index].originalFilename ?: "file_$index"),
                "status" to "error",
                "message" to "Upload failed"
            )
        } catch (e: Exception) {
            mapOf(
                "filename" to (files[index].originalFilename ?: "file_$index"),
                "status" to "error",
                "message" to (e.cause?.message ?: "Unknown error")
            )
        }
    }

    private fun generateKey(folder: String, filename: String?): String {
        val uuid = UUID.randomUUID()
        val timestamp = System.currentTimeMillis()
        val sanitizedFilename = filename?.replace(" ", "_") ?: "file"
        return "$folder/${uuid}_${timestamp}_$sanitizedFilename"
    }

    private fun uploadToS3(file: MultipartFile, key: String) {
        ensureBucketExists()

        val request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(request, RequestBody.fromInputStream(file.inputStream, file.size))
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
        } catch (e: S3Exception) {
            if (e.statusCode() == 404) {
                createBucket()
            } else {
                throw e
            }
        }

        bucketReady.set(true)
    }

    private fun createBucket() {
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucketName)
                .build()
        )
    }

    private fun buildUrl(key: String): String {
        val baseUrl = rustfsUrl.trimEnd('/')
        return "$baseUrl/$bucketName/$key"
    }
}
