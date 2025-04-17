<template>
  <div class="monitor-container">
    <!-- 状态概览 -->
    <div class="status-summary">
      <el-row :gutter="20">
        <el-col
            v-for="stat in statusSummary"
            :key="stat.status"
            :xs="24"
            :sm="12"
            :md="6"
        >
          <el-card shadow="hover" class="summary-card">
            <div class="card-content">
              <div class="icon" :style="{backgroundColor: stat.color}">
                <el-icon :size="28" color="white">
                  <component :is="getStatusIcon(stat.status)" />
                </el-icon>
              </div>
              <div class="stats">
                <div class="count">{{ stat.count }}</div>
                <div class="label">{{ stat.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 主内容区 -->
    <el-row :gutter="20" class="main-content">
      <!-- 设备列表 -->
      <el-col :xs="24" :sm="8">
        <el-card class="device-list">
          <template #header>
            <div class="list-header">
              <span>设备列表</span>
              <el-input
                  v-model="searchKeyword"
                  placeholder="搜索设备..."
                  clearable
                  suffix-icon="Search"
                  style="width: 200px"
              />
            </div>
          </template>

          <el-table
              :data="filteredDevices"
              highlight-current-row
              @current-change="handleSelectDevice"
              height="calc(100vh - 320px)"
              v-loading="loading"
          >
            <el-table-column prop="equipmentCode" label="设备编号" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{row}">
                <el-tag :type="STATUS_CONFIG[row.status]?.type || 'info'" size="small">
                  {{ STATUS_CONFIG[row.status]?.label || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="equipmentName" label="设备名称" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 设备详情 -->
      <el-col :xs="24" :sm="16">
        <el-card v-if="currentDevice" class="device-detail">
          <template #header>
            <div class="detail-header">
              <h3>{{ currentDevice.equipmentName }} ({{ currentDevice.equipmentCode }})</h3>
              <el-tag :type="STATUS_CONFIG[currentDevice.status]?.type || 'info'" size="large">
                {{ STATUS_CONFIG[currentDevice.status]?.label || '未知' }}
              </el-tag>
            </div>
          </template>

          <!-- 实时参数仪表盘 -->
          <el-row v-loading="!currentParams.length" :gutter="15" class="param-dashboard">
            <el-col
                v-for="(param, index) in currentParams"
                :key="index"
                :xs="24"
                :sm="12"
                :md="8"
            >
              <el-card shadow="hover" class="param-card">
                <div class="param-content">
                  <div class="param-header">
                    <span class="param-name">{{ param.paramName }}</span>
                    <el-icon
                        v-if="!param.isNormal"
                        color="#F56C6C"
                        :size="18"
                    >
                      <Warning />
                    </el-icon>
                  </div>
                  <div
                      class="param-value"
                      :class="{ 'abnormal': !param.isNormal }"
                  >
                    {{ param.paramValue?.toFixed(2) ?? '--' }}
                    <span class="unit">{{ param.unit }}</span>
                  </div>
                  <div class="param-time">
                    {{ formatTime(param.collectTime) }}
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 参数趋势图表 -->
          <div ref="chartContainer" class="param-trend-chart"></div>
        </el-card>

        <el-card v-else class="empty-card">
          <el-empty description="请从左侧列表选择设备" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElIcon } from 'element-plus'
import { Monitor, Connection, Setting, Warning } from '@element-plus/icons-vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import * as echarts from 'echarts'
import axios from '@/utils/axios'

// 状态配置
const STATUS_CONFIG = {
  1: { label: '正常', type: 'success', icon: Monitor },
  2: { label: '待机', type: 'info', icon: Connection },
  3: { label: '维护', type: 'warning', icon: Setting },
  4: { label: '报废', type: 'danger', icon: Warning }
}

// 响应式状态
const devices = ref([])
const searchKeyword = ref('')
const currentDeviceId = ref(null)
const paramCache = new Map()
const chartContainer = ref(null)
const chartInstance = ref(null)
const stompClient = ref(null)
const loading = ref(true)
let chartResizeHandler = null

// 计算属性
const statusSummary = computed(() => {
  if (!Array.isArray(devices.value)) return []

  return Object.entries(STATUS_CONFIG).map(([status, config]) => {
    const count = devices.value.reduce((acc, cur) =>
        cur.status === Number(status) ? acc + 1 : acc, 0
    )
    return {
      status: Number(status),
      label: config.label,
      count,
      color: config.type === 'success' ? '#67C23A' :
          config.type === 'warning' ? '#E6A23C' :
              config.type === 'danger' ? '#F56C6C' : '#909399'
    }
  })
})

const filteredDevices = computed(() => {
  if (!Array.isArray(devices.value)) return []
  const keyword = searchKeyword.value.toLowerCase()
  return devices.value.filter(d =>
      d.equipmentCode?.toLowerCase().includes(keyword) ||
      d.equipmentName?.toLowerCase().includes(keyword)
  )
})

const currentDevice = computed(() =>
    devices.value.find(d => d.equipmentId === currentDeviceId.value)
)

const currentParams = computed(() => {
  if (!currentDevice.value) return []
  const params = paramCache.get(currentDevice.value.equipmentId) || {}
  return Object.values(params)
      .map(arr => arr[arr.length - 1])
      .filter(Boolean)
      .filter(p => p?.paramName && p?.paramValue !== undefined)
})

// 方法
const getStatusIcon = (status) => {
  return STATUS_CONFIG[status]?.icon || Monitor
}

const formatTime = (timeStr) => {
  try {
    return new Date(timeStr).toLocaleTimeString('zh-CN', {
      hour12: false,
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch {
    return '--:--:--'
  }
}

const initializeChart = () => {
  try {
    if (chartContainer.value && !chartInstance.value) {
      chartInstance.value = echarts.init(chartContainer.value)
      const option = {
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            let html = `<div class="chart-tooltip">
              <div class="time">${params[0].axisValue}</div>`
            params.forEach(p => {
              html += `<div class="param-item">
                <span class="color-dot" style="background:${p.color}"></span>
                <span class="param-name">${p.seriesName}:</span>
                <span class="param-value">${p.value} ${p.data.unit}</span>
              </div>`
            })
            html += '</div>'
            return html
          }
        },
        legend: {
          type: 'scroll',
          bottom: 0,
          itemWidth: 14,
          itemHeight: 8
        },
        grid: {
          top: 30,
          left: 20,
          right: 20,
          bottom: 40,
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false
        },
        yAxis: { type: 'value' },
        dataZoom: [{
          type: 'inside',
          start: 80,
          end: 100
        }]
      }
      chartInstance.value.setOption(option)

      // 添加窗口resize监听
      chartResizeHandler = () => chartInstance.value?.resize()
      window.addEventListener('resize', chartResizeHandler)
    }
  } catch (error) {
    console.error('图表初始化失败:', error)
  }
}

const updateChartData = () => {
  if (!currentDevice.value || !chartInstance.value) return

  try {
    const deviceParams = paramCache.get(currentDevice.value.equipmentId) || {}
    const seriesData = []
    const xAxisSet = new Set()

    Object.entries(deviceParams).forEach(([paramName, records]) => {
      const validRecords = records
          .filter(r => r?.collectTime && !isNaN(new Date(r.collectTime)))
          .slice(-30)

      if (validRecords.length === 0) return

      const data = validRecords.map(r => r.paramValue)
      validRecords.forEach(r => xAxisSet.add(new Date(r.collectTime).toLocaleTimeString()))

      seriesData.push({
        name: paramName,
        type: 'line',
        data,
        showSymbol: false,
        smooth: true,
        lineStyle: { width: 2 }
      })
    })

    if (seriesData.length > 0) {
      chartInstance.value.setOption({
        xAxis: { data: Array.from(xAxisSet) },
        series: seriesData
      }, { notMerge: true })
    }
  } catch (error) {
    console.error('图表更新失败:', error)
  }
}

const handleSelectDevice = (device) => {
  currentDeviceId.value = device?.equipmentId || null
  nextTick(() => {
    updateChartData()
    chartInstance.value?.resize()
  })
}

const handleWebSocketMessage = (message) => {
  try {
    const paramData = JSON.parse(message.body)

    // 数据校验
    if (!paramData?.equipmentId ||
        !paramData?.paramName ||
        paramData.paramValue === undefined ||
        !paramData.collectTime) {
      console.warn('收到无效参数:', paramData)
      return
    }

    // 更新缓存
    const deviceParams = paramCache.get(paramData.equipmentId) || {}
    const paramRecords = deviceParams[paramData.paramName] || []

    // 保留最近200条
    const newRecords = [...paramRecords, paramData].slice(-200)
    deviceParams[paramData.paramName] = newRecords
    paramCache.set(paramData.equipmentId, deviceParams)

    // 节流更新（500ms）
    if (paramData.equipmentId === currentDeviceId.value) {
      const now = Date.now()
      if (!currentDevice.value.lastUpdate || now - currentDevice.value.lastUpdate > 500) {
        updateChartData()
        currentDevice.value.lastUpdate = now
      }
    }
  } catch (error) {
    console.error('处理WebSocket消息失败:', error)
  }
}

const connectWebSocket = () => {
  try {
    const socket = new SockJS('http://localhost:3000/mes-websocket')
    stompClient.value = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onStompError: (frame) => {
        console.error('WebSocket错误:', frame.headers.message)
      }
    })

    stompClient.value.onConnect = () => {
      stompClient.value.subscribe('/topic/equipment/realtime', handleWebSocketMessage)
    }

    stompClient.value.activate()
  } catch (error) {
    console.error('WebSocket连接失败:', error)
    ElMessage.error('实时数据连接失败')
  }
}

const loadDevices = async () => {
  try {
    loading.value = true
    const data = await axios.get('/equipment/list')
    devices.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('设备加载失败:', error)
    ElMessage.error('设备列表加载失败')
    devices.value = []
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(async () => {
  await loadDevices()
  connectWebSocket()

  nextTick(() => {
    initializeChart()
    // 添加resize监听
    window.addEventListener('resize', () => chartInstance.value?.resize())
  })
})

onBeforeUnmount(() => {
  // 清理资源
  if (stompClient.value) {
    stompClient.value.deactivate()
    stompClient.value = null
  }
  if (chartInstance.value) {
    chartInstance.value.dispose()
    chartInstance.value = null
  }
  if (chartResizeHandler) {
    window.removeEventListener('resize', chartResizeHandler)
  }
})
</script>

<style scoped>
.monitor-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.status-summary {
  margin-bottom: 20px;
}

.summary-card {
  margin-bottom: 0;

  .card-content {
    display: flex;
    align-items: center;
    padding: 12px;
  }

  .icon {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
  }

  .stats {
    .count {
      font-size: 24px;
      font-weight: 600;
      line-height: 1;
    }

    .label {
      color: #666;
      font-size: 14px;
      margin-top: 4px;
    }
  }
}

.main-content {
  margin-top: 20px;
}

.device-list {
  :deep(.el-card__body) {
    padding: 0;
  }
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
}

.param-dashboard {
  margin-bottom: 20px;

  .param-card {
    margin-bottom: 15px;

    .param-content {
      padding: 16px;

      .param-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .param-name {
          color: #666;
          font-size: 14px;
        }
      }

      .param-value {
        font-size: 24px;
        font-weight: bold;
        color: #67C23A;

        &.abnormal {
          color: #F56C6C;
        }

        .unit {
          font-size: 14px;
          color: #999;
          margin-left: 4px;
        }
      }

      .param-time {
        color: #999;
        font-size: 12px;
        margin-top: 8px;
      }
    }
  }
}

.param-trend-chart {
  width: 100%;
  height: 400px;
  min-height: 400px;
}

.empty-card {
  height: calc(100vh - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-card) {
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>