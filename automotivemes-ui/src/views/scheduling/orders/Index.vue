<template>
  <div class="work-order-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" inline class="search-form">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="工单编号" prop="orderNo">
            <el-input v-model="queryParams.orderNo" clearable placeholder="请输入工单编号"/>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="设备编码" prop="equipmentCode">
            <el-input v-model="queryParams.equipmentCode" clearable placeholder="请输入设备编码"/>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工单状态" prop="status">
            <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
              <el-option
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工单类型" prop="orderType">
            <el-select v-model="queryParams.orderType" clearable placeholder="请选择类型">
              <el-option
                  v-for="item in typeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="创建时间">
            <el-date-picker
                v-model="queryParams.createTimeRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12" class="text-right">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </el-form>

    <!-- 数据表格 -->
    <el-table
        :data="tableData"
        v-loading="loading"
        @row-click="handleRowClick"
    >
      <el-table-column  prop="orderNo" label="工单编号" align="center"/>
      <el-table-column prop="equipmentCode" label="设备编码" align="center"/>
      <el-table-column prop="equipmentName" label="设备名称" align="center"/>
      <el-table-column prop="orderType" label="工单类型" align="center">
        <template #default="{row}">
          <el-tag :type="typeTagMap[row.orderType]">
            {{ row.orderType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" align="center">
        <template #default="{row}">
          <el-tag :type="statusTagMap[row.status]">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="creatorName" label="创建人" align="center"/>
      <el-table-column prop="assigneeName" label="处理人" align="center"/>
      <el-table-column prop="createTime" label="创建时间" align="center"/>
      <el-table-column prop="planCount" label="排程数" align="center"/>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 工单详情对话框 -->
    <el-dialog
        v-model="detailVisible"
        title="工单详情"
        width="800px"
        class="detail-dialog"
        :close-on-click-modal="false"
        align-center
    >
      <div class="dialog-content">
        <!-- 基本信息分组 -->
        <div class="info-group">
          <h3 class="group-title"><i class="el-icon-document"></i> 工单信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="工单编号" label-class-name="detail-label">
              <span class="highlight-text">{{ currentDetail.orderNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="工单类型">
              <el-tag :type="typeTagMap[currentDetail.orderType]" effect="light">
                {{ currentDetail.orderType }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag :type="statusTagMap[currentDetail.status]" effect="light">
                {{ currentDetail.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="排程数">
              <span class="number-text">{{ currentDetail.planCount }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 设备信息分组 -->
        <div class="info-group">
          <h3 class="group-title"><i class="el-icon-cpu"></i> 设备信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="设备编码">{{ currentDetail.equipmentCode }}</el-descriptions-item>
            <el-descriptions-item label="设备名称" label-class-name="detail-label">
              <el-tag type="info">{{ currentDetail.equipmentName }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 人员时间信息 -->
        <div class="info-group">
          <h3 class="group-title"><i class="el-icon-clock"></i> 时间信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="创建人">{{ currentDetail.creatorName }}</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ currentDetail.assigneeName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">
          <span :class="{ 'time-text': currentDetail.finishTime }">
            {{ currentDetail.finishTime || '进行中' }}
          </span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 计划信息卡片式布局 -->
        <div class="info-group">
          <h3 class="group-title"><i class="el-icon-data-analysis"></i> 计划详情</h3>
          <el-tabs type="border-card" stretch>
            <el-tab-pane label="排程计划">
          <pre class="json-preview" v-if="currentDetail.schedules">
            {{ formatJSON(currentDetail.schedules) }}
          </pre>
              <el-empty v-else description="暂无排程数据" :image-size="60"/>
            </el-tab-pane>
            <el-tab-pane label="维护计划">
          <pre class="json-preview" v-if="currentDetail.maintenanceInfo">
            {{ formatJSON(currentDetail.maintenanceInfo) }}
          </pre>
              <el-empty v-else description="暂无维护数据" :image-size="60"/>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 统计图表 -->
    <div ref="chartRef" style="width: 100%; height: 400px; margin-top: 20px"></div>
  </div>
</template>

<script setup>
import {nextTick, onMounted, ref, watchEffect} from 'vue'
import {ElMessage} from 'element-plus'
import * as echarts from 'echarts'
import axios from '@/utils/axios.js'

// 图表配置
const chartOptions = {
  title: {
    text: '工单状态分布',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '状态分布',
      type: 'pie',
      radius: '50%',
      emphasis: {
        label: {
          show: true,
          fontSize: 20
        }
      }
    }
  ]
}

// 响应式数据
const queryParams = ref({
  page: 1,
  size: 10,
  orderNo: '',
  equipmentCode: '',
  status: '',
  orderType: '',
  createTimeRange: []
})

const statusOptions = ref([
  { value: '待处理', label: '待处理' },
  { value: '进行中', label: '进行中' },
  { value: '已完成', label: '已完成' }
])

const typeOptions = ref([
  { value: '生产', label: '生产工单' },
  { value: '维护', label: '维护工单' },
  { value: '维修', label: '维修工单' }
])

const statusTagMap = {
  '待处理': 'warning',
  '进行中': 'primary',
  '已完成': 'success'
}

const typeTagMap = {
  '生产工单': '',
  '维护工单': 'info',
  '维修工单': 'danger'
}

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const currentDetail = ref({})
const chartRef = ref(null)
let chartInstance = null

// 方法
const loadData = async () => {
  try {
    loading.value = true
    const params = {
      ...queryParams.value,
      createTimeStart: queryParams.value.createTimeRange?.[0],
      createTimeEnd: queryParams.value.createTimeRange?.[1]
    }
    delete params.createTimeRange

    const data = await axios.post('/order/listWorkOrders', params)
    tableData.value = data.records
    total.value = data.total

    // 更新图表
    updateChart(data.records)
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

const updateChart = (data) => {
  if (!chartInstance) return

  const statusCount = data.reduce((acc, cur) => {
    acc[cur.status] = (acc[cur.status] || 0) + 1
    return acc
  }, {})

  const chartData = Object.entries(statusCount).map(([name, value]) => ({
    name,
    value
  }))

  chartInstance.setOption({
    series: [{
      data: chartData,
      label: {
        formatter: '{b}: {c} ({d}%)'
      }
    }]
  })
}

const handleSearch = () => {
  queryParams.value.page = 1
  loadData()
}

const handleReset = () => {
  queryParams.value = {
    page: 1,
    size: 10,
    orderNo: '',
    equipmentCode: '',
    status: '',
    orderType: '',
    createTimeRange: []
  }
  loadData()
}

const handleSizeChange = (size) => {
  queryParams.value.size = size
  loadData()
}

const handleCurrentChange = (page) => {
  queryParams.value.page = page
  loadData()
}

const handleRowClick = async (row) => {
  try {
    currentDetail.value = await axios.get(`/order/getWorkOrderDetail/${row.orderId}`)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('详情加载失败')
  }
}

const formatJSON = (str) => {
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str || '无数据'
  }
}

// 生命周期
onMounted(() => {
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption(chartOptions)
  loadData()

  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})

// 自动更新图表
watchEffect(() => {
  if (tableData.value.length > 0) {
    nextTick(() => updateChart(tableData.value))
  }
})
</script>

<style scoped>
.work-order-container {
  padding: 20px;
  background: #fff;
}

.search-form {
  margin-bottom: 20px;
}

.text-right {
  text-align: right;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

pre {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
}

:deep(.el-table tr) {
  transition: background-color 0.3s;
}

:deep(.el-table tr:hover) {
  background-color: #c1caf1 !important;
  cursor: pointer;
}

.detail-dialog {
  --group-margin: 16px;
  --highlight-color: #409EFF;
}

.dialog-content {
  padding-right: 10px;
}

.info-group {
  margin-bottom: var(--group-margin);
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.group-title {
  color: var(--highlight-color);
  margin: 0 0 16px 0;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.json-preview {
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 12px;
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  line-height: 1.6;
  max-height: 200px;
  overflow: auto;
  transition: all 0.3s;
}

.json-preview:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.highlight-text {
  color: var(--highlight-color);
  font-weight: 500;
  font-size: 15px;
}

.number-text {
  color: #67C23A;
  font-weight: bold;
  font-size: 16px;
}

.time-text {
  color: #909399;
  font-style: italic;
}

::v-deep(.detail-label) {
  width: 100px;
  background: #f5f7fa !important;
  font-weight: 600;
}

::v-deep(.el-tabs__content) {
  padding: 12px;
  background: white;
}

::v-deep(.el-descriptions__body) {
  background: white;
}
</style>