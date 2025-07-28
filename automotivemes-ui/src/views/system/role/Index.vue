<template>
  <div class="main-container">
    <!-- 输入框和搜索重置按钮 -->
    <div class="input-container">
      <div class="block">
        <span class="input-title">角色名称</span>
        <el-input
            v-model="searchData.roleName"
            placeholder="请输入角色名称"
            class="input"
            @clear="search"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="block">
        <span class="input-title">权限字符</span>
        <el-input
            v-model="searchData.roleCode"
            placeholder="请输入权限字符"
            class="input"
            @clear="search"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <el-button @click="search()" :icon="Search" type="primary">搜索</el-button>
      <el-button @click="refresh()" :icon="Refresh">重置</el-button>
    </div>

    <!-- 增、删、改、导出按钮 -->
    <div class="btn-container">
      <el-button
          v-if="hasPermission('system:role:add')"
          @click="openAddDialog"
          type="primary"
          :icon="Plus"
          plain
      >新增</el-button>
      <el-button
          v-if="hasPermission('system:role:update')"
          @click="openEditDialog"
          type="success"
          :icon="Edit"
          :disabled="selectedRows.length === 0"
          plain
      >修改</el-button>
      <el-button
          v-if="hasPermission('system:role:delete')"
          @click="confirmBatchDelete"
          :icon="Delete"
          type="danger"
          :disabled="selectedRows.length === 0"
          plain
      >删除</el-button>
      <el-button
          v-if="hasPermission('system:role:export')"
          @click="exportRoles"
          :icon="Upload"
          type="warning"
          plain
      >导出</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table
        v-loading="loading"
        class="table-container"
        :data="tableData"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="roleId" label="角色编号" width="120" align="center" />
      <el-table-column prop="roleName" label="角色名称" width="180" align="center" />
      <el-table-column prop="roleCode" label="权限字符" width="200" align="center" />
      <el-table-column prop="description" label="描述" width="300" show-overflow-tooltip align="center"/>
      <el-table-column label="操作" min-width="200" align="center">
        <template #default="scope">
          <el-button
              size="small"
              type="text"
              :icon="Edit"
              @click="handleEditRole(scope.row)"
              v-if="hasPermission('system:role:update')"
          >修改</el-button>
          <el-button
              size="small"
              type="text"
              :icon="Delete"
              @click="handleSingleDelete(scope.row)"
              v-if="hasPermission('system:role:delete')"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        class="pagination-container"
    />
  </div>

  <!-- 新增角色对话框 -->
  <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '修改角色' : '新增角色'"
      width="500px"
  >
    <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input
            v-model="formData.roleName"
            placeholder="请输入角色名称"
            clearable
        />
      </el-form-item>

      <el-form-item label="权限字符" prop="roleCode">
        <el-input
            v-model="formData.roleCode"
            placeholder="请输入权限字符"
            clearable
        />
      </el-form-item>

      <el-form-item label="菜单权限" prop="permIds">
        <el-scrollbar height="400px">
          <el-tree
              ref="treeRef"
              :data="permissionTree"
              node-key="permId"
              show-checkbox
              default-expand-all
              highlight-current
              :props="{
              label: 'permName',
              children: 'children'
            }"
          >
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <el-icon v-if="data.icon">
                  <component :is="data.icon" />
                </el-icon>
                <span>{{ node.label }}</span>
                <el-tag
                    v-if="data.permType !== 'MENU'"
                    size="small"
                    :type="data.permType === 'BUTTON' ? 'danger' : 'warning'"
                    class="ml-2"
                >
                  {{ data.permType === 'BUTTON' ? '按钮' : '接口' }}
                </el-tag>
              </span>
            </template>
          </el-tree>
        </el-scrollbar>
      </el-form-item>

      <el-form-item label="备注说明" prop="description">
        <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色备注信息"
            maxlength="200"
            show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {Delete, Edit, Plus, Refresh, Search, Upload} from '@element-plus/icons-vue'
import {nextTick, reactive, ref} from 'vue'
import {useStore} from 'vuex'
import axios from '@/utils/axios'
import {ElMessage, ElMessageBox} from "element-plus";

const store = useStore()

// 搜索条件
const searchData = ref({
  roleName: '',
  roleCode: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])

// 权限验证
const hasPermission = (permission) => {
  return store.state.user.permissions.includes(permission)
}

// 获取角色列表
const search = async () => {
  try {
    loading.value = true
    const res = await axios.post('/system/role/page', {
      page: currentPage.value,
      size: pageSize.value,
      ...searchData.value
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error('获取角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const refresh = () => {
  searchData.value = { roleName: '', roleCode: '' }
  currentPage.value = 1
  pageSize.value = 10
  search()
}

// 分页事件处理
const handleCurrentChange = (val) => {
  currentPage.value = val
  search()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  search()
}

// 表格选择处理
const handleSelectionChange = (selection) => {
  selectedRows.value = selection.map(row => row.roleId)
}

// 添加角色对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const permissionTree = ref([])  // 存储权限树数据
const treeRef = ref()  // 树组件引用

const formData = reactive({
  roleId: null,
  roleName: '',
  roleCode: '',
  description: '',
  permIds: []
})

// 验证规则
const rules = reactive({
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '权限字符不能为空', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '只能包含大写字母和下划线', trigger: 'blur' }
  ],
  permIds: [
    { required: true, message: '请至少选择一个权限', trigger: 'change' }
  ],
  description: [
    { max: 200, message: '备注不能超过200个字符', trigger: 'blur' }
  ]
})

// 打开新增对话框
const openAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  loadPermissionTree()
}

// 加载权限树
const loadPermissionTree = async () => {
  try {
    permissionTree.value = await axios.get('/system/permission/tree')
  } catch (error) {
    console.error('加载权限树失败:', error)
  }
}

// 重置表单
const resetForm = () => {
  formData.roleName = ''
  formData.roleCode = ''
  formData.description = ''
  formData.permIds = []
  if (treeRef.value) {
    treeRef.value.setCheckedKeys([])
  }
}

// 提交表单
const submitForm = async () => {
  // 获取树组件选中节点
  const checkedKeys = treeRef.value.getCheckedKeys()
  const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
  const allPermIds = [...checkedKeys, ...halfCheckedKeys]

  formData.permIds = allPermIds
  await formRef.value.validate()

  try {
    await formRef.value.validate()

    const requestData = {
      roleName: formData.roleName,
      roleCode: formData.roleCode,
      description: formData.description,
      permIds: allPermIds
    }

    if (isEdit.value) {
      requestData.roleId = formData.roleId
      await axios.post('/system/role/update', requestData)
      ElMessage.success('角色修改成功')
    } else {
      await axios.post('/system/role/add', requestData)
      ElMessage.success('角色新增成功')
    }

    dialogVisible.value = false
    await search()
  } catch (error) {
    console.error(error)
  }
}

// 修改角色 - 处理行内修改按钮
const handleEditRole = async (row) => {
  isEdit.value = true
  dialogVisible.value = true
  try {
    // 获取角色基本信息
    const roleRes = await axios.get(`/system/role/${row.roleId}`)
    // 获取角色权限ID列表
    const permRes = await axios.get(`/system/role/${row.roleId}/permIds`)

    // 填充表单数据
    formData.roleId = row.roleId
    formData.roleName = roleRes.roleName
    formData.roleCode = roleRes.roleCode
    formData.description = roleRes.description

    // 加载权限树并设置选中状态
    await loadPermissionTree()
    await nextTick(() => {
      treeRef.value.setCheckedKeys(permRes)
    })
  } catch (error) {
    ElMessage.error('获取角色信息失败')
    console.error('Error fetching role details:', error)
  }
}

// 修改角色 - 处理顶部修改按钮
const openEditDialog = () => {
  if (selectedRows.value.length !== 1) {
    ElMessage.warning('请选择且仅选择一条记录进行修改')
    return
  }
  handleEditRole({ roleId: selectedRows.value[0] })
}

// 单个删除
const handleSingleDelete = (row) => {
  ElMessageBox.confirm('此操作将永久删除该角色，是否继续？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.post('/system/role/delete', { roleId: row.roleId })
      ElMessage.success('删除成功')
      await search()
    } catch (error) {
      console.log(error)
    }
  }).catch(() => {})
}

// 批量删除
const confirmBatchDelete = () => {
  ElMessageBox.confirm(`确认删除选中的${selectedRows.value.length}项吗？`, '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      selectedRows.value.map(roleId =>
          axios.post('/system/role/delete', {
            roleId: roleId
          })
      )
      ElMessage.success('批量删除成功')
      await search()
    } catch (error) {
      ElMessage.error(`删除失败: ${error.response?.data?.message || error.message}`)
    }
  }).catch(() => {})
}

// 初始化加载
search()
</script>

<style scoped>
.main-container {
  background: #fff;
  border-radius: 4px;
}

.input-container {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 20px;
}

.block {
  display: flex;
  align-items: center;
  gap: 8px;
}

.input {
  width: 220px;
}

.input-title {
  min-width: 60px;
  text-align: right;
  color: #606266;
  font-size: 14px;
}

.btn-container {
  margin: 15px 0;
}

.table-container {
  margin-top: 15px;
}

.pagination-container {
  margin-top: 20px;
  justify-content: flex-end;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-right: 8px;
}

.ml-2 {
  margin-left: 8px;
}

.el-scrollbar {
  width: 500px;
  height: 400px;
}

:deep(.el-tree-node__content) {
  height: 36px;
}
</style>