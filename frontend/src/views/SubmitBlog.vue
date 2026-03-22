<template>
  <div class="submit-blog">
    <div class="card">
      <h1>投稿文章</h1>
      <p class="subtitle">提交后会进入待审核列表，管理员通过后才会在首页展示。</p>
      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label>标题 <span class="required">*</span></label>
          <input v-model="form.title" type="text" required placeholder="请输入文章标题" />
        </div>
        <div class="field">
          <label>摘要</label>
          <textarea v-model="form.summary" rows="2" placeholder="列表中展示的一小段文字"></textarea>
        </div>
        <div class="field">
          <label>正文</label>
          <textarea v-model="form.content" rows="10" placeholder="文章正文，可填写 Markdown/HTML"></textarea>
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
        <div class="footer">
          <button type="submit" :disabled="submitting" v-ripple>
            {{ submitting ? '提交中...' : '提交审核' }}
          </button>
          <span v-if="submitSuccess" class="success">投稿成功，待管理员审核</span>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { blogApi } from '@/api'

const router = useRouter()
const submitting = ref(false)
const submitSuccess = ref(false)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  cover: '',
  category: '',
  tags: ''
})

async function handleSubmit() {
  if (!form.title?.trim()) {
    alert('请输入标题')
    return
  }
  submitting.value = true
  submitSuccess.value = false
  try {
    const payload = {
      title: form.title.trim(),
      summary: form.summary?.trim() || null,
      content: form.content?.trim() || null,
      cover: form.cover?.trim() || null,
      category: form.category?.trim() || null,
      tags: form.tags?.trim() || null
    }
    await blogApi.submit(payload)
    submitSuccess.value = true
    setTimeout(() => {
      router.push('/blog')
    }, 1500)
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '投稿失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.submit-blog {
  max-width: 800px;
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
.field {
  margin-bottom: 1.2rem;
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
.field textarea {
  width: 100%;
  padding: 0.55rem 0.75rem;
  border-radius: 8px;
  border: 1px solid var(--color-border);
}
.field textarea {
  resize: vertical;
}
.row {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}
.field.half {
  flex: 1 1 220px;
}
.footer {
  margin-top: 1rem;
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

