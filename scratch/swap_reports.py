import os
import re

files = [
    'bao_cao_thanh_vien_1.md',
    'bao_cao_thanh_vien_2.md',
    'bao_cao_thanh_vien_3.md',
    'bao_cao_thanh_vien_4.md'
]

# Map current content to target file
# Content in TV1 (Minh) -> target TV3
# Content in TV2 (Hiep) -> target TV2 (minus Video Call)
# Content in TV3 (Quan) -> target TV4
# Content in TV4 (Duy) -> target TV1 (+ Video Call from TV2)

contents = {}

for f in files:
    with open(os.path.join('d:/chatapp', f), 'r', encoding='utf-8') as file:
        full_text = file.read()
        # Front matter ends before Chapter 1
        front_matter = full_text.split('# Chương 1: Mở đầu')[0]
        body = '# Chương 1: Mở đầu' + full_text.split('# Chương 1: Mở đầu')[1]
        contents[f] = {'front': front_matter, 'body': body}

# Extract Video Call from TV2 body
# In TV2 (Hiep), Video Call is section 2.5.4
video_call_pattern = r'### 2\.5\.4 Biểu đồ tuần tự — Video Call \(Agora RTC\).*?(\n\n### 2\.5\.5|$)'
match = re.search(video_call_pattern, contents['bao_cao_thanh_vien_2.md']['body'], re.DOTALL)
video_call_content = ""
if match:
    video_call_content = match.group(0).replace('### 2.5.5', '').strip() + "\n\n"
    # Remove from TV2
    contents['bao_cao_thanh_vien_2.md']['body'] = contents['bao_cao_thanh_vien_2.md']['body'].replace(video_call_content, "")

# Also extract Video Call API from TV2
# | 12 | POST | `/api/v1/chatrooms/{id}/video-call/` | ✓ | Khởi tạo video call | 200 |
vc_api_pattern = r'\| \d+ \| POST \| `/api/v1/chatrooms/\{id\}/video-call/` \|.*?\n'
match_api = re.search(vc_api_pattern, contents['bao_cao_thanh_vien_2.md']['body'])
vc_api_line = ""
if match_api:
    vc_api_line = match_api.group(0)
    contents['bao_cao_thanh_vien_2.md']['body'] = contents['bao_cao_thanh_vien_2.md']['body'].replace(vc_api_line, "")

# Prepare New Bodies
new_bodies = {
    'bao_cao_thanh_vien_1.md': contents['bao_cao_thanh_vien_4.md']['body'], # Duy
    'bao_cao_thanh_vien_2.md': contents['bao_cao_thanh_vien_2.md']['body'], # Hiep
    'bao_cao_thanh_vien_3.md': contents['bao_cao_thanh_vien_1.md']['body'], # Minh
    'bao_cao_thanh_vien_4.md': contents['bao_cao_thanh_vien_3.md']['body']  # Quan
}

# Add Video Call to Duy (TV1)
# Insert before ER Diagram (Section 2.6)
if video_call_content:
    new_bodies['bao_cao_thanh_vien_1.md'] = new_bodies['bao_cao_thanh_vien_1.md'].replace('## 2.6 Sơ đồ thực thể quan hệ', video_call_content + "\n## 2.6 Sơ đồ thực thể quan hệ")

# Add Video Call API to Duy (TV1)
if vc_api_line:
    new_bodies['bao_cao_thanh_vien_1.md'] = new_bodies['bao_cao_thanh_vien_1.md'].replace('| 11 | POST | `/api/v1/invitations/` |', vc_api_line + "| 11 | POST | `/api/v1/invitations/` |")

# Write Back
for f in files:
    with open(os.path.join('d:/chatapp', f), 'w', encoding='utf-8') as file:
        file.write(contents[f]['front'] + new_bodies[f])

print("Swapping complete.")
