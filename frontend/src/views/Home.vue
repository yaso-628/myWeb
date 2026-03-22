<template>
  <div class="home">
    <section class="hero">
      <div class="hero-bg"></div>
      <h1 class="hero-title">{{ siteName || '欢迎来到我的个人网站' }}</h1>
      <p class="subtitle typing-text">{{ displayDesc }}</p>
    </section>

    <InteractivePanel />

    <section class="latest-blogs" ref="blogsSection">
      <div class="section-header">
        <h2>最新文章</h2>
        <router-link v-if="userStore.isAdmin" to="/admin/blogs/new" class="btn-publish" v-ripple>发表文章</router-link>
      </div>
      <ul v-if="blogs.length" class="blog-list">
        <li v-for="(blog, i) in blogs" :key="blog.id" class="blog-item" :style="{ animationDelay: `${i * 0.08}s` }">
          <router-link :to="`/blog/${blog.id}`" class="blog-link">{{ blog.title }}</router-link>
          <span class="meta">{{ blog.createTime }} · {{ blog.category || '未分类' }}</span>
        </li>
      </ul>
      <p v-else class="empty">暂无文章</p>
      <router-link v-if="blogs.length" to="/blog" class="more">查看更多 →</router-link>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { blogApi, profileApi } from '@/api'
import { useUserStore } from '@/store/user'
import InteractivePanel from '@/components/InteractivePanel.vue'

const userStore = useUserStore()
const siteName = ref('')
const siteDesc = ref('')
const displayDesc = ref('')
const blogs = ref([])
const blogsSection = ref(null)

function typeWriter(text, speed = 80) {
  let i = 0
  displayDesc.value = ''
  const timer = setInterval(() => {
    if (i < text.length) {
      displayDesc.value += text[i]
      i++
    } else {
      clearInterval(timer)
    }
  }, speed)
}

watch(siteDesc, (val) => {
  const text = val || '记录技术与生活'
  setTimeout(() => typeWriter(text), 500)
})

onMounted(async () => {
  try {
    const [siteNameRes, siteDescRes, blogRes] = await Promise.all([
      profileApi.getByKey('site_name'),
      profileApi.getByKey('site_desc'),
      blogApi.list(1, 5)
    ])
    siteName.value = siteNameRes?.data?.data?.value || ''
    siteDesc.value = siteDescRes?.data?.data?.value || ''
    blogs.value = blogRes?.data?.data?.list || []
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.home {
  max-width: 960px;
  margin: 0 auto;
  padding: 1.5rem 1rem 2.5rem;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}
.hero {
  position: relative;
  padding: 2.75rem 2.25rem;
  border-radius: 24px;
  overflow: hidden;
  background: radial-gradient(circle at 0% 0%, rgba(255, 255, 255, 0.92) 0%, rgba(255, 255, 255, 0.82) 45%, rgba(255, 255, 255, 0.7) 100%);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow:
    0 24px 60px rgba(15, 23, 42, 0.18),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  text-align: left;
}
.dark .hero {
  background: radial-gradient(circle at 0% 0%, rgba(31, 41, 55, 0.96) 0%, rgba(17, 24, 39, 0.9) 45%, rgba(15, 23, 42, 0.8) 100%);
  border-color: rgba(148, 163, 184, 0.45);
  box-shadow:
    0 24px 60px rgba(15, 23, 42, 0.9),
    0 0 0 1px rgba(30, 64, 175, 0.55) inset;
}
.hero-bg {
  position: absolute;
  inset: -40%;
  background: radial-gradient(circle at 10% 0%, rgba(96, 165, 250, 0.22) 0, transparent 55%),
              radial-gradient(circle at 90% 20%, rgba(244, 114, 182, 0.22) 0, transparent 55%);
  filter: blur(2px);
  opacity: 1;
}
.hero-title {
  position: relative;
  font-size: 2.2rem;
  margin-bottom: 0.9rem;
  line-height: 1.25;
  background: linear-gradient(110deg, #0f172a, #1d4ed8, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.dark .hero-title {
  background: linear-gradient(110deg, #e5e7eb, #93c5fd, #c4b5fd);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle {
  position: relative;
  color: var(--color-text-muted);
  min-height: 1.5em;
  font-size: 0.98rem;
}
.typing-text::after {
  content: '|';
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  50% { opacity: 0; }
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}
.latest-blogs h2 {
  margin: 0;
  font-size: 1.1rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--color-text-muted);
}
.btn-publish {
  padding: 0.4rem 0.9rem;
  background: var(--color-primary);
  color: #fff;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.9rem;
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-publish:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--color-glow);
}
.blog-list {
  list-style: none;
  margin: 0;
  padding: 1.25rem 1.75rem 1.5rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}
.dark .blog-list {
  background: rgba(15, 23, 42, 0.9);
  border-color: rgba(51, 65, 85, 0.9);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.9);
}
.blog-item {
  padding: 1rem 0;
  border-bottom: 1px solid var(--color-border);
  animation: slideUp 0.5s ease backwards;
}
.blog-link {
  font-weight: 500;
  transition: color 0.2s;
}
.blog-link:hover {
  color: var(--color-primary);
}
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.meta {
  display: block;
  font-size: 0.875rem;
  color: var(--color-text-muted);
  margin-top: 0.25rem;
}
.empty {
  color: var(--color-text-muted);
  padding: 1.5rem 0;
}
.more {
  display: inline-block;
  margin-top: 1.2rem;
  transition: transform 0.2s;
}
.more:hover {
  transform: translateX(4px);
}

@media (max-width: 768px) {
  .home {
    padding: 1rem 0.25rem 1.5rem;
    gap: 1.25rem;
  }
  .hero {
    padding: 1.5rem 1rem;
    border-radius: 16px;
  }
  .hero-title {
    font-size: 1.5rem;
    margin-bottom: 0.6rem;
  }
  .subtitle {
    font-size: 0.9rem;
  }
  .section-header {
    align-items: flex-start;
    gap: 0.6rem;
    flex-direction: column;
    margin-bottom: 0.75rem;
  }
  .latest-blogs h2 {
    font-size: 1rem;
    letter-spacing: 0.04em;
  }
  .btn-publish {
    padding: 0.35rem 0.7rem;
    font-size: 0.85rem;
  }
  .blog-list {
    padding: 0.75rem 1rem 1rem;
    border-radius: 12px;
  }
  .blog-item {
    padding: 0.75rem 0;
  }
  .blog-link {
    font-size: 0.98rem;
    line-height: 1.45;
    word-break: break-word;
  }
  .meta {
    font-size: 0.8rem;
  }
  .more {
    margin-top: 0.8rem;
    font-size: 0.9rem;
  }
}
</style>
