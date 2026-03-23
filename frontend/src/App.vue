<template>
  <div id="app">
    <DynamicBackground v-if="!isAdminLayout" />
    <SakuraParticles v-if="!isAdminLayout" />
    <ClickBurstParticles v-if="!isAdminLayout" />
    <WelcomeOverlay v-if="!isAdminLayout" @enter="onWelcomeEnter" />
    <Header v-if="!isAdminLayout" />
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <Footer v-if="!isAdminLayout" />
    <BackToTop v-if="!isAdminLayout" />
    <FunWidget v-if="!isAdminLayout" />
    <ChibiCorner v-if="!isAdminLayout" />
    <MusicPlayer v-if="!isAdminLayout" ref="musicPlayerRef" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import BackToTop from '@/components/BackToTop.vue'
import FunWidget from '@/components/FunWidget.vue'
import ChibiCorner from '@/components/ChibiCorner.vue'
import DynamicBackground from '@/components/DynamicBackground.vue'
import SakuraParticles from '@/components/SakuraParticles.vue'
import ClickBurstParticles from '@/components/ClickBurstParticles.vue'
import MusicPlayer from '@/components/MusicPlayer.vue'
import WelcomeOverlay from '@/components/WelcomeOverlay.vue'

const route = useRoute()
const musicPlayerRef = ref(null)
const isAdminLayout = computed(() => route.path.startsWith('/admin'))

function onWelcomeEnter() {
  musicPlayerRef.value?.play?.()
}
</script>

<style scoped>
#app {
  position: relative;
  z-index: 1;
}

.main-content {
  min-height: calc(100vh - 120px);
  padding: 2.5rem 1.5rem 3rem;
  /* 使用纯背景图，不再覆盖朦胧渐变 */
  background: transparent;
  backdrop-filter: none;
  position: relative;
  z-index: 1;
}
.page-enter-active,
.page-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

@media (max-width: 768px) {
  .main-content {
    padding: 1.25rem 0.75rem 1.5rem;
  }
}
</style>
