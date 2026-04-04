package com.group4.chatapp.configs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    @Value("\${firebase.service-account-json:}")
    private lateinit var serviceAccountJson: String

    @Value("\${firebase.service-account-path:}")
    private lateinit var serviceAccountPath: String

    @Bean
    fun firebaseApp(): FirebaseApp {
        val credentials = when {
            serviceAccountJson.isNotBlank() -> {
                GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray()))
            }
            serviceAccountPath.isNotBlank() -> {
                FileInputStream(serviceAccountPath).use { GoogleCredentials.fromStream(it) }
            }
            else -> throw IllegalStateException("Firebase credentials not configured. Set FIREBASE_SERVICE_ACCOUNT_JSON or FIREBASE_SERVICE_ACCOUNT_PATH")
        }

        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()

        return FirebaseApp.initializeApp(options)
    }
}
