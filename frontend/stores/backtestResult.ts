import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useBacktestResultStore = defineStore('backtestResult', () => {
  const backtestResult = ref('') // <-- use ref for reactivity

  function setResult(result: string) {
    backtestResult.value = result
  }

  return { backtestResult, setResult }
})
