# -*- coding: utf-8 -*-
import re
import sys
sys.stdout.reconfigure(encoding='utf-8')

def read_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

def write_file(path, content):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

tv1 = read_file(r'd:\chatapp\bao_cao_thanh_vien_1.md')
tv2 = read_file(r'd:\chatapp\bao_cao_thanh_vien_2.md')
tv3 = read_file(r'd:\chatapp\bao_cao_thanh_vien_3.md')
tv4 = read_file(r'd:\chatapp\bao_cao_thanh_vien_4.md')

# 1. UPDATE TASK DISTRIBUTION TABLE IN ALL FILES
old_tv2_task = r'\| 2 \| \[Họ tên TV2\] \| \[MSSV2\] \| Chat cá nhân 1-1, Gửi/nhận tin nhắn văn bản, Typing indicator, Trạng thái đã xem, Xóa tin nhắn, Dịch tin nhắn AI \| 25% \|'
new_tv2_task = r'| 2 | [Họ tên TV2] | [MSSV2] | Chat cá nhân 1-1, Gửi/nhận tin nhắn, Typing indicator, Trạng thái đã xem, Xóa tin nhắn, Dịch tin nhắn AI, Voice-to-Text | 25% |'

old_tv4_task = r'\| 4 \| \[Họ tên TV4\] \| \[MSSV4\] \| Gửi hình ảnh/media, Thông báo đẩy \(FCM\), Bật/tắt thông báo, Chatbot AI streaming \| 25% \|'
new_tv4_task = r'| 4 | [Họ tên TV4] | [MSSV4] | Gửi hình ảnh/media, Thông báo đẩy (FCM), Chatbot AI streaming, Video Call (Agora) | 25% |'

for content, name in [(tv1, 'tv1'), (tv2, 'tv2'), (tv3, 'tv3'), (tv4, 'tv4')]:
    content = re.sub(old_tv2_task, new_tv2_task, content)
    content = re.sub(old_tv4_task, new_tv4_task, content)
    if name == 'tv1': tv1 = content
    elif name == 'tv2': tv2 = content
    elif name == 'tv3': tv3 = content
    elif name == 'tv4': tv4 = content

# 2. MOVE VIDEO CALL FROM TV2 TO TV4

# Extract Video Call Sequence Diagram from TV2
seq_pattern = r'(### 2\.5\.5 Biểu đồ tuần tự — Video Call \(Agora RTC\).*?\*Hình 2\.10: Biểu đồ tuần tự — Video Call \(Agora RTC\)\*)'
video_call_seq_match = re.search(seq_pattern, tv2, re.DOTALL)
if video_call_seq_match:
    video_call_seq = video_call_seq_match.group(1)
    # Remove from TV2
    tv2 = tv2.replace(video_call_seq, '')
    # Inject into TV4 before "2.6 Sơ đồ thực thể"
    # Note: TV4 sequence diagrams end around 2.5.4 AI Chatbot. We can add this as 2.5.5
    video_call_seq_tv4 = video_call_seq.replace('2.5.5', '2.5.5').replace('Hình 2.10', 'Hình 2.12') # adjust fig numbers later
    tv4 = tv4.replace('## 2.6 Sơ đồ thực thể', video_call_seq_tv4 + '\n\n## 2.6 Sơ đồ thực thể')

# Extract Video Call WS Events from TV2
ws_pattern = r'(\| Video Call \| `/user/queue/calls/video` \| `VideoCallEvent` \| Cuộc gọi video đến \|\n\| Video Rejected \| `/user/queue/calls/video_rejected` \| `VideoCallRejectedEvent` \| Từ chối cuộc gọi \|)\n'
ws_match = re.search(ws_pattern, tv2)
if ws_match:
    ws_text = ws_match.group(1) + '\n'
    tv2 = tv2.replace(ws_text, '')
    # Inject to TV4 WS section
    tv4 = tv4.replace('| Bật/Tắt thông báo |', ws_text + '| Bật/Tắt thông báo |')

# Extract Video Call API from TV2
api_pattern = r'(\| 12 \| POST \| `/api/v1/chatrooms/\{id\}/video-call/` \| ✓ \| Khởi tạo video call \| 200 \|)\n'
api_match = re.search(api_pattern, tv2)
if api_match:
    api_text = api_match.group(1) + '\n'
    tv2 = tv2.replace(api_text, '')
    # Inject to TV4 API section
    tv4 = tv4.replace('| 15 | POST | `/api/v1/ai/chat/`', api_text + '| 15 | POST | `/api/v1/ai/chat/`')

# Extract Video Call Result from TV2
res_pattern = r'(\| Video call \| ✅ Đạt \| Agora RTC 1-1, token generation \|)\n'
res_match = re.search(res_pattern, tv2)
if res_match:
    res_text = res_match.group(1) + '\n'
    tv2 = tv2.replace(res_text, '')
    tv4 = tv4.replace('| AI Chatbot | ✅ Đạt |', res_text + '| AI Chatbot | ✅ Đạt |')

# 3. ADD VOICE TO TEXT TO TV2
# Add sequence diagram for Voice-to-Text in TV2 (replace the removed 2.5.5)
voice_seq = """### 2.5.5 Biểu đồ tuần tự — Voice-to-Text (Chuyển đổi giọng nói thành văn bản)

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
tv2 = tv2.replace('## 2.6 Sơ đồ thực thể', voice_seq + '\n## 2.6 Sơ đồ thực thể')

# Add API for Voice to Text in TV2
voice_api = '| 12 | POST | `/api/v1/voice/recognize/` | ✓ | Nhận diện giọng nói thành văn bản | 200 |\n'
tv2 = tv2.replace('| 13 | DELETE', voice_api + '| 13 | DELETE')

# Add Result for Voice to Text in TV2
voice_res = '| Voice-to-Text | ✅ Đạt | Chuyển đổi chính xác tiếng Việt qua Google API |\n'
tv2 = tv2.replace('| Dịch tin nhắn AI |', voice_res + '| Dịch tin nhắn AI |')

write_file(r'd:\chatapp\bao_cao_thanh_vien_1.md', tv1)
write_file(r'd:\chatapp\bao_cao_thanh_vien_2.md', tv2)
write_file(r'd:\chatapp\bao_cao_thanh_vien_3.md', tv3)
write_file(r'd:\chatapp\bao_cao_thanh_vien_4.md', tv4)

print("Swapped Video Call to TV4 and added Voice-to-Text to TV2")
