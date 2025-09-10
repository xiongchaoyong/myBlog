import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { useUserStore } from '@/stores/userStore'

//安装element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'; // 导入所有图标

const app = createApp(App)
const   pinia = createPinia()
app.use(pinia)

// 全局注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}


// 恢复用户登录状态
const userStore = useUserStore()
userStore.restoreFromLocalStorage()

app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')

