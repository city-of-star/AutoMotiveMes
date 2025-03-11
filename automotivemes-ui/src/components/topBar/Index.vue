<template>
  <header class="top-bar">
    <!-- 生产状态区 -->
    <div class="production-status">
      <div class="status-item">
        <ClockIcon class="status-icon" />
        <span class="label">当前班次：</span>
        <span class="value">{{ currentShift }}</span>
      </div>

      <div class="status-item highlight">
        <ChartBarIcon class="status-icon" />
        <span class="label">生产目标：</span>
        <span class="value">{{ production.current }}/{{ production.target }}</span>
      </div>

      <div class="status-item" :class="{ 'warning': equipmentOEE < 80 }">
        <CpuChipIcon class="status-icon" />
        <span class="label">设备OEE：</span>
        <span class="value">{{ equipmentOEE }}%</span>
      </div>
    </div>

    <!-- 控制功能区 -->
    <div class="control-group">
      <!-- 报警通知 -->
      <div class="control-item" @click="toggleAlerts">
        <BellIcon class="control-icon" />
        <span class="badge" v-if="unreadAlerts > 0">{{ unreadAlerts }}</span>
        <transition name="slide-fade">
          <div v-if="showAlertsDropdown" class="alerts-dropdown">
            <div v-for="alert in recentAlerts" :key="alert.id" class="alert-item">
              <span class="alert-level" :class="alert.level">{{ alert.level }}</span>
              <span class="alert-message">{{ alert.message }}</span>
              <span class="alert-time">{{ formatTime(alert.timestamp) }}</span>
            </div>
            <div v-if="recentAlerts.length === 0" class="empty-alerts">
              暂无未读报警
            </div>
          </div>
        </transition>
      </div>

      <!-- 全屏控制 -->
      <div class="control-item" @click="toggleFullscreen">
        <ArrowsPointingOutIcon class="control-icon" />
      </div>

      <!-- 用户菜单 -->
      <div class="control-item" @click="toggleUserMenu">
        <div class="user-profile">
          <span class="user-name">{{ userName }}</span>
          <ChevronDownIcon class="user-menu-icon" />
        </div>
        <transition name="slide-fade">
          <div v-if="showUserMenu" class="user-menu-dropdown">
            <div class="menu-item" @click="handleUserAction('profile')">
              <UserCircleIcon class="menu-icon" /> 个人中心
            </div>
            <div class="menu-item" @click="handleUserAction('settings')">
              <Cog6ToothIcon class="menu-icon" /> 系统设置
            </div>
            <div class="menu-item" @click="handleUserAction('logout')">
              <ArrowLeftOnRectangleIcon class="menu-icon" /> 退出登录
            </div>
          </div>
        </transition>
      </div>

      <!-- 语言切换 -->
      <div class="control-item">
<!--        <LanguageSwitcher />-->
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import {
  ClockIcon,
  ChartBarIcon,
  CpuChipIcon,
  BellIcon,
  ArrowsPointingOutIcon,
  ChevronDownIcon,
  UserCircleIcon,
  Cog6ToothIcon,
  ArrowLeftOnRectangleIcon
} from '@heroicons/vue/24/outline'
// import LanguageSwitcher from './LanguageSwitcher.vue'

// 生产数据
const currentShift = ref('早班 08:00 - 16:00')
const production = ref({ current: 1520, target: 2000 })
const equipmentOEE = ref(85.6)

// 报警管理
const unreadAlerts = ref(3)
const showAlertsDropdown = ref(false)
const recentAlerts = ref([
  { id: 1, level: 'critical', message: '#3冲压机温度超限', timestamp: Date.now() - 3600000 },
  { id: 2, level: 'warning', message: '涂装车间气压不足', timestamp: Date.now() - 1800000 },
  { id: 3, level: 'info', message: 'AGV#5需要充电', timestamp: Date.now() - 600000 }
])

// 用户菜单
const userName = ref('张工 · 工艺工程师')
const showUserMenu = ref(false)

// 全屏控制
const isFullscreen = ref(false)
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 报警面板切换
const toggleAlerts = () => {
  showAlertsDropdown.value = !showAlertsDropdown.value
  if (showAlertsDropdown.value) {
    unreadAlerts.value = 0
  }
}

// 用户菜单切换
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

// 全局点击监听
const handleClickOutside = (event) => {
  if (!event.target.closest('.control-item')) {
    showAlertsDropdown.value = false
    showUserMenu.value = false
  }
}

// 时间格式化
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 用户操作处理
const handleUserAction = (action) => {
  switch (action) {
    case 'logout':
      // 处理退出登录逻辑
      break
    case 'profile':
      // 跳转个人中心
      break
    case 'settings':
      // 打开系统设置
      break
  }
  showUserMenu.value = false
}

// 生命周期钩子
onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.top-bar {
  height: 56px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 250px; /* 侧边栏宽度 */
  right: 0;
  z-index: 1000;
}

.production-status {
  display: flex;
  gap: 32px;
  align-items: center;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.status-icon {
  width: 18px;
  height: 18px;
}

.highlight .value {
  color: #409eff;
  font-weight: 600;
}

.warning .value {
  color: #e6a23c;
}

.control-group {
  display: flex;
  gap: 24px;
  align-items: center;
}

.control-item {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.control-icon {
  width: 20px;
  height: 20px;
  color: #606266;
}

.badge {
  background: #f56c6c;
  color: white;
  border-radius: 10px;
  padding: 2px 6px;
  font-size: 12px;
  position: absolute;
  top: -6px;
  right: -8px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  color: #303133;
  font-size: 14px;
  min-width: 120px;
}

/* 下拉菜单通用样式 */
.alerts-dropdown, .user-menu-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  min-width: 280px;
  max-height: 60vh;
  overflow-y: auto;
  margin-top: 8px;
}

.alert-item {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  display: grid;
  grid-template-columns: 60px 1fr 50px;
  gap: 8px;
  font-size: 13px;
}

.alert-level {
  border-radius: 4px;
  padding: 2px 4px;
  text-align: center;
  &.critical { background: #fde2e2; color: #f56c6c; }
  &.warning { background: #fdf6ec; color: #e6a23c; }
  &.info { background: #f0f9eb; color: #67c23a; }
}

.user-menu-dropdown {
  width: 160px;
  padding: 8px 0;
}

.menu-item {
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  &:hover {
    background: #f5f7fa;
  }
}

.menu-icon {
  width: 16px;
  height: 16px;
}

/* 过渡动画 */
.slide-fade-enter-active {
  transition: all 0.2s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.15s ease-in;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>