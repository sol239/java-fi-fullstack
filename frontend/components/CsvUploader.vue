<template>
  <UCard class="max-w-md mx-auto">
    <template #header>
      <h2 class="text-xl font-semibold">Upload CSV File</h2>
    </template>

    <UForm :state="formState" @submit="uploadCsv" class="space-y-4">
      <UFormGroup label="Table Name" required >
        <UInput
          v-model="formState.tableName"
          placeholder="Enter table name"
          :disabled="isUploading"
        />
      </UFormGroup>

      <UFormGroup label="CSV File" required>
        <UInput
          type="file"
          accept=".csv"
          @change="handleFileChange"
          :disabled="isUploading"
        />
      </UFormGroup>

      <UButton
        type="submit"
        :loading="isUploading"
        :disabled="!formState.tableName || !selectedFile"
        block
      >
        {{ isUploading ? "Uploading..." : "Upload CSV" }}
      </UButton>
    </UForm>

    <div v-if="message || error" class="mt-4">
      <UAlert
        v-if="message"
        icon="i-heroicons-check-circle"
        color="green"
        variant="soft"
        :title="message"
      />
      <UAlert
        v-if="error"
        icon="i-heroicons-exclamation-triangle"
        color="red"
        variant="soft"
        :title="error"
      />
    </div>
  </UCard>
</template>

<script setup>
import { ref, reactive } from 'vue'

const formState = reactive({
  tableName: ''
})

const selectedFile = ref(null)
const message = ref('')
const error = ref('')
const isUploading = ref(false)

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0]
}

const uploadCsv = async () => {
  if (!selectedFile.value || !formState.tableName) {
    error.value = 'Please provide both table name and file.'
    return
  }

  isUploading.value = true
  error.value = ''
  message.value = ''

  // TODO: fix api endpoint

  try {
    const formData = new FormData()
    formData.append('tableName', formState.tableName)
    formData.append('file', selectedFile.value)

    const response = await fetch('http://localhost:8080/api/csv/upload', {
      method: 'POST',
      body: formData,
      credentials: 'include' // <-- add this line
    })

    const text = await response.text()
    if (response.ok) {
      message.value = text
      // Reset form on success
      formState.tableName = ''
      selectedFile.value = null
    } else {
      error.value = text
    }
  } catch (err) {
    error.value = 'Upload failed: ' + err.message
  } finally {
    isUploading.value = false
  }
}
</script>
