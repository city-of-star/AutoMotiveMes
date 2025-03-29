import service from '@/utils/request'
import { ElMessage } from "element-plus";
import router from "@/router";

export default {
    namespaced: true,
    state: {
        token: localStorage.getItem('token') || '',
        roles: [],
        permissions: [],
        routes: [],
        username: '',
        realName: '',
        email: '',
        phone: '',
    },
    getters: {
    },
    mutations: {
        SET_TOKEN(state, token) {
            state.token = token;
            localStorage.setItem('token', token);
        },
        SET_ROLES: (state, roles) => {
            state.roles = roles
        },
        SET_PERMISSIONS: (state, permissions) => {
            state.permissions = permissions
        },
        SET_ROUTES: (state, routes) => {
            state.routes = routes
        },
        SET_USERINFO(state, user) {
            state.realName = user.realName
            state.email = user.email
            state.phone = user.phone
        },
        CLEAR_AUTH(state) {
            state.token = ''
            state.roles= []
            state.permissions= []
            state.routes= []
            state.username= ''
            state.realName= ''
            state.email= ''
            state.phone= ''
            localStorage.removeItem('token')
        }
    },
    actions: {
        async login({ commit }, data) {
            const response = await service.post('/user/login', data);
            console.log("response", response)
            if (response.message === 'Success') {  // 登录成功
                commit('SET_TOKEN', response.data.token)
                commit('SET_ROLES', response.data.roles)
                commit('SET_PERMISSIONS', response.data.permissions)
                ElMessage.success("登录成功!")
                await router.push('/').then(() => {
                    window.location.reload();  // 刷新页面
                });
            } else {
                ElMessage.error(response.data.message)
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
        async getUserRoleAndPermission({ commit, state }) {
            const response = await service.post('/user/getRoleAndPermission');
            if (response.message === 'Success') {
                commit('SET_ROLES', response.data.roles)
                commit('SET_PERMISSIONS', response.data.permissions)
                return {
                    roles: state.roles,
                    permissions: state.permissions
                }
            } else {
                console.log("getUserRoleAndPermission", response.message)
            }
        },
        async getUserInfo({ commit }) {
            const response = await service.post('/user/info');
            if (response.message === 'Success') {
                commit('SET_USERINFO', {
                    realName: response.data.realName,
                    email: response.data.email,
                    phone: response.data.phone
                })
                return response
            } else {
                console.log("getUserInfo", response.message)
            }

        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
            router.push('/login')
        }
    },
    modules: {
    }
}
