import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css';

// 导入你项目原有的 request 工具
import request from './utils/request'

const app = createApp(App)

// 将 request 实例提供给所有组件
app.provide('$request', request)

app.use(router)
app.mount('#app')