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
import { useAuthStore } from '~/stores/useAuthStore'
import { useUsersStore } from '#imports'

const authStore = useAuthStore()
const usersStore = useUsersStore()
authStore.reset()

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/logout`

function printUser() {
  if (authStore.isLoggedIn) {
    console.log('Current User:', authStore.user)
    console.log('User Roles:', authStore.userRoles)
  } else {
    console.log('No user is logged in.')
  }
}

async function logout() {
  console.log('Logging out...')
  
  try {
    // First, call the server logout endpoint
    const response = await fetch(url, {
      method: 'POST',
      credentials: 'include'
    })
    
    if (!response.ok) {
      console.warn('Server logout failed, but continuing with client logout')
    }
    
    // Clear client-side auth state
    authStore.logout()
    
    // Force clear any authentication cookies client-side
    document.cookie.split(";").forEach(function(c) { 
      document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/"); 
    });
    
    // Optional: Force a page refresh to ensure clean state
    await navigateTo('/', { replace: true })

    usersStore.reset() // Reset users store if needed
    
    console.log('Logout completed')

  } catch (err) {
    console.error('Logout failed:', err)
    // Even if server logout fails, clear client state
    authStore.logout()
  }
}
</script>
