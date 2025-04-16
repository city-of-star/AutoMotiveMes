<template>
  <div class="equipment-status-container">
    <!-- 状态概览卡片 -->
    <div class="status-overview">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in statusStats" :key="index">
          <el-card class="status-card" shadow="hover">
            <div class="card-content">
              <div class="icon" :style="{backgroundColor: item.color}">
                <el-icon :size="24"><component :is="item.icon"/></el-icon>
              </div>
              <div class="stats">
                <div class="count">{{ item.count }}</div>
                <div class="label">{{ item.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 设备列表与详情 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="table-header">
              <span>设备列表</span>
              <el-input
                  v-model="searchKey"
                  placeholder="搜索设备"
                  clearable
                  style="width: 200px"
                  @input="filterEquipment"
              />
            </div>
          </template>

          <el-table
              :data="filteredEquipment"
              highlight-current-row
              @current-change="handleEquipmentSelect"
              height="500"
          >
            <el-table-column prop="equipment_code" label="设备编码" width="120"/>
            <el-table-column prop="equipment_name" label="设备名称"/>
            <el-table-column label="状态" width="80">
              <template #default="{row}">
                <el-tag :type="statusTypeMap[row.status]">{{ statusMap[row.status] }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 设备详情 -->
      <el-col :span="16" v-if="currentEquipment">
        <el-card shadow="never">
          <template #header>
            <div class="detail-header">
              <h3>{{ currentEquipment.equipment_name }} - 状态监控</h3>
              <div class="status-indicator">
                当前状态：
                <el-tag :type="statusTypeMap[currentEquipment.status]">
                  {{ statusMap[currentEquipment.status] }}
                </el-tag>
              </div>
            </div>
          </template>

          <!-- 实时参数 -->
          <div class="realtime-params">
            <el-row :gutter="20">
              <el-col :span="8" v-for="param in currentParameters" :key="param.param_name">
                <el-card shadow="hover" class="param-card">
                  <div class="param-content">
                    <div class="param-name">{{ param.param_name }}</div>
                    <div
                        class="param-value"
                        :class="{ 'abnormal': !param.is_normal }"
                    >
                      {{ param.param_value }}
                      <span class="unit">{{ param.unit }}</span>
                    </div>
                    <div class="param-time">{{ param.collect_time }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <!-- 参数趋势图 -->
          <div class="chart-container mt-20">
            <div ref="chartRef" style="height: 300px;" v-if="currentEquipment"></div>
            <el-empty v-else description="请先选择设备" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, nextTick} from 'vue'
import * as echarts from 'echarts'
import { useEventListener } from '@vueuse/core'
import {
  Monitor,
  Connection,
  Setting,
  Warning
} from '@element-plus/icons-vue'

// 状态映射
const statusMap = {
  1: '正常',
  2: '待机',
  3: '维护中',
  4: '已报废'
}

const statusTypeMap = {
  1: 'success',
  2: 'info',
  3: 'warning',
  4: 'danger'
}

// 模拟数据
const equipmentList = ref([
  {
    equipment_id: 1,
    equipment_code: 'WSH-001',
    equipment_name: '焊接机器人',
    status: 1,
    location: '焊接车间/生产线A/工位3',
    parameters_config: {
      temperature: { unit: '℃', min: 20, max: 80 },
      pressure: { unit: 'MPa', min: 0.8, max: 1.2 }
    }
  },
  {
    equipment_id: 2,
    equipment_code: 'SC-002',
    equipment_name: '冲压机',
    status: 2,
    location: '冲压车间/生产线B/工位5',
    parameters_config: {
      pressure: { unit: 'MPa', min: 5, max: 8 },
      speed: { unit: 'rpm', min: 100, max: 300 }
    }
  },
  {
    equipment_id: 3,
    equipment_code: 'JC-003',
    equipment_name: '检测设备',
    status: 3,
    location: '质检车间/线C/工位2',
    parameters_config: {
      accuracy: { unit: '%', min: 95, max: 100 }
    }
  }
])

const statusStats = ref([
  { label: '正常运行', count: 8, icon: Monitor, color: '#67C23A' },
  { label: '待机设备', count: 2, icon: Connection, color: '#909399' },
  { label: '维护中', count: 1, icon: Setting, color: '#E6A23C' },
  { label: '已报废', count: 1, icon: Warning, color: '#F56C6C' }
])

// 当前选中设备
const currentEquipment = ref(null)
const currentParameters = ref([
  { param_name: '温度', param_value: 45.6, unit: '℃', is_normal: true, collect_time: '2024-03-05 14:30' },
  { param_name: '压力', param_value: 1.1, unit: 'MPa', is_normal: true, collect_time: '2024-03-05 14:30' },
  { param_name: '电流', param_value: 32.4, unit: 'A', is_normal: false, collect_time: '2024-03-05 14:30' }
])

// 图表相关
const chartRef = ref(null)
let chartInstance = null

onMounted(() => {
  useEventListener(window, 'resize', () => {
    chartInstance?.resize()
  })

  if (equipmentList.value.length > 0) {
    handleEquipmentSelect(equipmentList.value[0]);
  }
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  const option = {
    title: {
      text: '参数趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        let res = `<div style="min-width: 150px">${params[0].axisValue}<br/>`
        params.forEach(item => {
          res += `${item.marker} ${item.seriesName}: ${item.value} ${item.data.unit || ''}<br/>`
        })
        return res + '</div>'
      }
    },
    legend: {
      data: ['温度', '压力'],
      top: 30
    },
    grid: {
      top: 80,
      bottom: 30
    },
    xAxis: {
      type: 'category',
      data: ['14:00', '14:05', '14:10', '14:15', '14:20', '14:25', '14:30']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value) => `${value} ${currentParameters.value[0].unit}`
      }
    },
    series: [
      {
        name: '温度',
        type: 'line',
        data: [
          { value: 42, unit: '℃' },
          { value: 45, unit: '℃' },
          { value: 48, unit: '℃' },
          { value: 43, unit: '℃' },
          { value: 46, unit: '℃' },
          { value: 44, unit: '℃' },
          { value: 45.6, unit: '℃' }
        ],
        smooth: true,
        lineStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '压力',
        type: 'line',
        data: [
          { value: 1.0, unit: 'MPa' },
          { value: 1.05, unit: 'MPa' },
          { value: 1.1, unit: 'MPa' },
          { value: 1.08, unit: 'MPa' },
          { value: 1.12, unit: 'MPa' },
          { value: 1.09, unit: 'MPa' },
          { value: 1.1, unit: 'MPa' }
        ],
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  chartInstance.setOption(option)
}

// 设备搜索
const searchKey = ref('')
const filteredEquipment = ref([...equipmentList.value])

const filterEquipment = () => {
  filteredEquipment.value = equipmentList.value.filter(e =>
      e.equipment_code.toLowerCase().includes(searchKey.value.toLowerCase()) ||
      e.equipment_name.toLowerCase().includes(searchKey.value.toLowerCase())
  )
}

const handleEquipmentSelect = (equipment) => {
  currentEquipment.value = equipment
  // 模拟动态参数更新
  currentParameters.value = currentParameters.value.map(p => ({
    ...p,
    param_value: (Math.random() * 50 + 20).toFixed(1),
    is_normal: Math.random() > 0.2
  }))

  // 初始化图表
  nextTick(() => {
    if (chartInstance) {
      chartInstance.dispose()
    }
    initChart()
  })
}
</script>

<style scoped>
.equipment-status-container {
  padding: 20px;
}

.status-overview .status-card {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  align-items: center;
}

.card-content .icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stats {
  margin-left: 15px;
}

.stats .count {
  font-size: 24px;
  font-weight: bold;
}

.stats .label {
  color: #666;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.param-card {
  margin-bottom: 10px;
  transition: all 0.3s;
}

.param-card:hover {
  transform: translateY(-2px);
}

.param-content {
  text-align: center;
  padding: 10px;
}

.param-name {
  color: #666;
  margin-bottom: 8px;
  font-size: 14px;
}

.param-value {
  font-size: 24px;
  font-weight: bold;
  color: #67C23A;
  transition: color 0.3s;
}

.param-value.abnormal {
  color: #F56C6C;
}

.unit {
  font-size: 14px;
  color: #999;
  margin-left: 2px;
}

.param-time {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.mt-20 {
  margin-top: 20px;
}

.chart-container {
  position: relative;
}
</style>