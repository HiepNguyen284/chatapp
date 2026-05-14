package com.group4.chatapp.unit.service

import com.group4.chatapp.dtos.user.UserDto
import com.group4.chatapp.dtos.user.UserProfileUpdateDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.Attachment
import com.group4.chatapp.models.User
import com.group4.chatapp.repositories.AttachmentRepository
import com.group4.chatapp.repositories.UserRepository
import com.group4.chatapp.services.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.mail.MailSendException
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.ErrorResponseException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock lateinit var repository: UserRepository
    @Mock lateinit var attachmentRepository: AttachmentRepository
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @Mock lateinit var s3Service: S3Service
    @Mock lateinit var fileTypeService: FileTypeService
    @Mock lateinit var messagingTemplate: SimpMessagingTemplate
    @Mock lateinit var userCacheService: UserCacheService
    @Mock lateinit var passwordResetTokenService: PasswordResetTokenService
    @Mock lateinit var emailService: EmailService

    @InjectMocks
    lateinit var userService: UserService

    private fun buildUser(id: Long = 1L, username: String = "testuser", displayName: String? = "Test User"): User {
        return User.builder()
            .id(id)
            .username(username)
            .password("encoded_password")
            .displayName(displayName)
            .build()
    }

    private fun setSecurityContext(user: User) {
        val authentication = mock<Authentication> {
            on { principal } doReturn user
        }
        val securityContext = mock<SecurityContext> {
            on { getAuthentication() } doReturn authentication
        }
        SecurityContextHolder.setContext(securityContext)
    }

    @Nested
    @DisplayName("createUser")
    inner class CreateUser {

        @Test
        fun `should create user successfully`() {
            val dto = UserDto("newuser", "password123")

            whenever(repository.existsByUsername("newuser")).thenReturn(false)
            whenever(passwordEncoder.encode("password123")).thenReturn("encoded")
            whenever(repository.save(any<User>())).thenAnswer { it.arguments[0] as User }

            userService.createUser(dto)

            verify(repository).save(argThat<User> { username == "newuser" })
        }

        @Test
        fun `should throw when username already exists`() {
            val dto = UserDto("existing", "password123")
            whenever(repository.existsByUsername("existing")).thenReturn(true)

            val exception = org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                userService.createUser(dto)
            }

            assertEquals(HttpStatus.CONFLICT, exception.statusCode)
        }
    }

    @Nested
    @DisplayName("getCurrentProfile")
    inner class GetCurrentProfile {

        @Test
        fun `should return profile when user exists`() {
            val user = buildUser()
            setSecurityContext(user)

            val result = userService.getCurrentProfile()

            assertNotNull(result)
            assertEquals("testuser", result.username)
        }

        @Test
        fun `should throw when user not authenticated`() {
            val securityContext = mock<SecurityContext> {
                on { getAuthentication() } doReturn null
            }
            SecurityContextHolder.setContext(securityContext)

            org.junit.jupiter.api.assertThrows<ErrorResponseException> {
                userService.getCurrentProfile()
            }
        }
    }

    @Nested
    @DisplayName("updateCurrentProfile")
    inner class UpdateCurrentProfile {

        @Test
        fun `should update displayName`() {
            val user = buildUser()
            setSecurityContext(user)

            val dto = mock<UserProfileUpdateDto> {
                on { displayName() } doReturn "New Name"
                on { avatar() } doReturn null
            }

            whenever(repository.save(any<User>())).thenAnswer { it.arguments[0] as User }

            val result = userService.updateCurrentProfile(dto)

            assertNotNull(result)
            verify(repository).save(any<User>())
            verify(userCacheService).invalidateUserCache("testuser")
        }

        @Test
        fun `should upload avatar when provided`() {
            val user = buildUser()
            setSecurityContext(user)

            val avatarFile = mock<MultipartFile> {
                on { isEmpty } doReturn false
                on { contentType } doReturn "image/png"
                on { originalFilename } doReturn "avatar.png"
            }

            val dto = mock<UserProfileUpdateDto> {
                on { displayName() } doReturn null
                on { avatar() } doReturn avatarFile
            }

            whenever(fileTypeService.getMimeType("image/png")).thenReturn("image")
            whenever(fileTypeService.getFileExtension("avatar.png")).thenReturn("png")
            whenever(fileTypeService.checkTypeInFileType("image", "png")).thenReturn(Attachment.FileType.IMAGE)
            whenever(s3Service.uploadAvatar(avatarFile)).thenReturn("https://s3.example.com/avatar.png")

            val attachment = Attachment.of("https://s3.example.com/avatar.png", Attachment.FileType.IMAGE)
            whenever(attachmentRepository.save(any<Attachment>())).thenReturn(attachment)
            whenever(repository.save(any<User>())).thenAnswer { it.arguments[0] as User }

            userService.updateCurrentProfile(dto)

            verify(s3Service).uploadAvatar(avatarFile)
            verify(attachmentRepository).save(any<Attachment>())
        }

        @Test
        fun `should throw when avatar is not an image`() {
            val user = buildUser()
            setSecurityContext(user)

            val avatarFile = mock<MultipartFile> {
                on { isEmpty } doReturn false
                on { contentType } doReturn "video/mp4"
                on { originalFilename } doReturn "video.mp4"
            }

            val dto = mock<UserProfileUpdateDto> {
                on { displayName() } doReturn null
                on { avatar() } doReturn avatarFile
            }

            whenever(fileTypeService.getMimeType("video/mp4")).thenReturn("video")
            whenever(fileTypeService.getFileExtension("video.mp4")).thenReturn("mp4")
            whenever(fileTypeService.checkTypeInFileType("video", "mp4")).thenReturn(Attachment.FileType.VIDEO)

            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.updateCurrentProfile(dto)
            }
        }
    }

    @Nested
    @DisplayName("searchUser")
    inner class SearchUser {

        @Test
        fun `should return results when found`() {
            val user = buildUser()
            setSecurityContext(user)

            val foundUser = buildUser(id = 2L, username = "founduser")
            whenever(repository.searchByKeyword(eq("found"), eq(1L), any<PageRequest>()))
                .thenReturn(listOf(foundUser))

            val results = userService.searchUser("found", 10)

            assertEquals(1, results.size)
            assertEquals("founduser", results[0].username)
        }

        @Test
        fun `should return empty list when no results`() {
            val user = buildUser()
            setSecurityContext(user)

            whenever(repository.searchByKeyword(eq("nonexistent"), eq(1L), any<PageRequest>()))
                .thenReturn(emptyList())

            val results = userService.searchUser("nonexistent", 10)

            assertTrue(results.isEmpty())
        }

        @Test
        fun `should return empty list when keyword is blank`() {
            val results = userService.searchUser("   ", 10)

            assertTrue(results.isEmpty())
        }

        @Test
        fun `should throw when limit is invalid`() {
            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.searchUser("test", 0)
            }

            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.searchUser("test", 21)
            }
        }
    }

    @Nested
    @DisplayName("changePassword")
    inner class ChangePassword {

        @Test
        fun `should change password successfully`() {
            val user = buildUser()
            setSecurityContext(user)

            whenever(passwordEncoder.matches("oldPassword", "encoded_password")).thenReturn(true)
            whenever(passwordEncoder.matches("newPassword123", "encoded_password")).thenReturn(false)
            whenever(passwordEncoder.encode("newPassword123")).thenReturn("new_encoded")
            whenever(repository.save(any<User>())).thenAnswer { it.arguments[0] as User }

            userService.changePassword("oldPassword", "newPassword123")

            verify(repository).save(any<User>())
            verify(userCacheService).invalidateUserCache("testuser")
        }

        @Test
        fun `should throw when old password is wrong`() {
            val user = buildUser()
            setSecurityContext(user)

            whenever(passwordEncoder.matches("wrongPassword", "encoded_password")).thenReturn(false)

            org.junit.jupiter.api.assertThrows<ErrorResponseException> {
                userService.changePassword("wrongPassword", "newPassword123")
            }
        }

        @Test
        fun `should throw when new password is same as old`() {
            val user = buildUser()
            setSecurityContext(user)

            whenever(passwordEncoder.matches("samePassword", "encoded_password")).thenReturn(true)
            whenever(passwordEncoder.matches("samePassword", "encoded_password")).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.changePassword("samePassword", "samePassword")
            }
        }

        @Test
        fun `should throw when new password is too short`() {
            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.changePassword("oldPassword", "short")
            }
        }
    }

    @Nested
    @DisplayName("requestPasswordReset")
    inner class RequestPasswordReset {

        @Test
        fun `should send email when user exists`() {
            val user = buildUser()
            whenever(repository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user))
            whenever(passwordResetTokenService.generateToken("testuser")).thenReturn("reset-token")

            userService.requestPasswordReset("testuser")

            verify(emailService).sendPasswordResetEmail("testuser", "testuser", "reset-token")
        }

        @Test
        fun `should do nothing when user not found (no info leak)`() {
            whenever(repository.findByUsername("nonexistent")).thenReturn(java.util.Optional.empty())

            userService.requestPasswordReset("nonexistent")

            verify(emailService, never()).sendPasswordResetEmail(any(), any(), any())
        }

        @Test
        fun `should do nothing when username is blank`() {
            userService.requestPasswordReset("   ")

            verify(repository, never()).findByUsername(any())
        }

        @Test
        fun `should revoke token and throw when email fails`() {
            val user = buildUser()
            whenever(repository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user))
            whenever(passwordResetTokenService.generateToken("testuser")).thenReturn("reset-token")
            whenever(emailService.sendPasswordResetEmail(any(), any(), any())).thenThrow(MailSendException("SMTP error"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.requestPasswordReset("testuser")
            }

            verify(passwordResetTokenService).revokeToken("reset-token")
        }
    }

    @Nested
    @DisplayName("resetPassword")
    inner class ResetPassword {

        @Test
        fun `should reset password when token is valid`() {
            val user = buildUser()
            whenever(passwordResetTokenService.validateToken("valid-token")).thenReturn("testuser")
            whenever(repository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user))
            whenever(passwordEncoder.matches("newPassword123", "encoded_password")).thenReturn(false)
            whenever(passwordEncoder.encode("newPassword123")).thenReturn("new_encoded")
            whenever(repository.save(any<User>())).thenAnswer { it.arguments[0] as User }

            userService.resetPassword("valid-token", "newPassword123")

            verify(repository).save(any<User>())
            verify(userCacheService).invalidateUserCache("testuser")
            verify(passwordResetTokenService).revokeByUsername("testuser")
        }

        @Test
        fun `should throw when token is invalid`() {
            whenever(passwordResetTokenService.validateToken("invalid-token"))
                .thenThrow(ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired password reset token"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.resetPassword("invalid-token", "newPassword123")
            }
        }

        @Test
        fun `should throw when new password is too short`() {
            org.junit.jupiter.api.assertThrows<ApiException> {
                userService.resetPassword("valid-token", "short")
            }
        }
    }

    @Nested
    @DisplayName("getUserByAuthentication")
    inner class GetUserByAuthentication {

        @Test
        fun `should return user from JWT principal`() {
            val user = buildUser()
            val jwt = mock<Jwt> {
                on { subject } doReturn "testuser"
            }
            val authentication = mock<Authentication> {
                on { principal } doReturn jwt
            }
            whenever(userCacheService.getCachedUser("testuser")).thenReturn(java.util.Optional.of(user))

            val result = userService.getUserByAuthentication(authentication)

            assertTrue(result.isPresent)
            assertEquals("testuser", result.get().username)
        }

        @Test
        fun `should return user from User principal`() {
            val user = buildUser()
            val authentication = mock<Authentication> {
                on { principal } doReturn user
            }

            val result = userService.getUserByAuthentication(authentication)

            assertTrue(result.isPresent)
            assertEquals("testuser", result.get().username)
        }

        @Test
        fun `should return empty when authentication is null`() {
            val result = userService.getUserByAuthentication(null)

            assertTrue(result.isEmpty)
        }
    }
}
