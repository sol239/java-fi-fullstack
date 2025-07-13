<template>
  <div>
    <label for="table-select">Select Table:</label>
    <select id="table-select" v-model="selectedTable" @change="fetchChartData">
      <option disabled value="">-- Select a table --</option>
      <option v-for="table in tables" :key="table" :value="table">{{ table }}</option>
    </select>

    <div ref="chartContainer" style="width: 700px; height: 400px; margin-top: 20px;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as LightweightCharts from 'lightweight-charts'
import { useTableNamesStore } from '@/stores/tableNames'

const tableNamesStore = useTableNamesStore()
const tables = tableNamesStore.tableNameList


console.log(tables)  // Debugging: Check the table names

const selectedTable = ref('')

const chartContainer = ref(null)
let chart = null
let lineSeries = null

onMounted(() => {
  chart = LightweightCharts.createChart(chartContainer.value, { width: 700, height: 400 })
  lineSeries = chart.addSeries(LightweightCharts.LineSeries)
})

// Fetch data from backend API and update chart
async function fetchChartData() {
  if (!selectedTable.value) return

  try {
    const response = await fetch(`http://localhost:8080/api/data?table=${encodeURIComponent(selectedTable.value)}`)
    if (!response.ok) {
      throw new Error('Failed to fetch chart data')
    }
    const data = await response.json()

    // data should be [{ time, value }, ...] with time as string (YYYY-MM-DD format)
    lineSeries.setData(data)
  } catch (error) {
    console.error(error)
    alert('Error loading chart data')
  }
}
</script>

<style scoped>
select {
  padding: 6px 12px;
  font-size: 16px;
}
</style>
