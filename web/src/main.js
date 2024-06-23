import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'


//将app.vue引入到indexhtml中
createApp(App).use(store).use(router).mount('#app')

