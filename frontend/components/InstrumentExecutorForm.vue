<script setup lang="ts">
import type { FormError, FormSubmitEvent } from '@nuxt/ui'
import type { TabsItem } from '@nuxt/ui'
import { ref, reactive } from 'vue'
import { useSelectedTableStore } from '#imports'
import { storeToRefs } from 'pinia'

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

const validate = (state: any): FormError[] => {
  const errors: FormError[] = []
  if (!state.instrumentConsoleString) errors.push({ name: 'instrumentConsoleString', message: 'Required' })
  return errors
}

const config = useRuntimeConfig()
const backendBase = config.public.backendBase

const url = `${backendBase}/api/instrument/run`


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
</script>

<template>
  <UForm :validate="validate" :state="state" @submit="onSubmit">
    <div>
      <label class="block mb-1 font-medium" title="Instrument Console String"></label>
      <div class="flex gap-2">
        <UInput
          v-model="state.instrumentConsoleString"
          type="text"
          step="0.01"
          class="flex-1"
          :disabled="isRunning"
        />
        <UButton
          type="submit"
          variant="outline"
          color="primary"
          :loading="isRunning"
          :disabled="isRunning || !state.instrumentConsoleString"
        >
          {{ isRunning ? "Running..." : "Run Instrument" }}
        </UButton>
      </div>
    </div>
  </UForm>
</template>