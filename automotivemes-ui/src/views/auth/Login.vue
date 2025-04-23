<template>
  <div class="industrial-login-container">
    <canvas ref="canvasRef" id="dataParticles"></canvas>

    <div class="login-card">
      <div class="system-brand">
        <img src="@/assets/logo.png" class="logo">
        <h1>汽车 MES 生产监控中心</h1>
        <p class="version">AutoMotiveMES - V1.0.0</p>
      </div>

      <el-form @submit.prevent="handleLogin" class="login-form">
        <el-form-item>
          <el-input
              v-model="form.username"
              placeholder="工号/用户名"
              class="industrial-input"
              @focus="e => animateBorder(e.currentTarget)">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-input
              v-model="form.password"
              type="password"
              placeholder="安全密码"
              class="industrial-input"
              @focus="e => animateBorder(e.currentTarget)"
              show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-button
            :loading="loading"
            type="primary"
            native-type="submit"
            class="login-btn">
          登录
          <el-icon class="icon-right"><ArrowRight /></el-icon>
        </el-button>
      </el-form>

      <div class="safety-tips">
        <el-icon><Warning /></el-icon>
        <span>安全提示：本系统包含企业敏感生产数据</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick, onUnmounted} from 'vue'
import { User, Lock, ArrowRight, Warning } from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
import {useStore} from "vuex";

const canvasRef = ref(null)

// 动态粒子背景
const initParticles = () => {
  const canvas = canvasRef.value  // 通过引用获取canvas
  if (!canvas) {  // 确保canvas存在
    console.error('Canvas element not found')
    return
  }
  const ctx = canvas.getContext('2d')
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight

  const particles = []
  class Particle {
    constructor() {
      this.x = Math.random() * canvas.width
      this.y = Math.random() * canvas.height
      this.size = Math.random() * 2 + 1
      this.speedX = Math.random() * 3 - 1.5
      this.speedY = Math.random() * 3 - 1.5
    }
    update() {
      this.x += this.speedX
      this.y += this.speedY
      if (this.x > canvas.width) this.x = 0
      if (this.x < 0) this.x = canvas.width
      if (this.y > canvas.height) this.y = 0
      if (this.y < 0) this.y = canvas.height
    }
    draw() {
      ctx.fillStyle = 'rgba(64, 158, 255, 0.8)'
      ctx.beginPath()
      ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
      ctx.fill()
    }
  }

  function animate() {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    particles.forEach(particle => {
      particle.update()
      particle.draw()
    })
    requestAnimationFrame(animate)
  }

  // 创建粒子
  for (let i = 0; i < 80; i++) {
    particles.push(new Particle())
  }
  animate()
}

const handleResize = () => {
  initParticles()  // 窗口调整时重新初始化
}


onMounted(() => {
  nextTick(() => {  // 确保DOM更新完成
    initParticles()
    window.addEventListener('resize', handleResize)
  })
})

// 组件销毁时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const store = useStore()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    if (!form.value.username || !form.value.password) {
      ElMessage.warning('请输入工号和密码')
      return
    }

    loading.value = true

    await store.dispatch('user/login', {
      username: form.value.username,
      password: form.value.password
    })

  } catch (error) {
    ElMessage.error(error.message || '认证失败，请检查工号密码')
  } finally {
    loading.value = false
  }
}

// 输入框动态边框
const animateBorder = (el) => {
  const inputWrapper = el?.querySelector('.el-input__wrapper')
  if (!inputWrapper) return

  inputWrapper.style.boxShadow = '0 0 15px rgba(64, 158, 255, 0.3)'
  setTimeout(() => {
    inputWrapper.style.boxShadow = ''
  }, 500)
}
</script>

<style scoped>
.industrial-login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: linear-gradient(45deg, #0f172a, #1e293b);
  overflow: hidden;
}

#dataParticles {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.login-card {
  position: relative;
  z-index: 2;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  margin: 10% auto;
  animation: cardEntrance 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.system-brand {
  text-align: center;
  margin-bottom: 40px;
}
.logo {
  width: 80px;
  margin-bottom: 20px;
  filter: drop-shadow(0 0 8px rgba(64, 158, 255, 0.4));
}
h1 {
  color: #e2e8f0;
  font-size: 24px;
  margin-bottom: 8px;
}
.version {
  color: #64748b;
  font-size: 12px;
}

.industrial-input {
  transition: all 0.3s ease;
  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    color: #e2e8f0;
    &:hover {
      border-color: #3b82f6;
    }
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  background: linear-gradient(135deg, #3b82f6 0%, #6366f1 100%);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  .icon-right {
    margin-left: 8px;
    transition: transform 0.3s ease;
  }
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
    .icon-right {
      transform: translateX(4px);
    }
  }
}

.safety-tips {
  margin-top: 20px;
  color: #94a3b8;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  .el-icon {
    margin-right: 8px;
    color: #f59e0b;
  }
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    margin: 20% auto;
  }
}
</style>