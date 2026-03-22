<template>
  <div class="admin-profile">
    <Sidebar />
    <div class="main">
      <h1>个人简介 / 站点配置</h1>
      <form v-if="!loading" class="form" @submit.prevent="save">
        <h3 class="section-title">站点配置</h3>
        <div class="field">
          <label>背景图</label>
          <input
            v-model="form.bgImage"
            type="text"
            placeholder="可填写网络地址或 /uploads/bg/xxx.jpg 等本地路径，留空则使用默认渐变"
          />
          <div class="upload-row">
            <input ref="bgImageFileRef" class="file-hidden" type="file" accept="image/*" @change="handleUploadBgImage" />
            <button type="button" class="btn secondary" :disabled="uploadingBgImage" @click="pickBgImageFile">
              {{ uploadingBgImage ? '上传中...' : '选择图片并上传' }}
            </button>
          </div>
        </div>
        <h3 class="section-title">音乐列表</h3>
        <p class="field-hint">访客可在页面右下角音乐播放器中切换和暂停歌曲</p>
        <div class="field add-music">
          <label>添加歌曲</label>
          <div class="add-music-row">
            <input v-model="newMusic.title" type="text" placeholder="歌名" />
            <input v-model="newMusic.artist" type="text" placeholder="艺术家" />
          </div>
          <div class="upload-row">
            <input v-model="newMusic.url" type="text" placeholder="音乐链接或上传后自动填充" />
            <input ref="musicFileRef" class="file-hidden" type="file" accept="audio/*" @change="handleUploadMusic" />
            <button type="button" class="btn secondary" :disabled="uploadingMusic" @click="pickMusicFile">
              {{ uploadingMusic ? '上传中...' : '选择并上传' }}
            </button>
          </div>
          <button type="button" class="btn primary add-btn" :disabled="!newMusic.title || !newMusic.url || addingMusic" @click="addMusic">
            {{ addingMusic ? '添加中...' : '添加到列表' }}
          </button>
        </div>
        <div class="music-list" v-if="musicList.length">
          <div class="music-list-header">已添加的歌曲</div>
          <ul>
            <li v-for="m in musicList" :key="m.id" class="music-item">
              <span class="music-info">{{ m.title }} - {{ m.artist || '未知' }}</span>
              <button type="button" class="btn-delete" @click="deleteMusic(m.id)">删除</button>
            </li>
          </ul>
        </div>
        <div class="field">
          <label>站点名称</label>
          <input v-model="form.siteName" type="text" placeholder="网站标题" />
        </div>
        <div class="field">
          <label>站点描述</label>
          <input v-model="form.siteDesc" type="text" placeholder="副标题或简短描述" />
        </div>
        <h3 class="section-title">个人简介</h3>
        <div class="field">
          <label>头像 URL</label>
          <input v-model="form.avatar" type="url" placeholder="https://..." />
        </div>
        <div class="field">
          <label>个人简介</label>
          <textarea v-model="form.bio" rows="6" placeholder="关于我、个人介绍..."></textarea>
        </div>
        <div class="field">
          <label>联系方式</label>
          <input v-model="form.contact" type="text" placeholder="邮箱、微信、GitHub 等" />
        </div>
        <div class="actions">
          <button type="submit" class="btn primary" :disabled="saving">保存</button>
          <span v-if="saved" class="tip">已保存</span>
        </div>
      </form>
      <p v-else class="loading">加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import { profileApi, adminApi } from '@/api'

const loading = ref(false)
const saving = ref(false)
const saved = ref(false)
const uploadingBgImage = ref(false)
const bgImageFileRef = ref(null)

const form = reactive({
  siteName: '',
  siteDesc: '',
  bgImage: '',
  avatar: '',
  bio: '',
  contact: ''
})

const musicList = ref([])
const newMusic = reactive({ title: '', artist: '', url: '' })
const addingMusic = ref(false)
const uploadingMusic = ref(false)
const musicFileRef = ref(null)

function pickBgImageFile() {
  bgImageFileRef.value?.click?.()
}

function pickMusicFile() {
  musicFileRef.value?.click?.()
}

async function load() {
  loading.value = true
  try {
    const [infoRes, siteNameRes, siteDescRes, bgImageRes] = await Promise.all([
      profileApi.getInfo(),
      profileApi.getByKey('site_name'),
      profileApi.getByKey('site_desc'),
      profileApi.getByKey('bg_image')
    ])
    const data = infoRes?.data?.data
    if (data) {
      form.avatar = data.avatar ?? ''
      form.bio = data.bio ?? ''
      form.contact = data.contact ?? ''
    }
    form.siteName = siteNameRes?.data?.data?.value ?? ''
    form.siteDesc = siteDescRes?.data?.data?.value ?? ''
    form.bgImage = bgImageRes?.data?.data?.value ?? ''
    musicList.value = (await adminApi.listMusic())?.data?.data ?? []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function addMusic() {
  if (!newMusic.title?.trim() || !newMusic.url?.trim()) return
  addingMusic.value = true
  try {
    await adminApi.addMusic({
      title: newMusic.title.trim(),
      artist: (newMusic.artist || '').trim(),
      url: newMusic.url.trim()
    })
    newMusic.title = ''
    newMusic.artist = ''
    newMusic.url = ''
    musicList.value = (await adminApi.listMusic())?.data?.data ?? []
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '添加失败')
  } finally {
    addingMusic.value = false
  }
}

async function deleteMusic(id) {
  if (!confirm('确定删除这首歌曲？')) return
  try {
    await adminApi.deleteMusic(id)
    musicList.value = (await adminApi.listMusic())?.data?.data ?? []
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '删除失败')
  }
}

async function handleUploadMusic() {
  const input = musicFileRef.value
  if (!input || !input.files || !input.files[0]) {
    alert('请先选择要上传的音乐文件')
    return
  }
  uploadingMusic.value = true
  try {
    const res = await adminApi.uploadMusic(input.files[0])
    const url = res?.data?.data
    if (url) {
      newMusic.url = url
      alert('音乐上传成功，已自动填充链接')
    }
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '音乐上传失败')
  } finally {
    uploadingMusic.value = false
    if (input) input.value = ''
  }
}

async function save() {
  saving.value = true
  saved.value = false
  try {
    await profileApi.updateInfo({
      avatar: form.avatar,
      bio: form.bio,
      contact: form.contact
    })
    await Promise.all([
      adminApi.saveProfileKey('site_name', form.siteName),
      adminApi.saveProfileKey('site_desc', form.siteDesc),
      adminApi.saveProfileKey('bg_image', form.bgImage)
    ])
    saved.value = true
    setTimeout(() => { saved.value = false }, 2000)
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '保存失败，请确认已登录管理员账号')
  } finally {
    saving.value = false
  }
}

async function handleUploadBgImage() {
  const input = bgImageFileRef.value
  if (!input || !input.files || !input.files[0]) {
    alert('请先选择要上传的图片文件')
    return
  }
  uploadingBgImage.value = true
  try {
    const res = await adminApi.uploadBgImage(input.files[0])
    const url = res?.data?.data
    if (url) {
      form.bgImage = url
      alert('背景图上传成功，已自动应用')
    }
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.message || '背景图上传失败')
  } finally {
    uploadingBgImage.value = false
    if (input) input.value = ''
  }
}

onMounted(load)
</script>

<style scoped>
.admin-profile {
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
.form {
  max-width: 560px;
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
}
.section-title {
  margin: 1.5rem 0 1rem;
  font-size: 1rem;
  color: var(--color-text-muted);
}
.section-title:first-child {
  margin-top: 0;
}
.field {
  margin-bottom: 1.25rem;
}
.field label {
  display: block;
  margin-bottom: 0.35rem;
  font-weight: 500;
  color: var(--color-text);
}
.field input,
.field textarea {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 1rem;
}
.field textarea {
  resize: vertical;
  min-height: 120px;
}
.upload-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.file-hidden {
  display: none;
}
.actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
}
.btn.primary {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.btn.secondary {
  padding: 0.4rem 0.9rem;
  background: #fff;
  color: var(--color-text);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  cursor: pointer;
}
.btn.primary:hover:not(:disabled) {
  opacity: 0.9;
}
.btn.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.tip {
  color: var(--color-text-muted);
  font-size: 0.9rem;
}
.loading {
  padding: 2rem 0;
  color: var(--color-text-muted);
}
.field-hint {
  font-size: 0.85rem;
  color: var(--color-text-muted);
  margin: -0.5rem 0 1rem;
}
.add-music-row {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}
.add-music-row input {
  flex: 1;
}
.add-btn {
  margin-top: 0.5rem;
}
.music-list {
  margin-top: 1rem;
  padding: 1rem;
  background: var(--color-bg);
  border-radius: 8px;
  border: 1px solid var(--color-border);
}
.music-list-header {
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 0.75rem;
  color: var(--color-text-muted);
}
.music-list ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.music-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.9rem;
}
.music-item:last-child {
  border-bottom: none;
}
.music-info {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.btn-delete {
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  color: #dc2626;
  background: transparent;
  border: 1px solid #dc2626;
  border-radius: 4px;
  cursor: pointer;
}
.btn-delete:hover {
  background: rgba(220, 38, 38, 0.1);
}
</style>
