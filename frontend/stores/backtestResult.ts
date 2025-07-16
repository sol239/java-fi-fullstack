import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { BacktestResult } from '~/components/BackTestingForm.vue'

export const useBacktestResultStore = defineStore('backtestResult', () => {
  const backtestResult = ref<BacktestResult | null>(null) // use BacktestResult type

  function setResult(result: BacktestResult) {
    backtestResult.value = result
  }

  return { backtestResult, setResult }
})