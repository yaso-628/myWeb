import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const THEME_KEY = 'myweb_theme'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem(THEME_KEY) === 'dark')

  function toggle() {
    isDark.value = !isDark.value
  }

  function setDark(val) {
    isDark.value = !!val
  }

  watch(isDark, (v) => {
    localStorage.setItem(THEME_KEY, v ? 'dark' : 'light')
    document.documentElement.classList.toggle('dark', v)
  }, { immediate: true })

  return { isDark, toggle, setDark }
})
