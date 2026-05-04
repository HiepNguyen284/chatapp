# BÁO CÁO ỨNG DỤNG NHẮN TIN THỜI GIAN THỰC - CHATAPP

> **Nhóm 4** | Môn học: Kiến trúc phần mềm
> Ngày: 04/05/2026

---

## MỤC LỤC

- [Chương 1: Mở đầu](#chương-1-mở-đầu)
- [Chương 2: Phân tích Thiết kế](#chương-2-phân-tích-thiết-kế)
- [Chương 3: Kết quả](#chương-3-kết-quả)

---

# CHƯƠNG 1: MỞ ĐẦU

## 1.1. Giới thiệu ứng dụng

**ChatApp** là một ứng dụng nhắn tin thời gian thực đa nền tảng, được xây dựng với kiến trúc Client-Server hiện đại. Ứng dụng cung cấp đầy đủ các tính năng giao tiếp trực tuyến bao gồm: nhắn tin văn bản, gửi file đa phương tiện, gọi video, chatbot AI, dịch thuật tin nhắn tự động và chuyển đổi giọng nói thành văn bản.

**Thông tin kỹ thuật tổng quan:**

| Thành phần | Công nghệ |
|---|---|
| Backend (Server) | Spring Boot 4.0.5, Kotlin/Java 21 |
| Frontend (Client) | Flutter 3.5+, Dart |
| Cơ sở dữ liệu | PostgreSQL 18 |
| Cache | Redis 8 |
| Message Broker | Apache Artemis 2.53 (STOMP) |
| Object Storage | Versity S3-compatible |
| Reverse Proxy | Caddy 2 |
| AI/LLM | OpenAI-compatible API |
| Speech-to-Text | Whisper (Python FastAPI) |
| Video Call | Agora RTC Engine |
| Push Notification | Firebase Cloud Messaging |
| Container | Docker Compose |

## 1.2. Lý do thực hiện

Trong bối cảnh chuyển đổi số hiện nay, nhu cầu giao tiếp trực tuyến ngày càng tăng cao. Các ứng dụng nhắn tin phổ biến như Zalo, Messenger, Telegram đã trở thành công cụ không thể thiếu trong cuộc sống hàng ngày. Tuy nhiên, việc nghiên cứu và phát triển một ứng dụng nhắn tin từ đầu mang lại nhiều giá trị:

1. **Học thuật**: Ứng dụng nhắn tin là bài toán tổng hợp nhiều kiến thức kiến trúc phần mềm: real-time communication (WebSocket), xác thực bảo mật (JWT/OAuth2), lưu trữ phân tán (S3), message queue (STOMP Broker), caching (Redis), và tích hợp AI.

2. **Thực tiễn**: Nắm vững quy trình phát triển full-stack từ thiết kế database, xây dựng RESTful API, đến phát triển giao diện đa nền tảng với Flutter.

3. **Xu hướng AI**: Tích hợp các tính năng AI hiện đại (chatbot, dịch thuật, tóm tắt, speech-to-text) vào ứng dụng truyền thống, tạo trải nghiệm người dùng vượt trội.

4. **Kiến trúc phần mềm**: Thực hành thiết kế hệ thống với kiến trúc microservice-ready, containerized deployment, và các design pattern phổ biến (Repository, Service Layer, Provider Pattern).

## 1.3. Concept ứng dụng

ChatApp được thiết kế theo concept **"All-in-One Messenger with AI"** — một nền tảng nhắn tin tích hợp trí tuệ nhân tạo, hướng tới:

- **Real-time First**: Mọi tương tác đều diễn ra theo thời gian thực thông qua WebSocket (STOMP protocol) với Apache Artemis làm message broker, đảm bảo tin nhắn được gửi và nhận tức thì.

- **AI-Powered**: Tích hợp sâu các tính năng AI:
  - **Chatbot AI**: Trợ lý ảo hỗ trợ người dùng với khả năng streaming response (SSE)
  - **Dịch thuật**: Dịch tin nhắn sang nhiều ngôn ngữ trong thời gian thực
  - **Tóm tắt**: Tóm tắt cuộc hội thoại dài bằng AI
  - **Speech-to-Text**: Chuyển đổi tin nhắn thoại thành văn bản qua Whisper

- **Cross-Platform**: Frontend Flutter cho phép chạy trên Android, iOS và Web từ một codebase duy nhất.

- **Security-First**: Bảo mật đa lớp với Argon2 password hashing, JWT access/refresh token, OAuth2 Resource Server, và WebSocket authentication.

- **Cloud-Native**: Toàn bộ hệ thống được containerize với Docker Compose, sẵn sàng triển khai trên bất kỳ cloud platform nào.

## 1.4. Phân tích yêu cầu

### 1.4.1. Yêu cầu chức năng

#### Nhóm 1: Quản lý tài khoản
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F01 | Đăng ký tài khoản | Tạo tài khoản mới với username và password |
| F02 | Đăng nhập | Xác thực và nhận JWT access/refresh token |
| F03 | Làm mới token | Tự động refresh access token khi hết hạn |
| F04 | Cập nhật hồ sơ | Thay đổi tên hiển thị, avatar |
| F05 | Đổi mật khẩu | Đổi mật khẩu khi đã đăng nhập |
| F06 | Quên mật khẩu | Reset mật khẩu qua email |

#### Nhóm 2: Nhắn tin
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F07 | Nhắn tin 1-1 | Gửi/nhận tin nhắn real-time giữa 2 người |
| F08 | Nhắn tin nhóm | Gửi/nhận tin nhắn trong nhóm chat |
| F09 | Gửi file đa phương tiện | Gửi ảnh, video, audio, tài liệu (PDF, DOC, XLS...) |
| F10 | Trả lời tin nhắn | Reply trực tiếp một tin nhắn cụ thể |
| F11 | Chỉnh sửa tin nhắn | Sửa nội dung tin nhắn đã gửi |
| F12 | Thu hồi tin nhắn | Thu hồi (recall) tin nhắn đã gửi |
| F13 | Trạng thái đang gõ | Hiển thị "đang nhập..." cho người đối diện |
| F14 | Đánh dấu đã đọc | Đánh dấu đã đọc tin nhắn trong phòng chat |

#### Nhóm 3: Quản lý nhóm chat
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F15 | Tạo nhóm | Tạo nhóm chat với tối thiểu 3 thành viên |
| F16 | Thêm thành viên | Admin thêm thành viên vào nhóm |
| F17 | Xóa thành viên | Admin xóa thành viên khỏi nhóm |
| F18 | Rời nhóm | Thành viên tự rời khỏi nhóm |
| F19 | Giải tán nhóm | Admin giải tán nhóm chat |
| F20 | Cập nhật thông tin nhóm | Đổi tên, avatar nhóm |

#### Nhóm 4: Quan hệ người dùng
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F21 | Tìm kiếm người dùng | Tìm kiếm theo username |
| F22 | Gửi lời mời kết bạn | Gửi invitation cho người dùng khác |
| F23 | Chấp nhận/Từ chối lời mời | Xử lý lời mời kết bạn |
| F24 | Mời vào nhóm | Gửi lời mời tham gia nhóm chat |
| F25 | Chặn/Bỏ chặn | Chặn người dùng không mong muốn |
| F26 | Trạng thái online | Hiển thị trạng thái trực tuyến/ngoại tuyến |

#### Nhóm 5: Tính năng AI
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F27 | Chatbot AI | Trò chuyện với chatbot AI, hỗ trợ streaming |
| F28 | Dịch thuật tin nhắn | Dịch tin nhắn sang ngôn ngữ khác |
| F29 | Tóm tắt hội thoại | Tóm tắt nội dung cuộc hội thoại |
| F30 | Speech-to-Text | Chuyển đổi ghi âm thành văn bản |

#### Nhóm 6: Đa phương tiện & Thông báo
| STT | Chức năng | Mô tả |
|-----|-----------|-------|
| F31 | Gọi video | Video call 1-1 và nhóm qua Agora RTC |
| F32 | Push notification | Thông báo đẩy qua Firebase Cloud Messaging |
| F33 | Thông báo cục bộ | Thông báo trong ứng dụng |
| F34 | Cài đặt thông báo | Bật/tắt push notification |
| F35 | Ghim phòng chat | Ghim phòng chat yêu thích lên đầu |

### 1.4.2. Yêu cầu phi chức năng

| STT | Yêu cầu | Mô tả |
|-----|---------|-------|
| NF01 | Hiệu năng | Tin nhắn gửi/nhận trong < 500ms trên mạng ổn định |
| NF02 | Bảo mật | Mã hóa mật khẩu Argon2, JWT token có thời hạn (access: 30 phút, refresh: 30 ngày) |
| NF03 | Khả năng mở rộng | Kiến trúc container hóa, dễ dàng scale horizontal |
| NF04 | Đa nền tảng | Hỗ trợ Android, iOS, Web từ cùng một codebase Flutter |
| NF05 | Khả năng phục hồi | Auto-reconnect WebSocket với token refresh, graceful degradation |
| NF06 | Kích thước file | Hỗ trợ upload file tối đa 100MB, audio tối đa 12MB |
| NF07 | Tương thích | API RESTful chuẩn, tài liệu Swagger/OpenAPI tự động |

---

# CHƯƠNG 2: PHÂN TÍCH THIẾT KẾ

## 2.1. Kiến trúc tổng quan

Ứng dụng ChatApp được thiết kế theo mô hình **Client-Server** với kiến trúc phân lớp (Layered Architecture) phía server và mô hình **Provider Pattern** phía client.

### 2.1.1. Sơ đồ kiến trúc tổng quan

```mermaid
graph TB
    subgraph Client ["📱 Client Layer"]
        FL["Flutter App<br/>(Android / iOS / Web)"]
    end

    subgraph Gateway ["🌐 API Gateway"]
        CA["Caddy Reverse Proxy<br/>Port 8080"]
    end

    subgraph Server ["⚙️ Application Server"]
        SB["Spring Boot 4.0<br/>Kotlin/Java 21"]
        subgraph Layers ["Layered Architecture"]
            CT["Controllers<br/>(REST API)"]
            SV["Services<br/>(Business Logic)"]
            RP["Repositories<br/>(Data Access)"]
        end
        WS["WebSocket/STOMP<br/>Endpoint"]
        AI["AI Services<br/>(Translation, Summary, Chatbot)"]
    end

    subgraph Data ["💾 Data Layer"]
        PG["PostgreSQL 18<br/>(Primary Database)"]
        RD["Redis 8<br/>(Cache & Sessions)"]
        S3["Versity S3<br/>(File Storage)"]
    end

    subgraph Messaging ["📨 Message Broker"]
        AR["Apache Artemis 2.53<br/>(STOMP Relay)"]
    end

    subgraph External ["☁️ External Services"]
        FB["Firebase Cloud Messaging"]
        AG["Agora RTC<br/>(Video Call)"]
        LLM["OpenAI-compatible API<br/>(LLM)"]
        WH["Whisper Service<br/>(Speech-to-Text)"]
        SM["SMTP Server<br/>(Email)"]
    end

    FL -->|"HTTP/REST"| CA
    FL -->|"WebSocket/STOMP"| CA
    FL -->|"Agora SDK"| AG
    FL -->|"FCM SDK"| FB

    CA -->|"/api/*"| CT
    CA -->|"/socket/*"| WS
    CA -->|"/storage/*"| S3

    CT --> SV
    SV --> RP
    SV --> AI
    WS --> AR

    RP --> PG
    SV --> RD
    SV --> S3
    SV --> FB
    SV --> SM
    AI --> LLM
    AI --> WH
    AR --> WS
```

### 2.1.2. Kiến trúc phía Server (Backend)

Server được xây dựng theo mô hình **3-Layer Architecture**:

| Tầng | Thành phần | Chức năng |
|------|-----------|-----------|
| **Presentation** | 11 REST Controllers | Tiếp nhận HTTP request, validation, trả response |
| **Business Logic** | 24+ Services | Xử lý logic nghiệp vụ, tích hợp AI |
| **Data Access** | 12 JPA Repositories | Truy vấn và thao tác với PostgreSQL |

**Danh sách Controllers:**
- `UserController` — Quản lý tài khoản, xác thực, FCM token
- `MessageController` — CRUD tin nhắn, dịch thuật, tóm tắt
- `ChatRoomController` — Quản lý phòng chat
- `GroupChatController` — Quản lý nhóm chat
- `InvitationController` — Lời mời kết bạn/nhóm
- `ChatbotController` — Chatbot AI với SSE streaming
- `SpeechToTextController` — Chuyển giọng nói thành văn bản
- `PresenceWebSocketController` — Trạng thái online/offline
- `HealthController` — Health check endpoint
- `ExceptionController` — Xử lý ngoại lệ toàn cục

### 2.1.3. Kiến trúc phía Client (Frontend)

Client Flutter sử dụng **Provider Pattern** cho state management:

```mermaid
graph TB
    subgraph UI ["🎨 UI Layer (Screens)"]
        LS["LoginScreen"]
        RS["RegisterScreen"]
        HS["HomeScreen"]
        CLS["ChatListScreen"]
        CS["ChatScreen"]
        ACS["AIChatScreen"]
        CBS["ChatbotScreen"]
        VCS["VideoCallScreen"]
        PS["ProfileScreen"]
        SS["SettingsScreen"]
        IS["InvitationsScreen"]
        AFS["AddFriendScreen"]
        CGS["CreateGroupScreen"]
        GMS["GroupMembersScreen"]
        PLS["PeopleScreen"]
    end

    subgraph State ["📦 State Management (Providers)"]
        AP["AuthProvider"]
        CP["ChatProvider"]
        CRP["ChatRoomsProvider"]
        CBP["ChatbotProvider"]
        IP["InvitationProvider"]
        USP["UserSearchProvider"]
        VCP["VideoCallProvider"]
    end

    subgraph Services ["🔧 Service Layer"]
        API["ApiClient<br/>(HTTP + Token Refresh)"]
        AS["AuthService"]
        MS["MessageService"]
        CRS["ChatRoomService"]
        GCS["GroupChatService"]
        CBS2["ChatbotService"]
        INS["InvitationService"]
        US["UserService"]
        RTS["RealtimeService<br/>(WebSocket/STOMP)"]
        AGS["AgoraService"]
        FMS["FirebaseMessagingService"]
        LNS["LocalNotificationService"]
    end

    UI --> State
    State --> Services
    Services --> API
    RTS -->|"STOMP"| API
```

## 2.2. Biểu đồ Use Case tổng quan

```mermaid
graph LR
    User(("👤 Người dùng"))

    subgraph ChatApp ["Hệ thống ChatApp"]
        UC1["Quản lý tài khoản"]
        UC2["Nhắn tin"]
        UC3["Quản lý nhóm"]
        UC4["Quan hệ người dùng"]
        UC5["Tính năng AI"]
        UC6["Gọi video"]
        UC7["Thông báo"]
    end

    AI(("🤖 Chatbot AI"))
    Agora(("📹 Agora RTC"))
    Firebase(("🔔 Firebase"))

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7

    UC5 --> AI
    UC6 --> Agora
    UC7 --> Firebase
```

## 2.3. Biểu đồ Use Case chi tiết

### 2.3.1. Module Quản lý tài khoản

```mermaid
graph LR
    User(("👤 Người dùng"))

    subgraph Auth ["Quản lý tài khoản"]
        A1["Đăng ký"]
        A2["Đăng nhập"]
        A3["Làm mới token"]
        A4["Xem hồ sơ cá nhân"]
        A5["Cập nhật hồ sơ"]
        A6["Đổi mật khẩu"]
        A7["Quên mật khẩu"]
        A8["Đăng xuất"]
    end

    SMTP(("📧 SMTP Server"))

    User --> A1
    User --> A2
    User --> A3
    User --> A4
    User --> A5
    User --> A6
    User --> A7
    User --> A8

    A2 -.->|"include"| A3
    A7 -.->|"include"| SMTP
```

### 2.3.2. Module Nhắn tin

```mermaid
graph LR
    User(("👤 Người dùng"))

    subgraph Msg ["Nhắn tin"]
        M1["Gửi tin nhắn văn bản"]
        M2["Gửi file đa phương tiện"]
        M3["Trả lời tin nhắn"]
        M4["Chỉnh sửa tin nhắn"]
        M5["Thu hồi tin nhắn"]
        M6["Xem lịch sử tin nhắn"]
        M7["Đánh dấu đã đọc"]
        M8["Gửi trạng thái đang gõ"]
        M9["Dịch thuật tin nhắn"]
        M10["Tóm tắt hội thoại"]
        M11["Speech-to-Text"]
    end

    S3(("💾 S3 Storage"))
    LLM(("🤖 LLM API"))
    Whisper(("🎤 Whisper"))

    User --> M1
    User --> M2
    User --> M3
    User --> M4
    User --> M5
    User --> M6
    User --> M7
    User --> M8
    User --> M9
    User --> M10
    User --> M11

    M2 -.->|"include"| S3
    M9 -.->|"include"| LLM
    M10 -.->|"include"| LLM
    M11 -.->|"include"| Whisper
```

### 2.3.3. Module Quản lý nhóm

```mermaid
graph LR
    Admin(("👤 Admin nhóm"))
    Member(("👤 Thành viên"))

    subgraph Group ["Quản lý nhóm chat"]
        G1["Tạo nhóm"]
        G2["Cập nhật thông tin nhóm"]
        G3["Thêm thành viên"]
        G4["Xóa thành viên"]
        G5["Rời nhóm"]
        G6["Giải tán nhóm"]
        G7["Xem danh sách thành viên"]
        G8["Mời vào nhóm"]
    end

    Admin --> G1
    Admin --> G2
    Admin --> G3
    Admin --> G4
    Admin --> G6
    Admin --> G7
    Admin --> G8
    Member --> G5
    Member --> G7
```

### 2.3.4. Module Tính năng AI

```mermaid
graph LR
    User(("👤 Người dùng"))

    subgraph AIFeatures ["Tính năng AI"]
        AI1["Tạo cuộc trò chuyện AI"]
        AI2["Chat với Chatbot"]
        AI3["Xem lịch sử chat AI"]
        AI4["Xóa cuộc trò chuyện AI"]
        AI5["Dịch tin nhắn"]
        AI6["Tóm tắt hội thoại"]
    end

    LLM(("🤖 OpenAI API"))

    User --> AI1
    User --> AI2
    User --> AI3
    User --> AI4
    User --> AI5
    User --> AI6

    AI2 -.->|"SSE Streaming"| LLM
    AI5 -.->|"include"| LLM
    AI6 -.->|"include"| LLM
```

### 2.3.5. Module Video Call & Thông báo

```mermaid
graph LR
    User(("👤 Người dùng"))

    subgraph VideoCall ["Gọi video"]
        V1["Tham gia cuộc gọi"]
        V2["Bật/tắt camera"]
        V3["Bật/tắt microphone"]
        V4["Chuyển camera trước/sau"]
        V5["Kết thúc cuộc gọi"]
    end

    subgraph Notify ["Thông báo"]
        N1["Nhận push notification"]
        N2["Nhận thông báo cục bộ"]
        N3["Cài đặt thông báo"]
    end

    Agora(("📹 Agora"))
    FCM(("🔔 Firebase"))

    User --> V1
    User --> V2
    User --> V3
    User --> V4
    User --> V5
    User --> N1
    User --> N2
    User --> N3

    V1 -.->|"include"| Agora
    N1 -.->|"include"| FCM
```

## 2.4. Biểu đồ lớp (Class Diagram)

### 2.4.1. Biểu đồ lớp — Entity Models (Server)

```mermaid
classDiagram
    class User {
        -Long id
        -String username
        -String password
        -String displayName
        -Attachment avatar
        +getAuthorities() Collection
        +equals(Object) boolean
    }

    class ChatRoom {
        -Long id
        -String name
        -Attachment avatar
        -Set~User~ members
        -List~ChatMessage~ messages
        -Type type
        -Long creatorId
        -Timestamp createdOn
        +getSocketPath() String
        +isChatGroup() boolean
    }

    class ChatMessage {
        -Long id
        -User sender
        -ChatRoom room
        -ChatMessage replyTo
        -String message
        -Timestamp lastEdit
        -Timestamp sentOn
        -List~Attachment~ attachments
        -Status status
        +isRecalled() boolean
    }

    class ChatRoomMember {
        -Long id
        -ChatRoom chatRoom
        -User user
        -boolean isAdmin
        -Timestamp joinedAt
        +createMember(ChatRoom, User, boolean) ChatRoomMember
    }

    class Invitation {
        -Long id
        -User sender
        -User receiver
        -ChatRoom chatRoom
        -Status status
        +isFriendRequest() boolean
        +isPending() boolean
        +isAccepted() boolean
    }

    class Attachment {
        -Long id
        -String source
        -FileType type
        +isImage() boolean
        +isDocumentFormat(String) boolean
        +isAudioFormat(String) boolean
    }

    class UserBlock {
        -Long id
        -User blocker
        -User blocked
    }

    class FcmToken {
        -Long id
        -User user
        -String token
        -Timestamp createdAt
        -Timestamp lastUsed
    }

    class ChatRoomPin {
        -Long id
        -User user
        -ChatRoom chatRoom
        -Timestamp pinnedOn
    }

    class ChatRoomReadState {
        -Long id
        -ChatRoom room
        -User reader
        -Timestamp lastReadAt
    }

    class ChatbotConversation {
        -Long id
        -User owner
        -String title
        -String modelName
        -boolean mcpEnabled
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

    class ChatRoomType {
        <<enumeration>>
        DUO
        GROUP
    }

    class MessageStatus {
        <<enumeration>>
        NORMAL
        EDITED
        RECALLED
    }

    class InvitationStatus {
        <<enumeration>>
        PENDING
        ACCEPTED
        REJECTED
    }

    class FileType {
        <<enumeration>>
        IMAGE
        VIDEO
        RAW
        DOCUMENT
        AUDIO
    }

    class ChatbotRole {
        <<enumeration>>
        USER
        ASSISTANT
        SYSTEM
        TOOL
    }

    User "1" --> "0..*" ChatRoom : members
    User "1" --> "0..1" Attachment : avatar
    ChatRoom "1" --> "0..*" ChatMessage : messages
    ChatRoom "1" --> "0..1" Attachment : avatar
    ChatMessage "0..*" --> "1" User : sender
    ChatMessage "0..*" --> "1" ChatRoom : room
    ChatMessage "0..1" --> "0..1" ChatMessage : replyTo
    ChatMessage "1" --> "0..*" Attachment : attachments
    ChatRoomMember "0..*" --> "1" ChatRoom : chatRoom
    ChatRoomMember "0..*" --> "1" User : user
    Invitation "0..*" --> "1" User : sender
    Invitation "0..*" --> "1" User : receiver
    Invitation "0..*" --> "0..1" ChatRoom : chatRoom
    UserBlock "0..*" --> "1" User : blocker
    UserBlock "0..*" --> "1" User : blocked
    FcmToken "0..*" --> "1" User : user
    ChatRoomPin "0..*" --> "1" User : user
    ChatRoomPin "0..*" --> "1" ChatRoom : chatRoom
    ChatRoomReadState "0..*" --> "1" ChatRoom : room
    ChatRoomReadState "0..*" --> "1" User : reader
    ChatbotConversation "0..*" --> "1" User : owner
    ChatbotMessage "0..*" --> "1" ChatbotConversation : conversation

    ChatRoom --> ChatRoomType
    ChatMessage --> MessageStatus
    Invitation --> InvitationStatus
    Attachment --> FileType
    ChatbotMessage --> ChatbotRole
```

### 2.4.2. Biểu đồ lớp — Service Layer (Server)

```mermaid
classDiagram
    class UserService {
        +createUser(UserDto) void
        +searchUser(String, int) List
        +getCurrentProfile() UserWithAvatarDto
        +updateCurrentProfile(UserProfileUpdateDto) UserWithAvatarDto
        +changePassword(String, String) void
        +requestPasswordReset(String) void
        +resetPassword(String, String) void
    }

    class JwtsService {
        +tokenObtainPair(UserDto) TokenObtainPairDto
        +refreshToken(String) TokenRefreshDto
        +revokeRefreshToken(String) void
    }

    class MessageService {
        +getMessages(long, int) List
        +sendMessage(long, MessageSendDto) MessageSendResponseDto
        +changeMessage(long, MessageSendDto) void
        +deleteMessage(long) void
        +setTypingStatus(long, boolean) void
        +setReadStatus(long) void
    }

    class ChatRoomService {
        +listRooms() List
        +getOrCreateDuoRoom(long) ChatRoom
    }

    class GroupChatService {
        +createGroup(GroupChatCreateDto) GroupChatDto
        +updateGroup(long, GroupChatUpdateDto) GroupChatDto
        +addMembers(long, List) GroupChatDto
        +removeMember(long, long) void
        +leaveGroup(long) void
        +dissolveGroup(long) void
    }

    class ChatbotService {
        +listConversations() List
        +createConversation(ChatbotConversationCreateDto) ChatbotConversationDto
        +listMessages(long) List
        +deleteConversation(long) void
        +streamReply(long, ChatbotStreamRequestDto) SseEmitter
    }

    class TranslationService {
        +translate(MessageTranslateRequestDto) MessageTranslationDto
    }

    class SummaryService {
        +summarize(MessageSummarizeRequestDto) MessageSummaryDto
    }

    class NotificationService {
        +sendPushNotification(User, String, String) void
    }

    class S3Service {
        +uploadFile(MultipartFile) String
        +deleteFile(String) void
    }

    class PresenceService {
        +getPresence(String) UserPresenceDto
        +setPresence(String, boolean) void
    }

    MessageService --> ChatRoomService
    MessageService --> NotificationService
    MessageService --> S3Service
    GroupChatService --> ChatRoomService
    GroupChatService --> NotificationService
    ChatbotService --> TranslationService
    UserService --> S3Service
```

## 2.5. Biểu đồ tuần tự (Sequence Diagram)

### 2.5.1. Luồng Đăng nhập (Login Flow)

```mermaid
sequenceDiagram
    actor User as Người dùng
    participant App as Flutter App
    participant Auth as AuthProvider
    participant API as ApiClient
    participant Server as Spring Boot
    participant DB as PostgreSQL
    participant WS as WebSocket
    participant FCM as Firebase

    User->>App: Nhập username/password
    App->>Auth: login(username, password)
    Auth->>API: POST /api/v1/users/token/
    API->>Server: HTTP Request
    Server->>DB: Tìm user theo username
    DB-->>Server: User entity
    Server->>Server: Verify password (Argon2)
    Server->>Server: Tạo JWT access + refresh token
    Server-->>API: TokenObtainPairDto
    API-->>Auth: Token pair
    Auth->>Auth: Lưu tokens (SharedPreferences)
    Auth->>WS: connect() - Kết nối WebSocket
    WS->>Server: STOMP CONNECT + Bearer token
    Server-->>WS: CONNECTED
    Auth->>API: GET /api/v1/users/me/
    API-->>Auth: UserProfile
    Auth->>FCM: initialize() - Đăng ký FCM token
    FCM->>API: POST /api/v1/users/fcm-token/
    Auth-->>App: Login thành công
    App-->>User: Chuyển đến HomeScreen
```

### 2.5.2. Luồng Gửi tin nhắn (Send Message Flow)

```mermaid
sequenceDiagram
    actor UserA as Người gửi
    participant AppA as Flutter App A
    participant API as ApiClient
    participant Server as Spring Boot
    participant S3 as Versity S3
    participant DB as PostgreSQL
    participant Artemis as Apache Artemis
    participant AppB as Flutter App B
    actor UserB as Người nhận
    participant FCM as Firebase FCM

    UserA->>AppA: Nhập tin nhắn + đính kèm file
    AppA->>API: POST /api/v1/messages/?room=1 (multipart)
    API->>Server: HTTP Multipart Request

    alt Có file đính kèm
        Server->>S3: Upload file
        S3-->>Server: File URL
        Server->>DB: Lưu Attachment entity
    end

    Server->>DB: Lưu ChatMessage entity
    Server->>Artemis: Publish tin nhắn qua STOMP
    Server->>FCM: Gửi push notification
    Server-->>API: MessageSendResponseDto
    API-->>AppA: Thành công

    Artemis-->>AppB: STOMP message (real-time)
    AppB-->>UserB: Hiển thị tin nhắn mới

    FCM-->>AppB: Push notification (nếu app ở background)
```

### 2.5.3. Luồng Chatbot AI (AI Chat Flow)

```mermaid
sequenceDiagram
    actor User as Người dùng
    participant App as Flutter App
    participant API as ApiClient
    participant Server as Spring Boot
    participant DB as PostgreSQL
    participant LLM as OpenAI API

    User->>App: Nhập câu hỏi cho AI
    App->>API: POST /api/v1/chatbot/conversations/{id}/stream
    API->>Server: HTTP Request (Accept: text/event-stream)
    Server->>DB: Lưu user message
    Server->>DB: Lấy lịch sử hội thoại
    Server->>LLM: Gửi prompt + history (streaming)

    loop SSE Streaming
        LLM-->>Server: Token chunk
        Server-->>API: SSE event (data: chunk)
        API-->>App: Stream data
        App-->>User: Hiển thị từng token
    end

    Server->>DB: Lưu assistant message hoàn chỉnh
    Server-->>API: SSE event (done)
    App-->>User: Hiển thị hoàn tất
```

### 2.5.4. Luồng Video Call

```mermaid
sequenceDiagram
    actor UserA as Người gọi
    participant AppA as Flutter App A
    participant Agora as Agora RTC Server
    participant AppB as Flutter App B
    actor UserB as Người nhận

    UserA->>AppA: Nhấn "Gọi video"
    AppA->>AppA: Yêu cầu quyền Camera + Mic
    AppA->>AppA: Khởi tạo AgoraService
    AppA->>Agora: joinChannel(channelName)
    Agora-->>AppA: onJoinChannelSuccess

    UserB->>AppB: Nhấn "Tham gia"
    AppB->>AppB: Yêu cầu quyền Camera + Mic
    AppB->>Agora: joinChannel(channelName)
    Agora-->>AppB: onJoinChannelSuccess
    Agora-->>AppA: onUserJoined(UserB)

    loop Video Call Active
        AppA->>Agora: Stream video/audio
        Agora-->>AppB: Forward stream
        AppB->>Agora: Stream video/audio
        Agora-->>AppA: Forward stream
    end

    UserA->>AppA: Nhấn "Kết thúc"
    AppA->>Agora: leaveChannel()
    Agora-->>AppB: onUserOffline(UserA)
```

## 2.6. Sơ đồ thực thể quan hệ (ER Diagram)

```mermaid
erDiagram
    users {
        bigint id PK
        varchar username UK "NOT NULL"
        varchar password "NOT NULL"
        varchar display_name
        bigint avatar_id FK
    }

    chat_room {
        bigint id PK
        varchar name
        bigint avatar_id FK
        int type "NOT NULL (0=DUO, 1=GROUP)"
        bigint creator_id
        timestamp created_on
    }

    chat_message {
        bigint id PK
        bigint sender_id FK "NOT NULL"
        bigint room_id FK "NOT NULL"
        bigint reply_to_id FK
        text message
        timestamp last_edit
        timestamp sent_on
        int status "NOT NULL (0=NORMAL, 1=EDITED, 2=RECALLED)"
    }

    chat_room_members {
        bigint chat_room_id FK
        bigint members_id FK
    }

    chat_room_member_roles {
        bigint id PK
        bigint chat_room_id FK "NOT NULL"
        bigint user_id FK "NOT NULL"
        boolean is_admin "DEFAULT false"
        timestamp joined_at "NOT NULL"
    }

    chat_room_pins {
        bigint id PK
        bigint user_id FK "NOT NULL"
        bigint chat_room_id FK "NOT NULL"
        timestamp pinned_on
    }

    chat_room_read_states {
        bigint id PK
        bigint room_id FK "NOT NULL"
        bigint reader_id FK "NOT NULL"
        timestamp last_read_at "NOT NULL"
    }

    attachment {
        bigint id PK
        varchar source "NOT NULL"
        int type "NOT NULL (0=IMAGE, 1=VIDEO, 2=RAW, 3=DOCUMENT, 4=AUDIO)"
    }

    chat_message_attachments {
        bigint chat_message_id FK
        bigint attachments_id FK
    }

    invitation {
        bigint id PK
        bigint sender_id FK "NOT NULL"
        bigint receiver_id FK "NOT NULL"
        bigint chat_room_id FK
        int status "NOT NULL (0=PENDING, 1=ACCEPTED, 2=REJECTED)"
    }

    user_blocks {
        bigint id PK
        bigint blocker_id FK "NOT NULL"
        bigint blocked_id FK "NOT NULL"
    }

    fcm_tokens {
        bigint id PK
        bigint user_id FK "NOT NULL"
        varchar token "NOT NULL, max 500"
        timestamp created_at
        timestamp last_used
    }

    chatbot_conversations {
        bigint id PK
        bigint owner_id FK "NOT NULL"
        varchar title "NOT NULL"
        varchar model_name "NOT NULL"
        boolean mcp_enabled "NOT NULL"
        varchar mcp_session_id
        text mcp_metadata
        timestamp created_on
        timestamp updated_on
    }

    chatbot_messages {
        bigint id PK
        bigint conversation_id FK "NOT NULL"
        int role "NOT NULL (0=USER, 1=ASSISTANT, 2=SYSTEM, 3=TOOL)"
        text content "NOT NULL"
        text metadata_json
        timestamp created_on
    }

    users ||--o| attachment : "avatar"
    chat_room ||--o| attachment : "avatar"
    chat_room ||--o{ chat_message : "messages"
    chat_room ||--o{ chat_room_members : "members"
    chat_room ||--o{ chat_room_member_roles : "member_roles"
    chat_room ||--o{ chat_room_pins : "pins"
    chat_room ||--o{ chat_room_read_states : "read_states"
    users ||--o{ chat_room_members : "member_of"
    users ||--o{ chat_room_member_roles : "roles"
    users ||--o{ chat_message : "sender"
    users ||--o{ chat_room_pins : "pinned_rooms"
    users ||--o{ chat_room_read_states : "read_states"
    chat_message ||--o{ chat_message_attachments : "attachments"
    attachment ||--o{ chat_message_attachments : "attached_to"
    chat_message ||--o| chat_message : "reply_to"
    users ||--o{ invitation : "sent_invitations"
    users ||--o{ invitation : "received_invitations"
    invitation ||--o| chat_room : "group_invitation"
    users ||--o{ user_blocks : "blocker"
    users ||--o{ user_blocks : "blocked"
    users ||--o{ fcm_tokens : "devices"
    users ||--o{ chatbot_conversations : "ai_chats"
    chatbot_conversations ||--o{ chatbot_messages : "messages"
```

## 2.7. Giao diện đáp ứng chức năng và luồng

Ứng dụng ChatApp Flutter sử dụng **Dark Theme** với tông màu xanh dương (`#3B82F6`) làm chủ đạo. Dưới đây là mô tả chi tiết các màn hình chính:

### 2.7.1. Màn hình Đăng nhập (LoginScreen)

- **Chức năng**: F02 (Đăng nhập)
- **Mô tả**: Form đăng nhập với 2 trường nhập (username, password), nút "Đăng nhập" và liên kết "Đăng ký tài khoản mới". Hiển thị loading indicator và thông báo lỗi thân thiện bằng tiếng Việt khi không kết nối được server.
- **Luồng**: Nhập thông tin → Nhấn đăng nhập → Validate → Gọi API → Lưu token → Kết nối WebSocket → Chuyển đến HomeScreen.

### 2.7.2. Màn hình Đăng ký (RegisterScreen)

- **Chức năng**: F01 (Đăng ký tài khoản)
- **Mô tả**: Form đăng ký với các trường username, password, xác nhận password. Validate ở client-side trước khi gọi API.
- **Luồng**: Nhập thông tin → Validate → Gọi API đăng ký → Thông báo thành công → Quay về màn hình đăng nhập.

### 2.7.3. Màn hình Chính (HomeScreen)

- **Chức năng**: Navigation hub
- **Mô tả**: Bottom navigation bar với 4 tab: Chat (danh sách phòng chat), Danh bạ (People), AI Chat, Cài đặt. Badge hiển thị số tin nhắn chưa đọc trên tab Chat.
- **Luồng**: Người dùng chuyển giữa các tab, mỗi tab là một màn hình con độc lập.

### 2.7.4. Màn hình Danh sách Chat (ChatListScreen)

- **Chức năng**: F07, F08, F35 (Nhắn tin, Ghim phòng)
- **Mô tả**: Danh sách phòng chat được sắp xếp theo tin nhắn mới nhất. Mỗi phòng hiển thị: avatar, tên, tin nhắn cuối, thời gian, badge chưa đọc. Phòng chat đã ghim nằm trên cùng. Nút floating action để tạo nhóm mới hoặc thêm bạn.
- **Luồng**: Nhấn vào phòng → Mở ChatScreen | Vuốt để ghim/bỏ ghim | Nhấn FAB → Tạo nhóm/Thêm bạn.

### 2.7.5. Màn hình Nhắn tin (ChatScreen)

- **Chức năng**: F07-F14, F28-F30 (Nhắn tin, AI features)
- **Mô tả**: Màn hình chính của ứng dụng. Gồm: AppBar (tên phòng, avatar, trạng thái online), vùng hiển thị tin nhắn (message bubbles với phân biệt gửi/nhận), thanh nhập tin nhắn (text input, nút gửi file, ghi âm). Hỗ trợ: reply tin nhắn (swipe), long-press để chỉnh sửa/thu hồi/dịch/tóm tắt, hiển thị "đang nhập...", attachment preview (ảnh, video, audio player, document).
- **Luồng**: Nhập tin nhắn → Gửi (REST API) → Broadcast qua WebSocket → Render real-time cho tất cả thành viên.

### 2.7.6. Màn hình Chatbot AI (ChatbotScreen)

- **Chức năng**: F27 (Chatbot AI)
- **Mô tả**: Giao diện chat riêng với AI. Sidebar hiển thị danh sách conversations. Tin nhắn AI hỗ trợ Markdown rendering. Response được stream real-time (SSE) với typing animation.
- **Luồng**: Tạo conversation → Nhập câu hỏi → Stream response từ LLM → Render Markdown → Lưu lịch sử.

### 2.7.7. Màn hình Video Call (VideoCallScreen)

- **Chức năng**: F31 (Gọi video)
- **Mô tả**: Grid layout hiển thị video (1-4 người tham gia). Toolbar ở dưới: Mute/Unmute, Camera On/Off, Switch Camera, End Call. Status bar hiển thị trạng thái kết nối và số người tham gia.
- **Luồng**: Nhập channel name → Yêu cầu quyền → Join Agora channel → Stream video/audio → End call.

### 2.7.8. Màn hình Cài đặt (SettingsScreen)

- **Chức năng**: F04, F05, F34 (Cập nhật profile, Đổi MK, Cài đặt thông báo)
- **Mô tả**: Hiển thị avatar và tên người dùng ở trên cùng. Danh sách cài đặt: Chỉnh sửa hồ sơ, Đổi mật khẩu, Danh sách bị chặn, Cài đặt thông báo, Cài đặt dịch thuật, Đăng xuất.

### 2.7.9. Các màn hình phụ trợ

| Màn hình | Chức năng | Mô tả ngắn |
|----------|-----------|-------------|
| AddFriendScreen | F21, F22 | Tìm kiếm user và gửi lời mời kết bạn |
| InvitationsScreen | F23, F24 | Xem và xử lý lời mời kết bạn/nhóm |
| CreateGroupScreen | F15 | Chọn thành viên và tạo nhóm chat mới |
| GroupMembersScreen | F16-F20 | Quản lý thành viên nhóm (admin features) |
| PeopleScreen | F26 | Danh sách bạn bè với trạng thái online |
| ProfileScreen | F04 | Xem/chỉnh sửa hồ sơ cá nhân |
| ChangePasswordScreen | F05 | Form đổi mật khẩu |


---

# CHƯƠNG 3: KẾT QUẢ

## 3.1. Mô hình triển khai ứng dụng

Ứng dụng ChatApp được triển khai theo mô hình **Docker Compose** với 6 container:

```mermaid
graph TB
    subgraph DockerCompose ["Docker Compose Deployment"]
        subgraph Gateway ["Container: chatapp-gateway"]
            Caddy["Caddy 2 Alpine<br/>Port: 8080 (exposed)"]
        end

        subgraph App ["Container: chatapp-app"]
            SpringBoot["Spring Boot 4.0<br/>Java 21 (jlink custom JRE)<br/>Port: 8080 (internal)"]
        end

        subgraph DB ["Container: chatapp-postgres"]
            Postgres["PostgreSQL 18 Alpine<br/>Port: 5432 (internal)"]
        end

        subgraph Cache ["Container: chatapp-redis"]
            Redis["Redis 8 Alpine<br/>Port: 6379 (internal)"]
        end

        subgraph MQ ["Container: chatapp-artemis"]
            Artemis["Apache Artemis 2.53<br/>Port: 61613 (STOMP)"]
        end

        subgraph Storage ["Container: chatapp-versitygw"]
            Versity["Versity S3 Gateway<br/>Port: 9000 (internal)"]
        end
    end

    subgraph Client ["Client Devices"]
        Android["📱 Android App"]
        iOS["📱 iOS App"]
        Web["🌐 Web App"]
    end

    subgraph External ["External Services"]
        Firebase["🔔 Firebase"]
        AgoraCloud["📹 Agora"]
        LLM["🤖 LLM API"]
    end

    Client -->|":8080"| Caddy
    Caddy -->|"/api/*, /socket/*"| SpringBoot
    Caddy -->|"/storage/*"| Versity
    SpringBoot --> Postgres
    SpringBoot --> Redis
    SpringBoot --> Artemis
    SpringBoot --> Versity
    SpringBoot --> Firebase
    SpringBoot --> LLM
    Client --> AgoraCloud
```

**Chi tiết routing của Caddy Gateway:**

| Path | Đích | Mô tả |
|------|------|-------|
| `/api/*` | `app:8080` | RESTful API endpoints |
| `/ws*` | `app:8080` | WebSocket native endpoint |
| `/socket*` | `app:8080` | SockJS fallback endpoint |
| `/storage/*` | `versitygw:9000` | S3 file storage (strip prefix) |
| `/*` (default) | `app:8080` | Các request khác |

## 3.2. Các bước cài đặt và triển khai ứng dụng

### 3.2.1. Yêu cầu hệ thống

| Thành phần | Yêu cầu |
|---|---|
| Docker | Docker Engine 24+ với Docker Compose v2 |
| RAM Server | Tối thiểu 4GB (khuyến nghị 8GB) |
| Disk | Tối thiểu 10GB cho dữ liệu |
| Flutter SDK | 3.5+ (cho phát triển client) |
| Java JDK | 21+ (cho phát triển server) |
| Android SDK | API Level 31+ (cho build Android) |

### 3.2.2. Triển khai Backend (Server)

**Bước 1: Clone repository**
```bash
git clone <repository-url> chatapp
cd chatapp
```

**Bước 2: Cấu hình môi trường**
```bash
cp .env.example .env
# Chỉnh sửa file .env với các giá trị thực tế:
# - JWTS_SECRET: khóa bí mật JWT (tối thiểu 32 bytes)
# - DB_PASSWORD: mật khẩu PostgreSQL
# - S3_ACCESS_KEY/S3_SECRET_KEY: khóa truy cập S3
# - LLM_API_KEY: API key cho dịch vụ AI
# - FIREBASE_SERVICE_ACCOUNT_PATH: đường dẫn file service account
# - SMTP_USERNAME/SMTP_PASSWORD: thông tin SMTP cho gửi email
# - AGORA_APP_ID/AGORA_APP_CERTIFICATE: thông tin Agora
```

**Bước 3: Cấu hình Firebase**
```bash
mkdir -p secrets
# Copy file firebase-service-account.json vào thư mục secrets/
cp /path/to/firebase-service-account.json secrets/
```

**Bước 4: Khởi chạy Docker Compose**
```bash
docker compose up -d
```

**Bước 5: Kiểm tra trạng thái**
```bash
docker compose ps
# Đảm bảo tất cả 6 container đều ở trạng thái "running (healthy)"
```

Server sẽ khả dụng tại `http://localhost:8080`. API documentation tại `http://localhost:8080/swagger-ui.html`.

### 3.2.3. Triển khai Frontend (Client Flutter)

**Bước 1: Clone repository**
```bash
git clone <repository-url> chatapp-flutter
cd chatapp-flutter
```

**Bước 2: Cấu hình API URL**
```bash
cp .env.example.json .env.json
# Chỉnh sửa .env.json:
# { "API_BASE_URL": "http://<server-ip>:8080" }
```

**Bước 3: Cấu hình Firebase (Android)**
```bash
# Copy google-services.json vào android/app/
cp /path/to/google-services.json android/app/
```

**Bước 4: Cấu hình Agora**
```bash
# Chỉnh sửa lib/core/agora_config.dart
# Thay <YOUR_APP_ID> bằng Agora App ID thực tế
```

**Bước 5: Cài đặt dependencies và build**
```bash
flutter pub get

# Build cho Android
flutter build apk --release

# Hoặc chạy trên thiết bị kết nối
flutter run
```

## 3.3. Các kết quả thực hiện được

### 3.3.1. Tổng quan chức năng đã hoàn thành

| # | Nhóm chức năng | Số tính năng | Trạng thái |
|---|---|---|---|
| 1 | Quản lý tài khoản | 6 | ✅ Hoàn thành |
| 2 | Nhắn tin (1-1 và nhóm) | 8 | ✅ Hoàn thành |
| 3 | Quản lý nhóm chat | 6 | ✅ Hoàn thành |
| 4 | Quan hệ người dùng | 6 | ✅ Hoàn thành |
| 5 | Tính năng AI | 4 | ✅ Hoàn thành |
| 6 | Đa phương tiện & Thông báo | 5 | ✅ Hoàn thành |
| **Tổng** | | **35** | **✅ 35/35** |

### 3.3.2. Chi tiết chức năng qua giao diện

#### A. Module Xác thực

| Giao diện | Chức năng chi tiết |
|---|---|
| **LoginScreen** | Đăng nhập bằng username/password, hiển thị lỗi xác thực, loading state, auto-connect WebSocket sau đăng nhập |
| **RegisterScreen** | Đăng ký tài khoản mới, validate mật khẩu, chuyển về login sau thành công |
| **ChangePasswordScreen** | Đổi mật khẩu với xác nhận mật khẩu cũ, validate mật khẩu mới |

#### B. Module Nhắn tin

| Giao diện | Chức năng chi tiết |
|---|---|
| **ChatListScreen** | Danh sách phòng chat real-time, badge tin chưa đọc, ghim/bỏ ghim phòng, sắp xếp theo thời gian |
| **ChatScreen** | Gửi/nhận tin nhắn real-time, gửi ảnh/video/audio/tài liệu, reply tin nhắn (swipe), chỉnh sửa tin nhắn, thu hồi tin nhắn, typing indicator, đánh dấu đã đọc, dịch thuật, tóm tắt, speech-to-text |

#### C. Module Quản lý nhóm

| Giao diện | Chức năng chi tiết |
|---|---|
| **CreateGroupScreen** | Tạo nhóm mới, chọn thành viên từ danh sách bạn bè, đặt tên nhóm |
| **GroupMembersScreen** | Xem danh sách thành viên, thêm/xóa thành viên (admin), phân quyền admin, rời nhóm, giải tán nhóm, cập nhật tên/avatar nhóm |

#### D. Module Quan hệ người dùng

| Giao diện | Chức năng chi tiết |
|---|---|
| **AddFriendScreen** | Tìm kiếm người dùng theo username, gửi lời mời kết bạn |
| **InvitationsScreen** | Xem lời mời đến/đi, chấp nhận/từ chối lời mời kết bạn và mời nhóm |
| **PeopleScreen** | Danh sách bạn bè, trạng thái online/offline real-time, nhấn để mở chat |

#### E. Module AI

| Giao diện | Chức năng chi tiết |
|---|---|
| **ChatbotScreen** | Chat với AI, streaming response (SSE), Markdown rendering, quản lý nhiều cuộc hội thoại, xóa cuộc hội thoại |
| **ChatScreen (AI features)** | Dịch thuật tin nhắn (hỗ trợ nhiều ngôn ngữ), tóm tắt hội thoại, chuyển giọng nói thành văn bản |

#### F. Module Video Call & Thông báo

| Giao diện | Chức năng chi tiết |
|---|---|
| **JoinVideoCallScreen** | Nhập tên kênh, yêu cầu quyền camera/mic, tham gia cuộc gọi |
| **VideoCallScreen** | Grid layout video 1-4 người, mute/unmute, camera on/off, switch camera, kết thúc cuộc gọi |
| **SettingsScreen** | Bật/tắt push notification, cài đặt ngôn ngữ dịch thuật mặc định |

## 3.4. Kết quả thử nghiệm/triển khai

### 3.4.1. Môi trường triển khai

| Thông số | Chi tiết |
|---|---|
| Server | Docker Compose trên máy local/VPS |
| Database | PostgreSQL 18 Alpine |
| Client Test | Android (thiết bị thật), Web (Chrome) |
| Kết nối | Tailscale VPN (cho test từ xa) |

### 3.4.2. Thống kê dữ liệu thử nghiệm

| Thực thể | Số lượng | Ghi chú |
|---|---|---|
| Bảng trong CSDL | 14 | 12 entity chính + 2 bảng join |
| REST API Endpoints | 30+ | Đầy đủ CRUD cho tất cả module |
| WebSocket Channels | 5+ | Chat, Invitation, Presence, Typing, Read |
| Service Classes (Server) | 24+ | Bao gồm 4 AI service |
| Flutter Screens | 17 | 2 auth + 10 home + 5 chat |
| Flutter Providers | 7 | State management cho từng module |
| Flutter Services | 19 | API client, realtime, Firebase, Agora |
| Flutter Models | 16 | Data transfer objects |

### 3.4.3. Kết quả kiểm thử

| Test Case | Kết quả | Ghi chú |
|---|---|---|
| Đăng ký tài khoản mới | ✅ Pass | Mã hóa Argon2, validate unique username |
| Đăng nhập/Đăng xuất | ✅ Pass | JWT token pair, auto-refresh 401 |
| Gửi tin nhắn văn bản | ✅ Pass | Real-time qua WebSocket < 500ms |
| Gửi file ảnh/video | ✅ Pass | Upload S3, preview trong chat |
| Gửi file tài liệu | ✅ Pass | PDF, DOC, XLS với icon phân biệt |
| Ghi âm và Speech-to-Text | ✅ Pass | Whisper transcription tiếng Việt |
| Reply/Edit/Recall tin nhắn | ✅ Pass | Real-time update cho tất cả người dùng |
| Tạo nhóm chat | ✅ Pass | Tối thiểu 3 thành viên |
| Quản lý thành viên nhóm | ✅ Pass | Phân quyền admin/member |
| Chatbot AI streaming | ✅ Pass | SSE response, Markdown render |
| Dịch thuật tin nhắn | ✅ Pass | Đa ngôn ngữ qua LLM |
| Video call 1-1 | ✅ Pass | Agora RTC, camera/mic toggle |
| Push notification | ✅ Pass | FCM trên Android |
| Auto-reconnect WebSocket | ✅ Pass | 4s delay + token refresh |
| Chặn/Bỏ chặn người dùng | ✅ Pass | Ẩn tin nhắn từ người bị chặn |
| Ghim phòng chat | ✅ Pass | Persistent, hiển thị đầu danh sách |

## 3.5. Kết luận và các điểm hạn chế

### 3.5.1. Kết luận

Nhóm đã hoàn thành việc phát triển ứng dụng nhắn tin thời gian thực **ChatApp** với đầy đủ 35 chức năng theo yêu cầu. Ứng dụng đáp ứng được các mục tiêu đề ra:

1. **Kiến trúc phần mềm hiện đại**: Áp dụng thành công kiến trúc phân lớp (Layered Architecture) phía server và Provider Pattern phía client, đảm bảo tính module hóa và dễ bảo trì.

2. **Real-time Communication**: Triển khai thành công hệ thống nhắn tin thời gian thực với WebSocket/STOMP và Apache Artemis message broker, đảm bảo tin nhắn được gửi nhận tức thì.

3. **Tích hợp AI**: Tích hợp thành công 4 tính năng AI (Chatbot, Dịch thuật, Tóm tắt, Speech-to-Text) sử dụng OpenAI-compatible API và Whisper, nâng cao trải nghiệm người dùng.

4. **Đa nền tảng**: Client Flutter chạy trên Android, iOS và Web từ cùng một codebase, tiết kiệm chi phí phát triển.

5. **Container hóa**: Toàn bộ backend được đóng gói trong Docker Compose với 6 container, dễ dàng triển khai và mở rộng.

6. **Bảo mật**: Triển khai đầy đủ các cơ chế bảo mật: Argon2 password hashing, JWT với refresh token, OAuth2 Resource Server, WebSocket authentication.

### 3.5.2. Các điểm hạn chế

| # | Hạn chế | Mô tả | Hướng khắc phục |
|---|---------|-------|-----------------|
| 1 | Chưa có End-to-End Encryption | Tin nhắn được mã hóa TLS trong transit nhưng chưa có E2E encryption | Tích hợp Signal Protocol hoặc tương tự |
| 2 | Video call chưa có call invitation | Người dùng cần biết trước tên channel để tham gia | Tích hợp Agora Call Invitation API |
| 3 | Web không hỗ trợ Push Notification | Firebase Web SDK chưa được tích hợp | Thêm Firebase Web SDK + Service Worker |
| 4 | Chưa có Unit Test đầy đủ | Test coverage còn thấp | Bổ sung JUnit test cho server, Widget test cho Flutter |
| 5 | Chưa hỗ trợ tin nhắn offline | Tin nhắn chỉ gửi được khi có kết nối | Implement message queue local + sync |
| 6 | Whisper service chưa tích hợp Docker | Whisper service hiện đang comment trong compose.yml | Uncomment và cấu hình GPU support |
| 7 | Chưa có screen sharing | Video call chưa hỗ trợ chia sẻ màn hình | Sử dụng Agora Screen Sharing API |
| 8 | Single server deployment | Chưa hỗ trợ horizontal scaling | Chuyển sang Kubernetes hoặc Docker Swarm |

## 3.6. Tài liệu tham khảo

| # | Tài liệu | URL |
|---|----------|-----|
| 1 | Spring Boot Documentation | https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/ |
| 2 | Flutter Documentation | https://docs.flutter.dev/ |
| 3 | Spring WebSocket (STOMP) Guide | https://docs.spring.io/spring-framework/reference/web/websocket.html |
| 4 | Apache Artemis Documentation | https://activemq.apache.org/components/artemis/documentation/ |
| 5 | PostgreSQL Documentation | https://www.postgresql.org/docs/ |
| 6 | Redis Documentation | https://redis.io/docs/ |
| 7 | Firebase Cloud Messaging | https://firebase.google.com/docs/cloud-messaging |
| 8 | Agora RTC Engine (Flutter) | https://docs.agora.io/en/Video/landing-page?platform=Flutter |
| 9 | OpenAI API Documentation | https://platform.openai.com/docs/api-reference |
| 10 | OpenAI Whisper | https://github.com/openai/whisper |
| 11 | Docker Compose Documentation | https://docs.docker.com/compose/ |
| 12 | Caddy Web Server | https://caddyserver.com/docs/ |
| 13 | Versity S3 Gateway | https://github.com/versity/versitygw |
| 14 | STOMP Protocol Specification | https://stomp.github.io/stomp-specification-1.2.html |
| 15 | Provider Package (Flutter) | https://pub.dev/packages/provider |
| 16 | Spring Security OAuth2 | https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html |
| 17 | JWT (JSON Web Tokens) | https://jwt.io/introduction |
| 18 | Argon2 Password Hashing | https://github.com/P-H-C/phc-winner-argon2 |

---

> **Ngày hoàn thành**: 04/05/2026
> **Nhóm thực hiện**: Nhóm 4




