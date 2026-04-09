package com.group4.chatapp.services

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
) {

    @Value("\${spring.mail.from:}")
    private lateinit var fromEmail: String

    @Value("\${password-reset.frontend-url:http://localhost:3000/reset-password}")
    private lateinit var passwordResetFrontendUrl: String

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendPasswordResetEmail(username: String, recipientEmail: String, resetToken: String) {
        val resetLink = "$passwordResetFrontendUrl?token=$resetToken"

        val message = SimpleMailMessage()
        if (fromEmail.isNotBlank()) {
            message.from = fromEmail
        }
        message.setTo(recipientEmail)
        message.subject = "Reset your ChatApp password"
        message.text = """
            Hello $username,

            We received a request to reset your password.
            Open this link to set a new password:
            $resetLink

            This link will expire soon. If you did not request this, you can ignore this email.
        """.trimIndent()

        mailSender.send(message)
        logger.info("Password reset email sent to user={}", username)
    }
}
