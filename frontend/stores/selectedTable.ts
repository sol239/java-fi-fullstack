import { defineStore } from 'pinia'

export const useSelectedTableStore = defineStore('selectedTable', {
  state: () => ({
    selectedTable: ''
  }),
  actions: {
    setSelectedTable(tableName: string) {
      this.selectedTable = tableName
    }
  }
})
