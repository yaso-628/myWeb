<template>
  <div class="link-page">
    <h1>友链</h1>
    <div v-if="userStore.isLoggedIn" class="apply-card">
      <h2>申请友链</h2>
      <p class="tip">提交后需要管理员审核通过才会展示在列表中。</p>
      <form @submit.prevent="handleApply">
        <div class="field">
          <label>站点名称 <span class="required">*</span></label>
          <input v-model="form.name" type="text" required placeholder="你的站点名称" />
        </div>
        <div class="field">
          <label>站点链接 <span class="required">*</span></label>
          <input v-model="form.url" type="url" required placeholder="https://..." />
        </div>
        <div class="field">
          <label>描述</label>
          <input v-model="form.description" type="text" placeholder="一句话介绍你的站点" />
        </div>
        <div class="field">
          <label>Logo URL</label>
          <input v-model="form.logo" type="url" placeholder="可选，站点 Logo 图片地址" />
        </div>
        <div class="actions">
          <button type="submit" :disabled="submitting" v-ripple>
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
          <span v-if="submitSuccess" class="success">已提交，等待审核</span>
        </div>
      </form>
    </div>
    <ul v-if="list.length" class="link-list">
      <li v-for="(link, i) in list" :key="link.id" class="link-item" :style="{ animationDelay: `${i * 0.06}s` }">
        <a :href="link.url" target="_blank" rel="noopener noreferrer" class="link-card">
          <img v-if="link.logo" :src="link.logo" :alt="link.name" class="logo" />
          <div class="info">
            <span class="name">{{ link.name }}</span>
            <p v-if="link.description" class="desc">{{ link.description }}</p>
          </div>
        </a>
      </li>
    </ul>
    <p v-else-if="!loading" class="empty">暂无友链</p>
    <p v-else class="loading">加载中...</p>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { linkApi } from '@/api'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const list = ref([])
const loading = ref(false)

const form = reactive({
  name: '',
  url: '',
  description: '',
  logo: ''
})
const submitting = ref(false)
const submitSuccess = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await linkApi.list(1)
    list.value = res?.data?.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleApply() {
  if (!form.name?.trim() || !form.url?.trim()) {
    alert('请填写名称和链接')
    return
  }
  submitting.value = true
  submitSuccess.value = false
  try {
    const payload = {
      name: form.name.trim(),
      url: form.url.trim(),
      description: form.description?.trim() || null,
      logo: form.logo?.trim() || null
    }
    await linkApi.apply(payload)
    submitSuccess.value = true
    setTimeout(() => {
      submitSuccess.value = false
    }, 2500)
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.link-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.apply-card {
  margin-bottom: 2rem;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid var(--color-border);
  background: var(--color-card);
}
.apply-card h2 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
}
.tip {
  margin: 0 0 1rem;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
.field {
  margin-bottom: 0.75rem;
}
.field label {
  display: block;
  margin-bottom: 0.25rem;
  font-size: 0.9rem;
}
.field .required {
  color: #dc2626;
}
.field input {
  width: 100%;
  padding: 0.45rem 0.75rem;
  border-radius: 6px;
  border: 1px solid var(--color-border);
}
.actions {
  margin-top: 0.5rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.actions button {
  padding: 0.45rem 1.25rem;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  background: var(--color-primary);
  color: #fff;
}
.actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.success {
  font-size: 0.85rem;
  color: #166534;
}
.link-page h1 {
  margin-bottom: 1.5rem;
}
.link-list {
  list-style: none;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}
.link-item {
  margin: 0;
  animation: cardReveal 0.5s ease backwards;
}
@keyframes cardReveal {
  from {
    opacity: 0;
    transform: translateY(16px) perspective(400px) rotateX(-8deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) perspective(400px) rotateX(0);
  }
}
.link-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: var(--color-card);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  text-decoration: none;
  color: inherit;
  transition: transform 0.3s, box-shadow 0.3s;
  transform-style: preserve-3d;
}
.link-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 24px rgba(0,0,0,0.12);
  text-decoration: none;
}
.logo {
  width: 48px;
  height: 48px;
  object-fit: contain;
  border-radius: 4px;
}
.info {
  flex: 1;
  min-width: 0;
}
.name {
  font-weight: 600;
  display: block;
}
.desc {
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.empty, .loading {
  padding: 3rem 0;
  text-align: center;
  color: var(--color-text-muted);
}
</style>
