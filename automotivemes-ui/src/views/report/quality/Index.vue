<template>
  <div class="quality-container">
    <!-- 头部筛选 -->
    <div class="filter-header">
      <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="loadData"
      />
      <el-select v-model="selectedLine" placeholder="全部生产线" @change="loadData">
        <el-option
            v-for="item in productionLines"
            :key="item.value"
            :label="item.label"
            :value="item.value"
        />
      </el-select>
    </div>

    <!-- 质量概览 -->
    <div class="overview-cards">
      <el-card v-for="item in overviewData" :key="item.title" class="overview-card">
        <div class="card-content">
          <div class="card-icon" :style="{backgroundColor: item.color}">
            <component :is="item.icon" style="font-size: 24px" />
          </div>
          <div class="card-info">
            <div class="value">{{ item.value }}</div>
            <div class="title">{{ item.title }}</div>
            <div class="compare" :class="item.trend">
              <el-icon :class="item.trend"><CaretTop /></el-icon>
              {{ item.rate }}
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 质量分析图表 -->
    <div class="analysis-charts">
      <el-card class="chart-card">
        <h3>缺陷趋势分析</h3>
        <div ref="trendChart" class="chart"></div>
      </el-card>

      <el-card class="chart-card">
        <h3>缺陷类型分布</h3>
        <div ref="pieChart" class="chart"></div>
      </el-card>
    </div>

    <!-- TOP问题分析 -->
    <el-card class="problem-card">
      <div class="card-header">
        <h3>TOP 5 质量问题</h3>
        <el-tag type="warning">当前周期数据</el-tag>
      </div>
      <div ref="barChart" class="chart"></div>
    </el-card>

    <!-- 检测数据明细 -->
    <el-card class="detail-card">
      <div class="card-header">
        <h3>检测数据明细</h3>
        <el-button type="primary" @click="exportData">导出数据</el-button>
      </div>
      <el-table :data="inspectionData" height="400" v-loading="loading">
        <el-table-column prop="time" label="检测时间" width="160" />
        <el-table-column prop="productId" label="产品ID" width="180" />
        <el-table-column prop="line" label="生产线" width="120" />
        <el-table-column prop="defectType" label="缺陷类型" width="150" />
        <el-table-column prop="defectLevel" label="严重等级" width="120">
          <template #default="{row}">
            <el-tag :type="getLevelColor(row.defectLevel)">
              {{ levelMap[row.defectLevel] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人员" width="120" />
        <el-table-column label="操作">
          <template #default="{row}">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="缺陷详情" width="600px">
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="产品ID">{{ currentDetail.productId }}</el-descriptions-item>
        <el-descriptions-item label="检测时间">{{ currentDetail.time }}</el-descriptions-item>
        <el-descriptions-item label="缺陷类型">{{ currentDetail.defectType }}</el-descriptions-item>
        <el-descriptions-item label="严重等级">
          <el-tag :type="getLevelColor(currentDetail.defectLevel)">
            {{ levelMap[currentDetail.defectLevel] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="缺陷描述" :span="2">{{ currentDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="处理措施" :span="2">{{ currentDetail.solution }}</el-descriptions-item>
        <el-descriptions-item label="现场照片" :span="2">
          <img :src="currentDetail.image" class="defect-image" />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { CaretTop } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// 图表实例
const trendChart = ref(null)
const pieChart = ref(null)
const barChart = ref(null)
let trendInstance = null
let pieInstance = null
let barInstance = null

// 模拟数据
const generateData = () => {
  const defects = ['外观划伤', '装配不良', '尺寸偏差', '功能故障', '材料缺陷']
  const data = []
  for (let i = 0; i < 100; i++) {
    data.push({
      time: `2023-07-${String(i%28+1).padStart(2,'0')} ${String(i%24).padStart(2,'0')}:00`,
      productId: `P-${Math.random().toString(36).slice(2,10).toUpperCase()}`,
      line: `生产线 ${(i%4)+1}`,
      defectType: defects[i%5],
      defectLevel: Math.floor(Math.random()*3 +1),
      operator: `操作员${String.fromCharCode(65+(i%5))}`,
      description: '具体缺陷现象描述文本',
      solution: '返工/报废/让步接收等处理措施',
      image: 'https://dummyimage.com/200x150/ccc/000'
    })
  }
  return data
}

// 状态管理
const dateRange = ref([])
const selectedLine = ref('all')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)
const detailVisible = ref(false)
const currentDetail = ref(null)

const productionLines = ref([
  { value: 'all', label: '全部生产线' },
  { value: 'line1', label: '焊接线' },
  { value: 'line2', label: '涂装线' },
  { value: 'line3', label: '总装线' }
])

const overviewData = reactive([
  {
    title: '合格率',
    value: '98.2%',
    rate: '+0.5%',
    trend: 'up',
    color: '#67C23A',
    icon: 'el-icon-success'
  },
  {
    title: '缺陷率',
    value: '1.8%',
    rate: '-0.3%',
    trend: 'down',
    color: '#F56C6C',
    icon: 'el-icon-warning'
  },
  {
    title: '返工率',
    value: '0.8%',
    rate: '+0.1%',
    trend: 'up',
    color: '#E6A23C',
    icon: 'el-icon-refresh'
  },
  {
    title: '质量成本',
    value: '¥ 12,500',
    rate: '-5%',
    trend: 'down',
    color: '#409EFF',
    icon: 'el-icon-money'
  }
])

const levelMap = {
  1: '轻微',
  2: '一般',
  3: '严重'
}

const inspectionData = ref(generateData())

// 图表初始化
const initCharts = () => {
  trendInstance = echarts.init(trendChart.value)
  pieInstance = echarts.init(pieChart.value)
  barInstance = echarts.init(barChart.value)

  // 缺陷趋势配置
  const trendOption = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: { type: 'value' },
    series: [{
      name: '缺陷数量',
      type: 'line',
      smooth: true,
      data: [12, 15, 8, 18, 10, 5, 9],
      areaStyle: { color: 'rgba(255, 99, 71, 0.2)' }
    }]
  }

  // 缺陷分布配置
  const pieOption = {
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: 45, name: '外观划伤' },
        { value: 30, name: '装配不良' },
        { value: 15, name: '尺寸偏差' },
        { value: 8, name: '功能故障' },
        { value: 2, name: '材料缺陷' }
      ]
    }]
  }

  // TOP问题配置
  const barOption = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: ['外观划伤', '装配不良', '尺寸偏差', '功能故障', '材料缺陷']
    },
    series: [{
      type: 'bar',
      data: [45, 30, 15, 8, 2],
      itemStyle: { color: '#409EFF' }
    }]
  }

  trendInstance.setOption(trendOption)
  pieInstance.setOption(pieOption)
  barInstance.setOption(barOption)
}

// 交互方法
const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    inspectionData.value = generateData()
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const getLevelColor = (level) => {
  return { 1: 'success', 2: 'warning', 3: 'danger' }[level]
}

const viewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

const exportData = () => {
  ElMessage.success('数据导出功能开发中')
}

// 生命周期
onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendInstance.dispose()
  pieInstance.dispose()
  barInstance.dispose()
})

const handleResize = () => {
  trendInstance.resize()
  pieInstance.resize()
  barInstance.resize()
}
</script>

<style scoped>
.quality-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.filter-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.overview-card {
  :deep(.el-card__body) {
    padding: 15px;
  }
}

.card-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.card-info {
  flex: 1;

  .value {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .title {
    color: #666;
    margin-bottom: 5px;
  }

  .compare {
    font-size: 12px;

    &.up { color: #67c23a; }
    &.down { color: #f56c6c; }

    .el-icon {
      vertical-align: middle;
    }
  }
}

.analysis-charts {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.problem-card {
  margin-bottom: 20px;

  .chart {
    height: 350px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.detail-card {
  .el-pagination {
    margin-top: 15px;
    justify-content: flex-end;
  }
}

.defect-image {
  max-width: 100%;
  height: 150px;
  margin-top: 10px;
  border: 1px solid #eee;
}
</style>