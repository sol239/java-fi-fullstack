<template>
    <UContainer class="py-16 flex flex-col items-center gap-8">
        <UCard class="max-w-xl w-full text-center">
            <h1 class="text-4xl font-bold mb-4">Welcome to My Project 🚀</h1>
            <p class="mb-6 text-lg">
                This is a Nuxt UI-powered landing page. Explore the features, check out the code, and learn more about this project below.
            </p>
            <div class="flex flex-col gap-4 items-center">
                <UButton
                    to="https://github.com/"
                    target="_blank"
                    icon="i-simple-icons-github"
                    color="secondary"
                    variant="solid"
                    size="lg"
                    class="w-48"
                >
                    View on GitHub
                </UButton>
                <UButton
                    to="#about"
                    color="primary"
                    variant="outline"
                    size="lg"
                    class="w-48"
                >
                    About
                </UButton>
            </div>
        </UCard>
        <UCard id="about" class="max-w-xl w-full mt-8">
            <h2 class="text-2xl font-semibold mb-2">About</h2>
            <p>
                This project demonstrates a Nuxt 3 frontend using Nuxt UI components for rapid development and beautiful design.
                <br />
                Use the navigation above to explore features or visit the GitHub repository for source code and documentation.
            </p>
        </UCard>
    </UContainer>
</template>

<script setup lang="ts">

</script>
