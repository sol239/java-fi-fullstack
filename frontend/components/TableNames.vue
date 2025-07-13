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
      <!-- Button for selecting a table to display -->
      <div class="max-h-64 overflow-y-auto space-y-2 p-4 border rounded">
        <UButton
          v-for="item in tableNamesStore.tableNameList.map(name => ({ label: name, id: name }))"
          :key="item.id"
          :color="selectedTableStore.selectedTable === item.id ? 'primary' : 'gray'"
          variant="subtle"
          class="w-full"
          @click="selectedTableStore.setSelectedTable(item.id)"
        >
          {{ item.label }}
        </UButton>
      </div>
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
import { useTableNamesStore } from '~/stores/tableNames'
import { useSelectedTableStore } from '~/stores/selectedTable' // <-- import the store

const { data: tableNames, pending, error, refresh } = await useFetch('/api/tables', {
  default: () => [],
  transform: (data) => data || []
})

console.log(tableNames)         // This is a ref object, not the array
console.log(tableNames.value)   // This is the array of table names

const tableNamesStore = useTableNamesStore()
const selectedTableStore = useSelectedTableStore() // <-- use the store

const tableNamesArray = Array.isArray(tableNames.value)
  ? tableNames.value.map(String)
  : []

tableNamesStore.setTableNames(tableNamesArray)

</script>
