<script setup lang="ts">

import AddUserForm from '@/components/admin/AddUserForm.vue'

</script>


<template>

    <UPopover>
        <UButton variant="outline">Add user</UButton>
        <template #content>
                  <AddUserForm />

        </template>
    </UPopover>
</template>