<template>
  <div class="user-profile-page">
    <div class="card">
      <h1>个人信息</h1>
      <p class="subtitle">这些信息会用于评论区头像和昵称展示。</p>

      <div v-if="loading" class="loading">加载中...</div>
      <template v-else>
        <div class="avatar-row">
          <div class="avatar-preview">
            <div v-if="form.avatar" class="avatar">
              <img :src="toAssetUrl(form.avatar)" alt="头像预览" />
            </div>
            <div v-else class="avatar placeholder">
              <span>{{ (form.nickname || userStore.user?.username || '我').charAt(0).toUpperCase() }}</span>
            </div>
          </div>
          <div class="avatar-input">
            <label>头像图片</label>
            <input type="file" accept="image/*" @change="onAvatarFileChange" />
            <p class="hint">支持本地上传，单个文件大小不超过 20MB。</p>
            <label>或使用头像 URL</label>
            <input
              v-model="form.avatar"
              type="url"
              placeholder="粘贴一张图片链接，例如 https://example.com/avatar.png"
            />
          </div>
        </div>

        <div class="field">
          <label>昵称</label>
          <input v-model="form.nickname" type="text" placeholder="展示在评论区的名字" />
        </div>
        <div class="field">
          <label>邮箱</label>
          <input v-model="form.email" type="email" placeholder="可用于找回密码或联系" />
        </div>

        <div class="footer">
          <button :disabled="saving" @click="save" v-ripple>
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
          <span v-if="saved" class="success">已保存</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { userApi } from '@/api'
import { useUserStore } from '@/store/user'
import { resolveAssetUrl } from '@/utils/asset'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const saved = ref(false)
const uploadingAvatar = ref(false)

const form = reactive({
  nickname: '',
  email: '',
  avatar: ''
})

function toAssetUrl(url) {
  return resolveAssetUrl(url)
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await userApi.me()
    const data = res?.data?.data
    if (data) {
      form.nickname = data.nickname ?? ''
      form.email = data.email ?? ''
      form.avatar = data.avatar ?? ''
      // 同步最新用户信息到全局 store，方便 Header、评论区使用
      userStore.setUser({
        ...(userStore.user || {}),
        username: data.username,
        nickname: data.nickname,
        email: data.email,
        avatar: data.avatar,
        role: data.role
      })
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

async function save() {
  saving.value = true
  saved.value = false
  try {
    const payload = {
      nickname: form.nickname || null,
      email: form.email || null,
      avatar: form.avatar || null
    }
    await userApi.updateProfile(payload)
    // 更新到全局 store
    userStore.setUser({
      ...(userStore.user || {}),
      nickname: form.nickname,
      email: form.email,
      avatar: form.avatar
    })
    saved.value = true
    setTimeout(() => {
      saved.value = false
    }, 2500)
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

async function onAvatarFileChange(e) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  const maxSize = 20 * 1024 * 1024 // 20MB
  if (file.size > maxSize) {
    alert('头像文件不能超过 20MB')
    e.target.value = ''
    return
  }
  uploadingAvatar.value = true
  try {
    const res = await userApi.uploadAvatar(file)
    const url = res?.data?.data
    if (url) {
      form.avatar = url
    }
  } catch (err) {
    console.error(err)
    alert(err?.response?.data?.message || '头像上传失败')
  } finally {
    uploadingAvatar.value = false
  }
}
</script>

<style scoped>
.user-profile-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem;
}
.card {
  background: var(--color-card, #fff);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  padding: 1.75rem 2rem;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.08);
}
.card h1 {
  margin-bottom: 0.5rem;
}
.subtitle {
  margin: 0 0 1.5rem;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
.loading {
  padding: 2rem 0;
  text-align: center;
  color: var(--color-text-muted);
}
.avatar-row {
  display: flex;
  gap: 1.25rem;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}
.avatar-preview {
  flex: 0 0 auto;
}
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 999px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(37, 99, 235, 0.08);
  color: var(--color-primary);
  font-size: 1.5rem;
  font-weight: 600;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar.placeholder {
  border: 1px dashed var(--color-border);
}
.avatar-input {
  flex: 1 1 220px;
}
.avatar-input label {
  display: block;
  margin-bottom: 0.35rem;
  font-weight: 500;
}
.hint {
  margin-top: 0.25rem;
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.field {
  margin-bottom: 1rem;
}
.field label {
  display: block;
  margin-bottom: 0.35rem;
  font-weight: 500;
}
.field input {
  width: 100%;
  padding: 0.55rem 0.75rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
}
.footer {
  margin-top: 1.25rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.footer button {
  padding: 0.5rem 1.5rem;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  background: var(--color-primary);
  color: #fff;
}
.footer button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.success {
  font-size: 0.85rem;
  color: #166534;
}
</style>

