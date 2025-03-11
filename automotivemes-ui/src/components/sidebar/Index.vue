<template>
  <aside class="sidebar">
    <div class="logo-area">
      <h1>智慧工厂MES</h1>
    </div>

    <nav class="menu-container">
      <router-link
          v-for="item in filteredRoutes"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ 'active': $route.name === item.name }"
      >
        <component :is="routeIcons[item.name]" class="menu-icon" />
        <span class="menu-text">{{ routeNames[item.name] }}</span>
      </router-link>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import router from '@/router'
import {
  HomeIcon,
  CpuChipIcon,
  TruckIcon,
  CalendarIcon,
  BeakerIcon,
  ChartBarIcon,
  ClipboardDocumentIcon,
} from '@heroicons/vue/24/outline'

// 路由显示名称映射
const routeNames = {
  'home': '首页',
  'equipment-monitor': '设备监控',
  'material-track': '物料追踪',
  'production-plan': '生产计划',
  'quality-inspect': '质量检测',
  'report-analysis': '报告分析',
  'work-order': '工单管理',
}

// 路由图标映射
const routeIcons = {
  'home': HomeIcon,
  'equipment-monitor': CpuChipIcon,
  'material-track': TruckIcon,
  'production-plan': CalendarIcon,
  'quality-inspect': BeakerIcon,
  'report-analysis': ChartBarIcon,
  'work-order': ClipboardDocumentIcon,
}

const $route = useRoute()

// 过滤需要显示的路由
const filteredRoutes = computed(() => {
  return router.options.routes.filter(route =>
      !['login', 'register'].includes(route.name) && route.meta?.requiresAuth
  )
})
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 250px;
  height: 100vh;
  background: #ffffff;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.25);
  padding: 20px 0;
}

.logo-area {
  padding: 0 24px;
  margin-bottom: 32px;
}

.logo-area h1 {
  font-size: 20px;
  color: #1a1a1a;
  font-weight: 600;
}

.menu-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  color: #606266;
  text-decoration: none;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #1a1a1a;
}

.menu-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.menu-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
}

.menu-text {
  font-size: 14px;
}
</style>