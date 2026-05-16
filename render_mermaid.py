# -*- coding: utf-8 -*-
"""Extract Mermaid blocks from 4 reports and render to PNG using mmdc."""
import json
import os
import re
import subprocess

MMDC = r'C:\Users\minhd\AppData\Roaming\npm\mmdc.cmd'
REPORTS = [
    ('bao_cao_thanh_vien_1.md', 'tv1'),
    ('bao_cao_thanh_vien_2.md', 'tv2'),
    ('bao_cao_thanh_vien_3.md', 'tv3'),
    ('bao_cao_thanh_vien_4.md', 'tv4'),
]
IMG_DIR = r'd:\chatapp\images'
os.makedirs(IMG_DIR, exist_ok=True)

config = {
    "theme": "base",
    "layout": "elk",
    "elk": {
        "mergeEdges": False
    },
    "themeVariables": {
        "primaryColor": "#f0f4f8",
        "primaryTextColor": "#1a376c",
        "primaryBorderColor": "#2e75b6",
        "lineColor": "#1a376c",
        "secondaryColor": "#deeaf1",
        "tertiaryColor": "#ffffff",
        "fontFamily": "Segoe UI, Arial, sans-serif",
        "fontSize": "18px",
        "background": "#ffffff",
        "nodeBorder": "#2e75b6",
        "clusterBkg": "#f8fbff",
        "clusterBorder": "#8da3bd",
        "edgeLabelBackground": "#ffffff"
    },
    "flowchart": {
        "curve": "stepBefore"
    },
    "class": {
        "curve": "stepBefore"
    },
    "er": {
        "layoutDirection": "LR",
        "entityPadding": 25,
        "fontSize": 18
    }
}
config_path = os.path.join(IMG_DIR, '_mermaid_config.json')
with open(config_path, 'w', encoding='utf-8') as f:
    json.dump(config, f)

puppeteer_cfg = {
    "args": ["--no-sandbox", "--disable-setuid-sandbox"],
    "headless": True
}
puppeteer_cfg_path = os.path.join(IMG_DIR, '_puppeteer_config.json')
with open(puppeteer_cfg_path, 'w', encoding='utf-8') as f:
    json.dump(puppeteer_cfg, f)


def extract_mermaid_blocks(markdown_content):
    blocks = []
    for match in re.finditer(r'```mermaid\s*\n(.*?)```', markdown_content, re.DOTALL):
        block_content = match.group(1)
        end_pos = match.end()
        caption_match = re.search(r'\[image:\s*([^\]]+)\]', markdown_content[end_pos:end_pos + 1200])
        if caption_match:
            img_name = caption_match.group(1).strip()
        else:
            img_name = f'x_{len(blocks) + 1}.png'
        blocks.append((block_content, img_name))
    return blocks


def normalize_class_members(diagram_block):
    if 'classDiagram' not in diagram_block:
        return diagram_block
    # Mermaid CLI currently breaks return types into misaligned extra rows.
    # Keep method signatures only to preserve clean alignment.
    return re.sub(
        r'^(\s*[+\-#~][^()\n]*\([^)\n]*\))\s*:?\s*[\w<>\[\],.?]+(\s*)$',
        r'\1\2',
        diagram_block,
        flags=re.MULTILINE,
    )


def normalize_elk_layout(diagram_block):
    if '%%{init:' in diagram_block:
        return diagram_block
    # Apply the same ELK layout profile used by figure 2.8 to all diagrams
    # so connectors are orthogonal and less likely to overlap.
    return "%%{init: {'layout': 'elk', 'elk': {'mergeEdges': false}}}%%\n" + diagram_block


def normalize_block(diagram_block):
    normalized = normalize_class_members(diagram_block)
    normalized = normalize_elk_layout(normalized)
    return normalized


total = 0
try:
    for md_file, prefix in REPORTS:
        path = os.path.join(r'd:\chatapp', md_file)
        with open(path, 'r', encoding='utf-8') as f:
            content = f.read()

        blocks_with_captions = extract_mermaid_blocks(content)
        print(f'{md_file}: {len(blocks_with_captions)} blocks')

        for block, img_name in blocks_with_captions:
            mmd_path = os.path.join(IMG_DIR, img_name.replace('.png', '.mmd'))
            png_path = os.path.join(IMG_DIR, img_name)

            with open(mmd_path, 'w', encoding='utf-8') as f:
                f.write(normalize_block(block.strip()))

            try:
                result = subprocess.run(
                    [
                        MMDC,
                        '-i', mmd_path,
                        '-o', png_path,
                        '-s', '4',
                        '-b', 'white',
                        '--configFile', config_path,
                        '-C', r'd:\chatapp\mermaid.css',
                        '-p', puppeteer_cfg_path,
                    ],
                    capture_output=True,
                    text=True,
                    timeout=180,
                )
                if result.returncode == 0 and os.path.exists(png_path):
                    sz = os.path.getsize(png_path) // 1024
                    print(f'  OK {img_name} ({sz}KB)')
                    total += 1
                else:
                    err = (result.stderr or result.stdout or '').strip().replace('\n', ' ')
                    print(f'  FAIL {img_name}: {err[:240]}')
            except Exception as e:
                print(f'  ERR {img_name}: {str(e)[:180]}')
            finally:
                if os.path.exists(mmd_path):
                    os.remove(mmd_path)

    print(f'\nTotal rendered: {total}')
finally:
    if os.path.exists(config_path):
        os.remove(config_path)
    if os.path.exists(puppeteer_cfg_path):
        os.remove(puppeteer_cfg_path)
