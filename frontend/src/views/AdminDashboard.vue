<template>
  <div class="admin-dashboard">
    <Sidebar />
    <div class="main">
      <h1>后台管理</h1>
      <div v-if="stats" class="stats">
        <div class="stat-card">
          <span class="num">{{ stats.blogCount }}</span>
          <span class="label">文章</span>
        </div>
        <div class="stat-card">
          <span class="num">{{ stats.commentCount }}</span>
          <span class="label">评论/留言</span>
        </div>
        <div class="stat-card">
          <span class="num">{{ stats.linkCount }}</span>
          <span class="label">友链</span>
        </div>
      </div>
      <p v-else-if="!loading" class="empty">加载失败，请先登录</p>
      <p v-else class="loading">加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import { adminApi } from '@/api'

const stats = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await adminApi.stats()
    stats.value = res?.data?.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
}
.main {
  flex: 1;
  padding: 2rem;
  margin-left: 200px;
}
.main h1 {
  margin-bottom: 1.5rem;
}
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1rem;
}
.stat-card {
  padding: 1.5rem;
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--color-border);
  text-align: center;
}
.num {
  display: block;
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-primary);
}
.label {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
.empty, .loading {
  padding: 2rem 0;
  color: var(--color-text-muted);
}
</style>
