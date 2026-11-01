// 功能說明：引入 Vue 建立應用程式、根元件、路由及全域樣式。
// 輸入來源：本專案的 Vue 模組與資源檔。
// 預期輸出：備妥啟動畫面所需的程式和樣式。
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'

// 功能說明：建立 Vue 應用、安裝路由並掛載到 HTML 根節點。
// 輸入來源：App 根元件、router 設定及 index.html 的 app 節點。
// 預期輸出：開始顯示 Vue 應用畫面。
const app = createApp(App)

app.use(router)

app.mount('#app')
