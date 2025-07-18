<script setup lang="ts">

import { User } from '~/entity/User'
import { useUsersStore } from '#imports'
import { storeToRefs } from 'pinia'

const form = ref({
    username: '',
    password: '',
    enabled: true,
    roles: ["USER"]
})

const toast = useToast()
const usersStore = useUsersStore()
const { users } = storeToRefs(usersStore)

async function submitForm() {
    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase
    const url = `${backendBase}/api/users/add`

    const { data, error } = await useFetch<User>(url, {
        method: 'POST',
        body: form.value,
        credentials: 'include'
    })

    if (error.value) {
        toast.add({
            title: 'Failed to add user',
            color: 'error',
            icon: 'i-lucide-alert-triangle'
        })
    } else {
        toast.add({
            title: 'User added successfully',
            color: 'success',
            icon: 'i-lucide-circle-check'
        })
        // Add the new user to the store
        const u = data.value
        if (u) {
            const newUser = new User(
                u.id,
                u.username,
                u.password,
                u.enabled,
                u.roles
            )
            usersStore.users.push(newUser)
        }
    }
}

</script>

<template>
    <UForm :state="form" @submit.prevent="submitForm">
        <UInput v-model="form.username" label="Username" required />
        <UInput v-model="form.password" type="password" label="Password" required />
        <UCheckbox v-model="form.enabled" label="Enabled" />

        <!-- Role selection is currently disabled, only USER role users can be added. -->
        <!-- <USelect v-model="form.roles" :options="['USER', 'ADMIN']" label="Roles" multiple /> -->

        <UButton type="submit" color="primary">Add User</UButton>

    </UForm>

</template>