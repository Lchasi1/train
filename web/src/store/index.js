//全局变量，方便页面的传递参数
import { createStore } from 'vuex'

export default createStore({
  state: {
    member:{}
  },
  getters: {
  },
  mutations: {//set方法
    setMember(state,member){
      state.member = member;
    }
  },
  actions: {//异步任务

  },
  modules: {
  }
})
