package com.group4.chatapp.services

import com.group4.chatapp.models.FcmToken
import com.group4.chatapp.repositories.FcmTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userService: UserService
) {

    @Transactional
    fun registerToken(token: String) {
        val user = userService.getUserOrThrows()
        val userId = user.id
        val existing = fcmTokenRepository.findByUserIdAndToken(userId, token)
        if (existing.isPresent) {
            val fcmToken = existing.get()
            fcmToken.lastUsed = Timestamp.from(Instant.now())
            fcmTokenRepository.save(fcmToken)
        } else {
            fcmTokenRepository.save(FcmToken(
                user = user,
                token = token,
                lastUsed = Timestamp.from(Instant.now())
            ))
        }
    }

    fun getTokensForUser(userId: Long): List<String> {
        return fcmTokenRepository.findByUserId(userId).map { it.token }
    }

    fun getTokensForUsers(userIds: List<Long>): Map<Long, List<String>> {
        return fcmTokenRepository.findByUserIdIn(userIds)
            .groupBy({ it.user.id }, { it.token })
    }

    fun deleteInvalidToken(userId: Long, token: String) {
        fcmTokenRepository.deleteByUserIdAndToken(userId, token)
    }
}
