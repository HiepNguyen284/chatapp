package com.group4.chatapp.unit.service

import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.ChatRoom
import com.group4.chatapp.models.ChatRoomPin
import com.group4.chatapp.models.User
import com.group4.chatapp.repositories.*
import com.group4.chatapp.services.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class ChatRoomServiceTest {

    @Mock lateinit var userService: UserService
    @Mock lateinit var messageRepository: MessageRepository
    @Mock lateinit var chatRoomReadStateRepository: ChatRoomReadStateRepository
    @Mock lateinit var chatRoomRepository: ChatRoomRepository
    @Mock lateinit var chatRoomPinRepository: ChatRoomPinRepository
    @Mock lateinit var messagingTemplate: SimpMessagingTemplate
    @Mock lateinit var agoraTokenService: AgoraTokenService
    @Mock lateinit var notificationService: NotificationService

    @InjectMocks
    lateinit var chatRoomService: ChatRoomService

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    private fun buildDuoRoom(id: Long = 10L, member1: User, member2: User): ChatRoom {
        return ChatRoom.builder()
            .id(id)
            .type(ChatRoom.Type.DUO)
            .members(mutableSetOf(member1, member2))
            .build()
    }

    private fun buildGroupRoom(id: Long = 20L, members: Set<User>): ChatRoom {
        return ChatRoom.builder()
            .id(id)
            .type(ChatRoom.Type.GROUP)
            .name("Test Group")
            .members(members.toMutableSet())
            .build()
    }

    @Nested
    @DisplayName("listRoomsWithLatestMessage")
    inner class ListRooms {

        @Test
        fun `should return empty list when user has no rooms`() {
            val user = buildUser()
            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomPinRepository.findPinnedRoomIdsByUserId(1L)).thenReturn(emptyList())
            whenever(chatRoomRepository.findWithLatestMessage(1L)).thenReturn(emptyList())

            val result = chatRoomService.listRoomsWithLatestMessage()

            assertTrue(result.isEmpty())
        }
    }

    @Nested
    @DisplayName("pinRoom")
    inner class PinRoom {

        @Test
        fun `should pin room successfully`() {
            val user = buildUser()
            val room = buildDuoRoom(member1 = user, member2 = buildUser(2L, "user2"))

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(true)
            whenever(chatRoomPinRepository.existsByUser_IdAndChatRoom_Id(1L, 10L)).thenReturn(false)
            whenever(chatRoomPinRepository.save(any<ChatRoomPin>())).thenAnswer { it.arguments[0] }
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            chatRoomService.pinRoom(10L)

            verify(chatRoomPinRepository).save(any<ChatRoomPin>())
            verify(messagingTemplate).convertAndSendToUser(eq("user1"), eq("/queue/chatrooms/pinned/"), any())
        }

        @Test
        fun `should be idempotent when already pinned`() {
            val user = buildUser()
            val room = buildDuoRoom(member1 = user, member2 = buildUser(2L, "user2"))

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(true)
            whenever(chatRoomPinRepository.existsByUser_IdAndChatRoom_Id(1L, 10L)).thenReturn(true)
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            chatRoomService.pinRoom(10L)

            verify(chatRoomPinRepository, never()).save(any<ChatRoomPin>())
        }
    }

    @Nested
    @DisplayName("unpinRoom")
    inner class UnpinRoom {

        @Test
        fun `should unpin room successfully`() {
            val user = buildUser()
            val room = buildDuoRoom(member1 = user, member2 = buildUser(2L, "user2"))

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(true)
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            chatRoomService.unpinRoom(10L)

            verify(chatRoomPinRepository).deleteByUser_IdAndChatRoom_Id(1L, 10L)
        }
    }

    @Nested
    @DisplayName("removeFriend")
    inner class RemoveFriend {

        @Test
        fun `should remove duo room successfully`() {
            val user = buildUser()
            val user2 = buildUser(2L, "user2")
            val room = buildDuoRoom(member1 = user, member2 = user2)

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))

            chatRoomService.removeFriend(10L)

            verify(chatRoomPinRepository).deleteByChatRoom_Id(10L)
            verify(chatRoomReadStateRepository).deleteByRoomId(10L)
            verify(messageRepository).deleteByRoomId(10L)
            verify(chatRoomRepository).delete(room)
            verify(messagingTemplate, times(2)).convertAndSendToUser(any(), eq("/queue/friends/removed/"), any())
        }

        @Test
        fun `should throw when room is not DUO type`() {
            val user = buildUser()
            val room = buildGroupRoom(
                members = setOf(user, buildUser(2L, "user2"), buildUser(3L, "user3"))
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(20L)).thenReturn(Optional.of(room))

            val exception = org.junit.jupiter.api.assertThrows<ApiException> {
                chatRoomService.removeFriend(20L)
            }

            assertTrue(exception.body.title!!.contains("Only duo rooms"))
        }

        @Test
        fun `should throw when room not found`() {
            val user = buildUser()
            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(999L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                chatRoomService.removeFriend(999L)
            }
        }

        @Test
        fun `should throw when user is not a member`() {
            val user = buildUser()
            val otherUser1 = buildUser(2L, "other1")
            val otherUser2 = buildUser(3L, "other2")
            val room = buildDuoRoom(member1 = otherUser1, member2 = otherUser2)

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))

            org.junit.jupiter.api.assertThrows<ApiException> {
                chatRoomService.removeFriend(10L)
            }
        }
    }

    @Nested
    @DisplayName("initiateVideoCall")
    inner class InitiateVideoCall {

        @Test
        fun `should throw when user is not a member`() {
            val user = buildUser()
            val room = buildDuoRoom(member1 = buildUser(2L, "user2"), member2 = buildUser(3L, "user3"))

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(false)

            org.junit.jupiter.api.assertThrows<ApiException> {
                chatRoomService.initiateVideoCall(10L)
            }
        }

        @Test
        fun `should initiate video call and notify other members`() {
            val user = buildUser()
            val user2 = buildUser(2L, "user2")
            val room = buildDuoRoom(member1 = user, member2 = user2)

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(true)
            whenever(agoraTokenService.generateToken(any(), any())).thenReturn("agora-token")

            val result = chatRoomService.initiateVideoCall(10L)

            assertNotNull(result)
            assertEquals(10L, result.roomId)
            verify(messagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/calls/video"), any())
        }
    }

    @Nested
    @DisplayName("rejectVideoCall")
    inner class RejectVideoCall {

        @Test
        fun `should reject video call and notify members`() {
            val user = buildUser()
            val user2 = buildUser(2L, "user2")
            val room = buildDuoRoom(member1 = user, member2 = user2)

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomRepository.userIsMemberInChatRoom(1L, 10L)).thenReturn(true)

            chatRoomService.rejectVideoCall(10L)

            verify(messagingTemplate).convertAndSendToUser(eq("user2"), eq("/queue/calls/video_rejected"), any())
        }
    }
}
