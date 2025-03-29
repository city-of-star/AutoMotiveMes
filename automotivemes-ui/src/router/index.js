import { createRouter, createWebHistory } from 'vue-router'
import store from "@/store";

const routes  = [
  {
    path: '/',
    name: 'home',
    meta: { title: '首页' },
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/Index.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/auth/Index.vue'),
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/exception/404.vue')
  },
]

// 异步路由（需要权限控制）
const asyncRoutes = [
  {
    path: '/equipment-monitor',
    meta: { title: '设备监控' },
    children: [
      {
        path: '/equipment-monitor/list',
        name: 'equipment-monitor-list',
        component: () => import('@/views/equipment-monitor/Index.vue'),
        meta: { roles: ['SUPER_ADMIN'], permissions: ['equipment:monitor'], title: '设备列表' },
      }
    ]
  },
  {
    path: '/material-track',
    name: 'material-track',
    component: () => import('@/views/material-track/Index.vue'),
    meta: { roles: ['SUPER_ADMIN'], permissions: ['material:trace'], title: '物料追踪' }
  },
  {
    path: '/production-plan',
    name: 'production-plan',
    component: () => import('@/views/production-plan/Index.vue'),
    meta: { roles: ['SUPER_ADMIN'], permissions: ['plan:manage'], title: '生产计划' }
  },
  {
    path: '/quality-inspect',
    name: 'quality-inspect',
    component: () => import('@/views/quality-inspect/Index.vue'),
    meta: { roles: ['SUPER_ADMIN'], permissions: ['quality:inspect'], title: '质量检测' }
  },
  {
    path: '/report-analysis',
    name: 'report-analysis',
    component: () => import('@/views/report-analysis/Index.vue'),
    meta: { roles: ['SUPER_ADMIN'], permissions: ['report:production'], title: '报表分析' }
  },
  {
    path: '/work-order',
    name: 'work-order',
    component: () => import('@/views/work-order/Index.vue'),
    meta: { roles: ['SUPER_ADMIN'], permissions: ['order:manage'], title: '工单执行' }
  },
]

const whiteList = ['/login', '/register', '/404'];  // 手动定义白名单路径

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 检查 token 是否存在
  const token = store.state.user.token || localStorage.getItem('token')
  if (!token) {
    next(`/login?redirect=${to.path}`)
    return
  }

  // 已登录但用户信息未加载
  if (token) {
    if (!store.state.user.roles?.length) {
      try {
        // 重新获取用户权限
        await store.dispatch('user/getUserRoleAndPermission')

        // 重新生成动态路由
        const roles = store.state.user.roles
        const permissions = store.state.user.permissions
        const accessedRoutes = filterRoutes(asyncRoutes, roles, permissions)

        // 添加首页路由
        const homeRoute = routes.find(route => route.name === 'home')
        if (homeRoute) {
          accessedRoutes.unshift(homeRoute)
        }

        // 清空旧路由
        router.getRoutes().forEach(route => {
          if (route.name && !routes.find(r => r.name === route.name)) {
            router.removeRoute(route.name)
          }
        })

        // 添加新路由
        accessedRoutes.forEach(route => router.addRoute(route))

        // 将新路由添加到 user 全局变量
        store.commit('user/SET_ROUTES', accessedRoutes)

        // 添加通配符路由
        if (!router.hasRoute('404')) {
          router.addRoute({ path: '/:pathMatch(.*)*', redirect: '/404' })
        }

        // 重新导航到目标路由
        next({ ...to, replace: true })
      } catch (error) {
        // 获取信息失败则退出登录
        await store.dispatch('user/logout')
        next(`/login?redirect=${to.path}`)
      }
    } else {
      next()
    }
  }
})

// 权限过滤函数
function filterRoutes(routes, roles, permissions) {
  return routes.filter(route => {
    if (route.meta) {
      const hasRole = route.meta.roles
          ? roles.some(role => route.meta.roles.includes(role))
          : true
      const hasPermission = route.meta.permissions
          ? permissions.some(p => route.meta.permissions.includes(p))
          : true
      return hasRole && hasPermission
    }
    return true
  })
}

export default router