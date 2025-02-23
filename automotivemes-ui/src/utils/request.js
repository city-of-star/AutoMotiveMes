import axios from 'axios'
import store from '@/store'
import router from '@/router'

const service = axios.create({
    baseURL: 'http://localhost:3000',
    timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(config => {
    if (store.state.user.token) {
        config.headers.Authorization = `Bearer ${store.state.user.token}`
    }
    return config
}, error => {
    return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response && error.response.status === 401) {
            router.push('/login')
        }
        return Promise.reject(error)
    }
)

export default service