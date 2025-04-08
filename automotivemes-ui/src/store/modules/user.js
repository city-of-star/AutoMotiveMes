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
                commit('SET_TOKEN', response.token)
                commit('SET_ROLES', response.roles)
                commit('SET_PERMISSIONS', response.permissions)
                ElMessage.success("登录成功!")
                await router.push({name: 'home'}).then(() => {
                    window.location.reload();  // 刷新页面
                });
        },
        async register(_, data){
            await service.post('/auth/register', data);
            ElMessage.success("注册成功!")
        },
        async getUserRoleAndPermission({ commit }) {
            const response = await service.post('/auth/getRoleAndPermission');
            commit('SET_ROLES', response.roles)
            commit('SET_PERMISSIONS', response.permissions)
        },
        async getUserInfo({ commit }) {
            const response = await service.post('/auth/info');
            commit('SET_USERINFO', {
                realName: response.realName,
                email: response.email,
                phone: response.phone
            })
        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
            router.push('/login')
        }
    },
    modules: {
    }
}
