<template>
  <div class="admin-comments">
    <Sidebar />
    <div class="main">
      <h1>评论 / 留言管理</h1>
      <div class="filters">
        <select v-model="typeFilter" @change="fetchList">
          <option value="all">全部（文章+留言）</option>
          <option value="guestbook">仅留言板</option>
        </select>
        <select v-model="statusFilter" @change="fetchList">
          <option :value="null">全部状态</option>
          <option :value="0">待审核</option>
          <option :value="1">已通过</option>
          <option :value="2">已拒绝</option>
        </select>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="list.length" class="list">
        <div v-for="item in list" :key="item.id" class="item">
          <div class="head">
            <span class="nickname">{{ item.nickname || '匿名' }}</span>
            <span class="time">{{ formatDate(item.createTime) }}</span>
            <span :class="['badge', statusClass(item.status)]">{{ statusText(item.status) }}</span>
            <span v-if="item.blogId" class="source">文章ID: {{ item.blogId }}</span>
            <span v-else class="source guestbook">留言板</span>
          </div>
          <p class="content">{{ item.content }}</p>
          <div class="actions">
            <template v-if="item.status !== 1">
              <button class="btn-sm ok" @click="updateStatus(item.id, 1)">通过</button>
            </template>
            <template v-if="item.status !== 2">
              <button class="btn-sm reject" @click="updateStatus(item.id, 2)">拒绝</button>
            </template>
            <button class="btn-sm danger" @click="confirmDelete(item)">删除</button>
          </div>
        </div>
      </div>
      <p v-else class="empty">暂无评论/留言</p>
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
import Sidebar from '@/components/Sidebar.vue'
import { adminApi } from '@/api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const typeFilter = ref('all')
const statusFilter = ref(null)
const loading = ref(false)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function formatDate(d) {
  if (!d) return '-'
  const dt = new Date(d)
  return dt.toLocaleDateString('zh-CN') + ' ' + dt.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function statusText(s) {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return map[s] ?? '未知'
}

function statusClass(s) {
  const map = { 0: 'pending', 1: 'ok', 2: 'reject' }
  return map[s] ?? ''
}

async function fetchList() {
  loading.value = true
  try {
    const guestbookOnly = typeFilter.value === 'guestbook'
    const res = await adminApi.listComments(page.value, pageSize, undefined, guestbookOnly, statusFilter.value)
    list.value = res?.data?.data?.list || []
    total.value = res?.data?.data?.total ?? 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function updateStatus(id, status) {
  try {
    await adminApi.updateCommentStatus(id, status)
    fetchList()
  } catch (e) {
    console.error(e)
    alert('操作失败')
  }
}

function confirmDelete(item) {
  if (!confirm('确定删除这条' + (item.blogId ? '评论' : '留言') + '吗？')) return
  doDelete(item.id)
}

async function doDelete(id) {
  try {
    await adminApi.deleteComment(id)
    fetchList()
  } catch (e) {
    console.error(e)
    alert('删除失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-comments {
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
.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.filters select {
  padding: 0.5rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
}
.list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.item {
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 1rem;
}
.head {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}
.nickname {
  font-weight: 600;
}
.time {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.source {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}
.source.guestbook {
  color: var(--color-primary);
}
.badge {
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}
.badge.pending {
  background: #fef3c7;
  color: #92400e;
}
.badge.ok {
  background: #dcfce7;
  color: #166534;
}
.badge.reject {
  background: #fee2e2;
  color: #991b1b;
}
.content {
  margin: 0.5rem 0;
  line-height: 1.6;
  color: var(--color-text);
  white-space: pre-wrap;
  word-break: break-word;
}
.actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.75rem;
}
.btn-sm {
  padding: 0.25rem 0.6rem;
  font-size: 0.85rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #e5e7eb;
  color: #374151;
}
.btn-sm:hover {
  opacity: 0.9;
}
.btn-sm.ok {
  background: #dcfce7;
  color: #166534;
}
.btn-sm.reject {
  background: #fee2e2;
  color: #991b1b;
}
.btn-sm.danger {
  background: #fee2e2;
  color: #dc2626;
}
.empty, .loading {
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
