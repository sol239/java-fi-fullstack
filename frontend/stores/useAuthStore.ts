import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    user: null as null | { name: string; },
    userRoles: [] as string[],
  }),
  actions: {
    login(userInfo: { name: string;}) {
      this.isLoggedIn = true
      this.user = userInfo
      this.userRoles = ['user']
    },
    logout() {
      this.isLoggedIn = false
      this.user = null
      this.userRoles = []
    },
    reset() {
      this.isLoggedIn = false
      this.user = null
      this.userRoles = []
    }
  }
})

