-- =============================================
-- THÀNH VIÊN 4 – Media, Thông báo & Tính năng AI
-- =============================================
-- Chức năng: Gửi/hiển thị hình ảnh, xem ảnh toàn màn hình,
-- thông báo tin nhắn/lời mời/nhóm, bật/tắt thông báo,
-- Chatbot AI

-- ATTACHMENTS (File đính kèm đa phương tiện)
CREATE TABLE attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(500) NOT NULL,
    type INT NOT NULL -- 0=IMAGE, 1=VIDEO, 2=RAW, 3=DOCUMENT, 4=AUDIO
);

-- CHAT MESSAGE ATTACHMENTS (Bảng join ManyToMany - Ảnh trong tin nhắn)
CREATE TABLE chat_message_attachments (
    chat_message_id BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    PRIMARY KEY (chat_message_id, attachment_id),
    FOREIGN KEY (chat_message_id) REFERENCES chat_messages(id),
    FOREIGN KEY (attachment_id) REFERENCES attachments(id)
);

-- NOTIFICATIONS (🆕 Bảng mới - Thông báo)
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

-- NOTIFICATION SETTINGS (🆕 Bảng mới - Cài đặt thông báo)
CREATE TABLE notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    is_muted BOOLEAN DEFAULT FALSE,
    muted_until TIMESTAMP NULL, -- NULL = tắt vĩnh viễn khi is_muted = true
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    UNIQUE (user_id, room_id)
);

-- CHAT ROOMS (Chatbot AI - Mở rộng enum type)
CREATE TABLE chat_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    avatar_id BIGINT,
    type INT NOT NULL, -- 0=DUO, 1=GROUP, 2=AI_BOT (🆕 thêm AI_BOT)
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (avatar_id) REFERENCES attachments(id)
);

-- =============================================
-- BẢNG THAM CHIẾU
-- =============================================

-- USERS (Tham chiếu - Người nhận thông báo)
-- CREATE TABLE users (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     username VARCHAR(255),
--     display_name VARCHAR(255),
--     avatar_id BIGINT,
--     ...
-- );

-- CHAT MESSAGES (Tham chiếu - Tin nhắn chứa attachment + AI bot)
-- CREATE TABLE chat_messages (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     sender_id BIGINT NOT NULL,
--     room_id BIGINT NOT NULL,
--     message TEXT,
--     sent_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     status INT NOT NULL,
--     ...
-- );

-- =============================================
-- GHI CHÚ
-- =============================================
-- Chatbot AI (3.3):
--   - Tạo chat_room với type = 2 (AI_BOT)
--   - Mỗi user có 1 phòng AI riêng
--   - Tin nhắn AI lưu trong chat_messages
--   - sender = system bot account
--   - Gọi AI API để sinh phản hồi
--
-- Luồng thông báo:
--   1. Event xảy ra (tin nhắn mới, lời mời, thêm vào nhóm)
--   2. Kiểm tra notification_settings → nếu is_muted = true → bỏ qua
--   3. Tạo record trong notifications
--   4. Gửi push qua WebSocket
