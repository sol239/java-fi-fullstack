// server/api/data.get.ts
function generateMockData(startDate: string, count: number, startValue: number): Array<{ open: number, high: number, low: number, close: number, time: number }> {
  const data = []
  let currentTimestamp = Math.floor(new Date(startDate).getTime() / 1000) // Convert to Unix timestamp
  let previousClose = startValue
  
  for (let i = 0; i < count; i++) {
    const open = previousClose
    
    // Generate realistic price movements
    const baseChange = (Math.random() - 0.5) * 2 // -1 to 1
    const volatility = 0.5 + Math.random() * 0.5 // 0.5 to 1.0
    
    // Calculate high and low based on open price
    const highChange = Math.random() * volatility
    const lowChange = -Math.random() * volatility
    
    const high = Math.max(open + highChange, open) // High should be at least open
    const low = Math.min(open + lowChange, open) // Low should be at most open
    
    // Close price should be between low and high
    const closeRange = high - low
    const closePosition = Math.random() // 0 to 1
    const close = low + (closeRange * closePosition)
    
    // Ensure realistic OHLC relationships
    const finalHigh = Math.max(open, high, close)
    const finalLow = Math.min(open, low, close)
    
    data.push({
      open: Math.round(open * 100) / 100, // Round to 2 decimal places
      high: Math.round(finalHigh * 100) / 100,
      low: Math.round(finalLow * 100) / 100,
      close: Math.round(close * 100) / 100,
      time: currentTimestamp
    })
    
    previousClose = close
    currentTimestamp += 86400 // Add 24 hours (86400 seconds)
  }
  
  return data
}

export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const table = query.table
  
  const mockData: Record<string, Array<{ open: number, high: number, low: number, close: number, time: number }>> = {
    BTC: generateMockData('2024-01-01', 100, 100),
    BTC1: generateMockData('2024-01-01', 100, 200),
    ETH: generateMockData('2024-01-01', 100, 300),
    default: [
      { open: 10, high: 10.63, low: 9.49, close: 9.55, time: 1642427876 },
      { open: 9.55, high: 10.30, low: 9.42, close: 9.94, time: 1642514276 },
      { open: 9.94, high: 10.17, low: 9.92, close: 9.78, time: 1642600676 },
      { open: 9.78, high: 10.59, low: 9.18, close: 9.51, time: 1642687076 },
      { open: 9.51, high: 10.46, low: 9.10, close: 10.17, time: 1642773476 },
      { open: 10.17, high: 10.96, low: 10.16, close: 10.47, time: 1642859876 },
      { open: 10.47, high: 11.39, low: 10.40, close: 10.81, time: 1642946276 },
      { open: 10.81, high: 11.60, low: 10.30, close: 10.75, time: 1643032676 },
      { open: 10.75, high: 11.60, low: 10.49, close: 10.93, time: 1643119076 },
      { open: 10.93, high: 11.53, low: 10.76, close: 10.96, time: 1643205476 }
    ]
  }
  
  return typeof table === 'string' && mockData[table] ? mockData[table] : mockData['default']
})