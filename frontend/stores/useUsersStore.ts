import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '~/entity/User'

export const useUsersStore = defineStore('users', () => {
  const users = ref<User[]>([])

  function setUsers(newUsers: User[]) {
    users.value = newUsers
  }

  return { users, setUsers }
})