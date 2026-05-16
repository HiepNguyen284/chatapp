import os
import re

file_path = r'd:\chatapp\gen_docx.py'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix mojibake
replacements = {
    'Má»¤C Lá»¤C': 'MỤC LỤC',
    'DANH SÃ CH VIáº¾T Táº®T': 'DANH SÁCH VIẾT TẮT',
    'DANH SÃ CH HÃŒNH': 'DANH SÁCH HÌNH',
    'DANH SÃ CH Báº¢NG': 'DANH SÁCH BẢNG',
    'Báº¢NG PHÃ‚N CÃ”NG': 'BẢNG PHÂN CÔNG',
    'Bá»˜ GIÃ O Dá»¤C': 'BỘ GIÁO DỤC',
    'BÃ O CÃ O': 'BÁO CÁO',
    'á»¨NG Dá»¤NG': 'ỨNG DỤNG',
    'ChÆ°Æ¡ng': 'Chương',
    '[TÃªn TrÆ°á» ng]': '[Tên Trường]',
    'MÃ´n:': 'Môn:',
    'Báº£ng': 'Bảng',
    'HÃ¬nh': 'Hình',
    r'\*(?:H[iÃ¬]nh) (\d+\.\d+):': r'\*Hình (\d+\.\d+):',
}

for k, v in replacements.items():
    content = content.replace(k, v)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print('Fixed mojibake.')
