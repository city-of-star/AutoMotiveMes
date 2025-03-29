<template>
  <div class="equipment-list">
    <div v-for="equipment in equipments"
         :key="equipment.equipmentId"
         class="equipment-card"
         :class="equipmentStatusClass(equipment.equipmentId)"
         style="height: 300px;"
    >
      <h3 @click="goDetail(equipment.equipmentId)">{{ equipment.equipmentName }}</h3>
      <div v-if="realtimeData[equipment.equipmentId]">
        <p>温度: {{ realtimeData[equipment.equipmentId].temperature }}℃</p>
        <p>转速: {{ realtimeData[equipment.equipmentId].rpm }} RPM</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

// store.dispatch('user/logout')

// 获取设备数据
store.dispatch('equipment/fetchEquipments')

// 动态显示设备数据
const equipments = computed(() => store.state.equipment.equipmentList)
const realtimeData = computed(() => store.state.equipment.realtimeData)

// 根据缓存信息，更新设备状态样式
const equipmentStatusClass = () => {
  const status = '运行';

  return {
    'status-normal': status === '运行',
    'status-maintenance': status === '维护',
    'status-warning': status === '待机',
    'status-error': status === '故障',
  }
}

// 跳转到设备详情页
const goDetail = (id) => {
  router.push(`/equipment/${id}`)
}
</script>

<style scoped>
.equipment-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.equipment-card {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.status-normal { border-left: 4px solid #67C23A; }
.status-maintenance { border-left: 4px solid #3a65c2; }
.status-warning { border-left: 4px solid #E6A23C; }
.status-error { border-left: 4px solid #F56C6C; }
</style>