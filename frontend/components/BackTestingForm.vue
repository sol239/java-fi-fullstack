<script setup lang="ts">
import type { TabsItem } from '@nuxt/ui'
import { ref } from 'vue'
import type { FormSubmitEvent } from '@nuxt/ui'
import { useSelectedTableStore } from '#imports'

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable
const items = ref<TabsItem[]>([
    { label: 'Setup' },
    { label: 'Strategy' }
])

const selectedTab = ref('0') // <-- use index

// TODO: Validate the form data

const form = ref({
    // Setup fields
    balance: 10000.0,
    leverage: 1.0,
    fee: 0.0025,
    takeProfit: 1.3,
    stopLoss: 0.85,
    amount: 4000.0,
    maxTrades: 1,
    delaySeconds: 10800,
    dateRestriction: '',
    tradeLifeSpanSeconds: 90000000,
    // Strategy fields
    openClause: 'WHERE rsi_14_ins_ <= 30',
    closeClause: 'WHERE rsi_14_ins_ >= 70',
    stopLossEnabled: false,
    takeProfitEnabled: true
})

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/backtest/run`

async function onSubmit(event: FormSubmitEvent<typeof form>) {
  console.log('Post URL:', url)

  try {
    const res = await $fetch(url, {
      method: 'POST',
      body: new URLSearchParams({
        tableName: tableName,
        balance: form.value.balance.toString(),
        leverage: form.value.leverage.toString(),
        fee: form.value.fee.toString(),
        takeProfit: form.value.takeProfit.toString(),
        stopLoss: form.value.stopLoss.toString(),
        amount: form.value.amount.toString(),
        maxTrades: form.value.maxTrades.toString(),
        delaySeconds: form.value.delaySeconds.toString(),
        dateRestriction: form.value.dateRestriction,
        tradeLifeSpanSeconds: form.value.tradeLifeSpanSeconds.toString(),
        openClause: form.value.openClause,
        closeClause: form.value.closeClause,
        stopLossEnabled: form.value.stopLossEnabled ? 'true' : 'false',
        takeProfitEnabled: form.value.takeProfitEnabled ? 'true' : 'false'
      }),
      credentials: 'include'
    })
  } catch (err: any) {
    console.error('Login error:', err)
  }
}

// console.log selectedTab with watch
import { watch } from 'vue'
watch(selectedTab, (newTab) => {
    console.log(`Selected tab changed to: ${newTab}`)
}, { immediate: true })

</script>

<template>
    <UCard>
        <UTabs :items="items" v-model="selectedTab"  class="w-full" />

        <UForm :state="form" @submit="onSubmit" class="flex flex-col gap-4">
            <template v-if="selectedTab == 0">
                <div>
                    <label class="block mb-1 font-medium"
                        title="Initial account balance for backtesting.">Balance</label>
                    <UInput v-model="form.balance" type="number" step="0.01" />
                </div>
                <div>
                    <label class="block mb-1 font-medium"
                        title="Leverage multiplier applied to trades.">Leverage</label>
                    <UInput v-model="form.leverage" type="number" step="0.01" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Trading fee as a percentage of each trade.">Fee</label>
                    <UInput v-model="form.fee" type="number" step="0.0001" />
                </div>
                <div>
                    <label class="block mb-1 font-medium"
                        title="Take profit multiplier relative to the open price.">Take
                        Profit</label>
                    <UInput v-model="form.takeProfit" type="number" step="0.01" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Stop loss multiplier relative to the open price.">Stop
                        Loss</label>
                    <UInput v-model="form.stopLoss" type="number" step="0.01" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Amount of asset to trade per position.">Amount</label>
                    <UInput v-model="form.amount" type="number" step="0.01" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Maximum number of trades that can be open at once.">Max
                        Trades</label>
                    <UInput v-model="form.maxTrades" type="number" step="1" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Minimum delay in seconds between opening trades.">Delay
                        Seconds</label>
                    <UInput v-model="form.delaySeconds" type="number" step="1" />
                </div>
                <div>
                    <label class="block mb-1 font-medium"
                        title="Restrict trading to a specific date or interval (e.g. YYYY-MM-DD or range).">Date
                        Restriction</label>
                    <UInput v-model="form.dateRestriction" type="text" />
                </div>
                <div>
                    <label class="block mb-1 font-medium"
                        title="Maximum duration in seconds that a trade can remain open.">Trade Life Span
                        Seconds</label>
                    <UInput v-model="form.tradeLifeSpanSeconds" type="number" step="1" />
                </div>
            </template>
            <template v-else-if="selectedTab == 1">
                <div>
                    <label class="block mb-1 font-medium" title="SQL clause for opening a trade.">Open Clause</label>
                    <UInput v-model="form.openClause" type="text" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="SQL clause for closing a trade.">Close Clause</label>
                    <UInput v-model="form.closeClause" type="text" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Enable stop loss for trades.">Stop Loss Enabled</label>
                    <UCheckbox v-model="form.stopLossEnabled" />
                </div>
                <div>
                    <label class="block mb-1 font-medium" title="Enable take profit for trades.">Take Profit
                        Enabled</label>
                    <UCheckbox v-model="form.takeProfitEnabled" />
                </div>
            </template>
            <div class="flex gap-2 mt-4">
                <UButton type="submit" color="primary">Backtest</UButton>
                <UButton color="primary" variant="outline">Save</UButton>

                <!-- TODO: Implement selecting from saved setups/strategies -->

                <!-- TODO: Implement saving -> with json probably -->
            </div>
        </UForm>
    </UCard>
</template>