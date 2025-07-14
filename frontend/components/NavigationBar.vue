<script setup lang="ts">
import { ref, onMounted } from 'vue';
import type { NavigationMenuItem } from '@nuxt/ui';

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
    label: 'Demo',
    to: '/demo',
    data_target: 'demo',
  },
  {
    label: 'Admin',
    icon: 'i-heroicons-wrench-screwdriver',
    to: '/admin',
    data_target: 'admin',
  },];

</script>


<template>
  <UNavigationMenu :items="navItems" class="w-full justify-end" />
</template>
