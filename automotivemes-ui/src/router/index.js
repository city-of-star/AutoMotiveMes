import {createRouter, createWebHistory} from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import store from "@/store";

// 配置 NProgress()进度条
NProgress.configure({
  showSpinner: false,  // 隐藏加载小圆圈
  easing: 'ease',
  speed: 500
})

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
    meta: { title: '登录', noTab: true },
    component: () => import('@/views/auth/Login.vue'),
  },
  {
    path: '/register',
    name: 'register',
    meta: { title: '注册', noTab: true },
    component: () => import('@/views/auth/Login.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: { title: '404', noTab: true },
    component: () => import('@/views/exception/404.vue')
  },
  {
    path: '/info',
    name: 'info',
    meta: { title: '个人信息' },
    component: () => import('@/views/auth/UserInfo.vue')
  },
  {
    path: '/scheduling/plan/:orderId',
    name: 'scheduling-plan',
    component: () => import('@/views/production/plan/Index.vue'),
    meta: { title: '排程计划', },
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
      {
        path: '/system/alarm/config',
        name: 'alarm-config',
        component: () => import('@/views/system/alarm/Index.vue'),
        meta: {
          permissions: 'system:alarm:config',
          title: '报警配置',
          icon: 'ArchiveBoxIcon'
        },
      },
    ]
  },
  {
    path: '/monitor',
    name: 'monitor',
    component: () => import('@/views/monitor/Index.vue'),
    meta: {
      permissions: 'monitor',
      title: '生产监控',
      icon: 'BuildingOfficeIcon'
    }
  },
  {
    path: '/production',
    meta: { permissions: 'production:manage', title: '生产排程', icon: 'CalendarIcon' },
    children: [
      {
        path: '/production/order/manage',
        name: 'production-order-manage',
        component: () => import('@/views/production/order/Index.vue'),
        meta: {
          permissions: 'production:order:manage',
          title: '工单管理',
          icon: 'ClipboardDocumentListIcon'
        },
      },
      {
        path: '/production/record/view',
        name: 'production-record-view',
        component: () => import('@/views/production/record/Index.vue'),
        meta: {
          permissions: 'production:record:view',
          title: '生产记录',
          icon: 'TableCellsIcon'
        },
      },
    ]
  },
  {
    path: '/equipment',
    meta: { permissions: 'equipment:manage', title: '设备管理', icon: 'CpuChipIcon' },
    children: [
      {
        path: '/equipment/status/view',
        name: 'equipment-status-view',
        component: () => import('@/views/equipment/status/Index.vue'),
        meta: {
          permissions: 'equipment:status:view',
          title: '设备状态',
          icon: 'PowerIcon'
        },
      },
      {
        path: '/equipment/maintenance/view',
        name: 'equipment-maintenance-view',
        component: () => import('@/views/equipment/maintenance/Index.vue'),
        meta: {
          permissions: 'equipment:maintenance:view',
          title: '维护记录',
          icon: 'WrenchScrewdriverIcon'
        },
      }
    ]
  },
  {
    path: '/quality',
    meta: { permissions: 'quality:manage', title: '质量管理', icon: 'ShieldCheckIcon' },
    children: [
      {
        path: '/quality/inspection/view',
        name: 'quality-inspection-view',
        component: () => import('@/views/quality/inspection/Index.vue'),
        meta: {
          permissions: 'quality:inspection:view',
          title: '质检检测',
          icon: 'MagnifyingGlassIcon'
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
      },
    ]
  },
  {
    path: '/report',
    meta: { permissions: 'report:manage', title: '生产报表', icon: 'DocumentChartBarIcon' },
    children: [
      {
        path: '/report/daily/view',
        name: 'daily-report-view',
        component: () => import('@/views/report/daily/Index.vue'),
        meta: {
          permissions: 'report:daily:view',
          title: '生产日报',
          icon: 'SunIcon'
        },
      },
      {
        path: '/report/quality/view',
        name: 'quality-report-view',
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
  // 开始加载进度条
  NProgress.start()

  // 白名单直接放行
  if (whiteList.includes(to.path)) {
    next()
    NProgress.done()
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
        router.addRoute({ path: '/:pathMatch(.*)*', redirect: '/404', name: 'catchAll' })

        // 重新导航到目标路由
        next({ ...to, replace: true })
      } catch (error) {
        NProgress.done()  // 出现错误时也结束进度条
        // 获取信息失败则退出登录
        await store.dispatch('user/logout')
        next(`/login?redirect=${to.path}`)
      }
    } else {
      // 路由存在性检查
      if (!to.matched.length) {
        next('/404')
        return
      }
      next()
    }
  }
})

router.afterEach(() => {
  NProgress.done()  // 结束加载进度条
})

router.onError((error) => {
  NProgress.done()  // 出现错误时也结束进度条
  console.error('路由错误:', error)
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