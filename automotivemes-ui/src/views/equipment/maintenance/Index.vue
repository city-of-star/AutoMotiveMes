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
          <el-form-item label="设备名称：">
            <el-input v-model="queryForm.equipmentName" clearable placeholder="请输入设备名称" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="维护类型：">
            <el-select v-model="queryForm.maintenanceType" clearable placeholder="全部类型">
              <el-option label="日常保养" :value="1" />
              <el-option label="定期维护" :value="2" />
              <el-option label="紧急维修" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="维护结果：">
            <el-input v-model="queryForm.resultKeyword" clearable placeholder="请输入结果关键字" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="计划时间：" class="compact-form-item">
            <el-date-picker
                v-model="planTimeRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="4">
          <el-form-item label="实际时间：" class="compact-form-item">
            <el-date-picker
                v-model="actualTimeRange"
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
        :empty-text="'暂无维护记录'"
    >
      <el-table-column prop="maintenanceId" label="记录ID" width="120" align="center" />

      <el-table-column prop="equipmentCode" label="设备编码" width="150" align="center" />

      <el-table-column prop="equipmentName" label="设备名称" width="180" show-overflow-tooltip align="center" />

      <el-table-column prop="maintenanceType" label="类型" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getTypeTagType(row.maintenanceType)" effect="light">
            {{ row.maintenanceType }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="planDate" label="计划日期" width="150" align="center" />

      <el-table-column prop="actualDate" label="实际日期" width="150" align="center">
        <template #default="{ row }">
          {{ row.actualDate || '未执行' }}
        </template>
      </el-table-column>

      <el-table-column prop="operator" label="维护人员" width="120" align="center" />

      <el-table-column prop="result" label="维护结果" min-width="200" align="center">
        <template #default="{ row }">
          <div class="result-cell">
            {{ row.result || '--' }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="cost" label="成本(元)" width="120" align="center">
        <template #default="{ row }">
          {{ row.cost !== null ? row.cost.toFixed(2) : '--' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="80" align="center">
        <template #default="{ row }">
          <el-button type="text" @click="showDetail(row.maintenanceId)">详情</el-button>
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
      :title="`维护记录详情（ID：${currentDetail.maintenanceId}）`"
      width="800px"
      :close-on-click-modal="false"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="设备型号">{{ currentDetail.equipmentModel }}</el-descriptions-item>
      <el-descriptions-item label="安装位置">{{ currentDetail.location }}</el-descriptions-item>
      <el-descriptions-item label="制造商">{{ currentDetail.manufacturer }}</el-descriptions-item>
      <el-descriptions-item label="维护周期">{{ currentDetail.maintenanceCycle }}天</el-descriptions-item>
      <el-descriptions-item label="维护类型" :span="2">{{ currentDetail.maintenanceType }}</el-descriptions-item>
      <el-descriptions-item label="计划日期">{{ currentDetail.planDate }}</el-descriptions-item>
      <el-descriptions-item label="实际日期">{{ currentDetail.actualDate || '未执行' }}</el-descriptions-item>
      <el-descriptions-item label="维护内容" :span="2">
        <pre class="content-pre">{{ currentDetail.maintenanceContent }}</pre>
      </el-descriptions-item>
      <el-descriptions-item label="维护结果" :span="2">{{ currentDetail.result }}</el-descriptions-item>
      <el-descriptions-item label="维护成本">{{ currentDetail.cost ? currentDetail.cost.toFixed(2)+'元' : '--' }}</el-descriptions-item>
      <el-descriptions-item label="上次维护">{{ currentDetail.lastMaintenanceDate || '暂无记录' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import axios from '@/utils/axios'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const planTimeRange = ref([])
const actualTimeRange = ref([])

// 查询表单
const queryForm = ref({
  equipmentCode: '',
  equipmentName: '',
  maintenanceType: null,
  resultKeyword: '',
})

// 分页数据
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 标签样式
const getTypeTagType = (type) => ({
  '日常保养': 'primary',
  '定期维护': 'success',
  '紧急维修': 'warning'
}[type] || '')

// 处理搜索
const handleSearch = () => {
  pagination.value.current = 1
  fetchData()
}

// 处理重置
const handleReset = () => {
  queryForm.value = {
    equipmentCode: '',
    equipmentName: '',
    maintenanceType: null,
    resultKeyword: ''
  }
  planTimeRange.value = []
  actualTimeRange.value = []
  handleSearch()
}

// 处理分页变化
const handlePageChange = () => {
  fetchData()
}

// 显示详情
const showDetail = async (id) => {
  try {
    currentDetail.value = await axios.get(`/equipment/maintenanceDetail/${id}`)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true

    const params = {
      ...queryForm.value,
      page: pagination.value.current,
      size: pagination.value.size,
      planStartDate: planTimeRange.value?.[0],
      planEndDate: planTimeRange.value?.[1],
      actualStartDate: actualTimeRange.value?.[0],
      actualEndDate: actualTimeRange.value?.[1]
    }

    const data = await axios.post('/equipment/listMaintenanceRecord', params)
    tableData.value = data.records || []
    pagination.value.total = data.total || 0
  } catch (error) {
    ElMessage.error('数据加载失败')
    console.error('Error fetching data:', error)
  } finally {
    loading.value = false
  }
}

// 初始化加载
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.content-pre {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}

.result-cell {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

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

.el-table ::v-deep(.el-tag) {
  font-weight: 500;
  letter-spacing: 0.5px;
}

.el-descriptions ::v-deep(.el-descriptions__body) {
  background: #f8f9fa;
}
</style>