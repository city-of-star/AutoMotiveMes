const { defineConfig } = require('@vue/cli-service');
const path = require('path');

module.exports = defineConfig({
  transpileDependencies: true,

  chainWebpack: config => {
    config.resolve.alias
        .set('@', path.resolve(__dirname, 'src'));
  },

  css: {
    loaderOptions: {
      scss: {
        additionalData: `@import "~@/assets/styles/variables.scss";`
      }
    }
  },

  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
      }
    }
  }
});