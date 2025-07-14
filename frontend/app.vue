<template>
  <UApp>
    <div class="flex items-center justify-between px-4 py-2 border-b border-gray-200">
      <NavigationBar />
      <!-- Popover Button -->
      <UPopover>
        <UButton
          :label="authStore.isLoggedIn && authStore.user?.name
            ? authStore.user.name.split(' ').map(n => n[0]).join('').toUpperCase()
            : 'Login'"
          color="primary"
          variant="subtle"
        />
        <template #content>
          <template v-if="!authStore.isLoggedIn">
            <LoginForm />
          </template>
          <template v-else>
            <UButton color="primary" variant="outline" @click="authStore.logout()">Logout</UButton>
          </template>
        </template>
      </UPopover>
    </div>

    <NuxtPage />
  </UApp>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
</script>
