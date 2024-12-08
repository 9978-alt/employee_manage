import { createApp } from 'vue'
import App from './App.vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'
import router from "./routers/router"
import pinia from './stores/index'
// import mitt from 'mitt'
// app.config.globalProperties.Bus = mitt()



const app = createApp(App)
app.use(router)
app.use(pinia)
app.use(ElementPlus, {
    locale: zhCn,
})
app.mount('#app')
