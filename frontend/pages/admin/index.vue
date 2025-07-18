<script setup lang="ts">
// TODO: Allow this page only for admin users
import UsersTable from '@/components/admin/UsersTable.vue'
import AddUser from '@/components/admin/AddUser.vue'
</script>


<template>
    <AddUser />
    <USeparator color="primary" type="solid" />
    <UsersTable />

</template>