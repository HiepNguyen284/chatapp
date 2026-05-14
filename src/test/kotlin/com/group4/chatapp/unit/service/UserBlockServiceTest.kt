package com.group4.chatapp.unit.service

import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.Invitation
import com.group4.chatapp.models.User
import com.group4.chatapp.models.UserBlock
import com.group4.chatapp.repositories.InvitationRepository
import com.group4.chatapp.repositories.UserBlockRepository
import com.group4.chatapp.services.UserBlockService
import com.group4.chatapp.services.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class UserBlockServiceTest {

    @Mock lateinit var userService: UserService
    @Mock lateinit var userBlockRepository: UserBlockRepository
    @Mock lateinit var invitationRepository: InvitationRepository
    @Mock lateinit var messagingTemplate: SimpMessagingTemplate

    @InjectMocks
    lateinit var userBlockService: UserBlockService

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    @Nested
    @DisplayName("blockByUsername")
    inner class BlockByUsername {

        @Test
        fun `should block user successfully`() {
            val me = buildUser(1L, "me")
            val target = buildUser(2L, "target")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("target")).thenReturn(Optional.of(target))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.save(any<UserBlock>())).thenAnswer { it.arguments[0] }

            // For publishStatusToBothUsers
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false).thenReturn(true)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            userBlockService.blockByUsername("target")

            verify(userBlockRepository).save(any<UserBlock>())
            verify(invitationRepository).deletePendingBetweenUsers(1L, 2L, Invitation.Status.PENDING)
            verify(messagingTemplate, times(2)).convertAndSendToUser(any(), eq("/queue/users/block/"), any())
        }

        @Test
        fun `should throw when blocking self`() {
            val me = buildUser(1L, "me")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("me")).thenReturn(Optional.of(me))

            org.junit.jupiter.api.assertThrows<ApiException> {
                userBlockService.blockByUsername("me")
            }
        }

        @Test
        fun `should be idempotent when already blocked`() {
            val me = buildUser(1L, "me")
            val target = buildUser(2L, "target")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("target")).thenReturn(Optional.of(target))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(true)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            userBlockService.blockByUsername("target")

            verify(userBlockRepository, never()).save(any<UserBlock>())
        }

        @Test
        fun `should throw when user not found`() {
            val me = buildUser(1L, "me")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("nonexistent")).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                userBlockService.blockByUsername("nonexistent")
            }
        }
    }

    @Nested
    @DisplayName("unblockByUsername")
    inner class UnblockByUsername {

        @Test
        fun `should unblock user successfully`() {
            val me = buildUser(1L, "me")
            val target = buildUser(2L, "target")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("target")).thenReturn(Optional.of(target))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            userBlockService.unblockByUsername("target")

            verify(userBlockRepository).deleteByBlocker_IdAndBlocked_Id(1L, 2L)
            verify(messagingTemplate, times(2)).convertAndSendToUser(any(), eq("/queue/users/block/"), any())
        }
    }

    @Nested
    @DisplayName("getBlockStatus")
    inner class GetBlockStatus {

        @Test
        fun `should return not blocked for both`() {
            val me = buildUser(1L, "me")
            val other = buildUser(2L, "other")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("other")).thenReturn(Optional.of(other))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            val result = userBlockService.getBlockStatus("other")

            assertFalse(result.blockedByMe())
            assertFalse(result.blockedByUser())
        }

        @Test
        fun `should return blockedByMe true for one-way block`() {
            val me = buildUser(1L, "me")
            val other = buildUser(2L, "other")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("other")).thenReturn(Optional.of(other))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(true)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            val result = userBlockService.getBlockStatus("other")

            assertTrue(result.blockedByMe())
            assertFalse(result.blockedByUser())
        }

        @Test
        fun `should return both true for mutual block`() {
            val me = buildUser(1L, "me")
            val other = buildUser(2L, "other")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("other")).thenReturn(Optional.of(other))
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(true)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(true)

            val result = userBlockService.getBlockStatus("other")

            assertTrue(result.blockedByMe())
            assertTrue(result.blockedByUser())
        }

        @Test
        fun `should throw when checking self`() {
            val me = buildUser(1L, "me")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userService.getUserByUsername("me")).thenReturn(Optional.of(me))

            org.junit.jupiter.api.assertThrows<ApiException> {
                userBlockService.getBlockStatus("me")
            }
        }
    }

    @Nested
    @DisplayName("listBlockedUsers")
    inner class ListBlockedUsers {

        @Test
        fun `should return blocked users list`() {
            val me = buildUser(1L, "me")
            val blocked1 = buildUser(2L, "blocked1")
            val blocked2 = buildUser(3L, "blocked2")

            val block1 = UserBlock.builder().id(1L).blocker(me).blocked(blocked1).build()
            val block2 = UserBlock.builder().id(2L).blocker(me).blocked(blocked2).build()

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userBlockRepository.findByBlocker_Id(1L)).thenReturn(listOf(block1, block2))

            val result = userBlockService.listBlockedUsers()

            assertEquals(2, result.size)
        }

        @Test
        fun `should return empty list when no blocks`() {
            val me = buildUser(1L, "me")

            whenever(userService.getUserOrThrows()).thenReturn(me)
            whenever(userBlockRepository.findByBlocker_Id(1L)).thenReturn(emptyList())

            val result = userBlockService.listBlockedUsers()

            assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("isBlockedEitherWay")
    inner class IsBlockedEitherWay {

        @Test
        fun `should return true when A blocks B`() {
            val a = buildUser(1L, "a")
            val b = buildUser(2L, "b")

            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(true)

            assertTrue(userBlockService.isBlockedEitherWay(a, b))
        }

        @Test
        fun `should return true when B blocks A`() {
            val a = buildUser(1L, "a")
            val b = buildUser(2L, "b")

            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(true)

            assertTrue(userBlockService.isBlockedEitherWay(a, b))
        }

        @Test
        fun `should return false when neither blocks`() {
            val a = buildUser(1L, "a")
            val b = buildUser(2L, "b")

            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            assertFalse(userBlockService.isBlockedEitherWay(a, b))
        }
    }

    @Nested
    @DisplayName("ensureNotBlockedEitherWay")
    inner class EnsureNotBlockedEitherWay {

        @Test
        fun `should throw when blocked`() {
            val a = buildUser(1L, "a")
            val b = buildUser(2L, "b")

            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                userBlockService.ensureNotBlockedEitherWay(a, b, "Blocked!")
            }
        }

        @Test
        fun `should not throw when not blocked`() {
            val a = buildUser(1L, "a")
            val b = buildUser(2L, "b")

            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(1L, 2L)).thenReturn(false)
            whenever(userBlockRepository.existsByBlocker_IdAndBlocked_Id(2L, 1L)).thenReturn(false)

            userBlockService.ensureNotBlockedEitherWay(a, b, "Blocked!")
            // No exception = success
        }
    }
}
