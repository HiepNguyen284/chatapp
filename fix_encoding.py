"""Fix encoding using ftfy library."""
import sys, os
sys.stdout.reconfigure(encoding='utf-8')
import ftfy

filepath = r'd:\chatapp\bao_cao_thanh_vien_1.md'

with open(filepath, 'r', encoding='utf-8') as f:
    text = f.read()

fixed = ftfy.fix_text(text)

if fixed != text:
    with open(filepath, 'w', encoding='utf-8', newline='\n') as f:
        f.write(fixed)
    print(f"FIXED: {len(text)} -> {len(fixed)} chars")
    print(f"Preview: {fixed[:150]}")
else:
    print("No changes needed")
    print(f"Preview: {text[:150]}")
