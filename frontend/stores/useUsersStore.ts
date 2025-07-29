import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '~/entity/User'

export const useUsersStore = defineStore('users', () => {
  const users = ref<User[]>([])

  function setUsers(newUsers: User[]) {
    users.value = newUsers
  }

  function deleteUser(userId: string) {
    for (let i = 0; i < users.value.length; i++) {
      if (users.value[i].id === Number(userId)) {
        users.value.splice(i, 1)
        break
      }
    }
  }

  function reset() {
    users.value = []
  }

  return { users, setUsers, deleteUser, reset }
})