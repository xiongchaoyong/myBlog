import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Layout from '@/views/Layout.vue'

/**
 * 路由配置
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
  
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/category-articles',
      name: 'CategoryArticles',
      component: () => import('@/views/CategoryArticles.vue')
    },
    {
      path: '/article/:id',
      name: 'Article',
      component: () => import('@/views/Article.vue')
    },
    {
      path: '/publish/:id?',
      name: 'Publish',
      component: () => import('@/views/Publish.vue')
    },
    {
      path: '/moments',
      name: 'Moments',
      component: () => import('@/views/Moments.vue')
    },
    {
      path: '/hot-trends',
      name: 'HotTrends',
      component: () => import('@/views/HotTrendsView.vue')
    },
    {
      path: '/tool-cards',
      name: 'ToolCards',
      component: () => import('@/components/ToolCards.vue')
    },
    {
      path: '/chat',
      name: 'chat',
      component: () => import("@/views/socialMeida/components/Chat.vue")
    },
    {
      path: '/contacts',
      name: 'contacts',
      component: () => import("@/views/socialMeida/components/Contacts.vue")
    },
    {
      path: '/myroom',
      name: 'myroom',
      component: () => import("@/views/socialMeida/components/MyRoom.vue")
    },
    {
      path: '/ai-chat',
      name: 'AIChat',
      component: () => import("@/views/AIChatView.vue")
    },
    {
      path: '/callback',
      name: 'callback',
      component: () => import("@/views/callback.vue")
    },
  ],
})

export default router
