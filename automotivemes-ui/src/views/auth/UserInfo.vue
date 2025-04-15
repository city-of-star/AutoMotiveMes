<template>
  <div class="personal-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">个人信息</span>
        </div>
      </template>

      <!-- 基本信息 -->
      <div class="user-info">
        <!-- 头像 -->
        <div class="avatar-container">
          <el-avatar :size="120" :src="user.headImg || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
          <div class="username">{{ user.realName }}</div>
          <div class="role">{{ user.roleName }}</div>
        </div>

        <!-- 详细信息 -->
        <div class="detail-info">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ user.deptName }}</el-descriptions-item>
            <el-descriptions-item label="职位">{{ user.postName }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ user.email }}
              <el-button type="text" @click="showEmailDialog">修改</el-button>
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ user.phone }}
              <el-button type="text" @click="showPhoneDialog">修改</el-button>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <!-- 修改密码按钮 -->
      <div class="password-change">
        <el-button type="primary" @click="showPasswordDialog">修改密码</el-button>
      </div>
    </el-card>

    <!-- 修改邮箱对话框 -->
    <el-dialog v-model="emailDialogVisible" title="修改邮箱" width="30%">
      <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef">
        <el-form-item label="新邮箱" prop="email">
          <el-input v-model="emailForm.email" placeholder="请输入新邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="emailDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateEmail">确认</el-button>
      </template>
    </el-dialog>

    <!-- 修改手机对话框 -->
    <el-dialog v-model="phoneDialogVisible" title="修改手机号" width="30%">
      <el-form :model="phoneForm" :rules="phoneRules" ref="phoneFormRef">
        <el-form-item label="新手机号" prop="phone">
          <el-input v-model.number="phoneForm.phone" placeholder="请输入新手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePhone">确认</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="30%">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePassword">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {useStore} from 'vuex'
import {ElMessage} from 'element-plus'
import {onMounted, ref} from "vue"

const store = useStore()
const user = ref({})

const getUserInfo = async () => {
  try {
    user.value = await store.dispatch('user/getUserInfo')
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  getUserInfo()
})

// 邮箱修改相关
const emailDialogVisible = ref(false)
const emailForm = ref({ email: '' })
const emailFormRef = ref(null)
const emailRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
}

// 手机号修改相关
const phoneDialogVisible = ref(false)
const phoneForm = ref({ phone: '' })
const phoneFormRef = ref(null)
const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码修改相关
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordFormRef = ref(null)
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const showEmailDialog = () => {
  emailForm.value.email = user.value.email
  emailDialogVisible.value = true
}

const showPhoneDialog = () => {
  phoneForm.value.phone = user.value.phone
  phoneDialogVisible.value = true
}

const showPasswordDialog = () => {
  passwordDialogVisible.value = true
}

const updateEmail = async () => {
  try {
    await emailFormRef.value.validate()
    // 调用API更新邮箱
    await store.dispatch('user/updateEmail', emailForm.value.email)
    ElMessage.success('邮箱修改成功')
    emailDialogVisible.value = false
  } catch (error) {
    console.error('邮箱修改失败:', error)
  }
}

const updatePhone = async () => {
  try {
    await phoneFormRef.value.validate()
    // 调用API更新手机号
    await store.dispatch('user/updatePhone', phoneForm.value.phone)
    ElMessage.success('手机号修改成功')
    phoneDialogVisible.value = false
  } catch (error) {
    console.error('手机号修改失败:', error)
  }
}

const updatePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    // 调用API修改密码
    await store.dispatch('user/updatePassword', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    console.error('密码修改失败:', error)
  }
}
</script>

<style scoped>
.personal-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.avatar-container {
  text-align: center;
  flex-shrink: 0;
}

.avatar-container .username {
  margin-top: 10px;
  font-size: 16px;
  font-weight: 500;
}

.avatar-container .role {
  color: #666;
  font-size: 14px;
}

.detail-info {
  flex-grow: 1;
}

.password-change {
  margin-top: 20px;
  text-align: center;
}

.el-descriptions {
  margin-top: 10px;
}

.el-descriptions-item__label {
  width: 80px;
}
</style>