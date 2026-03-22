<template>
  <div class="login-page">
    <el-card class="card" shadow="hover">
      <h1>登录</h1>
      <el-form :model="form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            autocomplete="current-password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <p v-if="error" class="error">{{ error }}</p>
        <el-form-item>
          <el-button
            type="primary"
            class="submit-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authApi } from '@/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    const res = await authApi.login(form)
    // 兼容 res.data.data 或 res.data 直接为数据
    const data = res?.data?.data ?? res?.data
    const token = data?.token
    if (token) {
      userStore.setToken(token)
      userStore.setUser({ username: data.username, nickname: data.nickname, role: data.role })
      const redirect = (typeof route.query.redirect === 'string') ? route.query.redirect : '/'
      await router.replace(redirect).catch(() => {})
      // 若路由未生效则强制刷新，确保 Header 等组件更新
      if (router.currentRoute.value.path === '/login') {
        window.location.href = redirect
      }
    } else {
      error.value = res?.data?.message || '登录失败'
    }
  } catch (e) {
    error.value = e?.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.card {
  width: 100%;
  max-width: 360px;
}
.card h1 {
  margin-bottom: 1.5rem;
  text-align: center;
}
.submit-btn {
  width: 100%;
}
.error {
  color: #dc2626;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}
.tip {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
</style>
