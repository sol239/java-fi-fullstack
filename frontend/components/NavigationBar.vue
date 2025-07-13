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
];

</script>


<template>
    <UNavigationMenu :items="navItems" class="w-full justify-end" />
</template>
