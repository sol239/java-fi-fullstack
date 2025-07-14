<script setup lang="ts">
// TODO: Allow this page only for admin users
import UsersTable from '@/components/admin/UsersTable.vue'

</script>


<template>

    <UCard>
        <UButton variant="outline">Add user</UButton>
    </UCard>

    <USeparator color="primary" type="solid" />
    <UsersTable />

</template>