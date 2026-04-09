package com.group4.chatapp.services

import com.group4.chatapp.exceptions.ApiException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Duration
import java.util.Base64

@Service
class PasswordResetTokenService(
    private val redisTemplate: StringRedisTemplate,
) {

    @Value("\${password-reset.token-expiry-minutes:30}")
    private var tokenExpiryMinutes: Long = 30

    private val logger = LoggerFactory.getLogger(PasswordResetTokenService::class.java)
    private val secureRandom = SecureRandom()

    private fun tokenKey(token: String) = "auth:password-reset:token:$token"
    private fun usernameKey(username: String) = "auth:password-reset:user:$username"

    fun generateToken(username: String): String {
        try {
            revokeByUsername(username)

            val raw = ByteArray(32)
            secureRandom.nextBytes(raw)
            val token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw)
            val ttl = Duration.ofMinutes(tokenExpiryMinutes)

            redisTemplate.opsForValue().set(tokenKey(token), username, ttl)
            redisTemplate.opsForValue().set(usernameKey(username), token, ttl)

            return token
        } catch (e: Exception) {
            logger.warn("Redis unavailable, failed to issue password reset token for user={}: {}", username, e.message)
            throw ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Password reset is temporarily unavailable")
        }
    }

    fun validateToken(token: String): String {
        val username = try {
            redisTemplate.opsForValue().get(tokenKey(token))
        } catch (e: Exception) {
            logger.warn("Redis unavailable, failed to validate password reset token: {}", e.message)
            throw ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Password reset is temporarily unavailable")
        }

        if (username.isNullOrBlank()) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired password reset token")
        }

        return username
    }

    fun revokeToken(token: String) {
        try {
            val key = tokenKey(token)
            val username = redisTemplate.opsForValue().get(key)
            redisTemplate.delete(key)
            if (!username.isNullOrBlank()) {
                redisTemplate.delete(usernameKey(username))
            }
        } catch (e: Exception) {
            logger.warn("Redis unavailable, failed to revoke password reset token: {}", e.message)
        }
    }

    fun revokeByUsername(username: String) {
        try {
            val existingToken = redisTemplate.opsForValue().get(usernameKey(username))
            if (!existingToken.isNullOrBlank()) {
                redisTemplate.delete(tokenKey(existingToken))
            }
            redisTemplate.delete(usernameKey(username))
        } catch (e: Exception) {
            logger.warn("Redis unavailable, failed to revoke password reset token by username={}: {}", username, e.message)
        }
    }
}
