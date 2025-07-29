<template>
    <UContainer class="py-16 flex flex-col items-center gap-8">
        <UCard class="max-w-xl w-full text-center">
            <h1 class="text-4xl font-bold mb-4">Welcome to Java-Fi 🚀</h1>
            <p class="mb-6 text-lg">
               This is a fullstack application built with Spring Boot and Nuxt 3 used for analysing market data.
            </p>
            <div class="flex flex-col gap-4 items-center">
                <UButton
                    to="https://github.com/sol239/java-fi-fullstack"
                    target="_blank"
                    icon="i-simple-icons-github"
                    color="secondary"
                    variant="solid"
                    size="lg"
                    class="w-48"
                >
                    View on GitHub
                </UButton>
            </div>
        </UCard>
        <UCard id="about" class="max-w-xl w-full mt-8">
            <h2 class="text-2xl font-semibold mb-2">About</h2>
            <p>
                You can find more information about this project on <b>GitHub</b>.
            </p>
        </UCard>
    </UContainer>
</template>

<script setup lang="ts">

</script>
