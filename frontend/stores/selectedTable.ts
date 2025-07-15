import { defineStore } from 'pinia'

export const useSelectedTableStore = defineStore('selectedTable', {
  state: () => ({
    selectedTable: ref<string>(''),
  }),
  actions: {
    setSelectedTable(tableName: string) {
      this.selectedTable = tableName
    }
  }
})
