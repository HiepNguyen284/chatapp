package com.group4.chatapp.unit.service

import com.group4.chatapp.models.FcmToken
import com.group4.chatapp.models.User
import com.group4.chatapp.repositories.FcmTokenRepository
import com.group4.chatapp.services.FcmTokenService
import com.group4.chatapp.services.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class FcmTokenServiceTest {

    @Mock lateinit var fcmTokenRepository: FcmTokenRepository
    @Mock lateinit var userService: UserService

    @InjectMocks
    lateinit var fcmTokenService: FcmTokenService

    private fun buildUser(id: Long = 1L): User {
        return User.builder().id(id).username("user1").password("pass").build()
    }

    private fun buildFcmToken(user: User, token: String): FcmToken {
        return FcmToken(user = user, token = token, lastUsed = Timestamp.from(Instant.now()))
    }

    @Nested
    @DisplayName("registerToken")
    inner class RegisterToken {

        @Test
        fun `should register new token`() {
            val user = buildUser()
            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(fcmTokenRepository.findByUserIdAndToken(1L, "new-token")).thenReturn(Optional.empty())
            whenever(fcmTokenRepository.findByToken("new-token")).thenReturn(Optional.empty())
            whenever(fcmTokenRepository.save(any<FcmToken>())).thenAnswer { it.arguments[0] }
            whenever(fcmTokenRepository.findAllByUserIdOrderByLastUsedDesc(1L)).thenReturn(emptyList())

            fcmTokenService.registerToken("new-token")

            verify(fcmTokenRepository).save(any<FcmToken>())
        }

        @Test
        fun `should update lastUsed when token already exists`() {
            val user = buildUser()
            val existingToken = buildFcmToken(user, "existing-token")

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(fcmTokenRepository.findByUserIdAndToken(1L, "existing-token"))
                .thenReturn(Optional.of(existingToken))
            whenever(fcmTokenRepository.save(any<FcmToken>())).thenAnswer { it.arguments[0] }
            whenever(fcmTokenRepository.findAllByUserIdOrderByLastUsedDesc(1L)).thenReturn(listOf(existingToken))

            fcmTokenService.registerToken("existing-token")

            verify(fcmTokenRepository).save(existingToken)
        }
    }

    @Nested
    @DisplayName("getTokensForUser")
    inner class GetTokensForUser {

        @Test
        fun `should return token list`() {
            val user = buildUser()
            val token1 = buildFcmToken(user, "token1")
            val token2 = buildFcmToken(user, "token2")

            whenever(fcmTokenRepository.findAllByUserIdOrderByLastUsedDesc(1L))
                .thenReturn(listOf(token1, token2))

            val result = fcmTokenService.getTokensForUser(1L)

            assertEquals(2, result.size)
            assertEquals("token1", result[0])
        }

        @Test
        fun `should return empty list when no tokens`() {
            whenever(fcmTokenRepository.findAllByUserIdOrderByLastUsedDesc(1L))
                .thenReturn(emptyList())

            val result = fcmTokenService.getTokensForUser(1L)

            assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("pruneInactiveTokens")
    inner class PruneInactiveTokens {

        @Test
        fun `should delete old tokens`() {
            whenever(fcmTokenRepository.deleteByLastUsedBefore(any<Timestamp>())).thenReturn(5L)

            val result = fcmTokenService.pruneInactiveTokens()

            assertEquals(5L, result)
        }
    }

    @Nested
    @DisplayName("deleteInvalidToken")
    inner class DeleteInvalidToken {

        @Test
        fun `should delete invalid token`() {
            fcmTokenService.deleteInvalidToken(1L, "bad-token")

            verify(fcmTokenRepository).deleteByUserIdAndToken(1L, "bad-token")
        }
    }
}
