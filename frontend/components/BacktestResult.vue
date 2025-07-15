<script setup lang="ts">
import { useBacktestResultStore } from '#imports'
import { storeToRefs } from 'pinia'

const backtestResultStore = useBacktestResultStore()
const { backtestResult } = storeToRefs(backtestResultStore)

</script>

<template>

    <div v-if="backtestResult">
        <UTextarea v-model="backtestResult" class="w-full h-64" readonly  autoresize/>
    </div>

    <div v-else>
      <UAlert color="warning" variant="subtle" icon="info">
        No backtest result available.
      </UAlert>
    </div>
</template>