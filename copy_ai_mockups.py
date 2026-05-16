import shutil
import os

brain_dir = r"C:\Users\minhd\.gemini\antigravity\brain\81d93176-f72d-4289-946a-4f553f565319"
img_dir = r"d:\chatapp\images"

ai_login = os.path.join(brain_dir, "ui_login_ai_1778766799278.png")
ai_chatlist = os.path.join(brain_dir, "ui_chatlist_ai_1778766812459.png")
ai_chatscreen = os.path.join(brain_dir, "ui_chatscreen_ai_1778766825093.png")
ai_chatbot = os.path.join(brain_dir, "ui_chatbot_ai_1778766839903.png")

mappings = {
    # TV1 (Login, Register, Profile, Friends)
    "tv1_hinh_2_9.png": ai_login,
    "tv1_hinh_2_10.png": ai_login,
    "tv1_hinh_2_11.png": ai_login,
    "tv1_hinh_2_12.png": ai_login,
    "tv1_hinh_2_13.png": ai_chatlist, # friends list
    "tv1_hinh_2_14.png": ai_chatlist,
    "tv1_hinh_2_15.png": ai_chatlist,
    "tv1_hinh_2_16.png": ai_login,
    "tv1_hinh_3_2.png": ai_login,
    "tv1_hinh_3_3.png": ai_login,
    "tv1_hinh_3_4.png": ai_login,
    "tv1_hinh_3_5.png": ai_chatlist,
    "tv1_hinh_3_6.png": ai_chatlist,
    
    # TV2 (Chat, Typing, Read receipt)
    "tv2_hinh_2_9.png": ai_chatscreen,
    "tv2_hinh_2_10.png": ai_chatscreen,
    "tv2_hinh_2_11.png": ai_chatscreen,
    "tv2_hinh_3_2.png": ai_chatscreen,
    "tv2_hinh_3_3.png": ai_chatscreen,
    "tv2_hinh_3_4.png": ai_chatscreen,
    "tv2_hinh_3_5.png": ai_chatscreen,
    "tv2_hinh_3_6.png": ai_chatscreen,

    # TV3 (Chatlist, Group)
    "tv3_hinh_2_9.png": ai_chatlist,
    "tv3_hinh_2_10.png": ai_chatlist,
    "tv3_hinh_2_11.png": ai_chatlist,
    "tv3_hinh_2_12.png": ai_chatlist,
    "tv3_hinh_3_2.png": ai_chatlist,
    "tv3_hinh_3_3.png": ai_chatlist,
    "tv3_hinh_3_4.png": ai_chatlist,
    "tv3_hinh_3_5.png": ai_chatlist,

    # TV4 (Media, Chatbot, Settings)
    "tv4_hinh_2_9.png": ai_chatscreen,
    "tv4_hinh_2_10.png": ai_chatbot,
    "tv4_hinh_2_11.png": ai_login, # settings
    "tv4_hinh_3_2.png": ai_chatscreen,
    "tv4_hinh_3_3.png": ai_chatscreen,
    "tv4_hinh_3_4.png": ai_chatbot,
}

for out_name, src in mappings.items():
    dest = os.path.join(img_dir, out_name)
    shutil.copy2(src, dest)
    print(f"Copied AI mockup to {out_name}")

