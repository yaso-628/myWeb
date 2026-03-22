<template>
  <div class="music-player" :class="{ expanded: expanded, dark: isDark }">
    <button class="toggle-btn" @click="expanded = !expanded" title="音乐">
      <span v-if="playing" class="icon">🎵</span>
      <span v-else class="icon muted">🔇</span>
    </button>
    <Transition name="slide">
      <div v-show="expanded" class="panel">
        <div class="visualizer" :class="{ active: playing }">
          <span v-for="i in 4" :key="i" class="bar"></span>
        </div>
        <div class="controls">
          <button class="ctrl" @click="togglePlay" :disabled="!currentUrl">
            {{ playing ? '⏸' : '▶' }}
          </button>
          <label class="vol-wrap">
            <span>音量</span>
            <input v-model.number="volume" type="range" min="0" max="100" @input="onVolumeChange" />
          </label>
        </div>
        <div class="playlist-wrap">
          <div class="playlist-header">音乐列表</div>
          <div v-if="loading" class="playlist-hint">加载中...</div>
          <div v-else-if="!playlist.length" class="playlist-hint">暂无歌曲，管理员可在后台添加</div>
          <ul v-else class="playlist">
            <li
              v-for="(item, idx) in playlist"
              :key="item.id"
              class="playlist-item"
              :class="{ active: currentIndex === idx }"
              @click="playAt(idx)"
            >
              <span class="num">{{ idx + 1 }}</span>
              <span class="info">{{ item.title }} - {{ item.artist || '未知' }}</span>
            </li>
          </ul>
        </div>
      </div>
    </Transition>
    <audio
      ref="audioEl"
      @ended="onEnded"
      @play="playing = true"
      @pause="playing = false"
      @error="onError"
    ></audio>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { musicApi } from '@/api'
import { resolveAssetUrl } from '@/utils/asset'
import { useThemeStore } from '@/store/theme'

const audioEl = ref(null)
const playing = ref(false)
const expanded = ref(false)
const volume = ref(60)
const playlist = ref([])
const currentIndex = ref(0)
const loading = ref(false)

const themeStore = useThemeStore()
const isDark = computed(() => themeStore.isDark)

const currentUrl = computed(() => {
  const item = playlist.value[currentIndex.value]
  return item ? resolveAssetUrl(item.url) : ''
})

async function loadPlaylist() {
  loading.value = true
  try {
    const res = await musicApi.list()
    playlist.value = res?.data?.data || []
    if (playlist.value.length && !audioEl.value?.src) {
      currentIndex.value = 0
      setAudioSrc()
    }
  } catch (_) {
    playlist.value = []
  } finally {
    loading.value = false
  }
}

function setAudioSrc() {
  const url = currentUrl.value
  if (url && audioEl.value) {
    audioEl.value.src = url
    audioEl.value.volume = volume.value / 100
  }
}

function playAt(idx) {
  if (idx === currentIndex.value) {
    togglePlay()
    return
  }
  currentIndex.value = idx
  setAudioSrc()
  audioEl.value?.play().catch(() => {})
}

function togglePlay() {
  if (!audioEl.value || !currentUrl.value) return
  if (playing.value) {
    audioEl.value.pause()
  } else {
    audioEl.value.volume = volume.value / 100
    audioEl.value.play().catch(() => {})
  }
}

function onVolumeChange() {
  if (audioEl.value) audioEl.value.volume = volume.value / 100
}

function onEnded() {
  // 播放下一首
  if (playlist.value.length > 1) {
    const next = (currentIndex.value + 1) % playlist.value.length
    currentIndex.value = next
    setAudioSrc()
    audioEl.value?.play().catch(() => {})
  }
}

function onError() {
  playing.value = false
}

function tryAutoPlay() {
  const consented = sessionStorage.getItem('bgm_consent')
  if (consented === '1' && currentUrl.value && audioEl.value) {
    audioEl.value.volume = volume.value / 100
    audioEl.value.play().catch(() => {})
  }
}

watch(currentUrl, (url) => {
  if (url && audioEl.value) {
    audioEl.value.src = url
    tryAutoPlay()
  }
})

onMounted(async () => {
  await loadPlaylist()
  if (audioEl.value) {
    audioEl.value.volume = volume.value / 100
  }
  document.addEventListener('click', tryAutoPlay, { once: true })
})

defineExpose({
  play() {
    if (currentUrl.value && audioEl.value) {
      audioEl.value.volume = volume.value / 100
      audioEl.value.play().catch(() => {})
    }
  },
  refreshPlaylist: loadPlaylist
})
</script>

<style scoped>
.music-player {
  position: fixed;
  bottom: 90px;
  right: 24px;
  z-index: 997;
}
.toggle-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: radial-gradient(circle at 30% 0%, rgba(255, 255, 255, 0.98), rgba(255, 255, 255, 0.8));
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.7);
  font-size: 1.25rem;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}
.toggle-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 4px 16px var(--color-glow);
}
.icon.muted {
  opacity: 0.6;
}
.panel {
  position: absolute;
  bottom: 52px;
  right: 0;
  background: radial-gradient(circle at 0 0, rgba(15, 23, 42, 0.94), rgba(30, 64, 175, 0.92));
  backdrop-filter: blur(20px);
  border: 1px solid rgba(96, 165, 250, 0.6);
  border-radius: 12px;
  padding: 0.75rem 1rem;
  min-width: 200px;
  max-width: 280px;
  max-height: 320px;
  box-shadow: 0 0 25px rgba(37, 99, 235, 0.55);
  display: flex;
  flex-direction: column;
}
.music-player.dark .panel {
  background: radial-gradient(circle at 0 0, rgba(15, 23, 42, 0.96), rgba(30, 64, 175, 0.98));
  border-color: rgba(129, 140, 248, 0.85);
  box-shadow: 0 0 30px rgba(59, 130, 246, 0.7);
}
.controls {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--color-border);
}
.music-player.dark .controls {
  border-bottom-color: rgba(75, 85, 99, 0.6);
}
.ctrl {
  padding: 0.35rem 0.75rem;
  border-radius: 6px;
  background: var(--color-primary);
  color: #fff;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}
.ctrl:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.vol-wrap {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
.vol-wrap input {
  flex: 1;
  width: 60px;
}
.playlist-wrap {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.playlist-header {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-text-muted);
  margin-bottom: 0.5rem;
}
.playlist-hint {
  font-size: 0.75rem;
  color: var(--color-text-muted);
  padding: 0.5rem 0;
}
.playlist {
  list-style: none;
  margin: 0;
  padding: 0;
  overflow-y: auto;
  max-height: 200px;
}
.playlist-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.4rem 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: background 0.2s;
}
.playlist-item:hover {
  background: rgba(37, 99, 235, 0.1);
}
.music-player.dark .playlist-item:hover {
  background: rgba(96, 165, 250, 0.15);
}
.playlist-item.active {
  background: linear-gradient(90deg, rgba(59, 130, 246, 0.45), rgba(147, 197, 253, 0.22));
  color: #f9fafb;
}
.music-player.dark .playlist-item.active {
  background: rgba(96, 165, 250, 0.25);
}
.playlist-item .num {
  flex-shrink: 0;
  width: 1.5em;
  color: var(--color-text-muted);
  font-weight: 500;
}
.playlist-item.active .num {
  color: var(--color-primary);
}
.playlist-item .info {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.visualizer {
  display: flex;
  align-items: flex-end;
  gap: 3px;
  height: 18px;
  margin-bottom: 0.5rem;
  opacity: 0.4;
  transition: opacity 0.2s ease;
}
.visualizer.active {
  opacity: 1;
}
.visualizer .bar {
  width: 3px;
  border-radius: 999px;
  background: linear-gradient(to top, rgba(59, 130, 246, 0.9), rgba(191, 219, 254, 0.9));
  transform-origin: bottom;
  animation: eq-bar 1s ease-in-out infinite;
}
.visualizer .bar:nth-child(2) {
  animation-delay: -0.2s;
}
.visualizer .bar:nth-child(3) {
  animation-delay: -0.4s;
}
.visualizer .bar:nth-child(4) {
  animation-delay: -0.6s;
}
@keyframes eq-bar {
  0%, 100% {
    transform: scaleY(0.3);
  }
  50% {
    transform: scaleY(1);
  }
}
.slide-enter-active,
.slide-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.slide-enter-from,
.slide-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
.dark .toggle-btn {
  background: radial-gradient(circle at 30% 0%, rgba(15, 23, 42, 0.96), rgba(15, 23, 42, 0.85));
  border-color: rgba(129, 140, 248, 0.85);
}
</style>
