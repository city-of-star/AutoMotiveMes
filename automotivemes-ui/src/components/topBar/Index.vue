<template>
  <div class="topBar" :style="{ left: sidebarWidth, width: contentWidth }">
    <div class="left-section">
      <el-button class="collapse-btn" @click="toggleCollapse">
        <el-icon :size="20"><component :is="isCollapse ? Expand : Fold" /></el-icon>
      </el-button>
    </div>

    <div class="right-section">
      <el-space :size="20">
        <el-tag type="info">{{ currentTime }}</el-tag>
        <el-tooltip effect="dark" content="全屏" placement="bottom">
          <el-button @click="toggleFullScreen">
            <el-icon :size="18"><FullScreen /></el-icon>
          </el-button>
        </el-tooltip>
        <el-dropdown trigger="click">
          <div class="user-info">
            <el-avatar :size="30" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"/>
            <span class="username">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleCommand('profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="handleCommand('logout')">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-space>
    </div>
  </div>
</template>

<script setup>
import { Expand, Fold, FullScreen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const currentTime = ref('')  // 当前时间
let timer = null

// 计算属性
const isCollapse = computed(() => store.state.app.sidebar.opened)
const sidebarWidth = computed(() => `${isCollapse.value ? store.state.app.sidebar.widthFold : store.state.app.sidebar.widthExpend}px`)
const contentWidth = computed(() => `calc(100% - ${sidebarWidth.value})`)
const username = computed(() => store.state.user.username || '管理员')

// 时间更新
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

// 生命周期
onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onBeforeUnmount(() => {
  clearInterval(timer)
})

// 切换折叠状态
const toggleCollapse = () => {
  store.commit('app/TOGGLE_SIDEBAR')
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCommand = (command) => {
  switch(command) {
    case 'logout':
      store.dispatch('user/logout')
      break
    case 'profile':
      ElMessage.info('打开个人中心')
      break
  }
}
</script>

<style scoped lang="scss">
.topBar {
  position: fixed;
  top: 0;
  height: 60px;
  display: flex;
  background-color: white;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  transition: all 0.3s ease-in-out;
  z-index: 999;
  box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);

  .left-section {
    display: flex;
    align-items: center;
    gap: 20px;

    .collapse-btn {
      border: none;
      background: transparent;
      padding: 8px;
      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }

    .system-title {
      font-size: 20px;
      font-weight: 500;
      margin: 0;
      background: linear-gradient(45deg, #409eff, #79bbff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }

  .right-section {
    margin-right: 30px;
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 5px 10px;
      border-radius: 4px;
      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }

      .username {
        font-size: 14px;
      }
    }
  }
}
</style>