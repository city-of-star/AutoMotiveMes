<template>
  <div class="main-container">

    <!-- 部门树 -->
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

      <!-- 输入框和搜索重置按钮 -->
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

      <!-- 增、删、改、导入、导出按钮 -->
      <div class="btn-container">
        <el-button
            v-if="hasPermission('system:user:add')"
            @click="openAddDialog"
            type="primary"
            :icon="Plus"
            plain
        >新增</el-button>
        <el-button
            v-if="hasPermission('system:user:update')"
            @click="openEditDialog"
            type="success"
            :icon="Edit"
            :disabled="selectedRows.length === 0"
            plain
        >修改</el-button>
        <el-button
            v-if="hasPermission('system:user:delete')"
            @click="deleteUserDialog = true"
            :icon="Delete"
            type="danger"
            :disabled="selectedRows.length === 0"
            plain
        >删除</el-button>
        <el-button
            v-if="hasPermission('system:user:import')"
            @click="importUsers"
            :icon="Download"
            type="info"
            plain
        >导入</el-button>
        <el-button
            v-if="hasPermission('system:user:export')"
            @click="exportUsers"
            :icon="Upload"
            type="warning"
            plain
        >导出</el-button>
      </div>

      <!-- 表格数据 -->
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
        <el-table-column prop="username" label="用户名称" width="140" align="center" header-align="center" />
        <el-table-column prop="realName" label="真实姓名" width="140" align="center" header-align="center" />
        <el-table-column prop="deptName" label="部门" width="140" align="center" header-align="center" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号码" width="180" align="center" header-align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center" header-align="center">
          <template #default="scope">
            <el-switch
                :model-value="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @click="handleSwitchClick(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" header-align="center"/>
        <el-table-column label="操作" width="270" align="center" header-align="center">
          <template #default="scope">
            <el-button size="small" type="text" :icon="Edit" @click="handleEditUser(scope.row)">修改</el-button>
            <el-button size="small" type="text" :icon="Delete" @click="handleSingleDelete(scope.row)">删除</el-button>
            <el-button size="small" type="text" :icon="Refresh" @click="handleResetPassword(scope.row)">重置密码</el-button>
          </template>
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

  <!-- 重置密码提示对话框 -->
  <ConfirmDialog
      v-model:visible="resetDialogVisible"
      message="确认重置密码为默认值123456？"
      @confirm="confirmResetPassword"
  />

  <!-- 切换用户状态系统提示对话框 -->
  <ConfirmDialog
      v-model:visible="centerDialogVisible"
      :message="`确认要${currentNewStatus === 1 ? '启用' : '停用'}此用户吗？`"
      @confirm="confirmSwitch"
      @cancel="cancelSwitch"
  />

  <!-- 删除用户系统提示对话框 -->
  <ConfirmDialog
      v-model:visible="deleteUserDialog"
      :message="`确认要删除此用户吗？`"
      @confirm="deleteUser"
  />

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
            <el-input placeholder="请输入用户名" v-model="addForm.username" clearable />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input placeholder="请输入真实姓名" v-model="addForm.realName" clearable />
          </el-form-item>
          <el-form-item label="所属部门" prop="deptId">
            <el-tree-select
                v-model="addForm.deptId"
                :data="deptData"
                :props="deptProps"
                check-strictly
                placeholder="请选择部门"
                clearable
            />
          </el-form-item>
          <el-form-item label="岗位" prop="postId">
            <el-select
                v-model="addForm.postId"
                placeholder="请选择岗位"
                style="width: 100%"
                clearable
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
                clearable
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
                clearable
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
                v-model="addForm.confirmPassword"
                type="password"
                show-password
                placeholder="请输入密码"
                clearable
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
            <el-input placeholder="请输入手机号码" v-model="addForm.phone" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input placeholder="请输入邮箱" v-model="addForm.email" clearable />
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

  <!-- 修改用户对话框 -->
  <el-dialog
      v-model="editDialogVisible"
      title="修改用户"
      width="800px"
  >
    <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editFormRules"
        label-width="100px"
        label-position="right"
        status-icon
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="editForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="所属部门" prop="deptId">
            <el-tree-select
                v-model="editForm.deptId"
                :data="deptData"
                :props="deptProps"
                check-strictly
                placeholder="请选择部门"
                clearable
            />
          </el-form-item>
          <el-form-item label="岗位" prop="postId">
            <el-select
                v-model="editForm.postId"
                placeholder="请选择岗位"
                style="width: 100%"
                clearable
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
                v-model="editForm.roleId"
                placeholder="请选择角色"
                style="width: 100%"
                clearable
            >
              <el-option
                  v-for="role in roleOptions"
                  :key="role.roleId"
                  :label="role.roleName"
                  :value="role.roleId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="editForm.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号码" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" placeholder="请输入邮箱" clearable />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
    <span class="dialog-footer">
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEditUser">确定</el-button>
    </span>
    </template>
  </el-dialog>

  <!-- 导入用户对话框 -->
  <el-dialog
      v-model="importDialogVisible"
      title="用户导入"
      width="600px"
  >
    <!-- 模板下载按钮 -->
    <template #header>
      <div class="dialog-header">
        <span>用户导入</span>
        <el-button
            type="primary"
            link
            @click="downloadTemplate"
            :disabled="!hasPermission('system:user:import')"
        >
          下载模板
        </el-button>
      </div>
    </template>

    <!-- 上传区域 -->
    <el-upload
        class="upload-container"
        drag
        :action="baseUrl+'/system/user/batchImportUser'"
        method="post"
        :headers="{ Authorization: `Bearer ${store.state.user.token}` }"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="(e) => uploading = e.percent < 100"
        :disabled="uploading"
    >
      <el-icon class="el-icon--upload"><upload /></el-icon>
      <div class="el-upload__text">
        将文件拖到此处，或<em>点击上传</em>
      </div>
      <div class="el-upload__tip" v-html="uploadTips"></div>
    </el-upload>

    <!-- 操作按钮 -->
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button
            type="primary"
            @click="submitUpload"
            :loading="uploading"
        >确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
/* 引入部分 */
import { Delete, Download, Edit, Plus, Refresh, Search, Upload } from '@element-plus/icons-vue'
import ConfirmDialog from '@/components/system/confirmDialog/Index.vue'
import axios from '@/utils/axios'
import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from "element-plus"

/* 状态声明 */
const store = useStore()

// 用户状态切换相关
const centerDialogVisible = ref(false)
const currentUserId = ref(null)
const currentNewStatus = ref(null)
const currentRow = ref(null)

// 删除对话框状态
const deleteUserDialog = ref(false)

// 搜索条件
const searchData = ref({
  deptName: '',
  username: '',
  phone: '',
  status: null,
  time: [],
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

// 部门树相关
const deptData = ref([])
const deptProps = {
  children: 'children',
  label: 'deptName',
  value: 'deptId'
}

// 新增对话框相关
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

// 修改对话框相关
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = ref({
  userId: null,
  realName: '',
  deptId: null,
  postId: null,
  roleId: null,
  phone: '',
  email: '',
  status: 1
})

// 选项数据
const statusOptions = [
  { value: 1, label: '正常' },
  { value: 0, label: '停用' }
]
const postOptions = ref([])
const roleOptions = ref([])

/* 计算属性 */
// 全选/全不选逻辑
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

// 选中的用户ID数组
const selectedRows = computed(() =>
    tableData.value.filter(row => row.select).map(row => row.userId)
)

// 权限数组
const permissions = computed(() => store.state.user.permissions)

/* 方法 */
// 权限验证方法
const hasPermission = (permission) => {
  return permissions.value.includes(permission)
}

/* 数据获取方法 */
// 获取部门树数据
const fetchDeptTree = async () => {
  deptData.value = await axios.get('/system/dept/tree')
}

// 获取岗位和角色选项
const fetchOptions = async () => {
  try {
    roleOptions.value = await axios.get('/system/role/list')
    postOptions.value = await axios.get('/system/post/list')
  } catch (error) {
    console.error('获取选项失败:', error)
  }
}

/* 用户操作 */
// 用户状态切换处理
const handleSwitchClick = (row) => {
  currentRow.value = row
  currentUserId.value = row.userId
  currentNewStatus.value = row.status === 1 ? 0 : 1
  centerDialogVisible.value = true
  // 立即更新本地状态保持响应式
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

// 取消状态修改
const cancelSwitch = () => {
  currentRow.value = null
  centerDialogVisible.value = false
  search() // 重新加载确保数据一致性
}

// 删除用户
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

/* 表单相关方法 */
// 新增用户表单验证规则
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

// 修改用户表单验证规则
const editFormRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
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

/* 对话框操作 */
// 打开新增对话框
const openAddDialog = () => {
  addForm.value = {
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

    const payload = { ...addForm.value }
    delete payload.confirmPassword

    await axios.post('/system/user/add', payload)
    ElMessage.success('新增用户成功')
    addDialogVisible.value = false
    await search()
  } catch (error) {
    console.error('新增用户失败:', error)
  }
}

// 打开修改对话框
const openEditDialog = async () => {
  if (selectedRows.value.length !== 1) {
    ElMessage.warning('请选择一条记录进行修改')
    return
  }
  try {
    const res = await axios.post('/system/user/getInfo', {
      userId: selectedRows.value[0]
    })
    editForm.value = {
      userId: res.userId,
      realName: res.realName,
      deptId: res.deptId,
      postId: res.postId,
      roleId: res.roleId,
      phone: res.phone,
      email: res.email,
      status: res.status
    }
    await fetchOptions()
    editDialogVisible.value = true
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 提交修改用户
const submitEditUser = async () => {
  try {
    await editFormRef.value.validate()
    await axios.post('/system/user/update', editForm.value)
    ElMessage.success('修改用户成功')
    editDialogVisible.value = false
    await search()
  } catch (error) {
    console.error('修改用户失败:', error)
  }
}

/* 搜索相关 */
// 主搜索方法
const search = async () => {
  try {
    loading.value = true
    const res = await axios.post('/system/user/page', {
      page: currentPage.value,
      size: pageSize.value,
      ...searchData.value,
      startTime: searchData.value.time?.[0],
      endTime: searchData.value.time?.[1],
    })

    tableData.value = res.records.map(item => ({
      ...item,
      select: false
    }))
    total.value = res.total
  } catch (error) {
    console.error('查询操作失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const refresh = () => {
  searchData.value = {
    deptName: '',
    username: '',
    phone: '',
    status: null,
    time: [],
  }
  currentPage.value = 1
  pageSize.value = 10
  search()
}

/* 分页事件处理 */
const handleCurrentChange = (val) => {
  currentPage.value = val
  search()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  search()
}

/* 部门树事件 */
const handleDeptNodeClick = (data) => {
  searchData.value.deptName = data.deptName
  search()
}

/* 导入导出功能 */
const baseUrl = window.CONFIG.api.baseURL;
const importDialogVisible = ref(false)
const uploading = ref(false)
// 上传提示信息
const uploadTips = computed(() => `
  支持扩展名：<span style="color: #409EFF; margin: 0 4px">.xlsx</span>
  文件大小限制：<span style="color: #409EFF;">10MB</span><br/>
  模板说明：<span style="color: #67C23A;">请使用下载的模板填写数据，保持表头顺序不变</span>
`)
const importUsers = () => {
  importDialogVisible.value = true
}
// 下载模板
const downloadTemplate = async () => {
  try {
    const response = await axios.get('/system/user/getUserImportTemplate', {
      responseType: 'blob', // 指定响应类型为二进制流
      headers: {
        Authorization: `Bearer ${store.state.user.token}`  // 携带认证token
      }
    });

    // 创建临时下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;

    // 从响应头中获取文件名
    const fileName = response.headers['content-disposition']
        ?.split('filename=')[1]
        ?.replace(/"/g, '') || '用户导入模板.xlsx';

    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();

    // 清理资源
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('模板下载失败:', error);
    ElMessage.error('模板下载失败，请检查权限或稍后重试');
  }
}

// 文件上传前校验
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isSizeValid = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('仅支持.xlsx格式文件')
    return false
  }
  if (!isSizeValid) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  return true
}

// 提交上传
const submitUpload = () => {
  // 触发el-upload的内部上传
  const uploadElement = document.querySelector('.upload-container .el-upload__input')
  uploadElement?.click()
}

// 上传成功处理
const handleSuccess = (response) => {
  uploading.value = false
  if (response.code === 200) {
    ElMessage.success('用户导入成功')
    importDialogVisible.value = false
    // 刷新用户列表
    search()
  } else {
    ElMessage.error(response.msg || '导入失败')
  }
}

// 上传错误处理
const handleError = (error) => {
  uploading.value = false
  ElMessage.error(error.message || '上传失败，请检查网络连接')
}

const exportUsers = async () => {
  try {
    const response = await axios.get('/system/user/exportUser', {
      responseType: 'blob',
      headers: {
        Authorization: `Bearer ${store.state.user.token}`
      }
    });

    // 创建临时下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;

    // 从响应头获取文件名
    const fileName = response.headers['content-disposition']
            ?.split('filename=')[1]
            ?.replace(/"/g, '')
        || '用户数据导出.xlsx';  // 后备默认文件名

    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();

    // 清理资源
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请检查权限或稍后再试');
  }
};

/* 操作栏三个按钮的功能实现 */
const handleEditUser = async (row) => {
  try {
    const res = await axios.post('/system/user/getInfo', { userId: row.userId })
    editForm.value = { ...res }
    await fetchOptions()
    editDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleSingleDelete = (row) => {
  tableData.value.forEach(item => item.select = false);
  row.select = true;
  deleteUserDialog.value = true
}

const resetDialogVisible = ref(false)
const resetUserId = ref(null)

const handleResetPassword = (row) => {
  resetUserId.value = row.userId
  resetDialogVisible.value = true
}

const confirmResetPassword = async () => {
  try {
    await axios.post('/system/user/resetPassword', {
      userId: resetUserId.value,
    })
    ElMessage.success('密码已重置为默认值')
    resetDialogVisible.value = false
  } catch (error) {
    ElMessage.error('重置失败')
  }
}

/* 生命周期钩子 */
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

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 20px;
}

.upload-container {
  text-align: center;
  padding: 20px;
}

:deep(.el-upload-dragger) {
  padding: 40px 20px;
  background-color: #fafafa;
  border: 2px dashed #e0e0e0;
  border-radius: 8px;
  transition: border-color 0.3s;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
}

.el-upload__tip {
  margin-top: 15px;
  color: #909399;
  line-height: 1.6;
}
</style>