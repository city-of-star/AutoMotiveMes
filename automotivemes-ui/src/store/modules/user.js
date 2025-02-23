import service from '@/utils/request.js';

// 过滤异步路由的函数，原代码中未定义，这里先简单定义一个空函数占位
function filterAsyncRoutes(data) {
    return data;
}

export default {
    state: {
        id: null,
        username: null,
        token: localStorage.getItem('token'),
        userInfo: null,
        permissions: [],
        isDynamicAdded: false,
        routes: [],
        realtimeData: [],
        historyData: []
    },
    getters: {
        // 可以根据需要添加 getters
    },
    mutations: {
        SET_TOKEN: (state, token) => {
            state.token = token;
            localStorage.setItem('token', token);
        },
        SET_USERINFO: (state, info) => {
            state.userInfo = info;
        },
        SET_REALTIME_DATA: (state, data) => {
            state.realtimeData = data;
        }
    },
    actions: {
        login({ commit }, { username, password }) {
            return service.post('/api/auth/login', { username, password })
                .then(res => {
                    commit('SET_TOKEN', res.data.token);
                    return res;
                });
        },
        async generateRoutes({ state }) {
            // 从后端获取权限数据
            const { data } = await service.get('/api/user/permissions');
            const routes = filterAsyncRoutes(data);
            state.routes = routes;
            state.isDynamicAdded = true;
            return routes;
        },
        fetchRealtimeData({ commit }) {
            return service.get('/api/devices/realtime')
                .then(res => commit('SET_REALTIME_DATA', res.data));
        }
    },
    modules: {
        // 如果后续需要模块化，可以再添加
    }
};