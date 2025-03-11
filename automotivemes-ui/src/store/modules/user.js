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
        SET_TOKEN(state, payload) {
            state.token = payload.token;
            state.username = payload.username;
            localStorage.setItem('token', payload.token);
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
                commit('SET_TOKEN', {
                    token: response.data.token,
                    username: data.username
                })
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
            commit('SET_USERInfo', {
                realName: response.data.realName,
                email: response.data.email,
                phone: response.data.phone
            })
            return response
        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
        }
    },
    modules: {
    }
}
