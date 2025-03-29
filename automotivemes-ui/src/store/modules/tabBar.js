export default {
    namespaced: true,
    state: {
        tabs: []
    },
    mutations: {
        ADD_TAB(state, route) {
            state.tabs.push(route)
        },
        REMOVE_TAB(state, index) {
            state.tabs.splice(index, 1)
        },
        SET_TABS(state, tabs) {
            state.tabs = tabs
        }
    }
}