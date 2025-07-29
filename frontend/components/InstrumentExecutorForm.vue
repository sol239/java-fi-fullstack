<script setup lang="ts">
import type { FormError, FormSubmitEvent } from '@nuxt/ui'
import { ref, reactive, onMounted } from 'vue'
import { useSelectedTableStore } from '#imports'
import { storeToRefs } from 'pinia'
import { useAvailableInstrumentsStore } from '#imports'

const availableInstrumentsStore = useAvailableInstrumentsStore()
const { availableInstruments } = storeToRefs(availableInstrumentsStore)
const selectedTableStore = useSelectedTableStore()
const { selectedTable } = storeToRefs(selectedTableStore)

console.log('Selected table:', selectedTable.value)
const toast = useToast()

const state = reactive({
  instrumentConsoleString: 'rsi:14',
})

const isRunning = ref(false)
const message = ref('')
const error = ref('')

const instrumentOptions = ref<{ label: string; value: string }[]>([])
const selectedInstrumentKey = ref('')

const validate = (state: any): FormError[] => {
  const errors: FormError[] = []
  if (!state.instrumentConsoleString) errors.push({ name: 'instrumentConsoleString', message: 'Required' })
  return errors
}

const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const url = `${backendBase}/api/instrument/run`

async function getAvailableInstruments() {
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase || 'http://localhost:8080'
  const res = await fetch(`${backendBase}/api/instrument`, {
    credentials: 'include',
    method: 'GET'
  })
  return await res.json()
}

async function onSubmit(event: FormSubmitEvent<typeof state>) {
  message.value = ''
  error.value = ''
  isRunning.value = true
  console.log("Running instrument with data:\nConsole String:", state.instrumentConsoleString, "\nTable Name:", selectedTable.value)

  try {
    const res = await $fetch(url, {
      method: 'POST',
      body: new URLSearchParams({
        instrumentConsoleString: state.instrumentConsoleString,
        tableName: selectedTable.value
      }),
      credentials: 'include'
    })
    message.value = 'Instrument executed successfully.'
    toast.add({ title: message.value, color: 'primary', icon: 'i-heroicons-check-circle', duration: 1500 })
  } catch (err: any) {
    error.value = 'Instrument execution failed: ' + (err?.message || err)
    toast.add({ title: error.value, color: 'warning', icon: 'i-heroicons-exclamation-triangle', duration: 1500 })
    console.error('Instrument error:', err)
  } finally {
    isRunning.value = false
  }
}

onMounted(async () => {
  const instruments = await getAvailableInstruments()
  instrumentOptions.value = Object.entries(instruments).map(([key, val]) => ({
    label: key,
    value: val as string
  }))

  availableInstrumentsStore.setInstruments(Object.keys(instruments))

})
</script>

<template>
  <UForm :validate="validate" :state="state" @submit="onSubmit">
    <div>

      <label class="block mb-1 font-medium" title="Instrument Console String"></label>
      <div class="flex gap-2">
        <USelect v-if="instrumentOptions.length" v-model="state.instrumentConsoleString" :items="instrumentOptions"
          class="w-48 mb-2" placeholder="Select instrument" />
        <UInput v-model="state.instrumentConsoleString" type="text" step="0.01" class="flex-1" :disabled="isRunning" />
        <UButton type="submit" variant="outline" color="primary" :loading="isRunning"
          :disabled="isRunning || !state.instrumentConsoleString">
          {{ isRunning ? "Running..." : "Run Instrument" }}
        </UButton>
      </div>
    </div>
  </UForm>
</template>