# BẢNG PHÂN CÔNG NHIỆM VỤ

| STT | Họ và tên | MSSV | Nhiệm vụ cụ thể | Đóng góp (%) |
|:---:|-----------|------|------------------|:------------:|
| 1 | Nguyễn Văn Duy | B22DCCN154 | Gửi hình ảnh/media, Thông báo đẩy (FCM), Chatbot AI streaming, Video Call (Agora) | 25% |
| 2 | Nguyễn Hoàng Hiệp | B22DCCN298 | Chat cá nhân 1-1, Gửi/nhận tin nhắn, Typing indicator, Trạng thái đã xem, Xóa tin nhắn, Dịch tin nhắn AI, Voice-to-Text | 25% |
| 3 | Nguyễn Quang Minh | B22DCCN538 | Tài khoản (Đăng ký, Đăng nhập, Quên MK, Profile), Quản lý bạn bè (Lời mời, Chặn, Xóa bạn), Trạng thái online, Tóm tắt AI | 25% |
| 4 | Đặng Hữu Hoàng Quân | B22DCCN658 | Danh sách cuộc trò chuyện, Tìm kiếm, Ghim chat, Tạo nhóm, Quản lý thành viên nhóm, Phân quyền admin | 25% |

*Bảng 0.1: Bảng phân công nhiệm vụ các thành viên*

---

# MỤC LỤC
---

<div style="page-break-before: always;"></div>

# DANH SÁCH VIẾT TẮT

| Viết tắt | Ý nghĩa |
|----------|---------|
| API | Application Programming Interface |
| CRUD | Create, Read, Update, Delete |
| DTO | Data Transfer Object |
| FCM | Firebase Cloud Messaging |
| GVHD | Giảng viên hướng dẫn |
| HTTP | HyperText Transfer Protocol |
| JPA | Java Persistence API |
| JWT | JSON Web Token |
| LLM | Large Language Model |
| MK | Mật khẩu |
| MSSV | Mã số sinh viên |
| ORM | Object-Relational Mapping |
| REST | Representational State Transfer |
| S3 | Simple Storage Service |
| SDK | Software Development Kit |
| SQL | Structured Query Language |
| SSE | Server-Sent Events |
| STOMP | Simple Text Oriented Messaging Protocol |
| UI | User Interface |
| UML | Unified Modeling Language |
| WebSocket | Giao thức truyền thông hai chiều thời gian thực |

*Bảng 0.2: Danh sách viết tắt*

---

# DANH SÁCH HÌNH

| Ký hiệu | Mô tả |
|----------|-------|
| Hình 2.1 | Sơ đồ kiến trúc tổng quan hệ thống ChatApp |
| Hình 2.2 | Biểu đồ Use Case tổng quan |
| Hình 2.3 | Biểu đồ Use Case chi tiết — Media, Thông báo & AI |
| Hình 2.4 | Biểu đồ lớp — Module Media, Thông báo & AI |
| Hình 2.5 | Biểu đồ tuần tự — Gửi hình ảnh |
| Hình 2.6 | Biểu đồ tuần tự — Push notification |
| Hình 2.7 | Biểu đồ tuần tự — Chatbot AI streaming |
| Hình 2.8 | Biểu đồ tuần tự — Chatbot AI Streaming |
| Hình 2.9 | Biểu đồ tuần tự — Push Notification (FCM) |
| Hình 2.10 | Biểu đồ tuần tự — Video Call (Agora RTC) |
| Hình 2.11 | Biểu đồ tuần tự — Video Call (Agora RTC) |
| Hình 2.12 | Sơ đồ thực thể quan hệ (ER Diagram) |
| Hình 2.13 | Giao diện ChatScreen — Gửi hình ảnh |
| Hình 2.14 | Giao diện AI Chatbot |
| Hình 2.15 | Giao diện Cài đặt thông báo |
| Hình 2.16 | Luồng dữ liệu Provider trong Flutter |
| Hình 2.17 | Biểu đồ hoạt động — Upload media & push notification |
| Hình 3.1 | Sơ đồ triển khai Docker Compose |
| Hình 3.2 | Kết quả — Gửi hình ảnh |
| Hình 3.3 | Kết quả — Push notification |
| Hình 3.4 | Kết quả — AI Chatbot |

*Bảng 0.3: Danh sách hình*

---

# DANH SÁCH BẢNG

| Ký hiệu | Mô tả |
|----------|-------|
| Bảng 1.1 | Yêu cầu chức năng |
| Bảng 1.2 | Yêu cầu phi chức năng |
| Bảng 1.3 | So sánh lựa chọn công nghệ |
| Bảng 1.4 | So sánh ChatApp với các ứng dụng nhắn tin hiện có |
| Bảng 1.5 | Tổng hợp các mẫu thiết kế |
| Bảng 1.6 | Đặc tả UC-10 — Gửi hình ảnh |
| Bảng 1.7 | Đặc tả UC-18 — Chat với AI Bot |
| Bảng 1.8 | Đặc tả UC-15 — Push notification |
| Bảng 2.1 | Chi tiết các trường entity trong hệ thống |
| Bảng 2.2 | Cấu trúc phân lớp Flutter Frontend |
| Bảng 2.3 | Danh sách services trong Flutter Frontend |
| Bảng 2.4 | API Endpoints — Module Media, Thông báo & AI |
| Bảng 3.1 | Danh sách services |
| Bảng 3.2 | Kết quả thử nghiệm chức năng Media, Thông báo & AI |
| Bảng 3.3 | Đánh giá kiến trúc module Media, Thông báo & AI |
| Bảng 3.4 | Phân tích hiệu năng module AI & Media |

*Bảng 0.4: Danh sách bảng*

---

<div style="page-break-before: always;"></div>

# Chương 1: Mở đầu

## 1.1 Giới thiệu ứng dụng và lý do thực hiện

Trong thời đại công nghệ số hiện nay, nhu cầu giao tiếp trực tuyến ngày càng tăng cao. Các ứng dụng nhắn tin đã trở thành công cụ không thể thiếu trong cuộc sống hàng ngày, từ trao đổi công việc đến kết nối bạn bè, gia đình.

**ChatApp** là ứng dụng nhắn tin trực tuyến được phát triển bởi nhóm 4 sinh viên với mục tiêu xây dựng một hệ thống hoàn chỉnh, áp dụng các kiến thức kiến trúc phần mềm đã học. Ứng dụng hỗ trợ nhắn tin cá nhân, nhắn tin nhóm, gửi hình ảnh/tệp, gọi video, và tích hợp trí tuệ nhân tạo (AI) cho các tính năng tóm tắt, dịch thuật và chatbot.

**Lý do thực hiện:**

- **Nhu cầu thực tế**: Xây dựng sản phẩm phần mềm hoàn chỉnh từ thiết kế đến triển khai.
- **Kiến trúc hiện đại**: Áp dụng kiến trúc Client-Server với API Gateway, message broker, cache layer, object storage.
- **Công nghệ tiên tiến**: Spring Boot 4, Flutter, WebSocket (STOMP), Redis, Docker.
- **Tích hợp AI**: Large Language Model (LLM) qua OpenAI API cho tóm tắt, dịch thuật, chatbot.

## 1.2 Concept và mục tiêu

### Concept

ChatApp được thiết kế theo mô hình **Client-Server** với kiến trúc phân lớp:

- **Client**: Flutter đa nền tảng (Android, iOS, Web).
- **API Gateway**: Caddy reverse proxy.
- **Backend**: Spring Boot 4 application.
- **Hạ tầng**: PostgreSQL, Redis, Apache Artemis, VersityGW.

### Mục tiêu

1. Hệ thống nhắn tin thời gian thực (chat 1-1 và nhóm).
2. Gửi/nhận đa phương tiện (hình ảnh, video, tài liệu, âm thanh).
3. Thông báo đẩy qua Firebase Cloud Messaging.
4. Tính năng AI: tóm tắt, dịch, chatbot streaming.
5. Gọi video qua Agora RTC Engine.
6. Bảo mật JWT + Argon2.
7. Triển khai Docker Compose.

## 1.3 Phân tích yêu cầu

### 1.3.1 Yêu cầu chức năng

| STT | Mã | Yêu cầu | Mô tả |
|:---:|-----|---------|-------|
| 1 | FR-01 | Đăng ký tài khoản | Tạo tài khoản với username/password |
| 2 | FR-02 | Đăng nhập | JWT token pair authentication |
| 3 | FR-03 | Quên mật khẩu | Email reset link |
| 4 | FR-05 | Cập nhật profile | Đổi displayName, avatar |
| 5 | FR-06 | Quản lý bạn bè | Lời mời kết bạn |
| 6 | FR-07 | Chặn người dùng | Block/unblock |
| 7 | FR-08 | Nhắn tin 1-1 | Realtime DUO chat |
| 8 | FR-09 | Nhắn tin nhóm | GROUP chat ≥3 người |
| 9 | FR-10 | Gửi media | Upload hình ảnh, video, tài liệu, âm thanh |
| 10 | FR-11 | Trạng thái tin nhắn | Typing, đã gửi, đã xem |
| 11 | FR-12 | Xóa tin nhắn | Thu hồi (recall) |
| 12 | FR-13 | Ghim chat | Pin/unpin chatroom |
| 13 | FR-14 | Tìm kiếm | Search user |
| 14 | FR-15 | Thông báo đẩy | FCM push notification |
| 15 | FR-16 | Tóm tắt AI | LLM summarization |
| 16 | FR-17 | Dịch tin nhắn AI | LLM translation |
| 17 | FR-18 | Chatbot AI | Streaming SSE chatbot |
| 18 | FR-19 | Gọi video | Agora RTC 1-1 |
| 19 | FR-20 | Trạng thái online | Redis presence |

*Bảng 1.1: Yêu cầu chức năng*

### 1.3.2 Yêu cầu phi chức năng

| STT | Mã | Yêu cầu | Mô tả |
|:---:|-----|---------|-------|
| 1 | NFR-01 | Hiệu năng | < 500ms WebSocket delivery |
| 2 | NFR-02 | Bảo mật | JWT + Argon2 |
| 3 | NFR-03 | Khả dụng | Docker 24/7 |
| 4 | NFR-04 | Mở rộng | Scalable services |
| 5 | NFR-05 | Tương thích | Android, iOS, Web |
| 6 | NFR-06 | Cache | Redis |
| 7 | NFR-07 | Lưu trữ | S3-compatible |
| 8 | NFR-08 | Triển khai | Docker Compose |

*Bảng 1.2: Yêu cầu phi chức năng*

## 1.4 Lựa chọn công nghệ

| Thành phần | Công nghệ | Phiên bản | Lý do lựa chọn |
|------------|-----------|-----------|-----------------|
| **Backend** | Spring Boot | 4.0.5 | Hệ sinh thái lớn, WebSocket, Security, JPA |
| **Ngôn ngữ** | Java + Kotlin | 21 / 2.2 | Virtual threads + cú pháp ngắn gọn |
| **Frontend** | Flutter | Dart ^3.5 | Đa nền tảng, hot reload |
| **Database** | PostgreSQL | 18 | RDBMS mạnh mẽ, ACID |
| **Cache** | Redis | 8 | In-memory, pub/sub |
| **Broker** | Apache Artemis| Message broker hỗ trợ STOMP/JMS | 2.53.0 | STOMP protocol |
| **Storage** | VersityGW | Latest | S3-compatible |
| **Gateway** | Caddy | 2 Alpine | Reverse proxy |
| **Push** | Firebase Admin SDK | 9.8.0 | FCM push |
| **Video** | Agora RTC | 6.2.0 | Video call SDK |
| **AI** | OpenAI Java SDK | 4.30.0 | LLM integration |
| **State** | Provider | 6.1.2 | Flutter state management |
| **Deploy** | Docker Compose | — | Multi-service deployment |

*Bảng 1.3: So sánh lựa chọn công nghệ*

---

<div style="page-break-before: always;"></div>


## 1.5 So sánh với các ứng dụng nhắn tin hiện có

| Tiêu chí | ChatApp | Zalo | Telegram | Messenger |
|----------|---------|------|----------|-----------|
| **Nền tảng** | Android, iOS, Web (Flutter) | Android, iOS, Web, Desktop | Đa nền tảng | Android, iOS, Web |
| **AI tích hợp** | Tóm tắt, dịch, chatbot streaming (LLM) | Không | Bot API | Meta AI |
| **Push notification** | FCM (Firebase) | Proprietary | Proprietary | Proprietary |
| **Media upload** | S3-compatible (VersityGW) | Proprietary | Cloud-based | Proprietary |
| **Chatbot** | SSE streaming, MCP support | Không | Bot API (không streaming) | Meta AI (limited) |
| **Self-hosted** | Docker Compose | Không | TDLib | Không |

*Bảng 1.4: So sánh ChatApp với các ứng dụng nhắn tin hiện có*

## 1.6 Các mẫu thiết kế áp dụng (Design Patterns)

| Mẫu thiết kế | Loại (GoF) | Vị trí áp dụng | Lợi ích chính |
|---------------|------------|-----------------|---------------|
| Repository | Structural | JPA Repositories | Tách data access |
| Service Layer | Architectural | Service classes | Single Responsibility |
| DTO | Structural | API request/response | Bảo vệ domain model |
| Observer | Behavioral | STOMP, FCM | Loose coupling, realtime |
| Strategy | Behavioral | FileTypeService | Open/Closed Principle |
| Proxy | Structural | Caddy, Security | Access control |
| Builder | Creational | PromptService | Readable construction |

*Bảng 1.5: Tổng hợp các mẫu thiết kế*

**Chi tiết áp dụng trong module Media, Thông báo & AI:**
- **Strategy Pattern:** `FileTypeService` phân loại file thành 5 loại: IMAGE, VIDEO, DOCUMENT, AUDIO, RAW dựa trên extension.
- **Observer Pattern:** FCM push notification — `NotificationService` → `FcmTokenService` → Firebase Admin SDK → Device.
- **Builder Pattern:** `PromptService.buildTranslationPrompt()` và `buildSummaryPrompt()` xây dựng prompt LLM từng bước.
- **Service Layer:** `ChatbotService` (~21KB) quản lý toàn bộ lifecycle chatbot conversation + SSE streaming.

## 1.7 Đặc tả Use Case chi tiết — Media, Thông báo & AI

### UC-10: Gửi hình ảnh trong chat

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Gửi hình ảnh trong cuộc trò chuyện |
| **Actor** | Người dùng (thành viên chatroom) |
| **Tiền điều kiện** | ChatRoom tồn tại, user là thành viên |
| **Hậu điều kiện** | Attachment(IMAGE) được tạo, file upload lên S3, tin nhắn broadcast |
| **Luồng chính** | 1. Nhấn nút đính kèm (📎). 2. ImagePicker chọn ảnh. 3. `POST /api/v1/messages/?room={id}` (multipart). 4. AttachmentService → FileTypeService → IMAGE. 5. S3Service upload lên VersityGW. 6. Tạo Attachment + ChatMessage. 7. STOMP broadcast. |
| **Luồng ngoại lệ** | 5a. S3 upload fail → 500. 4a. File quá lớn → 413. |
| **API** | `POST /api/v1/messages/?room={roomId}` (multipart/form-data) |

*Bảng 1.6: Đặc tả UC-10 — Gửi hình ảnh*

### UC-18: Chat với AI Bot (Streaming)

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Trò chuyện với AI chatbot (streaming SSE) |
| **Actor** | Người dùng (đã đăng nhập) |
| **Tiền điều kiện** | ChatbotConversation tồn tại, dịch vụ AI cấu hình |
| **Hậu điều kiện** | ChatbotMessage(USER) + ChatbotMessage(ASSISTANT) được lưu |
| **Luồng chính** | 1. Nhập tin nhắn. 2. `POST /chatbot/conversations/{id}/stream`. 3. ChatbotService lưu USER message. 4. Load conversation history. 5. OpenAIClientService streaming → SseEmitter. 6. Client nhận từng chunk qua SSE. 7. Render markdown realtime. 8. Khi [DONE] → lưu ASSISTANT message. |
| **Luồng ngoại lệ** | 5a. Timeout 5 phút → SseEmitter timeout. 5b. AI error → error event. |
| **API** | `POST /api/v1/chatbot/conversations/{id}/stream` |

*Bảng 1.7: Đặc tả UC-18 — Chat với AI Bot*

*Bảng 1.8: Đặc tả UC-15 — Push notification*

### UC-19: Gọi video 1-1 (Agora RTC)

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Gọi video trực tuyến 1-1 |
| **Actor** | Người dùng (đã kết bạn) |
| **Tiền điều kiện** | Cả 2 đang online, đã cấp quyền Camera/Mic |
| **Hậu điều kiện** | Cuộc gọi được thiết lập, data stream qua Agora |
| **Luồng chính** | 1. Chọn user → nhấn icon Video Call. 2. `POST /api/v1/calls/token` lấy Agora token. 3. Gửi STOMP signal notify receiver. 4. Receiver nhận cuộc gọi → join Agora channel. 5. Hiển thị view video 2 phía. 6. Nhấn 'End' để kết thúc. |
| **Luồng ngoại lệ** | 3a. Receiver bận → hiển thị thông báo. |
| **API** | `POST /api/v1/calls/token` |

---

<div style="page-break-before: always;"></div>

# Chương 2: Phân tích thiết kế

## 2.1 Kiến trúc tổng quan

```mermaid
graph TB
    subgraph Client["Client Layer"]
        FL["Flutter App<br/>(Android / iOS / Web)"]
    end
    subgraph Gateway["API Gateway"]
        CA["Caddy Reverse Proxy<br/>:8080"]
    end
    subgraph Backend["Application Layer"]
        SB["Spring Boot 4 App<br/>REST API + WebSocket"]
    end
    subgraph Data["Data Layer"]
        PG["PostgreSQL 18"]
        RD["Redis 8"]
    end
    subgraph Messaging["Messaging Layer"]
        AR["Apache Artemis<br/>STOMP Broker"]
    end
    subgraph Storage["Storage Layer"]
        S3["VersityGW<br/>S3-Compatible"]
    end
    subgraph External["External Services"]
        FB["Firebase Cloud Messaging"]
        AI["OpenAI API (kilo.ai)"]
        AG["Agora RTC"]
    end

    FL <-->|"HTTP/WS"| CA
    CA <-->|"Proxy"| SB
    CA <-->|"/storage/*"| S3
    SB <-->|"JPA"| PG
    SB <-->|"Cache"| RD
    SB <-->|"STOMP"| AR
    SB -->|"Upload"| S3
    SB -->|"Push"| FB
    SB -->|"AI"| AI
    SB -->|"Token"| AG
```

[image: tv4_so_o_kien_truc_tong_quan_he_thong_chatapp.png]
*Hình 2.1: Sơ đồ kiến trúc tổng quan hệ thống ChatApp*

## 2.2 Biểu đồ Use Case tổng quan

```mermaid
graph LR
    User((Người dùng))
    AI_Bot((AI Chatbot))
    System((Hệ thống))

    subgraph UC_Auth["Quản lý tài khoản"]
        UC1["Đăng ký / Đăng nhập"]
        UC4["Cập nhật profile"]
    end
    subgraph UC_Friend["Quản lý bạn bè"]
        UC6["Lời mời kết bạn"]
        UC9["Chặn người dùng"]
    end
    subgraph UC_Chat["Nhắn tin"]
        UC10["Gửi tin nhắn 1-1"]
        UC11["Gửi tin nhắn nhóm"]
        UC12["Gửi hình ảnh/media"]
        UC13["Xóa tin nhắn"]
    end
    subgraph UC_Group["Quản lý nhóm"]
        UC14["Tạo nhóm chat"]
        UC15["Quản lý thành viên"]
        UC17["Ghim cuộc trò chuyện"]
    end
    subgraph UC_AI["Tính năng AI"]
        UC18["Tóm tắt hội thoại"]
        UC19["Dịch tin nhắn"]
        UC20["Chat với AI Bot"]
    end
    subgraph UC_Notify["Thông báo"]
        UC21["Nhận thông báo đẩy"]
        UC22["Bật/tắt thông báo"]
    end

    User --- UC_Auth
    User --- UC_Friend
    User --- UC_Chat
    User --- UC_Group
    User --- UC_AI
    User --- UC_Notify
    AI_Bot --- UC20
    System --- UC21
```

[image: tv4_bieu_o_use_case_tong_quan.png]
*Hình 2.2: Biểu đồ Use Case tổng quan*

---

## 2.3 Biểu đồ Use Case chi tiết — Media, Thông báo & AI Chatbot

```mermaid
graph LR
    User((Người dùng))
    System((Hệ thống))

    subgraph UC_Media["Module Media"]
        UC1["Gửi hình ảnh trong chat"]
        UC2["Gửi video/tài liệu/âm thanh"]
        UC3["Xem hình ảnh toàn màn hình"]
        UC4["Download tệp đính kèm"]
    end

    subgraph UC_Notify["Module Thông báo"]
        UC5["Nhận push notification tin nhắn"]
        UC6["Nhận push notification lời mời"]
        UC7["Nhận push notification nhóm"]
        UC8["Bật/tắt thông báo toàn cục"]
        UC9["Đăng ký FCM token"]
    end

    subgraph UC_AI["Module AI Chatbot"]
        UC10["Tạo cuộc trò chuyện AI"]
        UC11["Gửi tin nhắn cho AI"]
        UC12["Nhận phản hồi streaming SSE"]
        UC13["Xem lịch sử chatbot"]
        UC14["Xóa cuộc trò chuyện AI"]
    end

    User --- UC1
    User --- UC2
    User --- UC3
    User --- UC4
    User --- UC5
    User --- UC6
    User --- UC7
    User --- UC8
    User --- UC9
    User --- UC10
    User --- UC11
    User --- UC12
    User --- UC13
    User --- UC14

    System --- UC5
    System --- UC6
    System --- UC7

    UC1 -.->|"include"| UC4
    UC11 -.->|"include"| UC12
```

[image: tv4_bieu_o_use_case_chi_tiet_media_thong_bao_ai.png]
*Hình 2.3: Biểu đồ Use Case chi tiết — Media, Thông báo & AI*

**Mô tả chi tiết:**

**UC1 — Gửi hình ảnh**: Chọn ảnh từ gallery (ImagePicker) → multipart upload `POST /api/v1/messages/?room={id}` → AttachmentService xác định FileType → S3Service upload lên VersityGW → tạo Attachment entity → attach vào ChatMessage → STOMP broadcast.

**UC2 — Gửi video/tài liệu/âm thanh**: Tương tự UC1 nhưng FileTypeService phân loại: VIDEO, DOCUMENT (pdf, doc, xls...), AUDIO (mp3, wav...), RAW (các định dạng khác).

**UC5 — Push notification tin nhắn**: MessageService gửi tin nhắn → NotificationService kiểm tra NotificationPreference → FcmTokenService lấy token của receiver → Firebase Admin SDK gửi push → Client FCM handler → LocalNotificationService hiển thị.

**UC8 — Bật/tắt thông báo**: `PUT /api/v1/users/me/notification-settings/` → NotificationPreferenceService cập nhật setting. Khi tắt, NotificationService skip push.

**UC10 — Tạo cuộc trò chuyện AI**: `POST /api/v1/chatbot/conversations` → ChatbotService tạo ChatbotConversation(title, modelName, mcpEnabled).

**UC12 — Streaming SSE**: `POST /api/v1/chatbot/conversations/{id}/stream` → ChatbotService → OpenAIClientService streaming → SseEmitter gửi từng chunk → Client render markdown realtime.

## 2.4 Biểu đồ lớp — Module Media, Thông báo & AI

```mermaid
classDiagram
    class Attachment {
        -Long id
        -String source
        -FileType type
        +isImage() boolean
        +of(String, FileType) Attachment
        +isDocumentFormat(String) boolean
        +isAudioFormat(String) boolean
    }

    class FcmToken {
        -Long id
        -User user
        -String token
        -Timestamp createdAt
        -Timestamp lastUsed
    }

    class ChatbotConversation {
        -Long id
        -User owner
        -String title
        -String modelName
        -boolean mcpEnabled
        -String mcpSessionId
        -Timestamp createdOn
        -Timestamp updatedOn
    }

    class ChatbotMessage {
        -Long id
        -ChatbotConversation conversation
        -Role role
        -String content
        -String metadataJson
        -Timestamp createdOn
    }

    class AttachmentService {
        +createAttachment(MultipartFile) Attachment
        +getFileType(String) FileType
    }

    class S3Service {
        +upload(String, InputStream) String
        +delete(String) void
        +getPresignedUrl(String) String
    }

    class NotificationService {
        +notifyNewMessage(ChatMessage) void
        +notifyInvitation(Invitation) void
        +notifyGroupAction(ChatRoom, action) void
    }

    class FcmTokenService {
        +registerToken(String) void
        +getTokensByUserId(Long) List
        +cleanupExpiredTokens() void
    }

    class NotificationPreferenceService {
        +isPushEnabled(Long) boolean
        +setPushEnabled(Long, boolean) void
    }

    class ChatbotService {
        +listConversations() List
        +createConversation(dto) ChatbotConversationDto
        +listMessages(conversationId) List
        +deleteConversation(conversationId) void
        +streamReply(conversationId, dto) SseEmitter
    }

    class FileTypeService {
        +determineFileType(String) FileType
    }

    class LocalNotificationService {
        +show(title, body, payload) void
        +initialize() void
    }

    Attachment "0..*" --> "1" ChatMessage : attachments
    FcmToken "N" --> "1" User : user
    ChatbotConversation "N" --> "1" User : owner
    ChatbotMessage "N" --> "1" ChatbotConversation : conversation

    AttachmentService ..> S3Service : uploads
    AttachmentService ..> FileTypeService : classifies
    NotificationService ..> FcmTokenService : getTokens
    NotificationService ..> NotificationPreferenceService : checks
    ChatbotService ..> OpenAIClientService : streams
    ChatbotService ..> ChatbotConversation : manages
    ChatbotService ..> ChatbotMessage : stores
```

[image: tv4_bieu_o_lop_module_media_thong_bao_ai.png]
*Hình 2.4: Biểu đồ lớp — Module Media, Thông báo & AI*

**Giải thích:**

- **Attachment**: 5 FileTypes (IMAGE, VIDEO, RAW, DOCUMENT, AUDIO). Static helper methods phân loại định dạng file.
- **FcmToken**: Lưu FCM registration token per device. Unique constraint (user_id, token). FcmTokenCleanupJob dọn token hết hạn.
- **ChatbotConversation**: Cuộc trò chuyện AI, hỗ trợ MCP (Model Context Protocol). Mỗi conversation thuộc 1 user.
- **ChatbotMessage**: Tin nhắn trong chatbot với 4 roles: USER, ASSISTANT, SYSTEM, TOOL.
- **S3Service**: Quản lý upload/download file lên VersityGW (S3-compatible) qua AWS SDK.
- **NotificationService**: Orchestrator push notification, kiểm tra preference trước khi gửi.
- **ChatbotService (~21KB)**: Service lớn xử lý streaming SSE qua SseEmitter, quản lý conversation lifecycle.

### 2.5 Biểu đồ tuần tự

### 2.5.1 Biểu đồ tuần tự — Gửi hình ảnh

```mermaid
sequenceDiagram
    actor User as Người gửi
    participant App as Flutter App
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant MsgSvc as MessageService
    participant AttSvc as AttachmentService
    participant FTSvc as FileTypeService
    participant S3 as S3Service
    participant Store as VersityGW
    participant DB as PostgreSQL
    participant STOMP as STOMP Broker
    participant AppB as Flutter App B

    User->>App: Chọn ảnh từ gallery
    App->>App: ImagePicker.pickImage()
    App->>API: POST /api/v1/messages/?room=1
    Note over App,API: multipart/form-data (file + message)
    API->>SB: Forward multipart
    SB->>MsgSvc: sendMessage(roomId, dto)
    MsgSvc->>AttSvc: createAttachment(file)
    AttSvc->>FTSvc: determineFileType(filename)
    FTSvc-->>AttSvc: FileType.IMAGE
    AttSvc->>S3: upload(key, inputStream)
    S3->>Store: PUT object
    Store-->>S3: 200 OK
    S3-->>AttSvc: S3 key
    AttSvc->>DB: INSERT Attachment(source, IMAGE)
    DB-->>AttSvc: Attachment saved
    AttSvc-->>MsgSvc: Attachment entity
    MsgSvc->>DB: INSERT ChatMessage + attachments
    DB-->>MsgSvc: Saved
    MsgSvc->>STOMP: Broadcast to /queue/chat/1
    STOMP-->>AppB: STOMP MESSAGE (with attachment URL)
    AppB->>AppB: CachedNetworkImage load từ /storage/*
    MsgSvc-->>SB: Response
    SB-->>API: 201 Created
    API-->>App: Response
```

[image: tv4_bieu_o_tuan_tu_gui_hinh_anh.png]
*Hình 2.5: Biểu đồ tuần tự — Gửi hình ảnh*

### 2.5.2 Biểu đồ tuần tự — Push notification

```mermaid
sequenceDiagram
    participant MsgSvc as MessageService
    participant NotifSvc as NotificationService
    participant PrefSvc as NotificationPreferenceService
    participant FcmSvc as FcmTokenService
    participant DB as PostgreSQL
    participant Firebase as Firebase Admin SDK
    participant FCM as Firebase Cloud Messaging
    participant Device as Client Device
    participant LocalNotif as LocalNotificationService

    MsgSvc->>NotifSvc: notifyNewMessage(chatMessage)
    NotifSvc->>PrefSvc: isPushEnabled(receiverId)
    PrefSvc->>DB: Check notification_preferences
    DB-->>PrefSvc: enabled = true
    PrefSvc-->>NotifSvc: true
    NotifSvc->>FcmSvc: getTokensByUserId(receiverId)
    FcmSvc->>DB: SELECT FROM fcm_tokens
    DB-->>FcmSvc: List of tokens
    FcmSvc-->>NotifSvc: ["token1", "token2"]
    NotifSvc->>Firebase: sendMulticast(message, tokens)
    Firebase->>FCM: Deliver push
    FCM-->>Device: Push notification
    Device->>Device: FirebaseMessagingService.onMessage()
    Device->>LocalNotif: show(title, body, payload)
    LocalNotif-->>Device: System notification displayed
```

[image: tv4_bieu_o_tuan_tu_push_notification.png]
*Hình 2.6: Biểu đồ tuần tự — Push notification*

### 2.5.3 Biểu đồ tuần tự — Chatbot AI streaming (SSE)

```mermaid
sequenceDiagram
    actor User as Người dùng
    participant App as Flutter App
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant BotSvc as ChatbotService
    participant DB as PostgreSQL
    participant OpenAI as OpenAIClientService
    participant LLM as kilo.ai LLM

    User->>App: Nhập tin nhắn cho AI
    App->>API: POST /chatbot/conversations/1/stream
    Note over App,API: {message: "Hello AI", model: "gpt-4o"}
    API->>SB: Forward (Accept: text/event-stream)
    SB->>BotSvc: streamReply(conversationId, dto)
    BotSvc->>DB: Save ChatbotMessage(USER, content)
    BotSvc->>DB: Load conversation history
    DB-->>BotSvc: List of previous messages
    BotSvc->>BotSvc: Create SseEmitter(timeout=5min)
    BotSvc->>OpenAI: Streaming chat completion
    OpenAI->>LLM: API call (stream=true)

    loop Streaming chunks
        LLM-->>OpenAI: delta chunk
        OpenAI-->>BotSvc: onNext(chunk)
        BotSvc->>SB: SseEmitter.send(chunk)
        SB-->>API: SSE event
        API-->>App: data: {"content": "..."}
        App->>App: Append to markdown buffer
        App-->>User: Render markdown realtime
    end

    LLM-->>OpenAI: [DONE]
    OpenAI-->>BotSvc: onComplete()
    BotSvc->>DB: Save ChatbotMessage(ASSISTANT, fullContent)
    BotSvc->>SB: SseEmitter.complete()
    SB-->>API: SSE stream end
    API-->>App: Connection closed
```

[image: tv4_bieu_o_tuan_tu_chatbot_ai_streaming.png]
*Hình 2.7: Biểu đồ tuần tự — Chatbot AI streaming (SSE)*

### 2.5.4 Biểu đồ tuần tự — Video Call (Agora RTC)

```mermaid
sequenceDiagram
    actor UserA as Người gọi
    participant AppA as Flutter App A
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant Agora as Agora Service
    participant AgoraCloud as Agora Cloud
    participant STOMP as STOMP Broker
    participant AppB as Flutter App B
    actor UserB as Người nhận

    UserA->>AppA: Nhấn nút Video Call
    AppA->>API: POST /api/v1/chatrooms/{id}/video-call/
    API->>SB: Forward request
    SB->>Agora: generateToken(channelName, uid)
    Agora->>AgoraCloud: Request RTC token
    AgoraCloud-->>Agora: Token + channel info
    Agora-->>SB: VideoCallResponseDto
    SB->>STOMP: Send to /queue/calls/video (UserB)
    STOMP-->>AppB: VideoCallEvent
    AppB-->>UserB: Hiển thị incoming call UI
    SB-->>API: 200 OK + token
    API-->>AppA: Response
    AppA->>AppA: Join Agora channel
    UserB->>AppB: Accept call
    AppB->>AppB: Join Agora channel
    Note over AppA,AppB: Video/Audio stream qua Agora Cloud
    UserA->>AppA: End call
    AppA->>AppA: Leave channel
```

[image: tv4_bieu_o_tuan_tu_video_call_agora_rtc.png]
*Hình 2.8: Biểu đồ tuần tự — Video Call (Agora RTC)*


## 2.6 Sơ đồ thực thể quan hệ — ER Diagram

Sơ đồ ER mô tả toàn bộ 12 entity. Các entity **★** thuộc phạm vi Thành viên 4.

```mermaid
erDiagram
    USER {
        Long id PK
        String username UK
        String password
        String display_name
        Long avatar_id FK
    }
    INVITATION {
        Long id PK
        Long sender_id FK
        Long receiver_id FK
        Long chat_room_id FK
        Status status
    }
    USER_BLOCK {
        Long id PK
        Long blocker_id FK
        Long blocked_id FK
    }
    ATTACHMENT {
        Long id PK
        String source
        FileType type
    }
    FCM_TOKEN {
        Long id PK
        Long user_id FK
        String token
        Timestamp created_at
        Timestamp last_used
    }
    CHAT_ROOM {
        Long id PK
        String name
        Type type
        Long creator_id
    }
    CHAT_MESSAGE {
        Long id PK
        Long sender_id FK
        Long room_id FK
        String message
        Status status
        Timestamp sent_on
    }
    CHAT_ROOM_MEMBER {
        Long id PK
        Long chat_room_id FK
        Long user_id FK
        Boolean is_admin
    }
    CHAT_ROOM_PIN {
        Long id PK
        Long user_id FK
        Long chat_room_id FK
    }
    CHAT_ROOM_READ_STATE {
        Long id PK
        Long room_id FK
        Long reader_id FK
        Timestamp last_read_at
    }
    CHATBOT_CONVERSATION {
        Long id PK
        Long owner_id FK
        String title
        String model_name
        Boolean mcp_enabled
        String mcp_session_id
        Timestamp created_on
        Timestamp updated_on
    }
    CHATBOT_MESSAGE {
        Long id PK
        Long conversation_id FK
        Role role
        String content
        String metadata_json
        Timestamp created_on
    }

    USER ||--o| ATTACHMENT : "avatar"
    USER ||--o{ INVITATION : "sender"
    USER ||--o{ INVITATION : "receiver"
    USER ||--o{ USER_BLOCK : "blocker"
    USER ||--o{ USER_BLOCK : "blocked"
    USER ||--o{ FCM_TOKEN : "tokens"
    USER }o--o{ CHAT_ROOM : "members"
    INVITATION }o--o| CHAT_ROOM : "chatRoom"
    CHAT_ROOM ||--o{ CHAT_MESSAGE : "messages"
    CHAT_ROOM ||--o{ CHAT_ROOM_MEMBER : "roles"
    CHAT_ROOM ||--o{ CHAT_ROOM_PIN : "pins"
    CHAT_ROOM ||--o{ CHAT_ROOM_READ_STATE : "readStates"
    CHAT_ROOM ||--o| ATTACHMENT : "avatar"
    CHAT_MESSAGE ||--o{ ATTACHMENT : "attachments"
    CHAT_MESSAGE }o--o| CHAT_MESSAGE : "replyTo"
    USER ||--o{ CHAT_MESSAGE : "sender"
    USER ||--o{ CHAT_ROOM_MEMBER : "member"
    USER ||--o{ CHATBOT_CONVERSATION : "owner"
    CHATBOT_CONVERSATION ||--o{ CHATBOT_MESSAGE : "messages"
```

[image: tv4_so_o_thuc_the_quan_he_er_diagram.png]
*Hình 2.12: Sơ đồ thực thể quan hệ (ER Diagram)*
**Chi tiết các trường quan trọng:**

| Entity | Trường | Kiểu | Constraint | Mô tả |
|--------|--------|------|-----------|-------|
| USER | username | String | UNIQUE, NOT NULL | Tên đăng nhập duy nhất |
| USER | password | String | NOT NULL | Mã hóa Argon2 |
| USER | display_name | String | Nullable | Tên hiển thị |
| CHAT_ROOM | type | Enum | NOT NULL | DUO hoặc GROUP |
| CHAT_ROOM | name | String | Nullable | Tên nhóm (NULL cho DUO) |
| CHAT_MESSAGE | status | Enum | NOT NULL | SENT / RECALLED |
| CHAT_MESSAGE | reply_to_id | Long | FK, Nullable | Tin nhắn được trả lời |
| CHAT_ROOM_MEMBER | is_admin | Boolean | NOT NULL | Quyền admin trong nhóm |
| CHAT_ROOM_PIN | (user, room) | — | UNIQUE | Ngăn ghim trùng |
| ATTACHMENT | type | Enum | NOT NULL | IMAGE/VIDEO/DOCUMENT/AUDIO/RAW |
| FCM_TOKEN | (user, token) | — | UNIQUE | Ngăn đăng ký trùng |
| CHATBOT_MESSAGE | role | Enum | NOT NULL | USER/ASSISTANT/SYSTEM/TOOL |
| INVITATION | status | Enum | NOT NULL | PENDING/ACCEPTED/REJECTED |

*Bảng 2.1: Chi tiết các trường entity trong hệ thống*

**Các ràng buộc quan trọng:**
- **Cascade Delete:** Khi xóa ChatRoom → cascade xóa ChatMessage, ChatRoomMember, ChatRoomPin, ChatRoomReadState.
- **Unique Constraints:** (user_id, chat_room_id) trong ChatRoomPin, (user_id, token) trong FcmToken, (blocker_id, blocked_id) trong UserBlock.
- **Foreign Key References:** ChatMessage.sender_id → User, ChatMessage.room_id → ChatRoom, Invitation.sender_id → User.
- **Index Strategy:** Index trên username (User), 
oom_id + sent_on (ChatMessage), user_id (FcmToken) để tối ưu query.


**★ Entity thuộc phạm vi Thành viên 4:** ATTACHMENT, FCM_TOKEN, CHATBOT_CONVERSATION, CHATBOT_MESSAGE.

## 2.7 Giao diện đáp ứng chức năng

### 2.7.1 Gửi hình ảnh trong ChatScreen

**File:** `lib/screens/chat/chat_screen.dart`

**Mô tả:** Trong ChatScreen, nút đính kèm (📎) mở ImagePicker để chọn ảnh từ gallery hoặc camera. Ảnh được preview trước khi gửi. Sau khi gửi, ảnh hiển thị trong MessageBubble qua CachedNetworkImage, load từ `/storage/*` qua Caddy Gateway.

[image: tv4_giao_dien_chatscreen_gui_hinh_anh.png]
*Hình 2.13: Giao diện ChatScreen — Gửi hình ảnh*

`[Ảnh chụp màn hình gửi ảnh — placeholder]`

### 2.7.2 Màn hình AI Chatbot (ChatbotScreen)

**File:** `lib/screens/home/chatbot_screen.dart`

**Mô tả:** Giao diện chat với AI chatbot. Hiển thị danh sách cuộc trò chuyện AI ở sidebar, nội dung chat ở main area. Tin nhắn AI được render dưới dạng Markdown (flutter_markdown_plus). Hỗ trợ streaming — text xuất hiện từng ký tự.

**Các thành phần chính:**
- ListView conversations (sidebar)
- FAB tạo conversation mới
- Chat area: MessageBubble cho USER + ASSISTANT
- ASSISTANT messages render Markdown
- Streaming indicator khi AI đang trả lời
- Input bar gửi tin nhắn

[image: tv4_giao_dien_ai_chatbot.png]
*Hình 2.14: Giao diện AI Chatbot*

`[Ảnh chụp màn hình Chatbot — placeholder]`

### 2.7.3 Màn hình Cài đặt thông báo

**File:** `lib/screens/home/settings_screen.dart`

**Mô tả:** Trong SettingsScreen, có toggle switch bật/tắt thông báo đẩy. Gọi `PUT /api/v1/users/me/notification-settings/` để cập nhật server-side preference.

[image: tv4_giao_dien_cai_at_thong_bao.png]
*Hình 2.15: Giao diện Cài đặt thông báo*

`[Ảnh chụp màn hình Notification settings — placeholder]`

---

<div style="page-break-before: always;"></div>



**Chi tiết cấu hình WebSocket (WebSocketConfig.kt):**

```kotlin
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
               .setRelayHost(relayHost)  // Apache Artemis
               .setRelayPort(relayPort)  // 61613
        registry.setApplicationDestinationPrefixes("/app")
    }
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/socket").withSockJS()  // SockJS fallback
        registry.addEndpoint("/ws")                    // Native WebSocket
    }
}
```

**Giải thích:**
- STOMP Broker Relay kết nối đến Apache Artemis (external broker) thay vì sử dụng simple broker.
- Hai endpoints: `/socket` (SockJS fallback cho trình duyệt cũ) và `/ws` (native WebSocket cho Flutter).
- `WebSocketAuthenticationInterceptor` xác thực JWT token trong STOMP CONNECT frame.
- Application destination prefix `/app` cho messages từ client → server (typing indicator, read receipt).

**Chi tiết cấu hình Security (SecurityConfig.kt):**

```kotlin
@Bean
fun passwordEncoder() = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

@Bean
fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http {
        oauth2ResourceServer { jwt { jwtDecoder = jwtDecoder } }
        authorizeHttpRequests {
            authorize("/api/v1/messages/**", authenticated)
            authorize("/api/v1/chatbot/**", authenticated)
            authorize("/api/v1/invitations/**", authenticated)
            authorize("/api/v1/users/me/**", authenticated)
            authorize(anyRequest, permitAll)
        }
    }
}
```

**Giải thích:**
- Sử dụng **Argon2PasswordEncoder** (PHC winner 2015) — bảo mật hơn BCrypt nhờ memory-hard function.
- JWT xác thực qua **HMAC-SHA256** (symmetric key ≥ 32 bytes) với NimbusJwtDecoder.
- Các endpoint `/api/v1/messages/**`, `/api/v1/chatbot/**`, `/api/v1/invitations/**`, `/api/v1/users/me/**` yêu cầu authenticated.
- Các endpoint public: đăng ký, đăng nhập, tìm kiếm user, quên mật khẩu.


**Chi tiết kiến trúc Flutter Frontend:**

Hệ thống Flutter sử dụng **Provider** (official state management) với cấu trúc phân lớp rõ ràng:

| Layer | Thư mục | Chức năng | Ví dụ |
|-------|---------|-----------|-------|
| **Models** | `lib/models/` | Data classes (JSON serialization) | `ChatRoomModel`, `MessageReceiveModel` |
| **Services** | `lib/services/` | API calls, business logic | `RealtimeService` (30KB), `ChatbotService` |
| **Providers** | `lib/providers/` | State management (ChangeNotifier) | `ChatRoomsProvider`, `AuthProvider` |
| **Screens** | `lib/screens/` | UI pages | `ChatScreen`, `ChatbotScreen` |
| **Widgets** | `lib/widgets/` | Reusable UI components | `MessageBubble`, `AppAvatar` |
| **Utils** | `lib/utils/` | Helpers, formatters | Date formatting, error handling |
| **Core** | `lib/core/` | Constants, theme | `AppConstants`, base URL |

*Bảng 2.2: Cấu trúc phân lớp Flutter Frontend*

**Các service chính (Frontend):**

| Service | File | Kích thước | Chức năng |
|---------|------|-----------|-----------|
| RealtimeService | `realtime_service.dart` | 30KB (1021 dòng) | WebSocket STOMP, 15+ event types |
| MessageService | `message_service.dart` | 8.6KB | CRUD tin nhắn, multipart upload |
| GroupChatService | `group_chat_service.dart` | 8KB | Quản lý nhóm chat |
| ChatbotService | `chatbot_service.dart` | 5.4KB | AI chatbot SSE streaming |
| ApiClient | `api_client.dart` | 8.1KB | HTTP client, JWT refresh, error handling |
| LocalNotificationService | `local_notification_service.dart` | 5.3KB | Hiển thị notification trên device |
| FcmTokenManagerService | `fcm_token_manager_service.dart` | 3.8KB | Quản lý FCM token lifecycle |
| AgoraService | `agora_service.dart` | 6.9KB | Video call via Agora RTC |

*Bảng 2.3: Danh sách services trong Flutter Frontend*

**Luồng dữ liệu Provider:**

`mermaid
flowchart LR
    UI[Screen/Widget] -->|listen| Provider
    Provider -->|call| Service
    Service -->|HTTP| ApiClient
    ApiClient -->|REST| CaddyGW[Caddy Gateway]
    Service -->|STOMP| RealtimeService
    RealtimeService -->|WebSocket| CaddyGW
    Provider -->|notifyListeners| UI
`

[image: tv4_luong_du_lieu_provider_trong_flutter.png]
*Hình 2.16: Luồng dữ liệu Provider trong Flutter*

**Chi tiết ApiClient (`api_client.dart`, 8.1KB):**
- Tự động attach JWT access token vào mọi request header.
- Khi nhận 401 Unauthorized, tự động refresh access token và retry request.
- Sử dụng `TokenStorageService` (SharedPreferences) để lưu/đọc token.
- Support multipart/form-data upload cho ảnh và media files.
- Centralized error handling: parse error response từ server.

**Chi tiết RealtimeService (`realtime_service.dart`, 30KB):**
- Quản lý lifecycle WebSocket: connect, disconnect, reconnect.
- 15+ `StreamController.broadcast()` cho các loại event.
- `_requestedRooms` Set: theo dõi rooms cần subscribe.
- `_activeRoomSubscriptions`: ngăn duplicate STOMP subscriptions.
- Reconnect strategy: deactivate old client → refresh token → create new client.
- SockJS fallback endpoint `/socket` + native WebSocket endpoint `/ws`.

## 2.8 Bảng API Endpoints — Module Media, Thông báo & AI

| STT | Method | Endpoint | Auth | Mô tả | Status Codes |
|:---:|--------|----------|:----:|-------|:------------:|
| 1 | POST | `/api/v1/messages/?room={id}` | ✓ | Gửi tin nhắn + media (multipart) | 201, 403 |
| 2 | POST | `/api/v1/users/me/fcm-token/` | ✓ | Đăng ký FCM token | 201 |
| 3 | GET | `/api/v1/users/me/notification-settings/` | ✓ | Lấy cài đặt thông báo | 200 |
| 4 | PUT | `/api/v1/users/me/notification-settings/` | ✓ | Cập nhật cài đặt thông báo | 200 |
| 5 | GET | `/api/v1/chatbot/conversations` | ✓ | Danh sách cuộc trò chuyện AI | 200 |
| 6 | POST | `/api/v1/chatbot/conversations` | ✓ | Tạo cuộc trò chuyện AI mới | 201 |
| 7 | GET | `/api/v1/chatbot/conversations/{id}/messages` | ✓ | Lịch sử chat AI | 200 |
| 8 | POST | `/api/v1/chatbot/conversations/{id}/stream` | ✓ | Streaming SSE chat AI | 200 (SSE) |
| 9 | DELETE | `/api/v1/chatbot/conversations/{id}` | ✓ | Xóa cuộc trò chuyện AI | 204 |
| 10 | POST | `/api/v1/speech-to-text` | ✓ | Chuyển giọng nói → text | 200 |

*Bảng 2.4: API Endpoints — Module Media, Thông báo & AI*

## 2.9 Biểu đồ hoạt động — Luồng upload media & push notification

```mermaid
flowchart TD
    A([Bắt đầu]) --> B[Người dùng chọn file đính kèm]
    B --> C{Loại file?}
    C -->|Ảnh| D[ImagePicker.pickImage]
    C -->|Video/Tài liệu/Audio| E[FilePicker.pickFiles]
    D --> F[Tạo multipart request]
    E --> F
    F --> G[POST /api/v1/messages/?room=id]
    G --> H[AttachmentService.createAttachment]
    H --> I[FileTypeService.determineFileType]
    I --> J{FileType?}
    J -->|IMAGE| K[FileType.IMAGE]
    J -->|VIDEO| L[FileType.VIDEO]
    J -->|PDF/DOC| M[FileType.DOCUMENT]
    J -->|MP3/WAV| N[FileType.AUDIO]
    J -->|Khác| O[FileType.RAW]
    K --> P[S3Service.upload → VersityGW]
    L --> P
    M --> P
    N --> P
    O --> P
    P --> Q[INSERT Attachment + ChatMessage]
    Q --> R[STOMP broadcast tin nhắn]
    R --> S[NotificationService.notifyNewMessage]
    S --> T{Push enabled?}
    T -->|Không| U([Kết thúc])
    T -->|Có| V[FcmTokenService.getTokens]
    V --> W[Firebase sendMulticast]
    W --> U
```

[image: tv4_bieu_o_hoat_ong_upload_media_push_notification.png]
*Hình 2.17: Biểu đồ hoạt động — Upload media & push notification*

---

<div style="page-break-before: always;"></div>

# Chương 3: Kết quả

## 3.1 Mô hình triển khai

```mermaid
graph TB
    subgraph DC["Docker Compose — ChatApp"]
        GW["Caddy Gateway :8080"]
        APP["Spring Boot 4 App"]
        PG["PostgreSQL 18"]
        RD["Redis 8"]
        AR["Apache Artemis"]
        S3["VersityGW"]
    end
    Client["Flutter Client"] -->|":8080"| GW
    GW -->|"/api/*, /ws*"| APP
    GW -->|"/storage/*"| S3
    APP --> PG
    APP --> RD
    APP --> AR
    APP --> S3
    APP -.-> FB["Firebase FCM"]
    APP -.-> AI["kilo.ai"]
```

[image: tv4_so_o_trien_khai_docker_compose.png]
*Hình 3.1: Sơ đồ triển khai Docker Compose*

| Service | Image | Chức năng |
|---------|-------|-----------|
| gateway | caddy:2-alpine | Reverse proxy |
| app | Custom Dockerfile | Spring Boot application |
| postgres | postgres:18-alpine | Relational database |
| redis | redis:8-alpine | Cache & presence |
| artemis | apache/artemis:2.53.0 | STOMP message broker |
| versitygw | versity/versitygw | S3-compatible storage |

*Bảng 3.1: Danh sách services*


**Luồng khởi động:****
1. Docker Compose khởi tạo PostgreSQL, Redis, Artemis, VersityGW song song.
2. Mỗi service phải pass health check trước khi service phụ thuộc khởi động.
3. Spring Boot app khởi động sau khi tất cả dependencies healthy.
4. Caddy gateway khởi động sau khi app và versitygw sẵn sàng.
5. Toàn bộ hệ thống sẵn sàng phục vụ qua port 8080.

## 3.2 Các bước cài đặt và triển khai

### 3.2.1 Backend

```bash
git clone <repository-url> chatapp && cd chatapp
cp .env.example .env
mkdir -p secrets && cp <firebase-key> secrets/firebase-service-account.json
docker compose up -d
```

### 3.2.2 Frontend

```bash
git clone <repository-url> chatapp-flutter && cd chatapp-flutter
cp .env.example.json .env.json
flutter pub get && flutter run
```



## 3.3 Kết quả thực hiện — Media, Thông báo & AI Chatbot

### 3.3.1 Gửi hình ảnh / Media

- Chọn ảnh qua ImagePicker → multipart upload `POST /api/v1/messages/?room={id}`.
- AttachmentService phân loại FileType: IMAGE, VIDEO, DOCUMENT, AUDIO, RAW.
- S3Service upload file lên VersityGW → trả về S3 key → tạo Attachment entity.
- ChatMessage được tạo với attachments list → STOMP broadcast.
- Client load ảnh qua CachedNetworkImage từ `/storage/*` (Caddy proxy → VersityGW).
- Hỗ trợ preview ảnh toàn màn hình khi tap.

[image: tv4_ket_qua_gui_hinh_anh.png]
*Hình 3.2: Kết quả — Gửi hình ảnh*

`[Ảnh chụp màn hình gửi ảnh — placeholder]`

### 3.3.2 Push Notification (FCM)

- Firebase Admin SDK (v9.8.0) tích hợp trong Spring Boot.
- FCM token được đăng ký khi user đăng nhập: `POST /api/v1/users/fcm-token/`.
- FcmToken entity lưu per-device token với unique constraint (user_id, token).
- NotificationService kiểm tra NotificationPreference trước khi gửi.
- Hỗ trợ 3 loại notification: tin nhắn mới, lời mời kết bạn, action nhóm.
- Client-side: FirebaseMessagingService handle background + foreground messages.
- LocalNotificationService hiển thị system notification với title/body/payload.

[image: tv4_ket_qua_push_notification.png]
*Hình 3.3: Kết quả — Push notification*

`[Ảnh chụp màn hình Push notification — placeholder]`

### 3.3.3 Bật/tắt thông báo

- SettingsScreen có SwitchListTile toggle notification.
- `PUT /api/v1/users/me/notification-settings/` cập nhật server-side preference.
- Khi tắt, NotificationService.isPushEnabled() = false → skip gửi push.
- Setting persist trong database, đồng bộ across devices.

### 3.3.4 AI Chatbot — Streaming SSE

- **Tạo conversation**: `POST /api/v1/chatbot/conversations` → ChatbotConversation(title, modelName, mcpEnabled).
- **Gửi tin nhắn**: `POST /api/v1/chatbot/conversations/{id}/stream` → SseEmitter streaming.
- **Luồng streaming**:
  1. Lưu ChatbotMessage(USER) vào DB.
  2. Load conversation history từ DB.
  3. Tạo SseEmitter(timeout=5 phút).
  4. OpenAIClientService gọi LLM (stream=true).
  5. Mỗi delta chunk → SseEmitter.send() → SSE event → client.
  6. Client append chunk vào markdown buffer → render realtime.
  7. Khi hoàn tất → lưu ChatbotMessage(ASSISTANT) → SseEmitter.complete().
- **Lịch sử**: `GET /api/v1/chatbot/conversations/{id}/messages` → danh sách ChatbotMessage.
- **Xóa**: `DELETE /api/v1/chatbot/conversations/{id}` → cascade delete messages.
- **MCP Support**: mcpEnabled flag cho Model Context Protocol integration.

`[Ảnh chụp màn hình AI Chatbot — placeholder]`

### 3.3.5 Gọi video 1-1 (Agora RTC)

- Tích hợp **Agora RTC SDK** cho truyền tải video/audio độ trễ thấp.
- **Backend**: Sinh Agora Dynamic Key (Token) với quyền join channel dựa trên `channelName`.
- **Luồng signal**: Sử dụng STOMP để gửi thông báo cuộc gọi đến (CallEvent).
- **Client**: Hiển thị màn hình cuộc gọi với các chức năng: tắt mic, đảo camera, kết thúc cuộc gọi.
- Hỗ trợ background call qua push notification nếu app đang đóng.

[image: tv4_ket_qua_video_call.png]
*Hình 3.5: Kết quả — Gọi video 1-1*

`[Ảnh chụp màn hình Video Call — placeholder]`

## 3.4 Kết quả thử nghiệm

| STT | Chức năng | Kịch bản test | Kết quả | Ghi chú |
|:---:|-----------|---------------|:-------:|---------|
| 1 | Gửi ảnh | Upload JPG 2MB | ✅ Đạt | S3 upload < 2s |
| 2 | Gửi ảnh | Upload PNG 5MB | ✅ Đạt | FileType = IMAGE |
| 3 | Gửi video | Upload MP4 10MB | ✅ Đạt | FileType = VIDEO |
| 4 | Gửi tài liệu | Upload PDF | ✅ Đạt | FileType = DOCUMENT |
| 5 | Gửi audio | Upload MP3 | ✅ Đạt | FileType = AUDIO |
| 6 | Gửi file khác | Upload ZIP | ✅ Đạt | FileType = RAW |
| 7 | Xem ảnh | Tap fullscreen | ✅ Đạt | PhotoView hoạt động |
| 8 | FCM register | Đăng ký token | ✅ Đạt | Token lưu DB |
| 9 | Push msg | Tin nhắn mới (bg) | ✅ Đạt | Notification < 3s |
| 10 | Push msg | Tin nhắn mới (fg) | ✅ Đạt | In-app notification |
| 11 | Push invite | Lời mời kết bạn | ✅ Đạt | Notification đúng |
| 12 | Push group | Thêm vào nhóm | ✅ Đạt | Group notification |
| 13 | Toggle notif | Tắt thông báo | ✅ Đạt | Không nhận push |
| 14 | Toggle notif | Bật lại thông báo | ✅ Đạt | Nhận push bình thường |
| 15 | AI create | Tạo conversation | ✅ Đạt | Conversation saved |
| 16 | AI stream | Chat streaming | ✅ Đạt | SSE chunks render |
| 17 | AI stream | Long response (2000 tokens) | ✅ Đạt | Stream < 10s |
| 18 | AI history | Xem lịch sử | ✅ Đạt | Messages load đúng |
| 19 | AI delete | Xóa conversation | ✅ Đạt | Cascade delete |
| 20 | AI markdown | Code block render | ✅ Đạt | Syntax highlight |
| 21 | Multi-device | Push to 2 devices | ✅ Đạt | Both receive |
| 22 | Token cleanup | Expired token | ✅ Đạt | Auto cleanup job |

*Bảng 3.2: Kết quả thử nghiệm chức năng Media, Thông báo & AI*

**Số liệu demo:**
- Số file media upload thử nghiệm: 50+
- Tổng dung lượng upload: ~200MB
- Số push notification gửi: 100+
- Thời gian push trung bình: < 2s
- Số conversation AI tạo: 15
- Số tin nhắn AI: 80+
- Thời gian phản hồi AI trung bình: < 5s


### Đánh giá kiến trúc

| Tiêu chí | Đánh giá | Chi tiết |
|----------|:--------:|---------|
| Upload ảnh | ✅ Đạt | S3 upload + STOMP broadcast < 2s |
| FileType classification | ✅ Đạt | 5 loại: IMAGE, VIDEO, DOCUMENT, AUDIO, RAW |
| Push notification | ✅ Đạt | FCM delivery < 2s |
| Notification preference | ✅ Đạt | Toggle ON/OFF chính xác |
| AI chatbot streaming | ✅ Đạt | SSE chunks render realtime |
| Chatbot conversation | ✅ Đạt | CRUD + history đầy đủ |
| Fallback model | ✅ Đạt | Auto-fallback khi rate limited |

*Bảng 3.3: Đánh giá kiến trúc module Media, Thông báo & AI*

### Phân tích hiệu năng

| Thao tác | Thời gian TB | Mục tiêu | Đạt? |
|----------|:-----------:|:--------:|:----:|
| Upload ảnh 2MB | ~1.2s | < 2s | ✅ |
| FCM push delivery | ~1.5s | < 3s | ✅ |
| AI chatbot first token | ~1s | < 3s | ✅ |
| AI chatbot full response | ~5s | < 10s | ✅ |

*Bảng 3.4: Phân tích hiệu năng module AI & Media*

## 3.5 Kết luận và hạn chế

### Kết luận

1. **Media Upload**: Pipeline upload hoạt động ổn định qua AttachmentService → S3Service → VersityGW. Hỗ trợ 5 loại file, phân loại tự động.
2. **Push Notification**: FCM integration hoạt động đáng tin cậy, hỗ trợ multi-device, kiểm tra preference trước khi gửi.
3. **AI Chatbot**: Streaming SSE qua SseEmitter hoạt động mượt mà, client render markdown realtime, hỗ trợ MCP.
4. **Kiến trúc**: Phân tách rõ ràng giữa AttachmentService (media), NotificationService (push), ChatbotService (AI) theo Single Responsibility.
5. **Storage**: VersityGW hoạt động ổn định với Caddy proxy, client truy cập media qua `/storage/*` transparent.

### Hạn chế

1. **File size limit**: Chưa có giới hạn file size upload phía server, có thể upload file rất lớn.
2. **Media compression**: Chưa tự động compress ảnh/video trước khi upload.
3. **Offline notification**: Chưa queue notification khi user offline quá lâu (FCM tự handle nhưng có TTL).
4. **AI model selection**: User chưa thể chọn model khác nhau trên UI (chỉ config server-side).
5. **MCP chưa hoàn thiện**: Model Context Protocol flag có nhưng implementation còn hạn chế.

### Hướng phát triển
- Thêm file size limit và media compression
- Thêm notification channel (mute per-conversation)
- Thêm UI chọn AI model (GPT-4o, Claude, Gemini)
- Hoàn thiện MCP integration
- Thêm voice message recording

## 3.6 Tài liệu tham khảo

1. Spring Boot Documentation — https://docs.spring.io/spring-boot/
2. Flutter Documentation — https://docs.flutter.dev/
3. PostgreSQL Documentation — https://www.postgresql.org/docs/
4. Redis Documentation — https://redis.io/docs/
5. Apache Artemis Documentation — https://activemq.apache.org/components/artemis/documentation/
6. Docker Compose Documentation — https://docs.docker.com/compose/
7. OpenAI API Reference — https://platform.openai.com/docs/api-reference
8. Firebase Cloud Messaging — https://firebase.google.com/docs/cloud-messaging
9. Agora RTC Engine SDK — https://docs.agora.io/en/
10. STOMP Protocol Specification — https://stomp.github.io/stomp-specification-1.2.html
11. JWT (RFC 7519) — https://datatracker.ietf.org/doc/html/rfc7519
12. Amazon S3 API Reference — https://docs.aws.amazon.com/AmazonS3/latest/API/
13. Caddy Server Documentation — https://caddyserver.com/docs/
14. Provider State Management — https://pub.dev/packages/provider
15. Server-Sent Events (SSE) — https://html.spec.whatwg.org/multipage/server-sent-events.html
