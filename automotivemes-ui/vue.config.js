const { defineConfig } = require('@vue/cli-service');
const path = require('path');

module.exports = defineConfig({
  transpileDependencies: true,

  chainWebpack: config => {
    // 配置 webpack 的 resolve.alias，用于设置路径别名
    config.resolve.alias
        // 设置 '@' 别名，指向项目的 src 目录
        // __dirname 表示当前文件所在的目录，通过 path.resolve 方法将其与 'src' 拼接成完整路径
        .set('@', path.resolve(__dirname, 'src'));
  },

  css: {
    loaderOptions: {
      scss: {
        // additionalData 选项用于在每个 SCSS 文件开头自动注入指定的代码
        // 这里导入了项目 src/assets/styles 目录下的 variables.scss 文件
        // '~' 表示从模块解析路径开始查找，结合 '@' 别名可以定位到 src 目录
        additionalData: `@import "~@/assets/styles/variables.scss";`
      }
    }
  },

  // 配置开发服务器的选项
  devServer: {
    // 配置代理，用于解决开发环境中的跨域问题
    proxy: {
      // 当请求的路径以 '/api' 开头时，会触发代理
      '/api': {
        // 代理的目标地址，这里将请求转发到本地的 3000 端口
        target: 'http://localhost:3000',
        // 改变请求头的 origin 属性，使服务器认为请求来自目标地址
        changeOrigin: true,
      }
    }
  }
});