<template>
  <div class="profile-page">
    <h1>个人简介</h1>
    <p v-if="loading" class="loading">加载中...</p>
    <template v-else-if="hasContent">
      <div class="profile-card animate-in">
        <div v-if="info.avatar" class="avatar-wrap">
          <img :src="toAssetUrl(info.avatar)" alt="头像" class="avatar" />
        </div>
        <div v-if="info.bio" class="bio content" v-html="info.bio"></div>
        <div v-if="info.contact" class="contact">
          <span class="label">联系方式：</span>
          <span class="value">{{ info.contact }}</span>
        </div>
      </div>
      <p v-if="userStore.isAdmin" class="edit-hint">
        <router-link to="/admin/profile">编辑个人简介</router-link>
      </p>
    </template>
    <template v-else>
      <p class="empty">暂无简介</p>
      <p v-if="userStore.isAdmin" class="edit-hint">
        <router-link to="/admin/profile">去填写个人简介</router-link>
      </p>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { profileApi } from '@/api'
import { useUserStore } from '@/store/user'
import { resolveAssetUrl } from '@/utils/asset'

const userStore = useUserStore()
const info = ref({ avatar: '', bio: '', contact: '' })
const loading = ref(false)

const hasContent = computed(() => {
  const i = info.value
  return !!(i?.avatar || i?.bio || i?.contact)
})

function toAssetUrl(url) {
  return resolveAssetUrl(url)
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await profileApi.getInfo()
    info.value = res?.data?.data ?? { avatar: '', bio: '', contact: '' }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}
.profile-page h1 {
  margin-bottom: 1.5rem;
}
.profile-card {
  background: var(--color-card);
  border-radius: 12px;
  padding: 2rem;
  border: 1px solid var(--color-border);
}
.avatar-wrap {
  margin-bottom: 1.5rem;
}
.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-border, #e5e7eb);
}
.bio {
  line-height: 1.8;
  margin-bottom: 1rem;
}
.bio.content :deep(img) {
  max-width: 100%;
}
.contact {
  font-size: 0.95rem;
  color: var(--color-text-muted, #6b7280);
}
.contact .label {
  margin-right: 0.25rem;
}
.edit-hint {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}
.edit-hint a {
  color: var(--color-primary, #2563eb);
}
.empty, .loading {
  padding: 3rem 0;
  text-align: center;
  color: var(--color-text-muted, #6b7280);
}
</style>
