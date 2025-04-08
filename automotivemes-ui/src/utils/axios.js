import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from "@/router";

const service = axios.create({
    baseURL: 'http://localhost:3000/api',
    timeout: 15000, // 超时调整为15秒
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
        const res = response.data

        // HTTP 状态码为 200 的情况下处理业务状态码
        if (response.status === 200) {
            // 业务成功（code 200）
            if (res.code === 200) {
                return res.data // 直接返回接口数据
            }

            // 直接在前端显示业务错误信息
            const errorMessage = res.msg || '操作失败'
            ElMessage.error(errorMessage)
            return Promise.reject(new Error(errorMessage))
        }

        return Promise.reject(new Error("非预期响应"))
    },
    error => {
        // 统一错误处理
        if (error.response) {
            const httpStatus = error.response.status
            const res = error.response.data || {}

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
        } else if (error.isBusinessError) { // 捕获业务错误
            handleBusinessError(error)
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

function handleBusinessError(error) {
    const businessErrorMap = {
        400: '请求参数错误',
        401: '会话已过期',
        403: '没有操作权限',
        404: '资源不存在',
        500: '业务处理失败'
    }

    ElMessage.error(
        error.message ||
        businessErrorMap[error.code] ||
        `业务错误 (${error.code})`
    )

    // 特殊业务状态码处理
    if (error.code === 401) {
        handleUnauthorized()
    }
}

export default service