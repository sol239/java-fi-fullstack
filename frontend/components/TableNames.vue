<template>

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
      <div class="flex items-center gap-4">
        <UButton variant="outline" icon="i-heroicons-arrow-path" @click="refresh" :loading="pending" size="sm" />
        <USelectMenu v-model="selectedTable" :items="reactiveTableNames" class="w-48" />
        <UBadge variant="soft">
          {{ length }}
        </UBadge>
      </div>
    </div>
</template>

<script setup>
import { useTableNamesStore } from '~/stores/useTableNamesStore'
import { useSelectedTableStore } from '~/stores/useSelectedTableStore' // <-- import the store
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { watch } from 'vue' // <-- import watch

const { data: tableNames, pending, error, refresh } = await useFetch('/api/tables', {
  default: () => [],
  transform: (data) => data || []
})

const tableNamesStore = useTableNamesStore()
const selectedTableStore = useSelectedTableStore()
const { selectedTable } = storeToRefs(selectedTableStore) // <-- make selectedTable reactive

const reactiveTableNames = computed(() => tableNamesStore.tableNameList)
const length = computed(() => reactiveTableNames.value.length)

const tableNamesArray = Array.isArray(tableNames.value)
  ? tableNames.value.map(String)
  : []

tableNamesStore.setTableNames(tableNamesArray)

console.log("Current selected table:", selectedTableStore.selectedTable)

// Set first table as default selection if available
if (tableNamesArray.length && !selectedTableStore.selectedTable) {
  selectedTableStore.selectedTable = tableNamesArray[0]
}

// Watch for changes and log
watch(selectedTable, (newVal) => {
  console.log("Current selected table:", newVal)
})

</script>
