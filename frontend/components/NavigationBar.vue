<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useAuthStore } from '~/stores/useAuthStore'
import type { NavigationMenuItem } from '@nuxt/ui';

const authStore = useAuthStore()

// --- Navigation Items ---
const navItems: NavigationMenuItem[] = [
  {
    label: 'Home',
    icon: 'i-heroicons-home',
    to: '/',
    data_target: 'home',
  },
  {
    label: 'Charts',
    icon: 'i-heroicons-chart-bar',
    to: '/charts',
    data_target: 'charts',
  },
  {
    label: 'Admin',
    icon: 'i-heroicons-wrench-screwdriver',
    to: '/admin',
    data_target: 'admin',
    adminOnly: true, // custom flag
  },
];

const filteredNavItems = computed(() =>
  navItems.filter(
    item => !item.adminOnly || (authStore.userRoles && authStore.userRoles.includes('ADMIN'))
  )
)
</script>


<template>
  <UNavigationMenu :items="filteredNavItems" class="w-full justify-end" />
</template>
