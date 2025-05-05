<template>
  <div class="equipment-status-container">
    <!-- 状态概览卡片 -->
    <div class="status-overview">
      <el-row :gutter="20">
        <el-col :span="6" v-for="(item, index) in statusStats" :key="index">
          <el-card class="status-card" shadow="hover" :loading="overviewLoading">
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
              />
            </div>
          </template>

          <el-table
              :data="filteredEquipment"
              highlight-current-row
              @current-change="handleEquipmentSelect"
              height="500"
              v-loading="loading"
          >
            <el-table-column prop="equipmentCode" label="设备编码" width="120"/>
            <el-table-column prop="equipmentName" label="设备名称"/>
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
              <h3>{{ currentEquipment.equipmentName }} - 状态监控</h3>
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
              <el-col
                  :span="6"
                  v-for="param in computedCurrentParameters"
                  :key="param.paramName"
              >
                <el-card shadow="hover" class="param-card">
                  <div class="param-content">
                    <div class="param-name">{{ param.paramName }}</div>
                    <div
                        class="param-value"
                        :class="{ 'abnormal': param.isNormal === 0 }"
                    >
                      {{ param.paramValue }}
                      <span class="unit">{{ param.unit }}</span>
                    </div>
                    <div class="param-time">{{ formatTime(param.collectTime) }}</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </div>

          <!-- 参数趋势图 -->
          <div class="chart-container mt-20" v-loading="historyLoading">
            <div ref="chartRef" style="height: 300px;"></div>
          </div>

        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { Monitor, Connection, Setting, Warning } from '@element-plus/icons-vue'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import axios from '@/utils/axios'
import { websocket } from "@/utils/websocket";

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

// 设备数据
const equipmentList = ref([])
const currentEquipment = ref(null)
const currentParameters = ref({})
const loading = ref(true)

// 加载状态变量
const overviewLoading = ref(true)
const historyLoading = ref(false)

// 图表相关
const chartRef = ref(null)
let chartInstance = null
const chartData = ref({
  timestamps: [],
  series: new Map()
})

// 状态统计
const statusStats = computed(() => {
  const stats = {
    1: { count: 0, label: '正常运行', icon: Monitor, color: '#67C23A' },
    2: { count: 0, label: '待机设备', icon: Connection, color: '#909399' },
    3: { count: 0, label: '维护中', icon: Setting, color: '#E6A23C' },
    4: { count: 0, label: '已报废', icon: Warning, color: '#F56C6C' }
  }

  if (equipmentList.value && Array.isArray(equipmentList.value)) {
    equipmentList.value.forEach(equip => {
      const status = equip.status || 2
      stats[status].count++
    })
  }

  return Object.values(stats)
})

// 设备搜索
const searchKey = ref('')
const filteredEquipment = computed(() => {
  const key = searchKey.value.toLowerCase()
  return equipmentList.value.filter(e =>
      e.equipmentCode.toLowerCase().includes(key) ||
      e.equipmentName.toLowerCase().includes(key)
  )
})

// 计算属性转换对象为数组
const computedCurrentParameters = computed(() => {
  return Object.values(currentParameters.value)
})

// 初始化
onMounted(async () => {
  await fetchEquipmentList()
})

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

// 获取设备列表
const fetchEquipmentList = async () => {
  try {
    loading.value = true
    overviewLoading.value = true
    equipmentList.value = await axios.get('/equipment/list')
    if (equipmentList.value.length > 0) {
      await handleEquipmentSelect(equipmentList.value[0])
    }
  } catch (error) {
    ElMessage.error('设备列表加载失败')
    equipmentList.value = []
  } finally {
    loading.value = false
    overviewLoading.value = false
  }
}

// 获取实时设备参数数据
websocket.subscribe('/topic/equipment/realtime', (message) => {
  handleRealtimeData(JSON.parse(message.body));
});

// 处理实时数据
const handleRealtimeData = (data) => {
  if (!currentEquipment.value || data.equipmentId !== currentEquipment.value.equipmentId) return

  // 更新参数列表
  const newParam = {
    ...data,
    collectTime: new Date(data.collectTime)
  }

  // 更新参数对象（保留最新值）
  currentParameters.value = {
    ...currentParameters.value,
    [data.paramName]: {
      ...data,
      collectTime: new Date(data.collectTime)
    }
  }
  // 更新图表数据
  updateChartData(newParam)
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: params => {
        let res = `${params[0].axisValueLabel}<br/>`
        params.forEach(p => {
          res += `${p.marker} ${p.seriesName}: ${p.value} ${p.data.unit}<br/>`
        })
        return res
      }
    },
    legend: {
      data: [],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: []
  }

  chartInstance.setOption(option)
}

// 更新图表数据
const updateChartData = (param) => {
  const timestamp = new Date(param.collectTime).toLocaleTimeString();

  // 正确维护时间轴（保留最新900个点，新数据在末尾）
  chartData.value.timestamps = [
    timestamp,
    ...chartData.value.timestamps.slice(-899),
  ];

  // 更新对应参数的数据序列
  if (!chartData.value.series.has(param.paramName)) {
    chartData.value.series.set(param.paramName, {
      name: param.paramName,
      unit: param.unit,
      data: []
    });
  }

  const series = chartData.value.series.get(param.paramName);
  series.data = [
    { value: param.paramValue, unit: param.unit },
    ...series.data.slice(-899),
  ];

  // 更新图表配置
  const option = {
    xAxis: {
      data: chartData.value.timestamps
    },
    series: Array.from(chartData.value.series.values()).map(s => ({
      name: s.name,
      type: 'line',
      data: s.data,
      showSymbol: false,
      smooth: true
    }))
  };

  chartInstance.setOption(option);
};

// 设备选择处理
const handleEquipmentSelect = async (equipment) => {
  currentEquipment.value = equipment
  currentParameters.value = {}

  // 清空并重新初始化图表
  if (chartInstance) {
    chartInstance.dispose()
  }
  await nextTick()
  initChart()

  // 加载历史数据
  await fetchHistoryData(equipment.equipmentId)
}

// 时间格式化
const formatTime = (time) => {
  return new Date(time).toLocaleTimeString()
}

const fetchHistoryData = async (equipmentId) => {
  historyLoading.value = true
  try {
    let data = await axios.get(`/equipment/historyData/${equipmentId}`);
    data = data.slice(-900); // 只取最后900条

    // 初始化图表数据
    chartData.value.timestamps = []
    chartData.value.series.clear()

    data.forEach(item => {
      const time = new Date(item.collectTime)
      const timeStr = time.toLocaleTimeString()

      // 更新时间轴
      if (!chartData.value.timestamps.includes(timeStr)) {
        chartData.value.timestamps.push(timeStr)
      }

      // 更新系列数据
      if (!chartData.value.series.has(item.paramName)) {
        chartData.value.series.set(item.paramName, {
          name: item.paramName,
          unit: item.unit,
          data: []
        })
      }

      const series = chartData.value.series.get(item.paramName)
      series.data.push({
        value: item.paramValue,
        unit: item.unit
      })
    })

    // 更新图表
    updateChartData()
  } catch (error) {
    console.log(error)
  } finally {
    historyLoading.value = false
  }
}
</script>

<style scoped>
/* 保持原有的样式不变 */
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