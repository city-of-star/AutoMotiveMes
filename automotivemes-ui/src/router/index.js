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
    component: () => import('@/views/auth/Login.vue'),
  },
  {
    path: '/register',
    name: 'register',
    meta: { title: '注册' },
    component: () => import('@/views/auth/Login.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: { title: '404' },
    component: () => import('@/views/exception/404.vue')
  },
  {
    path: '/info',
    name: 'info',
    meta: { title: '个人信息' },
    component: () => import('@/views/auth/UserInfo.vue')
  }
]

// 异步路由（需要权限控制）
const asyncRoutes = [
  {
    path: '/system',
    meta: { permissions: 'system:manage', title: '系统管理', icon: 'Cog6ToothIcon' },
    children: [
      {
        path: '/system/user-manage',
        name: 'user-manage',
        component: () => import('@/views/system/user/Index.vue'),
        meta: { permissions: 'system:user:manage', title: '用户管理', icon: 'UserGroupIcon' },
      },
      {
        path: '/system/role-manage',
        name: 'role-manage',
        component: () => import('@/views/system/role/Index.vue'),
        meta: { permissions: 'system:role:manage', title: '角色管理', icon: 'IdentificationIcon' },
      },
      {
        path: '/system/dept-manage',
        name: 'dept-manage',
        component: () => import('@/views/system/dept/Index.vue'),
        meta: { permissions: 'system:dept:manage', title: '部门管理', icon: 'BuildingOffice2Icon' },
      },
      {
        path: '/system/post-manage',
        name: 'post-manage',
        component: () => import('@/views/system/post/Index.vue'),
        meta: { permissions: 'system:post:manage', title: '岗位管理', icon: 'BriefcaseIcon' },
      },
    ]
  },
  {
    path: '/production',
    meta: { permissions: 'production:monitor', title: '生产监控', icon: 'BuildingOfficeIcon' },
    children: [
      {
        path: '/production/real-time',
        name: 'real-time-monitor',
        component: () => import('@/views/production/real-time/Index.vue'),
        meta: {
          permissions: 'production:monitor:real-time',
          title: '实时监控',
          icon: 'ChartBarIcon',
          keepAlive: false
        },
      },
      {
        path: '/production/history',
        name: 'history-query',
        component: () => import('@/views/production/history/Index.vue'),
        meta: {
          permissions: 'production:monitor:history',
          title: '历史查询',
          icon: 'ClockIcon'
        },
      }
    ]
  },
  {
    path: '/scheduling',
    meta: { permissions: 'scheduling:manage', title: '生产排程', icon: 'CalendarIcon' },
    children: [
      {
        path: '/scheduling/orders',
        name: 'work-order',
        component: () => import('@/views/scheduling/orders/Index.vue'),
        meta: {
          permissions: 'scheduling:order:manage',
          title: '工单管理',
          icon: 'ClipboardDocumentListIcon'
        },
      },
      {
        path: '/scheduling/plan',
        name: 'scheduling-plan',
        component: () => import('@/views/scheduling/plan/Index.vue'),
        meta: {
          permissions: 'scheduling:plan:manage',
          title: '排程计划',
          icon: 'TableCellsIcon'
        },
      }
    ]
  },
  {
    path: '/equipment',
    meta: { permissions: 'equipment:manage', title: '设备管理', icon: 'CpuChipIcon' },
    children: [
      {
        path: '/equipment/status',
        name: 'equipment-status',
        component: () => import('@/views/equipment/status/Index.vue'),
        meta: {
          permissions: 'equipment:status:view',
          title: '设备状态',
          icon: 'PowerIcon'
        },
      },
      {
        path: '/equipment/maintenance',
        name: 'equipment-maintenance',
        component: () => import('@/views/equipment/maintenance/Index.vue'),
        meta: {
          permissions: 'equipment:maintenance:manage',
          title: '维护记录',
          icon: 'WrenchScrewdriverIcon'
        },
      }
    ]
  },
  {
    path: '/alarm',
    meta: { permissions: 'alarm:manage', title: '报警中心', icon: 'BellAlertIcon' },
    children: [
      {
        path: '/alarm/current',
        name: 'current-alarm',
        component: () => import('@/views/alarm/current/Index.vue'),
        meta: {
          permissions: 'alarm:current:view',
          title: '实时报警',
          icon: 'ExclamationTriangleIcon'
        },
      },
      {
        path: '/alarm/history',
        name: 'alarm-history',
        component: () => import('@/views/alarm/history/Index.vue'),
        meta: {
          permissions: 'alarm:history:view',
          title: '报警历史',
          icon: 'ArchiveBoxIcon'
        },
      }
    ]
  },
  {
    path: '/report',
    meta: { permissions: 'report:view', title: '生产报表', icon: 'DocumentChartBarIcon' },
    children: [
      {
        path: '/report/daily',
        name: 'daily-report',
        component: () => import('@/views/report/daily/Index.vue'),
        meta: {
          permissions: 'report:daily:view',
          title: '生产日报',
          icon: 'SunIcon'
        },
      },
      {
        path: '/report/quality',
        name: 'quality-report',
        component: () => import('@/views/report/quality/Index.vue'),
        meta: {
          permissions: 'report:quality:view',
          title: '质量分析',
          icon: 'ChartPieIcon'
        },
      }
    ]
  }
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

  // 验证token是否有效
  try {
    await store.dispatch('user/isValidToken')
  } catch (error) {
    await store.dispatch('user/logout')
    next(`/login?redirect=${to.path}`)
    return
  }

  // 已登录但路由未加载
  if (token) {
    if (!store.state.user.routes?.length) {
      try {
        // 重新获取用户权限
        await store.dispatch('user/getUserRoleAndPermission')

        // 重新生成动态路由
        const permissions = store.state.user.permissions
        const accessedRoutes = filterRoutes(asyncRoutes, permissions)

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
function filterRoutes(routes, permissions) {
  return routes
      .map(route => ({
        ...route,
        children: route.children ? [...route.children] : undefined,
      }))
      .filter(route => {
        // 检查当前路由权限
        const hasPermission = route.meta?.permissions
            ? permissions.some(p => route.meta.permissions.includes(p))
            : true

        // 递归处理子路由
        if (route.children) {
          const filteredChildren = filterRoutes(route.children, permissions)
          route.children = filteredChildren

          // 如果子路由全部被过滤，则父路由也不保留
          if (filteredChildren.length === 0) {
            return false
          }
        }

        return hasPermission
      })
}

export default router