package com.group4.chatapp.services

import org.springframework.web.multipart.MultipartFile

interface FileStorageService {
    fun uploadFile(file: MultipartFile): String
    fun uploadMultipleFiles(files: List<MultipartFile>?): List<Map<String, Any>>?
}
