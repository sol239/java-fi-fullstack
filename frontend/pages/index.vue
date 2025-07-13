<template>
    <div>
        <h1>Spring Boot returns: {{ message }}</h1>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const message = ref('Loading...')

onMounted(async () => {
    try {
        const res = await fetch('/api/backend')
        message.value = await res.text()
    } catch (e) {
        message.value = 'Error connecting to Spring Boot'
        console.error(e)
    }
})
</script>
