<template>
  <div class="admin-blogs">
    <Sidebar />
    <div class="main">
      <div class="header">
        <h1>文章管理</h1>
        <router-link to="/admin/blogs/new" class="btn primary">发表文章</router-link>
      </div>
      <div class="filters">
        <select v-model="statusFilter" @change="fetchList">
          <option :value="null">全部</option>
          <option :value="0">草稿</option>
          <option :value="1">已发布</option>
        </select>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else-if="list.length" class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>分类</th>
            <th>状态</th>
            <th>阅读</th>
            <th>时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="blog in list" :key="blog.id">
            <td>{{ blog.id }}</td>
            <td>
              <router-link :to="`/blog/${blog.id}`" target="_blank">{{ blog.title || '-' }}</router-link>
            </td>
            <td>{{ blog.category || '-' }}</td>
            <td>
              <span :class="['badge', blog.status === 1 ? 'published' : 'draft']">
                {{ blog.status === 1 ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>{{ blog.viewCount || 0 }}</td>
            <td>{{ formatDate(blog.updateTime || blog.createTime) }}</td>
            <td>
              <router-link :to="`/admin/blogs/edit/${blog.id}`" class="link">编辑</router-link>
              <button class="link danger" @click="confirmDelete(blog)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">暂无文章</p>
      <div v-if="total > pageSize" class="pagination">
        <button :disabled="page <= 1" @click="page--; fetchList()">上一页</button>
        <span>{{ page }} / {{ totalPages }}</span>
        <button :disabled="page >= totalPages" @click="page++; fetchList()">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import { adminApi } from '@/api'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const loading = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

function formatDate(d) {
  if (!d) return '-'
  const dt = new Date(d)
  return dt.toLocaleDateString('zh-CN') + ' ' + dt.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function fetchList() {
  loading.value = true
  try {
    const res = await adminApi.listBlogs(page.value, pageSize.value, statusFilter.value)
    list.value = res?.data?.data?.list || []
    total.value = res?.data?.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function confirmDelete(blog) {
  if (!confirm(`确定删除「${blog.title}」吗？`)) return
  deleteBlog(blog.id)
}

async function deleteBlog(id) {
  try {
    await adminApi.deleteBlog(id)
    fetchList()
  } catch (e) {
    console.error(e)
    alert('删除失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-blogs {
  display: flex;
  min-height: 100vh;
}
.main {
  flex: 1;
  padding: 2rem;
  margin-left: 200px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.header h1 {
  margin: 0;
}
.btn.primary {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: #fff;
  border-radius: 6px;
  text-decoration: none;
}
.btn.primary:hover {
  opacity: 0.9;
}
.filters {
  margin-bottom: 1rem;
}
.filters select {
  padding: 0.5rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
}
.table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--color-border);
}
.table th,
.table td {
  padding: 0.75rem 1rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}
.table th {
  background: var(--color-bg-soft, #f9fafb);
  font-weight: 600;
}
.table td a {
  color: var(--color-primary);
}
.link {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-primary);
  padding: 0 0.5rem;
}
.link.danger {
  color: #dc2626;
}
.badge {
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}
.badge.published {
  background: #dcfce7;
  color: #166534;
}
.badge.draft {
  background: #fef3c7;
  color: #92400e;
}
.loading, .empty {
  padding: 2rem;
  color: var(--color-text-muted);
}
.pagination {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}
.pagination button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}
.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
