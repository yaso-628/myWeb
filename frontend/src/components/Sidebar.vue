<template>
  <aside class="bits2d-sidebar">
    <div class="bits2d-sidebar__glow" aria-hidden="true" />
    <router-link to="/" class="bits2d-sidebar__back">
      ← 返回首页
    </router-link>
    <nav class="bits2d-sidebar__nav">
      <router-link to="/admin" class="bits2d-sidebar__link">概览</router-link>
      <router-link to="/admin/blogs" class="bits2d-sidebar__link">文章管理</router-link>
      <router-link to="/admin/comments" class="bits2d-sidebar__link">评论管理</router-link>
      <router-link to="/admin/links" class="bits2d-sidebar__link">友链管理</router-link>
      <router-link to="/admin/profile" class="bits2d-sidebar__link">站点配置</router-link>
    </nav>
  </aside>
</template>

<script setup>
</script>

<style scoped>
.bits2d-sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 220px;
  height: 100vh;
  padding: 2rem 0;
  background: rgba(255, 255, 255, 0.78);
  border-right: 1px solid rgba(229, 231, 235, 0.8);
  backdrop-filter: blur(16px);
  z-index: 40;
  overflow: hidden;
}

.dark .bits2d-sidebar {
  background: rgba(31, 41, 55, 0.6);
  border-right-color: rgba(51, 65, 85, 0.75);
}

.bits2d-sidebar__glow {
  position: absolute;
  inset: -60px;
  background:
    radial-gradient(circle at 30% 10%, rgba(56, 189, 248, 0.25), transparent 55%),
    radial-gradient(circle at 80% 35%, rgba(167, 139, 250, 0.22), transparent 55%),
    radial-gradient(circle at 50% 90%, rgba(251, 113, 133, 0.16), transparent 60%);
  filter: blur(18px);
  animation: bits2d-float 8s ease-in-out infinite;
  pointer-events: none;
}

.bits2d-sidebar__back {
  display: block;
  padding: 0.6rem 1.5rem;
  margin-bottom: 1rem;
  color: var(--color-primary);
  font-weight: 700;
  text-decoration: none;
  border-bottom: 1px solid rgba(229, 231, 235, 0.7);
  position: relative;
  z-index: 1;
  transition: transform 160ms ease;
}

.bits2d-sidebar__back:hover {
  transform: translateY(-2px);
}

.dark .bits2d-sidebar__back {
  border-bottom-color: rgba(51, 65, 85, 0.75);
}

.bits2d-sidebar__nav {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  position: relative;
  z-index: 1;
}

.bits2d-sidebar__link {
  padding: 0.55rem 1.5rem;
  color: var(--color-text-muted);
  text-decoration: none;
  border-radius: 14px;
  position: relative;
  overflow: hidden;
  transition: transform 160ms ease, background 160ms ease, color 160ms ease, box-shadow 160ms ease;
}

.bits2d-sidebar__link::after {
  content: '';
  position: absolute;
  inset: -2px;
  background: conic-gradient(
    from 110deg,
    rgba(56, 189, 248, 0),
    rgba(56, 189, 248, 0.35),
    rgba(167, 139, 250, 0.35),
    rgba(251, 113, 133, 0.25),
    rgba(56, 189, 248, 0)
  );
  filter: blur(16px);
  opacity: 0;
  transition: opacity 160ms ease;
}

.bits2d-sidebar__link:hover {
  transform: translateY(-2px);
  color: var(--color-primary);
  background: rgba(37, 99, 235, 0.08);
  box-shadow: 0 0 28px rgba(37, 99, 235, 0.18);
}

.bits2d-sidebar__link:hover::after {
  opacity: 1;
}

.bits2d-sidebar__link.router-link-active {
  color: var(--color-primary);
  background: rgba(37, 99, 235, 0.12);
  box-shadow: 0 0 36px rgba(37, 99, 235, 0.25);
}
</style>
