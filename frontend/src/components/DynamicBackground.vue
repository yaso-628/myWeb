<template>
  <div class="dynamic-bg" :class="{ 'has-image': bgImage }">
    <div
      v-if="bgImage"
      class="bg-image-wrap"
      :style="{ transform: `translate3d(0, ${parallaxY}px, 0)` }"
    >
      <img :src="bgImage" alt="" class="bg-image" />
      <div class="bg-overlay"></div>
    </div>
    <div v-else class="bg-gradient" :style="{ transform: `translate3d(0, ${parallaxY}px, 0)` }"></div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount, onMounted } from 'vue'
import { profileApi } from '@/api'
import { resolveAssetUrl } from '@/utils/asset'

const bgImage = ref('')
const parallaxY = ref(0)
let rafId = 0
let lastScroll = 0
let destroyed = false
let cleanupScroll = null

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

onMounted(() => {
  const onScroll = () => {
    lastScroll = window.scrollY || 0
    if (rafId) return
    rafId = window.requestAnimationFrame(() => {
      rafId = 0
      if (destroyed) return
      // 背景轻微跟随滚动，制造“街景视差”
      parallaxY.value = -Math.round(lastScroll * 0.03)
    })
  }

  window.addEventListener('scroll', onScroll, { passive: true })
  cleanupScroll = () => window.removeEventListener('scroll', onScroll)
})

onBeforeUnmount(() => {
  destroyed = true
  cleanupScroll?.()
  if (rafId) window.cancelAnimationFrame(rafId)
})
</script>

<style scoped>
.dynamic-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

.dynamic-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  /* 暗角 + 柔雾：让顶部导航白字更易读，也更“舞台感” */
  background: radial-gradient(circle at 50% 20%, rgba(0, 0, 0, 0.04), rgba(0, 0, 0, 0.38) 70%, rgba(0, 0, 0, 0.52) 100%);
  pointer-events: none;
  z-index: 1;
}

.dynamic-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  /* 轻量扫描线（纯 CSS，避免重 JS） */
  background: repeating-linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.035),
    rgba(255, 255, 255, 0.035) 1px,
    rgba(0, 0, 0, 0.0) 3px,
    rgba(0, 0, 0, 0.0) 6px
  );
  mix-blend-mode: overlay;
  opacity: 0.25;
  pointer-events: none;
  z-index: 2;
}
.bg-image-wrap {
  position: absolute;
  inset: 0;
  z-index: 0;
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
  background: radial-gradient(circle at 0% 0%, #f2fbff 0%, #e8f4ff 32%, #d9efff 63%, #f5f0ff 100%);
  opacity: 0.85;
  z-index: 0;
}
</style>
