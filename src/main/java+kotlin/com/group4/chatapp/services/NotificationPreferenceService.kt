package com.group4.chatapp.services

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class NotificationPreferenceService(
    private val redisTemplate: StringRedisTemplate,
) {

    private val logger = LoggerFactory.getLogger(NotificationPreferenceService::class.java)

    private fun keyForUser(userId: Long) = "prefs:notifications:push:$userId"

    fun isPushEnabled(userId: Long): Boolean {
        return try {
            val value = redisTemplate.opsForValue().get(keyForUser(userId))
            value?.toBooleanStrictOrNull() ?: true
        } catch (e: Exception) {
            logger.warn("Redis unavailable, defaulting push notifications to enabled for user {}: {}", userId, e.message)
            true
        }
    }

    fun setPushEnabled(userId: Long, enabled: Boolean) {
        try {
            redisTemplate.opsForValue().set(keyForUser(userId), enabled.toString())
        } catch (e: Exception) {
            logger.warn("Redis unavailable, cannot persist push notification preference for user {}: {}", userId, e.message)
        }
    }
}
