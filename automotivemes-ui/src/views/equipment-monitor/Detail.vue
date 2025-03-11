<template>
  <div v-if="device" class="device-detail">
    <h2>{{ device.name }} - 详细信息</h2>
    <RealtimeChart :data="chartData" />

    <div class="control-panel">
      <button @click="sendCommand('start')">启动设备</button>
      <button @click="sendCommand('stop')">停止设备</button>
    </div>

    <div class="data-grid">
      <div v-for="(value, key) in realtimeData"
           :key="key" class="data-item">
        <span class="label">{{ key }}</span>
        <span class="value">{{ value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import RealtimeChart from '@/components/RealtimeChart.vue'

const props = defineProps(['id'])
const store = useStore()

const device = computed(() =>
    store.state.device.deviceList.find(d => d.id === props.id)
)

const realtimeData = computed(() =>
    store.state.device.realtimeData[props.id] || {}
)

const chartData = computed(() => ({
  labels: Object.keys(realtimeData.value),
  datasets: [{
    label: '实时数据',
    data: Object.values(realtimeData.value),
    backgroundColor: 'rgba(54, 162, 235, 0.2)',
    borderColor: 'rgb(54, 162, 235)'
  }]
}))

const sendCommand = async (command) => {
  await api.post(`/devices/${props.id}/command`, { command })
}
</script>