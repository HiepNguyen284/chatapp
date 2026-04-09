package com.group4.chatapp.repositories

import com.group4.chatapp.models.FcmToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FcmTokenRepository : JpaRepository<FcmToken, Long> {
    fun findByUserId(userId: Long): List<FcmToken>
    fun findAllByUserIdOrderByLastUsedDesc(userId: Long): List<FcmToken>
    fun findByUserIdAndToken(userId: Long, token: String): Optional<FcmToken>
    fun deleteByUserIdAndToken(userId: Long, token: String)
    fun findByToken(token: String): Optional<FcmToken>
    fun findByUserIdIn(userIds: List<Long>): List<FcmToken>
    fun deleteByLastUsedBefore(lastUsed: java.sql.Timestamp): Long
}
