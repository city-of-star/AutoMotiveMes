export default {
    namespaced: true,
    state: {
        sidebar: {
            opened: false,
            widthExpend: 250,
            widthFold: 64
        },
        btn_update_color: '#71E2A3',  // 修改
        btn_delete_color: '#FF929E',  // 删除
        btn_import_color: '#969A9E',  // 导入
        btn_export_color: '#FFBA00',  // 导出
    },
    mutations: {
        TOGGLE_SIDEBAR(state) {
            state.sidebar.opened = !state.sidebar.opened
        }
    }
}