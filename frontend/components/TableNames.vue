<template>
  <UCard>
    <template #header>
      <h2 class="text-xl font-semibold">Database Tables</h2>
    </template>

    <div v-if="pending" class="flex items-center justify-center py-4">
      <UIcon name="i-heroicons-arrow-path" class="animate-spin mr-2" />
      <span>Loading tables...</span>
    </div>

    <UAlert
      v-else-if="error"
      icon="i-heroicons-exclamation-triangle"
      color="red"
      variant="soft"
      :title="error"
    />

    <div v-else-if="!tableNames.length" class="text-center py-4 text-gray-500">
      <UIcon name="i-heroicons-table-cells" class="text-3xl mb-2" />
      <p>No tables found in the database</p>
    </div>

    <div v-else>
      <UBadge variant="soft" class="mb-3">
        {{ tableNames.length }} table{{ tableNames.length !== 1 ? 's' : '' }} found
      </UBadge>
      <URadioGroup
        v-model="selectedTable"
        :items="tableNamesStore.tableNameList.map(name => ({ label: name, value: name }))"
        class="mb-4"
      />
    </div>

    <template #footer>
      <UButton
        variant="ghost"
        icon="i-heroicons-arrow-path"
        @click="refresh"
        :loading="pending"
        size="sm"
      >
        Refresh
      </UButton>
    </template>
  </UCard>
</template>

<script setup>
import { ref } from 'vue'
import { useTableNamesStore } from '~/stores/tableNames'

const { data: tableNames, pending, error, refresh } = await useFetch('/api/tables', {
  default: () => [],
  transform: (data) => data || []
})

console.log(tableNames)         // This is a ref object, not the array
console.log(tableNames.value)   // This is the array of table names

const tableNamesStore = useTableNamesStore()

const tableNamesArray = Array.isArray(tableNames.value)
  ? tableNames.value.map(String)
  : []

tableNamesStore.setTableNames(tableNamesArray)

const selectedTable = ref(null)
</script>
