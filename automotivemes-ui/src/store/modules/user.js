import service from '@/utils/axios'
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
        theme_color: '#1890FF',
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
            const response = await service.post('/auth/login', data);
            if (response.msg === 'success') {  // 登录成功
                commit('SET_TOKEN', response.data.token)
                commit('SET_ROLES', response.data.roles)
                commit('SET_PERMISSIONS', response.data.permissions)
                ElMessage.success("登录成功!")

                await router.push('/').then(() => {
                    window.location.reload();  // 刷新页面
                });

                console.log(response)
            } else {
                ElMessage.error(response.msg)
            }
        },
        async register(_, data){
            const response = await service.post('/auth/register', data);
            if (response.msg === 'success') {  // 注册成功
                ElMessage.success("注册成功!")
            } else {
                ElMessage.error(response.msg)
            }
        },
        async getUserRoleAndPermission({ commit, state }) {
            const response = await service.post('/auth/getRoleAndPermission');
            if (response.msg === 'success') {
                commit('SET_ROLES', response.data.roles)
                commit('SET_PERMISSIONS', response.data.permissions)
                return {
                    roles: state.roles,
                    permissions: state.permissions
                }
            } else {
                console.log("getUserRoleAndPermission", response.msg)
            }
        },
        async getUserInfo({ commit }) {
            const response = await service.post('/auth/info');
            if (response.msg === 'success') {
                commit('SET_USERINFO', {
                    realName: response.data.realName,
                    email: response.data.email,
                    phone: response.data.phone
                })
                return response
            } else {
                console.log("getUserInfo", response.msg)
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
