import service from '@/utils/request'
import { ElMessage } from "element-plus";
import router from "@/router";

export default {
    namespaced: true,
    state: {
        token: localStorage.getItem('token') || '',
        username: '',
        realName: '',
        email: '',
        phone: '',
    },
    getters: {
    },
    mutations: {
        SET_TOKEN(state, token, username) {
            state.token = token
            state.username = username
            localStorage.setItem('token', token)
        },
        SET_USERInfo(state, user) {
            state.realName = user.realName
            state.email = user.email
            state.phone = user.phone
        },
        CLEAR_AUTH(state) {
            state.token = ''
            state.user = null
            localStorage.removeItem('token')
            localStorage.removeItem('user')
        }
    },
    actions: {
        async login({ commit }, data) {
            const response = await service.post('/user/login', data);
            if (response.message === 'Success') {  // 登录成功
                ElMessage.success("登录成功!")
                commit('SET_TOKEN', response.data, data.username)
                await router.push('/')
            } else {
                ElMessage.error(response.message)
            }
        },
        async register(_, data){
            const response = await service.post('/user/register', data);
            if (response.message === 'Success') {  // 注册成功
                ElMessage.success("注册成功!")
            } else {
                ElMessage.error(response.message)
            }
        },
        async getUserInfo({commit}, data) {
            const response = await service.post('/user/info', data);
            commit('SET_USERInfo', response.data)
            return response
        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
        }
    },
    modules: {
    }
}
