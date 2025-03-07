<template>
  <div class="tabs-container">
    <div class="scroll-wrapper">
      <div
          v-for="tab in visitedTabs"
          :key="tab.fullPath"
          class="tab-item"
          :class="{
          'active': isActive(tab),
          'pinned': tab.meta.affix,
          'unsaved': hasUnsavedChanges(tab)
        }"
          @contextmenu.prevent="openContextMenu($event, tab)"
      >
        <router-link :to="tab.fullPath" class="tab-link">
          <span class="title">
            <component
                :is="tab.meta.icon || 'DocumentTextIcon'"
                class="tab-icon"
            />
            {{ tab.meta.title }}
            <span v-if="tab.params.orderId" class="tag">#{{ tab.params.orderId }}</span>
          </span>
          <XMarkIcon
              v-if="!tab.meta.affix"
              class="close-btn"
              @click.stop="closeTab(tab)"
          />
        </router-link>
      </div>
    </div>

    <ContextMenu
        v-model:visible="contextMenu.visible"
        :position="contextMenu.position"
        :current-tab="contextMenu.currentTab"
        @refresh="refreshTab"
        @close-others="closeOtherTabs"
        @close-right="closeRightTabs"
        @pin="togglePinTab"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useStore } from 'vuex';
// import { DocumentTextIcon, XMarkIcon } from '@heroicons/vue/24/outline';
// import ContextMenu from './TabContextMenu.vue';

// 获取路由和 store 实例
const route = useRoute();
const store = useStore();

// 获取 visitedTabs 和 unsavedChanges
const visitedTabs = computed(() => store.state.tabsNav.visitedTabs);
const unsavedChanges = computed(() => store.getters['tabs/unsavedChanges']);

// 定义上下文菜单状态
const contextMenu = ref({
  visible: false,
  position: { x: 0, y: 0 },
  currentTab: null
});

// 判断是否激活
const isActive = (tab) => {
  return tab.path === route.path;
};

// 判断是否有未保存的更改
const hasUnsavedChanges = (tab) => {
  return unsavedChanges.value.has(tab.fullPath);
};

// 打开上下文菜单
const openContextMenu = (e, tab) => {
  contextMenu.value = {
    visible: true,
    position: { x: e.clientX, y: e.clientY },
    currentTab: tab
  };
};

// 刷新标签页
const refreshTab = () => {
  const fullPath = contextMenu.value.currentTab.fullPath;
  store.commit('tabs/REFRESH_TAB', fullPath);
};

// 关闭其他标签页
const closeOtherTabs = () => {
  store.commit('tabs/CLOSE_OTHER_TABS', contextMenu.value.currentTab);
};

// 关闭右侧标签页
const closeRightTabs = () => {
  store.commit('tabs/CLOSE_RIGHT_TABS', contextMenu.value.currentTab);
};

// 切换标签页固定状态
const togglePinTab = () => {
  store.commit('tabs/TOGGLE_PIN_TAB', contextMenu.value.currentTab);
};

// 关闭标签页
const closeTab = (tab) => {
  store.dispatch('tabs/closeTab', tab);
};

// 监听路由变化
watch(() => route, (to) => {
  if (to.meta.noTab) return;
  store.commit('tabs/ADD_TAB', {
    path: to.path,
    fullPath: to.fullPath,
    meta: { ...to.meta },
    params: { ...to.params },
    query: { ...to.query }
  });
}, { immediate: true });
</script>

<style scoped>

</style>