# Plan: Chuyển đổi sang RustFS (giữ Cloudinary)

## Tổng quan
Thêm hỗ trợ RustFS (S3-compatible object storage) bên cạnh Cloudinary. Có thể switch giữa hai provider qua config.

## Docker Compose Architecture
```
┌─────────────────────────────────────────────┐
│           podman compose up -d              │
├─────────────────────────────────────────────┤
│                                             │
│  ┌──────────────┐  ┌──────────────┐        │
│  │   PostgreSQL │  │    RustFS    │        │
│  │   :5432      │  │   :9000      │        │
│  └──────────────┘  └──────────────┘        │
│         │                  │               │
│         └────────┬─────────┘               │
│                  │                         │
│           ┌──────▼──────┐                  │
│           │  ChatApp    │                  │
│           │   :8080     │                  │
│           └─────────────┘                  │
│                                             │
└─────────────────────────────────────────────┘
```

## Cấu hình để switch provider
Trong `application.yml`:
```yaml
file:
  storage:
    provider: "rustfs"  # hoặc "cloudinary"
```

## Các files đã tạo/sửa

### 1. build.gradle.kts
- Giữ lại: `cloudinary-http5`
- Thêm mới: `aws-sdk-s3`

### 2. FileStorageService.kt (interface)
```kotlin
interface FileStorageService {
    fun uploadFile(file: MultipartFile): String
    fun uploadMultipleFiles(files: List<MultipartFile>?): List<Map<String, Any>>?
}
```

### 3. CloudinaryService.java
- Implement `FileStorageService`
- Giữ nguyên logic upload

### 4. RustfsService.kt
- Implement `FileStorageService`
- Sử dụng AWS SDK S3 v2
- Kết nối RustFS qua `endpointOverride`

### 5. FileStorageConfig.kt
- Switch provider dựa trên `file.storage.provider`
- Default: `cloudinary`

### 6. AttachmentService.java
- Sử dụng `FileStorageService` interface
- Không phụ thuộc vào implementation cụ thể

### 7. application.yml
```yaml
file:
  storage:
    provider: "${FILE_STORAGE_PROVIDER:cloudinary}"

rustfs:
  url: "${RUSTFS_URL:http://localhost:9000}"
  access-key: "${RUSTFS_ACCESS_KEY}"
  secret-key: "${RUSTFS_SECRET_KEY}"
  bucket-name: "${RUSTFS_BUCKET_NAME}"
```

### 8. docker-compose.yml
- PostgreSQL: port 5432
- RustFS: port 9000 (service), 9001 (console)
- App: port 8080

## Thứ tự chạy
```bash
# Build và chạy
podman compose up -d

# Xem logs
podman compose logs -f

# Dừng
podman compose down
```

## Environment Variables
```env
# Database
DB_URL=jdbc:postgresql://postgres:5432/chatapp
DB_USERNAME=chatapp
DB_PASSWORD=chatapp

# File Storage Provider (cloudinary | rustfs)
FILE_STORAGE_PROVIDER=rustfs

# RustFS (khi dùng rustfs)
RUSTFS_URL=http://rustfs:9000
RUSTFS_ACCESS_KEY=rustfsadmin
RUSTFS_SECRET_KEY=rustfsadmin
RUSTFS_BUCKET_NAME=chatapp

# Cloudinary (khi dùng cloudinary)
CLOUDINARY_API_KEY=xxx
CLOUDINARY_API_SECRET=xxx
CLOUDINARY_CLOUD_NAME=xxx
```

## Chú ý
- RustFS chạy trên port 9000 (service) và 9001 (console)
- Trong Docker network, dùng hostname `rustfs` thay vì `localhost`
- Bucket sẽ được tự động tạo khi upload file đầu tiên
- Có thể switch provider mà không cần rebuild
