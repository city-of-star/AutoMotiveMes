import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/Index.vue'),
        meta: { title: '数据看板', icon: 'dashboard' }
      }
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !store.state.user.token) {
    next('/login')
  } else {
    // 动态添加路由
    if (!store.state.user.isDynamicAdded) {
      store.dispatch('generateRoutes').then(routes => {
        routes.forEach(route => router.addRoute(route))
        next({ ...to, replace: true })
      }).catch(error => {
        console.error('Failed to generate routes:', error);
        next('/login'); // 处理生成路由失败的情况
      })
    } else {
      next()
    }
  }
})

export default router