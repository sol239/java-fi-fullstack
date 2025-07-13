export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const backendBase = config.backendBase
  try {
    const res = await fetch(`${backendBase}/api/tables`)
    
    if (!res.ok) {
      throw new Error(`Backend responded with status: ${res.status}`)
    }
    
    const tables = await res.json()
    return tables
  } catch (error) {
    console.error('Error fetching tables:', error)
    throw createError({
      statusCode: 500,
      statusMessage: 'Failed to fetch table names'
    })
  }
})
