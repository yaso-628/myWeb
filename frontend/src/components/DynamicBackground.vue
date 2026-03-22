<template>
  <div class="dynamic-bg" :class="{ 'has-image': bgImage }">
    <div v-if="bgImage" class="bg-image-wrap">
      <img :src="bgImage" alt="" class="bg-image" />
      <div class="bg-overlay"></div>
    </div>
    <div v-else class="bg-gradient"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { profileApi } from '@/api'
import { resolveAssetUrl } from '@/utils/asset'

const bgImage = ref('')

onMounted(async () => {
  try {
    const res = await profileApi.getByKey('bg_image')
    const url = res?.data?.data?.value
    if (url) {
      const fullUrl = resolveAssetUrl(url)
      const img = new Image()
      img.onload = () => { bgImage.value = fullUrl }
      img.onerror = () => {}
      img.src = fullUrl
    }
  } catch (_) {}
})
</script>

<style scoped>
.dynamic-bg {
  position: fixed;
  inset: 0;
  z-index: -2;
  overflow: hidden;
}
.bg-image-wrap {
  position: absolute;
  inset: 0;
}
.bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  animation: bg-zoom 20s ease-in-out infinite alternate;
}
@keyframes bg-zoom {
  from { transform: scale(1); }
  to { transform: scale(1.05); }
}
.bg-overlay {
  position: absolute;
  inset: 0;
  /* 只保留非常轻微的上下渐变，让原图基本保持原样 */
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.05) 0%,
    rgba(0, 0, 0, 0.0) 40%,
    rgba(0, 0, 0, 0.12) 100%
  );
}
.dark .bg-overlay {
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.25) 0%,
    rgba(0, 0, 0, 0.1) 40%,
    rgba(0, 0, 0, 0.35) 100%
  );
}
.bg-gradient {
  position: absolute;
  inset: 0;
  /* 更柔和的茉莉白风格渐变背景 */
  background: radial-gradient(circle at 0% 0%, #fdfcff 0%, #f5ecff 32%, #e3f5ff 65%, #ffe9f2 100%);
  opacity: 0.85;
}
</style>
