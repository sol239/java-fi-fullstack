<template>
  <div class="p-6 bg-white rounded-2xl shadow-md max-w-md mx-auto">
    <h2 class="text-xl font-semibold mb-4">Upload CSV File</h2>
    <form @submit.prevent="uploadCsv">
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Table Name</label>
        <input
          v-model="tableName"
          type="text"
          placeholder="Enter table name"
          class="w-full px-3 py-2 border rounded-md"
          required
        />
      </div>
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">CSV File</label>
        <input
          type="file"
          accept=".csv"
          @change="handleFileChange"
          class="w-full"
          required
        />
      </div>
      <button
        type="submit"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md"
        :disabled="isUploading"
      >
        {{ isUploading ? "Uploading..." : "Upload" }}
      </button>
      <p v-if="message" class="mt-4 text-sm text-green-600">{{ message }}</p>
      <p v-if="error" class="mt-4 text-sm text-red-600">{{ error }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tableName = ref('')
const selectedFile = ref(null)
const message = ref('')
const error = ref('')
const isUploading = ref(false)

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0]
}

const uploadCsv = async () => {
  if (!selectedFile.value || !tableName.value) {
    error.value = 'Please provide both table name and file.'
    return
  }

  isUploading.value = true
  error.value = ''
  message.value = ''

  try {
    const formData = new FormData()
    formData.append('tableName', tableName.value)
    formData.append('file', selectedFile.value)

    const response = await fetch('http://localhost:8080/api/csv/upload', {
      method: 'POST',
      body: formData,
    })

    const text = await response.text()
    if (response.ok) {
      message.value = text
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

<style scoped>
input[type="file"] {
  padding: 6px 0;
}
</style>
