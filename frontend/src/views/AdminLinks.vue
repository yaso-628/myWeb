<template>
  <div class="admin-links">
    <Sidebar />
    <div class="main">
      <div class="header">
        <h1>友链管理</h1>
        <button class="btn primary" @click="openModal()">添加友链</button>
      </div>
      <div class="filters">
        <select v-model="statusFilter" @change="fetchList">
          <option :value="null">全部</option>
          <option :value="0">待审核</option>
          <option :value="1">已通过</option>
          <option :value="2">已拒绝</option>
        </select>
      </div>
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else-if="list.length" class="table">
        <thead>
          <tr>
            <th>名称</th>
            <th>链接</th>
            <th>描述</th>
            <th>状态</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="link in list" :key="link.id">
            <td>
              <span v-if="link.logo" class="logo-wrap">
                <img :src="link.logo" :alt="link.name" class="logo" />
              </span>
              {{ link.name }}
            </td>
            <td><a :href="link.url" target="_blank" rel="noopener">{{ link.url }}</a></td>
            <td class="desc">{{ link.description || '-' }}</td>
            <td>
              <span :class="['badge', statusClass(link.status)]">{{ statusText(link.status) }}</span>
            </td>
            <td>{{ link.sort ?? 0 }}</td>
            <td>
              <button class="link" @click="openModal(link)">编辑</button>
              <template v-if="link.status !== 1">
                <button class="link ok" @click="updateStatus(link.id, 1)">通过</button>
              </template>
              <button class="link danger" @click="confirmDelete(link)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty">暂无友链</p>
      <div v-if="total > pageSize" class="pagination">
        <button :disabled="page <= 1" @click="page--; fetchList()">上一页</button>
        <span>{{ page }} / {{ totalPages }}</span>
        <button :disabled="page >= totalPages" @click="page++; fetchList()">下一页</button>
      </div>

      <!-- 添加/编辑弹窗 -->
      <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <h3>{{ editingId ? '编辑友链' : '添加友链' }}</h3>
          <form @submit.prevent="saveLink">
            <div class="field">
              <label>名称 <span class="required">*</span></label>
              <input v-model="form.name" type="text" required placeholder="站点名称" />
            </div>
            <div class="field">
              <label>链接 <span class="required">*</span></label>
              <input v-model="form.url" type="url" required placeholder="https://..." />
            </div>
            <div class="field">
              <label>描述</label>
              <input v-model="form.description" type="text" placeholder="简短描述" />
            </div>
            <div class="field">
              <label>Logo URL</label>
              <input v-model="form.logo" type="url" placeholder="https://..." />
            </div>
            <div class="field">
              <label>排序</label>
              <input v-model.number="form.sort" type="number" placeholder="数字越小越靠前" />
            </div>
            <div class="field">
              <label>状态</label>
              <select v-model.number="form.status">
                <option :value="0">待审核</option>
                <option :value="1">已通过</option>
                <option :value="2">已拒绝</option>
              </select>
            </div>
            <div class="actions">
              <button type="submit" class="btn primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
              <button type="button" class="btn secondary" @click="closeModal">取消</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import { adminApi } from '@/api'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const statusFilter = ref(null)
const loading = ref(false)
const showModal = ref(false)
const editingId = ref(null)
const saving = ref(false)

const form = reactive({
  name: '',
  url: '',
  description: '',
  logo: '',
  sort: 0,
  status: 1
})

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

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
    const res = await adminApi.listLinks(page.value, pageSize, statusFilter.value)
    list.value = res?.data?.data?.list || []
    total.value = res?.data?.data?.total ?? 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openModal(link) {
  editingId.value = link?.id ?? null
  form.name = link?.name ?? ''
  form.url = link?.url ?? ''
  form.description = link?.description ?? ''
  form.logo = link?.logo ?? ''
  form.sort = link?.sort ?? 0
  form.status = link?.status ?? 1
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
}

async function saveLink() {
  if (!form.name?.trim() || !form.url?.trim()) {
    alert('请填写名称和链接')
    return
  }
  saving.value = true
  try {
    const payload = {
      name: form.name.trim(),
      url: form.url.trim(),
      description: form.description?.trim() || null,
      logo: form.logo?.trim() || null,
      sort: form.sort ?? 0,
      status: form.status
    }
    if (editingId.value) {
      await adminApi.updateLink(editingId.value, payload)
      alert('保存成功')
    } else {
      await adminApi.createLink(payload)
      alert('添加成功')
    }
    closeModal()
    fetchList()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

async function updateStatus(id, status) {
  try {
    await adminApi.updateLinkStatus(id, status)
    fetchList()
  } catch (e) {
    console.error(e)
    alert('操作失败')
  }
}

function confirmDelete(link) {
  if (!confirm(`确定删除友链「${link.name}」吗？`)) return
  doDelete(link.id)
}

async function doDelete(id) {
  try {
    await adminApi.deleteLink(id)
    fetchList()
  } catch (e) {
    console.error(e)
    alert('删除失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-links {
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
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.btn.primary:hover:not(:disabled) {
  opacity: 0.9;
}
.btn.secondary {
  padding: 0.5rem 1rem;
  background: #e5e7eb;
  color: #374151;
  border: none;
  border-radius: 6px;
  cursor: pointer;
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
  font-size: 0.9rem;
}
.table .desc {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.logo-wrap {
  display: inline-flex;
  align-items: center;
  margin-right: 0.5rem;
}
.logo {
  width: 24px;
  height: 24px;
  object-fit: contain;
  border-radius: 4px;
}
.badge {
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
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
.link {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-primary);
  padding: 0 0.5rem;
  font-size: 0.9rem;
}
.link.ok {
  color: #166534;
}
.link.danger {
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

/* 弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  max-width: 480px;
  width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.modal h3 {
  margin: 0 0 1.5rem;
}
.field {
  margin-bottom: 1rem;
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
.field select {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
}
.actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}
</style>
