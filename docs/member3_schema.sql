-- =============================================
-- THÀNH VIÊN 3 – Danh sách chat & Chat nhóm
-- =============================================
-- Chức năng: Hiển thị danh sách cuộc trò chuyện, tin cuối cùng,
-- badge chưa đọc, tìm kiếm, ghim cuộc trò chuyện,
-- tạo nhóm, thêm/xóa thành viên, phân quyền, rời/giải tán nhóm

-- CHAT ROOMS (Phòng chat nhóm)
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    avatar_id BIGINT,
    type INT NOT NULL, -- 0=DUO, 1=GROUP (Chat nhóm), 2=AI_BOT
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (avatar_id) REFERENCES attachments(id)
);

-- CHAT ROOM MEMBERS (Bảng join ManyToMany - Thành viên nhóm)
CREATE TABLE chat_room_members (
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (chat_room_id, user_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CHAT ROOM MEMBER ROLES (Phân quyền admin/member)
CREATE TABLE chat_room_member_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- CHAT ROOM READ STATES (Badge số tin chưa đọc)
CREATE TABLE chat_room_read_states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    reader_id BIGINT NOT NULL,
    last_read_at TIMESTAMP NOT NULL,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (reader_id) REFERENCES users(id),
    UNIQUE (room_id, reader_id)
);

-- CHAT MESSAGES (Hiển thị tin nhắn cuối + gửi tin nhóm)
CREATE TABLE chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    message TEXT,
    sent_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INT NOT NULL, -- 0=NORMAL, 1=EDITED, 2=RECALLED
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id)
);

-- PINNED CONVERSATIONS (🆕 Bảng mới - Ghim cuộc trò chuyện)
CREATE TABLE pinned_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    pinned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    UNIQUE (user_id, room_id)
);

-- =============================================
-- BẢNG THAM CHIẾU
-- =============================================

-- USERS (Tham chiếu - Tìm kiếm user, hiển thị thành viên nhóm)
-- CREATE TABLE users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(255),
--     display_name VARCHAR(255),
--     avatar_id BIGINT,
--     ...
-- );

-- ATTACHMENTS (Tham chiếu - Ảnh nhóm)
-- CREATE TABLE attachments (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     source VARCHAR(500) NOT NULL,
--     type INT NOT NULL,
--     ...
-- );

-- FRIENDSHIPS (Tham chiếu - Tìm kiếm bạn bè)
-- CREATE TABLE friendships (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     user1_id BIGINT NOT NULL,
--     user2_id BIGINT NOT NULL,
--     ...
-- );

-- =============================================
-- GHI CHÚ QUERY
-- =============================================
-- Danh sách chat:      chat_rooms JOIN chat_room_members
-- Tin nhắn cuối cùng:  MAX(sent_on) FROM chat_messages GROUP BY room_id
-- Badge chưa đọc:      COUNT(*) FROM chat_messages WHERE sent_on > last_read_at
-- Tìm kiếm:            LIKE trên users.username, users.display_name, chat_rooms.name
