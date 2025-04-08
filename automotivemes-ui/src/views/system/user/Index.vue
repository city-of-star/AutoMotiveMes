<template>
  <div class="main-container">

    <div class="left-container">
      <el-input v-model="searchData.deptId" placeholder="请输入部门名称" class="input-dept" clearable>
        <template #prefix>
          <el-icon class="el-input__icon"><Search /></el-icon>
        </template>
      </el-input>
      <el-tree
          :data="deptData"
          :props="deptProps"
          @node-click="handleDeptNodeClick"
          :highlight-current="true"
          node-key="deptId"
          class="tree-container"
      />
    </div>

    <div class="right-container">

      <div class="input-container">
        <div class="block">
          <span class="input-title">用户名称</span>
          <el-input v-model="searchData.username" placeholder="请输入用户名称" class="input" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="input-title">手机号码</span>
          <el-input v-model="searchData.phone" placeholder="请输入手机号码" class="input" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="input-title">用户状态</span>
          <el-select v-model="searchData.status" placeholder="用户状态" class="input" clearable>
            <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>
        <div class="block">
          <span class="input-title">创建时间</span>
          <el-date-picker
              v-model="searchData.time"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="default"
              style="width: 220px;"
              clearable
          />
        </div>
        <el-button @click="search()" :icon="Search" type="primary">搜索</el-button>
        <el-button @click="refresh()" :icon="Refresh">重置</el-button>
      </div>

      <div class="btn-container">
        <el-button v-if="hasPermission('system:user:add')"  :icon="Plus" :color=theme_color plain>新增</el-button>
        <el-button v-if="hasPermission('system:user:update')"  :icon="Edit" :color=btn_update_color plain>修改</el-button>
        <el-button v-if="hasPermission('system:user:delete')"  :icon="Delete" :color=btn_delete_color plain>删除</el-button>
        <el-button v-if="hasPermission('system:user:import')"  :icon="Download" :color=btn_import_color plain>导入</el-button>
        <el-button v-if="hasPermission('system:user:export')"  :icon="Upload" :color=btn_export_color plain>导出</el-button>
      </div>

      <el-table v-loading="loading" class="table-container" :data="tableData">
        <el-table-column width="80" align="center" header-align="center">
          <template #header>
            <el-checkbox
                v-model="headerSelect"
                :indeterminate="tableData.some(row => row.select) && !headerSelect"
            />
          </template>
          <template #default="{ row }">
            <el-checkbox v-model="row.select" />
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户编号" width="80" align="center" header-align="center" />
        <el-table-column prop="username" label="用户名称" width="120" align="center" header-align="center" />
        <el-table-column prop="realName" label="真实姓名" width="120" align="center" header-align="center" />
        <el-table-column prop="deptName" label="部门" width="200" align="center" header-align="center" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号码" width="150" align="center" header-align="center" />
        <el-table-column prop="status" label="状态" width="120" align="center" header-align="center">
          <template #default="scope">
            <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleSwitchChange(scope.row.status)"
                style="margin: 0 auto"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" header-align="center"/>
        <el-table-column label="操作" width="180" align="center" header-align="center">

        </el-table-column>
      </el-table>
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          style="float: right; margin-top: 30px"
      />
    </div>
  </div>
</template>

<script setup>
import { Search, Refresh, Plus, Edit, Delete, Download, Upload } from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import {computed, onMounted, ref} from 'vue'
import { useStore } from 'vuex'

const store = useStore()

// 主题和按钮颜色
const theme_color = store.state.user.theme_color;
const btn_update_color = store.state.app.btn_update_color;
const btn_delete_color = store.state.app.btn_delete_color;
const btn_import_color = store.state.app.btn_import_color;
const btn_export_color = store.state.app.btn_export_color;

// 查询的条件值
let searchData = ref({
  deptId: null,
  username: '',
  phone: '',
  status: null,
  time: [],
})

// 状态选择器的选择框
const statusOptions = [
  {
    value: 1,
    label: '正常',
  },
  {
    value: 0,
    label: '停用',
  },
]

// 分页相关变量
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])  // 用户列表
let loading = ref(false)  // 加载状态

// 部门树状组件数据
const deptData = ref([])
const deptProps = {
  children: 'children',
  label: 'deptName',
  value: 'deptId'
}

// 全选计算属性
const headerSelect = computed({
  get() {
    return tableData.value.length > 0 &&
        tableData.value.every(row => row.select)
  },
  set(value) {
    tableData.value.forEach(row => {
      row.select = value
    })
  }
})

// 获取选中数据（返回一个只包含userId的数组）
const selectedRows = computed(() =>
    tableData.value.filter(row => row.select).map(row => row.userId)
)

// 获取权限数组
const permissions = computed(() => store.state.user.permissions)

// 权限验证函数(是否显示对应的按钮)
const hasPermission = (permission) => {
  return permissions.value.includes(permission)
}

// 获取部门树
const fetchDeptTree = async () => {
  try {
    const res = await axios.get('/dept/tree')
    deptData.value = res.data
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

// 处理部门节点点击
const handleDeptNodeClick = (data) => {
  searchData.value.deptId = data.deptId
  search()
}

// 查询用户数据
const search = async () => {
  try {
    loading.value = true
    const res = await axios.post('/user/search', {
      page: currentPage.value,
      size: pageSize.value,
      deptId: searchData.value.deptId,
      username: searchData.value.username,
      phone: searchData.value.phone,
      status: searchData.value.status,
      startTime: searchData.value.time ? searchData.value.time[0] : undefined,
      endTime: searchData.value.time ? searchData.value.time[1] : undefined,
    })

    // 添加select属性
    tableData.value = res.data.records.map(item => ({
      ...item,
      select: false
    }))

    total.value = res.data.total
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false;  // 确保始终重置
  }
}

// 删除按钮
const deleteUser = async () => {

}

// 重置按钮
const refresh = () => {
  searchData.value.deptId = null
  searchData.value.username = ''
  searchData.value.phone = ''
  searchData.value.status = null
  searchData.value.time = []
  currentPage.value = 1  // 默认第 1 页
  pageSize.value = 10  // 默认每页 10 条数据
  search()
}

// 用户翻页
const handleCurrentChange = (val) => {
  currentPage.value = val
  search()
}

// 用户调节分页大小
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1 // 每页条数改变时重置到第一页
  search()
}

// 初始化加载数据
onMounted(() => {
  search()
  fetchDeptTree()
})

// 切换用户状态
const handleSwitchChange = (status) => {
  if (status === 0) {
    console.log(status)
  } else {
    console.log(status)
  }
}
</script>

<style scoped>
.main-container {
  display: flex;
  gap: 20px;
}

.left-container {
  border-right: 1px solid #d7d7d7;
  padding-right: 20px;
  height: 100%;
}

.right-container {
  flex: 1;
  min-width: 0;
  overflow: auto;
  padding: 0 20px;
}

.el-tree {
  width: 13vw;
  min-width: 170px;
  margin-top: 30px;
}

.input-dept {
  width: 13vw;
  min-width: 170px;
}

.input {
  width: 220px;
}

.input-container {
  display: flex;
  gap: 16px; /* 统一控制元素间距 */
  flex-wrap: wrap; /* 允许换行 */
  align-items: center; /* 垂直居中 */
}

.block {
  display: flex;
  align-items: center;
  gap: 8px; /* 标签和输入框之间的间距 */
}

.input-title {
  min-width: 70px;  /* 统一标签宽度 */
  text-align: right;  /* 标签右对齐 */
  color: #606266;  /* Element Plus 默认文字颜色 */
  font-size: 14px;
}

.btn-container {
  display: flex;
  margin-top: 20px;
}

.table-container {
  margin-top: 10px;
}

:deep(.el-table__header th) {
  background-color: #F8F8F9 !important;
}
</style>