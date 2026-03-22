<template>
  <aside class="sidebar">
    <router-link to="/" class="back-home">← 返回首页</router-link>
    <nav class="nav">
      <router-link to="/admin">概览</router-link>
      <router-link to="/admin/blogs">文章管理</router-link>
      <router-link to="/admin/comments">评论管理</router-link>
      <router-link to="/admin/links">友链管理</router-link>
      <router-link to="/admin/profile">站点配置</router-link>
    </nav>
  </aside>
</template>

<script setup>
</script>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 200px;
  height: 100vh;
  background: #fff;
  border-right: 1px solid var(--color-border);
  padding: 2rem 0;
}
.back-home {
  display: block;
  padding: 0.5rem 1.5rem;
  margin-bottom: 1rem;
  color: var(--color-primary);
  font-weight: 500;
  border-bottom: 1px solid var(--color-border);
}
.back-home:hover {
  background: rgba(37, 99, 235, 0.08);
}
.nav {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.nav a {
  padding: 0.5rem 1.5rem;
  color: var(--color-text-muted);
}
.nav a:hover, .nav a.router-link-active {
  color: var(--color-primary);
  background: rgba(37, 99, 235, 0.08);
}
</style>
