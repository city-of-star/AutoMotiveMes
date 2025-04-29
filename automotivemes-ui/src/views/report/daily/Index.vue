<template>
  <div class="daily-report-container">
    <!-- 头部筛选区域 -->
    <div class="filter-header">
      <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
      />
      <el-button type="primary" @click="fetchData">刷新数据</el-button>
      <el-button type="success" @click="exportExcel" :loading="loading.export">导出Excel</el-button>
    </div>

    <!-- 概况统计 -->
    <div class="summary-cards">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>生产概况</span>
          </div>
        </template>
        <div v-loading="loading.summary" class="card-content">
          <div class="summary-item">
            <span class="label">总产量：</span>
            <el-tag type="info">{{ summaryData.totalOutput || 0 }}</el-tag>
          </div>
          <div class="summary-item">
            <span class="label">合格率：</span>
            <el-tag type="success">{{ summaryData.qualifiedRate || 0 }}%</el-tag>
          </div>
          <div class="summary-item">
            <span class="label">设备利用率：</span>
            <el-tag type="warning">{{ summaryData.equipmentUtilization || 0 }}%</el-tag>
          </div>
          <div class="summary-item">
            <span class="label">异常记录：</span>
            <el-tag type="danger">{{ summaryData.abnormalRecords || 0 }}</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 生产明细表格 -->
    <div class="detail-table">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>生产明细</span>
          </div>
        </template>
        <el-table
            :data="detailData.records"
            v-loading="loading.details"
            style="width: 100%"
        >
          <el-table-column prop="orderNo" label="工单号" align="center"/>
          <el-table-column prop="productCode" label="产品型号" align="center"/>
          <el-table-column prop="processName" label="工序" align="center"/>
          <el-table-column prop="equipmentCode" label="设备编号" align="center"/>
          <el-table-column prop="outputQuantity" label="产出量" align="center"/>
          <el-table-column prop="defectiveQuantity" label="不良数" align="center"/>
          <el-table-column prop="operatorName" label="操作员" align="center"/>
          <el-table-column label="生产时间" align="center">
            <template #default="{row}">
              {{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="duration" label="时长" align="center"/>
        </el-table>
        <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="detailData.total"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
            layout="total, sizes, prev, pager, next, jumper"
            style="float: right"
        />
      </el-card>
    </div>

    <!-- 底部统计区域 -->
    <div class="bottom-stats">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>工单进度</span>
              </div>
            </template>
            <div v-loading="loading.progress" class="progress-list">
              <div v-for="item in orderProgress" :key="item.orderNo" class="progress-item">
                <div class="order-info">
                  <span class="order-no">{{ item.orderNo }}</span>
                  <span class="product-name">{{ item.productName }}</span>
                </div>
                <el-progress
                    :percentage="item.progressRate || 0"
                    :status="getProgressStatus(item.progressRate)"
                />
                <div class="quantity-info">
                  <span>已完成 {{ item.completedQuantity }}/{{ item.totalQuantity }}</span>
                  <span>当前工序：{{ item.currentProcess }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>设备状态分布</span>
              </div>
            </template>
            <div v-loading="loading.equipment" class="equipment-stats">
              <el-row :gutter="20">
                <el-col :span="6" v-for="(value, key) in equipmentStatus" :key="key">
                  <div class="status-item">
                    <div class="status-label">{{ getStatusLabel(key) }}</div>
                    <div class="status-value">{{ value }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import axios from '@/utils/axios'
import { saveAs } from 'file-saver';
import ExcelJS from 'exceljs';
import {ElMessage} from "element-plus";

// 响应式数据
const selectedDate = ref(new Date().toISOString().split('T')[0])
const loading = reactive({
  summary: false,
  details: false,
  progress: false,
  equipment: false,
  export: false
})

const summaryData = ref({})
const detailData = reactive({
  records: [],
  total: 0
})
const orderProgress = ref([])
const equipmentStatus = ref({})
const pagination = reactive({
  current: 1,
  size: 10
})

// 状态标签映射
const statusLabels = {
  totalEquipment: '总设备数',
  normalCount: '正常',
  maintenanceCount: '维护中',
  standbyCount: '待机',
  scrapCount: '报废'
}

// 方法
const fetchData = async () => {
  try {
    // 获取概况数据
    loading.summary = true
    summaryData.value = await axios.get('/report/daily/summary', {
      params: {date: selectedDate.value}
    })

    // 获取明细数据
    loading.details = true
    const detailRes = await axios.get('/report/daily/details', {
      params: {
        date: selectedDate.value,
        page: pagination.current,
        size: pagination.size
      }
    })
    detailData.records = detailRes.records
    detailData.total = detailRes.total

    // 获取工单进度
    loading.progress = true
    orderProgress.value = await axios.get('/report/daily/order-progress', {
      params: {date: selectedDate.value}
    })

    // 获取设备状态
    loading.equipment = true
    equipmentStatus.value = await axios.get('/report/daily/equipment-status', {
      params: {date: selectedDate.value}
    })
  } catch (error) {
    console.error('数据获取失败:', error)
  } finally {
    loading.summary = false
    loading.details = false
    loading.progress = false
    loading.equipment = false
  }
}

const handleDateChange = () => {
  pagination.current = 1
  fetchData()
}

const handlePageChange = (newPage) => {
  pagination.current = newPage
  fetchData()
}

const handleSizeChange = (newSize) => {
  pagination.size = newSize
  // 切换每页条数后重置到第一页
  pagination.current = 1
  fetchData()
}

const formatTime = (datetime) => {
  return datetime ? datetime.slice(11, 16) : '--'
}

const getProgressStatus = (percentage) => {
  return percentage >= 100 ? 'success' : 'primary'
}

const getStatusLabel = (key) => {
  return statusLabels[key] || key
}

// 导出逻辑
const exportExcel = async () => {
  try {
    loading.export = true;

    // 获取全部数据（分页请求）
    let allData = [];
    let currentPage = 1;
    let total = 0;
    const pageSize = 1000; // 每次请求1000条

    do {
      const res = await axios.get('/report/daily/details', {
        params: {
          date: selectedDate.value,
          page: currentPage,
          size: pageSize
        }
      });
      allData = allData.concat(res.records);
      total = res.total;
      currentPage++;
    } while (allData.length < total);

    allData.reverse();  // 倒序排列

    // 创建Excel工作簿
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('生产明细');

    // 设置列定义（包含宽度和样式）
    worksheet.columns = [
      { header: '工单号', key: 'orderNo', width: 24 },
      { header: '产品型号', key: 'productCode', width: 18 },
      { header: '工序', key: 'processName', width: 15 },
      { header: '设备编号', key: 'equipmentCode', width: 15 },
      { header: '产出量', key: 'outputQuantity', width: 12 },
      { header: '不良数', key: 'defectiveQuantity', width: 12 },
      { header: '操作员', key: 'operatorName', width: 14 },
      { header: '生产时间', key: 'productionTime', width: 28 },
      { header: '时长(分钟)', key: 'duration', width: 14 }
    ];

    // 设置标题行样式
    const titleRow = worksheet.getRow(1);
    titleRow.eachCell(cell => {
      cell.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'FF4F81BD' } // 深蓝色背景
      };
      cell.font = {
        bold: true,
        color: { argb: 'FFFFFFFF' } // 白色字体
      };
      cell.alignment = {
        vertical: 'middle',
        horizontal: 'center'
      };
    });

    // 添加数据行
    allData.forEach(record => {
      worksheet.addRow({
        orderNo: record.orderNo,
        productCode: record.productCode,
        processName: record.processName,
        equipmentCode: record.equipmentCode,
        outputQuantity: record.outputQuantity,
        defectiveQuantity: record.defectiveQuantity,
        operatorName: record.operatorName,
        productionTime: `${formatTime(record.startTime)} ~ ${formatTime(record.endTime)}`,
        duration: record.duration
      });
    });

    // 设置数据行样式
    worksheet.eachRow({ from: 2 }, (row, rowNumber) => {
      row.eachCell(cell => {
        cell.alignment = {
          vertical: 'middle',
          horizontal: 'center'
        };

        // 隔行变色
        if (rowNumber % 2 === 0) {
          cell.fill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: { argb: 'FFF2F4F7' } // 浅灰色背景
          };
        }
      });
    });

    // 生成文件并保存
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    saveAs(blob, `生产日报_${selectedDate.value}.xlsx`);

  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请检查网络或重试');
  } finally {
    loading.export = false;
  }
};

// 生命周期钩子
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.daily-report-container {
  padding: 20px;
}

.filter-header {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

.summary-cards {
  margin-bottom: 20px;
}

.card-content {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-table {
  margin-bottom: 20px;
}

.bottom-stats {
  margin-top: 20px;
}

.progress-list {
  padding: 10px;
}

.progress-item {
  margin-bottom: 15px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.quantity-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.status-item {
  text-align: center;
  padding: 15px;
  border-radius: 4px;
  background: #f5f7fa;
}

.status-label {
  color: #909399;
  margin-bottom: 8px;
}

.status-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}
</style>