import { createRouter, createWebHistory } from 'vue-router'
import StockQueryPage from '../pages/StockQueryPage.vue'

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
