<template>
  <div class="blog-list">
    <div class="header">
      <h1>博客文章</h1>
      <div class="actions">
        <UiButton
          v-if="userStore.isAdmin"
          to="/admin/blogs/new"
          color="primary"
          plain
          hoverable
          roundedLg
          v-ripple
          class="admin-publish-btn"
        >
          后台发表
        </UiButton>
        <NeButton
          v-else-if="userStore.isLoggedIn"
          to="/blog/submit"
          :disabled="false"
        >
          我要投稿
        </NeButton>
      </div>
    </div>
    <div class="filters">
      <NeInput
        v-model="categoryFilter"
        type="text"
        placeholder="按分类筛选"
        @update:modelValue="debouncedFetch"
      />
    </div>
    <ul v-if="list.length" class="list">
      <li v-for="(blog, i) in list" :key="blog.id" class="item" :style="{ animationDelay: `${i * 0.06}s` }">
        <router-link :to="`/blog/${blog.id}`" class="title">{{ blog.title }}</router-link>
        <p v-if="blog.summary" class="summary">{{ blog.summary }}</p>
        <div class="meta">
          <span>{{ blog.createTime }}</span>
          <span v-if="blog.category">{{ blog.category }}</span>
          <span>阅读 {{ blog.viewCount || 0 }}</span>
        </div>
      </li>
    </ul>
    <p v-else-if="!loading" class="empty">暂无文章</p>
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { blogApi } from '@/api'
import { useUserStore } from '@/store/user'
import NeInput from '@/components/bits2d/NeInput.vue'
import NeButton from '@/components/bits2d/NeButton.vue'
import { UiButton } from '@vuebits/ui'

const userStore = useUserStore()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const categoryFilter = ref('')
const loading = ref(false)
let debounceTimer = null

async function fetchList() {
  loading.value = true
  try {
    const res = await blogApi.list(page.value, pageSize, 1, categoryFilter.value || undefined)
    list.value = res?.data?.data?.list || []
    total.value = res?.data?.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function debouncedFetch() {
  clearTimeout(debounceTimer)
  page.value = 1
  debounceTimer = setTimeout(fetchList, 300)
}

onMounted(fetchList)
</script>

<style scoped>
.blog-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.header h1 {
  margin: 0;
}
.admin-publish-btn {
  /* 避免“黑边包裹 + 渐变外壳”的观感 */
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

.admin-publish-btn :deep(.ui-button) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

.admin-publish-btn :deep(.ui-button__container) {
  min-height: 36px;
  padding: 0 1rem;
  border-radius: 999px;
}

.admin-publish-btn:hover {
  background: rgba(37, 99, 235, 0.08) !important;
  box-shadow: none !important;
}
.filters {
  margin-bottom: 1rem;
  max-width: 260px;
  width: 100%;
}
.filters {
  display: flex;
  align-items: center;
}
.list {
  list-style: none;
  padding: 0;
}
.item {
  padding: 1.5rem;
  margin-bottom: 0.5rem;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  background: var(--color-card);
  animation: itemReveal 0.4s ease backwards;
  transition: transform 0.2s, box-shadow 0.2s;
}
.item:hover {
  transform: translateX(6px);
  box-shadow: 0 4px 16px var(--color-glow);
}
@keyframes itemReveal {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
.title {
  font-size: 1.25rem;
  font-weight: 600;
}
.summary {
  margin-top: 0.5rem;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}
.meta {
  margin-top: 0.5rem;
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.meta span + span {
  margin-left: 1rem;
}
.empty, .loading {
  padding: 3rem 0;
  color: var(--color-text-muted);
  text-align: center;
}
.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  justify-content: center;
}
.pagination :deep(.ui-button) {
  white-space: nowrap;
}

@media (max-width: 768px) {
  .blog-list {
    padding: 1rem 0.25rem 1.25rem;
  }
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.6rem;
  }
  .header h1 {
    font-size: 1.4rem;
  }
  .btn-publish {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
  .filters input {
    width: 100%;
  }
  .item {
    padding: 1rem;
    border-radius: 8px;
  }
  .item:hover {
    transform: none;
    box-shadow: none;
  }
  .title {
    font-size: 1.08rem;
    line-height: 1.45;
    word-break: break-word;
  }
  .summary {
    font-size: 0.85rem;
  }
  .meta {
    display: flex;
    flex-wrap: wrap;
    gap: 0.3rem 0.7rem;
    font-size: 0.75rem;
  }
  .meta span + span {
    margin-left: 0;
  }
  .pagination {
    gap: 0.6rem;
    margin-top: 1.2rem;
  }
  .pagination button {
    padding: 0.4rem 0.75rem;
  }
}
</style>
