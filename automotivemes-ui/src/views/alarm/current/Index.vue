<template>
  <!-- 报警表格 -->
  <div class="alarm-table">
    <div class="table-header">
      <div class="header-item">报警代码</div>
      <div class="header-item">等级</div>
      <div class="header-item">设备ID</div>
      <div class="header-item">开始时间</div>
      <div class="header-item">状态</div>
      <div class="header-item">操作</div>
    </div>

    <div
        v-for="alarm in alarmList"
        :key="alarm.alarmId"
        class="table-row"
        :class="alarmStatusClass(alarm)">
      <div class="row-item">{{ alarm.alarmCode }}</div>
      <div class="row-item">
        <span class="level-tag">{{ formatLevel(alarm.alarmLevel) }}</span>
      </div>
      <div class="row-item">#{{ alarm.equipmentId }}</div>
      <div class="row-item">{{ formatDateTime(alarm.startTime) }}</div>
      <div class="row-item">{{ formatStatus(alarm.status) }}</div>
      <div class="row-item">
        <button
            v-if="alarm.status === 0"
            @click="handleAlarm(alarm.alarmId)"
            class="handle-btn">
          立即处理
        </button>
        <span v-else class="handled-text">已处理</span>
      </div>
    </div>

    <div v-if="alarmList.length === 0" class="empty-placeholder">
      当前没有未处理的报警
    </div>
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import axios from '@/utils/axios'
import {Client} from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const alarmList = ref([])
const stompClient = ref(null)

// 格式化报警等级
const formatLevel = (level) => {
  const levels = ['', '警告', '一般故障', '严重故障']
  return levels[level] || '未知'
}

// 格式化状态
const formatStatus = (status) => {
  return ['未处理', '处理中', '已处理'][status] || '未知'
}

// 格式化时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString()
}

// 报警状态样式
const alarmStatusClass = (alarm) => {
  return {
    'critical-alarm': alarm.alarmLevel === 3,
    'warning-alarm': alarm.alarmLevel === 2,
    'info-alarm': alarm.alarmLevel === 1
  }
}

// 处理报警
const handleAlarm = async (alarmId) => {
  try {
    await axios.post('/equipment/handleAlarm', null, {
      params: { alarmId }
    })

    // 更新本地状态
    const index = alarmList.value.findIndex(a => a.alarmId === alarmId)
    if (index > -1) {
      alarmList.value[index].status = 2
      alarmList.value[index].endTime = new Date().toISOString()
    }
  } catch (error) {
    console.error('处理报警失败:', error)
  }
}

// 初始化WebSocket
const initWebSocket = () => {
  const socket = new SockJS('http://localhost:3000/mes-websocket')
  stompClient.value = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onStompError: (frame) => {
      console.error('WebSocket错误:', frame.headers.message)
    }
  })

  stompClient.value.onConnect = () => {
    // 订阅报警主题
    stompClient.value.subscribe(
        '/topic/equipment/alarm',
        message => {
          const newAlarm = JSON.parse(message.body)
          // 添加到列表开头
          alarmList.value.unshift(newAlarm)
        }
    )
  }

  stompClient.value.activate()
}

// 获取初始报警数据
const fetchActiveAlarms = async () => {
  try {
    alarmList.value = await axios.get('/equipment/listRealTimeAlarms')
  } catch (error) {
    console.error('获取报警数据失败:', error)
  }
}

// 组件挂载时
onMounted(() => {
  fetchActiveAlarms()
  initWebSocket()
})

// 组件卸载时
onUnmounted(() => {
  if (stompClient.value) {
    stompClient.value.deactivate()
  }
})
</script>

<style scoped>
.alarm-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  overflow: hidden;
}

.table-header {
  display: flex;
  background: #f8f9fa;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
  font-weight: 600;
}

.header-item {
  flex: 1;
  padding: 0 16px;
  text-align: center;
}

.table-row {
  display: flex;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
  transition: background 0.3s;
}

.table-row:hover {
  background: #f8f9fa;
}

.row-item {
  flex: 1;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.level-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.critical-alarm .level-tag {
  background: #dc3545;
}

.warning-alarm .level-tag {
  background: #ffc107;
  color: #333;
}

.info-alarm .level-tag {
  background: #17a2b8;
}

.handle-btn {
  padding: 6px 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.handle-btn:hover {
  opacity: 0.8;
}

.handled-text {
  color: #28a745;
  font-size: 14px;
}

.empty-placeholder {
  text-align: center;
  padding: 40px 0;
  color: #6c757d;
}
</style>