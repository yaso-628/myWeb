<template>
  <div class="login-page">
    <NeCard>
      <h1 class="login-title">登录</h1>

      <form class="login-form" @submit.prevent="handleLogin">
        <label class="field-label">用户名</label>
        <NeInput
          v-model="form.username"
          placeholder="请输入用户名"
          required
        />

        <label class="field-label password-label">密码</label>
        <NeInput
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          required
        />

        <p v-if="error" class="error">{{ error }}</p>

        <div class="submit-row">
          <NeButton :loading="loading" :disabled="loading" @click="handleLogin">
            登录
          </NeButton>
        </div>
      </form>

      <p class="tip">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </NeCard>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { authApi } from '@/api'
import { useUserStore } from '@/store/user'
import NeCard from '@/components/bits2d/NeCard.vue'
import NeInput from '@/components/bits2d/NeInput.vue'
import NeButton from '@/components/bits2d/NeButton.vue'

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
.login-title {
  text-align: center;
  margin-bottom: 1.25rem;
}

.login-form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 0.8rem;
}

.field-label {
  font-size: 0.92rem;
  font-weight: 650;
  color: var(--color-text);
}

.password-label {
  margin-top: 0.35rem;
}

.error {
  color: #dc2626;
  font-size: 0.875rem;
  margin-top: 0.15rem;
}

.submit-row {
  display: flex;
  justify-content: center;
  margin-top: 0.35rem;
}
.tip {
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
</style>
