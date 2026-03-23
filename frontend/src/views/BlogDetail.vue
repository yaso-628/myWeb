<template>
  <div class="blog-detail">
    <div v-if="blog" class="content">
      <NeTabs v-model="activeTab" :items="tabs">
        <template #article>
          <h1>{{ blog.title }}</h1>
          <div class="meta">
            <span>{{ formatDate(blog.publishTime || blog.createTime) }}</span>
            <span v-if="blog.category">{{ blog.category }}</span>
            <span v-if="blog.tags" class="tags">{{ blog.tags }}</span>
            <span>阅读 {{ blog.viewCount || 0 }}</span>
          </div>
          <img v-if="blog.cover" :src="blog.cover" alt="封面" class="cover" />
          <div class="body" v-html="blog.content"></div>
        </template>

        <template #comments>
          <!-- 评论区 -->
          <section class="comments">
            <h2>评论</h2>

            <div class="comment-form animate-in">
              <NeCard>
                <template v-if="userStore.isLoggedIn">
                  <div class="user-info">
                    <div class="avatar" v-if="userStore.user?.avatar">
                      <img :src="toAssetUrl(userStore.user.avatar)" :alt="userStore.user.nickname || userStore.user.username" />
                    </div>
                    <div class="user-text">
                      <div class="name">{{ userStore.user?.nickname || userStore.user?.username }}</div>
                      <div class="hint">已登录，使用你的个人信息发表评论</div>
                    </div>
                  </div>
                </template>

                <template v-else>
                  <div class="guest-fields">
                    <NeInput v-model="guestNickname" type="text" placeholder="昵称（必填）" required />
                    <NeInput v-model="guestEmail" type="email" placeholder="邮箱（必填，仅用于联系）" required />
                  </div>
                </template>

                <NeTextarea
                  v-model="commentContent"
                  :rows="4"
                  placeholder="写点什么吧..."
                  required
                />

                <div class="actions">
                  <button
                    type="button"
                    class="btn-primary-solid"
                    :disabled="submitting"
                    v-ripple
                    @click="submitComment"
                  >
                    {{ submitting ? '提交中...' : '发表评论' }}
                  </button>
                  <span v-if="submitSuccess" class="success-tip">评论已提交，审核通过后会显示</span>
                </div>
              </NeCard>
            </div>

            <div class="comment-list" v-if="comments.length">
              <NeCard>
                <h3>已有评论</h3>
                <ul>
                  <li v-for="item in comments" :key="item.id" class="comment-item">
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
                        <span class="time">{{ formatFullDate(item.createTime) }}</span>
                      </div>
                      <p class="content" :class="{ collapsed: !expandedComments[item.id] }">
                        {{ item.content }}
                      </p>
                      <button
                        v-if="item.content && item.content.length > 120"
                        class="toggle"
                        type="button"
                        @click="toggleCommentExpand(item.id)"
                      >
                        {{ expandedComments[item.id] ? '收起' : '展开更多' }}
                      </button>
                    </div>
                  </li>
                </ul>
              </NeCard>
            </div>

            <p v-else-if="!commentLoading" class="empty-comments">暂无评论，快来抢沙发～</p>
            <p v-else class="loading">评论加载中...</p>
          </section>
        </template>
      </NeTabs>
    </div>
    <p v-else-if="!loading" class="empty">文章不存在</p>
    <p v-else class="loading">加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { blogApi, commentApi } from '@/api'
import { useUserStore } from '@/store/user'
import { resolveAssetUrl } from '@/utils/asset'
import NeTabs from '@/components/bits2d/NeTabs.vue'
import NeCard from '@/components/bits2d/NeCard.vue'
import NeInput from '@/components/bits2d/NeInput.vue'
import NeTextarea from '@/components/bits2d/NeTextarea.vue'

const route = useRoute()
const userStore = useUserStore()
const blog = ref(null)
const loading = ref(false)

const comments = ref([])
const commentLoading = ref(false)
const commentContent = ref('')
const submitting = ref(false)
const submitSuccess = ref(false)
const guestNickname = ref('')
const guestEmail = ref('')
const expandedComments = ref({})
const activeTab = ref(0)
const tabs = [
  { key: 'article', text: '文章内容' },
  { key: 'comments', text: '评论区' }
]

function toAssetUrl(url) {
  return resolveAssetUrl(url)
}

function formatDate(d) {
  if (!d) return ''
  const dt = new Date(d)
  return dt.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

function formatFullDate(d) {
  if (!d) return ''
  const dt = new Date(d)
  return dt.toLocaleDateString('zh-CN') + ' ' + dt.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function fetchBlogAndComments() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await blogApi.getById(id)
    blog.value = res?.data?.data
    if (blog.value?.id) {
      await fetchComments()
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function fetchComments() {
  if (!blog.value?.id) return
  commentLoading.value = true
  try {
    const res = await commentApi.listByBlogId(blog.value.id, 1)
    comments.value = res?.data?.data || []
  } catch (e) {
    console.error(e)
  } finally {
    commentLoading.value = false
  }
}

function toggleCommentExpand(id) {
  expandedComments.value[id] = !expandedComments.value[id]
}

async function submitComment() {
  if (!blog.value?.id) {
    return
  }
  if (!commentContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  const payload = {
    blogId: blog.value.id,
    content: commentContent.value.trim()
  }

  if (!userStore.isLoggedIn) {
    if (!guestNickname.value.trim()) {
      alert('请输入昵称')
      return
    }
    if (!guestEmail.value.trim()) {
      alert('请输入邮箱')
      return
    }
    payload.nickname = guestNickname.value.trim()
    payload.email = guestEmail.value.trim()
  }

  submitting.value = true
  submitSuccess.value = false
  try {
    await commentApi.add(payload)
    commentContent.value = ''
    if (!userStore.isLoggedIn) {
      guestNickname.value = ''
      guestEmail.value = ''
    }
    submitSuccess.value = true
    setTimeout(() => {
      submitSuccess.value = false
    }, 3000)
    await fetchComments()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(fetchBlogAndComments)
</script>

<style scoped>
.blog-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.content h1 {
  margin-bottom: 1rem;
}
.meta {
  font-size: 0.9rem;
  color: var(--color-text-muted);
  margin-bottom: 2rem;
}
.meta span + span {
  margin-left: 1rem;
}
.meta .tags {
  color: var(--color-primary);
}
.cover {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}
.body {
  line-height: 1.8;
}
.body :deep(img) {
  max-width: 100%;
}
.empty, .loading {
  padding: 3rem 0;
  text-align: center;
  color: var(--color-text-muted);
}

.comments {
  margin-top: 3rem;
}
.comments h2 {
  margin-bottom: 1rem;
  font-size: 1.25rem;
}
.card {
  background: var(--color-card, #fff);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  padding: 1.25rem 1.5rem;
}
.comment-form {
  margin-bottom: 1.5rem;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}
.user-text .name {
  font-weight: 600;
}
.user-text .hint {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.guest-fields {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}
.actions {
  margin-top: 0.75rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.actions :deep(.ui-button) {
  white-space: nowrap;
}
.success-tip {
  font-size: 0.85rem;
  color: #166534;
}
.comment-list h3 {
  margin-bottom: 0.75rem;
  font-size: 1rem;
}
.comment-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.comment-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--color-border);
}
.comment-item:last-child {
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
.comment-body .head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
}
.comment-body .nickname {
  font-weight: 600;
}
.comment-body .time {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.comment-body .content {
  font-size: 0.95rem;
  color: var(--color-text);
}
.comment-body .content {
  white-space: pre-wrap;
  word-break: break-word;
}
.comment-body .content.collapsed {
  max-height: 4.5em; /* 约 3 行 */
  overflow: hidden;
}
.comment-body .toggle {
  margin-top: 0.25rem;
  padding: 0;
  border: none;
  background: none;
  color: var(--color-primary);
  font-size: 0.8rem;
  cursor: pointer;
}
.empty-comments {
  padding: 1.5rem 0;
  text-align: center;
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .blog-detail {
    padding: 1rem 0.25rem 1.2rem;
  }
  .content h1 {
    font-size: 1.5rem;
    line-height: 1.35;
    word-break: break-word;
  }
  .meta {
    display: flex;
    flex-wrap: wrap;
    gap: 0.25rem 0.6rem;
    margin-bottom: 1.2rem;
    font-size: 0.78rem;
  }
  .meta span + span {
    margin-left: 0;
  }
  .cover {
    max-height: 220px;
    border-radius: 6px;
  }
  .body {
    font-size: 0.95rem;
    line-height: 1.75;
  }
  .comments {
    margin-top: 1.75rem;
  }
  .comments h2 {
    font-size: 1.1rem;
  }
  .card {
    padding: 0.9rem 0.85rem;
    border-radius: 10px;
  }
  .guest-fields {
    flex-direction: column;
    gap: 0.5rem;
  }
  .actions {
    align-items: flex-start;
    flex-direction: column;
    gap: 0.45rem;
  }
  .actions button {
    width: 100%;
  }
  .comment-item {
    gap: 0.55rem;
  }
  .avatar.small {
    width: 28px;
    height: 28px;
    font-size: 0.82rem;
  }
  .comment-body .head {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.12rem;
  }
  .comment-body .nickname {
    font-size: 0.92rem;
  }
  .comment-body .time {
    font-size: 0.72rem;
  }
  .comment-body .content {
    font-size: 0.88rem;
  }
}
</style>
