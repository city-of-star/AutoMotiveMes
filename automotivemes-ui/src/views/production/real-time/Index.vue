<template>
  <!-- 状态卡片行 -->
  <el-row :gutter="20" class="mb-8">
    <el-col v-for="(card, index) in statusCards" :key="index" :xs="24" :sm="12" :md="6">
      <el-card
          shadow="hover"
          :class="`stat-card stat-card-${index}`"
          :body-style="{ padding: '20px' }"
      >
        <div class="card-content">
          <div class="icon-wrapper">
            <component :is="card.icon" class="stat-icon" />
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ card.title }}</div>
            <div class="stat-value">{{ card.value }}{{ card.unit }}</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 图表行 -->
  <el-row :gutter="20" class="chart-row">
    <el-col :xs="24" :md="12">
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>设备状态分布</span>
            <el-tag type="info" effect="plain">实时更新</el-tag>
          </div>
        </template>
        <div ref="statusChart" class="chart-container"></div>
      </el-card>
    </el-col>
    <el-col :xs="24" :md="12">
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <div class="chart-header">
            <span>实时报警统计</span>
            <el-tag type="info" effect="plain">最新报警</el-tag>
          </div>
        </template>
        <div ref="alarmChart" class="chart-container"></div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import {onMounted, ref, onBeforeUnmount, computed} from 'vue'
import * as echarts from 'echarts'
import axios from '@/utils/axios'
import {websocket} from "@/utils/websocket"
import {ElMessage} from "element-plus"
import {
  Monitor,
  Warning,
  TrendCharts,
  DataAnalysis
} from '@element-plus/icons-vue'

// 图表引用
const statusChart = ref(null)
const alarmChart = ref(null)

// 响应式数据
const equipmentCount = ref({onlineEquipmentCount: 0, normalEquipmentCount: 0})
const alarmData = ref({total: 0, levels: []})
const productionData = ref({todayOutput: 0, yieldRate: 0})

// 状态卡片配置
const statusCards = computed(() => [
  {
    title: '在线设备',
    value: equipmentCount.value.onlineEquipmentCount,
    icon: Monitor,
    unit: '台',
    color: 'linear-gradient(135deg, #40a9ff 0%, #1890ff 100%)'
  },
  {
    title: '当前报警',
    value: alarmData.value.total,
    icon: Warning,
    unit: '个',
    color: 'linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%)'
  },
  {
    title: '今日产量',
    value: productionData.value.todayOutput,
    icon: TrendCharts,
    unit: '件',
    color: 'linear-gradient(135deg, #73d13d 0%, #52c41a 100%)'
  },
  {
    title: '良品率',
    value: productionData.value.yieldRate,
    icon: DataAnalysis,
    unit: '%',
    color: 'linear-gradient(135deg, #9254de 0%, #722ed1 100%)'
  }
])

// 图表实例
let statusChartInstance = null
let alarmChartInstance = null

// 初始化状态图表
const initStatusChart = () => {
  statusChartInstance = echarts.init(statusChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'middle',
      itemGap: 12,
      textStyle: {
        color: '#666'
      }
    },
    series: [{
      name: '设备状态',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        fontSize: 14
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16
        }
      },
      data: []
    }]
  }
  statusChartInstance.setOption(option)
}

// 初始化报警图表
const initAlarmChart = () => {
  alarmChartInstance = echarts.init(alarmChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['警告', '一般故障', '严重故障'],
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          type: 'dashed'
        }
      }
    },
    series: [{
      name: '报警数量',
      type: 'bar',
      barWidth: '60%',
      itemStyle: {
        borderRadius: 4,
        color: params => {
          const colors = [
            'linear-gradient(180deg, #fff3cd 0%, #ffe594 100%)',
            'linear-gradient(180deg, #ffd8a6 0%, #ffab66 100%)',
            'linear-gradient(180deg, #ffb3b3 0%, #ff6666 100%)'
          ]
          return colors[params.dataIndex]
        }
      },
      emphasis: {
        itemStyle: {
          borderColor: 'rgba(0, 0, 0, 0.2)',
          borderWidth: 2,
          shadowBlur: 8,
          shadowColor: 'rgba(0, 0, 0, 0.1)'
        }
      },
      data: []
    }]
  }
  alarmChartInstance.setOption(option)
}
// 获取设备数据
const fetchEquipmentData = async () => {
  try {
    const res = await axios.get('/equipment/getEquipmentCount')
    equipmentCount.value = res
    updateStatusChart(res)
  } catch (error) {
    ElMessage.error('获取设备数据失败')
  }
}

// 更新状态图表
const updateStatusChart = (data) => {
  const chartData = [
    {value: data.normalEquipmentCount, name: '正常设备'},
    {value: data.onlineEquipmentCount - data.normalEquipmentCount, name: '待机设备'}
  ]
  statusChartInstance.setOption({
    series: [{
      data: chartData,
      itemStyle: {
        color: params => ['#40a9ff', '#bae0ff'][params.dataIndex]
      }
    }]
  })
}

// 获取报警数据
const fetchAlarmData = async () => {
  try {
    const res = await axios.get('/equipment/listRealTimeAlarms')
    alarmData.value.total = res.length
    updateAlarmChart(res)
  } catch (error) {
    ElMessage.error('获取报警数据失败')
  }
}

// 更新报警图表
const updateAlarmChart = (data) => {
  const levels = [0, 0, 0]
  data.forEach(a => levels[a.alarmLevel - 1]++)
  alarmChartInstance.setOption({
    series: [{data: levels}]
  })
}

// 获取生产数据
const fetchProductionData = async () => {
  try {
    const res = await axios.get('/order/statistics')
    productionData.value = {
      todayOutput: res.qualified,
      yieldRate: res.yieldRate.toFixed(1)
    }
  } catch (error) {
    ElMessage.error('获取生产数据失败')
  }
}

// 初始化
onMounted(async () => {
  initStatusChart()
  initAlarmChart()
  await fetchEquipmentData()
  await fetchAlarmData()
  await fetchProductionData()

  window.addEventListener('resize', () => {
    statusChartInstance?.resize()
    alarmChartInstance?.resize()
  })
})

onBeforeUnmount(() => {
  websocket.unsubscribe('/topic/equipment/realtime')
})
</script>

<style lang="scss" scoped>
.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 12px;
  overflow: hidden;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  }

  .card-content {
    display: flex;
    align-items: center;
  }

  .icon-wrapper {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20px;
    background: v-bind('statusCards[0].color');

    .stat-icon {
      font-size: 32px;
      color: white;
    }
  }

  &.stat-card-1 .icon-wrapper {
    background: v-bind('statusCards[1].color');
  }

  &.stat-card-2 .icon-wrapper {
    background: v-bind('statusCards[2].color');
  }

  &.stat-card-3 .icon-wrapper {
    background: v-bind('statusCards[3].color');
  }

  .stat-info {
    flex: 1;
  }

  .stat-title {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }

  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
}

.chart-card {
  margin-bottom: 20px;
  border-radius: 12px;

  :deep(.el-card__header) {
    border-bottom: none;
    padding: 16px 20px;
  }

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
  }
}

.chart-container {
  height: 400px;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 10px;

    .icon-wrapper {
      width: 48px;
      height: 48px;
      margin-right: 12px;

      .stat-icon {
        font-size: 24px;
      }
    }

    .stat-title {
      font-size: 12px;
    }

    .stat-value {
      font-size: 20px;
    }
  }

  .chart-container {
    height: 300px;
  }
}
</style>