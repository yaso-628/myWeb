import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const TOKEN_KEY = 'myweb_token'
const USER_KEY = 'myweb_user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref(JSON.parse(localStorage.getItem(USER_KEY) || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)

  function setToken(t) {
    token.value = t
    localStorage.setItem(TOKEN_KEY, t)
  }

  function setUser(u) {
    user.value = u
    localStorage.setItem(USER_KEY, JSON.stringify(u))
  }

  function clear() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, isLoggedIn, isAdmin, setToken, setUser, clear }
})
