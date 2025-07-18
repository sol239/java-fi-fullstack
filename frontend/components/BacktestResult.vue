<script setup lang="ts">
import { useBacktestResultStore } from '#imports'
import { storeToRefs } from 'pinia'
import { computed } from 'vue'

const backtestResultStore = useBacktestResultStore()
const { backtestResult } = storeToRefs(backtestResultStore)

// Parse summary from result string
const summary = computed(() => {
  const result = backtestResult.value?.summary

  console.log('BT RESULT:', backtestResult.value?.allTrades  )

  if (!result) return null
  return {
    winning: result.winningTradesCount,
    losing: result.losingTradesCount,
    total: result.totalTradesCount,
    winRate: (result.winrate * 100).toFixed(2),
    profit: result.profit.toFixed(2)
  }
})
</script>

<template>
  <UCard>
    <div v-if="summary" class="grid grid-cols-2 gap-4 mb-4">
      <div class="flex flex-col items-center justify-center p-2 rounded bg-green-50">
        <span class="text-green-600 text-lg font-bold">
          <i class="i-lucide-trophy mr-1"></i> {{ summary.winning }}
        </span>
        <span class="text-xs text-gray-500">Winning Trades</span>
      </div>
      <div class="flex flex-col items-center justify-center p-2 rounded bg-red-50">
        <span class="text-red-600 text-lg font-bold">
          <i class="i-lucide-thumbs-down mr-1"></i> {{ summary.losing }}
        </span>
        <span class="text-xs text-gray-500">Losing Trades</span>
      </div>
      <div class="flex flex-col items-center justify-center p-2 rounded bg-blue-50">
        <span class="text-blue-600 text-lg font-bold">
          <i class="i-lucide-bar-chart mr-1"></i> {{ summary.total }}
        </span>
        <span class="text-xs text-gray-500">Total Trades</span>
      </div>
      <div class="flex flex-col items-center justify-center p-2 rounded bg-yellow-50">
        <span class="text-yellow-600 text-lg font-bold">
          <i class="i-lucide-percent mr-1"></i> {{ summary.winRate }}%
        </span>
        <span class="text-xs text-gray-500">Win Rate</span>
      </div>
      <div class="col-span-2 flex flex-col items-center justify-center p-2 rounded bg-emerald-50 mt-2">
        <span class="text-emerald-600 text-xl font-bold">
          <i class="i-lucide-dollar-sign mr-1"></i> {{ summary.profit }} USD
        </span>
        <span class="text-xs text-gray-500">Profit</span>
      </div>
    </div>
  </UCard>

  <TradeResultList :trades="backtestResult.value?.allTrades" />
</template>