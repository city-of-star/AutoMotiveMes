<template>
  <Layout>
    <router-view/>
  </Layout>
</template>

<script setup>
import Layout from '@/layout/Index.vue'
import { onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
onMounted(() => {
  store.dispatch('equipment/initWebSocket')
})
</script>

<style scoped>

</style>
