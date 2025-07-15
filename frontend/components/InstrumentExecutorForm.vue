<script setup lang="ts">
import type { FormError, FormSubmitEvent } from '@nuxt/ui'
import type { TabsItem } from '@nuxt/ui'
import { ref, reactive } from 'vue'
import { useSelectedTableStore } from '#imports'

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable

console.log('Selected table:', tableName)

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
  console.log("Running instrument with data:\nConsole String:", state.instrumentConsoleString, "\nTable Name:", tableName)

  try {
    const res = await $fetch(url, {
      method: 'POST',
      body: new URLSearchParams({
        instrumentConsoleString: state.instrumentConsoleString,
        tableName: tableName
      }),
      credentials: 'include'
    })
    message.value = 'Instrument executed successfully.'
  } catch (err: any) {
    error.value = 'Instrument execution failed: ' + (err?.message || err)
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
      <div v-if="message || error" class="mt-4">
        <UAlert
          v-if="message"
          icon="i-heroicons-check-circle"
          color="green"
          variant="soft"
          :title="message"
        />
        <UAlert
          v-if="error"
          icon="i-heroicons-exclamation-triangle"
          color="red"
          variant="soft"
          :title="error"
        />
      </div>
    </div>
  </UForm>
</template>