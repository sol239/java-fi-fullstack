<script setup lang="ts">

import ResetPasswordForm from '@/components/admin/ResetPasswordForm.vue'

// Define a prop for 'row'
defineProps<{
  row: {
    original: {
      id: string | number
    }
  }
}>()

</script>


<template>

    <UPopover>
        <UButton variant="outline">Reset Password</UButton>
        <template #content>
            <ResetPasswordForm :userId="Number(row.original.id)" />
        </template>
    </UPopover>
</template>