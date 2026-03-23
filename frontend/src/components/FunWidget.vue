<template>
  <div class="fun-widget" :class="{ collapsed: collapsed }">
    <button class="toggle-btn" @click="collapsed = !collapsed" :title="collapsed ? '展开' : '收起'">
      {{ collapsed ? '✨' : '×' }}
    </button>
    <div v-show="!collapsed" class="widget-content">
      <div class="clock">{{ time }}</div>
      <p class="greeting">{{ greeting }}</p>
      <p class="quote">{{ quote }}</p>
      <div class="actions">
        <button type="button" class="mini-link" @click="go('/blog')">看文章</button>
        <button type="button" class="mini-link" @click="go('/comment')">去留言</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const time = ref('00:00:00')
const greeting = ref('')
const collapsed = ref(true)

const quotes = [
  '代码改变世界 ✨',
  '保持热爱，奔赴山海',
  '今天的努力，是明天的惊喜',
  '简单的事情重复做，重复的事情用心做',
  '技术没有终点，学习永不止步',
  '写代码是一场修行',
]
const quote = ref(quotes[0])

function go(path) {
  router.push(path)
}

function updateTime() {
  const now = new Date()
  time.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  const h = now.getHours()
  if (h >= 5 && h < 12) greeting.value = '早上好 ☀️'
  else if (h >= 12 && h < 18) greeting.value = '下午好 🌤️'
  else if (h >= 18 && h < 22) greeting.value = '晚上好 🌙'
  else greeting.value = '夜深了，早点休息 🌃'
}

let timer
onMounted(() => {
  updateTime()
  quote.value = quotes[Math.floor(Math.random() * quotes.length)]
  timer = setInterval(updateTime, 1000)
})
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.fun-widget {
  position: fixed;
  bottom: 90px;
  left: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 1rem 1.25rem;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  z-index: 999;
  transition: all 0.3s ease;
}
.fun-widget:hover {
  box-shadow: 0 8px 32px var(--color-glow);
}
.fun-widget.collapsed {
  padding: 0.5rem 0.75rem;
}
.dark .fun-widget {
  background: rgba(31, 41, 55, 0.95);
}
.toggle-btn {
  position: absolute;
  top: 4px;
  right: 8px;
  background: none;
  border: none;
  font-size: 1rem;
  cursor: pointer;
  opacity: 0.7;
}
.toggle-btn:hover {
  opacity: 1;
}
.widget-content {
  padding-right: 1.5rem;
}
.actions {
  margin-top: 0.6rem;
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}
.mini-link {
  border-radius: 999px;
  border: 1px solid var(--color-border);
  background: transparent;
  padding: 0.15rem 0.6rem;
  font-size: 0.75rem;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease, border-color 0.15s ease;
}
.mini-link:hover {
  background: rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.6);
  color: #1d4ed8;
}
.clock {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-primary);
  letter-spacing: 0.05em;
}
.greeting {
  margin: 0.25rem 0;
  font-size: 0.95rem;
  color: var(--color-text-muted);
}
.quote {
  font-size: 0.8rem;
  color: var(--color-text-muted);
  font-style: italic;
  margin: 0.5rem 0 0;
}
</style>
