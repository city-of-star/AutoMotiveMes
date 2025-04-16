<template>
  <div class="app-container">
    <!-- 固定定位元素 -->
    <Sidebar />
    <TopBar />
    <TabBar />

    <!-- 主要内容区域 -->
    <main class="main-content" :style="{ 'margin-left': left }">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/layout/sidebar/Index.vue'
import TopBar from '@/components/layout/topBar/Index.vue'
import TabBar from '@/components/layout/tabBar/Index.vue'

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
}

</style>