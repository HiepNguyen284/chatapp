package com.group4.chatapp.unit.service

import com.group4.chatapp.dtos.messages.MessageSendDto
import com.group4.chatapp.models.*
import com.group4.chatapp.repositories.ChatRoomReadStateRepository
import com.group4.chatapp.repositories.MessageRepository
import com.group4.chatapp.services.UserBlockService
import com.group4.chatapp.services.UserService
import com.group4.chatapp.services.messages.MessageService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.server.ResponseStatusException
import java.lang.reflect.Field
import java.sql.Timestamp
import java.util.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * MessageService is a public facade that delegates to package-private
 * MessageChangesService and MessageCheckService.
 * 
 * Because the delegates are package-private, we construct the service
 * manually via reflection for testing.
 */
@ExtendWith(MockitoExtension::class)
class MessageServiceTest {

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    private fun buildRoom(id: Long = 10L, type: ChatRoom.Type = ChatRoom.Type.DUO, members: Set<User>): ChatRoom {
        return ChatRoom.builder().id(id).type(type).members(members.toMutableSet()).build()
    }

    private fun buildMessage(
        id: Long = 100L,
        sender: User,
        room: ChatRoom,
        text: String = "Hello",
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
    @DisplayName("getMessages - page validation")
    inner class GetMessagesPageValidation {

        @Test
        fun `should throw when page is less than 1`() {
            // MessageService.getMessages checks page < 1 before delegating
            val messageService = createMessageServiceWithNullDeps()

            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                messageService.getMessages(10L, 0)
            }
        }

        @Test
        fun `should throw when page is negative`() {
            val messageService = createMessageServiceWithNullDeps()

            org.junit.jupiter.api.assertThrows<ResponseStatusException> {
                messageService.getMessages(10L, -1)
            }
        }
    }

    /**
     * Creates a MessageService instance using reflection to set the
     * package-private constructor dependencies to null or mocks.
     * Only useful for testing page validation which runs before any
     * delegate calls.
     */
    private fun createMessageServiceWithNullDeps(): MessageService {
        val ctor = MessageService::class.java.declaredConstructors.first()
        ctor.isAccessible = true
        // Constructor params: sendService, checkService, chatRoomReadStateRepository,
        //   messageRepository, userService, userBlockService, messagingTemplate
        val paramCount = ctor.parameterCount
        val nullArgs = Array<Any?>(paramCount) { null }
        return ctor.newInstance(*nullArgs) as MessageService
    }
}
