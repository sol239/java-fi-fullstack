export default defineEventHandler(async (event) => {

  
  const query = getQuery(event)
  const table = query.table
  
  if (!table || typeof table !== 'string') {
    throw createError({
      statusCode: 400,
      statusMessage: 'Table parameter is required'
    })
  }
  
  const config = useRuntimeConfig()
  const backendUrl = `${config.public.backendBase}/api/data`
  
  // Get headers from the original request
  const headers = getHeaders(event)
  
  try {
    const response = await $fetch<Array<{
      timestamp: number,
      open: number,
      high: number,
      low: number,
      close: number,
      volume: number,
      date: string
    }>>(backendUrl, {
      method: 'GET',
      query: {
        table: table
      },
      // Forward the cookies and authorization headers
      headers: {
        'cookie': headers.cookie || '',
        'authorization': headers.authorization || '',
        // Forward any other auth-related headers your backend expects
      }
    })
    
    if (!response || response.length === 0) {
      throw createError({
        statusCode: 404,
        statusMessage: `No data found for table: ${table}`
      })
    }
    
    return response
  } catch (error: any) {
    console.error('Error fetching data from backend:', error)
    if (error?.status === 401 || error?.statusCode === 401) {
      throw createError({
        statusCode: 401,
        statusMessage: 'Unauthorized: Backend requires authentication. Please log in to the backend before accessing this resource.'
      })
    }
    throw createError({
      statusCode: 500,
      statusMessage: `Failed to fetch data from backend for table: ${table}`
    })
  }
})