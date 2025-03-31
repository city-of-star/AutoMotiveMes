import { createStore } from 'vuex'
import user from './modules/user.js';
import equipment from "./modules/equipment.js";
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
    equipment,
    app,
    tabBar,
  }
})