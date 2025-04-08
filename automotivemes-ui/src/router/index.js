import {createRouter, createWebHistory} from 'vue-router'
import store from "@/store";

const routes  = [
  {
    path: '/',
    name: 'home',
    meta: { title: '首页', icon: 'HomeIcon' },
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/login',
    name: 'login',
    meta: { title: '登录' },
    component: () => import('@/views/auth/Index.vue'),
  },
  {
    path: '/register',
    name: 'register',
    meta: { title: '注册' },
    component: () => import('@/views/auth/Index.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: { title: '404' },
    component: () => import('@/views/exception/404.vue')
  },
]

// 异步路由（需要权限控制）
const asyncRoutes = [
  {
    path: '/system',
    meta: { title: '系统管理', icon: 'Cog6ToothIcon' },
    children: [
      {
        path: '/system/user-manage',
        name: 'user-manage',
        component: () => import('@/views/system/user/Index.vue'),
        meta: { permissions: ['system:user:manage'], title: '用户管理', icon: 'UserGroupIcon' },
      },
      {
        path: '/system/role-manage',
        name: 'role-manage',
        component: () => import('@/views/system/role/Index.vue'),
        meta: { permissions: ['system:role:manage'], title: '角色管理', icon: 'IdentificationIcon' },
      },
      {
        path: '/system/dept-manage',
        name: 'dept-manage',
        component: () => import('@/views/system/dept/Index.vue'),
        meta: { permissions: ['system:dept:manage'], title: '部门管理', icon: 'BuildingOffice2Icon' },
      },
      {
        path: '/system/post-manage',
        name: 'post-manage',
        component: () => import('@/views/system/post/Index.vue'),
        meta: { permissions: ['system:post:manage'], title: '岗位管理', icon: 'BriefcaseIcon' },
      },
    ]
  },
  {
    path: '/equipment',
    meta: { title: '设备监控', icon: 'ComputerDesktopIcon' },
    children: [
      {
        path: '/equipment/manage',
        name: 'equipment-manage',
        component: () => import('@/views/equipment/Manage.vue'),
        meta: { permissions: ['equipment:manage'], title: '设备管理', icon: 'WrenchScrewdriverIcon' },
      },
    ]
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
        if (homeRoute) { accessedRoutes.unshift(homeRoute) }

        // 清空旧路由
        router.getRoutes().forEach(route => {
          if (route.name && !routes.find(r => r.name === route.name)) {
            router.removeRoute(route.name)
          }
        })

        // 添加新路由
        accessedRoutes.forEach(route => router.addRoute(route))

        // 将新路由添加到 auth 全局变量
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
      return route.meta.permissions
          ? permissions.some(p => route.meta.permissions.includes(p))
          : true
    }
    return true
  })
}

export default router