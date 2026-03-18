package com.group4.chatapp.configs

import com.group4.chatapp.services.FileStorageService
import com.group4.chatapp.services.RustfsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FileStorageConfig {

    @Bean
    fun fileStorageService(rustfsService: RustfsService): FileStorageService {
        return rustfsService
    }
}
