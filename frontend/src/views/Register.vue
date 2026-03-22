<template>
  <div class="register-page">
    <el-card class="card" shadow="hover">
      <h1>注册</h1>
      <el-form :model="form" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            autocomplete="new-password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="选填" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" type="email" placeholder="选填" />
        </el-form-item>
        <p v-if="error" class="error">{{ error }}</p>
        <el-form-item>
          <el-button
            type="primary"
            class="submit-btn"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p class="tip">已有账号？<router-link to="/login">去登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'

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
