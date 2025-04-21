<template>
  <!-- 查询条件卡片 -->
  <el-card class="query-card">
    <el-form :model="queryForm" label-width="90px" label-position="left">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="设备编码：">
            <el-input v-model="queryForm.equipmentCode" clearable placeholder="请输入设备编码" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="报警等级：">
            <el-select v-model="queryForm.alarmLevel" clearable placeholder="全部等级">
              <el-option label="警告" :value="1" />
              <el-option label="一般故障" :value="2" />
              <el-option label="严重故障" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="处理状态：">
            <el-select v-model="queryForm.status" clearable placeholder="全部状态">
              <el-option label="未处理" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已处理" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="处理人：">
            <el-input v-model="queryForm.handler" clearable placeholder="请输入处理人" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="24" :md="16" :lg="14" :xl="16">
          <el-form-item label="报警时间：" class="compact-form-item">
            <el-date-picker
                v-model="timeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="24" :md="8" :lg="10" :xl="8">
          <el-form-item class="compact-form-item">
            <div class="form-actions">
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <!-- 数据表格 -->
  <el-card class="data-card">
    <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        :empty-text="'暂无报警历史数据'"
    >
      <el-table-column prop="alarmCode" label="报警编码" width="200" align="center">
        <template #default="{ row }">
          <el-tag :type="getCodeTagType(row)" effect="dark">
            {{ row.alarmCode }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="equipmentName" label="设备名称" width="180" show-overflow-tooltip align="center" />

      <el-table-column prop="alarmLevel" label="等级" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getLevelTagType(row)" effect="light">
            {{ formatLevel(row.alarmLevel) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="startTime" label="报警时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatDateTime(row.startTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="duration" label="持续时间" width="120" align="center">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>

      <el-table-column prop="handler" label="处理人" width="120" show-overflow-tooltip align="center" />

      <el-table-column prop="solution" label="处理方案" min-width="200" align="center">
        <template #default="{ row }">
          <div class="solution-cell">
            {{ row.solution }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)" size="small">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="80" align="center">
        <template #default="{ row }">
          <el-button type="text" @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
      />
    </div>
  </el-card>

  <!-- 详情弹窗 -->
  <el-dialog
      v-model="detailVisible"
      title="报警详情"
      width="800px"
      :close-on-click-modal="false"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="报警ID" :span="2">{{ currentDetail.alarmId }}</el-descriptions-item>
      <el-descriptions-item label="设备编码">{{ currentDetail.equipmentCode }}</el-descriptions-item>
      <el-descriptions-item label="设备位置">{{ currentDetail.location }}</el-descriptions-item>
      <el-descriptions-item label="报警原因" :span="2">{{ currentDetail.alarmReason }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{ formatDateTime(currentDetail.startTime) }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ formatDateTime(currentDetail.endTime) }}</el-descriptions-item>
      <el-descriptions-item label="处理方案" :span="2">
        <pre class="solution-pre">{{ currentDetail.solution }}</pre>
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const timeRange = ref([])

// 查询表单
const queryForm = ref({
  equipmentCode: '',
  alarmLevel: null,
  status: null,
  handler: '',
  startDate: null,
  endDate: null
})

// 分页数据
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 格式化方法
const formatLevel = (level) => ['警告', '一般故障', '严重故障'][level - 1] || '未知'
const formatStatus = (status) => ['未处理', '处理中', '已处理'][status] || '未知'
const formatDateTime = (date) => date ? new Date(date).toLocaleString() : '-'

// 标签样式
const getLevelTagType = (row) => ({
  1: 'warning',
  2: 'danger',
  3: 'danger'
}[row.alarmLevel])

const getCodeTagType = (row) =>
    row.alarmLevel === 3 ? 'danger' : row.alarmLevel === 2 ? 'warning' : 'info'

const getStatusTagType = (status) =>
    ['danger', 'warning', 'success'][status] || ''

// 处理搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchData()
}

// 处理重置
const handleReset = () => {
  queryForm.value = {
    equipmentCode: '',
    alarmLevel: null,
    status: null,
    handler: '',
    startDate: null,
    endDate: null
  }
  timeRange.value = []
  handleSearch()
}

// 处理分页变化
const handlePageChange = () => {
  fetchData()
}

// 显示详情
const showDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true

    const params = {
      ...queryForm.value,
      page: pagination.value.current,
      size: pagination.value.size,
      startDate: timeRange.value?.[0],
      endDate: timeRange.value?.[1]
    }

    const data = await axios.get('/equipment/listEquipmentAlarmHistory', { params })
    tableData.value = data.records || []
    pagination.value.total = data.total || 0

  } catch (error) {
    ElMessage.error('数据加载失败')
    console.error('Error fetching data:', error)
  } finally {
    loading.value = false
  }
}

// 持续时间格式化函数
const formatDuration = (seconds) => {
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor(seconds / 3600) % 24
  const minutes = Math.floor(seconds / 60) % 60
  const secs = seconds % 60

  const parts = []
  if (days > 0) parts.push(`${days}天`)
  if (hours > 0) parts.push(`${hours}小时`)
  if (minutes > 0) parts.push(`${minutes}分`)
  if (secs > 0 || parts.length === 0) parts.push(`${secs}秒`)

  return parts.join('')
}

// 初始化加载
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.query-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.data-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.solution-pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
  color: #606266;
}

.el-table ::v-deep(.el-tag) {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.el-descriptions ::v-deep(.el-descriptions__body) {
  background: #f8f9fa;
}

.solution-cell {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1; /* 限制显示一行 */
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.5em; /* 调整行高 */
  max-height: 3em; /* 两行高度 (2 * 1.5em) */
}
</style>