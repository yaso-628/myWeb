<template>
  <div class="register-page">
    <NeCard>
      <h1 class="register-title">注册</h1>

      <form class="register-form" @submit.prevent="handleRegister">
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

        <label class="field-label">昵称</label>
        <NeInput v-model="form.nickname" placeholder="选填" />

        <label class="field-label">邮箱</label>
        <NeInput v-model="form.email" type="email" placeholder="选填" />

        <p v-if="error" class="error">{{ error }}</p>

        <div class="submit-row">
          <button type="submit" class="btn-primary-solid" :disabled="loading" v-ripple>
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
      </form>

      <p class="tip">
        已有账号？<router-link to="/login">去登录</router-link>
      </p>
    </NeCard>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'
import NeCard from '@/components/bits2d/NeCard.vue'
import NeInput from '@/components/bits2d/NeInput.vue'

const router = useRouter()
const form = reactive({ username: '', password: '', nickname: '', email: '' })
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  error.value = ''
  loading.value = true
  try {
    await authApi.register(form)
    router.push('/login')
  } catch (e) {
    error.value = e?.response?.data?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.register-title {
  text-align: center;
  margin-bottom: 1.25rem;
}

.register-form {
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
