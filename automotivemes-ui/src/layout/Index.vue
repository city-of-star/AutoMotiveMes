<template>
  <div class="app-container">
    <!-- 固定定位元素 -->
    <Sidebar />
    <TopBar />
    <TabBar />

    <!-- 主要内容区域 -->
    <main class="main-content" :style="{ 'margin-left': left }">
      <div class="router-view-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/sidebar/Index.vue'
import TopBar from '@/components/topBar/Index.vue'
import TabBar from '@/components/tabBar/Index.vue'

import { computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const left = computed(() =>
    store.state.app.sidebar.opened ? store.state.app.sidebar.widthFold - 8 + 'px' : store.state.app.sidebar.widthExpend - 8 + 'px'
)
</script>

<style>
.app-container {

}

.main-content {
  margin-left: 250px;
  margin-top: 100px;
  padding: 20px;
  transition: margin-left 0.3s ease-in-out;
  background-color: #ececec;
}

.router-view-container {
  background-color: #ffffff;
  padding: 20px;
}

</style>