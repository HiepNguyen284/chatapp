package com.group4.chatapp.services

import com.group4.chatapp.models.FcmToken
import com.group4.chatapp.repositories.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userService: UserService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FcmTokenService::class.java)
    }

    @Transactional
    fun registerToken(token: String) {
        try {
            val user = userService.getUserOrThrows()
            val userId = user.id
            val existing = fcmTokenRepository.findByUserIdAndToken(userId, token)
            if (existing.isPresent) {
                val fcmToken = existing.get()
                fcmToken.lastUsed = Timestamp.from(Instant.now())
                fcmTokenRepository.save(fcmToken)
                LOGGER.debug("Updated FCM token for user {}", userId)
            } else {
                fcmTokenRepository.save(FcmToken(
                    user = user,
                    token = token,
                    lastUsed = Timestamp.from(Instant.now())
                ))
                LOGGER.debug("Registered new FCM token for user {}", userId)
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to register FCM token: {}", e.message, e)
            throw e
        }
    }

    fun getTokensForUser(userId: Long): List<String> {
        return try {
            fcmTokenRepository.findByUserId(userId).map { it.token }
        } catch (e: Exception) {
            LOGGER.error("Failed to retrieve FCM tokens for user {}: {}", userId, e.message)
            emptyList()
        }
    }

    fun getTokensForUsers(userIds: List<Long>): Map<Long, List<String>> {
        return try {
            fcmTokenRepository.findByUserIdIn(userIds)
                .groupBy({ it.user.id }, { it.token })
        } catch (e: Exception) {
            LOGGER.error("Failed to retrieve FCM tokens for users: {}", e.message)
            emptyMap()
        }
    }

    fun deleteInvalidToken(userId: Long, token: String) {
        try {
            fcmTokenRepository.deleteByUserIdAndToken(userId, token)
            LOGGER.debug("Deleted invalid FCM token for user {}", userId)
        } catch (e: Exception) {
            LOGGER.error("Failed to delete invalid FCM token for user {}: {}", userId, e.message)
        }
    }
}
