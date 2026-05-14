# Báo cáo Unit Test — Chatapp Backend

> **Ngày thực hiện:** 02/05/2026  
> **Tổng số test:** 164  
> **Kết quả:** ✅ 164 passed, 0 failed  
> **Công cụ:** JUnit 5, Mockito (mockito-kotlin 5.4.0)

## 1. Tổng quan

Unit test được thiết kế để kiểm tra **business logic** tại tầng Service, sử dụng mock hoàn toàn cho các dependency bên ngoài (Repository, Redis, S3, WebSocket, Firebase, OpenAI). Không có test nào yêu cầu khởi động Spring context hay kết nối database thật.

### Dependencies bổ sung (build.gradle.kts)

```kotlin
testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
testImplementation("org.mockito:mockito-junit-jupiter")
```

## 2. Cấu trúc thư mục test

```
src/test/kotlin/com/group4/chatapp/
├── unit/service/                          # Public services
│   ├── UserServiceTest.kt                 # 21 tests
│   ├── JwtsServiceTest.kt                 #  6 tests
│   ├── ChatRoomServiceTest.kt             # 11 tests
│   ├── GroupChatServiceTest.kt            # 14 tests
│   ├── MessageServiceTest.kt             #  2 tests
│   ├── InvitationServiceTest.kt           #  2 tests
│   ├── UserBlockServiceTest.kt            # 16 tests
│   ├── PasswordResetTokenServiceTest.kt   #  6 tests
│   ├── RefreshTokenServiceTest.kt         #  5 tests
│   ├── UserCacheServiceTest.kt            #  5 tests
│   ├── FcmTokenServiceTest.kt            #  6 tests
│   ├── NotificationPreferenceServiceTest.kt #  6 tests
│   ├── AttachmentServiceTest.kt           #  8 tests
│   └── ai/
│       ├── PromptServiceTest.kt           #  7 tests
│       ├── SummaryServiceTest.kt          #  5 tests
│       └── TranslationServiceTest.kt      #  7 tests
│
└── services/                              # Package-private services (cùng package với source)
    ├── messages/
    │   └── MessageChangesServiceTest.kt   #  6 tests
    └── invitations/
        └── InvitationSubServiceTest.kt    # 10 tests
            ├── InvitationSendServiceTest   #  5 tests
            └── InvitationReplyServiceTest  #  5 tests
```

> **Lưu ý:** Một số service (`MessageChangesService`, `MessageCheckService`, `InvitationSendService`, `InvitationReplyService`) có visibility là **package-private** trong Java. Test cho các class này được đặt trong cùng package (`com.group4.chatapp.services.messages` / `com.group4.chatapp.services.invitations`) để truy cập được.

## 3. Kết quả chi tiết theo Service

### 3.1 UserServiceTest — 21 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| createUser | Tạo user thành công | ✅ |
| createUser | Username đã tồn tại → throw CONFLICT | ✅ |
| getCurrentProfile | User đã đăng nhập → trả về profile | ✅ |
| getCurrentProfile | Chưa xác thực → throw | ✅ |
| updateCurrentProfile | Cập nhật displayName | ✅ |
| updateCurrentProfile | Upload avatar hợp lệ | ✅ |
| updateCurrentProfile | Avatar không phải image → throw | ✅ |
| searchUser | Tìm thấy kết quả | ✅ |
| searchUser | Không có kết quả → list rỗng | ✅ |
| searchUser | Keyword trống → list rỗng | ✅ |
| searchUser | Limit không hợp lệ → throw | ✅ |
| changePassword | Đổi mật khẩu thành công | ✅ |
| changePassword | Sai mật khẩu cũ → throw | ✅ |
| changePassword | Password mới trùng cũ → throw | ✅ |
| changePassword | Password mới quá ngắn → throw | ✅ |
| requestPasswordReset | User tồn tại → gửi email | ✅ |
| requestPasswordReset | User không tồn tại → no-op | ✅ |
| requestPasswordReset | Username trống → skip | ✅ |
| requestPasswordReset | Email gửi thất bại → revoke token + throw | ✅ |
| resetPassword | Token hợp lệ → đổi password | ✅ |
| resetPassword | Token không hợp lệ → throw | ✅ |

### 3.2 JwtsServiceTest — 6 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| tokenObtainPair | Login thành công → trả access + refresh | ✅ |
| tokenObtainPair | Sai password → throw | ✅ |
| refreshToken | Refresh hợp lệ → trả access mới | ✅ |
| refreshToken | Refresh hết hạn → throw | ✅ |
| refreshToken | Refresh đã bị thu hồi → throw | ✅ |
| revokeRefreshToken | Thu hồi thành công | ✅ |

### 3.3 ChatRoomServiceTest — 11 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| listRooms | Không có rooms → list rỗng | ✅ |
| pinRoom | Pin thành công → lưu + STOMP event | ✅ |
| pinRoom | Đã pin → idempotent (no-op) | ✅ |
| unpinRoom | Unpin thành công | ✅ |
| removeFriend | Xóa duo room thành công | ✅ |
| removeFriend | Room không phải DUO → throw | ✅ |
| removeFriend | Room không tồn tại → throw | ✅ |
| removeFriend | User không phải member → throw | ✅ |
| initiateVideoCall | Không phải member → throw | ✅ |
| initiateVideoCall | Tạo call + notify thành viên | ✅ |
| rejectVideoCall | Reject + notify thành viên | ✅ |

### 3.4 GroupChatServiceTest — 14 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| createGroup | Tạo group thành công (min 3 members) | ✅ |
| createGroup | Member không tồn tại → throw | ✅ |
| getGroupDetails | Member xem được chi tiết | ✅ |
| getGroupDetails | Non-member → throw | ✅ |
| updateGroup | Admin cập nhật tên group | ✅ |
| updateGroup | Non-admin → throw | ✅ |
| addMembers | Non-member thêm → throw | ✅ |
| addMembers | Target user không tồn tại → throw | ✅ |
| removeMember | Non-owner remove → throw | ✅ |
| removeMember | Remove creator → throw | ✅ |
| leaveGroup | Creator leave → throw | ✅ |
| leaveGroup | Non-member leave → throw | ✅ |
| dissolveGroup | Creator dissolve → xóa toàn bộ | ✅ |
| dissolveGroup | Non-creator → throw | ✅ |

### 3.5 MessageServiceTest — 2 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| getMessages | Page < 1 → throw | ✅ |
| getMessages | Page âm → throw | ✅ |

> **Ghi chú:** `MessageService` là facade delegate sang `MessageChangesService` và `MessageCheckService` (package-private). Test chi tiết nằm ở `MessageChangesServiceTest`.

### 3.6 MessageChangesServiceTest — 6 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| sendMessage | Gửi tin nhắn text thành công | ✅ |
| editMessage | Sửa tin nhắn của mình | ✅ |
| editMessage | Sửa tin đã RECALLED → throw | ✅ |
| editMessage | Sửa tin của người khác → throw | ✅ |
| recallMessage | Thu hồi tin nhắn thành công | ✅ |
| recallMessage | Thu hồi tin người khác → throw | ✅ |

### 3.7 InvitationServiceTest — 2 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| getInvitations | Có invitations → trả list | ✅ |
| getInvitations | Không có → list rỗng | ✅ |

### 3.8 InvitationSendServiceTest — 5 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| sendInvitation | Gửi lời mời kết bạn thành công | ✅ |
| sendInvitation | Tự mời bản thân → throw | ✅ |
| sendInvitation | Đã là bạn bè → throw | ✅ |
| sendInvitation | Đã có pending invitation → throw | ✅ |
| sendInvitation | Người nhận không tồn tại → throw | ✅ |

### 3.9 InvitationReplyServiceTest — 5 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| replyInvitation | Accept → tạo DUO room | ✅ |
| replyInvitation | Reject → set REJECTED | ✅ |
| replyInvitation | Invitation không tồn tại → throw | ✅ |
| replyInvitation | Invitation đã xử lý → throw | ✅ |
| replyInvitation | Không phải người nhận → throw | ✅ |

### 3.10 UserBlockServiceTest — 16 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| blockByUsername | Block thành công | ✅ |
| blockByUsername | Tự block bản thân → throw | ✅ |
| blockByUsername | Đã block → throw | ✅ |
| blockByUsername | User không tồn tại → throw | ✅ |
| unblockByUsername | Unblock thành công | ✅ |
| getBlockStatus | Chưa block | ✅ |
| getBlockStatus | Block 1 chiều (A→B) | ✅ |
| getBlockStatus | Block 1 chiều (B→A) | ✅ |
| getBlockStatus | Block 2 chiều | ✅ |
| isBlockedEitherWay | Block 1 chiều → true | ✅ |
| isBlockedEitherWay | Không block → false | ✅ |
| isBlockedEitherWay | Block 2 chiều → true | ✅ |
| ensureNotBlocked | Không block → no-op | ✅ |
| ensureNotBlocked | Có block → throw | ✅ |
| listBlockedUsers | Có blocked users → trả list | ✅ |
| listBlockedUsers | Không có → list rỗng | ✅ |

### 3.11 PasswordResetTokenServiceTest — 6 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| generateToken | Tạo token UUID, lưu Redis | ✅ |
| generateToken | Ghi đè token cũ | ✅ |
| validateToken | Token hợp lệ → trả username | ✅ |
| validateToken | Token không tồn tại → throw | ✅ |
| revokeToken | Xóa token khỏi Redis | ✅ |
| revokeByUsername | Xóa theo username | ✅ |

### 3.12 RefreshTokenServiceTest — 5 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| storeRefreshToken | Lưu vào Redis với TTL 30 ngày | ✅ |
| isValid | Token hợp lệ → true | ✅ |
| isValid | Token không tồn tại → false | ✅ |
| isValid | Token không khớp username → false | ✅ |
| revokeRefreshToken | Xóa khỏi Redis | ✅ |

### 3.13 UserCacheServiceTest — 5 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| getCachedUser | Cache hit → trả từ Redis | ✅ |
| getCachedUser | Cache miss → load DB, lưu cache | ✅ |
| getCachedUser | Cache miss + DB miss → empty | ✅ |
| getCachedUser | Redis lỗi → fallback DB | ✅ |
| invalidateUserCache | Xóa cache entry | ✅ |

### 3.14 FcmTokenServiceTest — 6 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| registerToken | Đăng ký token mới | ✅ |
| registerToken | Token đã tồn tại → cập nhật lastUsed | ✅ |
| getTokensForUser | Có tokens → trả list | ✅ |
| getTokensForUser | Không có → list rỗng | ✅ |
| deleteInvalidToken | Xóa token không hợp lệ | ✅ |
| pruneInactiveTokens | Xóa tokens inactive >30 ngày | ✅ |

### 3.15 NotificationPreferenceServiceTest — 6 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| isPushEnabled | Đã set true → true | ✅ |
| isPushEnabled | Đã set false → false | ✅ |
| isPushEnabled | Chưa set (default) → true | ✅ |
| isPushEnabled | Redis lỗi → fallback true | ✅ |
| setPushEnabled | Set true | ✅ |
| setPushEnabled | Set false | ✅ |

### 3.16 AttachmentServiceTest — 8 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| getAttachmentOrThrow | Tìm thấy → trả attachment | ✅ |
| getAttachmentOrThrow | Không tìm thấy → throw | ✅ |
| getAttachments | Không có attachments → list rỗng | ✅ |
| getAttachments | Upload thành công → lưu + trả list | ✅ |
| getAttachments | Upload thất bại → throw | ✅ |
| uploadAvatar | Upload avatar image thành công | ✅ |
| uploadAvatar | Avatar không phải image → throw | ✅ |
| uploadAvatar | File rỗng → throw | ✅ |

### 3.17 PromptServiceTest — 7 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| buildSummaryPrompt | Tạo prompt đúng format | ✅ |
| buildSummaryPrompt | Bao gồm room name | ✅ |
| buildTranslationPrompt | Tạo prompt dịch | ✅ |
| buildTranslationPrompt | Bao gồm source + target language | ✅ |
| buildTranslationPrompt | Bao gồm previous messages context | ✅ |
| buildTranslationPrompt | Không có previous messages | ✅ |
| buildTranslationPrompt | Previous messages rỗng | ✅ |

### 3.18 SummaryServiceTest — 5 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| summarize | Tóm tắt messages thành công | ✅ |
| summarize | API lỗi → fallback message | ✅ |
| summarize | Messages rỗng → throw | ✅ |
| summarize | Messages null → throw | ✅ |
| summarize | Cache hit → không gọi API lần 2 | ✅ |

### 3.19 TranslationServiceTest — 7 tests ✅

| Nhóm | Test case | Kết quả |
|------|-----------|---------|
| translate | Dịch text thành công | ✅ |
| translate | Redis cache hit → không gọi API | ✅ |
| translate | Text rỗng → throw | ✅ |
| translate | API rate limited → throw | ✅ |
| translate | API unavailable → throw | ✅ |
| translate | Target language rỗng → default "vi" | ✅ |
| translate | Cache sau khi dịch → lưu Redis 24h | ✅ |

## 4. Các service KHÔNG được unit test

Các service sau yêu cầu kết nối tới server/infra bên ngoài, không phù hợp cho unit test thuần:

| Service | Lý do bỏ qua |
|---------|---------------|
| `PresenceService` | Phụ thuộc sâu vào Redis Pub/Sub |
| `NotificationService` | Phụ thuộc Firebase Admin SDK |
| `S3Service` | Phụ thuộc Cloudinary/S3 SDK |
| `AgoraTokenService` | Phụ thuộc Agora SDK |
| `OpenAIClientService` | Phụ thuộc OpenAI API client |
| `ChatbotService` | Phụ thuộc SSE + OpenAI streaming |
| `EmailService` | Phụ thuộc SMTP server |
| Tất cả **Controllers** | Sẽ dùng integration test riêng |

## 5. Cách chạy test

```bash
# Chạy toàn bộ test
./gradlew test

# Chạy chỉ unit test (public services)
./gradlew test --tests "com.group4.chatapp.unit.*"

# Chạy test cho service cụ thể
./gradlew test --tests "com.group4.chatapp.unit.service.UserServiceTest"

# Chạy test cho package-private services
./gradlew test --tests "com.group4.chatapp.services.messages.*"
./gradlew test --tests "com.group4.chatapp.services.invitations.*"
```

## 6. Ghi chú kỹ thuật

- **Package-private services:** `MessageChangesService`, `MessageCheckService`, `InvitationSendService`, `InvitationReplyService` có visibility là package-private trong Java. Test files phải nằm trong **cùng package** (không phải `unit/service/`) và sử dụng `internal` visibility trong Kotlin.
- **Mockito Strict Stubbing:** Sử dụng `@ExtendWith(MockitoExtension::class)` với strict stubbing mặc định — tất cả mock phải được sử dụng, không thừa.
- **SecurityContext mocking:** Các test cần authenticated user sẽ mock `SecurityContextHolder` trực tiếp.
- **Lombok + Kotlin:** Sử dụng Lombok Kotlin compiler plugin để Builder pattern hoạt động từ Kotlin test code.
