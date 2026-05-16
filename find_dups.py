# -*- coding: utf-8 -*-
"""Find and remove duplicate sections in Chapter 3 of TV2, TV3, TV4."""
import re, sys
sys.stdout.reconfigure(encoding='utf-8')

files = [
    'bao_cao_thanh_vien_2.md',
    'bao_cao_thanh_vien_3.md',
    'bao_cao_thanh_vien_4.md',
]

for fname in files:
    path = f'd:\\chatapp\\{fname}'
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    lines = content.split('\n')
    
    # Find all heading positions in Chapter 3
    heading_positions = []
    for i, line in enumerate(lines):
        if line.startswith('## 3.') or line.startswith('### 3.') or line.strip() == '### Chi tiết cấu hình Docker services':
            heading_positions.append((i, line.strip()[:60]))
    
    print(f'\n=== {fname} ===')
    for pos, text in heading_positions:
        print(f'  Line {pos+1}: {text}')
    
    # Find duplicate heading text
    seen = {}
    duplicates = []
    for pos, text in heading_positions:
        if text in seen:
            duplicates.append((text, seen[text], pos))
        else:
            seen[text] = pos
    
    if duplicates:
        print(f'  ** {len(duplicates)} duplicate headings found **')
        for text, first, second in duplicates:
            print(f'    "{text}" at lines {first+1} and {second+1}')
    else:
        print('  No duplicate headings')

