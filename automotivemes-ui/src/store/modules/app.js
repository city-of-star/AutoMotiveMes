export default {
    namespaced: true,
    state: {
        // 侧边栏折叠展开
        sidebar: {
            opened: false,
            widthExpend: window.CONFIG.theme.sidebar.widthExpend,
            widthFold: window.CONFIG.theme.sidebar.widthFold
        },

        // 按钮配色
        btnUpdateColor: window.CONFIG.theme.btnColor.btnUpdateColor,  // 修改
        btnDeleteColor: window.CONFIG.theme.btnColor.btnDeleteColor,  // 删除
        btnImportColor: window.CONFIG.theme.btnColor.btnImportColor,  // 导入
        btnExportColor: window.CONFIG.theme.btnColor.btnExportColor,  // 导出
    },
    mutations: {
        // 切换侧边栏状态
        TOGGLE_SIDEBAR(state) {
            state.sidebar.opened = !state.sidebar.opened
        }
    }
}