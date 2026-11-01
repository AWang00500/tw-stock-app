// 功能說明：載入 Electron 應用程式與桌面視窗建立工具。
// 輸入來源：Electron 套件。
// 預期輸出：提供主程序建立視窗所需的工具。
const { app, BrowserWindow } = require('electron');

// 功能說明：建立並顯示主要視窗。
// 輸入來源：函式內設定的尺寸、安全選項及 Vite 開發伺服器網址。
// 預期輸出：開啟載入 Vue 開發頁面的 1000×700 視窗。
function createWindow() {
  const window = new BrowserWindow({
    width: 1000,
    height: 700,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
    },
  });

  window.loadURL('http://localhost:5173');
}

// 功能說明：等 Electron 初始化完成後建立視窗，並在 macOS 重新啟用時補開視窗。
// 輸入來源：Electron 的就緒與 activate 事件。
// 預期輸出：應用程式啟動或重新啟用時有可用視窗。
app.whenReady().then(() => {
  createWindow();

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

// 功能說明：所有視窗關閉時依作業系統決定是否結束應用程式。
// 輸入來源：Electron 的 window-all-closed 事件與目前平台。
// 預期輸出：非 macOS 平台結束應用程式，macOS 保留應用程式執行。
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
