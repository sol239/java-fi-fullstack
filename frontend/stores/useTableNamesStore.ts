import { defineStore } from 'pinia'

export const useTableNamesStore = defineStore('tableNames', {
  state: () => ({
    tableNameList: []
  }),
  actions: {
    setTableNames(names) {
      this.tableNameList = Array.isArray(names) ? [...names] : []
    }
  }
})
