<script setup lang="ts">
import type { FormError, FormSubmitEvent } from '@nuxt/ui'
import { useAuthStore } from '~/stores/useAuthStore' // <-- import the store

const state = reactive({
    username: '',
    password: ''
})

const validate = (state: any): FormError[] => {
    const errors: FormError[] = []
    if (!state.username) errors.push({ name: 'username', message: 'Required' })
    if (!state.password) errors.push({ name: 'password', message: 'Required' })
    return errors
}

const toast = useToast()
const router = useRouter()
const config = useRuntimeConfig()
const backendBase = config.public.backendBase
const authStore = useAuthStore()

console.log('Backend base URL:', backendBase)

async function onSubmit(event: FormSubmitEvent<typeof state>) {
    console.log("User submitted:", state.username)
    console.log("Password submitted:", state.password)

    try {
        const res = await $fetch(`${backendBase}/login`, {
            method: 'POST',
            body: new URLSearchParams({
                username: state.username,
                password: state.password
            }),
            credentials: 'include' // needed for session cookies
        })

        toast.add({
            title: 'Login Successful',
            description: `Welcome, ${state.username}`,
            color: 'primary'
        })

        // Set login state in Pinia store
        authStore.login({
            name: state.username,
        })



        // Redirect to dashboard or home
        router.push('/')
    } catch (err: any) {
        toast.add({
            title: 'Login Failed',
            description: 'Invalid credentials',
            color: 'error'
        })
        console.error('Login error:', err)
    }

    // Fetch user roles and store in authStore
    try {
        const roles = await $fetch<string[]>(`${backendBase}/api/users/${state.username}/roles`, {
            method: 'GET',
            credentials: 'include'
        })
        authStore.userRoles = roles
    } catch (rolesErr) {
        console.error('Failed to fetch user roles:', rolesErr)
        authStore.userRoles = []
    }
}
</script>



<template>
    <UForm :validate="validate" :state="state" @submit="onSubmit" class="flex flex-col gap-4">
        <UFormField label="Username" name="username">
            <UInput v-model="state.username" />
        </UFormField>

        <UFormField label="Password" name="password">
            <UInput v-model="state.password" type="password" />
        </UFormField>

        <UButton type="submit">
            Submit
        </UButton>
    </UForm>
</template>