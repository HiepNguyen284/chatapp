const puppeteer = require('puppeteer');
const path = require('path');

async function captureScreenshots() {
    console.log('Khởi động trình duyệt...');
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    
    // Set viewport to perfectly match the mobile-container in the HTML
    await page.setViewport({ width: 400, height: 850, deviceScaleFactor: 2 });
    
    const mockups = [
        { file: 'login.html', out: 'tv1_hinh_2_9' },
        { file: 'chat_screen.html', out: 'tv2_hinh_2_9' },
        { file: 'chat_list.html', out: 'tv3_hinh_2_9' },
        // Create copies for other placeholders so docx generator finds them
        { file: 'login.html', out: 'tv1_hinh_2_10' },
        { file: 'login.html', out: 'tv1_hinh_3_2' },
        { file: 'chat_screen.html', out: 'tv2_hinh_2_10' },
        { file: 'chat_screen.html', out: 'tv2_hinh_3_2' },
        { file: 'chat_list.html', out: 'tv3_hinh_2_10' },
        { file: 'chat_list.html', out: 'tv3_hinh_3_2' },
        { file: 'chat_screen.html', out: 'tv4_hinh_2_9' },
        { file: 'chat_list.html', out: 'tv4_hinh_3_2' }
    ];

    for (const mockup of mockups) {
        const fileUrl = `file:///${path.resolve(__dirname, mockup.file).replace(/\\/g, '/')}`;
        console.log(`Đang chụp ảnh ${mockup.file} -> ${mockup.out}.png`);
        
        await page.goto(fileUrl, { waitUntil: 'load', timeout: 10000 });
        
        // Find the mobile-container element to clip the screenshot exactly to it
        const element = await page.$('.mobile-container');
        if (element) {
            await element.screenshot({ path: `../images/${mockup.out}.png` });
        } else {
            console.error(`Không tìm thấy .mobile-container trong ${mockup.file}`);
        }
    }

    await browser.close();
    console.log('Hoàn thành chụp ảnh mockup!');
}

captureScreenshots().catch(console.error);
