<template>
  <div class="login-container">
    <el-form ref="form" :model="form" :rules="rules">
      <div class="login-header">
        <img src="@/assets/logo.png" class="logo" alt="">
        <h2>汽车MES生产监控系统</h2>
      </div>

      <el-form-item prop="username">
        <el-input
            v-model="form.username"
            placeholder="工号/用户名"
            prefix-icon="User"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
        />
      </el-form-item>

      <el-form-item>
        <el-button
            type="primary"
            class="login-btn"
            @click="handleLogin"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>

    <div class="footer">
      <el-link type="info" @click="goRegister">新用户注册</el-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from "element-plus";

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const store = useStore()
const router = useRouter()

const handleLogin = async () => {
  try {
    await store.dispatch('login', form.value)
    await router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message)
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  background: url('@/assets/login-bg.jpg') no-repeat;
  background-size: cover;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 120px;
  margin-bottom: 15px;
}

.login-btn {
  width: 100%;
}
</style>