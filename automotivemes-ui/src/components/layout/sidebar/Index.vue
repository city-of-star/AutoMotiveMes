<template>
  <div class="sidebar" :style="{ width: sidebarWidth, '--active-color': themeColor }">
    <div v-if="!isCollapse" class="logo-container">
      <img class="logo" src="../../../assets/logo.png" alt="logo" />
    </div>
    <h3 v-if="!isCollapse" class="title">汽车 MES 生产监控中心</h3>
    <el-menu
        :default-active="$route.path"
        class="el-menu-vertical-demo"
        :collapse="isCollapse"
        :unique-opened="true"
        style="margin-top: 40px"
        router
    >
      <template v-for="route in routes" :key="route.path">
        <!-- 有子路由的情况 -->
        <el-sub-menu v-if="route.children" :index="route.path">
          <template #title>
            <el-icon><component :is="getIcon(route.meta?.icon)" /></el-icon>
            <span>{{ route.meta?.title || route.name }}</span>
          </template>
          <el-menu-item v-for="child in route.children" :key="child.path" :index="child.path">
            <el-icon><component :is="getIcon(child.meta?.icon)" /></el-icon>
            <span>{{ child.meta?.title || child.name }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 没有子路由的情况 -->
        <el-menu-item v-else :index="route.path">
          <el-icon><component :is="getIcon(route.meta?.icon)" /></el-icon>
          <span>{{ route.meta?.title || route.name }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useStore } from 'vuex'
import {
  HomeIcon,
  Cog6ToothIcon,
  UserGroupIcon,
  IdentificationIcon,
  BuildingOffice2Icon,
  BriefcaseIcon,
  BuildingOfficeIcon,
  ChartBarIcon,
  ClockIcon,
  CalendarIcon,
  ClipboardDocumentListIcon,
  TableCellsIcon,
  CpuChipIcon,
  PowerIcon,
  WrenchScrewdriverIcon,
  BellAlertIcon,
  ExclamationTriangleIcon,
  ArchiveBoxIcon,
  DocumentChartBarIcon,
  SunIcon,
  ChartPieIcon,
} from '@heroicons/vue/24/outline'

const store = useStore()
const routes = computed(() => store.state.user.routes)
const themeColor = computed(() => store.state.user.themeColor) // 新增主题色计算属性

// 创建图标映射表
const icons = {
  HomeIcon,
  Cog6ToothIcon,
  UserGroupIcon,
  IdentificationIcon,
  BuildingOffice2Icon,
  BriefcaseIcon,
  BuildingOfficeIcon,
  ChartBarIcon,
  ClockIcon,
  CalendarIcon,
  ClipboardDocumentListIcon,
  TableCellsIcon,
  CpuChipIcon,
  PowerIcon,
  WrenchScrewdriverIcon,
  BellAlertIcon,
  ExclamationTriangleIcon,
  ArchiveBoxIcon,
  DocumentChartBarIcon,
  SunIcon,
  ChartPieIcon
}

// 获取图标
const getIcon = (iconName) => {
  return icons[iconName]
}

// 判断侧边栏是展开还是折叠状态
const isCollapse = computed({
  get: () => store.state.app.sidebar.opened,
  set: (val) => store.commit('app/TOGGLE_SIDEBAR', val)
})

// 侧边栏的宽度
const sidebarWidth = computed(() =>
    isCollapse.value ? store.state.app.sidebar.widthFold + 'px' : store.state.app.sidebar.widthExpend + 'px'
)
</script>

<style scoped lang="scss">
@import '@/assets/styles/layout';

@keyframes slideInLeft {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  transition: width 0.3s ease-in-out;
  z-index: 1000;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);

  .logo-container {
    width: 250px;
    margin-top: 10px;
    display: flex;
    justify-content: center;
    align-items: center;

    .logo {
      width: 100px;
      height: 100px;
      animation: slideInLeft 0.5s ease-out forwards;
    }
  }

  .title {
    width: 250px;
    text-align: center;
    margin-top: 10px;
    animation: slideInLeft 0.5s ease-out forwards;
  }

  /* 激活路由样式（注意深度选择器语法） */
  ::v-deep .el-menu {
    .el-menu-item.is-active,
    .el-sub-menu.is-active > .el-sub-menu__title {
      color: var(--active-color);  /* 使用CSS变量 */
      .el-icon {
        color: var(--active-color);  /* 使用CSS变量 */
      }
    }

    .el-sub-menu .el-menu-item.is-active {
      color: var(--active-color);  /* 使用CSS变量 */
      .el-icon {
        color: var(--active-color);  /* 使用CSS变量 */
      }
    }
  }
}
</style>
