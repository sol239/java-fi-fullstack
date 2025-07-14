<script setup lang="ts">
import { ref } from 'vue'
import type { TableColumn, DropdownMenuItem } from '@nuxt/ui'
import { useClipboard } from '@vueuse/core'
import { User } from '~/entity/User'

const { copy } = useClipboard()
const toast = useToast()

// TODO: Fetch real user data from backend
const user1: User = new User(1, 'john_doe', 'password123', true, ['ROLE_ADMIN', 'ROLE_USER'])
const user2: User = new User(2, 'jane_doe', 'securepassword', true, ['ROLE_USER'])
const user3: User = new User(3, 'alice_smith', 'mypassword', false, ['ROLE_USER'])
const user4: User = new User(4, 'bob_jones', 'anotherpassword', true, ['ROLE_USER'])
const users: User[] = [user1, user2, user3, user4]

const data = ref(users)

const columns: TableColumn<User>[] = [
  { accessorKey: 'id', header: 'ID' },
  { accessorKey: 'username', header: 'Username' },
  { accessorKey: 'enabled', header: 'Enabled' },
  { accessorKey: 'roles', header: 'Roles' },
  { id: 'action' }
]

function getDropdownActions(user: User): DropdownMenuItem[][] {
  return [
    [
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
    ],
    [
      {
        label: 'Edit',
        icon: 'i-lucide-edit'
      },
      {
        label: 'Delete',
        icon: 'i-lucide-trash',
        color: 'error'
      }
    ]
  ]
}
</script>

<template>
  <UTable :data="data" :columns="columns" class="flex-1">
    <template #username-cell="{ row }">
      <div class="flex items-center gap-3">
        <UAvatar
          size="lg"
          :alt="`${row.original.username} avatar`"
        />
        <div>
          <p class="font-medium text-highlighted">
            {{ row.original.username }}
          </p>
          <p class="text-xs text-gray-500">
            {{ row.original.password }}
          </p>
        </div>
      </div>
    </template>
    <template #enabled-cell="{ row }">
      <UBadge :color="row.original.enabled ? 'green' : 'red'">
        {{ row.original.enabled ? 'Enabled' : 'Disabled' }}
      </UBadge>
    </template>
    <template #roles-cell="{ row }">
      <div class="flex flex-wrap gap-1">
        <UBadge v-for="role in row.original.roles" :key="role" color="gray">
          {{ role }}
        </UBadge>
      </div>
    </template>
    <template #action-cell="{ row }">
      <UDropdownMenu :items="getDropdownActions(row.original)">
        <UButton
          icon="i-lucide-ellipsis-vertical"
          color="neutral"
          variant="ghost"
          aria-label="Actions"
        />
      </UDropdownMenu>
    </template>
  </UTable>
</template>