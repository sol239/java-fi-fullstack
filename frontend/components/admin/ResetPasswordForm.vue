<script setup lang="ts">

import { User } from '~/entity/User'
import { useUsersStore } from '#imports'
import { storeToRefs } from 'pinia'

const form = ref({
    password: ''
})

const toast = useToast()
const usersStore = useUsersStore()
const { users } = storeToRefs(usersStore)

const show = ref(false)
// Remove: const password = ref('')

const props = defineProps<{ userId: number }>()

async function submitForm() {
    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase
    const url = `${backendBase}/api/users/reset`

    console.log('Submitting form with userId:', props.userId, 'and password:', form.value.password  )

    const { data, error } = await useFetch<User>(url, {
        method: 'POST',
        body: { password: form.value.password, userId: props.userId },
        credentials: 'include'
    })

    if (error.value) {
        toast.add({
            title: 'Failed to reset password',
            color: 'error',
            icon: 'i-lucide-alert-triangle'
        })
    } else {
        toast.add({
            title: 'Password reset successfully',
            color: 'success',
            icon: 'i-lucide-circle-check'
        })
    }
}

</script>

<template>
  <UForm :state="form" @submit.prevent="submitForm">
    <UInput
      v-model="form.password"
      placeholder="New password"
      :type="show ? 'text' : 'password'"
      :ui="{ trailing: 'pe-1' }"
      required
    >
      <template #trailing>
        <UButton
          color="neutral"
          variant="link"
          size="sm"
          :icon="show ? 'i-lucide-eye-off' : 'i-lucide-eye'"
          :aria-label="show ? 'Hide password' : 'Show password'"
          :aria-pressed="show"
          aria-controls="password"
          @click="show = !show"
        />
      </template>
    </UInput>
    <UButton type="submit" color="primary" class="mt-4">Submit</UButton>
  </UForm>
</template>

<style>
/* Hide the password reveal button in Edge */
::-ms-reveal {
    display: none;
}
</style>