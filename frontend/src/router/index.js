// 功能說明：載入路由建立工具與股票查詢頁面。
// 輸入來源：Vue Router 套件及本地頁面元件。
// 預期輸出：提供建立頁面路由所需的工具與元件。
import { createRouter, createWebHistory } from 'vue-router'
import StockQueryPage from '../pages/StockQueryPage.vue'

// 功能說明：建立網址與頁面元件的對應。
// 輸入來源：目前網址、Vite 基底路徑及下方路由表。
// 預期輸出：網址為根路徑時顯示股票查詢頁。
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'stock-query',
      component: StockQueryPage,
    },
  ],
})

export default router
