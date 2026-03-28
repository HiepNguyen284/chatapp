-- =============================================
-- BIỂU ĐỒ CSDL - HỆ THỐNG APPCHAT
-- Tổng cộng: 13 bảng
-- =============================================

-- =============================================
-- BẢNG ĐÃ CÓ (EXISTING)
-- =============================================

-- USERS
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    avatar_id BIGINT,
    last_seen_at TIMESTAMP NULL
);

-- ATTACHMENTS
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(500) NOT NULL,
    type INT NOT NULL -- 0=IMAGE, 1=VIDEO, 2=RAW, 3=DOCUMENT, 4=AUDIO
);

-- USER BLOCKS
CREATE TABLE user_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    FOREIGN KEY (blocker_id) REFERENCES users(id),
    FOREIGN KEY (blocked_id) REFERENCES users(id),
    UNIQUE (blocker_id, blocked_id)
);

-- INVITATIONS
CREATE TABLE invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    chat_room_id BIGINT NULL, -- NULL = friend request, NOT NULL = group invite
    status INT NOT NULL, -- 0=PENDING, 1=ACCEPTED, 2=REJECTED
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id)
);

-- FRIENDSHIPS (🆕 Bảng mới)
CREATE TABLE friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id),
    UNIQUE (user1_id, user2_id)
    -- Quy ước: user1_id < user2_id
);

-- CHAT ROOMS
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    avatar_id BIGINT,
    type INT NOT NULL, -- 0=DUO, 1=GROUP, 2=AI_BOT
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (avatar_id) REFERENCES attachments(id)
);

-- CHAT ROOM MEMBERS (Bảng join ManyToMany)
CREATE TABLE chat_room_members (
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (chat_room_id, user_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CHAT ROOM MEMBER ROLES
CREATE TABLE chat_room_member_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CHAT MESSAGES
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    reply_to_id BIGINT NULL,
    message TEXT,
    last_edit TIMESTAMP NULL,
    sent_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INT NOT NULL, -- 0=NORMAL, 1=EDITED, 2=RECALLED
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (reply_to_id) REFERENCES chat_messages(id)
);

-- CHAT MESSAGE ATTACHMENTS (Bảng join ManyToMany)
CREATE TABLE chat_message_attachments (
    chat_message_id BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    PRIMARY KEY (chat_message_id, attachment_id),
    FOREIGN KEY (chat_message_id) REFERENCES chat_messages(id),
    FOREIGN KEY (attachment_id) REFERENCES attachments(id)
);

-- CHAT ROOM READ STATES
CREATE TABLE chat_room_read_states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    reader_id BIGINT NOT NULL,
    last_read_at TIMESTAMP NOT NULL,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (reader_id) REFERENCES users(id),
    UNIQUE (room_id, reader_id)
);

-- =============================================
-- BẢNG MỚI (NEW)
-- =============================================

-- PINNED CONVERSATIONS (🆕 Bảng mới)
CREATE TABLE pinned_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    pinned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    UNIQUE (user_id, room_id)
);

-- NOTIFICATIONS (🆕 Bảng mới)
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- NEW_MESSAGE, FRIEND_REQUEST, GROUP_INVITE
    title VARCHAR(255) NOT NULL,
    content VARCHAR(500),
    reference_id BIGINT NULL, -- ID entity liên quan (message/invitation)
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- NOTIFICATION SETTINGS (🆕 Bảng mới)
CREATE TABLE notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    is_muted BOOLEAN DEFAULT FALSE,
    muted_until TIMESTAMP NULL, -- NULL = tắt vĩnh viễn
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    UNIQUE (user_id, room_id)
);

-- =============================================
-- FOREIGN KEY CHO BẢNG USERS (thêm sau vì phụ thuộc vòng)
-- =============================================

ALTER TABLE users ADD FOREIGN KEY (avatar_id) REFERENCES attachments(id);
