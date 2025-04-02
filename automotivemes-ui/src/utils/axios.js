import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from "@/router";

const service = axios.create({
    baseURL: 'http://localhost:3000/api',
    timeout: 5000,
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
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        if (response.status === 200) {
            return res
        } else {
            // ElMessage.error(res.message || 'Error')
            return Promise.reject(new Error(res.message || 'Error'))
        }
    },
    error => {
        // ElMessage.error(error.response?.data?.message || error.message)
        if (error.response?.status === 401) {
            store.dispatch('user/logout')
            router.push('/login')
        } else if (error.response?.status === 403) {
            ElMessage.error('权限不足，无法访问')
        }
        return Promise.reject(error)
    }
)

export default service