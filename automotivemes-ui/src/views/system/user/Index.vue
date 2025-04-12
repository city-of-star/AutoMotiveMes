<template>
  <div class="main-container">

    <div class="left-container">
      <el-input v-model="searchData.deptName" placeholder="请输入部门名称" class="input-dept" @clear="search" clearable>
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
          class="tree-container el-tree"
      />
    </div>

    <div class="right-container">

      <div class="input-container">
        <div class="block">
          <span class="input-title">用户名称</span>
          <el-input v-model="searchData.username" placeholder="请输入用户名称" class="input" @clear="search" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="input-title">手机号码</span>
          <el-input v-model="searchData.phone" placeholder="请输入手机号码" class="input" @clear="search" clearable>
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="block">
          <span class="input-title">用户状态</span>
          <el-select v-model="searchData.status" placeholder="用户状态" class="input" @clear="search" clearable>
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
              @clear="search"
              clearable
          />
        </div>
        <el-button @click="search()" :icon="Search" type="primary">搜索</el-button>
        <el-button @click="refresh()" :icon="Refresh">重置</el-button>
      </div>

      <div class="btn-container">
        <el-button
            v-if="hasPermission('system:user:add')"
            :icon="Plus"
            :color="theme_color"
            plain
            @click="openAddDialog"
        >
          新增
        </el-button>
        <el-button v-if="hasPermission('system:user:update')"  :icon="Edit" :color=btn_update_color plain>修改</el-button>
        <el-button
            v-if="hasPermission('system:user:delete')"
            :icon="Delete"
            :color=btn_delete_color
            :disabled="selectedRows.length === 0"
            @click="deleteUser()"
            plain
        >删除</el-button>
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
                :model-value="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @click="handleSwitchClick(scope.row)"
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

  <!--  切换用户状态系统提示对话框-->
  <el-dialog
      v-model="centerDialogVisible"
      :title="currentNewStatus === 1 ? '启用用户' : '停用用户'"
      width="500"
      align-center
  >
    <span>确认要{{ currentNewStatus === 1 ? '启用' : '停用' }}此用户吗？</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancelSwitch">取消</el-button>
        <el-button type="primary" @click="confirmSwitch">确定</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 新增用户对话框 -->
  <el-dialog
      v-model="addDialogVisible"
      title="新增用户"
      width="800px"
  >
    <el-form
        ref="addFormRef"
        :model="addForm"
        :rules="addFormRules"
        label-width="100px"
        label-position="right"
        status-icon
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户名" prop="username">
            <el-input placeholder="请输入用户名" v-model="addForm.username" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input placeholder="请输入真实姓名" v-model="addForm.realName" />
          </el-form-item>
          <el-form-item label="所属部门" prop="deptId">
            <el-tree-select
                v-model="addForm.deptId"
                :data="deptData"
                :props="deptProps"
                check-strictly
                placeholder="请选择部门"
            />
          </el-form-item>
          <el-form-item label="岗位" prop="postId">
            <el-select
                v-model="addForm.postId"
                placeholder="请选择岗位"
                style="width: 100%"
            >
              <el-option
                  v-for="post in postOptions"
                  :key="post.postId"
                  :label="post.postName"
                  :value="post.postId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="角色" prop="roleId">
            <el-select
                v-model="addForm.roleId"
                placeholder="请选择角色"
                style="width: 100%"
                >
            <el-option
                v-for="role in roleOptions"
                :key="role.roleId"
                :label="role.roleName"
                :value="role.roleId"
            />
            </el-select>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
                v-model="addForm.password"
                type="password"
                show-password
                placeholder="请输入密码"
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
                v-model="addForm.confirmPassword"
                type="password"
                show-password
                placeholder="请输入密码"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="addForm.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号码" prop="phone">
            <el-input placeholder="请输入手机号码" v-model="addForm.phone" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input placeholder="请输入邮箱" v-model="addForm.email" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAddUser">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {Delete, Download, Edit, Plus, Refresh, Search, Upload} from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import {computed, onMounted, ref} from 'vue'
import {useStore} from 'vuex'
import {ElMessage} from "element-plus";

const store = useStore()

// 主题和按钮颜色
const theme_color = store.state.user.theme_color;
const btn_update_color = store.state.app.btn_update_color;
const btn_delete_color = store.state.app.btn_delete_color;
const btn_import_color = store.state.app.btn_import_color;
const btn_export_color = store.state.app.btn_export_color;

// 用户状态相关
const centerDialogVisible = ref(false)  // 是否显示弹框
const currentUserId = ref(null)
const currentNewStatus = ref(null)
const currentRow = ref(null) // 用于保存当前操作的行数据

// 查询的条件值
let searchData = ref({
  deptName: '',
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

// 新增相关状态
const addDialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  username: '',
  realName: '',
  deptId: null,
  postId: null,
  roleId: null,
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
  status: 1
})

// 表单验证规则
const validatePassword = (rule, value, callback) => {
  if (value !== addForm.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const addFormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  postId: [
    { required: true, message: '请选择岗位', trigger: 'change' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 岗位和角色选项
const postOptions = ref([])
const roleOptions = ref([])

// 获取岗位和角色列表
const fetchOptions = async () => {
  try {
    const responseRoles = await axios.get('/system/role/list')
    const responsePosts = await axios.get('/system/post/list')
    roleOptions.value = responseRoles.roles
    postOptions.value = responsePosts.posts
  } catch (error) {
    console.error('获取选项失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  addForm.value = {
    username: '',
    realName: '',
    deptId: null,
    postId: null,
    roleIds: null,
    password: '',
    confirmPassword: '',
    phone: '',
    email: '',
    status: 1
  }
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
  fetchOptions()
  addDialogVisible.value = true
}

// 提交新增用户
const submitAddUser = async () => {
  try {
    await addFormRef.value.validate()

    if (addForm.value.password !== addForm.value.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }

    const payload = {
      ...addForm.value,
      // 移除确认密码字段
      confirmPassword: undefined
    }

    await axios.post('/system/user/add', payload)

    ElMessage.success('新增用户成功')
    addDialogVisible.value = false
    await search() // 刷新列表
  } catch (error) {
    console.error('新增用户失败:', error)
  }
}

// 权限验证函数(是否显示对应的按钮)
const hasPermission = (permission) => {
  return permissions.value.includes(permission)
}

// 获取部门树
const fetchDeptTree = async () => {
  deptData.value = await axios.get('/system/dept/tree')
}

// 处理部门节点点击
const handleDeptNodeClick = (data) => {
  searchData.value.deptName = data.deptName
  search()
}

// 查询用户数据
const search = async () => {
  try {
    loading.value = true
    const res = await axios.post('/system/user/search', {
      page: currentPage.value,
      size: pageSize.value,
      deptName: searchData.value.deptName,
      username: searchData.value.username,
      phone: searchData.value.phone,
      status: searchData.value.status,
      startTime: searchData.value.time ? searchData.value.time[0] : undefined,
      endTime: searchData.value.time ? searchData.value.time[1] : undefined,
    })

    // 添加select属性
    tableData.value = res.records.map(item => ({
      ...item,
      select: false
    }))

    total.value = res.total
  } catch (error) {
    console.error('查询操作失败:', error)
  } finally {
    loading.value = false;  // 确保始终重置
  }
}

// 删除按钮
const deleteUser = async () => {
  try {
    await axios.post('/system/user/delete', {
      userIds: selectedRows.value
    })
    ElMessage.success('删除成功')
    await search()
  } catch (error) {
    console.error('删除操作失败:', error)
  }
}

// 切换用户状态点击事件处理
const handleSwitchClick = (row) => {
  currentRow.value = row
  currentUserId.value = row.userId
  currentNewStatus.value = row.status === 1 ? 0 : 1
  centerDialogVisible.value = true

  // 直接更新本地数据避免重新请求
  currentRow.value.status = currentNewStatus.value
}

// 确认状态修改
const confirmSwitch = async () => {
  try {
    await axios.post('/system/user/switchStatus', {
      userId: currentUserId.value,
      status: currentNewStatus.value
    })
    ElMessage.success('状态修改成功')
    centerDialogVisible.value = false
  } catch (error) {
    console.error('状态修改失败:', error)
  }
}

// 取消修改时恢复原始状态
const cancelSwitch = () => {
  currentRow.value = null
  centerDialogVisible.value = false
  search() // 重新加载数据确保状态一致
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