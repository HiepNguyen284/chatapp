package com.group4.chatapp.unit.service

import com.group4.chatapp.models.User
import com.group4.chatapp.repositories.UserRepository
import com.group4.chatapp.services.UserCacheService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class UserCacheServiceTest {

    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var redisTemplate: StringRedisTemplate
    @Mock lateinit var hashOps: HashOperations<String, String, String>

    lateinit var service: UserCacheService

    @BeforeEach
    fun setUp() {
        service = UserCacheService(userRepository, redisTemplate)
    }

    private fun buildUser(id: Long = 1L, username: String = "testuser"): User {
        return User.builder().id(id).username(username).password("pass").displayName("Test").build()
    }

    @Nested
    @DisplayName("getCachedUser")
    inner class GetCachedUser {

        @Test
        fun `should return from cache on hit`() {
            whenever(redisTemplate.opsForHash<String, String>()).thenReturn(hashOps)
            whenever(hashOps.entries("auth:user:testuser")).thenReturn(
                mapOf("id" to "1", "username" to "testuser", "displayName" to "Test")
            )

            val result = service.getCachedUser("testuser")

            assertTrue(result.isPresent)
            assertEquals(1L, result.get().id)
            verify(userRepository, never()).findByUsername(any())
        }

        @Test
        fun `should load from DB on cache miss and cache it`() {
            val user = buildUser()
            whenever(redisTemplate.opsForHash<String, String>()).thenReturn(hashOps)
            whenever(hashOps.entries("auth:user:testuser")).thenReturn(emptyMap())
            whenever(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user))

            val result = service.getCachedUser("testuser")

            assertTrue(result.isPresent)
            assertEquals("testuser", result.get().username)
            verify(userRepository).findByUsername("testuser")
            // Verify it tries to cache the user
            verify(hashOps).put(eq("auth:user:testuser"), eq("id"), eq("1"))
        }

        @Test
        fun `should return empty when user not in DB and not in cache`() {
            whenever(redisTemplate.opsForHash<String, String>()).thenReturn(hashOps)
            whenever(hashOps.entries("auth:user:nonexistent")).thenReturn(emptyMap())
            whenever(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty())

            val result = service.getCachedUser("nonexistent")

            assertTrue(result.isEmpty)
        }

        @Test
        fun `should fallback to DB on Redis error`() {
            val user = buildUser()
            whenever(redisTemplate.opsForHash<String, String>()).thenThrow(RuntimeException("Redis down"))
            whenever(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user))

            val result = service.getCachedUser("testuser")

            assertTrue(result.isPresent)
        }
    }

    @Nested
    @DisplayName("invalidateUserCache")
    inner class InvalidateUserCache {

        @Test
        fun `should delete cache key`() {
            service.invalidateUserCache("testuser")

            verify(redisTemplate).delete("auth:user:testuser")
        }
    }
}
