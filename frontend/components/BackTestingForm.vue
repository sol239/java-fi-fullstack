<script setup lang="ts">
import type { TabsItem } from '@nuxt/ui'
import { ref, computed, onMounted, watch } from 'vue'
import type { FormSubmitEvent } from '@nuxt/ui'
import { useSelectedTableStore } from '#imports'
import { useBacktestResultStore } from '#imports'
import { useAvailableInstrumentsStore } from '#imports'
import { storeToRefs } from 'pinia'

const availableInstrumentsStore = useAvailableInstrumentsStore()
const { availableInstruments } = storeToRefs(availableInstrumentsStore)



const open = ref(true)


const backtestResultStore = useBacktestResultStore()
const selectedTableStore = useSelectedTableStore()
const tableName = computed(() => selectedTableStore.selectedTable)
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
    openClause: 'WHERE rsi:14 <= 30',
    closeClause: 'WHERE rsi:14 >= 70',
    stopLossEnabled: true,
    takeProfitEnabled: false,
    runInstruments: false
})

const isRunning = ref(false)
const message = ref('')
const error = ref('')

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/backtest/run`

function toTradeResultArray(raw: any[]): TradeResult[] {
    return raw.map((trade) => ({
        openPrice: trade.openPrice,
        takePrice: trade.takePrice,
        stopPrice: trade.stopPrice,
        closePrice: trade.closePrice,
        amount: trade.amount,
        assetName: trade.assetName,
        openTime: trade.openTime,
        closeTime: trade.closeTime,
        PnL: trade.PnL,
        strategy: {
            openClause: trade.strategy.openClause,
            closeClause: trade.strategy.closeClause,
            takeProfit: trade.strategy.takeProfit,
            stopLoss: trade.strategy.stopLoss,
            setup: {
                balance: trade.strategy.setup.balance,
                leverage: trade.strategy.setup.leverage,
                fee: trade.strategy.setup.fee,
                takeProfit: trade.strategy.setup.takeProfit,
                stopLoss: trade.strategy.setup.stopLoss,
                amount: trade.strategy.setup.amount,
                riskReward: trade.strategy.setup.riskReward,
                maxTrades: trade.strategy.setup.maxTrades,
                delaySeconds: trade.strategy.setup.delaySeconds,
                dateRestriction: trade.strategy.setup.dateRestriction,
                maxOpenedTrades: trade.strategy.setup.maxOpenedTrades,
                tradeLifeSpanSeconds: trade.strategy.setup.tradeLifeSpanSeconds,
            }
        },
        openTimestamp: trade.openTimestamp,
        closeTimestamp: trade.closeTimestamp,
        closeReason: trade.closeReason,
    }));
}



async function onSubmit(event: FormSubmitEvent<typeof form>) {
    message.value = ''
    error.value = ''
    isRunning.value = true
    console.log('Post URL:', url)
    console.log('Form data:', form.value)
    console.log('Selected table:', tableName)

    const formInstruments = [
        ...(form.value.openClause ? form.value.openClause.split(' ') : []),
        ...(form.value.closeClause ? form.value.closeClause.split(' ') : [])
    ].filter(Boolean)


    // Remove instruments from formInstruments that are not included in availableInstruments and deduplicate
    const filteredFormInstruments = Array.from(
        new Set(
            formInstruments.filter(instr =>
                availableInstruments.value.some(avail => instr.includes(avail))
            )
        )
    )

    // Preprocess openClause and closeClause just before sending (do not modify UI)
    const preprocessClause = (clause: string) => {
        // Replace all instrument patterns like "ema:14" or "rsi:14,2" with "EMA_14_INS_" or "RSI_14_2_INS_"
        // This regex matches words with : or , and replaces them accordingly
        return clause.replace(/([a-zA-Z]+)([:|,][\d,]+)/g, (match, p1, p2) => {
            return `${p1.toUpperCase()}${p2.replace(/[:|,]/g, '_').replace(/_+$/, '')}_INS_`
        })
    }

    const openClauseToSend = preprocessClause(form.value.openClause)
    const closeClauseToSend = preprocessClause(form.value.closeClause)

    if (form.value.runInstruments) {
        if (filteredFormInstruments.length !== 0) {
            for (const instr of filteredFormInstruments) {
                console.log(`Running instrument: ${instr}`)
                // Run instrument executor for each instr
                try {
                    await $fetch(`${backendBase}/api/instrument/run`, {
                        method: 'POST',
                        body: new URLSearchParams({
                            instrumentConsoleString: instr,
                            tableName: tableName.value
                        }),
                        credentials: 'include'
                    })
                } catch (err: any) {
                    console.error('Instrument execution failed:', err)
                }
            }
        }
    }

    // const url = `${backendBase}/api/instrument/run`

    try {
        const { data, error: fetchError } = await useFetch(url, {
            method: 'POST',
            body: new URLSearchParams({
                tableName: tableName.value,
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
                openClause: openClauseToSend,
                closeClause: closeClauseToSend,
                stopLossEnabled: form.value.stopLossEnabled ? 'true' : 'false',
                takeProfitEnabled: form.value.takeProfitEnabled ? 'true' : 'false',
                runInstruments: form.value.runInstruments ? 'true' : 'false'
            }),
            credentials: 'include'
        })

        if (fetchError.value) {
            throw new Error(fetchError.value.message || 'Backtest failed')
        }

        console.log('Backtest response data:', data.value)

        const responseData = data.value as { backtestSummary: BacktestSummary; allTrades: any[] };
        const summary: BacktestSummary = responseData.backtestSummary;
        const allTradesRaw = JSON.parse(JSON.stringify(responseData.allTrades));
        const allTrades: TradeResult[] = toTradeResultArray(allTradesRaw);
        const result: BacktestResult = {
            summary,
            allTrades
        };

        backtestResultStore.setResult(result)
        message.value = 'Backtest completed successfully.'

    } catch (err: any) {
        error.value = 'Backtest failed: ' + (err?.message || err)
        console.error('Backtest error:', err)
    } finally {
        isRunning.value = false
    }
}

// console.log selectedTab with watch
watch(selectedTab, (newTab) => {
    console.log(`Selected tab changed to: ${newTab}`)

}, { immediate: true })

const instrumentOptions = ref<{ label: string; value: string }[]>([])

// Each instrument selection is an object: { instrument: string, operator: string, value: number|null }
const selectedOpenInstruments = ref<{ instrument: string, operator: string, value: number | null }[]>([])
const selectedCloseInstruments = ref<{ instrument: string, operator: string, value: number | null }[]>([])

const operatorOptions = [
    { label: '=', value: '=' },
    { label: '<', value: '<' },
    { label: '>', value: '>' },
    { label: '<=', value: '<=' },
    { label: '>=', value: '>=' }
]

// Helper to sync USelect (array of strings) with our array of objects
function syncInstrumentSelection(selected: string[], arrRef: typeof selectedOpenInstruments) {
    // Remove deselected
    arrRef.value = arrRef.value.filter(obj => selected.includes(obj.instrument))
    // Add new selections
    selected.forEach(inst => {
        if (!arrRef.value.find(obj => obj.instrument === inst)) {
            arrRef.value.push({ instrument: inst, operator: '=', value: null })
        }
    })
}

// For USelect v-model binding
const selectedOpenInstrumentNames = computed({
    get: () => selectedOpenInstruments.value.map(obj => obj.instrument),
    set: (val: string[]) => syncInstrumentSelection(val, selectedOpenInstruments)
})
const selectedCloseInstrumentNames = computed({
    get: () => selectedCloseInstruments.value.map(obj => obj.instrument),
    set: (val: string[]) => syncInstrumentSelection(val, selectedCloseInstruments)
})

// Watch for changes and update openClause
watch(selectedOpenInstruments, (arr) => {
    if (arr.length && arr.every(obj => obj.operator && obj.value !== null && obj.value !== undefined && obj.value !== '')) {
        form.value.openClause = 'WHERE ' + arr.map(obj => `${obj.instrument} ${obj.operator} ${obj.value}`).join(' AND ')
    }
}, { deep: true })

// Watch for changes and update closeClause
watch(selectedCloseInstruments, (arr) => {
    if (arr.length && arr.every(obj => obj.operator && obj.value !== null && obj.value !== undefined && obj.value !== '')) {
        form.value.closeClause = 'WHERE ' + arr.map(obj => `${obj.instrument} ${obj.operator} ${obj.value}`).join(' AND ')
    }
}, { deep: true })

async function getAvailableInstruments() {
    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const res = await fetch(`${backendBase}/api/instrument`, {
        credentials: 'include',
        method: 'GET'
    })
    return await res.json()
}

onMounted(async () => {
    const instruments = await getAvailableInstruments()
    instrumentOptions.value = Object.entries(instruments).map(([key, val]) => ({
        label: key,
        value: val as string
    }))
})
</script>

<template>
    <UCollapsible v-model:open="open" class="flex flex-col gap-2">
        <UButton label="Open" color="neutral" variant="subtle" trailing-icon="i-lucide-chevron-down" block />
        <template #content>
            <UCard>
                <UTabs :items="items" v-model="selectedTab" class="w-full" />

                <UForm :state="form" @submit="onSubmit" class="flex flex-col gap-4">
                    <!-- SETUP SECTION -->
                    <template v-if="selectedTab == '0'">
                        <div class="flex flex-row gap-6">
                            <div class="flex flex-col gap-4 flex-1">
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
                                    <label class="block mb-1 font-medium"
                                        title="Trading fee as a percentage of each trade.">Fee</label>
                                    <UInput v-model="form.fee" type="number" step="0.0001" />
                                </div>
                                <div>
                                    <label class="block mb-1 font-medium"
                                        title="Take profit multiplier relative to the open price.">Take
                                        Profit</label>
                                    <UInput v-model="form.takeProfit" type="number" step="0.01" />
                                </div>
                                <div>
                                    <label class="block mb-1 font-medium"
                                        title="Stop loss multiplier relative to the open price.">Stop
                                        Loss</label>
                                    <UInput v-model="form.stopLoss" type="number" step="0.01" />
                                </div>
                            </div>
                            <div class="flex flex-col gap-4 flex-1">
                                <div>
                                    <label class="block mb-1 font-medium"
                                        title="Amount of asset to trade per position.">Amount</label>
                                    <UInput v-model="form.amount" type="number" step="0.01" />
                                </div>
                                <div>
                                    <label class="block mb-1 font-medium"
                                        title="Maximum number of trades that can be open at once.">Max
                                        Trades</label>
                                    <UInput v-model="form.maxTrades" type="number" step="1" />
                                </div>
                                <div>
                                    <label class="block mb-1 font-medium"
                                        title="Minimum delay in seconds between opening trades.">Delay
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
                                        title="Maximum duration in seconds that a trade can remain open.">Trade Life
                                        Span
                                        Seconds</label>
                                    <UInput v-model="form.tradeLifeSpanSeconds" type="number" step="1" />
                                </div>
                            </div>
                        </div>
                    </template>

                    <!-- STRATEGY SECTION -->
                    <template v-else-if="selectedTab == '1'">
                        <div class="flex flex-row gap-6">
                            <div class="flex flex-col gap-4 flex-1">
                                <div>
                                    <label class="block mb-1 font-medium" title="SQL clause for opening a trade.">Open
                                        Clause</label>
                                    <UInput v-model="form.openClause" type="text" class="w-full" />
                                    <USelect v-if="instrumentOptions.length" :items="instrumentOptions"
                                        v-model="selectedOpenInstrumentNames" multiple class="w-full mb-2 mt-4"
                                        placeholder="Select open instrument" />
                                </div>
                                <!-- Badge and controls for open clause -->
                                <div v-if="selectedOpenInstruments.length" class="flex flex-col gap-2 mt-2">
                                    <div v-for="(obj, idx) in selectedOpenInstruments" :key="obj.instrument"
                                        class="flex items-center gap-2">
                                        <UBadge color="primary">{{instrumentOptions.find(opt => opt.value ===
                                            obj.instrument)?.label}}</UBadge>
                                        <USelect :items="operatorOptions" v-model="obj.operator" class="w-20"
                                            placeholder="Op" />
                                        <UInput v-model="obj.value" type="number" class="w-24" placeholder="Value" />
                                    </div>
                                </div>
                                <!-- Close Instrument Select -->
                                <div>
                                    <label class="block mb-1 font-medium" title="SQL clause for closing a trade.">Close
                                        Clause</label>
                                    <UInput v-model="form.closeClause" type="text" class="w-full" />
                                    <USelect v-if="instrumentOptions.length" :items="instrumentOptions"
                                        v-model="selectedCloseInstrumentNames" multiple class="w-full mb-2 mt-4"
                                        placeholder="Select close instrument" />
                                </div>
                                <!-- Badge and controls for close clause -->
                                <div v-if="selectedCloseInstruments.length" class="flex flex-col gap-2 mt-2">
                                    <div v-for="(obj, idx) in selectedCloseInstruments" :key="obj.instrument"
                                        class="flex items-center gap-2">
                                        <UBadge color="primary">{{instrumentOptions.find(opt => opt.value ===
                                            obj.instrument)?.label}}</UBadge>
                                        <USelect :items="operatorOptions" v-model="obj.operator" class="w-20"
                                            placeholder="Op" />
                                        <UInput v-model="obj.value" type="number" class="w-24" placeholder="Value" />
                                    </div>
                                </div>
                                <!-- Add checkboxes in a row under the other fields -->
                                <div class="flex flex-row gap-6 mt-2">
                                    <div class="flex items-center gap-2">
                                        <label class="block font-medium" title="Enable stop loss for trades.">Stop Loss
                                            Enabled</label>
                                        <UCheckbox v-model="form.stopLossEnabled" />
                                    </div>
                                    <div class="flex items-center gap-2">
                                        <label class="block font-medium" title="Enable take profit for trades.">Take
                                            Profit Enabled</label>
                                        <UCheckbox v-model="form.takeProfitEnabled" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </template>

                    <div class="flex gap-2 mt-4">
                        <UButton type="submit" color="primary" :loading="isRunning" :disabled="isRunning">
                            {{ isRunning ? "Running..." : "Backtest" }}
                        </UButton>
                        <UButton color="primary" variant="outline" :disabled="isRunning">Save</UButton>
                        <div class="flex items-center gap-2">
                            <label class="block font-medium" title="Run instruments after backtest.">Run
                                Instruments</label>
                            <UCheckbox v-model="form.runInstruments"></UCheckbox>
                        </div>

                    </div>
                    <div v-if="message || error" class="mt-4">
                        <UAlert v-if="message" icon="i-heroicons-check-circle" variant="soft" :title="message" />
                        <UAlert v-if="error" icon="i-heroicons-exclamation-triangle" variant="soft" :title="error" />
                    </div>
                </UForm>
            </UCard>
        </template>

    </UCollapsible>


</template>