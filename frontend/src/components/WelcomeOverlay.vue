<template>
  <Transition name="fade">
    <div v-if="visible" class="welcome-overlay" @click="enter">
      <div class="content">
        <h2>欢迎来访</h2>
        <p>点击进入 · 开启音乐</p>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const visible = ref(false)
const emit = defineEmits(['enter'])

onMounted(() => {
  const entered = sessionStorage.getItem('welcome_entered')
  if (!entered) {
    visible.value = true
  }
})

function enter() {
  visible.value = false
  sessionStorage.setItem('welcome_entered', '1')
  sessionStorage.setItem('bgm_consent', '1')
  emit('enter')
}
</script>

<style scoped>
.welcome-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  cursor: pointer;
}
.content {
  text-align: center;
  color: #fff;
}
.content h2 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}
.content p {
  font-size: 1rem;
  opacity: 0.9;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
