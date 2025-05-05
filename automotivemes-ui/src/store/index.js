import { createStore } from 'vuex'
import user from './modules/user.js';
import app from "./modules/app.js";
import tabBar from "./modules/tabBar.js";

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user,
    app,
    tabBar,
  }
})