<template>
  <!-- TODO: Fix height - it has to be automatic -->
  <div ref="chartContainer" style="width: auto; height: 600px; margin-top: 20px;"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as LightweightCharts from 'lightweight-charts'
import { useSelectedTableStore } from '@/stores/selectedTable'

class Datafeed {
  constructor() {
    this.id1 = null
    this.id2 = null
    this.allLoadedData = []
  }

  setId1(id) {
    this.id1 = id
  }

  setId2(id) {
    this.id2 = id
  }

  setInitialData(data) {
    this.allLoadedData = [...data]
  }

  prependData(data) {
    this.allLoadedData = [...data, ...this.allLoadedData]
  }

  getData() {
    return this.allLoadedData
  }
}

const datafeed = new Datafeed()

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable

const chartContainer = ref(null)
let chart = null
let candlestickSeries = null

let allLoadedData = [] // Store all loaded data
let earliestId = null // Track the earliest loaded ID

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
      borderVisible: false,
      wickUpColor: '#26a69a',
      upColor: '#00DC82',
      wickDownColor: '#ef5350',
      downColor: '#ef5350'
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


async function functionFetchData(beforeId, count = 50) {
  console.log(`Fetching ${count} bars before ID: ${beforeId}`)
  const currentTableName = selectedTableStore.selectedTable
  const encodedTableName = encodeURIComponent(currentTableName)
  
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase || 'http://localhost:8080'
  const url = `${backendBase}/api/betweendata?table=${encodedTableName}&id1=${beforeId - count}&id2=${beforeId}`
  
  const { data, error } = await useFetch(url, {
    credentials: 'include'
  })
  
  if (error.value) {
    console.error('Error fetching data between IDs:', error.value)
    return []
  }
  
  const chartData = (data.value || []).map(item => ({
    id: item.id,
    time: item.timestamp,
    open: item.open,
    high: item.high,
    low: item.low,
    close: item.close
  })).sort((a, b) => a.time - b.time)

  return chartData
}

function getFirstAndLastId(chartData) {
  if (chartData.length > 0) {
    const id1 = chartData[0].id
    const id2 = chartData[chartData.length - 1].id
    console.log("First ID:", id1)
    console.log("Last ID:", id2)
    return { id1, id2 }
  } else {
    console.log('No data available for the selected table.')
    return { id1: null, id2: null }
  }
}

// Fetch data from backend API and update chart
async function fetchChartData() {
  const currentTableName = selectedTableStore.selectedTable
  if (!currentTableName) return

  try {
    const encodedTableName = encodeURIComponent(currentTableName)
    console.log(`Fetching data for table: ${encodedTableName}`)

    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const url = `${backendBase}/api/data?table=${encodedTableName}`

    const { data, error } = await useFetch(url, {
      credentials: 'include'
    })

    if (error.value) {
      throw new Error('Failed to fetch chart data: ' + error.value.message)
    }

    console.log('Fetched data:', data.value)

    // Transform backend data to chart format
    const chartData = (data.value || []).map(item => ({
      id: item.id,
      time: item.timestamp,
      open: item.open,
      high: item.high,
      low: item.low,
      close: item.close
    })).sort((a, b) => a.time - b.time)

    // Initialize data management
    allLoadedData = [...chartData]

    // set Datefeed
    datafeed.setId1(chartData.length > 0 ? chartData[0].id : null)
    datafeed.setId2(chartData.length > 0 ? chartData[chartData.length - 1].id : null)

    // Store all loaded data for lazy loading
    datafeed.setInitialData(chartData)

    earliestId = chartData.length > 0 ? chartData[0].id : null

    candlestickSeries.setData(datafeed.getData())

    // Subscribe to visible logical range changes for lazy loading
    chart.timeScale().subscribeVisibleLogicalRangeChange(async (logicalRange) => {
      if (logicalRange.from < 10 && earliestId !== null) {
        console.log('Loading more historical data...')
        const numberBarsToLoad = 50 - Math.floor(logicalRange.from)
        const newData = await functionFetchData(earliestId, numberBarsToLoad)
        
        if (newData.length > 0) {
          // Prepend new data to existing data
          allLoadedData = [...newData, ...allLoadedData]
          earliestId = newData[0].id
          datafeed.setId1(newData[0].id)
          datafeed.setId2(allLoadedData[allLoadedData.length - 1].id)
          datafeed.prependData(newData)
          
          setTimeout(() => {
            candlestickSeries.setData(datafeed.getData())
            console.log('New data prepended:', datafeed.getData())
          }, 250)
        }
      }
    })

    // Store first and last IDs for reference
    const { id1: currentId1, id2: currentId2 } = getFirstAndLastId(chartData)
    id1 = currentId1
    id2 = currentId2

  } catch (error) {
    console.error(error)
  }
}
</script>