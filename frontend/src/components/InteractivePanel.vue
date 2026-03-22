<template>
  <section class="interactive">
    <div class="interactive-header">
      <span class="tag">Playground</span>
      <h2>今天想从哪里认识我？</h2>
      <p>轻轻移动鼠标，卡片会跟随你的视线微微转动，点击即可进入对应页面。</p>
    </div>
    <div class="card-orbit">
      <article
        v-for="card in cards"
        :key="card.title"
        class="orbit-card"
        @mousemove="onCardMove"
        @mouseleave="onCardLeave"
        @click="go(card.primaryTo)"
      >
        <div class="orbit-card-glow"></div>
        <header class="orbit-card-header">
          <span class="emoji">{{ card.emoji }}</span>
          <h3>{{ card.title }}</h3>
        </header>
        <p class="orbit-card-desc">{{ card.desc }}</p>
        <div class="orbit-card-chips">
          <button
            v-for="chip in card.chips"
            :key="chip.label"
            type="button"
            class="chip"
            @click.stop="go(chip.to)"
          >
            {{ chip.label }}
          </button>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const cards = [
  {
    emoji: '📓',
    title: '最近在折腾什么',
    desc: '记录日常项目、踩坑和一些有意思的想法。',
    primaryTo: '/blog',
    chips: [
      { label: '全部文章', to: '/blog' },
      { label: '留言板', to: '/comment' }
    ]
  },
  {
    emoji: '💻',
    title: '我的技术栈',
    desc: '前后端都在玩，喜欢做好看的界面和顺手的工具。',
    primaryTo: '/profile',
    chips: [
      { label: '关于我', to: '/profile' },
      { label: '友链', to: '/link' }
    ]
  },
  {
    emoji: '🎧',
    title: '当前心情配乐',
    desc: '一边听歌一边写代码，灵感往往就在某一段旋律里。',
    primaryTo: '/comment',
    chips: [
      { label: '写一条留言', to: '/comment' },
      { label: '继续逛主页', to: '/' }
    ]
  }
]

function go(to) {
  if (!to) return
  router.push(to)
}

function onCardMove(e) {
  const card = e.currentTarget
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const midX = rect.width / 2
  const midY = rect.height / 2

  const rotateX = ((y - midY) / midY) * -6
  const rotateY = ((x - midX) / midX) * 6

  card.style.setProperty('--rx', `${rotateX}deg`)
  card.style.setProperty('--ry', `${rotateY}deg`)
}

function onCardLeave(e) {
  const card = e.currentTarget
  card.style.setProperty('--rx', '0deg')
  card.style.setProperty('--ry', '0deg')
}
</script>

<style scoped>
.interactive {
  display: flex;
  flex-direction: column;
  gap: 1.4rem;
}
.interactive-header h2 {
  font-size: 1.3rem;
  margin: 0.3rem 0 0.3rem;
}
.interactive-header p {
  margin: 0;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
.tag {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.2rem 0.65rem;
  border-radius: 999px;
  font-size: 0.75rem;
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
}
.dark .tag {
  background: rgba(59, 130, 246, 0.25);
  color: #bfdbfe;
}
.card-orbit {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
  gap: 1.25rem;
}
.orbit-card {
  position: relative;
  padding: 1.4rem 1.3rem 1.3rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.12);
  transform: perspective(900px) rotateX(var(--rx, 0deg)) rotateY(var(--ry, 0deg));
  transform-style: preserve-3d;
  transition: transform 0.18s ease-out, box-shadow 0.18s ease-out, border-color 0.18s ease-out;
  overflow: hidden;
  cursor: pointer;
}
.dark .orbit-card {
  background: rgba(15, 23, 42, 0.96);
  border-color: rgba(51, 65, 85, 0.9);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.9);
}
.orbit-card:hover {
  box-shadow: 0 22px 60px rgba(15, 23, 42, 0.2);
  border-color: rgba(59, 130, 246, 0.7);
}
.orbit-card-glow {
  position: absolute;
  inset: -40%;
  background:
    radial-gradient(circle at 0 0, rgba(59, 130, 246, 0.2), transparent 60%),
    radial-gradient(circle at 100% 100%, rgba(244, 114, 182, 0.2), transparent 60%);
  opacity: 0.8;
  pointer-events: none;
}
.orbit-card-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.55rem;
  margin-bottom: 0.5rem;
}
.emoji {
  font-size: 1.3rem;
}
.orbit-card-header h3 {
  margin: 0;
  font-size: 1rem;
}
.orbit-card-desc {
  position: relative;
  margin: 0.35rem 0 0.7rem;
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
.orbit-card-chips {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}
.chip {
  border-radius: 999px;
  padding: 0.25rem 0.7rem;
  border: none;
  font-size: 0.78rem;
  background: rgba(15, 23, 42, 0.04);
  color: var(--color-text-muted);
  cursor: pointer;
  transition: background 0.16s ease, transform 0.12s ease, box-shadow 0.16s ease;
}
.chip:hover {
  background: rgba(59, 130, 246, 0.12);
  color: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.22);
}
.dark .chip {
  background: rgba(15, 23, 42, 0.9);
}
.dark .chip:hover {
  background: rgba(59, 130, 246, 0.2);
  color: #bfdbfe;
}

@media (max-width: 768px) {
  .orbit-card {
    padding: 1.1rem 1rem 1rem;
  }
}
</style>

