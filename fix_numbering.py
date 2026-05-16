# -*- coding: utf-8 -*-
"""Insert Voice-to-Text properly"""
import re, sys
sys.stdout.reconfigure(encoding='utf-8')

path = r'd:\chatapp\bao_cao_thanh_vien_2.md'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

voice_to_text = """### 2.5.6 Biểu đồ tuần tự — Voice-to-Text (Chuyển đổi giọng nói thành văn bản)

Cơ chế: Ứng dụng ghi âm giọng nói người dùng, gửi file audio lên API backend. Backend gọi Google Cloud Speech-to-Text API để nhận diện văn bản và trả về client để gửi như tin nhắn thông thường.

```mermaid
sequenceDiagram
    actor User as Người gửi
    participant App as Flutter App
    participant API as Chat API (Spring)
    participant Google as Google Speech API
    
    User->>App: Nhấn giữ nút Voice
    App-->>User: Hiển thị UI Recording
    User->>App: Thả nút (Kết thúc)
    App->>API: POST /api/v1/voice/recognize (Audio File)
    API->>Google: Gửi Audio Blob
    Google-->>API: Trả về Text (Transcript)
    API-->>App: Text response
    App->>App: Điền Text vào ô nhập tin nhắn
    User->>App: Nhấn Gửi
    App->>API: POST /api/v1/messages/
```

*Hình 2.10: Biểu đồ tuần tự — Tính năng Voice-to-Text*

"""

if '### 2.5.6 Biểu đồ tuần tự — Voice-to-Text' not in content:
    content = content.replace('### 2.5.5 Chi tiết mô hình sự kiện WebSocket (STOMP Events)', voice_to_text + '### 2.5.7 Chi tiết mô hình sự kiện WebSocket (STOMP Events)')

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print('Done applying final fixes.')
