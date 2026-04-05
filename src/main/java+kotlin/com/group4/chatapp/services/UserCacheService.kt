package com.group4.chatapp.services

import com.group4.chatapp.models.User
import com.group4.chatapp.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Optional

@Service
class UserCacheService(
    private val userRepository: UserRepository,
    private val redisTemplate: StringRedisTemplate,
) {

    private val logger = LoggerFactory.getLogger(UserCacheService::class.java)

    private fun keyForUsername(username: String) = "auth:user:$username"

    fun getCachedUser(username: String): Optional<User> {
        try {
            val key = keyForUsername(username)
            val cached = redisTemplate.opsForHash<String, String>().entries(key)
            if (cached.isNotEmpty() && cached.containsKey("id")) {
                val user = User.builder()
                    .id(cached["id"]!!.toLong())
                    .username(cached["username"] ?: username)
                    .displayName(cached["displayName"])
                    .build()
                return Optional.of(user)
            }
        } catch (e: Exception) {
            logger.debug("Redis unavailable for user cache lookup: {}", e.message)
        }

        val user = userRepository.findByUsername(username)
        user.ifPresent { cacheUser(it) }
        return user
    }

    fun cacheUser(user: User) {
        try {
            val key = keyForUsername(user.username)
            val ops = redisTemplate.opsForHash<String, String>()
            ops.put(key, "id", user.id.toString())
            ops.put(key, "username", user.username)
            ops.put(key, "displayName", user.displayName ?: "")
            redisTemplate.expire(key, Duration.ofHours(1))
        } catch (e: Exception) {
            logger.warn("Redis unavailable, skipping user cache for {}: {}", user.username, e.message)
        }
    }

    fun invalidateUserCache(username: String) {
        try {
            redisTemplate.delete(keyForUsername(username))
        } catch (e: Exception) {
            logger.warn("Redis unavailable, skipping user cache invalidation for {}: {}", username, e.message)
        }
    }
}
