<template>
  <div class="app-container">
    <!-- 固定定位元素 -->
    <Sidebar />
    <TopBar />
    <TabsNav />

    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="router-view-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import Sidebar from '@/components/sidebar/Index.vue'
import TopBar from '@/components/topBar/Index.vue'
import TabsNav from '@/components/tabsNav/Index.vue'
</script>

<style>
.app-container {
  position: relative;
}

.main-content {
  margin-left: 250px;
  margin-top: 56px;
  padding: 24px;
  min-height: calc(100vh - 112px);
  background: #f8f9fa;
}

.router-view-container {
  overflow: auto;
  max-height: calc(100vh - 56px - 48px);
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
</style>