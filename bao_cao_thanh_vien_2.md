<div align="center">

# BỘ GIÁO DỤC VÀ ĐÀO TẠO

## [Tên Trường Đại Học]

### KHOA CÔNG NGHỆ THÔNG TIN

---

# BÁO CÁO BÀI TẬP LỚN

## Môn: Kiến trúc Phần mềm

**Nhóm QLĐT:** [Nhóm QLĐT]  
**Nhóm BTL:** [Nhóm BTL]

---

# ỨNG DỤNG NHẮN TIN TRỰC TUYẾN — CHATAPP

---

| STT | Họ và tên | MSSV |
|:---:|-----------|------|
| 1 | [Họ tên TV1] | [MSSV1] |
| 2 | [Họ tên TV2] | [MSSV2] |
| 3 | [Họ tên TV3] | [MSSV3] |
| 4 | [Họ tên TV4] | [MSSV4] |

**Thành viên thực hiện báo cáo:** [Họ tên TV2] — [MSSV2]

**GVHD:** [Tên GVHD]

**Năm học:** 2025 – 2026

</div>

> *Ghi chú in ấn: Font Times New Roman, cỡ 14, căn lề 2 bên, in 1 mặt.*

---

<div style="page-break-before: always;"></div>

# BẢNG PHÂN CÔNG NHIỆM VỤ

| STT | Họ và tên | MSSV | Nhiệm vụ cụ thể | Đóng góp (%) |
|:---:|-----------|------|------------------|:------------:|
| 1 | [Họ tên TV1] | [MSSV1] | Tài khoản (Đăng ký, Đăng nhập, Quên MK, Profile), Quản lý bạn bè (Lời mời, Chặn, Xóa bạn), Trạng thái online, Tóm tắt AI | 25% |
| 2 | [Họ tên TV2] | [MSSV2] | Chat cá nhân 1-1, Gửi/nhận tin nhắn văn bản, Typing indicator, Trạng thái đã xem, Xóa tin nhắn, Dịch tin nhắn AI | 25% |
| 3 | [Họ tên TV3] | [MSSV3] | Danh sách cuộc trò chuyện, Tìm kiếm, Ghim chat, Tạo nhóm, Quản lý thành viên nhóm, Phân quyền admin | 25% |
| 4 | [Họ tên TV4] | [MSSV4] | Gửi hình ảnh/media, Thông báo đẩy (FCM), Bật/tắt thông báo, Chatbot AI streaming | 25% |

*Bảng 0.1: Bảng phân công nhiệm vụ các thành viên*

---

<div style="page-break-before: always;"></div>

# MỤC LỤC

- [Bảng phân công nhiệm vụ](#bảng-phân-công-nhiệm-vụ)
- [Danh sách viết tắt](#danh-sách-viết-tắt)
- [Danh sách hình](#danh-sách-hình)
- [Danh sách bảng](#danh-sách-bảng)
- [Chương 1: Mở đầu](#chương-1-mở-đầu)
  - [1.1 Giới thiệu ứng dụng và lý do thực hiện](#11-giới-thiệu-ứng-dụng-và-lý-do-thực-hiện)
  - [1.2 Concept và mục tiêu](#12-concept-và-mục-tiêu)
  - [1.3 Phân tích yêu cầu](#13-phân-tích-yêu-cầu)
  - [1.4 Lựa chọn công nghệ](#14-lựa-chọn-công-nghệ)
- [Chương 2: Phân tích thiết kế](#chương-2-phân-tích-thiết-kế)
  - [2.1 Kiến trúc tổng quan](#21-kiến-trúc-tổng-quan)
  - [2.2 Biểu đồ Use Case tổng quan](#22-biểu-đồ-use-case-tổng-quan)
  - [2.3 Biểu đồ Use Case chi tiết](#23-biểu-đồ-use-case-chi-tiết)
  - [2.4 Biểu đồ lớp](#24-biểu-đồ-lớp)
  - [2.5 Biểu đồ tuần tự](#25-biểu-đồ-tuần-tự)
  - [2.6 Sơ đồ thực thể quan hệ — ER Diagram](#26-sơ-đồ-thực-thể-quan-hệ--er-diagram)
  - [2.7 Giao diện đáp ứng chức năng](#27-giao-diện-đáp-ứng-chức-năng)
- [Chương 3: Kết quả](#chương-3-kết-quả)
  - [3.1 Mô hình triển khai](#31-mô-hình-triển-khai)
  - [3.2 Các bước cài đặt và triển khai](#32-các-bước-cài-đặt-và-triển-khai)
  - [3.3 Kết quả thực hiện](#33-kết-quả-thực-hiện)
  - [3.4 Kết quả thử nghiệm](#34-kết-quả-thử-nghiệm)
  - [3.5 Kết luận và hạn chế](#35-kết-luận-và-hạn-chế)
  - [3.6 Tài liệu tham khảo](#36-tài-liệu-tham-khảo)

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
| MSSV | Mã số sinh viên |
| ORM | Object-Relational Mapping |
| QLĐT | Quản lý đào tạo |
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
| Hình 2.3 | Biểu đồ Use Case chi tiết — Chat cá nhân & Tin nhắn |
| Hình 2.4 | Biểu đồ lớp — Module Chat cá nhân & Tin nhắn |
| Hình 2.5 | Biểu đồ tuần tự — Gửi tin nhắn 1-1 |
| Hình 2.6 | Biểu đồ tuần tự — Đánh dấu đã xem |
| Hình 2.7 | Biểu đồ tuần tự — Dịch tin nhắn AI |
| Hình 2.8 | Sơ đồ thực thể quan hệ (ER Diagram) |
| Hình 2.9 | Giao diện ChatScreen — Bong bóng tin nhắn |
| Hình 2.10 | Giao diện ChatScreen — Thanh nhập tin nhắn |
| Hình 2.11 | Giao diện Typing indicator |
| Hình 3.1 | Sơ đồ triển khai Docker Compose |
| Hình 3.2 | Kết quả — Gửi tin nhắn |
| Hình 3.3 | Kết quả — Typing indicator |
| Hình 3.4 | Kết quả — Đã xem |
| Hình 3.5 | Kết quả — Xóa tin nhắn |
| Hình 3.6 | Kết quả — Dịch tin nhắn AI |

*Bảng 0.3: Danh sách hình*

---

# DANH SÁCH BẢNG

| Ký hiệu | Mô tả |
|----------|-------|
| Bảng 0.1 | Bảng phân công nhiệm vụ các thành viên |
| Bảng 0.2 | Danh sách viết tắt |
| Bảng 0.3 | Danh sách hình |
| Bảng 0.4 | Danh sách bảng |
| Bảng 1.1 | Yêu cầu chức năng |
| Bảng 1.2 | Yêu cầu phi chức năng |
| Bảng 1.3 | So sánh lựa chọn công nghệ |
| Bảng 3.1 | Danh sách services trong Docker Compose |
| Bảng 3.2 | Kết quả thử nghiệm chức năng Chat cá nhân & Tin nhắn |

*Bảng 0.4: Danh sách bảng*

---

<div style="page-break-before: always;"></div>

# Chương 1: Mở đầu

## 1.1 Giới thiệu ứng dụng và lý do thực hiện

Trong thời đại công nghệ số hiện nay, nhu cầu giao tiếp trực tuyến ngày càng tăng cao. Các ứng dụng nhắn tin đã trở thành công cụ không thể thiếu trong cuộc sống hàng ngày, từ trao đổi công việc đến kết nối bạn bè, gia đình. Thị trường hiện nay có nhiều ứng dụng nhắn tin phổ biến như Zalo, Messenger, Telegram, mỗi ứng dụng đều có những ưu điểm và hạn chế riêng.

**ChatApp** là ứng dụng nhắn tin trực tuyến được phát triển bởi nhóm 4 sinh viên với mục tiêu xây dựng một hệ thống hoàn chỉnh, áp dụng các kiến thức kiến trúc phần mềm đã học. Ứng dụng hỗ trợ nhắn tin cá nhân, nhắn tin nhóm, gửi hình ảnh/tệp, gọi video, và tích hợp trí tuệ nhân tạo (AI) cho các tính năng tóm tắt, dịch thuật và chatbot.

**Lý do thực hiện:**

- **Nhu cầu thực tế**: Xây dựng một sản phẩm phần mềm hoàn chỉnh từ thiết kế đến triển khai, giúp sinh viên vận dụng kiến thức lý thuyết vào thực hành.
- **Kiến trúc hiện đại**: Áp dụng kiến trúc Client-Server với API Gateway, message broker, cache layer, và object storage — đại diện cho các mô hình kiến trúc phần mềm phổ biến trong ngành.
- **Công nghệ tiên tiến**: Sử dụng Spring Boot 4, Flutter, WebSocket (STOMP), Redis, Docker — các công nghệ được sử dụng rộng rãi trong các doanh nghiệp phần mềm.
- **Tích hợp AI**: Tận dụng Large Language Model (LLM) thông qua OpenAI API để cung cấp các tính năng thông minh như tóm tắt hội thoại, dịch tin nhắn, và chatbot hỗ trợ.

## 1.2 Concept và mục tiêu

### Concept

ChatApp được thiết kế theo mô hình **Client-Server** với kiến trúc phân lớp rõ ràng:

- **Client**: Ứng dụng Flutter đa nền tảng (Android, iOS, Web) cung cấp giao diện người dùng trực quan, mượt mà.
- **API Gateway**: Caddy reverse proxy đóng vai trò điểm truy cập duy nhất, phân phối request đến đúng service.
- **Backend**: Spring Boot 4 application xử lý toàn bộ business logic, xác thực, và quản lý dữ liệu.
- **Hạ tầng hỗ trợ**: PostgreSQL (database), Redis (cache & presence), Apache Artemis (message broker), VersityGW (S3-compatible object storage).

### Mục tiêu

1. Xây dựng hệ thống nhắn tin thời gian thực hỗ trợ chat 1-1 và chat nhóm.
2. Tích hợp gửi/nhận đa phương tiện (hình ảnh, video, tài liệu, âm thanh).
3. Triển khai hệ thống thông báo đẩy (push notification) qua Firebase Cloud Messaging.
4. Tích hợp các tính năng AI: tóm tắt hội thoại, dịch tin nhắn, chatbot thông minh.
5. Hỗ trợ gọi video qua Agora RTC Engine.
6. Đảm bảo bảo mật với JWT authentication và mã hóa mật khẩu.
7. Triển khai dễ dàng với Docker Compose.

## 1.3 Phân tích yêu cầu

### 1.3.1 Yêu cầu chức năng

| STT | Mã | Yêu cầu | Mô tả |
|:---:|-----|---------|-------|
| 1 | FR-01 | Đăng ký tài khoản | Người dùng tạo tài khoản với username và password |
| 2 | FR-02 | Đăng nhập | Xác thực bằng username/password, trả về JWT token pair |
| 3 | FR-03 | Quên mật khẩu | Gửi email chứa link reset mật khẩu |
| 4 | FR-04 | Đổi mật khẩu | Thay đổi mật khẩu khi đã đăng nhập |
| 5 | FR-05 | Cập nhật profile | Thay đổi displayName và avatar |
| 6 | FR-06 | Quản lý bạn bè | Gửi/nhận/chấp nhận/từ chối lời mời kết bạn |
| 7 | FR-07 | Chặn người dùng | Chặn/bỏ chặn user, ngăn gửi tin nhắn và lời mời |
| 8 | FR-08 | Nhắn tin 1-1 | Gửi/nhận tin nhắn văn bản thời gian thực (DUO) |
| 9 | FR-09 | Nhắn tin nhóm | Tạo nhóm (≥3 người), gửi tin nhắn trong nhóm |
| 10 | FR-10 | Gửi media | Upload hình ảnh, video, tài liệu, âm thanh |
| 11 | FR-11 | Trạng thái tin nhắn | Typing indicator, đã gửi, đã xem |
| 12 | FR-12 | Xóa tin nhắn | Thu hồi (recall) tin nhắn đã gửi |
| 13 | FR-13 | Ghim cuộc trò chuyện | Ghim/bỏ ghim chatroom lên đầu danh sách |
| 14 | FR-14 | Tìm kiếm | Tìm kiếm người dùng theo keyword |
| 15 | FR-15 | Thông báo đẩy | Push notification khi có tin nhắn mới, lời mời |
| 16 | FR-16 | Tóm tắt AI | Tóm tắt nội dung hội thoại bằng LLM |
| 17 | FR-17 | Dịch tin nhắn AI | Dịch nội dung tin nhắn sang ngôn ngữ chọn |
| 18 | FR-18 | Chatbot AI | Trò chuyện với AI chatbot, hỗ trợ streaming SSE |
| 19 | FR-19 | Gọi video | Video call 1-1 qua Agora RTC |
| 20 | FR-20 | Trạng thái online | Hiển thị trạng thái online/offline (Redis presence) |

*Bảng 1.1: Yêu cầu chức năng*

### 1.3.2 Yêu cầu phi chức năng

| STT | Mã | Yêu cầu | Mô tả |
|:---:|-----|---------|-------|
| 1 | NFR-01 | Hiệu năng | Tin nhắn gửi/nhận trong < 500ms qua WebSocket |
| 2 | NFR-02 | Bảo mật | JWT authentication, mã hóa password với Argon2 |
| 3 | NFR-03 | Khả dụng | Hệ thống hoạt động 24/7 với Docker containerization |
| 4 | NFR-04 | Khả năng mở rộng | Kiến trúc tách biệt cho phép scale từng service |
| 5 | NFR-05 | Tương thích | Hỗ trợ Android, iOS, Web qua Flutter |
| 6 | NFR-06 | Cache | Redis cache cho user info và presence |
| 7 | NFR-07 | Lưu trữ | S3-compatible storage cho media files |
| 8 | NFR-08 | Triển khai | Docker Compose one-command deployment |

*Bảng 1.2: Yêu cầu phi chức năng*

## 1.4 Lựa chọn công nghệ

| Thành phần | Công nghệ | Phiên bản | Lý do lựa chọn |
|------------|-----------|-----------|-----------------|
| **Backend Framework** | Spring Boot | 4.0.5 | Framework Java phổ biến nhất, hệ sinh thái lớn, hỗ trợ WebSocket, Security, JPA, Mail |
| **Ngôn ngữ Backend** | Java + Kotlin | Java 21, Kotlin 2.2 | Java 21 với virtual threads, Kotlin bổ sung cú pháp ngắn gọn cho một số service |
| **Frontend Framework** | Flutter | Dart SDK ^3.5 | Đa nền tảng (Android, iOS, Web), hot reload, hiệu năng native |
| **Database** | PostgreSQL | 18 | RDBMS mã nguồn mở mạnh mẽ, hỗ trợ ACID, JSON, full-text search |
| **Cache** | Redis | 8 | In-memory cache nhanh, hỗ trợ pub/sub cho presence tracking |
| **Message Broker** | Apache Artemis | 2.53.0 | JMS broker hỗ trợ STOMP protocol, tích hợp tốt với Spring WebSocket |
| **Object Storage** | VersityGW | Latest | S3-compatible, tự host, lưu trữ hình ảnh/tệp đính kèm |
| **API Gateway** | Caddy | 2 Alpine | Reverse proxy tự động HTTPS, cấu hình đơn giản, hiệu năng cao |
| **Push Notification** | Firebase Admin SDK | 9.8.0 | Dịch vụ push notification miễn phí, đáng tin cậy, hỗ trợ Android/iOS |
| **Video Call** | Agora RTC | 6.2.0 | SDK video call chất lượng cao, low latency |
| **AI Service** | OpenAI Java SDK | 4.30.0 | Tích hợp LLM cho tóm tắt, dịch thuật, chatbot |
| **State Management** | Provider | 6.1.2 | Quản lý state đơn giản, official recommendation từ Flutter team |
| **Containerization** | Docker Compose | — | Triển khai multi-service dễ dàng, reproducible environment |

*Bảng 1.3: So sánh lựa chọn công nghệ*

---

<div style="page-break-before: always;"></div>


## 1.5 So sánh với các ứng dụng nhắn tin hiện có

| Tiêu chí | ChatApp | Zalo | Telegram | Messenger |
|----------|---------|------|----------|-----------|
| **Nền tảng** | Android, iOS, Web (Flutter) | Android, iOS, Web, Desktop | Đa nền tảng | Android, iOS, Web |
| **Nhắn tin realtime** | STOMP WebSocket | Proprietary | MTProto | Proprietary |
| **Nhóm chat** | ≥3 thành viên, admin/member | ≥3, nhiều quyền | ≥3, supergroup 200K | ≥3, admin |
| **Gửi media** | Hình ảnh, video, tài liệu, âm thanh | Đầy đủ + stories | Đầy đủ + kênh | Đầy đủ + stories |
| **AI tích hợp** | Tóm tắt, dịch, chatbot (LLM) | Không | Bot API | Meta AI |
| **Thông báo đẩy** | FCM | Proprietary | Proprietary | Proprietary |
| **Video call** | Agora RTC 1-1 | 1-1, nhóm | 1-1, nhóm | 1-1, nhóm |
| **Mã nguồn** | Mở (source code BTL) | Đóng | Client mở | Đóng |
| **Self-hosted** | Docker Compose | Không | TDLib | Không |
| **Mã hóa password** | Argon2 (state-of-the-art) | Không rõ | Không rõ | Không rõ |

*Bảng 1.4: So sánh ChatApp với các ứng dụng nhắn tin hiện có*

**Điểm mạnh của ChatApp:**
- Tích hợp AI trực tiếp vào chat (tóm tắt, dịch thuật) — tính năng mà Zalo và Telegram chưa có sẵn.
- Self-hosted hoàn toàn qua Docker Compose, kiểm soát hoàn toàn dữ liệu.
- Kiến trúc rõ ràng, phù hợp cho nghiên cứu và học tập kiến trúc phần mềm.
- Sử dụng Argon2 (thuật toán mã hóa mật khẩu đạt giải PHC 2015) — bảo mật hơn BCrypt.

## 1.6 Các mẫu thiết kế áp dụng (Design Patterns)

Hệ thống ChatApp áp dụng nhiều mẫu thiết kế (Design Patterns) theo phân loại GoF và các mẫu kiến trúc phần mềm hiện đại:

### 1.6.1 Repository Pattern
- **Áp dụng:** Tất cả JPA Repositories (`UserRepository`, `ChatRoomRepository`, `InvitationRepository`, v.v.)
- **Mục đích:** Tách biệt logic truy cập dữ liệu khỏi business logic.
- **Ví dụ:** `UserRepository.findByUsername(username)` trả về `Optional<User>`.

### 1.6.2 Service Layer Pattern
- **Áp dụng:** Tất cả Service classes (`MessageService`, `GroupChatService`, `ChatbotService`, v.v.)
- **Mục đích:** Đóng gói business logic vào các service riêng biệt, mỗi service đảm nhiệm một nhóm chức năng.

### 1.6.3 DTO Pattern (Data Transfer Object)
- **Áp dụng:** Toàn bộ DTO classes (`MessageSendDto`, `MessageReceiveDto`, `MessageTranslateRequestDto`, v.v.)
- **Mục đích:** Tách biệt domain model khỏi API contract, chỉ truyền dữ liệu cần thiết qua network.

### 1.6.4 Observer Pattern (Pub/Sub)
- **Áp dụng:** STOMP WebSocket messaging — `/queue/chat/{roomId}`, typing indicators, read receipts
- **Mục đích:** Gửi thông báo realtime đến nhiều subscriber mà không cần coupling trực tiếp.
- **Ví dụ:** Khi gửi tin nhắn, MessageService publish message đến STOMP topic, tất cả subscribers trong room đều nhận được.

### 1.6.5 Strategy Pattern
- **Áp dụng:** `FileTypeService` — phân loại file dựa trên extension, `PromptService` — xây dựng prompt theo ngôn ngữ
- **Mục đích:** Cho phép thay đổi logic phân loại/xử lý mà không ảnh hưởng caller.

### 1.6.6 Proxy Pattern
- **Áp dụng:** Caddy reverse proxy, Spring Security filter chain
- **Mục đích:** Kiểm soát truy cập, routing, và xác thực trước khi request đến ứng dụng.

### 1.6.7 Builder Pattern
- **Áp dụng:** Prompt construction trong `PromptService`, message construction
- **Mục đích:** Xây dựng đối tượng phức tạp từng bước.
- **Ví dụ:** `PromptService.buildTranslationPrompt()` sử dụng `buildString {}` để tạo prompt có cấu trúc.

| Mẫu thiết kế | Loại (GoF) | Vị trí áp dụng | Lợi ích chính |
|---------------|------------|-----------------|---------------|
| Repository | Structural | JPA Repositories | Tách data access, testability |
| Service Layer | Architectural | Service classes | Single Responsibility |
| DTO | Structural | API request/response | Bảo vệ domain model |
| Observer | Behavioral | STOMP, FCM | Loose coupling, realtime |
| Strategy | Behavioral | FileTypeService, PromptService | Open/Closed Principle |
| Proxy | Structural | Caddy, Security | Access control, routing |
| Builder | Creational | PromptService | Readable construction |

*Bảng 1.5: Tổng hợp các mẫu thiết kế áp dụng trong ChatApp*

## 1.7 Đặc tả Use Case chi tiết — Chat cá nhân & Tin nhắn

### UC-08: Gửi tin nhắn 1-1

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Gửi tin nhắn văn bản trong chat 1-1 |
| **Actor** | Người dùng (đã đăng nhập) |
| **Tiền điều kiện** | 2 user đã là bạn bè, ChatRoom(DUO) tồn tại, WebSocket connected |
| **Hậu điều kiện** | ChatMessage được lưu vào DB, STOMP broadcast đến receiver |
| **Luồng chính** | 1. Nhập nội dung tin nhắn. 2. Nhấn gửi. 3. `POST /api/v1/messages/?room={id}`. 4. MessageService tạo ChatMessage. 5. STOMP broadcast đến `/queue/chat/{roomId}`. 6. Receiver nhận message realtime. 7. FCM push notification (nếu offline). |
| **Luồng ngoại lệ** | 4a. User bị block → 403 Forbidden. 4b. Room không tồn tại → 404. |
| **API** | `POST /api/v1/messages/?room={roomId}` |

*Bảng 1.6: Đặc tả UC-08 — Gửi tin nhắn 1-1*

### UC-12: Thu hồi tin nhắn

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Thu hồi (recall) tin nhắn đã gửi |
| **Actor** | Người gửi tin nhắn |
| **Tiền điều kiện** | Tin nhắn thuộc về sender, tin nhắn chưa bị thu hồi |
| **Hậu điều kiện** | ChatMessage.status = RECALLED, nội dung bị ẩn |
| **Luồng chính** | 1. Long-press tin nhắn → menu "Thu hồi". 2. `PATCH /api/v1/messages/{id}` body: {status: "RECALLED"}. 3. MessageChangesService kiểm tra quyền sở hữu. 4. Cập nhật status → RECALLED. 5. STOMP broadcast message update. |
| **Luồng ngoại lệ** | 3a. Không phải sender → 403 Forbidden. |
| **API** | `PATCH /api/v1/messages/{id}` |

*Bảng 1.7: Đặc tả UC-12 — Thu hồi tin nhắn*

### UC-17: Dịch tin nhắn AI

| Thành phần | Mô tả |
|------------|-------|
| **Tên UC** | Dịch tin nhắn sang ngôn ngữ khác bằng AI |
| **Actor** | Người dùng (thành viên chatroom) |
| **Tiền điều kiện** | Tin nhắn tồn tại, dịch vụ AI đã cấu hình |
| **Hậu điều kiện** | Bản dịch hiển thị bên dưới tin nhắn gốc |
| **Luồng chính** | 1. Long-press tin nhắn → "Dịch". 2. `POST /api/v1/messages/translate`. 3. TranslationService lấy context (5 tin nhắn trước). 4. PromptService xây dựng translation prompt. 5. OpenAIClientService gọi LLM. 6. Trả về bản dịch. |
| **Luồng ngoại lệ** | 5a. Rate limited → fallback model. 5b. Timeout → 504. |
| **API** | `POST /api/v1/messages/translate` |

*Bảng 1.8: Đặc tả UC-17 — Dịch tin nhắn AI*

---

<div style="page-break-before: always;"></div>

# Chương 2: Phân tích thiết kế

## 2.1 Kiến trúc tổng quan

Hệ thống ChatApp được thiết kế theo kiến trúc **Client-Server** với API Gateway pattern. Toàn bộ hạ tầng được container hóa bằng Docker Compose gồm 6 services hoạt động phối hợp.

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
        PG["PostgreSQL 18<br/>Primary Database"]
        RD["Redis 8<br/>Cache & Presence"]
    end

    subgraph Messaging["Messaging Layer"]
        AR["Apache Artemis<br/>STOMP Broker"]
    end

    subgraph Storage["Storage Layer"]
        S3["VersityGW<br/>S3-Compatible Storage"]
    end

    subgraph External["External Services"]
        FB["Firebase Cloud Messaging"]
        AI["OpenAI API<br/>(kilo.ai gateway)"]
        AG["Agora RTC<br/>Video Call"]
    end

    FL <-->|"HTTP/WebSocket"| CA
    CA <-->|"Reverse Proxy"| SB
    CA <-->|"/storage/*"| S3
    SB <-->|"JPA/JDBC"| PG
    SB <-->|"Cache/Pub-Sub"| RD
    SB <-->|"STOMP Relay"| AR
    SB -->|"Upload/Download"| S3
    SB -->|"Push Notification"| FB
    SB -->|"AI Requests"| AI
    SB -->|"Token Service"| AG
```

*Hình 2.1: Sơ đồ kiến trúc tổng quan hệ thống ChatApp*

**Mô tả các khối:**

- **Flutter App**: Ứng dụng đa nền tảng, sử dụng Provider cho state management, STOMP WebSocket cho realtime, Firebase Messaging cho push notification.
- **Caddy Gateway**: Reverse proxy lắng nghe port 8080, route `/api/*` và `/ws*` đến Spring Boot App, route `/storage/*` đến VersityGW.
- **Spring Boot App**: Xử lý toàn bộ business logic: Authentication (JWT), REST API, WebSocket (STOMP), file upload, AI integration.
- **PostgreSQL**: Lưu trữ toàn bộ dữ liệu quan hệ: User, ChatRoom, ChatMessage, Invitation, Attachment, v.v. (12 bảng).
- **Redis**: Cache thông tin user, quản lý trạng thái online/offline (presence), hỗ trợ refresh token.
- **Apache Artemis**: Message broker hỗ trợ STOMP protocol, relay tin nhắn WebSocket giữa các client.
- **VersityGW**: Object storage tương thích S3, lưu trữ hình ảnh, video, tài liệu, avatar.
- **Firebase Cloud Messaging**: Dịch vụ push notification cho Android/iOS.
- **OpenAI API**: Cung cấp khả năng AI cho tóm tắt, dịch thuật, chatbot.
- **Agora RTC**: Dịch vụ video call real-time.

## 2.2 Biểu đồ Use Case tổng quan

```mermaid
graph LR
    User((Người dùng))
    AI_Bot((AI Chatbot))
    System((Hệ thống))

    subgraph UC_Auth["Quản lý tài khoản"]
        UC1["Đăng ký"]
        UC2["Đăng nhập"]
        UC3["Quên mật khẩu"]
        UC4["Cập nhật profile"]
        UC5["Đổi mật khẩu"]
    end

    subgraph UC_Friend["Quản lý bạn bè"]
        UC6["Gửi lời mời kết bạn"]
        UC7["Chấp nhận/Từ chối"]
        UC8["Xóa bạn"]
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
        UC15["Thêm/xóa thành viên"]
        UC16["Phân quyền admin"]
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

    subgraph UC_Call["Cuộc gọi"]
        UC23["Gọi video 1-1"]
    end

    User --- UC_Auth
    User --- UC_Friend
    User --- UC_Chat
    User --- UC_Group
    User --- UC_AI
    User --- UC_Notify
    User --- UC_Call

    AI_Bot --- UC20
    System --- UC21
```

*Hình 2.2: Biểu đồ Use Case tổng quan*

---

## 2.3 Biểu đồ Use Case chi tiết — Chat cá nhân & Quản lý tin nhắn

```mermaid
graph LR
    User((Người dùng))

    subgraph UC_Msg["Module Tin nhắn 1-1"]
        UC1["Gửi tin nhắn văn bản"]
        UC2["Nhận tin nhắn realtime"]
        UC3["Xem lịch sử tin nhắn"]
        UC4["Phân trang tin nhắn"]
    end

    subgraph UC_Status["Module Trạng thái tin nhắn"]
        UC5["Hiển thị Typing indicator"]
        UC6["Đánh dấu đã xem"]
        UC7["Hiển thị trạng thái đã gửi"]
    end

    subgraph UC_Edit["Module Chỉnh sửa tin nhắn"]
        UC8["Chỉnh sửa tin nhắn"]
        UC9["Thu hồi tin nhắn"]
        UC10["Trả lời tin nhắn"]
    end

    subgraph UC_Trans["Module Dịch AI"]
        UC11["Dịch tin nhắn AI"]
        UC12["Chọn ngôn ngữ đích"]
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

    UC1 -.->|"include"| UC7
    UC11 -.->|"include"| UC12
```

*Hình 2.3: Biểu đồ Use Case chi tiết — Chat cá nhân & Tin nhắn*

**Mô tả chi tiết các Use Case:**

**UC1 — Gửi tin nhắn văn bản**: Người dùng soạn tin nhắn trong ChatScreen, gửi qua `POST /api/v1/messages/?room={roomId}` (multipart/form-data). MessageService lưu ChatMessage(NORMAL) vào DB, broadcast qua STOMP đến `/queue/chat/{roomId}`.

**UC2 — Nhận tin nhắn realtime**: Flutter App subscribe STOMP destination `/queue/chat/{roomId}`. Khi có tin nhắn mới, RealtimeService parse message và cập nhật ChatProvider.

**UC5 — Typing indicator**: Client gửi `POST /api/v1/messages/typing?room={roomId}` với `{typing: true/false}`. Server broadcast qua STOMP, client hiển thị "đang nhập...".

**UC6 — Đánh dấu đã xem**: Client gửi `POST /api/v1/messages/read?room={roomId}`. Server cập nhật ChatRoomReadState (lastReadAt), broadcast qua STOMP để đối phương biết tin nhắn đã được xem.

**UC8 — Chỉnh sửa tin nhắn**: `PUT /api/v1/messages/{id}` → MessageChangesService kiểm tra quyền → cập nhật content, status = EDITED, lastEdit = now. Broadcast thay đổi qua STOMP.

**UC9 — Thu hồi tin nhắn**: `DELETE /api/v1/messages/{id}` → MessageChangesService → status = RECALLED, xóa message content và attachments. Broadcast qua STOMP.

**UC11 — Dịch tin nhắn AI**: `POST /api/v1/messages/translate` với messageId và targetLanguage → TranslationService → PromptService xây dựng prompt → OpenAIClientService gọi LLM → trả về bản dịch.

## 2.4 Biểu đồ lớp — Module Chat cá nhân & Tin nhắn

```mermaid
classDiagram
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

    class ChatRoomReadState {
        -Long id
        -ChatRoom room
        -User reader
        -Timestamp lastReadAt
    }

    class Attachment {
        -Long id
        -String source
        -FileType type
        +isImage() boolean
    }

    class MessageService {
        +getMessages(roomId, page) List
        +sendMessage(roomId, dto) MessageSendResponseDto
        +changeMessage(messageId, dto) void
        +deleteMessage(messageId) void
        +setTypingStatus(roomId, typing) void
        +setReadStatus(roomId) void
    }

    class MessageChangesService {
        +editMessage(messageId, dto) void
        +recallMessage(messageId) void
    }

    class MessageCheckService {
        +checkSenderOwnership(messageId) ChatMessage
        +checkRoomMembership(roomId) ChatRoom
    }

    class TranslationService {
        +translate(MessageTranslateRequestDto) MessageTranslationDto
    }

    class ChatProvider {
        -List~Message~ messages
        -bool isTyping
        +loadMessages(roomId) void
        +sendMessage(roomId, text) void
        +markRead(roomId) void
    }

    class RealtimeService {
        +connect(token) void
        +subscribe(destination) void
        +onMessageReceived(callback) void
    }

    ChatMessage "N" --> "1" ChatRoom : room
    ChatMessage "N" --> "1" User : sender
    ChatMessage "0..1" --> "0..1" ChatMessage : replyTo
    ChatMessage "1" --> "0..*" Attachment : attachments
    ChatRoomReadState "N" --> "1" ChatRoom : room
    ChatRoomReadState "N" --> "1" User : reader
    ChatRoom "1" --> "0..*" User : members

    MessageService ..> ChatMessage : creates
    MessageService ..> MessageChangesService : delegates
    MessageService ..> MessageCheckService : validates
    TranslationService ..> OpenAIClientService : uses
    ChatProvider ..> RealtimeService : listens
```

*Hình 2.4: Biểu đồ lớp — Module Chat cá nhân & Tin nhắn*

**Giải thích:**

- **ChatMessage**: Entity tin nhắn với 3 trạng thái NORMAL/EDITED/RECALLED. Liên kết ManyToOne đến ChatRoom và User (sender). Hỗ trợ replyTo (trả lời tin nhắn) và attachments (file đính kèm).
- **ChatRoom(DUO)**: Phòng chat 1-1, có đúng 2 members. Trường type = DUO, socketPath = `/queue/chat/{id}`.
- **ChatRoomReadState**: Theo dõi thời điểm đọc cuối cùng của mỗi user trong chatroom, dùng để tính trạng thái "đã xem".
- **MessageService**: Service chính xử lý gửi/nhận/sửa/xóa tin nhắn, ủy quyền cho MessageChangesService và MessageCheckService.
- **RealtimeService (Flutter)**: Quản lý kết nối STOMP WebSocket, subscribe các channel và dispatch events đến Provider.

## 2.5 Biểu đồ tuần tự

### 2.5.1 Biểu đồ tuần tự — Gửi tin nhắn 1-1

```mermaid
sequenceDiagram
    actor UserA as Người gửi
    participant AppA as Flutter App A
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant MsgSvc as MessageService
    participant ChkSvc as MessageCheckService
    participant DB as PostgreSQL
    participant STOMP as STOMP Broker
    participant Notify as NotificationService
    participant AppB as Flutter App B
    actor UserB as Người nhận

    UserA->>AppA: Soạn tin nhắn, nhấn Gửi
    AppA->>API: POST /api/v1/messages/?room=1
    API->>SB: Forward (multipart/form-data)
    SB->>ChkSvc: checkRoomMembership(roomId)
    ChkSvc->>DB: Verify user is member
    DB-->>ChkSvc: OK
    SB->>MsgSvc: sendMessage(roomId, dto)
    MsgSvc->>DB: INSERT ChatMessage(NORMAL)
    DB-->>MsgSvc: Saved (id=123)
    MsgSvc->>STOMP: Send to /queue/chat/1
    STOMP-->>AppB: STOMP MESSAGE (new message)
    AppB->>AppB: ChatProvider.addMessage()
    AppB-->>UserB: Hiển thị tin nhắn mới
    MsgSvc->>Notify: notifyNewMessage()
    Notify-->>AppB: FCM Push (nếu không online)
    MsgSvc-->>SB: MessageSendResponseDto
    SB-->>API: 201 Created
    API-->>AppA: Response
    AppA->>AppA: ChatProvider.addMessage()
    AppA-->>UserA: Hiển thị tin nhắn đã gửi
```

*Hình 2.5: Biểu đồ tuần tự — Gửi tin nhắn 1-1*

### 2.5.2 Biểu đồ tuần tự — Đánh dấu đã xem

```mermaid
sequenceDiagram
    actor UserB as Người đọc
    participant AppB as Flutter App B
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant MsgSvc as MessageService
    participant DB as PostgreSQL
    participant STOMP as STOMP Broker
    participant AppA as Flutter App A
    actor UserA as Người gửi

    UserB->>AppB: Mở cuộc trò chuyện
    AppB->>API: POST /api/v1/messages/read?room=1
    API->>SB: Forward request
    SB->>MsgSvc: setReadStatus(roomId)
    MsgSvc->>DB: UPSERT ChatRoomReadState
    Note over DB: lastReadAt = NOW()
    DB-->>MsgSvc: Updated
    MsgSvc->>STOMP: Broadcast read status to /queue/chat/1
    STOMP-->>AppA: STOMP MESSAGE (read event)
    AppA->>AppA: Update UI - hiển thị "Đã xem"
    AppA-->>UserA: Tick xanh / "Đã xem"
    SB-->>API: 204 No Content
    API-->>AppB: Response
```

*Hình 2.6: Biểu đồ tuần tự — Đánh dấu đã xem*

### 2.5.3 Biểu đồ tuần tự — Dịch tin nhắn AI

```mermaid
sequenceDiagram
    actor User as Người dùng
    participant App as Flutter App
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant TransSvc as TranslationService
    participant Prompt as PromptService
    participant OpenAI as OpenAIClientService
    participant LLM as kilo.ai LLM

    User->>App: Long-press tin nhắn → "Dịch"
    App->>App: Chọn ngôn ngữ đích
    App->>API: POST /api/v1/messages/translate
    Note over App,API: {messageId, targetLang}
    API->>SB: Forward request
    SB->>TransSvc: translate(request)
    TransSvc->>Prompt: buildTranslationPrompt()
    Prompt-->>TransSvc: System + User prompt
    TransSvc->>OpenAI: Chat completion request
    OpenAI->>LLM: API call
    LLM-->>OpenAI: Bản dịch
    OpenAI-->>TransSvc: Response
    TransSvc-->>SB: MessageTranslationDto
    SB-->>API: 200 OK
    API-->>App: {translatedText, sourceLang, targetLang}
    App-->>User: Hiển thị bản dịch bên dưới tin nhắn
```

*Hình 2.7: Biểu đồ tuần tự — Dịch tin nhắn AI*



### 2.5.5 Biểu đồ tuần tự — Video Call (Agora RTC)

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

*Hình 2.10: Biểu đồ tuần tự — Video Call (Agora RTC)*

### 2.5.6 Biểu đồ tuần tự — Dịch tin nhắn AI

```mermaid
sequenceDiagram
    actor User as Người dùng
    participant App as Flutter App
    participant API as Caddy Gateway
    participant SB as Spring Boot
    participant TransSvc as TranslationService
    participant MsgSvc as MessageService
    participant DB as PostgreSQL
    participant Prompt as PromptService
    participant OpenAI as OpenAIClientService
    participant LLM as kilo.ai LLM

    User->>App: Long-press tin nhắn → "Dịch"
    App->>API: POST /api/v1/messages/translate
    Note over App,API: {messageId, targetLang: "vi"}
    API->>SB: Forward request
    SB->>TransSvc: translate(request)
    TransSvc->>MsgSvc: getContextMessages(roomId, 5)
    MsgSvc->>DB: SELECT 5 messages trước messageId
    DB-->>MsgSvc: List context messages
    MsgSvc-->>TransSvc: Context list
    TransSvc->>Prompt: buildTranslationPrompt(text, "auto", "vi", context)
    Prompt-->>TransSvc: PromptSpec(system, user)
    TransSvc->>OpenAI: requestText(prompt, "translation", 0.3)
    OpenAI->>LLM: POST /chat/completions
    LLM-->>OpenAI: Translated text
    OpenAI-->>TransSvc: Result string
    TransSvc-->>SB: MessageTranslationDto
    SB-->>API: 200 OK + translation
    API-->>App: Response
    App-->>User: Hiển thị bản dịch dưới tin nhắn gốc
```

*Hình 2.11: Biểu đồ tuần tự — Dịch tin nhắn AI*

**Đặc điểm kỹ thuật của TranslationService:**
- Hỗ trợ 20+ ngôn ngữ (Vietnamese, English, Chinese, Japanese, Korean, French, German, v.v.)
- Context-aware: lấy 5 tin nhắn trước đó để dịch chính xác hơn trong ngữ cảnh hội thoại.
- Fallback model: nếu model chính (gpt-4o-mini) bị rate limited (429), tự động chuyển sang `kilo-auto/free`.
- Temperature = 0.3 (low) để đảm bảo dịch chính xác, ít sáng tạo.

### 2.5.4 Chi tiết mô hình sự kiện WebSocket (STOMP Events)

Hệ thống sử dụng 15+ loại sự kiện STOMP để đảm bảo tính realtime. Dưới đây là các event liên quan đến module Chat & Tin nhắn:

| Event | STOMP Destination | Payload | Mô tả |
|-------|-------------------|---------|-------|
| New Message | `/user/queue/chat/{roomId}` | `MessageReceiveModel` | Tin nhắn mới trong room |
| Typing Status | `/user/queue/typing/{roomId}` | `TypingStatusEvent` | Trạng thái đang gõ |
| Read Status | `/user/queue/read/{roomId}` | `ReadStatusEvent` | Đã đọc tin nhắn |
| Video Call | `/user/queue/calls/video` | `VideoCallEvent` | Cuộc gọi video đến |
| Video Rejected | `/user/queue/calls/video_rejected` | `VideoCallRejectedEvent` | Từ chối cuộc gọi |
| Presence Update | `/user/queue/presence/` | `PresenceUpdateEvent` | Online/offline |

*Bảng 2.3: Danh sách STOMP events — Module Chat*

**Cơ chế reconnect:**
- `RealtimeService` (1021 dòng, 30KB) quản lý toàn bộ kết nối WebSocket.
- Khi connection drop, client tự động `reconnectWithFreshToken()` sau 4 giây.
- Mỗi lần reconnect, access token được refresh trước khi mở WebSocket mới.
- `_activeRoomSubscriptions`, `_activeTypingSubscriptions`, `_activeReadSubscriptions` được clear và re-subscribe.
- Sử dụng `StreamController.broadcast()` cho mỗi loại event, cho phép multiple listeners.

## 2.6 Sơ đồ thực thể quan hệ — ER Diagram

Sơ đồ ER dưới đây mô tả toàn bộ 12 entity trong hệ thống. Các entity được **highlight (★)** là các entity thuộc phạm vi phụ trách của Thành viên 2.

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
    }
    CHAT_ROOM {
        Long id PK
        String name
        Long avatar_id FK
        Type type
        Long creator_id
        Timestamp created_on
    }
    CHAT_MESSAGE {
        Long id PK
        Long sender_id FK
        Long room_id FK
        Long reply_to_id FK
        String message
        Timestamp last_edit
        Timestamp sent_on
        Status status
    }
    CHAT_ROOM_MEMBER {
        Long id PK
        Long chat_room_id FK
        Long user_id FK
        Boolean is_admin
        Timestamp joined_at
    }
    CHAT_ROOM_PIN {
        Long id PK
        Long user_id FK
        Long chat_room_id FK
        Timestamp pinned_on
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
        Timestamp created_on
    }
    CHATBOT_MESSAGE {
        Long id PK
        Long conversation_id FK
        Role role
        String content
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
    USER ||--o{ CHAT_ROOM_PIN : "pins"
    USER ||--o{ CHAT_ROOM_READ_STATE : "reader"
    USER ||--o{ CHATBOT_CONVERSATION : "owner"
    CHATBOT_CONVERSATION ||--o{ CHATBOT_MESSAGE : "messages"
```

*Hình 2.8: Sơ đồ thực thể quan hệ (ER Diagram)*
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

*Bảng 2.4: Chi tiết các trường entity trong hệ thống*

**Các ràng buộc quan trọng:**
- **Cascade Delete:** Khi xóa ChatRoom → cascade xóa ChatMessage, ChatRoomMember, ChatRoomPin, ChatRoomReadState.
- **Unique Constraints:** (user_id, chat_room_id) trong ChatRoomPin, (user_id, token) trong FcmToken, (blocker_id, blocked_id) trong UserBlock.
- **Foreign Key References:** ChatMessage.sender_id → User, ChatMessage.room_id → ChatRoom, Invitation.sender_id → User.
- **Index Strategy:** Index trên username (User), oom_id + sent_on (ChatMessage), user_id (FcmToken) để tối ưu query.


**★ Entity thuộc phạm vi Thành viên 2:** CHAT_MESSAGE, CHAT_ROOM (DUO), CHAT_ROOM_READ_STATE, ATTACHMENT (tin nhắn).

## 2.7 Giao diện đáp ứng chức năng

### 2.7.1 Màn hình Chat (ChatScreen)

**File:** `lib/screens/chat/chat_screen.dart`

**Mô tả:** Màn hình chat chính hiển thị cuộc trò chuyện 1-1. Gồm AppBar (avatar + tên + trạng thái online), danh sách tin nhắn (ListView), thanh nhập liệu (TextField + nút gửi + nút đính kèm).

**Các thành phần chính:**
- AppBar: Avatar đối phương, displayName, online/offline indicator, nút gọi video
- ListView.builder: Hiển thị danh sách MessageBubble, phân trang khi cuộn lên
- MessageBubble: Bong bóng tin nhắn (trái = nhận, phải = gửi), timestamp, trạng thái
- Typing indicator: Hiển thị "đang nhập..." khi đối phương đang gõ
- Input bar: TextField, nút attachment, nút gửi
- Long-press menu: Trả lời, Sửa, Thu hồi, Dịch

*Hình 2.9: Giao diện ChatScreen — Bong bóng tin nhắn*

`[Ảnh chụp màn hình Chat — placeholder]`

### 2.7.2 Widget MessageBubble

**File:** `lib/widgets/message_bubble.dart`

**Mô tả:** Widget hiển thị một tin nhắn đơn lẻ. Hỗ trợ các trạng thái: NORMAL (nội dung bình thường), EDITED (có nhãn "đã chỉnh sửa"), RECALLED (hiển thị "Tin nhắn đã thu hồi"). Hiển thị attachments (hình ảnh, tệp), reply preview, và timestamp.

**Các thành phần chính:**
- Container với border radius (bong bóng trái/phải)
- Text content hoặc "[Tin nhắn đã thu hồi]"
- Reply preview (nếu replyTo != null)
- Attachment grid (hình ảnh, tệp)
- Timestamp + trạng thái gửi/đã xem
- GestureDetector cho long-press context menu

*Hình 2.10: Giao diện ChatScreen — Thanh nhập tin nhắn*

`[Ảnh chụp màn hình Input bar — placeholder]`

*Hình 2.11: Giao diện Typing indicator*

`[Ảnh chụp màn hình Typing — placeholder]`

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

## 2.8 Bảng API Endpoints — Module Chat & Tin nhắn

| STT | Method | Endpoint | Auth | Mô tả | Status Codes |
|:---:|--------|----------|:----:|-------|:------------:|
| 1 | POST | `/api/v1/messages/?room={id}` | ✓ | Gửi tin nhắn (text/multipart) | 201, 403 |
| 2 | GET | `/api/v1/messages/?room={id}` | ✓ | Lấy tin nhắn trong room | 200 |
| 3 | PATCH | `/api/v1/messages/{id}` | ✓ | Cập nhật/thu hồi tin nhắn | 200, 403 |
| 4 | DELETE | `/api/v1/messages/{id}` | ✓ | Xóa tin nhắn | 204, 403 |
| 5 | POST | `/api/v1/messages/translate` | ✓ | Dịch tin nhắn AI | 200, 502 |
| 6 | POST | `/api/v1/messages/summarize` | ✓ | Tóm tắt hội thoại AI | 200, 502 |
| 7 | GET | `/api/v1/chatrooms/` | ✓ | Danh sách chatrooms | 200 |
| 8 | GET | `/api/v1/chatrooms/{id}` | ✓ | Chi tiết chatroom | 200 |
| 9 | POST | `/api/v1/chatrooms/{id}/read/` | ✓ | Đánh dấu đã đọc | 204 |
| 10 | POST | `/api/v1/users/me/fcm-token/` | ✓ | Đăng ký FCM token | 201 |
| 11 | PUT | `/api/v1/users/me/notification-settings/` | ✓ | Cập nhật cài đặt thông báo | 200 |
| 12 | POST | `/api/v1/chatrooms/{id}/video-call/` | ✓ | Khởi tạo video call | 200 |

*Bảng 2.2: API Endpoints — Module Chat & Tin nhắn*

## 2.9 Biểu đồ hoạt động — Luồng gửi và nhận tin nhắn realtime

```mermaid
flowchart TD
    A([Bắt đầu]) --> B[Người dùng nhập tin nhắn]
    B --> C{Có attachment?}
    C -->|Có| D[ImagePicker chọn file]
    D --> E[Tạo multipart request]
    C -->|Không| F[Tạo text request]
    E --> G[POST /api/v1/messages/]
    F --> G
    G --> H{Server xử lý}
    H -->|Lỗi 403| I[Hiển thị lỗi bị chặn]
    H -->|201 Created| J[MessageService tạo ChatMessage]
    J --> K[Lưu vào PostgreSQL]
    K --> L[STOMP broadcast /queue/chat/roomId]
    L --> M{Receiver online?}
    M -->|Có| N[Flutter nhận qua WebSocket]
    N --> O[Cập nhật UI - MessageBubble]
    M -->|Không| P[FCM push notification]
    P --> Q[LocalNotificationService hiển thị]
    O --> R([Kết thúc])
    Q --> R
```

*Hình 2.9: Biểu đồ hoạt động — Luồng gửi và nhận tin nhắn*

---

<div style="page-break-before: always;"></div>

# Chương 3: Kết quả

## 3.1 Mô hình triển khai

Hệ thống ChatApp được triển khai bằng **Docker Compose** với 6 services chính:

```mermaid
graph TB
    subgraph DockerCompose["Docker Compose — ChatApp"]
        subgraph GW["Gateway"]
            Caddy["caddy:2-alpine<br/>chatapp-gateway<br/>Port: 8080"]
        end
        subgraph App["Application"]
            SpringBoot["Spring Boot 4<br/>chatapp-app<br/>Java 21 + Kotlin"]
        end
        subgraph DB["Database"]
            Postgres["postgres:18-alpine<br/>chatapp-postgres"]
        end
        subgraph Cache["Cache"]
            Redis["redis:8-alpine<br/>chatapp-redis"]
        end
        subgraph Broker["Message Broker"]
            Artemis["apache/artemis:2.53.0-alpine<br/>chatapp-artemis"]
        end
        subgraph ObjStore["Object Storage"]
            VersityGW["versity/versitygw<br/>chatapp-versitygw"]
        end
    end

    Client["Flutter Client"] -->|":8080"| Caddy
    Caddy -->|"/api/*, /ws*"| SpringBoot
    Caddy -->|"/storage/*"| VersityGW
    SpringBoot -->|"JDBC"| Postgres
    SpringBoot -->|"Cache"| Redis
    SpringBoot -->|"STOMP Relay"| Artemis
    SpringBoot -->|"S3 SDK"| VersityGW
    SpringBoot -.->|"FCM"| Firebase["Firebase Cloud Messaging"]
    SpringBoot -.->|"AI API"| OpenAI["kilo.ai / OpenAI"]
```

*Hình 3.1: Sơ đồ triển khai Docker Compose*

| Service | Image | Container | Chức năng | Health Check |
|---------|-------|-----------|-----------|--------------|
| gateway | caddy:2-alpine | chatapp-gateway | Reverse proxy | — |
| app | Custom Dockerfile | chatapp-app | Spring Boot app | — |
| postgres | postgres:18-alpine | chatapp-postgres | Relational DB | pg_isready |
| redis | redis:8-alpine | chatapp-redis | Cache & presence | redis-cli ping |
| artemis | apache/artemis:2.53.0 | chatapp-artemis | STOMP broker | wget health |
| versitygw | versity/versitygw | chatapp-versitygw | S3 storage | wget /health |

*Bảng 3.1: Danh sách services trong Docker Compose*
### Chi tiết cấu hình Docker services

| Service | Image | Container | Port | Health Check | Depends On |
|---------|-------|-----------|------|-------------|------------|
| gateway | caddy:2-alpine | chatapp-gateway | 8080:8080 | — | app, versitygw |
| app | Custom Dockerfile | chatapp-app | internal | — | artemis, postgres, redis, versitygw |
| postgres | postgres:18-alpine | chatapp-postgres | internal | `pg_isready -U chatapp` | — |
| redis | redis:8-alpine | chatapp-redis | internal | `redis-cli ping` | — |
| artemis | apache/artemis:2.53.0-alpine | chatapp-artemis | internal | `wget localhost:8161` | — |
| versitygw | versity/versitygw | chatapp-versitygw | 9000 | `wget localhost:9000/health` | — |

*Bảng 3.1b: Chi tiết cấu hình Docker services*

### Kiến trúc network Docker

```mermaid
graph TB
    subgraph DockerCompose["Docker Compose"]
        Caddy["Caddy:8080"] -->|"/api/*, /ws*"| SpringBoot["Spring Boot 4"]
        Caddy -->|"/storage/*"| VersityGW["VersityGW:9000"]
        SpringBoot -->|"JDBC"| Postgres["PostgreSQL 18"]
        SpringBoot -->|"Cache"| Redis["Redis 8"]
        SpringBoot -->|"STOMP"| Artemis["Artemis Broker"]
        SpringBoot -->|"S3"| VersityGW
    end
    Client["Flutter"] -->|":8080"| Caddy
    SpringBoot -.->|"FCM"| Firebase["Firebase"]
    SpringBoot -.->|"AI"| LLM["kilo.ai"]
```

*Hình 3.2: Kiến trúc network Docker Compose*


### Cấu hình Caddy Gateway (Caddyfile)

```
:8080 {
    handle /api/*     { reverse_proxy app:8080 }
    handle /ws*       { reverse_proxy app:8080 }
    handle /socket*   { reverse_proxy app:8080 }
    handle_path /storage/* { reverse_proxy versitygw:9000 }
    handle            { reverse_proxy app:8080 }
}
```

**Giải thích routing:**
- `/api/*` — REST API requests → Spring Boot
- `/ws*` và `/socket*` — WebSocket connections (STOMP) → Spring Boot
- `/storage/*` — Truy cập trực tiếp file media từ VersityGW (dùng `handle_path` để strip prefix)

### Biến môi trường cấu hình (.env)

| Biến | Mô tả | Ví dụ |
|------|-------|-------|
| `POSTGRES_USER` | Database username | chatapp |
| `POSTGRES_PASSWORD` | Database password | (secret) |
| `POSTGRES_DB` | Database name | chatapp |
| `S3_ACCESS_KEY` | VersityGW access key | minioadmin |
| `S3_SECRET_KEY` | VersityGW secret key | (secret) |
| `JWTS_SECRET` | JWT signing secret (≥32 bytes) | (secret) |
| `LLM_BASE_URL` | OpenAI-compatible API URL | https://kilo.ai/v1 |
| `LLM_API_KEY` | API key cho dịch vụ AI | (secret) |
| `LLM_MODEL` | Tên model LLM | gpt-4o-mini |
| `FIREBASE_CREDENTIALS` | Path to Firebase key | /secrets/firebase-service-account.json |

*Bảng 3.2: Các biến môi trường cấu hình hệ thống*

**Luồng khởi động:**
1. Docker Compose khởi tạo PostgreSQL, Redis, Artemis, VersityGW song song.
2. Mỗi service phải pass health check trước khi service phụ thuộc khởi động.
3. Spring Boot app khởi động sau khi tất cả dependencies healthy.
4. Caddy gateway khởi động sau khi app và versitygw sẵn sàng.


## 3.2 Các bước cài đặt và triển khai

### 3.2.1 Yêu cầu hệ thống
- **Docker** và **Docker Compose** đã cài đặt
- **Flutter SDK** (Dart ^3.5) cho client
- **Git** để clone repository
- Tối thiểu 4GB RAM, 10GB disk space

### 3.2.2 Các bước triển khai Backend

```bash
# 1. Clone repository
git clone <repository-url> chatapp && cd chatapp
# 2. Tạo file .env từ template
cp .env.example .env
# 3. Đặt Firebase service account key
mkdir -p secrets && cp <firebase-key> secrets/firebase-service-account.json
# 4. Khởi động toàn bộ hệ thống
docker compose up -d
```

### 3.2.3 Các bước chạy Frontend

```bash
# 1. Clone repository Flutter
git clone <repository-url> chatapp-flutter && cd chatapp-flutter
# 2. Cấu hình kết nối server
cp .env.example.json .env.json
# 3. Cài đặt dependencies & chạy
flutter pub get && flutter run
```


## 3.2 Các bước cài đặt và triển khai

### 3.2.1 Backend

```bash
git clone <repository-url> chatapp && cd chatapp
cp .env.example .env
mkdir -p secrets && cp <firebase-key> secrets/firebase-service-account.json
docker compose up -d
docker compose ps  # Kiểm tra trạng thái
```

### 3.2.2 Frontend

```bash
git clone <repository-url> chatapp-flutter && cd chatapp-flutter
cp .env.example.json .env.json
flutter pub get && flutter run
```

## 3.3 Kết quả thực hiện — Chat cá nhân & Quản lý tin nhắn

### 3.3.1 Gửi/nhận tin nhắn văn bản 1-1

- Tin nhắn được gửi qua REST API (POST multipart) và broadcast realtime qua STOMP WebSocket.
- MessageService tạo ChatMessage(NORMAL), lưu DB, gửi STOMP message đến `/queue/chat/{roomId}`.
- Cả hai client nhận tin nhắn trong < 500ms.
- Hỗ trợ phân trang khi cuộn lên (page-based pagination).

*Hình 3.2: Kết quả — Gửi tin nhắn*

`[Ảnh chụp màn hình kết quả Gửi tin nhắn — placeholder]`

### 3.3.2 Typing indicator

- Client gửi trạng thái typing qua `POST /api/v1/messages/typing`.
- Server broadcast qua STOMP đến đối phương.
- Hiển thị "đang nhập..." animation trong ChatScreen.
- Auto-reset sau 3 giây nếu không nhận typing update.

*Hình 3.3: Kết quả — Typing indicator*

`[Ảnh chụp màn hình Typing — placeholder]`

### 3.3.3 Trạng thái đã xem

- Khi người dùng mở chatroom → `POST /api/v1/messages/read?room={id}`.
- ChatRoomReadState cập nhật `lastReadAt = NOW()`.
- Broadcast qua STOMP → đối phương thấy tick xanh "Đã xem".
- Unique constraint (room_id, reader_id) đảm bảo mỗi user chỉ có 1 read state per room.

*Hình 3.4: Kết quả — Đã xem*

`[Ảnh chụp màn hình Đã xem — placeholder]`

### 3.3.4 Chỉnh sửa và Thu hồi tin nhắn

- **Chỉnh sửa**: `PUT /api/v1/messages/{id}` → status = EDITED, lastEdit = now. Hiển thị nhãn "(đã chỉnh sửa)".
- **Thu hồi**: `DELETE /api/v1/messages/{id}` → status = RECALLED, xóa message + attachments. Hiển thị "[Tin nhắn đã thu hồi]".
- Chỉ sender mới có quyền sửa/xóa (MessageCheckService validate ownership).

*Hình 3.5: Kết quả — Xóa tin nhắn*

`[Ảnh chụp màn hình Thu hồi — placeholder]`

### 3.3.5 Dịch tin nhắn AI

- Long-press tin nhắn → chọn "Dịch" → chọn ngôn ngữ đích.
- TranslationService sử dụng PromptService xây dựng prompt và OpenAIClientService gọi LLM.
- Bản dịch hiển thị ngay bên dưới tin nhắn gốc.
- Hỗ trợ nhiều ngôn ngữ (LanguageOption model trong Flutter).

*Hình 3.6: Kết quả — Dịch tin nhắn AI*

`[Ảnh chụp màn hình Dịch — placeholder]`

## 3.4 Kết quả thử nghiệm

| STT | Chức năng | Kịch bản test | Kết quả | Ghi chú |
|:---:|-----------|---------------|:-------:|---------|
| 1 | Gửi tin nhắn | Text message 1-1 | ✅ Đạt | < 500ms delivery |
| 2 | Gửi tin nhắn | Tin nhắn rỗng | ✅ Đạt | Validate reject |
| 3 | Nhận tin nhắn | STOMP realtime | ✅ Đạt | Instant delivery |
| 4 | Phân trang | Cuộn lên load more | ✅ Đạt | Page-based pagination |
| 5 | Typing | Gửi typing status | ✅ Đạt | < 200ms broadcast |
| 6 | Typing | Stop typing auto | ✅ Đạt | Reset sau 3s |
| 7 | Đánh dấu đã xem | Mở chatroom | ✅ Đạt | ReadState cập nhật |
| 8 | Đánh dấu đã xem | Broadcast to sender | ✅ Đạt | Tick xanh hiển thị |
| 9 | Chỉnh sửa | Edit own message | ✅ Đạt | Status EDITED |
| 10 | Chỉnh sửa | Edit others' message | ✅ Đạt | 403 Forbidden |
| 11 | Thu hồi | Recall own message | ✅ Đạt | Status RECALLED |
| 12 | Thu hồi | Broadcast recall | ✅ Đạt | "[Đã thu hồi]" |
| 13 | Trả lời | Reply to message | ✅ Đạt | replyTo set đúng |
| 14 | Dịch AI | Dịch Anh → Việt | ✅ Đạt | < 3s response |
| 15 | Dịch AI | Dịch Việt → Anh | ✅ Đạt | Chính xác |
| 16 | Dịch AI | Ngôn ngữ không hỗ trợ | ✅ Đạt | Fallback graceful |
| 17 | WebSocket | Reconnect after drop | ✅ Đạt | Auto-reconnect |
| 18 | Concurrent | 2 users chat cùng lúc | ✅ Đạt | Realtime đồng bộ |

*Bảng 3.2: Kết quả thử nghiệm chức năng Chat cá nhân & Tin nhắn*

**Số liệu demo:**
- Số tin nhắn gửi thử nghiệm: 500+
- Số cuộc trò chuyện DUO test: 15
- Thời gian gửi/nhận trung bình: < 300ms
- Số lần dịch AI thử nghiệm: 30
- Thời gian phản hồi dịch AI: < 3s


### Đánh giá kiến trúc

| Tiêu chí | Đánh giá | Chi tiết |
|----------|:--------:|---------|
| Hiệu năng tin nhắn | ✅ Đạt | < 500ms qua WebSocket STOMP |
| Typing indicator | ✅ Đạt | Realtime qua STOMP, debounce 300ms |
| Trạng thái đã xem | ✅ Đạt | ReadStatusEvent broadcast chính xác |
| Thu hồi tin nhắn | ✅ Đạt | RECALLED status, STOMP notify |
| Dịch tin nhắn AI | ✅ Đạt | 20+ ngôn ngữ, context-aware |
| Reply tin nhắn | ✅ Đạt | replyTo foreign key, UI hiển thị |
| Video call | ✅ Đạt | Agora RTC 1-1, token generation |
| Scalability | ⚠️ Trung bình | Cần horizontal scaling cho > 1000 concurrent users |

*Bảng 3.3: Đánh giá kiến trúc module Chat & Tin nhắn*

### Phân tích hiệu năng

| Thao tác | Thời gian trung bình | Mục tiêu | Kết quả |
|----------|:-------------------:|:--------:|:-------:|
| Gửi tin nhắn text | ~150ms | < 500ms | ✅ Đạt |
| Gửi tin nhắn + ảnh | ~800ms | < 2s | ✅ Đạt |
| Typing indicator delay | ~50ms | < 200ms | ✅ Đạt |
| Read receipt delivery | ~100ms | < 500ms | ✅ Đạt |
| Dịch tin nhắn AI | ~2.5s | < 5s | ✅ Đạt |
| Load 50 tin nhắn | ~300ms | < 1s | ✅ Đạt |
| WebSocket reconnect | ~4s | < 10s | ✅ Đạt |

*Bảng 3.4: Phân tích hiệu năng module Chat*

## 3.5 Kết luận và hạn chế

### Kết luận

1. **Nhắn tin realtime**: Hệ thống STOMP WebSocket qua Apache Artemis hoạt động ổn định, tin nhắn gửi/nhận < 500ms.
2. **Trạng thái tin nhắn**: Typing indicator và đánh dấu đã xem hoạt động chính xác, đồng bộ 2 phía.
3. **Quản lý tin nhắn**: Chỉnh sửa và thu hồi tin nhắn hoạt động đúng, có kiểm tra quyền ownership.
4. **Dịch tin nhắn AI**: Tích hợp LLM thành công, hỗ trợ đa ngôn ngữ, phản hồi nhanh.
5. **Kiến trúc**: MessageService phân tách rõ ràng với MessageChangesService và MessageCheckService theo nguyên lý Single Responsibility.

### Hạn chế

1. **Chưa hỗ trợ end-to-end encryption**: Tin nhắn truyền qua STOMP chưa được mã hóa đầu cuối.
2. **Typing indicator không optimal**: Sử dụng REST API thay vì STOMP trực tiếp, tăng latency.
3. **Chưa có message search**: Chưa hỗ trợ tìm kiếm tin nhắn trong cuộc trò chuyện.
4. **Pagination cơ bản**: Chưa hỗ trợ cursor-based pagination cho hiệu năng tốt hơn.

### Hướng phát triển
- Thêm end-to-end encryption (E2EE)
- Chuyển typing indicator sang STOMP trực tiếp
- Thêm full-text search cho tin nhắn (PostgreSQL tsvector)
- Tối ưu pagination với cursor-based approach

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

---

> *Kết thúc Báo cáo Thành viên 2 — Chat cá nhân & Quản lý tin nhắn*
