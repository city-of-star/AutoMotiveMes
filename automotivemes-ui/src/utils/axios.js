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

// 请求拦截器
service.interceptors.request.use(
    config => {
        if (store.state.user.token) {
            config.headers.Authorization = `Bearer ${store.state.user.token}`
        }
        return config
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        // 直接返回数据
        return response.data.data;
    },
    error => {
        // 统一错误处理
        if (error.response) {
            const httpStatus = error.response.status
            const res = error.response.data

            // 根据 HTTP 状态码处理
            switch (httpStatus) {
                case 400:
                    ElMessage.error(res.msg || '请求参数错误')
                    break
                case 401:
                    handleUnauthorized()
                    break
                case 403:
                    ElMessage.error('没有操作权限')
                    break
                case 404:
                    router.push({ name: '404' })
                    ElMessage.error(res.msg || '资源不存在')
                    break
                case 405:
                    ElMessage.error(res.msg || '请求方法不匹配异常')
                    break
                case 500:
                    ElMessage.error(res.msg || '系统内部错误')
                    break
                case 502:
                case 503:
                    ElMessage.error('服务维护升级中，请30分钟后重试')
                    break
                case 504: ElMessage.error('网关响应超时')
                    break
                default: {
                    if (httpStatus >= 500) {
                        ElMessage.error('服务器开小差了，工程师正在抢修中')
                    } else {
                        ElMessage.error(`${httpStatus}: 请求发生未知错误`)
                    }
                }
            }
        } else {
            // 处理无响应的情况（超时/断网）
            if (error.code === 'ECONNABORTED') {
                ElMessage.error('请求超时，请检查网络后重试')
            } else if (!window.navigator.onLine) {
                ElMessage.error('网络连接已断开，请检查网络设置')
            } else {
                ElMessage.error('服务连接异常，请稍后重试')
            }
        }

        return Promise.reject(error)
    }
)

// 统一授权处理
function handleUnauthorized() {
    ElMessage.error('登录信息已过期，请重新登录')
    store.dispatch('user/logout').then(() => {
        router.push({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath }
        })
    })
}

export default service