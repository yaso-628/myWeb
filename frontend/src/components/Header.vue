<template>
  <header class="bits2d-navbar">
    <div class="bits2d-navbar__panel">
      <router-link
        to="/"
        class="bits2d-navbar__logo"
        @click="mobileMenuOpen = false"
      >
        {{ siteName || '个人网站' }}
      </router-link>

      <button class="bits2d-mobile-btn" type="button" @click="mobileMenuOpen = !mobileMenuOpen">
        {{ mobileMenuOpen ? '关闭' : '菜单' }}
      </button>

      <div class="bits2d-navbar__menu" :class="{ 'is-open': mobileMenuOpen }">
        <div class="bits2d-navbar__tools">
          <TopClock />
          <div class="bits2d-navbar__theme">
            <!-- 主题切换：用 NeButton 保持霓虹动效 -->
            <UiButton
              class="theme-toggle-btn"
              :title="theme.isDark ? '浅色模式' : '深色模式'"
              color="primary"
              plain
              hoverable
              roundedLg
              v-ripple
              @click="theme.toggle()"
            >
              {{ theme.isDark ? '☀️' : '🌙' }}
            </UiButton>
          </div>
        </div>

        <nav class="bits2d-navbar__nav">
          <router-link to="/" class="bits2d-navlink" @click="mobileMenuOpen = false">首页</router-link>
          <router-link to="/blog" class="bits2d-navlink" @click="mobileMenuOpen = false">博客</router-link>
          <router-link to="/comment" class="bits2d-navlink" @click="mobileMenuOpen = false">留言板</router-link>
          <router-link to="/link" class="bits2d-navlink" @click="mobileMenuOpen = false">友链</router-link>
          <router-link to="/profile" class="bits2d-navlink" @click="mobileMenuOpen = false">简介</router-link>
        </nav>

        <div class="bits2d-navbar__auth">
          <template v-if="userStore.isLoggedIn">
            <div class="bits2d-dropdown" ref="dropdownRef">
              <button
                type="button"
                class="bits2d-dropdown__trigger"
                @click="dropdownOpen = !dropdownOpen"
              >
                {{ userStore.user?.nickname || userStore.user?.username || '用户' }}
              </button>

              <Transition name="bits2d-dropdown">
                <div v-if="dropdownOpen" class="bits2d-dropdown__menu">
                  <button type="button" class="bits2d-dropdown__item" @click="go('/me')">个人信息</button>
                  <button
                    v-if="userStore.isAdmin"
                    type="button"
                    class="bits2d-dropdown__item"
                    @click="go('/admin')"
                  >
                    后台管理
                  </button>
                  <div class="bits2d-dropdown__divider" />
                  <button
                    type="button"
                    class="bits2d-dropdown__item bits2d-dropdown__item--danger"
                    @click="logout"
                  >
                    退出登录
                  </button>
                </div>
              </Transition>
            </div>
          </template>

          <template v-else>
            <button type="button" class="btn-primary-solid" v-ripple @click="go('/login')">登录</button>
            <button type="button" class="btn-primary-solid" v-ripple @click="go('/register')">注册</button>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useThemeStore } from '@/store/theme'
import { profileApi } from '@/api'
import TopClock from '@/components/TopClock.vue'
import { UiButton } from '@vuebits/ui'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const theme = useThemeStore()
const siteName = ref('')
const mobileMenuOpen = ref(false)
const dropdownOpen = ref(false)
const dropdownRef = ref(null)

onMounted(async () => {
  try {
    const res = await profileApi.getByKey('site_name')
    siteName.value = res?.data?.data?.value || ''
  } catch (_) {}
})

function logout() {
  userStore.clear()
  mobileMenuOpen.value = false
  dropdownOpen.value = false
  router.push('/')
}

watch(() => route.fullPath, () => {
  mobileMenuOpen.value = false
  dropdownOpen.value = false
})

function go(path) {
  dropdownOpen.value = false
  mobileMenuOpen.value = false
  router.push(path)
}

onMounted(() => {
  // 点击外部关闭下拉（轻量监听一次）
  document.addEventListener('click', onDocClick)
})

function onDocClick(e) {
  const el = dropdownRef.value
  if (!el) return
  if (e.target instanceof Node && !el.contains(e.target)) {
    dropdownOpen.value = false
  }
}

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocClick)
})
</script>

<style scoped>
/* 下拉菜单：二次元霓虹小组件 */
.bits2d-dropdown {
  position: relative;
}

.bits2d-dropdown__trigger {
  border: 1px solid rgba(229, 231, 235, 0.85);
  background: rgba(255, 255, 255, 0.6);
  color: rgba(255, 255, 255, 0.95);
  border-radius: 999px;
  padding: 0.35rem 0.75rem;
  cursor: pointer;
  transition: transform 160ms ease, box-shadow 160ms ease;
}

.dark .bits2d-dropdown__trigger {
  background: rgba(2, 6, 23, 0.28);
  border-color: rgba(51, 65, 85, 0.75);
}

.bits2d-dropdown__trigger:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 26px rgba(56, 189, 248, 0.18);
}

.bits2d-dropdown__menu {
  position: absolute;
  right: 0;
  top: calc(100% + 10px);
  min-width: 180px;
  border-radius: 14px;
  padding: 0.6rem;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(229, 231, 235, 0.85);
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.15);
  backdrop-filter: blur(12px);
  animation: none;
  transform-origin: top right;
}

.dark .bits2d-dropdown__menu {
  background: rgba(31, 41, 55, 0.78);
  border-color: rgba(51, 65, 85, 0.75);
}

.bits2d-dropdown__item {
  width: 100%;
  text-align: left;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: 0.55rem 0.6rem;
  border-radius: 10px;
  color: var(--color-text);
  transition: background 160ms ease, transform 160ms ease;
}

.bits2d-dropdown__item:hover {
  background: rgba(37, 99, 235, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 0 22px rgba(56, 189, 248, 0.18);
}

.bits2d-dropdown__item--danger {
  color: #dc2626;
}

.bits2d-dropdown__divider {
  height: 1px;
  background: rgba(229, 231, 235, 0.85);
  margin: 0.45rem 0;
}

.dark .bits2d-dropdown__divider {
  background: rgba(51, 65, 85, 0.75);
}

/* 下拉菜单丝滑展开 */
.bits2d-dropdown-enter-active,
.bits2d-dropdown-leave-active {
  transition: opacity 180ms ease, transform 180ms ease;
}

.bits2d-dropdown-enter-from,
.bits2d-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

.theme-toggle-btn {
  min-width: 34px;
  min-height: 34px;
  width: 34px;
  height: 34px;
  padding: 0 !important;
  border-radius: 999px !important;
  background: transparent;
  border: none !important;
  font-size: 0.95rem;
  line-height: 34px;
  box-shadow: none !important;
}

.theme-toggle-btn:hover {
  background: rgba(37, 99, 235, 0.08) !important;
  box-shadow: none !important;
}

.bits2d-navbar__tools {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
</style>
