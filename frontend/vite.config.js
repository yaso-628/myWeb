import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 开发环境下，将 /uploads/** 也代理到后端，方便访问本地上传的图片和音乐
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
