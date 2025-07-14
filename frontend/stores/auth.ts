import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    user: null as null | { name: string; }
  }),
  actions: {
    login(userInfo: { name: string;}) {
      this.isLoggedIn = true
      this.user = userInfo
    },
    logout() {
      this.isLoggedIn = false
      this.user = null
    }
  }
})
