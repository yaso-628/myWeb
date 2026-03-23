<template>
  <Transition name="fade">
    <div v-if="visible" class="welcome-overlay" @click="enter">
      <div class="content">
        <h2 class="welcome-title">
          <span>{{ typedTitle }}</span>
          <span class="caret" v-if="typingTitle" aria-hidden="true">|</span>
        </h2>
        <p class="welcome-sub">
          <span>{{ typedSubtitle }}</span>
          <span class="caret caret-sub" v-if="typingSubtitle" aria-hidden="true">|</span>
        </p>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'

const visible = ref(false)
const emit = defineEmits(['enter'])

const fullTitle = '欢迎来访'
const fullSubtitle = '点击进入 · 开启音乐'
const typedTitle = ref('')
const typedSubtitle = ref('')
const typingTitle = ref(false)
const typingSubtitle = ref(false)

let titleTimer = null
let subTimer = null

onMounted(() => {
  const entered = sessionStorage.getItem('welcome_entered')
  if (!entered) {
    visible.value = true
  }
})

watch(visible, (v) => {
  if (!v) return

  typedTitle.value = ''
  typedSubtitle.value = ''
  typingTitle.value = true
  typingSubtitle.value = false

  let i = 0
  if (titleTimer) window.clearInterval(titleTimer)
  titleTimer = window.setInterval(() => {
    i++
    typedTitle.value = fullTitle.slice(0, i)
    if (i >= fullTitle.length) {
      typingTitle.value = false
      window.clearInterval(titleTimer)
      titleTimer = null

      let j = 0
      typingSubtitle.value = true
      if (subTimer) window.clearInterval(subTimer)
      subTimer = window.setInterval(() => {
        j++
        typedSubtitle.value = fullSubtitle.slice(0, j)
        if (j >= fullSubtitle.length) {
          typingSubtitle.value = false
          window.clearInterval(subTimer)
          subTimer = null
        }
      }, 45)
    }
  }, 65)
})

onBeforeUnmount(() => {
  if (titleTimer) window.clearInterval(titleTimer)
  if (subTimer) window.clearInterval(subTimer)
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
.welcome-title {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  font-weight: 900;
  letter-spacing: 0.02em;
  text-shadow: 0 0 22px rgba(56, 189, 248, 0.25);
}

.welcome-sub {
  font-size: 1rem;
  opacity: 0.9;
}

.caret {
  display: inline-block;
  margin-left: 6px;
  animation: caret-blink 0.9s step-end infinite;
}
.caret-sub {
  opacity: 0.85;
}

@keyframes caret-blink {
  50% {
    opacity: 0;
  }
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.35s ease, transform 0.35s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}
</style>
