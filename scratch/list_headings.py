import re
import os
import sys

workspace_dir = r"d:\chatapp"
files = [
    "bao_cao_thanh_vien_1.md",
    "bao_cao_thanh_vien_2.md",
    "bao_cao_thanh_vien_3.md",
    "bao_cao_thanh_vien_4.md"
]

out_path = os.path.join(workspace_dir, "scratch", "headings_output.txt")
with open(out_path, "w", encoding="utf-8") as out_f:
    for f_name in files:
        f_path = os.path.join(workspace_dir, f_name)
        if not os.path.exists(f_path):
            out_f.write(f"\nFile {f_name} does not exist.\n")
            continue
        out_f.write(f"\n==================== {f_name} ====================\n")
        with open(f_path, "r", encoding="utf-8") as f:
            lines = f.readlines()
        for idx, line in enumerate(lines):
            if line.strip().startswith("#"):
                out_f.write(f"Line {idx+1}: {line.strip()}\n")
print("Done! Headings written to scratch/headings_output.txt")
