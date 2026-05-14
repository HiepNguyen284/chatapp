package com.group4.chatapp.unit.service

import com.group4.chatapp.services.RefreshTokenService
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class RefreshTokenServiceTest {

    @Mock lateinit var redisTemplate: StringRedisTemplate
    @Mock lateinit var hashOps: HashOperations<String, String, String>

    lateinit var service: RefreshTokenService

    @BeforeEach
    fun setUp() {
        service = RefreshTokenService(redisTemplate)
    }

    @Nested
    @DisplayName("storeRefreshToken")
    inner class StoreRefreshToken {

        @Test
        fun `should store token in Redis with TTL`() {
            whenever(redisTemplate.opsForHash<String, String>()).thenReturn(hashOps)

            service.storeRefreshToken("test-jti", "testuser", Duration.ofDays(30))

            verify(hashOps).put("auth:refresh:jti:test-jti", "username", "testuser")
            verify(hashOps).put(eq("auth:refresh:jti:test-jti"), eq("created_at"), any())
            verify(redisTemplate).expire("auth:refresh:jti:test-jti", Duration.ofDays(30))
        }
    }

    @Nested
    @DisplayName("isValidRefreshToken")
    inner class IsValidRefreshToken {

        @Test
        fun `should return true when token exists`() {
            whenever(redisTemplate.hasKey("auth:refresh:jti:valid-jti")).thenReturn(true)

            assertTrue(service.isValidRefreshToken("valid-jti"))
        }

        @Test
        fun `should return false when token does not exist`() {
            whenever(redisTemplate.hasKey("auth:refresh:jti:invalid-jti")).thenReturn(false)

            assertFalse(service.isValidRefreshToken("invalid-jti"))
        }

        @Test
        fun `should return true on Redis failure (graceful fallback)`() {
            whenever(redisTemplate.hasKey(any())).thenThrow(RuntimeException("Redis down"))

            assertTrue(service.isValidRefreshToken("some-jti"))
        }
    }

    @Nested
    @DisplayName("revokeRefreshToken")
    inner class RevokeRefreshToken {

        @Test
        fun `should delete token from Redis`() {
            service.revokeRefreshToken("jti-to-revoke")

            verify(redisTemplate).delete("auth:refresh:jti:jti-to-revoke")
        }
    }
}
