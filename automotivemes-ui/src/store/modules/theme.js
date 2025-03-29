export default {
    namespaced: true,
    state: () => ({
        // 当前主题颜色
        themeColor: '#409EFF',
        // 当前文本颜色
        textColor: '#303133',
        // 当前背景颜色
        backgroundColor: '#ffffff',
        // 当前按钮颜色
        buttonColor: '#409EFF',
        // 当前按钮悬浮色
        buttonHover: '#1557B3',
        // 当前辅助色
        borderColor: '#B3D1FF',

        // 预设主题列表
        presetThemes: [
            {
                name: '科技蓝',
                themeColor: '#2979FF', // 更沉稳的科技蓝
                textColor: '#2D3748', // 提高对比度
                backgroundColor: '#F8FAFF', // 更柔和的背景
                buttonColor: '#2979FF',
                buttonHover: '#1557B3', // 增加悬停状态
                borderColor: '#B3D1FF' // 新增辅助色
            },
            {
                name: '森林绿',
                themeColor: '#38A169', // 降低饱和度
                textColor: '#2D3748',
                backgroundColor: '#F3FEF7',
                buttonColor: '#2F855A', // 主按钮更深
                buttonHover: '#276749',
                borderColor: '#9AE6B4'
            },
            {
                name: '琥珀黄',
                themeColor: '#D69E2E', // 更温暖的琥珀色
                textColor: '#2D3748',
                backgroundColor: '#FFFCF5',
                buttonColor: '#B7791F', // 主按钮加深
                buttonHover: '#975A16',
                borderColor: '#FBD38D'
            },
            {
                name: '蔷薇粉',
                themeColor: '#ED64A6', // 更现代的粉色调
                textColor: '#2D3748',
                backgroundColor: '#FFF5F7',
                buttonColor: '#D53F8C', // 增加深度
                buttonHover: '#B83280',
                borderColor: '#FBB6CE'
            },
            {
                name: '极光白',
                themeColor: '#718096', // 中性灰替代纯灰
                textColor: '#2D3748',
                backgroundColor: '#FFFFFF',
                buttonColor: '#4A5568', // 提高可读性
                buttonHover: '#2D3748',
                borderColor: '#E2E8F0'
            },
            {
                name: '夜影黑',
                themeColor: '#CBD5E0', // 浅灰替代纯白
                textColor: '#E2E8F0', // 更柔和的文本色
                backgroundColor: '#1A202C', // 深灰替代纯黑
                buttonColor: '#4A5568', // 提高可见度
                buttonHover: '#718096',
                borderColor: '#2D3748'
            }
        ]
    }),
    mutations: {

    },
    actions: {

    },
    getters: {

    }
}