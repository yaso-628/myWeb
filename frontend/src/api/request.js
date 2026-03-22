import axios from 'axios'
import { useUserStore } from '@/store/user'

const request = axios.create({
  baseURL: 'http://101.43.55.12:8080/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

request.interceptors.request.use(config => {
  try {
    const store = useUserStore()
    const token = store.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  } catch (_) {}
  return config
})

request.interceptors.response.use(
  res => res,
  err => {
    const status = err?.response?.status
    const url = err?.config?.url || ''

    if (status === 401) {
      // 未登录：清理本地登录态并跳转登录页
      try {
        useUserStore().clear()
      } catch (_) {}
      window.location.href = '/login'
    } else if (status === 403) {
      // 仅在访问后台管理接口时，才因为权限不足跳转到首页
      if (url.startsWith('/admin')) {
        window.location.href = '/'
      }
      // 其他 403（例如某些前台接口的权限控制）不强制跳转，让当前页面自行处理错误
    }
    return Promise.reject(err)
  }
)

export default request
