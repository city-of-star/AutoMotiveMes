const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false // 禁用 ESLint 检查，这样就不会有组件命名警告了，但会同时禁用其他 ESLint 规则
})
