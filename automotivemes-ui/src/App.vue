<template>
  <Login v-if="showLogin" />
  <Layout v-else-if="!is404Route">
    <router-view/>
  </Layout>
  <router-view v-else />
</template>

<script setup>
import {ref, computed, watchEffect} from 'vue'
import { useRoute } from 'vue-router'
import Layout from '@/layout/Index.vue'
import Login from '@/views/auth/Login.vue'
import store from '@/store'
import router from "@/router";

const route = useRoute()
const showLogin = ref(false)

// 判断当前是否404路由
const is404Route = computed(() => route.path === '/404')

watchEffect(() => {
  const isAuthenticated = !!store.state.user.token
  const isLoginRoute = route.path === '/login'

  if (isAuthenticated && isLoginRoute) {
    router.replace('/')  // 已登录时访问登录页自动跳转首页
    return
  }

  showLogin.value = !isAuthenticated && isLoginRoute
})
</script>

<style>
/* 保持样式不变 */
</style>