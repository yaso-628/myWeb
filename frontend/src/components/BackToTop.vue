<template>
  <Transition name="fade">
    <button
      v-show="visible"
      class="back-to-top"
      @click="scrollToTop"
      title="返回顶部"
      v-ripple
    >
      ↑
    </button>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)

function handleScroll() {
  visible.value = window.scrollY > 400
}

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
  onUnmounted(() => window.removeEventListener('scroll', handleScroll))
})
</script>

<style scoped>
.back-to-top {
  position: fixed;
  bottom: 24px;
  left: 24px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  box-shadow: 0 4px 12px var(--color-glow);
  z-index: 998;
  transition: transform 0.2s, box-shadow 0.2s;
}
.back-to-top:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px var(--color-glow);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
