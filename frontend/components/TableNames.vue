<template>
  <UCard>

    <div v-if="pending" class="flex items-center justify-center py-4">
      <UIcon name="i-heroicons-arrow-path" class="animate-spin mr-2" />
      <span>Loading tables...</span>
    </div>

    <UAlert v-else-if="error" icon="i-heroicons-exclamation-triangle" color="red" variant="soft" :title="error" />

    <div v-else-if="!tableNames.length" class="text-center py-4 text-gray-500">
      <UIcon name="i-heroicons-table-cells" class="text-3xl mb-2" />
      <p>No tables found in the database</p>
    </div>

    <div v-else>
      <div class="flex items-center gap-4 mb-3">
        <UButton variant="outline" icon="i-heroicons-arrow-path" @click="refresh" :loading="pending" size="sm" />
        <USelectMenu v-model="selectedTableStore.selectedTable" :items="tableNamesStore.tableNameList" class="w-48" />
        <UBadge variant="soft">
          {{ tableNames.length }}
        </UBadge>
        <UPopover>
          <UButton label="Add CSV" color="primary" variant="subtle" />
          <template #content>
            <CsvUploader />
          </template>
        </UPopover>
      </div>
    </div>
  </UCard>
</template>

<script setup>
import { useTableNamesStore } from '~/stores/tableNames'
import { useSelectedTableStore } from '~/stores/selectedTable' // <-- import the store

const { data: tableNames, pending, error, refresh } = await useFetch('/api/tables', {
  default: () => [],
  transform: (data) => data || []
})

const tableNamesStore = useTableNamesStore()
const selectedTableStore = useSelectedTableStore() // <-- use the store

const tableNamesArray = Array.isArray(tableNames.value)
  ? tableNames.value.map(String)
  : []

tableNamesStore.setTableNames(tableNamesArray)

// Set first table as default selection if available
if (tableNamesArray.length && !selectedTableStore.selectedTable) {
  selectedTableStore.selectedTable = tableNamesArray[0]
}

</script>
