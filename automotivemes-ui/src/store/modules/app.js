export default {
    namespaced: true,
    state: {
        // 侧边栏折叠展开
        sidebar: {
            opened: false,
            widthExpend: window.CONFIG.theme.sidebar.widthExpend,
            widthFold: window.CONFIG.theme.sidebar.widthFold
        },
    },
    mutations: {
        // 切换侧边栏状态
        TOGGLE_SIDEBAR(state) {
            state.sidebar.opened = !state.sidebar.opened
        }
    }
}