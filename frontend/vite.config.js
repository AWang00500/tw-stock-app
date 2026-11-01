import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// 功能說明：集中設定 Vite 的前端建置與開發行為。
// 輸入來源：下方的外掛、伺服器與路徑別名設定。
// 預期輸出：提供 Vue 開發伺服器及模組解析設定。
export default defineConfig({
  // 功能說明：啟用 Vue 單檔元件與開發工具外掛。
  // 輸入來源：本檔載入的 Vite 外掛。
  // 預期輸出：Vite 可處理 Vue 元件並提供開發輔助工具。
  plugins: [
    vue(),
    vueDevTools(),
  ],
  // 功能說明：設定開發伺服器連接埠及 API 轉送。
  // 輸入來源：瀏覽器送至本機伺服器的請求。
  // 預期輸出：固定使用預設連接埠，並將 /api 請求轉送至後端。
  server: {
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  // 功能說明：設定 src 資料夾的簡寫匯入路徑。
  // 輸入來源：程式碼中的 @ 路徑。
  // 預期輸出：將 @ 解析為 frontend/src。
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
