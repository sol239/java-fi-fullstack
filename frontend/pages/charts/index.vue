<template>
  <div>
    <UPopover>
      <UButton label="Add CSV" color="primary" variant="subtle" />
      <template #content>
        <CsvUploader />
      </template>
    </UPopover>
    <TableNames />
    <TableView />
  </div>
</template>

<script setup>
</script>
