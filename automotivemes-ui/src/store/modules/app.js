export default {
    namespaced: true,
    state: {
        sidebar: {
            opened: false,
            widthExpend: 250,
            widthFold: 64
        }
    },
    mutations: {
        TOGGLE_SIDEBAR(state) {
            state.sidebar.opened = !state.sidebar.opened
        }
    }
}