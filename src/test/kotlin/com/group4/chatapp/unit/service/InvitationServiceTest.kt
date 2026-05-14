package com.group4.chatapp.unit.service

import com.group4.chatapp.dtos.invitation.InvitationSendDto
import com.group4.chatapp.models.*
import com.group4.chatapp.repositories.InvitationRepository
import com.group4.chatapp.services.UserService
import com.group4.chatapp.services.invitations.InvitationService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class InvitationServiceTest {

    @Mock lateinit var repository: InvitationRepository
    @Mock lateinit var userService: UserService

    @InjectMocks
    lateinit var invitationService: InvitationService

    private fun buildUser(id: Long = 1L, username: String = "user1"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    @Nested
    @DisplayName("getInvitations")
    inner class GetInvitations {

        @Test
        fun `should return list when invitations exist`() {
            val user = buildUser()
            val sender = buildUser(2L, "sender")
            val invitation = Invitation.builder()
                .id(1L)
                .sender(sender)
                .receiver(user)
                .status(Invitation.Status.PENDING)
                .build()

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(repository.findByReceiverId(1L)).thenReturn(Stream.of(invitation))

            val result = invitationService.getInvitations()

            assertEquals(1, result.size)
        }

        @Test
        fun `should return empty list when no invitations`() {
            val user = buildUser()
            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(repository.findByReceiverId(1L)).thenReturn(Stream.empty())

            val result = invitationService.getInvitations()

            assertTrue(result.isEmpty())
        }
    }
}
