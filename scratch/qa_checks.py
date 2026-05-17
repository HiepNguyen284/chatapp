import re
import os
import sys

# Reconfigure stdout to use utf-8 to avoid Windows console CP1252 errors
sys.stdout.reconfigure(encoding='utf-8')

workspace_dir = r"d:\chatapp"
files = [
    "bao_cao_thanh_vien_1.md",
    "bao_cao_thanh_vien_2.md",
    "bao_cao_thanh_vien_3.md",
    "bao_cao_thanh_vien_4.md"
]
images_dir = os.path.join(workspace_dir, "images")

print("Starting QA Checks on Markdown Reports...")

for f_name in files:
    f_path = os.path.join(workspace_dir, f_name)
    if not os.path.exists(f_path):
        print(f"[-] File {f_name} does not exist!")
        continue
    
    print(f"\n==================== Analyzing {f_name} ====================")
    with open(f_path, "r", encoding="utf-8") as f:
        content = f.read()
        lines = content.split('\n')
        
    # 1. Check for unclosed backticks (code blocks)
    code_blocks = content.count("```")
    if code_blocks % 2 != 0:
        print(f"[!] Warning: Odd number of triple backticks ({code_blocks}). Possible unclosed code block!")
    
    # 2. Check for missing images
    image_refs = re.findall(r'\[image:\s*([^\]]+)\]', content, re.IGNORECASE)
    for img_ref in image_refs:
        img_name = img_ref.strip()
        img_path = os.path.join(images_dir, img_name)
        if not os.path.exists(img_path):
            print(f"[-] Missing Image: Referenced '{img_name}' but it was not found in images/ directory!")
            
    # 3. Check for underscore captions vs asterisk captions
    underscore_captions = re.findall(r'^_((?:Hình|Bảng) \d+\.\d+:.*?)_$', content, re.MULTILINE)
    asterisk_captions = re.findall(r'^\*((?:Hình|Bảng) \d+\.\d+:.*?)\*$', content, re.MULTILINE)
    print(f"[i] Found {len(underscore_captions)} underscore captions (_Hình / _Bảng) and {len(asterisk_captions)} asterisk captions (*Hình / *Bảng)")
    
    # 4. Check for sequential figure numbering in the text
    fig_numbers = []
    for line in lines:
        m_fig = re.search(r'([*_])Hình (\d+\.\d+):', line)
        if m_fig:
            fig_numbers.append(m_fig.group(2))
    
    print(f"[i] Figure sequence: {', '.join(fig_numbers)}")
    
    # Check if there are any duplicate figures or jumps
    ch3_figs = [f for f in fig_numbers if f.startswith("3.")]
    if ch3_figs:
        expected = 1
        for f in ch3_figs:
            num = int(f.split(".")[1])
            if num != expected:
                print(f"[!] Warning: Chapter 3 Figure numbering jump! Expected 3.{expected}, got {f}")
            expected = num + 1

    # 5. Check for sequential table numbering in the text
    tab_numbers = []
    for line in lines:
        m_tab = re.search(r'([*_])Bảng (\d+\.\d+):', line)
        if m_tab:
            tab_numbers.append(m_tab.group(2))
            
    print(f"[i] Table sequence: {', '.join(tab_numbers)}")
    ch3_tabs = [t for t in tab_numbers if t.startswith("3.")]
    if ch3_tabs:
        expected = 1
        for t in ch3_tabs:
            num = int(t.split(".")[1])
            if num != expected:
                print(f"[!] Warning: Chapter 3 Table numbering jump! Expected 3.{expected}, got {t}")
            expected = num + 1

print("\nQA Analysis Complete.")
