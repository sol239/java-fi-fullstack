<template>
    <UCard>
      <div ref="chartContainer" style="width: 700px; height: 400px; margin-top: 20px;"></div>
    </UCard>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as LightweightCharts from 'lightweight-charts'
import { useSelectedTableStore } from '@/stores/selectedTable'

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable

const chartContainer = ref(null)
let chart = null
let candlestickSeries = null

onMounted(() => {
  if (chartContainer.value) {
    const chartOptions = { 
      layout: { 
        textColor: 'white', 
        background: { type: 'solid', color: '#0f172b' }
      } 
    }

    chart = LightweightCharts.createChart(chartContainer.value, chartOptions)

    candlestickSeries = chart.addSeries(LightweightCharts.CandlestickSeries, { 
      upColor: '#00DC82', 
      downColor: '#ef5350', 
      borderVisible: false, 
      wickUpColor: '#26a69a', 
      wickDownColor: '#ef5350' 
    })

    // Fetch data on mount if tableName is already set
    if (tableName) {
      fetchChartData()
    }
  }
})

// Watch for changes in the selected table from the store
watch(() => selectedTableStore.selectedTable, (newTableName) => {
  if (newTableName) {
    fetchChartData()
  }
}, { immediate: true })

// Fetch data from backend API and update chart
async function fetchChartData() {
  const currentTableName = selectedTableStore.selectedTable
  if (!currentTableName) return

  try {
    const encodedTableName = encodeURIComponent(currentTableName)
    
    console.log(`Fetching data for table: ${encodedTableName}`)

    const response = await fetch(`/api/data?table=${encodedTableName}`)
    if (!response.ok) {
      throw new Error('Failed to fetch chart data')
    }
    const data = await response.json()

    // data should be [{ open, high, low, close, time }, ...] with time as Unix timestamp
    candlestickSeries.setData(data)
  } catch (error) {
    console.error(error)
  }
}
</script>