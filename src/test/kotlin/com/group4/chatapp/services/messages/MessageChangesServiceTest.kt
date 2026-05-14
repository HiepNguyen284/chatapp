package com.group4.chatapp.services.messages

import com.group4.chatapp.dtos.messages.MessageSendDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.*
import com.group4.chatapp.repositories.MessageRepository
import com.group4.chatapp.services.AttachmentService
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
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.sql.Timestamp
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class MessageChangesServiceTest {

    @Mock internal lateinit var messageRepository: MessageRepository
    @Mock internal lateinit var userService: UserService
    @Mock internal lateinit var userBlockService: UserBlockService
    @Mock internal lateinit var checkService: MessageCheckService
    @Mock internal lateinit var attachmentService: AttachmentService
    @Mock internal lateinit var notificationService: NotificationService
    @Mock internal lateinit var messagingTemplate: SimpMessagingTemplate

    @InjectMocks
    internal lateinit var messageChangesService: MessageChangesService

    private fun buildUser(id: Long = 1L, username: String = "sender"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    private fun buildRoom(id: Long = 10L, type: ChatRoom.Type = ChatRoom.Type.GROUP, members: Set<User>): ChatRoom {
        return ChatRoom.builder().id(id).type(type).members(members.toMutableSet()).build()
    }

    private fun buildMessage(
        id: Long = 100L,
        sender: User,
        room: ChatRoom,
        text: String? = "Hello",
        status: ChatMessage.Status = ChatMessage.Status.NORMAL
    ): ChatMessage {
        return ChatMessage.builder()
            .id(id)
            .sender(sender)
            .room(room)
            .message(text)
            .status(status)
            .attachments(emptyList())
            .sentOn(Timestamp(System.currentTimeMillis()))
            .build()
    }

    @Nested
    @DisplayName("sendMessage")
    inner class SendMessage {

        @Test
        fun `should send text message successfully`() {
            val user = buildUser()
            val user2 = buildUser(2L, "user2")
            val user3 = buildUser(3L, "user3")
            val room = buildRoom(members = setOf(user, user2, user3))

            val dto = MessageSendDto(null, "Hello world", null)
            val savedMessage = buildMessage(sender = user, room = room, text = "Hello world")

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(checkService.receiveChatRoomAndCheck(10L, user)).thenReturn(room)
            whenever(attachmentService.getAttachments(dto)).thenReturn(emptyList())
            whenever(messageRepository.save(any<ChatMessage>())).thenReturn(savedMessage)

            val result = messageChangesService.sendMessage(10L, dto)

            assertNotNull(result)
            assertEquals(100L, result.id())
            verify(messageRepository).save(any<ChatMessage>())
        }
    }

    @Nested
    @DisplayName("editMessage")
    inner class EditMessage {

        @Test
        fun `should edit own message successfully`() {
            val user = buildUser()
            val room = buildRoom(members = setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")))
            val message = buildMessage(sender = user, room = room)

            val dto = MessageSendDto(null, "Updated text", null)

            whenever(checkService.getMessageAndCheckSender(100L)).thenReturn(message)
            whenever(attachmentService.getAttachments(dto)).thenReturn(emptyList())
            whenever(messageRepository.save(any<ChatMessage>())).thenAnswer { it.arguments[0] as ChatMessage }

            messageChangesService.editMessage(100L, dto)

            verify(messageRepository).save(any<ChatMessage>())
        }

        @Test
        fun `should throw when editing recalled message`() {
            val user = buildUser()
            val room = buildRoom(members = setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")))
            val message = buildMessage(sender = user, room = room, text = null, status = ChatMessage.Status.RECALLED)

            val dto = MessageSendDto(null, "Updated text", null)

            whenever(checkService.getMessageAndCheckSender(100L)).thenReturn(message)

            org.junit.jupiter.api.assertThrows<ApiException> {
                messageChangesService.editMessage(100L, dto)
            }
        }

        @Test
        fun `should throw when editing someone else's message`() {
            whenever(checkService.getMessageAndCheckSender(100L))
                .thenThrow(ApiException(HttpStatus.FORBIDDEN, "You are not the sender of the message"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                messageChangesService.editMessage(100L, MessageSendDto(null, "hack", null))
            }
        }
    }

    @Nested
    @DisplayName("recallMessage")
    inner class RecallMessage {

        @Test
        fun `should recall message successfully`() {
            val user = buildUser()
            val room = buildRoom(members = setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")))
            val message = buildMessage(sender = user, room = room)

            whenever(checkService.getMessageAndCheckSender(100L)).thenReturn(message)
            whenever(messageRepository.save(any<ChatMessage>())).thenAnswer { it.arguments[0] as ChatMessage }

            messageChangesService.recallMessage(100L)

            verify(messageRepository).save(argThat<ChatMessage> {
                status == ChatMessage.Status.RECALLED && this.message == null
            })
        }

        @Test
        fun `should throw when recalling someone else's message`() {
            whenever(checkService.getMessageAndCheckSender(100L))
                .thenThrow(ApiException(HttpStatus.FORBIDDEN, "You are not the sender of the message"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                messageChangesService.recallMessage(100L)
            }
        }
    }
}
