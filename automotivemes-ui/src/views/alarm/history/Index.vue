<template>
  <div class="alarm-history-container">
    <div v-if="loading" class="loading">数据加载中...</div>
    <div v-else-if="error" class="error">数据加载失败: {{ error }}</div>
    <div v-else>
      <table class="alarm-table">
        <thead>
        <tr>
          <th>报警编码</th>
          <th>报警等级</th>
          <th>设备ID</th>
          <th>开始时间</th>
          <th>持续时间</th>
          <th>处理状态</th>
          <th>处理人</th>
          <th>处理方案</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="alarm in alarmHistory" :key="alarm.alarmId">
          <td>{{ alarm.alarmCode }}</td>
          <td>{{ formatAlarmLevel(alarm.alarmLevel) }}</td>
          <td>#{{ alarm.equipmentId }}</td>
          <td>{{ formatDate(alarm.startTime) }}</td>
          <td>{{ alarm.duration }}秒</td>
          <td>
              <span :class="statusClass(alarm.status)">
                {{ formatStatus(alarm.status) }}
              </span>
          </td>
          <td>{{ alarm.handler || '-' }}</td>
          <td>{{ alarm.solution || '-' }}</td>
        </tr>
        </tbody>
      </table>
      <div v-if="alarmHistory.length === 0" class="no-data">
        暂无报警历史记录
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import axios from '@/utils/axios'

const alarmHistory = ref([])
const loading = ref(true)
const error = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatAlarmLevel = (level) => {
  const levels = { 1: '警告', 2: '一般故障', 3: '严重故障' }
  return levels[level] || '未知'
}

const formatStatus = (status) => {
  const statusMap = { 0: '未处理', 1: '处理中', 2: '已处理' }
  return statusMap[status] || '未知状态'
}

const statusClass = (status) => {
  return {
    'status-tag': true,
    'unhandled': status === 0,
    'processing': status === 1,
    'resolved': status === 2
  }
}

onMounted(async () => {
  try {
    alarmHistory.value = await axios.get('/equipment/listEquipmentAlarmHistory')
  } catch (err) {
    error.value = err.message || '请求报警历史数据失败'
    console.error('获取报警历史失败:', err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.alarm-history-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.loading,
.error,
.no-data {
  padding: 20px;
  text-align: center;
  color: #666;
}

.alarm-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
}

.alarm-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

.alarm-table td {
  padding: 12px;
  border-bottom: 1px solid #e8e8e8;
  color: #606266;
}

.alarm-table tr:hover {
  background-color: #fafafa;
}

.status-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.unhandled {
  background-color: #ffebee;
  color: #f44336;
}

.processing {
  background-color: #fff3e0;
  color: #ff9800;
}

.resolved {
  background-color: #e8f5e9;
  color: #4caf50;
}

.no-data {
  padding: 16px;
  color: #909399;
  text-align: center;
}
</style>