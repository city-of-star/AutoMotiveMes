import service from '@/utils/axios'
import { ElMessage } from "element-plus";
import router from "@/router";

export default {
    namespaced: true,
    state: {
        // 认证相关
        token: localStorage.getItem('token') || '',
        roles: [],
        permissions: [],
        routes: [],

        // 主题色
        themeColor: localStorage.getItem('themeColor') || '#409EFF',

        // 用户信息
        userId: null,
        username: '',
        realName: localStorage.getItem('realName') || '',
        headImg: localStorage.getItem('headImg') || '',
        sex: '',
        email: '',
        phone: '',
        roleName: '',
        deptName: '',
        postName: '',
        createTime: null,
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
            state.sex = user.sex;
            state.themeColor = user.themeColor;
            state.roleName = user.roleName;
            state.deptName = user.deptName;
            state.postName = user.postName;
            state.headImg = user.headImg;
            state.email = user.email;
            state.phone = user.phone;
            state.createTime = user.createTime;
            localStorage.setItem('realName', user.realName);
            localStorage.setItem('headImg', user.headImg);
            localStorage.setItem('themeColor', user.themeColor);
        },
        CLEAR_AUTH(state) {
            state.token = ''
            state.roles= []
            state.permissions= []
            state.routes= []
            localStorage.removeItem('token')
            localStorage.removeItem('realName');
            localStorage.removeItem('headImg');
            localStorage.removeItem('themeColor');
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
                sex: response.sex,
                themeColor: response.themeColor,
                roleName: response.roleName,
                deptName: response.deptName,
                postName: response.postName,
                headImg: response.headImg,
                email: response.email,
                phone: response.phone,
                createTime: response.createTime,
            });
            return response;
        },
        async isValidToken() {
            await service.post('/auth/isValidToken')
        },
        logout({ commit }) {
            commit('CLEAR_AUTH')
            commit('tabBar/CLEAR_TABS', null, { root: true });
            router.push({name: 'login'})
        }
    },
}
