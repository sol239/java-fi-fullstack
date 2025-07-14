<template>
  <div>
    <div class="flex gap-6 mt-4 overflow-x-auto">
      <div style="min-width:350px;max-width:400px;flex:1;">
        <BackTestingForm />
      </div>

      <div style="min-width:700px;flex:2;">
        <UCard>
          <div class="flex items-center gap-4 mb-3">
            <TableNames />
            <UPopover>
              <UButton label="Add CSV" color="primary" variant="subtle" />
              <template #content>
                <CsvUploader />
              </template>
            </UPopover>
          </div>
          <TableView />
        </UCard>
      </div>



    </div>
  </div>
</template>

<script setup>
</script>
