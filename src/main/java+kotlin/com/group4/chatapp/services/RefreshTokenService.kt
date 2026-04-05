package com.group4.chatapp.services

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RefreshTokenService(
    private val redisTemplate: StringRedisTemplate,
) {

    private val logger = LoggerFactory.getLogger(RefreshTokenService::class.java)

    private fun keyForJti(jti: String) = "auth:refresh:jti:$jti"

    fun storeRefreshToken(jti: String, username: String, ttl: Duration) {
        try {
            val key = keyForJti(jti)
            redisTemplate.opsForHash<String, String>().put(key, "username", username)
            redisTemplate.opsForHash<String, String>().put(key, "created_at", System.currentTimeMillis().toString())
            redisTemplate.expire(key, ttl)
        } catch (e: Exception) {
            logger.warn("Redis unavailable, skipping refresh token storage for jti={}: {}", jti, e.message)
        }
    }

    fun isValidRefreshToken(jti: String): Boolean {
        return try {
            val exists = redisTemplate.hasKey(keyForJti(jti)) == true
            if (!exists) {
                logger.warn("Refresh token jti={} not found in Redis (revoked or expired)", jti)
            }
            exists
        } catch (e: Exception) {
            logger.warn("Redis unavailable, allowing refresh token jti={} anyway: {}", jti, e.message)
            true
        }
    }

    fun revokeRefreshToken(jti: String) {
        try {
            redisTemplate.delete(keyForJti(jti))
        } catch (e: Exception) {
            logger.warn("Redis unavailable, skipping refresh token revocation for jti={}: {}", jti, e.message)
        }
    }
}
