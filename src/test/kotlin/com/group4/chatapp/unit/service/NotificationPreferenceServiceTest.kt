package com.group4.chatapp.unit.service

import com.group4.chatapp.services.NotificationPreferenceService
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class NotificationPreferenceServiceTest {

    @Mock lateinit var redisTemplate: StringRedisTemplate
    @Mock lateinit var valueOps: ValueOperations<String, String>

    lateinit var service: NotificationPreferenceService

    @BeforeEach
    fun setUp() {
        service = NotificationPreferenceService(redisTemplate)
    }

    @Nested
    @DisplayName("isPushEnabled")
    inner class IsPushEnabled {

        @Test
        fun `should return true by default when not set`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("prefs:notifications:push:1")).thenReturn(null)

            assertTrue(service.isPushEnabled(1L))
        }

        @Test
        fun `should return true when set to true`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("prefs:notifications:push:1")).thenReturn("true")

            assertTrue(service.isPushEnabled(1L))
        }

        @Test
        fun `should return false when set to false`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get("prefs:notifications:push:1")).thenReturn("false")

            assertFalse(service.isPushEnabled(1L))
        }

        @Test
        fun `should default to true on Redis error`() {
            whenever(redisTemplate.opsForValue()).thenThrow(RuntimeException("Redis down"))

            assertTrue(service.isPushEnabled(1L))
        }
    }

    @Nested
    @DisplayName("setPushEnabled")
    inner class SetPushEnabled {

        @Test
        fun `should set enabled to true`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)

            service.setPushEnabled(1L, true)

            verify(valueOps).set("prefs:notifications:push:1", "true")
        }

        @Test
        fun `should set enabled to false`() {
            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)

            service.setPushEnabled(1L, false)

            verify(valueOps).set("prefs:notifications:push:1", "false")
        }
    }
}
