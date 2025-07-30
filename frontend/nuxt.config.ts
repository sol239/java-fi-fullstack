export default defineNuxtConfig({
  compatibilityDate: '2025-05-15',
  devtools: { enabled: true },
  vite: {},

  devServer: {
    port: Number(process.env.PORT) || 3001,
    host: '0.0.0.0',
  },

  css: ['~/app/assets/css/main.css'],
  sourcemap: false,

  modules: [
    '@nuxt/eslint',
    '@nuxt/scripts',
    '@nuxt/ui',
    '@pinia/nuxt',
  ],

  runtimeConfig: {
    public: {
      backendBase: process.env.BACKEND_BASE || 'http://localhost:8080',
    },
  },

  ssr: false, // disables server-side rendering


  nitro: {
    preset: 'static', // tells Nuxt to statically generate the site
  },

});
