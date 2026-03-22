<template>
  <div class="admin-blog-edit">
    <Sidebar />
    <div class="main">
      <h1>{{ isNew ? '发表文章' : '编辑文章' }}</h1>
      <form class="form" @submit.prevent="save">
        <div class="field">
          <label>标题 <span class="required">*</span></label>
          <input v-model="form.title" type="text" placeholder="文章标题" required />
        </div>
        <div class="field">
          <label>摘要</label>
          <textarea v-model="form.summary" rows="2" placeholder="简短摘要，用于列表展示"></textarea>
        </div>
        <div class="field">
          <label>正文</label>
          <textarea v-model="form.content" rows="12" placeholder="文章正文，支持 HTML"></textarea>
        </div>
        <div class="row">
          <div class="field half">
            <label>封面图 URL</label>
            <input v-model="form.cover" type="url" placeholder="https://..." />
          </div>
          <div class="field half">
            <label>分类</label>
            <input v-model="form.category" type="text" placeholder="如：技术、生活" />
          </div>
        </div>
        <div class="field">
          <label>标签</label>
          <input v-model="form.tags" type="text" placeholder="逗号分隔，如：Vue,Spring" />
        </div>
        <div class="field">
          <label>状态</label>
          <select v-model.number="form.status">
            <option :value="0">草稿</option>
            <option :value="1">已发布</option>
          </select>
        </div>
        <div class="actions">
          <button type="submit" class="btn primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          <router-link to="/admin/blogs" class="btn secondary">取消</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import { adminApi } from '@/api'

const route = useRoute()
const router = useRouter()
const saving = ref(false)

const isNew = computed(() => !route.params.id || route.params.id === 'new')

const form = reactive({
  title: '',
  summary: '',
  content: '',
  cover: '',
  category: '',
  tags: '',
  status: 0
})

onMounted(async () => {
  if (!isNew.value) {
    try {
      const res = await adminApi.getBlog(route.params.id)
      const b = res?.data?.data
      if (b) {
        form.title = b.title ?? ''
        form.summary = b.summary ?? ''
        form.content = b.content ?? ''
        form.cover = b.cover ?? ''
        form.category = b.category ?? ''
        form.tags = b.tags ?? ''
        form.status = b.status ?? 0
      }
    } catch (e) {
      console.error(e)
      alert('加载失败')
    }
  }
})

async function save() {
  if (!form.title?.trim()) {
    alert('请输入标题')
    return
  }
  saving.value = true
  try {
    const payload = {
      title: form.title.trim(),
      summary: form.summary?.trim() || null,
      content: form.content?.trim() || null,
      cover: form.cover?.trim() || null,
      category: form.category?.trim() || null,
      tags: form.tags?.trim() || null,
      status: form.status
    }
    if (isNew.value) {
      const res = await adminApi.createBlog(payload)
      router.push(`/admin/blogs/edit/${res?.data?.data}`)
    } else {
      await adminApi.updateBlog(route.params.id, payload)
      alert('保存成功')
    }
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.admin-blog-edit {
  display: flex;
  min-height: 100vh;
}
.main {
  flex: 1;
  padding: 2rem;
  margin-left: 200px;
  max-width: 720px;
}
.main h1 {
  margin-bottom: 1.5rem;
}
.form {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
}
.field {
  margin-bottom: 1.25rem;
}
.field label {
  display: block;
  margin-bottom: 0.35rem;
  font-weight: 500;
}
.field .required {
  color: #dc2626;
}
.field input,
.field textarea,
.field select {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
}
.field textarea {
  resize: vertical;
}
.row {
  display: flex;
  gap: 1rem;
}
.field.half {
  flex: 1;
}
.actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}
.btn {
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
  border: none;
  font-size: 1rem;
}
.btn.primary {
  background: var(--color-primary);
  color: #fff;
}
.btn.primary:hover:not(:disabled) {
  opacity: 0.9;
}
.btn.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn.secondary {
  background: #e5e7eb;
  color: #374151;
}
.btn.secondary:hover {
  background: #d1d5db;
}
</style>
