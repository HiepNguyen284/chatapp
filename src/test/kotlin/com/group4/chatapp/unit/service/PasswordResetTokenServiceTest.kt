package com.group4.chatapp.unit.service

import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.services.PasswordResetTokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.test.util.ReflectionTestUtils
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class PasswordResetTokenServiceTest {

    @Mock lateinit var redisTemplate: StringRedisTemplate
    @Mock lateinit var valueOps: ValueOperations<String, String>

    lateinit var service: PasswordResetTokenService

    @BeforeEach
    fun setUp() {
        service = PasswordResetTokenService(redisTemplate)
        ReflectionTestUtils.setField(service, "tokenExpiryMinutes", 30L)
    }

    @Nested
    @DisplayName("generateToken")
    inner class GenerateToken {

        @Test
        fun `should generate token and store in Redis`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)

            val token = service.generateToken("testuser")

            assertNotNull(token)
            verify(valueOps, times(2)).set(any(), any(), any<Duration>())
        }

        @Test
        fun `should revoke existing tokens before generating new one`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("auth:password-reset:user:testuser")).thenReturn("old-token")

            service.generateToken("testuser")

            verify(redisTemplate).delete("auth:password-reset:token:old-token")
        }
    }

    @Nested
    @DisplayName("validateToken")
    inner class ValidateToken {

        @Test
        fun `should return username for valid token`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("auth:password-reset:token:valid-token")).thenReturn("testuser")

            val result = service.validateToken("valid-token")

            assertEquals("testuser", result)
        }

        @Test
        fun `should throw when token is expired or invalid`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("auth:password-reset:token:invalid-token")).thenReturn(null)

            org.junit.jupiter.api.assertThrows<ApiException> {
                service.validateToken("invalid-token")
            }
        }
    }

    @Nested
    @DisplayName("revokeToken")
    inner class RevokeToken {

        @Test
        fun `should revoke token and associated username key`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("auth:password-reset:token:some-token")).thenReturn("testuser")

            service.revokeToken("some-token")

            verify(redisTemplate).delete("auth:password-reset:token:some-token")
            verify(redisTemplate).delete("auth:password-reset:user:testuser")
        }
    }

    @Nested
    @DisplayName("revokeByUsername")
    inner class RevokeByUsername {

        @Test
        fun `should revoke token by username`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("auth:password-reset:user:testuser")).thenReturn("existing-token")

            service.revokeByUsername("testuser")

            verify(redisTemplate).delete("auth:password-reset:token:existing-token")
            verify(redisTemplate).delete("auth:password-reset:user:testuser")
        }
    }
}
