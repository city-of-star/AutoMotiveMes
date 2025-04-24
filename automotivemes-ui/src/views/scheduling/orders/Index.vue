<template>
  <div class="order-manage-container">
    <!-- 查询条件 -->
    <el-card class="query-card">
      <el-form :model="queryForm" label-width="90px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="工单号">
              <el-input v-model="queryForm.orderNo" clearable placeholder="请输入工单号"/>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="产品型号">
              <el-input v-model="queryForm.productCode" clearable placeholder="请输入产品型号"/>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="状态">
              <el-select v-model="queryForm.status" clearable placeholder="全部状态">
                <el-option
                    v-for="item in statusOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="计划日期">
              <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>

          <el-col :span="24" class="text-right">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="success" @click="showCreateDialog">新建工单</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 工单表格 -->
    <el-card class="data-card">
      <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          height="463"
      >
        <el-table-column prop="orderNo" label="工单号" align="center"/>
        <el-table-column prop="productCode" label="产品型号" align="center"/>
        <el-table-column prop="productName" label="产品名称" align="center"/>
        <el-table-column prop="orderQuantity" label="数量" align="center"/>
        <el-table-column label="状态" align="center">
          <template #default="{row}">
            <el-tag :type="statusTagMap[row.status]">
              {{ statusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" align="center">
          <template #default="{row}">
            <el-tag :type="priorityTagMap[row.priority]">
              {{ priorityMap[row.priority] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedStartDate" label="计划开始" align="center"/>
        <el-table-column prop="plannedEndDate" label="计划完成" align="center"/>
        <el-table-column label="操作" fixed="right" align="center" width="250">
          <template #default="{row}">
            <el-button size="small" @click="showDetail(row)">详情</el-button>
            <el-button
                size="small"
                type="warning"
                @click="updateStatus(row.orderId)"
                :disabled="row.status !== 1"
            >
              排程
            </el-button>
            <el-button
                size="small"
                type="info"
                @click="viewSchedule(row.orderId)"
                :disabled="row.status < 2"
            >
              查看排程
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            @current-change="fetchOrders"
            @size-change="fetchOrders"
            layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <!-- 创建工单对话框 -->
    <el-dialog v-model="createVisible" title="新建生产工单" width="600px">
      <el-form :model="createForm" label-width="100px" :rules="rules" ref="createFormRef">
        <el-form-item label="产品" prop="productId">
          <el-select
              v-model="createForm.productId"
              filterable
              placeholder="请选择产品"
              style="width: 100%"
          >
            <el-option
                v-for="product in productOptions"
                :key="product.productId"
                :label="`${product.productCode} - ${product.productName}`"
                :value="product.productId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="生产数量" prop="orderQuantity">
          <el-input-number
              v-model="createForm.orderQuantity"
              :min="1"
              :max="9999"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-select v-model="createForm.priority" style="width: 100%">
            <el-option
                v-for="item in priorityOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="计划日期" prop="plannedDate">
          <el-date-picker
              v-model="createForm.plannedDate"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="生产线" prop="productionLine">
          <el-input v-model="createForm.productionLine"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 工单详情抽屉 -->
    <el-drawer v-model="detailVisible" title="工单详情" size="40%">
      <el-descriptions :column="1" border v-if="currentOrder">
        <el-descriptions-item label="工单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="产品型号">{{ currentOrder.productCode }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ currentOrder.orderQuantity }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ currentOrder.completedQuantity }}</el-descriptions-item>
        <el-descriptions-item label="不良品">{{ currentOrder.defectiveQuantity }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="priorityTagMap[currentOrder.priority]">
            {{ priorityMap[currentOrder.priority] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagMap[currentOrder.status]">
            {{ statusMap[currentOrder.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="计划时间">
          {{ currentOrder.plannedStartDate }} 至 {{ currentOrder.plannedEndDate }}
        </el-descriptions-item>
        <el-descriptions-item label="实际时间">
          {{ currentOrder.actualStartDate || '-' }} 至 {{ currentOrder.actualEndDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="生产线">{{ currentOrder.productionLine }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ currentOrder.creatorName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import axios from '@/utils/axios'
import { useRouter } from 'vue-router'
const router = useRouter()


// 状态映射
const statusMap = {
  1: '待排程',
  2: '已排程',
  3: '生产中',
  4: '暂停',
  5: '已完成',
  6: '已关闭'
}

const statusTagMap = {
  1: 'info',
  2: 'primary',
  3: 'success',
  4: 'warning',
  5: 'success',
  6: 'danger'
}

// 优先级映射
const priorityMap = {
  1: '紧急',
  2: '高',
  3: '普通',
  4: '低'
}

const priorityTagMap = {
  1: 'danger',
  2: 'warning',
  3: 'primary',
  4: 'info'
}

// 查询相关
const queryForm = ref({
  orderNo: '',
  productCode: '',
  status: null
})

const dateRange = ref([])
const statusOptions = [
  { value: 1, label: '待排程' },
  { value: 2, label: '已排程' },
  { value: 3, label: '生产中' },
  { value: 4, label: '暂停' },
  { value: 5, label: '已完成' },
  { value: 6, label: '已关闭' }
]

// 分页
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 创建工单相关
const createVisible = ref(false)
const productOptions = ref([])
const createForm = ref({
  productId: null,
  orderQuantity: 1,
  priority: 2,
  plannedDate: [],
  productionLine: ''
})

const priorityOptions = [
  { value: 1, label: '紧急' },
  { value: 2, label: '高' },
  { value: 3, label: '普通' },
  { value: 4, label: '低' }
]

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'blur' }],
  orderQuantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  plannedDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
  productionLine: [{ required: true, message: '请输入生产线', trigger: 'blur' }]
}

// 工单详情
const detailVisible = ref(false)
const currentOrder = ref(null)

onMounted(() => {
  fetchOrders()
  fetchProducts()
})

// 获取产品列表
const fetchProducts = async () => {
  try {
    productOptions.value = await axios.get('/order/product/list')
  } catch (error) {
    ElMessage.error('获取产品列表失败')
  }
}

// 获取工单列表
const fetchOrders = async () => {
  try {
    loading.value = true

    const res = await axios.post('/order/list', {
      page: pagination.value.current,
      size: pagination.value.size,
      orderNo: queryForm.value.orderNo,
      productCode: queryForm.value.productCode,
      status: queryForm.value.status,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    tableData.value = res.records
    pagination.value.total = res.total
  } catch (error) {
    ElMessage.error('获取工单列表失败')
  } finally {
    loading.value = false
  }
}

// 查询处理
const handleSearch = () => {
  pagination.value.current = 1
  fetchOrders()
}

const handleReset = () => {
  queryForm.value = {
    orderNo: '',
    productCode: '',
    status: null
  }
  dateRange.value = []
  handleSearch()
}

// 创建工单
const showCreateDialog = () => {
  createVisible.value = true
}

const submitCreate = async () => {
  try {
    await axios.post('/order/create', {
      productId: createForm.value.productId,
      orderQuantity: createForm.value.orderQuantity,
      priority: createForm.value.priority,
      plannedStartDate: createForm.value.plannedDate[0],
      plannedEndDate: createForm.value.plannedDate[1],
      productionLine: createForm.value.productionLine
    })
    ElMessage.success('创建成功')
    createVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

// 更新状态为已排程
const updateStatus = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认要为此工单生成排程计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await axios.post(`/order/update-status/${orderId}/2`)
    ElMessage.success('状态更新成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 显示详情
const showDetail = async (row) => {
  try {
    currentOrder.value = await axios.get(`/order/detail/${row.orderId}`)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 查看排程
const viewSchedule = (orderId) => {
  router.push(`/scheduling/plan/${orderId}`)
}
</script>

<style scoped>
.order-manage-container {
  padding: 10px;
}

.query-card {
  margin-bottom: 20px;
}

.data-card {
  margin-top: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-right {
  text-align: right;
}
</style>