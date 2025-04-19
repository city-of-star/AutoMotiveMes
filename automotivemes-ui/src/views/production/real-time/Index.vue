<template>
  <div class="monitor-container">
    <!-- 系统状态概览 -->
    <div class="status-overview">
      <div class="status-card" v-for="item in statusData" :key="item.title">
        <div class="status-indicator" :style="{backgroundColor: item.color}"></div>
        <h3>{{ item.title }}</h3>
        <p class="value">{{ item.value }}</p>
        <p class="unit">{{ item.unit }}</p>
      </div>
    </div>

    <!-- 生产线可视化 -->
    <div class="production-map">
      <h2>生产线实时状态</h2>
      <div class="assembly-line">
        <div class="station" v-for="(station, index) in lineStations" :key="index"
             :class="station.status">
          <span class="station-name">{{ station.name }}</span>
          <div class="station-status"></div>
        </div>
      </div>
    </div>

    <!-- 实时数据图表 -->
    <div class="charts-container">
      <div class="chart-card">
        <h3>生产速率监控</h3>
        <div class="chart" ref="rateChart"></div>
      </div>
      <div class="chart-card">
        <h3>良品率统计</h3>
        <div class="chart" ref="qualityChart"></div>
      </div>
    </div>

    <!-- 报警信息 -->
    <div class="alarm-panel">
      <h2>实时报警信息</h2>
      <div class="alarm-list">
        <div v-for="(alarm, index) in alarms" :key="index" class="alarm-item">
          <span class="time">{{ alarm.time }}</span>
          <span class="station">{{ alarm.station }}</span>
          <span class="message">{{ alarm.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

// 实时状态数据
const statusData = ref([
  { title: '当日产量', value: 0, unit: '台', color: '#4CAF50' },
  { title: '设备稼动率', value: '98%', unit: '百分比', color: '#2196F3' },
  { title: '良品率', value: '99.2%', unit: '百分比', color: '#FF9800' },
  { title: '故障设备', value: 2, unit: '台', color: '#F44336' }
])

// 生产线工位状态
const lineStations = ref([
  { name: '焊接工位', status: 'normal' },
  { name: '涂装工位', status: 'warning' },
  { name: '总装工位', status: 'normal' },
  { name: '检测工位', status: 'error' }
])

// 报警信息
const alarms = ref([
  { time: '09:23:45', station: '涂装机器人03', message: '涂料供应不足' },
  { time: '09:25:12', station: '总装线02', message: '螺栓拧紧异常' }
])

// 图表相关
const rateChart = ref(null)
const qualityChart = ref(null)
let rateChartInstance = null
let qualityChartInstance = null

// 模拟实时数据更新
let timer = null
const updateProductionData = () => {
  statusData.value[0].value = Math.floor(Math.random() * 100 + 500)
  statusData.value[3].value = Math.floor(Math.random() * 3)
}

// 初始化图表
const initCharts = () => {
  rateChartInstance = echarts.init(rateChart.value)
  qualityChartInstance = echarts.init(qualityChart.value)

  const rateOption = {
    xAxis: { type: 'category', data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00'] },
    yAxis: { type: 'value' },
    series: [{ data: [820, 932, 901, 934, 1290, 1330], type: 'line', smooth: true }]
  }

  const qualityOption = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      data: [
        { value: 985, name: '合格品' },
        { value: 15, name: '不合格品' }
      ]
    }]
  }

  rateChartInstance.setOption(rateOption)
  qualityChartInstance.setOption(qualityOption)
}

onMounted(() => {
  initCharts()
  timer = setInterval(updateProductionData, 3000)
})

onBeforeUnmount(() => {
  clearInterval(timer)
  rateChartInstance.dispose()
  qualityChartInstance.dispose()
})
</script>

<style scoped>
.monitor-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.status-overview {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.status-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: center;
}

.status-indicator {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin: 0 auto 10px;
}

.production-map {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.assembly-line {
  display: flex;
  justify-content: space-between;
  padding: 20px 0;
}

.station {
  flex: 1;
  text-align: center;
  padding: 10px;
  border: 2px solid #ddd;
  margin: 0 5px;
  border-radius: 4px;
}

.station-status {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin: 10px auto;
}

.station.normal .station-status { background-color: #4CAF50; }
.station.warning .station-status { background-color: #FFC107; }
.station.error .station-status { background-color: #F44336; }

.charts-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  height: 400px;
}

.chart {
  height: 350px;
}

.alarm-panel {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.alarm-list {
  max-height: 200px;
  overflow-y: auto;
}

.alarm-item {
  padding: 10px;
  margin: 5px 0;
  background: #fff3f3;
  border-left: 4px solid #F44336;
  display: grid;
  grid-template-columns: 100px 150px 1fr;
}

.time { color: #666; }
.message { color: #F44336; }
</style>