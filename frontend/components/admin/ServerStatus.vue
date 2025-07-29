<script setup lang="ts">
const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/admin/status`

const refreshDelay = 1000 // ms

import { reactive, computed, onMounted, onUnmounted } from 'vue'

const status = reactive({
  status: 'unknown',
  uptime: 0,
  activeThreads: 0,
  freeMemory: 0,
  totalMemory: 0,
  maxMemory: 0,
})

const { error, pending, refresh } = await useFetch(url, {
  method: 'GET',
  credentials: 'include',
  default: () => ({ ...status }),
  immediate: true,
  onResponse({ response }) {
    if (response._data) {
      Object.assign(status, response._data)
    }
  }
})

function formatUptime(ms: number) {
  const seconds = Math.floor(ms / 1000);
  const d = Math.floor(seconds / (3600*24));
  const h = Math.floor((seconds % (3600*24)) / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = Math.floor(seconds % 60);
  return `${d}d ${h}h ${m}m ${s}s`;
}

// Calculate percentage used memory relative to maxMemory
const usedMemoryPercent = computed(() => {
  if (!status) return 0;
  const used = status.totalMemory - status.freeMemory;
  return Math.min(100, ((used / status.maxMemory) * 100));
});

// Periodically refresh server status
onMounted(() => {
  const interval = setInterval(async () => {
    const res = await $fetch(url, { credentials: 'include' })
    Object.assign(status, res)
  }, refreshDelay)
  onUnmounted(() => clearInterval(interval))
})
</script>

<template>
  <div class="max-w-3xl mx-auto p-6 rounded-lg shadow-lg server-status-bg">
    <h2 class="text-3xl font-semibold mb-6 text-center text-white">Server Status</h2>

    <div v-if="pending" class="text-center text-blue-300 font-medium">Loading...</div>
    <div v-else-if="error" class="text-center text-red-400 font-semibold">
      Error loading server status.
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-6">
      <div class="p-4 bg-gray-800 rounded-lg shadow-sm border border-gray-700">
        <h3 class="text-xl font-semibold mb-2 primary-color">Uptime</h3>
        <p class="text-gray-100 text-lg">{{ formatUptime(status.uptime) }}</p>
      </div>
      <div class="p-4 bg-gray-800 rounded-lg shadow-sm border border-gray-700">
        <h3 class="text-xl font-semibold mb-2 primary-color">Active Threads</h3>
        <p class="text-gray-100 text-lg">{{ status.activeThreads }}</p>
      </div>
      <div class="p-4 bg-gray-800 rounded-lg shadow-sm border border-gray-700">
        <h3 class="text-xl font-semibold mb-2 primary-color">Memory Usage</h3>
        <ul class="text-gray-200 space-y-1 mb-4">
          <li><strong>Free:</strong> {{ (status.freeMemory / (1024 * 1024)).toFixed(2) }} MB</li>
          <li><strong>Total:</strong> {{ (status.totalMemory / (1024 * 1024)).toFixed(2) }} MB</li>
          <li><strong>Max:</strong> {{ (status.maxMemory / (1024 * 1024)).toFixed(2) }} MB</li>
        </ul>
        <div class="w-full bg-gray-700 rounded-full h-6 overflow-hidden">
          <div
            class="memory-pill h-6 text-white text-sm font-semibold flex items-center justify-center transition-width duration-500 ease-in-out"
            :style="{ width: usedMemoryPercent + '%' }"
          >
            {{ usedMemoryPercent.toFixed(1) }}%
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.server-status-bg {
  background: #0f172b;
}
.primary-color {
  color: #00DC82;
}
.memory-pill {
  background: #00DC82;
}
</style>
