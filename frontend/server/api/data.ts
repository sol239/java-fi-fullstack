// server/api/data.js
export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const table = query.table

  // Dummy data for demonstration
  // Replace with your actual DB query logic
  const sampleData = {
    BTC1: [
      { time: '2024-07-01', value: 100 },
      { time: '2024-07-02', value: 110 },
      { time: '2024-07-03', value: 120 }
    ],
    BTC: [
      { time: '2024-07-01', value: 200 },
      { time: '2024-07-02', value: 210 },
      { time: '2024-07-03', value: 220 }
    ]
  }

  return sampleData[table] || []
})
