
export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  vite: {},

  devServer: {
    port: Number(process.env.PORT) || 3000,
    host: '0.0.0.0'
  },

  css: ['~/app/assets/css/main.css'],
  sourcemap: false,

  modules: [
    '@nuxt/content',
    '@nuxt/eslint',
    '@nuxt/fonts',
    '@nuxt/icon',
    '@nuxt/image',
    '@nuxt/scripts',
    '@nuxt/test-utils',
    '@nuxt/ui',
    '@pinia/nuxt'
  ],

  runtimeConfig: {
    public: {
      backendBase: process.env.BACKEND_BASE || 'http://localhost:8080'
    }
  }


})