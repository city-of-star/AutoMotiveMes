import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// ElementPlus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs' // 引入中文语言包

createApp(App).use(store).use(router).use(ElementPlus, {locale: zhCn}).mount('#app')
