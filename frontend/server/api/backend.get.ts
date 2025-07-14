export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase

  const url = `${backendBase}/`;

  const res = await fetch(url)
  return await res.text()
})
