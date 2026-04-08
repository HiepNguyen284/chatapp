package com.group4.chatapp.configs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.util.Optional

@Configuration
class FirebaseConfig {

    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    @Value("\${firebase.service-account-json:}")
    private lateinit var serviceAccountJson: String

    @Value("\${firebase.service-account-path:}")
    private lateinit var serviceAccountPath: String

    @Bean
    fun firebaseApp(): Optional<FirebaseApp> {
        val isEnabled = serviceAccountJson.isNotBlank() || serviceAccountPath.isNotBlank()
        
        if (!isEnabled) {
            logger.warn("Firebase is not configured. Set FIREBASE_SERVICE_ACCOUNT_JSON or FIREBASE_SERVICE_ACCOUNT_PATH to enable push notifications")
            return Optional.empty()
        }

        return try {
            val credentials = when {
                serviceAccountJson.isNotBlank() -> {
                    GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray()))
                }
                serviceAccountPath.isNotBlank() -> {
                    FileInputStream(serviceAccountPath).use { GoogleCredentials.fromStream(it) }
                }
                else -> {
                    logger.error("Firebase credentials not configured")
                    return Optional.empty()
                }
            }

            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()

            val app = FirebaseApp.initializeApp(options)
            logger.info("Firebase initialized successfully")
            Optional.of(app)
        } catch (e: Exception) {
            logger.error("Failed to initialize Firebase: {}", e.message, e)
            Optional.empty()
        }
    }
}
