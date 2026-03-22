<template>
  <header class="header">
    <router-link to="/" class="logo" @click="mobileMenuOpen = false">{{ siteName || '个人网站' }}</router-link>
    <button class="mobile-menu-btn" type="button" @click="mobileMenuOpen = !mobileMenuOpen">
      {{ mobileMenuOpen ? '关闭' : '菜单' }}
    </button>
    <div class="nav" :class="{ open: mobileMenuOpen }">
      <el-button
        circle
        class="theme-btn"
        size="small"
        @click="theme.toggle()"
        :title="theme.isDark ? '浅色模式' : '深色模式'"
      >
        {{ theme.isDark ? '☀️' : '🌙' }}
      </el-button>
      <el-menu
        mode="horizontal"
        :default-active="$route.path"
        class="menu"
        @select="mobileMenuOpen = false"
        router
        background-color="transparent"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/blog">博客</el-menu-item>
        <el-menu-item index="/comment">留言板</el-menu-item>
        <el-menu-item index="/link">友链</el-menu-item>
        <el-menu-item index="/profile">简介</el-menu-item>
      </el-menu>
      <div class="auth-group">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ userStore.user?.nickname || userStore.user?.username || '用户' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/me')">个人信息</el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" @click="$router.push('/admin')">
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" text @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useThemeStore } from '@/store/theme'
import { profileApi } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const theme = useThemeStore()
const siteName = ref('')
const mobileMenuOpen = ref(false)

onMounted(async () => {
  try {
    const res = await profileApi.getByKey('site_name')
    siteName.value = res?.data?.data?.value || ''
  } catch (_) {}
})

function logout() {
  userStore.clear()
  mobileMenuOpen.value = false
  router.push('/')
}

watch(() => route.fullPath, () => {
  mobileMenuOpen.value = false
})
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
}
.dark .header {
  background: rgba(31, 41, 55, 0.9);
}
.logo {
  font-weight: 700;
  font-size: 1.25rem;
  color: var(--color-text);
}
.logo:hover {
  color: var(--color-primary);
  text-decoration: none;
}
.mobile-menu-btn {
  display: none;
  border: 1px solid var(--color-border);
  background: var(--color-card);
  color: var(--color-text);
  border-radius: 8px;
  padding: 0.35rem 0.65rem;
  cursor: pointer;
}
.theme-btn {
  margin-right: 1rem;
}
.nav {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.menu {
  border-bottom: none;
}
.auth-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-left: 1rem;
}

@media (max-width: 768px) {
  .header {
    flex-wrap: wrap;
    padding: 0.75rem 1rem;
    gap: 0.5rem;
  }
  .logo {
    font-size: 1.05rem;
  }
  .mobile-menu-btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    margin-left: auto;
  }
  .nav {
    display: none;
    width: 100%;
    flex-direction: column;
    align-items: stretch;
    gap: 0.5rem;
    padding-top: 0.5rem;
  }
  .nav.open {
    display: flex;
  }
  .theme-btn {
    margin-right: 0;
    align-self: flex-end;
  }
  .menu {
    width: 100%;
  }
  .menu :deep(.el-menu) {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    border-bottom: none;
  }
  .menu :deep(.el-menu-item) {
    width: 100%;
    height: 42px;
    line-height: 42px;
    padding: 0 12px !important;
  }
  .auth-group {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
    flex-wrap: wrap;
  }
}
</style>
