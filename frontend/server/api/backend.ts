export default defineEventHandler(async (event) => {
  const res = await fetch('http://localhost:8080/')
  return await res.text()
})
