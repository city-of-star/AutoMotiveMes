<template>
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h1>欢迎来到<span class="highlight">汽车 MES 生产监控中心</span></h1>
        <p class="subtitle">科技驱动未来 · 智能引领生产</p>
      </div>
      <img src="@/assets/logo.png" alt="公司Logo" class="company-logo">
    </div>

    <!-- 数据概览卡片 -->
    <div class="dashboard-grid">

      <!-- 快捷操作入口 -->
      <el-card class="quick-actions">
        <template #header>
          <div class="card-header">
            <el-icon><MagicStick /></el-icon>
            <span>快捷入口</span>
          </div>
        </template>
        <div class="action-grid">
          <div v-for="action in quickActions" :key="action.name" class="action-item">
            <el-button
                circle
                :icon="action.icon"
                :type="action.type"
                @click="router.push(action.path)"
            />
            <span class="action-label">{{ action.label }}</span>
          </div>
        </div>
      </el-card>

      <!-- 通知公告 -->
      <el-card class="announcements">
        <template #header>
          <div class="card-header">
            <el-icon><Bell /></el-icon>
            <span>最新公告</span>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
              v-for="(item, index) in notices"
              :key="index"
              :timestamp="item.time"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 生产任务总览 -->
      <el-card class="production-overview">
        <template #header>
          <div class="card-header">
            <el-icon><DataAnalysis /></el-icon>
            <span>生产总览</span>
          </div>
        </template>
        <div class="overview-items">
          <div class="overview-item">
            <div class="overview-icon order"></div>
            <div class="overview-text">
              <h3>当前工单</h3>
              <p>智能座舱组装订单</p>
            </div>
          </div>
          <div class="overview-item">
            <div class="overview-icon vehicle"></div>
            <div class="overview-text">
              <h3>主推车型</h3>
              <p>风神L7新能源系列</p>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 公司简介 -->
    <el-card class="company-info">
      <template #header>
        <div class="card-header">
          <el-icon><OfficeBuilding /></el-icon>
          <span>关于我们</span>
        </div>
      </template>
      <div class="info-content">
        <img src="@/assets/logo.png" alt="智能工厂" class="factory-image">
        <div class="info-text">
          <h3>汽车智能制造基地</h3>
          <p>成立于2005年，国家级智能制造示范工厂，占地面积120万平方米，拥有全自动生产线12条，年产能达45万辆。</p>
          <div class="info-tags">
            <el-tag type="info">ISO 9001认证</el-tag>
            <el-tag type="info">国家级技术中心</el-tag>
            <el-tag type="info">绿色工厂</el-tag>
          </div>
        </div>
      </div>
    </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  MagicStick,
  Bell,
  DataAnalysis,
  OfficeBuilding
} from '@element-plus/icons-vue'
import {
  ClipboardDocumentListIcon,
  MagnifyingGlassIcon,
  ExclamationTriangleIcon,
  PowerIcon
} from "@heroicons/vue/24/outline";

const router = useRouter()

// 快捷操作配置
const quickActions = ref([
  { label: '工单创建', icon: ClipboardDocumentListIcon, type: 'primary', path: '/production/order/manage' },
  { label: '质检管理', icon: MagnifyingGlassIcon, type: 'success', path: '/quality/inspection/view' },
  { label: '报警处理', icon: ExclamationTriangleIcon, type: 'warning', path: '/alarm/current' },
  { label: '设备监控', icon: PowerIcon, type: 'danger', path: '/equipment/status/view' }
])

// 公告数据
const notices = ref([
  { time: '2025-04-15', content: '五一假期生产安排通知' },
  { time: '2025-04-10', content: '智能制造系统升级公告' },
  { time: '2025-04-08', content: '安全月生产规范培训通知' }
])

</script>

<style lang="scss" scoped>
.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #409eff, #337ecc);
  padding: 2rem;
  border-radius: 8px;
  margin-bottom: 20px;
  color: white;

  .highlight {
    color: #ffd04b;
    margin-left: 10px;
  }

  .subtitle {
    font-size: 1.2rem;
    opacity: 0.9;
  }

  .company-logo {
    height: 80px;
    filter: brightness(0) invert(1);
  }
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;

  .el-icon {
    font-size: 1.3rem;
  }
}

.quick-actions {
  .action-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;

    .action-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .el-button {
        width: 60px;
        height: 60px;
        font-size: 24px;
        margin-bottom: 8px;
      }

      .action-label {
        font-size: 0.9rem;
        color: #666;
      }
    }
  }
}

.announcements {
  .el-timeline {
    padding-left: 10px;
  }
}

.company-info {
  .info-content {
    display: flex;
    gap: 30px;
    align-items: center;

    .factory-image {
      width: 300px;
      border-radius: 6px;
    }

    .info-text {
      flex: 1;

      h3 {
        margin-top: 0;
        color: #333;
      }

      .info-tags {
        margin-top: 15px;
        display: flex;
        gap: 10px;
      }
    }
  }
}

.production-overview {
  .overview-items {
    display: grid;
    gap: 20px;

    .overview-item {
      display: flex;
      align-items: center;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 6px;

      .overview-icon {
        width: 50px;
        height: 50px;
        margin-right: 15px;
        background-size: contain;
      }

      h3 {
        margin: 0 0 8px;
        font-size: 1.1rem;
      }

      p {
        margin: 0;
        color: #666;
      }
    }
  }
}
</style>