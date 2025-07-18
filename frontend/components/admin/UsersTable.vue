<script setup lang="ts">
import { ref, computed } from 'vue'
import type { TableColumn, DropdownMenuItem } from '@nuxt/ui'
import { useClipboard } from '@vueuse/core'
import { User } from '~/entity/User'
import { useUsersStore } from '#imports'
import ResetPasswordForm from '@/components/admin/ResetPasswordForm.vue'

const usersStore = useUsersStore()
const { users } = storeToRefs(usersStore)

const { copy } = useClipboard()
const toast = useToast()

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/users`

const data = computed(() => users.value)

async function fetchUsers() {
  try {
    const { data: fetchData, error: fetchError } = await useFetch(url, {
      method: 'GET',
      credentials: 'include'
    });

    if (fetchError.value) throw fetchError.value;

    // Map raw data to User instances
    const usersArr = (fetchData.value as any[]).map((u) =>
      new User(
        u.id,
        u.username,
        u.password,
        u.enabled,
        typeof u.roles === 'string' ? u.roles.split(',') : u.roles
      )
    );
    usersStore.setUsers(usersArr);

    console.log('User data:', usersArr);
  } catch (error) {
    console.error('Error fetching users:', error);
    toast.add({
      title: 'Failed to fetch users',
      color: 'error',
      icon: 'i-lucide-alert-triangle'
    });
  }
}

fetchUsers();



const columns: TableColumn<User>[] = [
  { accessorKey: 'id', header: 'ID' },
  { accessorKey: 'username', header: 'Username' },
  { accessorKey: 'enabled', header: 'Enabled' },
  { accessorKey: 'roles', header: 'Roles' },
  { id: 'resetPassword', header: 'Reset Password' }, // new column
  { id: 'action' }
]

function getDropdownActions(user: User): DropdownMenuItem[] {
  const actions: DropdownMenuItem[] = [
    {
      label: 'Copy user Id',
      icon: 'i-lucide-copy',
      onSelect: () => {
        copy(user.id.toString())
        toast.add({
          title: 'User ID copied to clipboard!',
          color: 'success',
          icon: 'i-lucide-circle-check'
        })
      }
    }
  ]

  if (!user.roles.includes('ADMIN')) {
    actions.push(
      {
        label: user.enabled ? 'Disable' : 'Enable',
        icon: 'i-lucide-power',
        onSelect: async () => {
          const newStatus = !user.enabled
          const config = useRuntimeConfig()
          const backendBase = config.public.backendBase
          const url = `${backendBase}/api/users/${user.id}/${newStatus ? 'enable' : 'disable'}`

          try {
            await $fetch(url, {
              method: 'POST',
              credentials: 'include'
            })
            user.enabled = newStatus
            toast.add({
              title: `User ${newStatus ? 'enabled' : 'disabled'} successfully`,
              color: 'success',
              icon: 'i-lucide-circle-check'
            })
          } catch (error) {
            toast.add({
              title: `Failed to ${newStatus ? 'enable' : 'disable'} user`,
              color: 'error',
              icon: 'i-lucide-alert-triangle'
            })
          }
        }
      },
      {
        label: 'Delete',
        icon: 'i-lucide-trash',
        color: 'error',
        onSelect: async () => {
          const config = useRuntimeConfig()
          const backendBase = config.public.backendBase
          const url = `${backendBase}/api/users/delete/${user.id}`

          try {
            await $fetch(url, {
              method: 'DELETE',
              credentials: 'include'
            })
            usersStore.deleteUser(user.id)
            console.log("DELETE USER:", user.id )
            toast.add({
              title: 'User deleted successfully',
              color: 'success',
              icon: 'i-lucide-circle-check'
            })
            
            console.log("USER STORE AFTER DELETE:", usersStore.users)

          } catch (error) {
            toast.add({
              title: 'Failed to delete user',
              color: 'error',
              icon: 'i-lucide-alert-triangle'
            })
          }
        }
      }
    )
  }

  return actions
}
</script>

<template>
  <UTable :data="data" :columns="columns" class="flex-1">
    <template #username-cell="{ row }">
      <div class="flex items-center gap-3">
        <UAvatar size="lg" :alt="`${row.original.username} avatar`" />
        <div>
          <p class="font-medium text-highlighted">
            {{ row.original.username }}
          </p>
        </div>
      </div>
    </template>
    <template #enabled-cell="{ row }">
      <UBadge :color="row.original.enabled ? 'success' : 'error'">
        {{ row.original.enabled ? 'Enabled' : 'Disabled' }}
      </UBadge>
    </template>
    <template #roles-cell="{ row }">
      <div class="flex flex-wrap gap-1">
        <UBadge :color="row.original.enabled ? 'success' : 'error'" v-for="role in row.original.roles" :key="role">
          {{ role }}
        </UBadge>
      </div>
    </template>
    <template #resetPassword-cell="{ row }">
      <UPopover>
        <UButton variant="outline">Reset Password</UButton>
        <template #content>
          <ResetPasswordForm :userId="row.original.id" />
        </template>
      </UPopover>
    </template>
    <template #action-cell="{ row }">
      <UDropdownMenu :items="getDropdownActions(row.original)">
        <UButton icon="i-lucide-ellipsis-vertical" color="neutral" variant="ghost" aria-label="Actions" />
      </UDropdownMenu>
    </template>
  </UTable>
</template>