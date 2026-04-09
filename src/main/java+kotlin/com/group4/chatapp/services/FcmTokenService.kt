package com.group4.chatapp.services

import com.group4.chatapp.models.FcmToken
import com.group4.chatapp.repositories.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.LoggerFactory
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userService: UserService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FcmTokenService::class.java)
        private const val MAX_TOKENS_PER_USER = 3
        private const val TOKEN_RETENTION_DAYS = 30L
    }

    @Transactional
    fun registerToken(token: String) {
        try {
            val user = userService.getUserOrThrows()
            val userId = user.id
            val now = Timestamp.from(Instant.now())

            val existing = fcmTokenRepository.findByUserIdAndToken(userId, token)
            if (existing.isPresent) {
                val fcmToken = existing.get()
                fcmToken.lastUsed = now
                fcmTokenRepository.save(fcmToken)
                LOGGER.debug("Updated FCM token for user {}", userId)
            } else {
                fcmTokenRepository.findByToken(token).ifPresent {
                    fcmTokenRepository.delete(it)
                }

                fcmTokenRepository.save(FcmToken(
                    user = user,
                    token = token,
                    lastUsed = now
                ))
                LOGGER.debug("Registered new FCM token for user {}", userId)
            }

            pruneUserTokens(userId)
        } catch (e: Exception) {
            LOGGER.error("Failed to register FCM token: {}", e.message, e)
            throw e
        }
    }

    fun getTokensForUser(userId: Long): List<String> {
        return try {
            fcmTokenRepository
                .findAllByUserIdOrderByLastUsedDesc(userId)
                .mapNotNull { it.token }
                .distinct()
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

    @Transactional
    fun pruneInactiveTokens(): Long {
        val cutoff = Timestamp.from(Instant.now().minus(TOKEN_RETENTION_DAYS, ChronoUnit.DAYS))
        return try {
            val deleted = fcmTokenRepository.deleteByLastUsedBefore(cutoff)
            if (deleted > 0) {
                LOGGER.info("Pruned {} inactive FCM tokens older than {} days", deleted, TOKEN_RETENTION_DAYS)
            }
            deleted
        } catch (e: Exception) {
            LOGGER.error("Failed to prune inactive FCM tokens: {}", e.message, e)
            0
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

    private fun pruneUserTokens(userId: Long) {
        val tokens = fcmTokenRepository.findAllByUserIdOrderByLastUsedDesc(userId)
        if (tokens.size <= MAX_TOKENS_PER_USER) {
            return
        }

        tokens.drop(MAX_TOKENS_PER_USER).forEach { stale ->
            fcmTokenRepository.delete(stale)
        }
        LOGGER.debug(
            "Pruned {} stale FCM token(s) for user {}",
            tokens.size - MAX_TOKENS_PER_USER,
            userId
        )
    }
}
