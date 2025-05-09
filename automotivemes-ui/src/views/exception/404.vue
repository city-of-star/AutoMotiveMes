<template>
  <div class="container">
    <!-- 动态齿轮背景 -->
    <div class="gear-bg">
      <div class="gear large reverse"></div>
      <div class="gear medium"></div>
      <div class="gear small reverse"></div>
    </div>

    <!-- 主要内容 -->
    <div class="content">
      <div class="error-code">
        <span>4</span>
        <div class="robot-icon">🤖</div>
        <span>4</span>
      </div>
      <h1 class="title">生产线偏离轨道</h1>
      <p class="subtitle">
        当前页面不在生产序列中<br>
        请检查访问路径或联系质量控制部门
      </p>
      <div class="actions">
        <button @click="goHome" class="home-btn">
          <span class="icon">🏭</span>
          返回首页
        </button>
        <button @click="contactSupport" class="support-btn">
          <span class="icon">🔧</span>
          联系技术支持
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const contactSupport = () => {
  window.location.href = 'https://www.baidu.com'
}
</script>

<style scoped lang="scss">
.container {
  height: 100vh;
  background: linear-gradient(45deg, #2c3e50, #34495e);
  color: #ecf0f1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
}

.gear-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.1;

  .gear {
    position: absolute;
    background: radial-gradient(circle, #7f8c8d 30%, #95a5a6 70%);
    border-radius: 50%;
    animation: rotate 12s linear infinite;

    &::after {
      content: '';
      position: absolute;
      width: 25%;
      height: 25%;
      background: #2c3e50;
      border-radius: 50%;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }

    &.large {
      width: 200px;
      height: 200px;
      top: 20%;
      left: 10%;
    }

    &.medium {
      width: 150px;
      height: 150px;
      top: 60%;
      right: 15%;
    }

    &.small {
      width: 100px;
      height: 100px;
      bottom: 20%;
      left: 30%;
    }

    &.reverse {
      animation-direction: reverse;
    }
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.content {
  text-align: center;
  z-index: 1;
  position: relative;

  .error-code {
    font-size: 10rem;
    font-weight: 900;
    display: flex;
    align-items: center;
    gap: 1rem;
    color: #e67e22;
    text-shadow: 0 0 20px rgba(230, 126, 34, 0.4);

    .robot-icon {
      font-size: 8rem;
      filter: drop-shadow(0 0 10px rgba(230, 126, 34, 0.6));
      animation: float 3s ease-in-out infinite;
    }
  }
}

.title {
  font-size: 2rem;
  margin: 1rem 0;
  color: #bdc3c7;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.subtitle {
  font-size: 1.2rem;
  line-height: 1.6;
  color: #95a5a6;
  margin-bottom: 2rem;
}

.actions {
  display: flex;
  gap: 1.5rem;
  justify-content: center;

  button {
    padding: 1rem 2rem;
    border: none;
    border-radius: 4px;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.8rem;

    .icon {
      font-size: 1.4rem;
    }

    &.home-btn {
      background: #3498db;
      color: white;

      &:hover {
        background: #2980b9;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4);
      }
    }

    &.support-btn {
      background: #e67e22;
      color: white;

      &:hover {
        background: #d35400;
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(230, 126, 34, 0.4);
      }
    }
  }
}

.factory-floor {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100px;
  background: linear-gradient(to top, #3d3d3d, #2d2d2d);

  .conveyor {
    width: 80%;
    height: 20px;
    background: #7f8c8d;
    margin: 0 auto;
    position: relative;
    border-radius: 3px;

    &::before {
      content: '';
      position: absolute;
      width: 100%;
      height: 2px;
      background: linear-gradient(90deg,
          transparent 0%,
          #e67e22 50%,
          transparent 100%);
      animation: conveyor 3s linear infinite;
    }
  }

  .warning-light {
    position: absolute;
    right: 5%;
    top: -30px;
    width: 20px;
    height: 20px;
    background: #e74c3c;
    border-radius: 50%;
    animation: pulse 1.5s infinite;
  }
}

@keyframes conveyor {
  from { transform: translateX(-100%); }
  to { transform: translateX(100%); }
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(231, 76, 60, 0.7); }
  70% { box-shadow: 0 0 0 10px rgba(231, 76, 60, 0); }
  100% { box-shadow: 0 0 0 0 rgba(231, 76, 60, 0); }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
  100% { transform: translateY(0px); }
}
</style>