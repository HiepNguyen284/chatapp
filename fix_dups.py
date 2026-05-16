# -*- coding: utf-8 -*-
"""Remove duplicate sections in Chapter 3 from TV2, TV3, TV4."""
import sys
sys.stdout.reconfigure(encoding='utf-8')

def remove_duplicate_range(lines, start_line, end_line):
    """Remove lines from start_line to end_line (0-indexed)."""
    return lines[:start_line] + lines[end_line:]

# TV2: Remove duplicate 3.2 (lines 1357-1374, keep 1324-1354 which is more detailed)
fname = 'd:\\chatapp\\bao_cao_thanh_vien_2.md'
with open(fname, 'r', encoding='utf-8') as f:
    lines = f.readlines()
print(f'TV2: {len(lines)} lines before')
# Remove lines 1356-1374 (0-indexed: 1355-1373) - the simpler duplicate "## 3.2"
# Find the second "## 3.2" and remove until "## 3.3"
start = None
end = None
for i, line in enumerate(lines):
    if line.strip() == '## 3.2 Các bước cài đặt và triển khai' and i > 1350:
        start = i
    if start and i > start and line.strip().startswith('## 3.3'):
        end = i
        break
if start and end:
    lines = lines[:start] + lines[end:]
    print(f'  Removed lines {start+1}-{end}: duplicate 3.2')
with open(fname, 'w', encoding='utf-8') as f:
    f.writelines(lines)
print(f'TV2: {len(lines)} lines after')

# TV3: Remove duplicate "Chi tiết cấu hình Docker" (lines 1310+) and duplicate "3.2" (lines 1374+)
fname = 'd:\\chatapp\\bao_cao_thanh_vien_3.md'
with open(fname, 'r', encoding='utf-8') as f:
    lines = f.readlines()
print(f'\nTV3: {len(lines)} lines before')

# First pass: find and remove the second big block starting from duplicate "### Chi tiết cấu hình Docker services" at ~line 1310
# This block runs from line 1310 to the second "## 3.2" at line 1374, and then to "## 3.3" at 1432
# We want to keep the FIRST 3.2 (simpler, lines 1289-1305) and remove the duplicate block (1310-1431)
second_docker_start = None
second_32_end = None
found_first_docker = False
for i, line in enumerate(lines):
    if line.strip() == '### Chi tiết cấu hình Docker services':
        if found_first_docker:
            second_docker_start = i
        else:
            found_first_docker = True
    if second_docker_start and line.strip().startswith('## 3.3'):
        second_32_end = i
        break

if second_docker_start and second_32_end:
    lines = lines[:second_docker_start] + lines[second_32_end:]
    print(f'  Removed lines {second_docker_start+1}-{second_32_end}: duplicate Docker config + 3.2')

with open(fname, 'w', encoding='utf-8') as f:
    f.writelines(lines)
print(f'TV3: {len(lines)} lines after')

# TV4: Same pattern as TV3
fname = 'd:\\chatapp\\bao_cao_thanh_vien_4.md'
with open(fname, 'r', encoding='utf-8') as f:
    lines = f.readlines()
print(f'\nTV4: {len(lines)} lines before')

second_docker_start = None
second_32_end = None
found_first_docker = False
for i, line in enumerate(lines):
    if line.strip() == '### Chi tiết cấu hình Docker services':
        if found_first_docker:
            second_docker_start = i
        else:
            found_first_docker = True
    if second_docker_start and line.strip().startswith('## 3.3'):
        second_32_end = i
        break

if second_docker_start and second_32_end:
    lines = lines[:second_docker_start] + lines[second_32_end:]
    print(f'  Removed lines {second_docker_start+1}-{second_32_end}: duplicate Docker config + 3.2')

with open(fname, 'w', encoding='utf-8') as f:
    f.writelines(lines)
print(f'TV4: {len(lines)} lines after')

print('\nDone! Duplicates removed.')
