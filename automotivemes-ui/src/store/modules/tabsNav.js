export default {
    namespaced: true,
    state: {
        visitedTabs: [],
        unsavedChanges: new Set()
    },
    getters: {
        visitedTabs: state => state.visitedTabs,
        unsavedChanges: state => state.unsavedChanges
    },
    mutations: {
        ADD_TAB(state, route) {
            const exists = state.visitedTabs.some(tab => tab.fullPath === route.fullPath)
            if (!exists) {
                state.visitedTabs.push({
                    ...route,
                    meta: {
                        affix: route.meta.affix || false,
                        ...route.meta
                    }
                })
            }
        },

        REMOVE_TAB(state, targetTab) {
            state.visitedTabs = state.visitedTabs.filter(tab =>
                tab.meta.affix || tab.fullPath !== targetTab.fullPath
            )
        },

        REFRESH_TAB(state, fullPath) {
            state.unsavedChanges.delete(fullPath)
        },

        UPDATE_UNSAVED(state, { fullPath, isUnsaved }) {
            isUnsaved ?
                state.unsavedChanges.add(fullPath) :
                state.unsavedChanges.delete(fullPath)
        },

        CLOSE_OTHER_TABS(state, currentTab) {
            state.visitedTabs = state.visitedTabs.filter(tab =>
                tab.meta.affix || tab.fullPath === currentTab.fullPath
            )
        },

        CLOSE_RIGHT_TABS(state, currentTab) {
            const index = state.visitedTabs.findIndex(tab =>
                tab.fullPath === currentTab.fullPath
            )
            state.visitedTabs = state.visitedTabs.slice(0, index + 1)
                .concat(state.visitedTabs.filter(tab => tab.meta.affix))
        },

        TOGGLE_PIN_TAB(state, tab) {
            const target = state.visitedTabs.find(t => t.fullPath === tab.fullPath)
            if (target) {
                target.meta.affix = !target.meta.affix
            }
        }
    },
    actions: {
        closeTab({ commit, state }, tab) {
            return new Promise(resolve => {
                commit('REMOVE_TAB', tab)
                const lastPath = state.visitedTabs.length > 0 ?
                    state.visitedTabs[state.visitedTabs.length - 1].fullPath : '/'
                resolve(lastPath)
            })
        }
    },
    modules: {
    }
}
