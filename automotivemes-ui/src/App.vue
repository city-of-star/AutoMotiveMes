<template>
  <Login v-if="!store.state.user.token" />
  <Layout v-else>
    <router-view/>
  </Layout>
</template>

<script setup>
import Layout from '@/layout/Index.vue';
import Login from '@/views/auth/Login.vue'
import store from "@/store";

</script>

<style>

</style>