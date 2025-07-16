<template>
  <UApp>
    <!-- Production front page - requires login to use the application -->
    <!--
    <div v-if="authStore.isLoggedIn">
      <div class="flex items-center justify-between px-4 py-2 border-b border-gray-200">
        <NavigationBar />
    <UPopover>
      <UButton :label="authStore.isLoggedIn ? '' : 'Login'" color="primary" variant="subtle">
        <template v-if="authStore.isLoggedIn">
          <UIcon name="i-heroicons-user" class="w-5 h-5" />
        </template>
</UButton>
<template #content>
        <template v-if="!authStore.isLoggedIn">
          <LoginForm />
        </template>
<template v-else>
          <UBadge variant="soft" class="mb-2">
            Logged in as: {{ authStore.user?.name || 'Unknown' }}
          </UBadge>
          <UButton color="primary" variant="outline" @click="authStore.logout()">Logout</UButton>
        </template>
</template>
</UPopover>
</div>
<NuxtPage />
</div>
<div v-else>
  <div class="flex items-center justify-center min-h-screen">
    <UCard>
      <UCardHeader>
        <h2 class="text-xl font-semibold">Welcome to the App</h2>
        <p class="text-gray-600 mb-2">Please log in to continue</p>
      </UCardHeader>
      <LoginForm />
    </UCard>
  </div>
</div>-->

    <!-- Developer front page  -->
    <div class="flex items-center justify-between px-4 py-2 border-b border-gray-200">
      <NavigationBar />
            <UButton color="primary" variant="outline" @click="printUser">Print User</UButton>

      <UPopover>
        <UButton :label="authStore.isLoggedIn ? '' : 'Login'" color="primary" variant="subtle">
          <template v-if="authStore.isLoggedIn">
            <UIcon name="i-heroicons-user" class="w-5 h-5" />
          </template>
        </UButton>
        <template #content>
          <template v-if="!authStore.isLoggedIn">
            <LoginForm />
          </template>
          <template v-else>
            <UBadge variant="soft" class="mb-2">
              Logged in as: {{ authStore.user?.name || 'Unknown' }}
            </UBadge>

            <UButton color="primary" variant="outline" @click="logout">Logout</UButton>

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
authStore.reset()

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/logout`

function printUser() {
  if (authStore.isLoggedIn) {
    console.log('Current User:', authStore.user)
  } else {
    console.log('No user is logged in.')
  }
}

async function logout() {
  authStore.reset()
  try {
    await fetch(url, {
      method: 'POST',
      credentials: 'include'
    })
    authStore.logout()
    window.location.reload() // přidej reload stránky
  } catch (err) {
    console.error('Logout failed:', err)
  }
}
</script>
