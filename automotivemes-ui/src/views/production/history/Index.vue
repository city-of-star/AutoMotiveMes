<template>
  <div class="history-container">
    <!-- 查询条件 -->
    <el-card class="search-card">
      <el-form :model="queryForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="生产日期">
              <el-date-picker
                  v-model="queryForm.dateRange"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="生产线">
              <el-select v-model="queryForm.line" placeholder="请选择">
                <el-option
                    v-for="item in productionLines"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="产品类型">
              <el-select v-model="queryForm.productType" placeholder="请选择">
                <el-option
                    v-for="item in productTypes"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card">
      <div class="table-header">
        <div class="total">共 {{ total }} 条记录</div>
        <el-button type="primary" @click="exportData">导出数据</el-button>
      </div>

      <el-table
          :data="tableData"
          stripe
          style="width: 100%"
          v-loading="loading"
      >
        <el-table-column prop="orderNo" label="生产订单号" width="180" />
        <el-table-column prop="productType" label="产品类型" width="120" />
        <el-table-column prop="line" label="生产线" width="100" />
        <el-table-column prop="quantity" label="数量" sortable width="100" />
        <el-table-column prop="goodRate" label="良品率" sortable width="120">
          <template #default="{ row }">
            <el-tag :type="row.goodRate >= 95 ? 'success' : 'danger'">
              {{ row.goodRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="完成时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          class="pagination"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="dialogVisible" title="生产详情" width="50%">
      <div v-if="currentDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="产品类型">{{ currentDetail.productType }}</el-descriptions-item>
          <el-descriptions-item label="生产线">{{ currentDetail.line }}</el-descriptions-item>
          <el-descriptions-item label="计划数量">{{ currentDetail.quantity }}</el-descriptions-item>
          <el-descriptions-item label="实际完成">{{ currentDetail.actualQuantity }}</el-descriptions-item>
          <el-descriptions-item label="良品率">{{ currentDetail.goodRate }}%</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentDetail.startTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ currentDetail.endTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentDetail.remark }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 模拟数据
const generateData = () => {
  const data = []
  for (let i = 0; i < 50; i++) {
    data.push({
      orderNo: `PO202307${String(i+1).padStart(3, '0')}`,
      productType: ['轿车', 'SUV', 'MPV'][i%3],
      line: `生产线 ${(i%4)+1}`,
      quantity: Math.floor(Math.random() * 1000 + 500),
      actualQuantity: Math.floor(Math.random() * 1000 + 480),
      goodRate: (95 + Math.random() * 4).toFixed(1),
      startTime: `2023-07-${String((i%28)+1).padStart(2,'0')} 08:00`,
      endTime: `2023-07-${String((i%28)+1).padStart(2,'0')} 16:00`,
      remark: i%5 === 0 ? '特殊工艺要求' : '标准生产流程'
    })
  }
  return data
}

// 查询表单
const queryForm = reactive({
  dateRange: [],
  line: '',
  productType: ''
})

const productionLines = ref([
  { value: 'line1', label: '生产线 1' },
  { value: 'line2', label: '生产线 2' },
  { value: 'line3', label: '生产线 3' }
])

const productTypes = ref([
  { value: 'car', label: '轿车' },
  { value: 'suv', label: 'SUV' },
  { value: 'mpv', label: 'MPV' }
])

// 表格数据
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 详情对话框
const dialogVisible = ref(false)
const currentDetail = ref(null)

// 查询处理
const handleSearch = () => {
  loading.value = true
  // 模拟接口请求
  setTimeout(() => {
    tableData.value = generateData()
    total.value = 50
    loading.value = false
  }, 500)
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.line = ''
  queryForm.productType = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  handleSearch()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  handleSearch()
}

// 导出数据
const exportData = () => {
  ElMessage.success('导出数据功能开发中')
}

// 查看详情
const viewDetail = (row) => {
  currentDetail.value = row
  dialogVisible.value = true
}

// 初始化加载数据
handleSearch()
</script>

<style scoped>
.history-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.total {
  color: #666;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.el-descriptions {
  margin-top: 20px;
}
</style>