import os
import re

files = [
    'bao_cao_thanh_vien_1.md',
    'bao_cao_thanh_vien_2.md',
    'bao_cao_thanh_vien_3.md',
    'bao_cao_thanh_vien_4.md'
]

def update_document(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Split front matter and body
    parts = content.split('# Chương 1: Mở đầu')
    front = parts[0]
    body = '# Chương 1: Mở đầu' + parts[1]

    # Renumber Figures
    fig_count = 1
    def fig_repl(match):
        nonlocal fig_count
        caption = match.group(2)
        # We assume Chapter 2 or 3, but for individual reports we use X.Y
        # Let's use 2.X for all figures in Chapter 2 and 3.X for Chapter 3
        # Simple approach: just use 1.X, 2.X based on current chapter
        # Better: find current chapter
        res = f"*Hình {current_chapter}.{fig_count}: {caption}*"
        fig_count += 1
        return res

    # Renumber Tables
    tbl_count = 1
    def tbl_repl(match):
        nonlocal tbl_count
        caption = match.group(2)
        res = f"*Bảng {current_chapter}.{tbl_count}: {caption}*"
        tbl_count += 1
        return res

    new_body_lines = []
    current_chapter = "0"
    fig_list = []
    tbl_list = []
    
    lines = body.split('\n')
    for line in lines:
        if line.startswith('# Chương '):
            current_chapter = line.split(' ')[2].replace(':', '')
            fig_count = 1
            tbl_count = 1
        
        # Match *Hình X.Y: ...* or *Bảng X.Y: ...*
        fig_match = re.search(r'\*Hình \d+\.\d+: (.*?)\*', line)
        if fig_match:
            new_line = f"*Hình {current_chapter}.{fig_count}: {fig_match.group(1)}*"
            fig_list.append(f"| Hình {current_chapter}.{fig_count} | {fig_match.group(1)} |")
            fig_count += 1
            new_body_lines.append(new_line)
            continue
            
        tbl_match = re.search(r'\*Bảng \d+\.\d+: (.*?)\*', line)
        if tbl_match:
            new_line = f"*Bảng {current_chapter}.{tbl_count}: {tbl_match.group(1)}*"
            tbl_list.append(f"| Bảng {current_chapter}.{tbl_count} | {tbl_match.group(1)} |")
            tbl_count += 1
            new_body_lines.append(new_line)
            continue
            
        new_body_lines.append(line)

    new_body = '\n'.join(new_body_lines)

    # Update Front Matter
    # Replace DANH SÁCH HÌNH table
    fig_table = "| Ký hiệu | Mô tả |\n|----------|-------|\n" + "\n".join(fig_list)
    front = re.sub(r'# DANH SÁCH HÌNH\n\n\|.*?\|.*?\|\n\|.*?\|.*?\|\n(\|.*?\|.*?\|\n)*', f"# DANH SÁCH HÌNH\n\n{fig_table}\n", front, flags=re.DOTALL)
    
    # Replace DANH SÁCH BẢNG table
    tbl_table = "| Ký hiệu | Mô tả |\n|----------|-------|\n" + "\n".join(tbl_list)
    front = re.sub(r'# DANH SÁCH BẢNG\n\n\|.*?\|.*?\|\n\|.*?\|.*?\|\n(\|.*?\|.*?\|\n)*', f"# DANH SÁCH BẢNG\n\n{tbl_table}\n", front, flags=re.DOTALL)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(front + new_body)

for f in files:
    update_document(os.path.join('d:/chatapp', f))

print("Renumbering and TOC update complete.")
