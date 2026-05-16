import re

files = [
    r'd:\chatapp\bao_cao_thanh_vien_1.md',
    r'd:\chatapp\bao_cao_thanh_vien_2.md',
    r'd:\chatapp\bao_cao_thanh_vien_3.md',
    r'd:\chatapp\bao_cao_thanh_vien_4.md'
]

old_text = r'\| 20 \| FR-20 \| Trạng thái online \| Hiển thị trạng thái online/offline \(Redis presence\) \|'
new_text = r'| 20 | FR-20 | Trạng thái online | Hiển thị trạng thái online/offline (Redis presence) |' + '\n' + r'| 21 | FR-21 | Voice-to-Text | Chuyển giọng nói thành văn bản qua Google Speech |'

for fpath in files:
    with open(fpath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Add FR-21
    if 'FR-21' not in content:
        content = re.sub(old_text, new_text, content)
        with open(fpath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Added FR-21 to {fpath}")
