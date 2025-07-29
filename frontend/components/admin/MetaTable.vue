<script setup lang="ts">
import { ref, computed } from 'vue'
import type { TableColumn } from '@nuxt/ui'
import { useClipboard } from '@vueuse/core'
import { useToast } from '#imports'
import type { Chart } from '~/entity/Chart' // import as type only

const charts = ref<Chart[]>([])
const { copy } = useClipboard()
const toast = useToast()

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/tables/meta`

const data = computed(() => charts.value)

async function fetchCharts() {
  try {
    const { data: fetchData, error: fetchError } = await useFetch(url, {
      method: 'GET',
      credentials: 'include'
    });

    if (fetchError.value) throw fetchError.value;

    console.log('Fetched chart meta:', fetchData.value);    

    // Map raw data to Chart interface objects
    const chartsArr = (fetchData.value as any[]).map((c) => ({
      id: c.id,
      name: c.name,
      description: c.description,
      assetName: c.assetName,
      timeframe: c.timeframe
    }));
    charts.value = chartsArr;

    console.log('Chart meta:', chartsArr);
  } catch (error) {
    console.error('Error fetching charts:', error);
    toast.add({
      title: 'Failed to fetch tables',
      color: 'error',
      icon: 'i-lucide-alert-triangle'
    });
  }
}

fetchCharts();

const columns: TableColumn<Chart>[] = [
  { accessorKey: 'name', header: 'Table Name' },
  { accessorKey: 'description', header: 'Description' },
  { accessorKey: 'assetName', header: 'Asset Name' },
  { accessorKey: 'timeframe', header: 'Timeframe' },
  { id: 'action' }
]

function getDropdownActions(chart: Chart) {
  return [
    {
      label: 'Copy Table Name',
      icon: 'i-lucide-copy',
      onSelect: () => {
        copy(chart.name)
        toast.add({
          title: 'Table name copied to clipboard!',
          color: 'success',
          icon: 'i-lucide-circle-check'
        })
      }
    },
    {
      label: 'Delete Table',
      icon: 'i-lucide-trash-2',
      onSelect: async () => {
        try {
          await $fetch(`${backendBase}/api/tables/delete/${chart.name}`, {
            method: 'DELETE',
            credentials: 'include'
          })
          toast.add({
            title: 'Table deleted!',
            color: 'success',
            icon: 'i-lucide-trash-2'
          })
          fetchCharts()
        } catch (error) {
          toast.add({
            title: 'Failed to delete table',
            color: 'error',
            icon: 'i-lucide-alert-triangle'
          })
        }
      }
    },
    {
      label: 'Clean Table',
      icon: 'i-lucide-brush-cleaning',
      onSelect: async () => {
        try {
          await $fetch(`${backendBase}/api/tables/clean/${chart.name}`, {
            method: 'PATCH',
            credentials: 'include'
          })
          toast.add({
            title: 'Table cleaned!',
            color: 'success',
            icon: 'i-lucide-broom'
          })
        } catch (error) {
          toast.add({
            title: 'Failed to clean table',
            color: 'error',
            icon: 'i-lucide-alert-triangle'
          })
        }
      }
    }
  ]
}

</script>

<template>
  <UTable :data="data" :columns="columns" class="flex-1">
    <template #name-cell="{ row }">
      <span class="font-medium text-highlighted">
        {{ row.original.name }}
      </span>
    </template>
    <template #description-cell="{ row }">
      {{ row.original.description }}
    </template>
    <template #assetName-cell="{ row }">
      {{ row.original.assetName }}
    </template>
    <template #timeframe-cell="{ row }">
      {{ row.original.timeframe }}
    </template>
    <template #action-cell="{ row }">
      <UDropdownMenu :items="getDropdownActions(row.original)">
        <UButton icon="i-lucide-ellipsis-vertical" color="neutral" variant="ghost" aria-label="Actions" />
      </UDropdownMenu>
    </template>
  </UTable>
</template>