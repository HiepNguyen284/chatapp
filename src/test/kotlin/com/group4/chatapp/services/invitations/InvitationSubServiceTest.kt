package com.group4.chatapp.services.invitations

import com.group4.chatapp.dtos.ChatRoomDto
import com.group4.chatapp.dtos.invitation.InvitationSendDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.*
import com.group4.chatapp.repositories.ChatRoomRepository
import com.group4.chatapp.repositories.InvitationRepository
import com.group4.chatapp.repositories.UserRepository
import com.group4.chatapp.services.ChatRoomService
import com.group4.chatapp.services.NotificationService
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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class InvitationSendServiceTest {

    @Mock internal lateinit var userService: UserService
    @Mock internal lateinit var userBlockService: UserBlockService
    @Mock internal lateinit var userRepository: UserRepository
    @Mock internal lateinit var repository: InvitationRepository
    @Mock internal lateinit var chatRoomRepository: ChatRoomRepository
    @Mock internal lateinit var messagingTemplate: SimpMessagingTemplate
    @Mock internal lateinit var notificationService: NotificationService

    @InjectMocks
    internal lateinit var invitationSendService: InvitationSendService

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    @Nested
    @DisplayName("sendInvitation")
    inner class SendInvitation {

        @Test
        fun `should send friend request successfully`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")

            whenever(userService.getUserOrThrows()).thenReturn(sender)
            whenever(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver))
            whenever(chatRoomRepository.usersShareRoomOfType(1L, 2L, ChatRoom.Type.DUO)).thenReturn(false)
            whenever(repository.existsFriendRequestWith(1L, 2L, Invitation.Status.PENDING)).thenReturn(false)
            whenever(repository.existsFriendRequestWith(2L, 1L, Invitation.Status.PENDING)).thenReturn(false)
            whenever(repository.saveAndFlush(any<Invitation>())).thenAnswer {
                val inv = it.arguments[0] as Invitation
                inv.id = 10L
                inv
            }

            invitationSendService.sendInvitation(InvitationSendDto("receiver", null))

            verify(repository).saveAndFlush(any<Invitation>())
            verify(messagingTemplate).convertAndSendToUser(eq("receiver"), eq("/queue/invitations/"), any())
        }

        @Test
        fun `should throw when self-invite`() {
            val sender = buildUser(1L, "sender")

            whenever(userService.getUserOrThrows()).thenReturn(sender)

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationSendService.sendInvitation(InvitationSendDto("sender", null))
            }
        }

        @Test
        fun `should throw when already friends`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")

            whenever(userService.getUserOrThrows()).thenReturn(sender)
            whenever(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver))
            whenever(chatRoomRepository.usersShareRoomOfType(1L, 2L, ChatRoom.Type.DUO)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationSendService.sendInvitation(InvitationSendDto("receiver", null))
            }
        }

        @Test
        fun `should throw when duplicate pending invitation`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")

            whenever(userService.getUserOrThrows()).thenReturn(sender)
            whenever(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver))
            whenever(repository.existsFriendRequestWith(1L, 2L, Invitation.Status.PENDING)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationSendService.sendInvitation(InvitationSendDto("receiver", null))
            }
        }

        @Test
        fun `should throw when receiver not found`() {
            val sender = buildUser(1L, "sender")

            whenever(userService.getUserOrThrows()).thenReturn(sender)
            whenever(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationSendService.sendInvitation(InvitationSendDto("nonexistent", null))
            }
        }
    }
}

@ExtendWith(MockitoExtension::class)
class InvitationReplyServiceTest {

    @Mock internal lateinit var userService: UserService
    @Mock internal lateinit var chatRoomService: ChatRoomService
    @Mock internal lateinit var repository: InvitationRepository
    @Mock internal lateinit var chatRoomRepository: ChatRoomRepository
    @Mock internal lateinit var messagingTemplate: SimpMessagingTemplate
    @Mock internal lateinit var notificationService: NotificationService

    @InjectMocks
    internal lateinit var invitationReplyService: InvitationReplyService

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    @Nested
    @DisplayName("replyInvitation")
    inner class ReplyInvitation {

        @Test
        fun `should accept friend request and create DUO room`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")
            val invitation = Invitation.builder()
                .id(10L)
                .sender(sender)
                .receiver(receiver)
                .chatRoom(null)
                .status(Invitation.Status.PENDING)
                .build()

            val duoRoom = ChatRoom.builder()
                .id(100L)
                .type(ChatRoom.Type.DUO)
                .members(setOf(sender, receiver))
                .build()

            whenever(userService.getUserOrThrows()).thenReturn(receiver)
            whenever(repository.findById(10L)).thenReturn(Optional.of(invitation))
            whenever(chatRoomRepository.save(any<ChatRoom>())).thenReturn(duoRoom)
            whenever(chatRoomService.getRoomWithLatestMessage(duoRoom)).thenReturn(
                ChatRoomDto(duoRoom, null)
            )

            val result = invitationReplyService.replyInvitation(10L, true)

            assertNotNull(result.newChatRoom())
            verify(repository).saveAndFlush(any<Invitation>())
        }

        @Test
        fun `should reject invitation`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")
            val invitation = Invitation.builder()
                .id(10L)
                .sender(sender)
                .receiver(receiver)
                .chatRoom(null)
                .status(Invitation.Status.PENDING)
                .build()

            whenever(userService.getUserOrThrows()).thenReturn(receiver)
            whenever(repository.findById(10L)).thenReturn(Optional.of(invitation))

            invitationReplyService.replyInvitation(10L, false)

            verify(repository).saveAndFlush(argThat<Invitation> {
                status == Invitation.Status.REJECTED
            })
        }

        @Test
        fun `should throw when invitation not found`() {
            val receiver = buildUser(2L, "receiver")
            whenever(userService.getUserOrThrows()).thenReturn(receiver)
            whenever(repository.findById(999L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationReplyService.replyInvitation(999L, true)
            }
        }

        @Test
        fun `should throw when invitation already replied`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")
            val invitation = Invitation.builder()
                .id(10L)
                .sender(sender)
                .receiver(receiver)
                .chatRoom(null)
                .status(Invitation.Status.ACCEPTED)
                .build()

            whenever(userService.getUserOrThrows()).thenReturn(receiver)
            whenever(repository.findById(10L)).thenReturn(Optional.of(invitation))

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationReplyService.replyInvitation(10L, true)
            }
        }

        @Test
        fun `should throw when user is not the receiver`() {
            val sender = buildUser(1L, "sender")
            val receiver = buildUser(2L, "receiver")
            val otherUser = buildUser(3L, "other")
            val invitation = Invitation.builder()
                .id(10L)
                .sender(sender)
                .receiver(receiver)
                .chatRoom(null)
                .status(Invitation.Status.PENDING)
                .build()

            whenever(userService.getUserOrThrows()).thenReturn(otherUser)
            whenever(repository.findById(10L)).thenReturn(Optional.of(invitation))

            org.junit.jupiter.api.assertThrows<ApiException> {
                invitationReplyService.replyInvitation(10L, true)
            }
        }
    }
}
