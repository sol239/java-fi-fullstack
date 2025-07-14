export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase

  try {
    const cookieHeader = event.node.req.headers.cookie || ''

    const res = await fetch(`${backendBase}/api/tables`, {
      headers: {
        // Forward the incoming cookie header to backend (to send session cookie)
        cookie: cookieHeader
      },
      // For fetch on server side, credentials: 'include' is not needed,
      // forwarding cookies manually is enough
    })

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
