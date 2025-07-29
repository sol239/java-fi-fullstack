import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAvailableInstrumentsStore = defineStore('availableInstruments', () => {
  const availableInstruments = ref<string[]>([])

  function setInstruments(instruments: string[]) {
    availableInstruments.value = instruments
  }

  return { availableInstruments, setInstruments }
})