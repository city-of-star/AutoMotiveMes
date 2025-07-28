import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { websocket } from './utils/websocket';

// 动态加载配置文件
const loadConfig = async () => {
    try {
        let response = ''
        if (process.env.NODE_ENV === 'development') {
            response = await fetch('/config/dev.config.js')
        } else if (process.env.NODE_ENV === 'production') {
            response = await fetch('/config/prod.config.js')
        }
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

// 初始化WebSocket连接
websocket.init();