<template>
  <div class="tabBar" :style="{ left: left }">
    <div class="tabs-container">
      <div v-for="(tab, index) in tabs"
           :key="tab.path"
           :class="['tab-item', { 'active': currentTabIndex === index }]"
           @click="switchTab(tab, index)"
           @contextmenu.prevent="showContextMenu($event, index)">
        <span class="tab-title">{{ tab.meta?.title || tab.name }}</span>
        <el-icon class="close-icon" @click.stop="closeTab(index)">
          <Close />
        </el-icon>
      </div>
    </div>

    <!-- 右键菜单 -->
    <div v-show="contextMenuVisible"
         class="context-menu"
         :style="{ left: contextMenuX + 'px', top: contextMenuY + 'px' }">
      <div class="menu-item" @click="refreshCurrent">刷新当前</div>
      <div class="menu-item" @click="closeCurrent">关闭当前</div>
      <div class="menu-item" @click="closeOthers">关闭其他</div>
      <div class="menu-item" @click="closeAll">关闭全部</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Close } from '@element-plus/icons-vue'
import { useStore } from 'vuex'

const store = useStore()
const router = useRouter()
const route = useRoute()

// 计算属性
const left = computed(() => store.state.app.sidebar.opened
        ? store.state.app.sidebar.widthFold + 'px'
        : store.state.app.sidebar.widthExpend + 'px'
)

const tabs = computed(() => store.state.tabBar.tabs)
const currentTabIndex = computed(() =>
    tabs.value.findIndex(tab => tab.path === route.path)
)

// 右键菜单相关
const contextMenuVisible = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const contextMenuIndex = ref(-1)

// 监听路由变化
watch(() => route.path, (newPath) => {
  addTab({
    path: newPath,
    name: route.name,
    meta: route.meta,
    fixed: false  // 默认不固定
  })
})

// 初始化时添加首页
onMounted(() => {
  if (tabs.value.length === 0) {
    addTab({
      path: '/',
      name: 'home',
      meta: { title: '首页' },
      fixed: true  // 首页固定
    })
  }
})

// 添加标签页
const addTab = (route) => {
  if (!route.meta?.noTab && !tabs.value.some(tab => tab.path === route.path)) {
    store.commit('tabBar/ADD_TAB', route)
  }
}

// 切换标签页
const switchTab = (tab) => {
  router.push(tab.path)
}

// 关闭标签页
const closeTab = (index) => {
  const tab = tabs.value[index]
  if (tab.fixed) return  // 固定标签页不能关闭
  store.commit('tabBar/REMOVE_TAB', index)

  // 如果关闭的是当前页
  if (tab.path === route.path) {
    const newTabs = tabs.value
    if (newTabs.length > 0) {
      // 跳转到前一个标签
      const prevTab = newTabs[Math.min(index, newTabs.length - 1)]
      router.push(prevTab.path)
    } else {
      router.push('/')
    }
  }
}

// 右键菜单事件
const showContextMenu = (e, index) => {
  contextMenuVisible.value = true
  contextMenuX.value = e.clientX
  contextMenuY.value = e.clientY
  contextMenuIndex.value = index
}

// 刷新当前
const refreshCurrent = () => {
  const currentTab = tabs.value[contextMenuIndex.value]
  router.replace({ path: '/' }).then(() => {

    router.push(currentTab.path)

    // router.push(currentTab.path).then(() => {
    //   window.location.reload();  // 刷新页面
    // });
  })
  contextMenuVisible.value = false
}

// 关闭当前
const closeCurrent = () => {
  closeTab(contextMenuIndex.value)
  contextMenuVisible.value = false
}

// 关闭其他
// const closeOthers = () => {
//   store.commit('tabBar/SET_TABS', [
//     tabs.value[contextMenuIndex.value]
//   ])
//   contextMenuVisible.value = false
// }

const closeOthers = () => {
  const currentTab = tabs.value[contextMenuIndex.value]
  store.commit('tabBar/SET_TABS', [
    ...tabs.value.filter(tab => tab.fixed),
    currentTab
  ])
  contextMenuVisible.value = false
}

// 关闭全部
// const closeAll = () => {
//   store.commit('tabBar/SET_TABS', [])
//   router.push('/')
//   contextMenuVisible.value = false
// }

const closeAll = () => {
  const fixedTabs = tabs.value.filter(tab => tab.fixed)
  store.commit('tabBar/SET_TABS', fixedTabs)
  if (fixedTabs.length > 0) {
    router.push(fixedTabs[0].path)
  } else {
    router.push('/')
  }
  contextMenuVisible.value = false
}

// 点击其他地方隐藏菜单
document.addEventListener('click', () => {
  contextMenuVisible.value = false
})
</script>

<style scoped>
.tabBar {
  position: fixed;
  top: 60px;
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
  z-index: 998;
  overflow-x: auto;
  transition: all 0.3s ease-in-out;
}

.tabs-container {
  display: flex;
  height: 100%;
  padding: 0 10px;
}

.tab-item {
  position: relative;
  display: flex;
  align-items: center;
  min-width: 50px;
  max-width: 150px;
  margin-top: 5px;
  height: 30px;
  padding: 0 20px;
  font-size: 14px;
  border-right: 1px solid #e6e6e6;
  cursor: pointer;
  transition: background 0.3s;
  background: #f5f7fa;
}

.tab-item:hover {
  background: #ebeff5;
}

.tab-item.active {
  background: #fff;
  color: #409eff;
  font-weight: 500;
}

.tab-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.close-icon {
  margin-left: 8px;
  padding: 2px;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-icon:hover {
  background: #c8c9cc;
  color: #fff;
}

.context-menu {
  position: fixed;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  border-radius: 4px;
  z-index: 9999;
}

.menu-item {
  padding: 8px 16px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #409eff;
}
</style>