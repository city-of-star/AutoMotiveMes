<template>
  <div class="auth-container">
    <h2>{{ isLogin ? '登录' : '注册' }}</h2>
    <el-form @submit.prevent="submitForm">
      <el-form-item label="用户名">
        <el-input v-model="form.username" required />
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" required />
      </el-form-item>

      <el-form-item v-if="!isLogin" label="确认密码">
        <el-input v-model="form.confirmPassword" type="password" required />
      </el-form-item>

      <el-form-item v-if="!isLogin" label="邮箱">
        <el-input v-model="form.email" type="email" required />
      </el-form-item>

      <el-button type="primary" native-type="submit">
        {{ isLogin ? '登录' : '注册' }}
      </el-button>
    </el-form>

    <p class="toggle-text" @click="toggleForm">
      {{ isLogin ? '没有账号？立即注册' : '已有账号？立即登录' }}
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from "element-plus";

const router = useRouter()
const store = useStore()

const isLogin = ref(true)
const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

const toggleForm = () => {
  isLogin.value = !isLogin.value
}

const submitForm = async () => {
  try {
    if (isLogin.value) {
      await store.dispatch('user/login', {
        username: form.value.username,
        password: form.value.password
      })
      await router.push('/')
    } else {
      // 处理注册逻辑
      if (form.value.password !== form.value.confirmPassword) {
        ElMessage.error("两次输入的密码不一致")
        return
      }
      await store.dispatch('user/register', form.value)
      ElMessage.success('注册成功，请登录')
      toggleForm()
    }
  } catch (error) {
    ElMessage.error(error.message)
  }
}
</script>