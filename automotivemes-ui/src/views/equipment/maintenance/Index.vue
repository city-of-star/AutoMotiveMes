<template>
  <div class="maintenance-container">
    <!-- 过滤条件 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="设备名称">
          <el-input
              v-model="filterForm.equipmentName"
              placeholder="输入设备名称"
              clearable
          />
        </el-form-item>

        <el-form-item label="维护类型">
          <el-select
              v-model="filterForm.maintenanceType"
              placeholder="全部类型"
              clearable
          >
            <el-option
                v-for="type in maintenanceTypes"
                :key="type.value"
                :label="type.label"
                :value="type.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="日期范围">
          <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="handleSearch"
          >
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col v-for="(stat, index) in stats" :key="index" :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="24"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="content">
              <div class="value">{{ stat.value }}</div>
              <div class="title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never">
          <div ref="typeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <div ref="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table
          :data="tableData"
          v-loading="loading"
          style="width: 100%"
          @row-click="handleRowClick"
      >
        <el-table-column prop="maintenanceCode" label="维护单号" width="140" />
        <el-table-column label="设备信息" width="200">
          <template #default="{row}">
            <div class="equipment-info">
              <div class="name">{{ row.equipmentName }}</div>
              <div class="code">{{ row.equipmentCode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="维护类型" width="120">
          <template #default="{row}">
            <el-tag :type="typeTagMap[row.maintenanceType]">
              {{ maintenanceTypeMap[row.maintenanceType] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计划/实际日期" width="220">
          <template #default="{row}">
            <div class="date-info">
              <div>计划：{{ row.planDate }}</div>
              <div :class="{'delayed': row.isDelayed}">
                实际：{{ row.actualDate || '未完成' }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="维护人员" width="120" />
        <el-table-column prop="cost" label="维护成本" width="120">
          <template #default="{row}">
            ¥{{ row.cost.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{row}">
            <el-tag :type="statusTagMap[row.status]">
              {{ statusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          class="pagination"
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          @current-change="handlePageChange"
          layout="total, prev, pager, next, jumper"
      />
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer
        v-model="detailVisible"
        title="维护记录详情"
        size="40%"
    >
      <div v-if="currentDetail" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="维护单号">
            {{ currentDetail.maintenanceCode }}
          </el-descriptions-item>
          <el-descriptions-item label="设备名称">
            {{ currentDetail.equipmentName }}
          </el-descriptions-item>
          <el-descriptions-item label="设备编码">
            {{ currentDetail.equipmentCode }}
          </el-descriptions-item>
          <el-descriptions-item label="维护类型">
            <el-tag :type="typeTagMap[currentDetail.maintenanceType]">
              {{ maintenanceTypeMap[currentDetail.maintenanceType] }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="维护内容">
            {{ currentDetail.content }}
          </el-descriptions-item>
          <el-descriptions-item label="维护结果">
            {{ currentDetail.result || '暂无结果记录' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from '@/utils/axios'
import { useEventListener } from '@vueuse/core'
import {
  Calendar,
  Clock,
  CircleCheck,
  Warning as WarningIcon
} from '@element-plus/icons-vue'

// 常量定义
const maintenanceTypeMap = {
  1: '日常保养',
  2: '定期维护',
  3: '紧急维修'
}

const typeTagMap = {
  1: 'info',
  2: 'primary',
  3: 'danger'
}

const statusMap = {
  0: '进行中',
  1: '已完成',
  2: '已超期'
}

const statusTagMap = {
  0: 'warning',
  1: 'success',
  2: 'danger'
}

// 模拟数据
const mockData = Array.from({ length: 50 }, (_, i) => ({
  id: i + 1,
  maintenanceCode: `WH-${20240000 + i}`,
  equipmentName: ['焊接机器人', '冲压机', '检测设备'][i % 3],
  equipmentCode: ['WSH-001', 'SC-002', 'JC-003'][i % 3],
  maintenanceType: [1, 2, 3][i % 3],
  planDate: `2024-03-${10 + (i % 20)}`,
  actualDate: i % 4 === 0 ? null : `2024-03-${12 + (i % 18)}`,
  operator: ['张三', '李四', '王五'][i % 3],
  cost: [500, 1200, 800][i % 3],
  content: `第${i + 1}次维护，完成日常保养工作`,
  result: i % 3 === 0 ? '维护完成，设备运行正常' : null,
  status: [0, 1, 2][i % 3],
  isDelayed: i % 5 === 0
}))

// 响应式数据
const filterForm = ref({
  equipmentName: '',
  maintenanceType: null,
  dateRange: []
})

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentDetail = ref(null)

// 分页配置
const pagination = ref({
  current: 1,
  size: 10,
  total: mockData.length
})

// 统计卡片数据
const stats = ref([
  { title: '总维护次数', value: 48, icon: Calendar, color: '#409EFF' },
  { title: '进行中', value: 5, icon: Clock, color: '#E6A23C' },
  { title: '已完成', value: 40, icon: CircleCheck, color: '#67C23A' },
  { title: '超期维护', value: 3, icon: WarningIcon, color: '#F56C6C' }
])

// 图表相关
const typeChart = ref(null)
const trendChart = ref(null)
let typeChartInstance = null
let trendChartInstance = null

// 初始化图表
const initCharts = () => {
  // 维护类型分布图
  typeChartInstance = echarts.init(typeChart.value)
  typeChartInstance.setOption({
    title: { text: '维护类型分布', left: 'center' },
    tooltip: { trigger: 'item' },
    legend: { top: 'bottom' },
    series: [{
      name: '维护类型',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 20, name: '日常保养' },
        { value: 15, name: '定期维护' },
        { value: 5, name: '紧急维修' }
      ],
      emphasis: { itemStyle: { shadowBlur: 10 } }
    }]
  })

  // 维护趋势图
  trendChartInstance = echarts.init(trendChart.value)
  trendChartInstance.setOption({
    title: { text: '每月维护趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: { type: 'value' },
    series: [{
      name: '维护次数',
      type: 'bar',
      data: [12, 18, 15, 8, 11, 9],
      itemStyle: { color: '#409EFF' }
    }]
  })
}

// 加载表格数据
const loadData = async () => {

  const res = await axios.get("/equipment/listMaintenanceRecord", {
    page: 1,
    size: 10,
  })
  console.log(res)

  loading.value = true
  setTimeout(() => {
    const start = (pagination.value.current - 1) * pagination.value.size
    tableData.value = mockData.slice(start, start + pagination.value.size)
    loading.value = false
  }, 500)
}

// 事件处理
const handleSearch = () => {
  pagination.value.current = 1
  loadData()
}

const handlePageChange = (page) => {
  pagination.value.current = page
  loadData()
}

const handleRowClick = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

// 生命周期
onMounted(() => {
  loadData()
  nextTick(() => initCharts())

  useEventListener(window, 'resize', () => {
    typeChartInstance?.resize()
    trendChartInstance?.resize()
  })
})

onBeforeUnmount(() => {
  typeChartInstance?.dispose()
  trendChartInstance?.dispose()
})
</script>

<style scoped>
.maintenance-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-card .icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-card .content {
  margin-left: 15px;
}

.stat-card .value {
  font-size: 24px;
  font-weight: bold;
}

.stat-card .title {
  color: #666;
  margin-top: 4px;
}

.equipment-info .name {
  font-weight: 500;
}

.equipment-info .code {
  color: #666;
  font-size: 12px;
}

.date-info div {
  line-height: 1.5;
}

.date-info .delayed {
  color: #F56C6C;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.detail-content {
  padding: 20px;
}
</style>