<script setup lang="ts">
import { ref } from 'vue'
import type { TableColumn, DropdownMenuItem } from '@nuxt/ui'
import { useClipboard } from '@vueuse/core'
import { User } from '~/entity/User'

const { copy } = useClipboard()
const toast = useToast()

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/users`

const data = ref<User[]>([]);

async function fetchUsers() {
  try {
    const { data: fetchData, error: fetchError } = await useFetch(url, {
      method: 'GET',
      credentials: 'include'
    });

    if (fetchError.value) throw fetchError.value;

    // Map raw data to User instances
    const users = (fetchData.value as any[]).map((u) =>
      new User(
        u.id,
        u.username,
        u.password,
        u.enabled,
        typeof u.roles === 'string' ? u.roles.split(',') : u.roles
      )
    );
    data.value = users;

    console.log('User data:', users);
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
        <UAvatar size="lg" :alt="`${row.original.username} avatar`" />
        <div>
          <p class="font-medium text-highlighted">
            {{ row.original.username }}
          </p>
        </div>
      </div>
    </template>
    <template #enabled-cell="{ row }">
      <UBadge>
        {{ row.original.enabled ? 'Enabled' : 'Disabled' }}
      </UBadge>
    </template>
    <template #roles-cell="{ row }">
      <div class="flex flex-wrap gap-1">
        <UBadge v-for="role in row.original.roles" :key="role">
          {{ role }}
        </UBadge>
      </div>
    </template>
    <template #action-cell="{ row }">
      <UDropdownMenu :items="getDropdownActions(row.original)">
        <UButton icon="i-lucide-ellipsis-vertical" color="neutral" variant="ghost" aria-label="Actions" />
      </UDropdownMenu>
    </template>
  </UTable>
</template>