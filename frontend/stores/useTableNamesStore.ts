import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTableNamesStore = defineStore('tableNames', () => {
  const tableNameList = ref<string[]>([])

  function setTableNames(names: string[]) {
    tableNameList.value = Array.isArray(names) ? [...names] : []
  }

  function addTableName(name: string) {
    if (name && !tableNameList.value.includes(name)) {
      tableNameList.value.push(name)
    }
  }

  return { tableNameList, setTableNames, addTableName }
})
