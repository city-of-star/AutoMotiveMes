import { createStore } from 'vuex'
import user from '@/store/modules/user.js';
import tabsNav from '@/store/modules/tabsNav.js'
import equipment from "@/store/modules/equipment";

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
    tabsNav,
    equipment,
  }
})