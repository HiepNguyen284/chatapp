package com.group4.chatapp.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class RustfsConfig {

    @Value("\${rustfs.endpoint}")
    private lateinit var endpoint: String

    @Value("\${rustfs.access-key}")
    private lateinit var accessKey: String

    @Value("\${rustfs.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun s3Client() = S3Client.builder()
        .endpointOverride(URI.create(endpoint))
        .region(Region.US_EAST_1)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    accessKey,
                    secretKey
                )
            )
        )
        .forcePathStyle(true)
        .build()
}
