<template>
  <div class="sakura-container" ref="container">
    <div
      v-for="(p, i) in petals"
      :key="i"
      class="petal"
      :style="petalStyle(p)"
    ></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const container = ref(null)
const petalCount = 45
const petals = ref([])

function random(min, max) {
  return Math.random() * (max - min) + min
}

function createPetals() {
  const list = []
  for (let i = 0; i < petalCount; i++) {
    list.push({
      left: random(0, 100),
      size: random(10, 20),
      duration: random(8, 18),
      delay: random(0, 10),
      sway: random(50, 120),
    })
  }
  petals.value = list
}

function petalStyle(p) {
  return {
    left: `${p.left}%`,
    width: `${p.size}px`,
    height: `${p.size}px`,
    animationDuration: `${p.duration}s`,
    animationDelay: `-${p.delay}s`,
    '--sway': `${p.sway}px`,
  }
}

onMounted(createPetals)
</script>

<style scoped>
.sakura-container {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.petal {
  position: absolute;
  top: -30px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ffb7c5'%3E%3Cpath d='M12 2c-2 4-4 8-4 10 0 3.3 2.7 6 6 6s6-2.7 6-6c0-2-2-6-4-10-1.3 2.7-2.7 5-4 7-1.3-2-2-4-2-5 0-1.7 1.3-3 3-3 1 0 2 .5 2.5 1.2.5-.7 1.5-1.2 2.5-1.2 1.7 0 3 1.3 3 3 0 1-.7 3-2 5-1.3-2-2.7-4.3-4-7z'/%3E%3C/svg%3E") center/contain no-repeat;
  opacity: 0.85;
  filter: drop-shadow(0 0 2px rgba(255, 183, 197, 0.5));
  animation: fall linear infinite;
}
@keyframes fall {
  0% {
    transform: translateY(0) translateX(0) rotate(0deg);
    opacity: 0.9;
  }
  25% {
    transform: translateY(25vh) translateX(var(--sway, 60px)) rotate(90deg);
  }
  50% {
    transform: translateY(50vh) translateX(0) rotate(180deg);
  }
  75% {
    transform: translateY(75vh) translateX(calc(var(--sway, 60px) * -1)) rotate(270deg);
  }
  100% {
    transform: translateY(110vh) translateX(0) rotate(360deg);
    opacity: 0.6;
  }
}
</style>
