<template>
  <!-- 查询条件优化：更紧凑的布局 -->
  <el-card class="query-card">
    <el-form :model="queryForm" label-width="100px" label-position="left" class="compact-form">
      <el-row :gutter="16">
        <!-- 调整为每行4个字段 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="工单号：" class="form-item-sm">
            <el-input v-model="queryForm.orderNo" clearable placeholder="工单号" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="产品名称：" class="form-item-sm">
            <el-input v-model="queryForm.productName" clearable placeholder="产品名称" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="产品型号：" class="form-item-sm">
            <el-input v-model="queryForm.productCode" clearable placeholder="产品型号" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="工序名称：" class="form-item-sm">
            <el-input v-model="queryForm.processName" clearable placeholder="工序名称" />
          </el-form-item>
        </el-col>

        <!-- 第二行 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="设备ID：" class="form-item-sm">
            <el-input
                v-model="queryForm.equipmentId"
                type="number"
                clearable
                placeholder="设备ID"
                :min="0"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="操作人员：" class="form-item-sm">
            <el-input
                v-model="queryForm.operatorId"
                type="number"
                clearable
                placeholder="人员ID"
                :min="0"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="24" :md="16" :lg="12">
          <el-form-item label="生产时间：" class="form-item-sm">
            <el-date-picker
                v-model="timeRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                class="full-width-datepicker"
            />
          </el-form-item>
        </el-col>

        <!-- 第三行 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="产出数量：" class="form-item-sm">
            <div class="number-range">
              <el-input
                  v-model="queryForm.minOutput"
                  type="number"
                  placeholder="最小"
                  :min="0"
                  class="compact-input"
              />
              <span class="separator">-</span>
              <el-input
                  v-model="queryForm.maxOutput"
                  type="number"
                  placeholder="最大"
                  :min="0"
                  class="compact-input"
              />
            </div>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="24" :md="16" :lg="12">
          <el-form-item label="异常筛选：" class="form-item-sm">
            <el-select
                v-model="queryForm.hasDefect"
                clearable
                placeholder="不良品"
                class="compact-select"
            >
              <el-option label="全部" :value="null" />
              <el-option label="有不良" :value="true" />
              <el-option label="无不良" :value="false" />
            </el-select>
            <el-select
                v-model="queryForm.hasRemark"
                clearable
                placeholder="备注"
                class="compact-select ml-8"
            >
              <el-option label="全部" :value="null" />
              <el-option label="有备注" :value="true" />
              <el-option label="无备注" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 操作按钮 -->
        <el-col :xs="24" class="text-right">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <!-- 表格优化：固定列+自适应 -->
  <el-card class="data-card">
    <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        class="compact-table"
        :scrollbar-always-on="true"
    >
      <!-- 固定左侧关键列 -->
      <el-table-column
          prop="equipmentCode"
          label="设备编码"
          width="120"
          fixed="left"
          show-overflow-tooltip
      />

      <el-table-column
          prop="orderNo"
          label="工单号"
          width="160"
          fixed="left"
          show-overflow-tooltip
      />

      <!-- 自适应列 -->
      <el-table-column
          prop="productName"
          label="产品名称"
          min-width="180"
          show-overflow-tooltip
      />

      <el-table-column
          prop="productCode"
          label="型号"
          width="120"
          show-overflow-tooltip
      />

      <el-table-column
          prop="processName"
          label="工序"
          width="120"
          show-overflow-tooltip
      />

      <!-- 响应式隐藏列 -->
      <el-table-column
          prop="processSequence"
          label="顺序"
          width="90"
          class-name="hidden-sm"
      />

      <el-table-column
          prop="outputQuantity"
          label="产出量"
          width="110"
          align="center"
      />

      <el-table-column
          prop="defectiveQuantity"
          label="不良数"
          width="110"
          align="center"
      />

      <el-table-column
          label="不良率"
          width="110"
          align="center"
      >
        <template #default="{ row }">
          {{ row.defectRate }}%
        </template>
      </el-table-column>

      <!-- 时间列优化 -->
      <el-table-column
          label="生产时间"
          min-width="220"
          class-name="hidden-sm"
      >
        <template #default="{ row }">
          <div v-if="row.startTime || row.endTime" class="time-cell">
            <div>{{ formatDateTime(row.startTime) }}</div>
            <div class="time-separator">至</div>
            <div>{{ formatDateTime(row.endTime) }}</div>
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>

      <el-table-column
          label="耗时"
          width="120"
          class-name="hidden-xs"
      >
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>

      <el-table-column
          prop="operatorName"
          label="操作员"
          width="120"
          class-name="hidden-sm"
      />

      <!-- 状态标签优化 -->
      <el-table-column
          label="质检"
          width="110"
          align="center"
      >
        <template #default="{ row }">
          <el-tag
              :type="getQualityTagType(row)"
              size="small"
              effect="dark"
              class="status-tag"
          >
            {{ row.qualityStatus }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
          label="异常"
          width="110"
          align="center"
      >
        <template #default="{ row }">
          <el-tag
              v-if="row.hasAbnormal"
              type="danger"
              size="small"
              effect="dark"
              class="status-tag"
          >异常</el-tag>
          <el-tag
              v-else
              type="success"
              size="small"
              effect="dark"
              class="status-tag"
          >正常</el-tag>
        </template>
      </el-table-column>

      <!-- 固定右侧操作列 -->
      <el-table-column
          label="操作"
          fixed="right"
          width="90"
          align="center"
      >
        <template #default="{ row }">
          <el-button
              link
              type="primary"
              size="small"
              @click="showDetail(row)"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页优化 -->
    <div class="pagination-wrapper">
      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handlePageChange"
          @current-change="handlePageChange"
      />
    </div>
  </el-card>

  <!-- 详情弹窗优化 -->
  <el-dialog
      v-model="detailVisible"
      :title="`生产记录详情 - ${currentDetail.recordId}`"
      width="800px"
      class="detail-dialog"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="工单号" label-align="right" align="left" min-width="120">{{ currentDetail.orderNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="设备编码" label-align="right" align="left">{{ currentDetail.equipmentCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="产品名称" label-align="right" align="left">{{ currentDetail.productName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="产品型号" label-align="right" align="left">{{ currentDetail.productCode || '-' }}</el-descriptions-item>
      <el-descriptions-item label="工序名称" label-align="right" align="left">{{ currentDetail.processName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="工序顺序" label-align="right" align="left">{{ currentDetail.processSequence || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划数量" label-align="right" align="left">{{ currentDetail.orderQuantity || 0 }}</el-descriptions-item>
      <el-descriptions-item label="已完成数" label-align="right" align="left">{{ currentDetail.orderCompleted || 0 }}</el-descriptions-item>
      <el-descriptions-item label="产出数量" label-align="right" align="left">{{ currentDetail.outputQuantity || 0 }}</el-descriptions-item>
      <el-descriptions-item label="不良数量" label-align="right" align="left">{{ currentDetail.defectiveQuantity || 0 }}</el-descriptions-item>
      <el-descriptions-item label="不良率" label-align="right" align="left">{{ currentDetail.defectRate || 0 }}%</el-descriptions-item>
      <el-descriptions-item label="操作人员" label-align="right" align="left">{{ currentDetail.operatorName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="开始时间" label-align="right" align="left">{{ formatDateTime(currentDetail.startTime) }}</el-descriptions-item>
      <el-descriptions-item label="结束时间" label-align="right" align="left">{{ formatDateTime(currentDetail.endTime) }}</el-descriptions-item>
      <el-descriptions-item label="质检结果" :span="2" label-align="right" align="left">
        <el-tag
            :type="getQualityTagType(currentDetail)"
            size="small"
            effect="dark"
            class="status-tag"
        >
          {{ currentDetail.qualityStatus }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="异常备注" :span="2" label-align="right" align="left">
        <pre class="remark-pre">{{ currentDetail.remark || '无异常备注' }}</pre>
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
  orderNo: '',
  productName: '',
  productCode: '',
  processName: '',
  equipmentId: null,
  operatorId: null,
  minOutput: null,
  maxOutput: null,
  hasDefect: null,
  hasRemark: null
})

// 分页配置
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 获取数据（保持原逻辑）
const fetchData = async () => {
  try {
    loading.value = true
    const params = {
      ...queryForm.value,
      startTimeBegin: timeRange.value?.[0],
      startTimeEnd: timeRange.value?.[1],
      page: pagination.value.current,
      size: pagination.value.size
    }
    const data = await axios.post('/order/listProductionRecord', params)
    tableData.value = data.records || []
    pagination.value.total = data.total || 0
  } catch (error) {
    ElMessage.error('数据加载失败')
    console.error('获取生产记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 以下方法保持原逻辑
const handleSearch = () => {
  pagination.value.current = 1
  fetchData()
}

const handleReset = () => {
  queryForm.value = {
    orderNo: '',
    productName: '',
    productCode: '',
    processName: '',
    equipmentId: null,
    operatorId: null,
    minOutput: null,
    maxOutput: null,
    hasDefect: null,
    hasRemark: null
  }
  timeRange.value = []
  handleSearch()
}

const handlePageChange = () => fetchData()

const showDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString().slice(0,5)
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60

  return [
    days && `${days}天`,
    hours && `${hours}时`,
    minutes && `${minutes}分`,
    `${secs}秒`
  ].filter(Boolean).join('').replace(/^0+/, '') || '0秒'
}

const getQualityTagType = (row) => {
  switch (row.inspectionResult) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return 'warning'
    default: return 'info'
  }
}

onMounted(fetchData)
</script>

<style scoped>
/* 紧凑查询表单 */
.compact-form {
  :deep(.el-form-item) {
    margin-bottom: 12px;
  }

  .form-item-sm {
    :deep(.el-form-item__label) {
      font-size: 13px;
    }
    :deep(.el-input__inner) {
      height: 32px;
      line-height: 32px;
    }
  }

  .full-width-datepicker {
    width: 100%;
  }

  .number-range {
    display: flex;
    align-items: center;
    gap: 4px;

    .compact-input {
      width: 100px;
      :deep(.el-input__inner) {
        text-align: center;
        padding: 0 8px;
      }
    }

    .separator {
      color: #999;
      flex-shrink: 0;
    }
  }

  .compact-select {
    width: 120px;
    &.ml-8 { margin-left: 8px; }
  }
}

/* 紧凑型表格 */
.compact-table {
  :deep(.el-table__header th) {
    background: #f8f9fa;
    font-size: 13px;
    padding: 8px 0;
  }

  :deep(.el-table__body td) {
    font-size: 12.5px;
    padding: 6px 0;
  }

  .time-cell {
    line-height: 1.4;
    .time-separator {
      color: #999;
      font-size: 12px;
    }
  }

  .status-tag {
    border-radius: 12px;
    padding: 0 8px;
  }

  @media (max-width: 768px) {
    .hidden-sm { display: none; }
  }
  @media (max-width: 480px) {
    .hidden-xs { display: none; }
  }
}

/* 分页样式 */
.pagination-wrapper {
  float: right;
  padding: 16px 0;
  background: #fff;
  :deep(.el-pagination) {
    justify-content: flex-end;
    .el-pagination__total {
      margin-right: auto;
    }
  }
}

/* 详情弹窗 */
.detail-dialog {
  :deep(.el-descriptions__body) {
    background: #f8f9fa;
  }
  .remark-pre {
    margin: 0;
    white-space: pre-wrap;
    font-family: inherit;
    background: #fff;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #eee;
  }
}

/* 卡片间距 */
.query-card {
  margin-bottom: 16px;
  border-radius: 6px;
  :deep(.el-card__body) {
    padding: 16px;
  }
}
.data-card {
  border-radius: 6px;
  :deep(.el-card__body) {
    padding: 0;
  }
}
</style>