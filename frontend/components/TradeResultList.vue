<script setup lang="ts">
import { useBacktestResultStore } from '#imports'
import { storeToRefs } from 'pinia'
import type { TradeResult } from '~/entity/TradeResult'

const backtestResultStore = useBacktestResultStore()
const { backtestResult } = storeToRefs(backtestResultStore)

</script>

<template>
    <div class="trade-list-container">
        <div class="trade-list">
            <div v-for="(trade, idx) in backtestResult?.allTrades ?? []" :key="idx" class="trade-item">
                <UCard>
                    <div>
                        <strong>Open Price:</strong> {{ trade.openPrice }}
                    </div>
                    <div>
                        <strong>Close Price:</strong> {{ trade.closePrice }}
                    </div>
                    <div>
                        <strong>Close Reason:</strong> {{ trade.closeReason }}
                    </div>
                    <div>
                        <strong>PnL:</strong> {{ Number(trade.PnL).toFixed(2) }}
                    </div>
                </UCard>
            </div>
        </div>
    </div>
</template>

<style scoped>
.trade-list-container {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 8px;
    background: #0f172b;
}

.trade-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.trade-item {
    padding: 8px;
    background: #0f172b;
    border-radius: 4px;
}

.trade-item:last-child {
    border-bottom: none;
}
</style>