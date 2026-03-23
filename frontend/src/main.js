import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import { createUI } from '@vuebits/ui'
import 'element-plus/dist/index.css'
import './assets/bits2d.css'
import '@vuebits/ui/dist/style.css'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import { useThemeStore } from './store/theme'
import vRipple from './directives/ripple'

const app = createApp(App)
app.directive('ripple', vRipple)
const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.use(createUI({}))
useThemeStore() // 初始化主题（store watch 会设置 dark class）
app.mount('#app')
