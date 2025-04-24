import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from "@/router";

const service = axios.create({
    baseURL: window.CONFIG.api.baseURL,
    timeout: window.CONFIG.api.timeout,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器（保持不变）
service.interceptors.request.use(
    config => {
        if (store.state.user.token) {
            config.headers.Authorization = `Bearer ${store.state.user.token}`
        }
        return config
    }
)

// 响应拦截器（优化后）
service.interceptors.response.use(
    response => {
        // HTTP 200 表示请求成功，直接返回数据
        return response.data.data;
    },
    error => {
        // 统一错误处理
        if (error.response) {
            const httpStatus = error.response.status
            const res = error.response.data
            console.log(res)

            // 根据 HTTP 状态码处理
            switch (httpStatus) {
                case 400:
                    ElMessage.error(res.msg || '请求参数错误')
                    break
                case 401:
                    handleUnauthorized()
                    break
                case 403:
                    ElMessage.error(res.msg || '没有操作权限')
                    break
                case 404:
                    router.push({ name: '404' })
                    ElMessage.error(res.msg || '资源不存在')
                    break
                case 500:
                    ElMessage.error(res.msg || '服务器内部错误')
                    break
                default:
                    ElMessage.error(`请求错误 (${httpStatus})`)
            }
        } else {
            ElMessage.error('网络连接异常，请检查网络')
        }

        return Promise.reject(error)
    }
)

/* 通用错误处理函数 */
function handleUnauthorized() {
    ElMessage.error('登录已过期或未登录')
    store.dispatch('user/logout').then(() => {
        router.push({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath }
        })
    })
}

export default service