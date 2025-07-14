<script setup lang="ts">
import type { FormError, FormSubmitEvent } from '@nuxt/ui'
import type { TabsItem } from '@nuxt/ui'
import { ref } from 'vue'
import { useSelectedTableStore } from '#imports'

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable

console.log('Selected table:', tableName)

const state = reactive({
  instrumentConsoleString: 'rsi:14',
})

const validate = (state: any): FormError[] => {
  const errors: FormError[] = []
  if (!state.instrumentConsoleString) errors.push({ name: 'instrumentConsoleString', message: 'Required' })
  return errors
}

const config = useRuntimeConfig()
const backendBase = config.public.backendBase

const url = `${backendBase}/api/instrument/run`


async function onSubmit(event: FormSubmitEvent<typeof state>) {
  console.log('Post URL:', url)

  try {
    const res = await $fetch(url, {
      method: 'POST',
      body: new URLSearchParams({
        instrumentConsoleString: state.instrumentConsoleString,
        tableName: tableName
      }),
      credentials: 'include'
    })
  } catch (err: any) {
    console.error('Login error:', err)
  }
}
</script>

<template>
  <UForm :validate="validate" :state="state" @submit="onSubmit">
    <div>
      <label class="block mb-1 font-medium" title="Initial account balance for backtesting."></label>
      <div class="flex gap-2">
        <UInput v-model="state.instrumentConsoleString" type="text" step="0.01" class="flex-1" />
        <UButton type="submit" variant="outline" color="primary">Run Instrument</UButton>
      </div>
    </div>
  </UForm>
</template>