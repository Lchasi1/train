//全局变量，方便页面的传递参数
import { createStore } from 'vuex'

const MEMBER = "MEMBER";

export default createStore({
  state: {
    member: window.SessionStorage.get(MEMBER) || {}
  },
  getters: {
  },
  mutations: {
    setMember (state, _member) {
      state.member = _member;
      window.SessionStorage.set(MEMBER, _member);
    }
  },
  actions: {
  },
  modules: {
  }
})
