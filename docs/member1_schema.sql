-- =============================================
-- THÀNH VIÊN 1 – Tài khoản & Quản lý bạn bè
-- =============================================
-- Chức năng: Đăng ký/đăng nhập/đăng xuất, quên mật khẩu,
-- cập nhật profile, quản lý bạn bè, chặn người dùng,
-- Online/Offline/Last seen, AI tóm tắt hội thoại

-- USERS
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    avatar_id BIGINT,
    last_seen_at TIMESTAMP NULL,
    FOREIGN KEY (avatar_id) REFERENCES attachments(id)
);

-- ATTACHMENTS (Lưu ảnh đại diện)
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(500) NOT NULL,
    type INT NOT NULL -- 0=IMAGE, 1=VIDEO, 2=RAW, 3=DOCUMENT, 4=AUDIO
);

-- INVITATIONS (Lời mời kết bạn & mời vào nhóm)
CREATE TABLE invitations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    chat_room_id BIGINT NULL, -- NULL = lời mời kết bạn, NOT NULL = mời vào nhóm
    status INT NOT NULL, -- 0=PENDING, 1=ACCEPTED, 2=REJECTED
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id)
);

-- FRIENDSHIPS (🆕 Bảng mới - Quan hệ bạn bè đã xác nhận)
CREATE TABLE friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id),
    UNIQUE (user1_id, user2_id)
    -- Quy ước: user1_id < user2_id để tránh trùng lặp
);

-- USER BLOCKS (Chặn người dùng)
CREATE TABLE user_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    FOREIGN KEY (blocker_id) REFERENCES users(id),
    FOREIGN KEY (blocked_id) REFERENCES users(id),
    UNIQUE (blocker_id, blocked_id)
);

-- =============================================
-- BẢNG THAM CHIẾU (Đọc dữ liệu cho AI Tóm tắt)
-- =============================================

-- CHAT ROOMS (Tham chiếu)
-- CREATE TABLE chat_rooms (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     name VARCHAR(255),
--     type INT NOT NULL, -- 0=DUO, 1=GROUP, 2=AI_BOT
--     ...
-- );

-- CHAT MESSAGES (Tham chiếu - Đọc tin nhắn để AI tóm tắt)
-- CREATE TABLE chat_messages (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     sender_id BIGINT NOT NULL,
--     room_id BIGINT NOT NULL,
--     message TEXT,
--     sent_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     ...
-- );
