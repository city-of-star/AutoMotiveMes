<template>
  <Login v-if="shouldShowLogin" />
  <Layout v-else-if="shouldUseLayout">
    <router-view />
  </Layout>
  <router-view v-else />
</template>

<script setup>
import { computed, watchEffect, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import Layout from '@/layout/Index.vue'
import Login from '@/views/auth/Login.vue'
import store from '@/store'
import router from "@/router"
import NProgress from "nprogress"

const route = useRoute()

const isAuthenticated = computed(() => !!store.state.user.token)
const isLoginRoute = computed(() => route.name === 'login')
const is404Route = computed(() => route.name === '404')

const shouldShowLogin = computed(() => !isAuthenticated.value && isLoginRoute.value)
const shouldUseLayout = computed(() => isAuthenticated.value && !is404Route.value)

// 路由守卫逻辑
watchEffect(() => {
  if (isAuthenticated.value && isLoginRoute.value) {
    router.replace('/').catch(() => {})
  }
})

// 进度条配置逻辑封装
const initProgress = () => {
  NProgress.configure({
    showSpinner: false,
    easing: 'ease',
    speed: 500,
    color: store.state.user.themeColor,
  })
}

// 生命周期钩子
onMounted(initProgress)

// 主题色变化监听
watch(
    () => store.state.user.themeColor,
    (color) => NProgress.configure({ color })
)
</script>

<style>
</style>