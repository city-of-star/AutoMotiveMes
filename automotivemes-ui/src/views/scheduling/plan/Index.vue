<template>
  <div class="schedule-plan-container">
    <el-card class="header-card">
      <el-page-header @back="goBack" title="返回工单列表">
        <template #content>
          <span class="header-title">排程计划 - {{ orderNo }}</span>
        </template>
      </el-page-header>

      <div class="action-bar">
        <el-button
            type="primary"
            @click="generateSchedule"
            :disabled="schedules.length > 0"
        >
          生成排程计划
        </el-button>
        <el-button @click="refresh">刷新</el-button>
      </div>
    </el-card>

    <el-card class="data-card">
      <el-table
          :data="schedules"
          v-loading="loading"
          border
          stripe
      >
        <el-table-column prop="processName" label="工序" align="center"/>
        <el-table-column prop="equipmentName" label="设备" align="center">
          <template #default="{row}">
            <div>{{ row.equipmentCode }} - {{ row.equipmentName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="计划时间" lign="center" align="center">
          <template #default="{row}">
            <div class="time-range">
              <div>{{ formatTime(row.plannedStartTime) }}</div>
              <div>至</div>
              <div>{{ formatTime(row.plannedEndTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作员" align="center"/>
        <el-table-column label="状态" align="center">
          <template #default="{row}">
            <el-tag :type="scheduleStatusTagMap[row.scheduleStatus]">
              {{ scheduleStatusMap[row.scheduleStatus] }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {useRoute, useRouter} from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const route = useRoute()
const router = useRouter()

const orderId = ref(route.params.orderId === ':orderId' ? 1 : route.params.orderId)
console.log(orderId.value)
const orderNo = ref('')
const schedules = ref([])
const loading = ref(false)

// 状态映射
const scheduleStatusMap = {
  1: '待执行',
  2: '执行中',
  3: '已完成',
  4: '已延迟'
}

const scheduleStatusTagMap = {
  1: 'info',
  2: 'primary',
  3: 'success',
  4: 'warning'
}

onMounted(() => {
  fetchOrderDetail()
  fetchSchedules()
})

// 获取工单基本信息
const fetchOrderDetail = async () => {
  try {
    const res = await axios.get(`/order/detail/${orderId.value}`)
    orderNo.value = res.orderNo
  } catch (error) {
    ElMessage.error('获取工单信息失败')
  }
}

// 获取排程计划
const fetchSchedules = async () => {
  try {
    loading.value = true
    const res = await axios.get(`/order/schedules/${orderId.value}`, {
      params: {
        page: 1,
        size: 100
      }
    })
    schedules.value = res.records
  } catch (error) {
    ElMessage.error('获取排程计划失败')
  } finally {
    loading.value = false
  }
}

// 生成排程
const generateSchedule = async () => {
  try {
    await axios.post(`/order/generate-schedule/${orderId.value}`)
    ElMessage.success('排程生成成功')
    fetchSchedules()
  } catch (error) {
    console.log(error)
  }
}

// 时间格式化
const formatTime = (time) => {
  return new Date(time).toLocaleString()
}

// 页面操作
const goBack = () => {
  router.push('/scheduling/orders')
}

const refresh = () => {
  fetchSchedules()
}
</script>

<style scoped>
.schedule-plan-container {
  padding: 10px;
}

.header-card {
  margin-bottom: 20px;
}

.header-title {
  font-size: 18px;
  margin-left: 10px;
}

.action-bar {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.data-card {
  margin-top: 20px;
}

.time-range {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #666;
}
</style>