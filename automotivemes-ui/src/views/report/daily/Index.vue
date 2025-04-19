<template>
  <div class="daily-report-container">
    <!-- 头部操作栏 -->
    <div class="report-header">
      <div class="header-left">
        <el-date-picker
            v-model="reportDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            @change="loadData"
        />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <div class="header-right">
        <el-button type="success" @click="exportPDF">导出PDF</el-button>
        <el-button @click="printReport">打印</el-button>
      </div>
    </div>

    <!-- 生产概览 -->
    <el-card class="summary-card">
      <div class="summary-title">生产概览</div>
      <div class="summary-grid">
        <div class="summary-item" v-for="item in summaryData" :key="item.title">
          <div class="summary-value">{{ item.value }}</div>
          <div class="summary-title">{{ item.title }}</div>
          <div class="summary-compare" :class="item.status">
            <span v-if="item.compareValue">{{ item.compareValue }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 生产详情 -->
    <el-card class="detail-card">
      <div class="detail-header">
        <h3>生产明细</h3>
        <el-tag type="warning">数据更新于 {{ lastUpdateTime }}</el-tag>
      </div>

      <el-table
          :data="productionData"
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column prop="line" label="生产线" width="120" />
        <el-table-column prop="productType" label="产品类型" width="150" />
        <el-table-column prop="planQuantity" label="计划产量" sortable width="120" />
        <el-table-column prop="actualQuantity" label="实际产量" sortable width="120" />
        <el-table-column prop="completionRate" label="达成率" sortable width="120">
          <template #default="{ row }">
            <el-tag :type="getRateType(row.completionRate)">
              {{ row.completionRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodRate" label="良品率" sortable width="120">
          <template #default="{ row }">
            <el-tag :type="getRateType(row.goodRate)" effect="dark">
              {{ row.goodRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="downtime" label="停机时间" width="120" />
        <el-table-column prop="oee" label="设备综合效率" sortable width="140">
          <template #default="{ row }">
            <el-progress
                :percentage="row.oee"
                :color="customColors"
                :format="formatOEE"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 生产趋势图表 -->
    <el-card class="chart-card">
      <h3>生产趋势分析</h3>
      <div ref="trendChart" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 图表实例
const trendChart = ref(null)
let chartInstance = null

// 数据状态
const reportDate = ref(getCurrentDate())
const lastUpdateTime = ref('--:--:--')
const loading = ref(false)

// 生产概览数据
const summaryData = reactive([
  { title: '总产量', value: 0, compareValue: '+0%', status: 'normal' },
  { title: '良品率', value: '0%', compareValue: '+0%', status: 'normal' },
  { title: '工时利用率', value: '0%', compareValue: '-0%', status: 'warning' },
  { title: '异常工时', value: '0h', compareValue: '-0%', status: 'normal' }
])

// 生产明细数据
const productionData = ref([])

// 获取当前日期
function getCurrentDate() {
  const today = new Date()
  return today.toISOString().split('T')[0]
}

// 模拟数据生成
const generateMockData = () => {
  const data = []
  const lines = ['焊装线', '涂装线', '总装线', '检测线']
  const types = ['SUV', '轿车', 'MPV', '新能源']

  lines.forEach(line => {
    types.forEach(type => {
      data.push({
        line,
        productType: type,
        planQuantity: Math.floor(Math.random() * 1000 + 500),
        actualQuantity: Math.floor(Math.random() * 1000 + 480),
        completionRate: Math.floor(Math.random() * 5 + 95),
        goodRate: (95 + Math.random() * 4).toFixed(1),
        downtime: `${Math.floor(Math.random() * 3)}h${Math.floor(Math.random() * 60)}m`,
        oee: Math.floor(Math.random() * 20 + 80)
      })
    })
  })
  return data
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 800))
    productionData.value = generateMockData()
    updateSummaryData()
    updateChart()
    lastUpdateTime.value = new Date().toLocaleTimeString()
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

// 更新概览数据
const updateSummaryData = () => {
  const total = productionData.value.reduce((sum, item) => sum + item.actualQuantity, 0)
  const goodTotal = productionData.value.reduce((sum, item) =>
      sum + item.actualQuantity * (item.goodRate/100), 0)

  summaryData[0].value = total.toLocaleString()
  summaryData[1].value = `${(goodTotal / total * 100).toFixed(1)}%`
}

// 图表配置
const initChart = () => {
  chartInstance = echarts.init(trendChart.value)

  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
    },
    yAxis: { type: 'value' },
    series: [{
      name: '生产速率',
      type: 'line',
      smooth: true,
      data: [820, 932, 901, 934, 1290, 1330],
      areaStyle: { color: 'rgba(64, 158, 255, 0.2)' }
    }]
  }

  chartInstance.setOption(option)
}

const updateChart = () => {
  // 实际项目中更新图表数据
}

// 工具方法
const getRateType = (rate) => {
  return rate >= 95 ? 'success' : rate >= 90 ? 'warning' : 'danger'
}

const formatOEE = (percentage) => {
  return `OEE ${percentage}%`
}

// 导出功能
const exportPDF = () => {
  ElMessage.success('PDF导出功能开发中')
}

const printReport = () => {
  window.print()
}

// 生命周期
onMounted(() => {
  initChart()
  loadData()
})

onBeforeUnmount(() => {
  if (chartInstance) chartInstance.dispose()
})

// 样式相关
const customColors = [
  { color: '#f56c6c', percentage: 60 },
  { color: '#e6a23c', percentage: 80 },
  { color: '#67c23a', percentage: 100 }
]
</script>

<style scoped>
.daily-report-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
}

.summary-card {
  margin-bottom: 20px;
}

.summary-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.summary-item {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  position: relative;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.summary-compare {
  font-size: 12px;
  position: absolute;
  right: 10px;
  top: 10px;
  padding: 2px 8px;
  border-radius: 10px;
}

.summary-compare.normal { background: #f0f9eb; color: #67c23a; }
.summary-compare.warning { background: #fdf6ec; color: #e6a23c; }

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-card {
  margin-top: 20px;
}

.chart-container {
  height: 400px;
}

@media print {
  .report-header, .el-card__header {
    display: none;
  }

  .daily-report-container {
    padding: 0;
    background: white;
  }

  .el-table {
    border: 1px solid #ebeef5;
  }
}
</style>