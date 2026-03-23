<template>
  <div class="home">
    <section class="hero">
      <div class="hero-grid"></div>
      <h1 class="hero-title">{{ siteName || '欢迎来到我的个人网站' }}</h1>
      <p class="subtitle typing-text">{{ displayDesc }}</p>
    </section>

    <InteractivePanel />

    <section class="latest-blogs" ref="blogsSection">
      <div class="section-header">
        <h2>最新文章</h2>
        <router-link v-if="userStore.isAdmin" to="/admin/blogs/new" class="btn-publish" v-ripple>发文章</router-link>
      </div>
      <div v-if="blogs.length" class="blog-grid">
        <NeCard
          v-for="(blog, i) in blogs"
          :key="blog.id"
          class="blog-card"
          :noPad="false"
          :title="''"
          :style="{ animationDelay: `${i * 0.08}s` }"
        >
          <template #default>
            <router-link :to="`/blog/${blog.id}`" class="blog-link">{{ blog.title }}</router-link>
            <span class="meta">{{ blog.createTime }} · {{ blog.category || '未分类' }}</span>
          </template>
          <template #footer>
            <router-link :to="`/blog/${blog.id}`" class="read-more">阅读全文</router-link>
          </template>
        </NeCard>
      </div>
      <p v-else class="empty">暂无文章</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import NeCard from '@/components/bits2d/NeCard.vue'
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
  padding: 3.75rem 2.25rem 2.25rem;
  border-radius: 24px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.10);
  box-shadow: 0 18px 60px rgba(15, 23, 42, 0.14);
  text-align: left;
}
.hero::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: 26px;
  background: conic-gradient(
    from 180deg,
    rgba(56, 189, 248, 0.0),
    rgba(56, 189, 248, 0.34),
    rgba(167, 139, 250, 0.34),
    rgba(251, 113, 133, 0.28),
    rgba(56, 189, 248, 0.0)
  );
  filter: blur(14px);
  opacity: 0.65;
  pointer-events: none;
  animation: bits2d-spin 7.5s linear infinite;
}

.dark .hero {
  background: rgba(0, 0, 0, 0.22);
}
.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.16) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.16) 1px, transparent 1px);
  background-size: 28px 28px;
  mask-image: radial-gradient(circle at 50% 45%, #000 20%, transparent 75%);
  animation: gridFloat 14s linear infinite;
  pointer-events: none;
}
.hero-title {
  position: relative;
  font-size: 2.4rem;
  margin-bottom: 0.8rem;
  line-height: 1.25;
  background: linear-gradient(110deg, #ffffff, #93c5fd, #c4b5fd, #fda4af);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 18px rgba(56, 189, 248, 0.20);
}
.dark .hero-title {
  background: linear-gradient(110deg, rgba(229, 231, 235, 1), #93c5fd, #c4b5fd, #fda4af);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle {
  position: relative;
  color: rgba(255, 255, 255, 0.82);
  min-height: 1.5em;
  font-size: 0.98rem;
  text-shadow: 0 0 16px rgba(56, 189, 248, 0.18);
}
.typing-text::after {
  content: '|';
  animation: blink 1s step-end infinite;
}
@keyframes blink {
  50% { opacity: 0; }
}
.blog-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.95rem;
}
.blog-card {
  animation: slideUp 0.5s ease backwards;
  backdrop-filter: blur(8px);
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
  padding: 0.3rem 0.7rem;
  background: var(--color-primary);
  color: #fff;
  border-radius: 999px;
  text-decoration: none;
  font-size: 0.8rem;
  line-height: 1.2;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
}
.btn-publish:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--color-glow);
}
.blog-link {
  display: block;
  font-weight: 500;
  margin-bottom: 0.4rem;
  color: var(--color-text);
  text-decoration: none;
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
.read-more {
  font-size: 0.88rem;
  color: var(--color-primary);
  text-decoration: none;
}
.empty {
  color: var(--color-text-muted);
  padding: 1.5rem 0;
}
@keyframes gridFloat {
  0% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .home {
    padding: 1rem 0.25rem 1.5rem;
    gap: 1.25rem;
  }
  .hero {
    padding: 2.2rem 1rem 1.4rem;
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
    padding: 0.28rem 0.6rem;
    font-size: 0.76rem;
  }
  .blog-grid {
    grid-template-columns: 1fr;
    gap: 0.7rem;
  }
  .blog-link {
    font-size: 0.98rem;
    line-height: 1.45;
    word-break: break-word;
  }
  .meta {
    font-size: 0.8rem;
  }
  .more-link {
    margin-top: 0.8rem;
  }
}
</style>
