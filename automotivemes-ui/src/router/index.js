import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import AuthView from '../views/auth/Index.vue'
import store from "@/store";

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: AuthView,
    meta: { isGuest: true }
  },
  {
    path: '/register',
    name: 'register',
    component: AuthView,
    meta: { isGuest: true }
  },
  {
    path: '/equipment-monitor',
    name: 'equipment-monitor',
    component: () => import('@/views/equipment-monitor/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/material-track',
    name: 'material-track',
    component: () => import('@/views/material-track/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/production-plan',
    name: 'production-plan',
    component: () => import('@/views/production-plan/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/quality-inspect',
    name: 'quality-inspect',
    component: () => import('@/views/quality-inspect/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/report-analysis',
    name: 'report-analysis',
    component: () => import('@/views/report-analysis/Index.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/work-order',
    name: 'work-order',
    component: () => import('@/views/work-order/Index.vue'),
    meta: { requiresAuth: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = store.state.user.token
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.meta.isGuest && isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router