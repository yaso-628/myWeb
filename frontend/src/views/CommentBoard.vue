<template>
  <div class="comment-board">
    <h1>留言板</h1>
    <div class="form-section animate-in">
      <h3>发表留言</h3>
      <form @submit.prevent="handleSubmit">
        <template v-if="userStore.isLoggedIn">
          <div class="user-info">
            <div class="avatar" v-if="userStore.user?.avatar">
              <img :src="toAssetUrl(userStore.user.avatar)" :alt="userStore.user.nickname || userStore.user.username" />
            </div>
            <div class="user-text">
              <div class="name">{{ userStore.user?.nickname || userStore.user?.username }}</div>
              <div class="hint">已登录，使用你的个人信息发表留言</div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="field">
            <NeInput v-model="form.nickname" type="text" placeholder="昵称" required />
          </div>
          <div class="field">
            <NeInput v-model="form.email" type="email" placeholder="邮箱" required />
          </div>
        </template>
        <div class="field">
          <NeTextarea v-model="form.content" placeholder="留言内容" required :rows="4" />
        </div>
        <UiButton class="submit-btn" :disabled="loading" color="primary" plain hoverable roundedLg v-ripple @click="handleSubmit">
          {{ loading ? '提交中...' : '提交' }}
        </UiButton>
        <span v-if="submitSuccess" class="success-tip">留言已提交，审核通过后会显示</span>
      </form>
    </div>
    <div class="list-section">
      <h3>留言列表</h3>
      <ul v-if="list.length" class="list">
        <li v-for="item in list" :key="item.id" class="item">
          <div class="avatar small">
            <template v-if="item.avatar">
              <img :src="toAssetUrl(item.avatar)" :alt="item.nickname || '访客'" />
            </template>
            <template v-else>
              <span>{{ (item.nickname || '访客').charAt(0).toUpperCase() }}</span>
            </template>
          </div>
          <div class="comment-body">
            <div class="head">
              <span class="nickname">{{ item.nickname || '访客' }}</span>
              <span class="time">{{ item.createTime }}</span>
            </div>
            <p class="content" :class="{ collapsed: !expandedMap[item.id] }">
              {{ item.content }}
            </p>
            <button
              v-if="item.content && item.content.length > 120"
              class="toggle"
              type="button"
              @click="toggleExpand(item.id)"
            >
              {{ expandedMap[item.id] ? '收起' : '展开更多' }}
            </button>
          </div>
        </li>
      </ul>
      <p v-else-if="!loading" class="empty">暂无留言</p>
      <p v-else class="loading">加载中...</p>
      <div v-if="total > pageSize" class="pagination">
        <NeButton :disabled="page <= 1" plain hoverable @click="page--; fetchList()">上一页</NeButton>
        <span>{{ page }} / {{ Math.ceil(total / pageSize) }}</span>
        <NeButton
          :disabled="page >= Math.ceil(total / pageSize)"
          plain
          hoverable
          @click="page++; fetchList()"
        >
          下一页
        </NeButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { commentApi } from '@/api'
import { useUserStore } from '@/store/user'
import { resolveAssetUrl } from '@/utils/asset'
import NeInput from '@/components/bits2d/NeInput.vue'
import NeTextarea from '@/components/bits2d/NeTextarea.vue'
import NeButton from '@/components/bits2d/NeButton.vue'
import { UiButton } from '@vuebits/ui'

const userStore = useUserStore()
const form = reactive({ nickname: '', email: '', content: '' })
const list = ref([])
const expandedMap = ref({})
const submitSuccess = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)

function toAssetUrl(url) {
  return resolveAssetUrl(url)
}

async function fetchList() {
  loading.value = true
  try {
    const res = await commentApi.guestbook(page.value, pageSize)
    list.value = res?.data?.data?.list || []
    total.value = res?.data?.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function toggleExpand(id) {
  expandedMap.value[id] = !expandedMap.value[id]
}

async function handleSubmit() {
  if (!form.content.trim()) {
    alert('请输入留言内容')
    return
  }

  const payload = {
    blogId: null,
    content: form.content.trim()
  }

  if (!userStore.isLoggedIn) {
    if (!form.nickname.trim()) {
      alert('请输入昵称')
      return
    }
    if (!form.email.trim()) {
      alert('请输入邮箱')
      return
    }
    payload.nickname = form.nickname.trim()
    payload.email = form.email.trim()
  }

  loading.value = true
  submitSuccess.value = false
  try {
    await commentApi.add(payload)
    form.content = ''
    if (!userStore.isLoggedIn) {
      form.nickname = ''
      form.email = ''
    }
    submitSuccess.value = true
    setTimeout(() => { submitSuccess.value = false }, 3000)
    fetchList()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '提交失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.comment-board {
  max-width: 700px;
  margin: 0 auto;
  padding: 2rem;
}
.comment-board h1 {
  margin-bottom: 1.5rem;
}
.form-section, .list-section {
  margin-bottom: 2rem;
}
.form-section h3, .list-section h3 {
  margin-bottom: 1rem;
  font-size: 1rem;
}
.field {
  margin-bottom: 0.75rem;
}
.field {
  /* NeInput/NeTextarea 基于 UiInput/UiTextarea：保持布局一致即可 */
  width: 100%;
}
.success-tip {
  margin-left: 0.75rem;
  font-size: 0.9rem;
  color: #166534;
}

.submit-btn {
  display: inline-flex;
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

.submit-btn:hover {
  background: rgba(37, 99, 235, 0.08) !important;
  box-shadow: none !important;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}
.user-text .name {
  font-weight: 500;
}
.user-text .hint {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.list {
  list-style: none;
}
.item {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 0;
  border-bottom: 1px solid var(--color-border);
}
.item:last-child {
  border-bottom: none;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: var(--color-primary);
  overflow: hidden;
}
.avatar.small {
  width: 32px;
  height: 32px;
  font-size: 0.9rem;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.comment-body {
  flex: 1;
}
.head {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}
.nickname {
  font-weight: 500;
}
.time {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.content {
  color: var(--color-text-muted);
  font-size: 0.95rem;
}
.content {
  white-space: pre-wrap;
  word-break: break-word;
}
.content.collapsed {
  max-height: 4.5em; /* 约 3 行 */
  overflow: hidden;
}
.toggle {
  margin-top: 0.25rem;
  padding: 0;
  border: none;
  background: none;
  color: var(--color-primary);
  font-size: 0.8rem;
  cursor: pointer;
  position: relative;
}
.toggle:hover {
  text-shadow: 0 0 14px rgba(37, 99, 235, 0.35);
}
.empty, .loading {
  padding: 2rem 0;
  color: var(--color-text-muted);
  text-align: center;
}
.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
  justify-content: center;
}
.pagination button {
  padding: 0.5rem 1rem;
}
.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .comment-board {
    padding: 1rem 0.25rem 1.2rem;
  }
  .comment-board h1 {
    margin-bottom: 1rem;
    font-size: 1.4rem;
  }
  .form-section, .list-section {
    margin-bottom: 1.4rem;
  }
  .form-section h3, .list-section h3 {
    font-size: 0.95rem;
    margin-bottom: 0.75rem;
  }
  .field input, .field textarea {
    padding: 0.5rem 0.6rem;
    font-size: 0.9rem;
  }
  button {
    width: 100%;
  }
  .submit-btn {
    width: 100%;
  }
  .success-tip {
    display: block;
    margin-left: 0;
    margin-top: 0.45rem;
    font-size: 0.82rem;
  }
  .user-info {
    gap: 0.55rem;
  }
  .user-text .name {
    font-size: 0.92rem;
  }
  .user-text .hint {
    font-size: 0.78rem;
  }
  .item {
    gap: 0.55rem;
    padding: 0.8rem 0;
  }
  .avatar.small {
    width: 28px;
    height: 28px;
    font-size: 0.82rem;
  }
  .head {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.12rem;
    margin-bottom: 0.25rem;
  }
  .nickname {
    font-size: 0.92rem;
  }
  .time {
    font-size: 0.72rem;
  }
  .content {
    font-size: 0.88rem;
  }
  .pagination {
    gap: 0.6rem;
    margin-top: 0.8rem;
  }
  .pagination button {
    width: auto;
    padding: 0.4rem 0.75rem;
  }
}
</style>
