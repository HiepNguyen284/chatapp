package com.group4.chatapp.unit.service

import com.group4.chatapp.dtos.user.UserDto
import com.group4.chatapp.services.JwtsService
import com.group4.chatapp.services.RefreshTokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.*
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class JwtsServiceTest {

    @Mock lateinit var jwtEncoder: JwtEncoder
    @Mock lateinit var jwtDecoder: JwtDecoder
    @Mock lateinit var authenticationManager: AuthenticationManager
    @Mock lateinit var refreshTokenService: RefreshTokenService

    @InjectMocks
    lateinit var jwtsService: JwtsService

    @BeforeEach
    fun setUp() {
        ReflectionTestUtils.setField(jwtsService, "accessTokenLifetime", Duration.ofMinutes(15))
        ReflectionTestUtils.setField(jwtsService, "refreshTokenLifetime", Duration.ofDays(30))
    }

    private fun buildMockJwt(tokenValue: String = "mock-token", jti: String = "mock-jti", subject: String = "testuser"): Jwt {
        return Jwt.withTokenValue(tokenValue)
            .header("alg", "HS256")
            .claim("sub", subject)
            .claim("jti", jti)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(3600))
            .build()
    }

    @Nested
    @DisplayName("tokenObtainPair")
    inner class TokenObtainPair {

        @Test
        fun `should return access and refresh tokens on successful login`() {
            val dto = UserDto("testuser", "password123")

            val authentication = mock<Authentication> {
                on { name } doReturn "testuser"
            }

            whenever(authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()))
                .thenReturn(authentication)

            val accessJwt = buildMockJwt("access-token-value", "access-jti")
            val refreshJwt = buildMockJwt("refresh-token-value", "refresh-jti")

            whenever(jwtEncoder.encode(any<JwtEncoderParameters>()))
                .thenReturn(accessJwt)
                .thenReturn(refreshJwt)

            val result = jwtsService.tokenObtainPair(dto)

            assertNotNull(result)
            assertEquals("access-token-value", result.access())
            assertEquals("refresh-token-value", result.refresh())
            verify(refreshTokenService).storeRefreshToken(eq("refresh-jti"), eq("testuser"), any<Duration>())
        }

        @Test
        fun `should throw BadCredentialsException on wrong password`() {
            val dto = UserDto("testuser", "wrongpassword")

            whenever(authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()))
                .thenThrow(BadCredentialsException("Bad credentials"))

            org.junit.jupiter.api.assertThrows<BadCredentialsException> {
                jwtsService.tokenObtainPair(dto)
            }
        }
    }

    @Nested
    @DisplayName("refreshToken")
    inner class RefreshToken {

        @Test
        fun `should return new access token when refresh token is valid`() {
            val refreshJwt = buildMockJwt("refresh-value", "refresh-jti", "testuser")

            whenever(jwtDecoder.decode("refresh-value")).thenReturn(refreshJwt)
            whenever(refreshTokenService.isValidRefreshToken("refresh-jti")).thenReturn(true)

            val newAccessJwt = buildMockJwt("new-access-value", "new-access-jti")
            whenever(jwtEncoder.encode(any<JwtEncoderParameters>())).thenReturn(newAccessJwt)

            val result = jwtsService.refreshToken("refresh-value")

            assertNotNull(result)
            assertEquals("new-access-value", result.access())
        }

        @Test
        fun `should throw when refresh token is revoked`() {
            val refreshJwt = buildMockJwt("refresh-value", "revoked-jti", "testuser")

            whenever(jwtDecoder.decode("refresh-value")).thenReturn(refreshJwt)
            whenever(refreshTokenService.isValidRefreshToken("revoked-jti")).thenReturn(false)

            val exception = org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                jwtsService.refreshToken("refresh-value")
            }

            assertEquals(HttpStatus.UNAUTHORIZED, exception.statusCode)
        }

        @Test
        fun `should throw when refresh token is expired (decode fails)`() {
            whenever(jwtDecoder.decode("expired-token"))
                .thenThrow(JwtException("Token expired"))

            org.junit.jupiter.api.assertThrows<JwtException> {
                jwtsService.refreshToken("expired-token")
            }
        }
    }

    @Nested
    @DisplayName("revokeRefreshToken")
    inner class RevokeRefreshToken {

        @Test
        fun `should revoke refresh token successfully`() {
            val refreshJwt = buildMockJwt("refresh-value", "jti-to-revoke", "testuser")

            whenever(jwtDecoder.decode("refresh-value")).thenReturn(refreshJwt)

            jwtsService.revokeRefreshToken("refresh-value")

            verify(refreshTokenService).revokeRefreshToken("jti-to-revoke")
        }
    }
}
