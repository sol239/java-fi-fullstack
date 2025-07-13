export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const backendBase = config.backendBase
  const res = await fetch(`${backendBase}/`)
  return await res.text()
})
