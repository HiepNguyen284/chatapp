package com.group4.chatapp.unit.service

import com.group4.chatapp.dtos.group.GroupChatCreateDto
import com.group4.chatapp.dtos.group.GroupChatUpdateDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.models.*
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
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class GroupChatServiceTest {

    @Mock lateinit var userService: UserService
    @Mock lateinit var attachmentService: AttachmentService
    @Mock lateinit var chatRoomRepository: ChatRoomRepository
    @Mock lateinit var chatRoomMemberRepository: ChatRoomMemberRepository
    @Mock lateinit var messageRepository: MessageRepository
    @Mock lateinit var chatRoomReadStateRepository: ChatRoomReadStateRepository
    @Mock lateinit var invitationRepository: InvitationRepository
    @Mock lateinit var chatRoomPinRepository: ChatRoomPinRepository
    @Mock lateinit var messagingTemplate: SimpMessagingTemplate
    @Mock lateinit var notificationService: NotificationService
    @Mock lateinit var presenceService: PresenceService

    @InjectMocks
    lateinit var groupChatService: GroupChatService

    private fun buildUser(id: Long = 1L, username: String = "creator"): User {
        return User.builder().id(id).username(username).password("pass").displayName(username).build()
    }

    private fun buildGroupRoom(
        id: Long = 10L,
        members: Set<User>,
        creatorId: Long? = null,
        name: String = "Test Group"
    ): ChatRoom {
        return ChatRoom.builder()
            .id(id)
            .type(ChatRoom.Type.GROUP)
            .name(name)
            .members(members.toMutableSet())
            .creatorId(creatorId)
            .build()
    }

    @Nested
    @DisplayName("createGroup")
    inner class CreateGroup {

        @Test
        fun `should create group successfully with min members`() {
            val creator = buildUser(1L, "creator")
            val member2 = buildUser(2L, "member2")
            val member3 = buildUser(3L, "member3")

            whenever(userService.getUserOrThrows()).thenReturn(creator)
            whenever(userService.getUserById(2L)).thenReturn(Optional.of(member2))
            whenever(userService.getUserById(3L)).thenReturn(Optional.of(member3))

            val savedRoom = buildGroupRoom(10L, setOf(creator, member2, member3), creator.id)
            whenever(chatRoomRepository.save(any<ChatRoom>())).thenReturn(savedRoom)
            whenever(chatRoomMemberRepository.save(any<ChatRoomMember>())).thenAnswer { it.arguments[0] }
            whenever(chatRoomMemberRepository.isUserAdmin(10L, 1L)).thenReturn(true)
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            val dto = GroupChatCreateDto("Test Group", listOf(2L, 3L), null)
            val result = groupChatService.createGroup(dto)

            assertNotNull(result)
            verify(chatRoomRepository).save(any<ChatRoom>())
            verify(chatRoomMemberRepository, atLeast(3)).save(any<ChatRoomMember>())
        }

        @Test
        fun `should throw when member not found`() {
            val creator = buildUser(1L, "creator")
            whenever(userService.getUserOrThrows()).thenReturn(creator)
            whenever(userService.getUserById(999L)).thenReturn(Optional.empty())

            val dto = GroupChatCreateDto("Test Group", listOf(999L), null)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.createGroup(dto)
            }
        }
    }

    @Nested
    @DisplayName("getGroupDetails")
    inner class GetGroupDetails {

        @Test
        fun `should return group details for member`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                user.id
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)
            whenever(chatRoomMemberRepository.isUserAdmin(10L, 1L)).thenReturn(true)
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            val result = groupChatService.getGroupDetails(10L)

            assertNotNull(result)
        }

        @Test
        fun `should throw when user is not a member`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(buildUser(2L, "u2"), buildUser(3L, "u3"), buildUser(4L, "u4")),
                2L
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(false)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.getGroupDetails(10L)
            }
        }
    }

    @Nested
    @DisplayName("updateGroup")
    inner class UpdateGroup {

        @Test
        fun `should update group name when admin`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                user.id,
                "Old Name"
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserAdmin(10L, 1L)).thenReturn(true)
            whenever(chatRoomRepository.save(any<ChatRoom>())).thenAnswer { it.arguments[0] }
            whenever(chatRoomMemberRepository.isUserAdmin(10L, 1L)).thenReturn(true)
            whenever(messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(10L)).thenReturn(Optional.empty())

            val dto = GroupChatUpdateDto("New Name", null)
            val result = groupChatService.updateGroup(10L, dto)

            assertNotNull(result)
            verify(chatRoomRepository).save(any<ChatRoom>())
        }

        @Test
        fun `should throw when non-admin tries to update`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                2L
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserAdmin(10L, 1L)).thenReturn(false)

            val dto = GroupChatUpdateDto("New Name", null)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.updateGroup(10L, dto)
            }
        }
    }

    @Nested
    @DisplayName("addMembers")
    inner class AddMembers {

        @Test
        fun `should throw when non-member tries to add`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(buildUser(2L, "u2"), buildUser(3L, "u3"), buildUser(4L, "u4")),
                2L
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(false)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.addMembers(10L, listOf(5L))
            }
        }

        @Test
        fun `should throw when target user not found`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                user.id
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)
            whenever(userService.getUserById(999L)).thenReturn(Optional.empty())

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.addMembers(10L, listOf(999L))
            }
        }
    }

    @Nested
    @DisplayName("removeMember")
    inner class RemoveMember {

        @Test
        fun `should throw when non-owner tries to remove`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                2L // owner is user 2
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)
            whenever(chatRoomMemberRepository.isUserMember(10L, 3L)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.removeMember(10L, 3L)
            }
        }

        @Test
        fun `should throw when trying to remove the creator`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                user.id
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.removeMember(10L, 1L)
            }
        }
    }

    @Nested
    @DisplayName("leaveGroup")
    inner class LeaveGroup {

        @Test
        fun `should throw when creator tries to leave`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                user.id
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(true)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.leaveGroup(10L)
            }
        }

        @Test
        fun `should throw when non-member tries to leave`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(buildUser(2L, "u2"), buildUser(3L, "u3"), buildUser(4L, "u4")),
                2L
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))
            whenever(chatRoomMemberRepository.isUserMember(10L, 1L)).thenReturn(false)

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.leaveGroup(10L)
            }
        }
    }

    @Nested
    @DisplayName("dissolveGroup")
    inner class DissolveGroup {

        @Test
        fun `should dissolve group when creator requests`() {
            val user = buildUser()
            val user2 = buildUser(2L, "u2")
            val user3 = buildUser(3L, "u3")
            val room = buildGroupRoom(10L, setOf(user, user2, user3), user.id)

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))

            groupChatService.dissolveGroup(10L)

            verify(chatRoomReadStateRepository).deleteByRoomId(10L)
            verify(invitationRepository).deleteByChatRoomId(10L)
            verify(chatRoomPinRepository).deleteByChatRoom_Id(10L)
            verify(messageRepository).deleteByRoomId(10L)
            verify(chatRoomMemberRepository).deleteByChatRoomId(10L)
            verify(chatRoomRepository).delete(room)
        }

        @Test
        fun `should throw when non-creator tries to dissolve`() {
            val user = buildUser()
            val room = buildGroupRoom(
                10L,
                setOf(user, buildUser(2L, "u2"), buildUser(3L, "u3")),
                2L // creator is user 2
            )

            whenever(userService.getUserOrThrows()).thenReturn(user)
            whenever(chatRoomRepository.findById(10L)).thenReturn(Optional.of(room))

            org.junit.jupiter.api.assertThrows<ApiException> {
                groupChatService.dissolveGroup(10L)
            }
        }
    }
}
