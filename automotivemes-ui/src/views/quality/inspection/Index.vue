<template>
  <!-- 质检任务查询 -->
  <el-card class="query-card">
    <el-form :model="queryForm" label-width="90px" label-position="left">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="工单号：">
            <el-input v-model="queryForm.orderNo" clearable placeholder="请输入工单号" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="产品名称：">
            <el-input v-model="queryForm.productName" clearable placeholder="请输入产品名称" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="工序名称：">
            <el-select v-model="queryForm.processName" clearable placeholder="请选择工序">
              <el-option
                  v-for="process in processOptions"
                  :key="process"
                  :label="process"
                  :value="process"
              />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="质检项：">
            <el-input v-model="queryForm.inspectionItem" clearable placeholder="请输入质检项" />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-form-item label="任务状态：">
            <el-select v-model="queryForm.taskStatus" clearable placeholder="全部状态">
              <el-option label="待检验" :value="1" />
              <el-option label="检验中" :value="2" />
              <el-option label="已完成" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="24" :md="24" :lg="24">
          <div class="form-actions">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </el-card>

  <!-- 质检任务表格 -->
  <el-card class="data-card">
    <el-table
        :data="taskData"
        v-loading="loading"
        stripe
        border
        style="width: 100%"
        empty-text="暂无质检任务"
    >
      <el-table-column prop="orderNo" label="工单号" width="160" align="center" />
      <el-table-column prop="productName" label="产品名称" width="180" align="center" />
      <el-table-column prop="processName" label="工序名称" width="150" align="center" />
      <el-table-column prop="inspectionItems" label="质检项" min-width="200" align="center" />

      <el-table-column label="任务状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.taskStatus)" effect="light">
            {{ formatStatus(row.taskStatus) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="检验时间" width="180" align="center">
        <template #default="{ row }">
          {{ formatTime(row.inspectionTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            :disabled="row.taskStatus !== 3"
            @click="showSubmitDialog(row)"
          >
            {{ {1:'已通过',2:'已拒绝',3:'提交复检'}[row.taskStatus] || '开始检验' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
      />
    </div>
  </el-card>

  <!-- 质检结果提交弹窗 -->
  <el-dialog
      v-model="submitVisible"
      :title="`提交质检结果 - ${currentTask.orderNo}`"
      width="600px"
      :close-on-click-modal="false"
  >
    <el-form
        :model="submitForm"
        label-width="100px"
        :rules="submitRules"
        ref="submitFormRef"
    >
      <el-form-item label="检验结果：" prop="inspectionResult">
        <el-radio-group v-model="submitForm.inspectionResult">
          <el-radio :label="1">合格</el-radio>
          <el-radio :label="2">不合格</el-radio>
          <el-radio :label="3">待复检</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="检验数据：" prop="inspectionData">
        <el-input
            v-model="submitForm.inspectionData"
            type="textarea"
            :rows="3"
            placeholder="请输入检验数据（JSON格式）"
        />
      </el-form-item>

      <el-form-item label="备注：">
        <el-input
            v-model="submitForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="submitVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

// 响应式数据
const loading = ref(false)
const taskData = ref([])
const submitVisible = ref(false)
const submitFormRef = ref(null)
const processOptions = ref(['压铸成型', '电路板组装', '气密测试', '定子绕线', '转子动平衡'])

// 当前操作任务
const currentTask = ref({
  taskId: null,
  orderNo: ''
})

// 表单数据
const queryForm = ref({
  orderNo: '',
  productName: '',
  processName: '',
  inspectionItem: '',
  taskStatus: null
})

const submitForm = ref({
  recordId: null,
  inspectionResult: null,
  inspectionData: '',
  remark: ''
})

// 表单验证规则
const submitRules = {
  inspectionResult: [
    { required: true, message: '请选择检验结果', trigger: 'blur' }
  ],
  inspectionData: [
    { required: true, message: '请输入检验数据', trigger: 'blur' }
  ]
}

// 分页配置
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 状态格式化
const formatStatus = (status) => {
  const statusMap = {1:'合格', 2:'不合格', 3:'待复检'}
  return statusMap[status] || '未知状态'
}

const statusTagType = (status) => {
  return {1:'success', 2:'danger', 3:'warning'}[status] || 'info'
}

// 时间格式化
const formatTime = (time) => {
  return time ? new Date(time).toLocaleString() : '--'
}

// 获取质检任务
const fetchTasks = async () => {
  try {
    loading.value = true
    const params = {
      ...queryForm.value,
      page: pagination.value.current,
      size: pagination.value.size
    }

    const data = await axios.post('/qualityInspection/listQualityTasks', params)
    taskData.value = data.records || []
    pagination.value.total = data.total || 0
  } catch (error) {
    ElMessage.error('获取质检任务失败')
    console.error('获取质检任务失败:', error)
  } finally {
    loading.value = false
  }
}

// 显示提交弹窗
const showSubmitDialog = (task) => {
  currentTask.value = { ...task }
  submitForm.value = {
    recordId: task.taskId,
    inspectionResult: null,
    inspectionData: '',
    remark: ''
  }
  submitVisible.value = true
}

// 提交质检结果
const handleSubmit = async () => {
  try {
    await submitFormRef.value.validate()

    const params = { ...submitForm.value }
    await axios.post('/qualityInspection/submitQualityResult', params)

    ElMessage.success('提交成功')
    submitVisible.value = false
    await fetchTasks()
  } catch (error) {
    if (error.errors) return
    ElMessage.error('提交失败')
    console.error('提交质检结果失败:', error)
  }
}

// 分页处理
const handlePageChange = () => {
  fetchTasks()
}

// 查询操作
const handleSearch = () => {
  pagination.value.current = 1
  fetchTasks()
}

// 重置查询
const handleReset = () => {
  queryForm.value = {
    orderNo: '',
    productName: '',
    processName: '',
    inspectionItem: '',
    taskStatus: null
  }
  handleSearch()
}

// 初始化加载
onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.query-card {
  margin-bottom: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.data-card {
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  padding: 12px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-tag {
  font-weight: 500;
}

:deep(.el-table__row) .el-button {
  padding: 6px 12px;
}
</style>