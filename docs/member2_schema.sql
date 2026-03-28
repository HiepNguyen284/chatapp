-- =============================================
-- THÀNH VIÊN 2 – Chat cá nhân (1–1) & Quản lý tin nhắn
-- =============================================
-- Chức năng: Gửi/nhận tin nhắn 1-1, hiển thị bubble + thời gian,
-- trạng thái "đang nhập...", đã gửi/đã xem, xóa tin nhắn,
-- đồng bộ trạng thái, AI dịch tin nhắn

-- CHAT ROOMS (Phòng chat 1-1)
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    avatar_id BIGINT,
    type INT NOT NULL, -- 0=DUO (Chat 1-1), 1=GROUP, 2=AI_BOT
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (avatar_id) REFERENCES attachments(id)
);

-- CHAT MESSAGES (Tin nhắn)
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

-- CHAT ROOM READ STATES (Trạng thái đọc tin nhắn)
CREATE TABLE chat_room_read_states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    reader_id BIGINT NOT NULL,
    last_read_at TIMESTAMP NOT NULL,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (reader_id) REFERENCES users(id),
    UNIQUE (room_id, reader_id)
);

-- CHAT ROOM MEMBERS (Bảng join ManyToMany - 2 thành viên DUO)
CREATE TABLE chat_room_members (
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (chat_room_id, user_id),
    FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =============================================
-- BẢNG THAM CHIẾU
-- =============================================

-- USERS (Tham chiếu - Hiển thị tên, avatar, trạng thái)
-- CREATE TABLE users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(255),
--     display_name VARCHAR(255),
--     avatar_id BIGINT,
--     last_seen_at TIMESTAMP NULL,
--     ...
-- );

-- ATTACHMENTS (Tham chiếu - Avatar phòng chat)
-- CREATE TABLE attachments (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     source VARCHAR(500) NOT NULL,
--     type INT NOT NULL,
--     ...
-- );

-- =============================================
-- GHI CHÚ
-- =============================================
-- Trạng thái "đang nhập...": Xử lý qua WebSocket event (không cần CSDL)
-- AI Dịch tin nhắn (3.2): Đọc message → gửi AI API → trả bản dịch trên UI
