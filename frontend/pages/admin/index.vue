<script setup lang="ts">
// TODO: Allow this page only for admin users
import UsersTable from '@/components/admin/UsersTable.vue'
import AddUser from '@/components/admin/AddUser.vue'
import MetaTable from '~/components/admin/MetaTable.vue';
</script>


<template>
    <AddUser />
    <UPopover>
        <UButton label="Add CSV" color="primary" variant="subtle" />
        <template #content>
            <CsvUploader />
        </template>
    </UPopover>
    <USeparator color="primary" type="solid" class="mt-4" />
    <UsersTable />
    <USeparator color="primary" type="solid" class="mt-4" />
    <MetaTable />

</template>