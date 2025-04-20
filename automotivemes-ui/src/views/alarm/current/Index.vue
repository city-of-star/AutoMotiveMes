<template>
  <!-- 报警表格 -->
  <div class="alarm-container">
    <el-card class="alarm-card">
      <template #header>
        <div class="card-header">
          <h3 class="mes-title">生产设备实时报警监控</h3>
          <div class="header-tag">
            <el-tag type="info">在线设备: {{ onlineCount }}</el-tag>
            <el-tag type="success">正常设备: {{ normalCount }}</el-tag>
          </div>
        </div>
      </template>

      <el-table
          :data="alarmList"
          style="width: 100%"
          :empty-text="'当前没有未处理的报警'"
          v-loading="loading"
          stripe
      >
        <el-table-column
            prop="alarmCode"
            label="报警代码"
            width="200"
            align="center"
            header-align="center">
          <template #default="{ row }">
            <el-tag effect="dark" :type="getCodeTagType(row)">
              {{ row.alarmCode }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="alarmLevel"
            label="等级"
            width="140"
            align="center"
            header-align="center">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row)" effect="light">
              {{ formatLevel(row.alarmLevel) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            label="设备详情"
            width="120"
            align="center"
            header-align="center">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="small"
                @click="showEquipmentDetail(row)"
            >查看</el-button>
          </template>
        </el-table-column>

        <el-table-column
            prop="alarmReason"
            label="报警原因"
            width="400"
            align="center"
            header-align="center">
        </el-table-column>

        <el-table-column
            prop="startTime"
            label="开始时间"
            width="200"
            align="center"
            header-align="center">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>

        <el-table-column
            prop="status"
            label="状态"
            width="140"
            align="center"
            header-align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.alarmStatus)" size="small">
              {{ formatStatus(row.alarmStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            label="操作"
            width="180"
            align="center"
            header-align="center">
          <template #default="{ row }">
            <el-button
                v-if="row.alarmStatus === 0"
                type="primary"
                size="small"
                @click="showHandleDialog(row)"
            >立即处理</el-button
            >
            <el-tag v-else type="success" effect="plain" size="small">已处理</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 设备详情弹窗 -->
    <el-dialog
        v-model="equipmentDialogVisible"
        title="设备详细信息"
        width="1400px"
        :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <!-- 基础信息 -->
        <el-descriptions-item label="设备ID" :span="1" min-width="120">{{ currentEquipment.equipmentId }}</el-descriptions-item>
        <el-descriptions-item label="设备代码" :span="1">{{ currentEquipment.equipmentCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称" :span="1">{{ currentEquipment.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="设备型号" :span="1">{{ currentEquipment.equipmentModel }}</el-descriptions-item>

        <!-- 状态信息 -->
        <el-descriptions-item label="所在位置" :span="2">{{ currentEquipment.location }}</el-descriptions-item>
        <el-descriptions-item label="设备状态" :span="1">
          <el-tag :type="getEquipmentStatusTag(currentEquipment.equipmentStatus)" size="small">
            {{ formatEquipmentStatus(currentEquipment.equipmentStatus) }}
          </el-tag>
        </el-descriptions-item>

        <!-- 日期信息 -->
        <el-descriptions-item label="生产日期" :span="1">{{ formatDate(currentEquipment.productionDate) }}</el-descriptions-item>
        <el-descriptions-item label="安装日期" :span="1">{{ formatDate(currentEquipment.installationDate) }}</el-descriptions-item>
        <el-descriptions-item label="最后维护" :span="1">{{ formatDate(currentEquipment.lastMaintenanceDate) }}</el-descriptions-item>
        <el-descriptions-item label="维护周期" :span="1">{{ currentEquipment.maintenanceCycle }}天</el-descriptions-item>

        <!-- 厂商信息 -->
        <el-descriptions-item label="制造商" :span="2">{{ currentEquipment.manufacturer }}</el-descriptions-item>

        <!-- 参数配置（处理JSON数组） -->
        <el-descriptions-item :span="2" label="参数配置">
          <div v-if="currentEquipment.parametersConfig" class="param-config">
            <div v-for="(param, index) in JSON.parse(currentEquipment.parametersConfig)" :key="index" class="param-item">
              <span class="param-name">{{ param.name }}</span>
              <span class="param-unit">（{{ param.unit }}）</span>
              <el-tag size="small" type="info" class="param-range">
                正常范围：{{ param.normalMin }} ~ {{ param.normalMax }}
              </el-tag>
            </div>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>

        <!-- 设备描述 -->
        <el-descriptions-item :span="2" label="设备描述">
          <pre class="equipment-description">{{ currentEquipment.description || '暂无描述' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 维护记录弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        title="报警处理维护"
        width="600px"
        :close-on-click-modal="false"
    >
      <el-form
          :model="maintenanceForm"
          :rules="formRules"
          ref="formRef"
          label-width="100px"
      >
        <el-form-item label="维护内容：" prop="maintenanceContent">
          <el-input
              v-model="maintenanceForm.maintenanceContent"
              type="textarea"
              :rows="3"
              placeholder="请输入具体维护措施和更换部件信息"
          />
        </el-form-item>

        <el-form-item label="处理结果：" prop="result">
          <el-input
              v-model="maintenanceForm.result"
              type="textarea"
              :rows="2"
              placeholder="请输入故障处理结果"
          />
        </el-form-item>

        <el-form-item label="维护成本：" prop="cost">
          <el-input-number
              v-model="maintenanceForm.cost"
              :min="0"
              :precision="2"
              :step="100"
              controls-position="right"
              style="width: 200px"
          >
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitMaintenance">确认提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount} from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { websocket } from '@/utils/websocket'

// 响应式数据
const alarmList = ref([])
const loading = ref(true)
const dialogVisible = ref(false)
const currentAlarmId = ref(null)
const formRef = ref(null)
const onlineCount = ref(0)
const normalCount = ref(0)
const equipmentDialogVisible = ref(false)
const currentEquipment = ref({})

// 设备状态格式化方法
const formatEquipmentStatus = (status) => {
  const statusMap = ['未知', '正常', '故障', '维护中']
  return statusMap[status] || '未知状态'
}

// 设备状态标签样式
const getEquipmentStatusTag = (status) => {
  const typeMap = ['', 'success', 'danger', 'warning']
  return typeMap[status] || ''
}

// 日期格式化（仅日期）
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\//g, '-')
}

// 显示设备详情方法
const showEquipmentDetail = (row) => {
  currentEquipment.value = row
  equipmentDialogVisible.value = true
}

// 维护表单数据
const maintenanceForm = ref({
  maintenanceContent: '',
  result: '',
  cost: 0
})

// 表单验证规则
const formRules = {
  maintenanceContent: [
    { required: true, message: '维护内容不能为空', trigger: 'blur' }
  ],
  result: [
    { required: true, message: '处理结果不能为空', trigger: 'blur' }
  ],
  cost: [
    { required: true, message: '维护成本不能为空' },
    { type: 'number', min: 0, message: '成本不能为负数' }
  ]
}

// 格式化方法
const formatLevel = (level) => {
  return ['警告', '一般故障', '严重故障'][level - 1] || '未知等级'
}

const formatStatus = (status) => {
  return ['未处理', '处理中', '已处理'][status] || '未知状态'
}

const formatDateTime = (datetime) => {
  return datetime ? new Date(datetime).toLocaleString() : '-'
}

// 标签样式计算
const getLevelTagType = (row) => {
  return {
    1: 'warning',
    2: 'danger',
    3: 'danger'
  }[row.alarmLevel]
}

const getCodeTagType = (row) => {
  return row.alarmLevel === 3 ? 'danger' : row.alarmLevel === 2 ? 'warning' : 'info'
}

const getStatusTagType = (status) => {
  return ['danger', 'warning', 'success'][status]
}

// 显示维护弹窗
const showHandleDialog = (row) => {
  currentAlarmId.value = row.alarmId
  maintenanceForm.value = { cost: 0 } // 重置表单
  dialogVisible.value = true
}

// 提交维护记录
const submitMaintenance = async () => {
  try {
    await formRef.value.validate()

    await axios.post('/equipment/handleAlarm', {
      alarmId: currentAlarmId.value,
      maintenanceContent: maintenanceForm.value.maintenanceContent,
      result: maintenanceForm.value.result,
      cost: maintenanceForm.value.cost
    })

    // 更新本地报警状态
    const index = alarmList.value.findIndex(a => a.alarmId === currentAlarmId.value)
    if (index > -1) {
      alarmList.value[index].alarmStatus = 2
      alarmList.value[index].endTime = new Date().toISOString()
    }

    ElMessage.success('维护记录提交成功')
    dialogVisible.value = false
  } catch (error) {
    if (error?.errors) return
    console.error('提交失败:', error)
    ElMessage.error('维护记录提交失败')
  }
}

// 获取正常和在线的设备数量
const getEquipmentCount = async () => {
  try {
    const res = await axios.get('/equipment/getEquipmentCount')
    onlineCount.value = res.onlineEquipmentCount
    normalCount.value = res.normalEquipmentCount
  } catch (error) {
    console.log(error)
  }
}

// 获取初始数据
const fetchInitialData = async () => {
  try {
    alarmList.value = await axios.get('/equipment/listRealTimeAlarms')
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

// WebSocket处理
websocket.subscribe('/topic/equipment/alarm', (message) => {
  try {
    const newAlarm = JSON.parse(message.body);

    // 深拷贝对象并更新数组
    alarmList.value = [{ ...newAlarm }, ...alarmList.value];

    // 更新设备计数（根据实际业务逻辑调整）
    updateCounters(newAlarm);
  } catch (e) {
    console.error('消息处理失败:', e);
    ElMessage.error('实时报警数据异常');
  }
});

// 独立的计数更新函数
function updateCounters(newAlarm) {
  // 假设消息中包含设备状态字段 equipmentStatus
  if (newAlarm.equipmentStatus === 2) {  // 2代表故障
    onlineCount.value = Math.max(0, onlineCount.value - 1);
  }
  // 如果报警状态为已处理，可能增加正常设备数
  if (newAlarm.alarmStatus === 2) {
    normalCount.value += 1;
  }
}

onMounted(() => {
  fetchInitialData()
  getEquipmentCount()
})

onBeforeUnmount(() => {
  websocket.unsubscribe('/topic/equipment/alarm');
});
</script>

<style scoped>
.alarm-container {
  padding: 20px;
  background: #f5f7fa;
}

.mes-title {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.header-tag {
  display: flex;
  gap: 12px;
}

.alarm-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

/* 参数配置样式 */
.param-config {
  line-height: 1.8;
}
.param-item {
  margin: 6px 0;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}
.param-name {
  font-weight: 500;
  color: #409eff;
}
.param-unit {
  color: #909399;
}
.param-range {
  margin-left: 12px;
}

/* 设备描述样式 */
.equipment-description {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}

</style>