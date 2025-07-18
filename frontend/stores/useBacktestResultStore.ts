import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { BacktestResult } from '~/entity/BacktestResult'

export const useBacktestResultStore = defineStore('backtestResult', () => {
  const backtestResult = ref<BacktestResult | null>(null) // use BacktestResult type

  function setResult(result: BacktestResult) {
    backtestResult.value = result
  }

  return { backtestResult, setResult }
})