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
        userId: null,
        username: '',
        realName: '',
        headImg: '',
        email: '',
        phone: '',
        roleId: null,
        deptId: null,
        postId: null,
        status: null,
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
            state.userId = user.userId;
            state.username = user.username;
            state.realName = user.realName;
            state.roleId = user.roleId;
            state.deptId = user.deptId;
            state.postId = user.postId;
            state.headImg = user.headImg;
            state.email = user.email;
            state.phone = user.phone;
            state.status = user.status;
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
        async login({ commit, dispatch  }, data) {
            const response = await service.post('/auth/login', data);
            commit('SET_TOKEN', response.token)
            await dispatch('getUserRoleAndPermission');
            await dispatch('getUserInfo');
            ElMessage.success("登录成功!")
            await router.push({ name: 'home' });
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
                userId: response.userId,
                username: response.username,
                realName: response.realName,
                roleId: response.roleId,
                deptId: response.deptId,
                postId: response.postId,
                headImg: response.headImg,
                email: response.email,
                phone: response.phone,
                status: response.status
            });
        },
        async isValidToken() {
            await service.post('/auth/isValidToken')
        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
            commit('tabBar/CLEAR_TABS', null, { root: true });
            router.push('/login')
        }
    },
    modules: {
    }
}
