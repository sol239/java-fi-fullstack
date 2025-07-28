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

      <UFormGroup label="Description" required>
        <UInput
          v-model="formState.description"
          placeholder="Enter description"
          :disabled="isUploading"
        />
      </UFormGroup>

      <UFormGroup label="Asset Name" required>
        <UInput
          v-model="formState.assetName"
          placeholder="Enter asset name"
          :disabled="isUploading"
        />
      </UFormGroup>

      <UFormGroup label="Timeframe" required>
        <UInput
          v-model="formState.timeframe"
          placeholder="Enter timeframe"
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
        :disabled="!formState.tableName || !formState.description || !formState.assetName || !formState.timeframe || !selectedFile"
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
// Update import path to match the filename
import { useTableNamesStore } from '~/stores/useTableNamesStore'

const formState = reactive({
  tableName: '',
  description: '',
  assetName: '',
  timeframe: ''
})

const selectedFile = ref(null)
const message = ref('')
const error = ref('')
const isUploading = ref(false)

const tableNamesStore = useTableNamesStore()

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0]
}

const uploadCsv = async () => {
  if (
    !selectedFile.value ||
    !formState.tableName ||
    !formState.description ||
    !formState.assetName ||
    !formState.timeframe
  ) {
    error.value = 'Please provide all fields and file.'
    return
  }

  isUploading.value = true
  error.value = ''
  message.value = ''

  // TODO: fix api endpoint

  try {
    const formData = new FormData()
    formData.append('tableName', formState.tableName)
    formData.append('description', formState.description)
    formData.append('assetName', formState.assetName)
    formData.append('timeframe', formState.timeframe)
    formData.append('file', selectedFile.value)

    const response = await fetch('http://localhost:8080/api/csv/upload', {
      method: 'POST',
      body: formData,
      credentials: 'include'
    })

    const text = await response.text()
    if (response.ok) {
      message.value = text
      tableNamesStore.addTableName(formState.tableName)
      formState.tableName = ''
      formState.description = ''
      formState.assetName = ''
      formState.timeframe = ''
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
