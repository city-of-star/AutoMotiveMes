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
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        if (response.status === 200) {
            return response.data
        }
    },
    error => {
        if (error.response?.data.code === 400) {
            ElMessage.error(error.response?.data.msg)
        } else if (error.response?.data.code === 401) {
            ElMessage.error('登录信息过期，请重新登录')
            store.dispatch('user/logout')
            router.push({name: 'login'})
        } else if (error.response?.data.code === 403) {
            ElMessage.error('权限不足，无法访问')
        } else if (error.response?.data.code === 404) {
            router.push({name: '404'})
        }
        return error.response.data
    }
)

export default service