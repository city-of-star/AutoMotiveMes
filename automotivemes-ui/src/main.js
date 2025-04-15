import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 环境判断（使用Vue CLI默认环境变量）
const isDevelopment = process.env.NODE_ENV === 'development'

// 动态加载配置文件
const loadConfig = async () => {
    try {
        const configFile = isDevelopment ? 'dev.config.js' : 'prod.config.js'
        const response = await fetch(`/config/${configFile}`)
        const configScript = await response.text()
        new Function(configScript)()
    } catch (error) {
        console.error('无法加载配置文件: ', error)
    }
}

loadConfig().then(() => {
    createApp(App)
        .use(store)
        .use(router)
        .use(ElementPlus, { locale: zhCn })
        .mount('#app')
});