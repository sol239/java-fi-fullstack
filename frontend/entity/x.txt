<template>
  <!-- TODO: Fix height - it has to be automatic -->
  <div ref="chartContainer" style="width: auto; height: 600px; margin-top: 20px;"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as LightweightCharts from 'lightweight-charts'
import { useSelectedTableStore } from '@/stores/selectedTable'

function dedupeAndSortChartData(data) {


  console.log("Dedupe and sort chart data:", data);

  // Remove duplicates by time
  const seen = new Set();
  const filtered = data.filter(item => {
    if (seen.has(item.time)) return false;
    seen.add(item.time);
    return true;
  });
  // Sort ascending by time
  return filtered.sort((a, b) => a.time - b.time);
}
class Datafeed {
  constructor() {
    this._earliestId = 0;
    this._data = [];
  }

  setEarliestId(earliestId) {
    this._earliestId = earliestId;
  }

  async getBars(numberOfExtraBars) {
    const historicalData = await functionFetchData(this._earliestId, numberOfExtraBars);
    this._data = [...historicalData, ...this._data];
    this._earliestId = historicalData[0].id;
    return this._data;
  }
}

class Chart {
  constructor(container) {
    this.series = null
    this.container = container;
    this.chart = null;
    this.chartSettings = null;
    this.datafeed = new Datafeed();
    this.lastIndex = "0";
  }

  activateInfinity() {
    this.chart.timeScale().subscribeVisibleLogicalRangeChange(async logicalRange => {
      // Add null check for logicalRange
      if (logicalRange && logicalRange.from < 10) {
        // load more data
        const numberBarsToLoad = 50 - logicalRange.from;
        const data = await this.datafeed.getBars(numberBarsToLoad);


        const chartData =
          (data || []).map(item => ({
            id: item.id,
            time: item.timestamp,
            open: item.open,
            high: item.high,
            low: item.low,
            close: item.close
          }))
          console.log("Data reordered!");
          chartData = dedupeAndSortChartData(chartData);

        setTimeout(() => {
          console.log("SETTING NEW", chartData);
          this.series.setData(chartData);
        }, 2000);
      }
    });
  }

  setLastIndex(lastIndex) {
    this.lastIndex = lastIndex;
  }

  getLastIndex() {
    return this.lastIndex;
  }

  clearLastIndex() {
    this.lastIndex = "0";
  }

  clearDatafeed() {
    this.datafeed = new Datafeed();
  }

  setDatafeed(datafeed) {
    this.datafeed = datafeed;
  }

  setChartSettings(settings) {
    this.chartSettings = settings;
  }

  setSeries() {
    this.series = this.chart.addSeries(LightweightCharts.CandlestickSeries, {
      borderVisible: false,
      wickUpColor: '#26a69a',
      upColor: '#00DC82',
      wickDownColor: '#ef5350',
      downColor: '#ef5350'
    })

  }



  async setSeriesData(count) {
    const bars = await this.datafeed.getBars(count);
    if (this.series) {
      console.log("Setting series data:", bars);
      this.series.setData(bars);
    }
    else {
      console.error("Series is not set, cannot set data.");
    }
  }

  clearSeries() {
    this.series = null;
  }

  clearChart() {
    this.chart = null;
  }


  createChart() {
    this.chart = LightweightCharts.createChart(this.container.value, this.chartSettings)
  }

}
async function functionFetchData(beforeId, count = 50) {

  beforeId = Math.ceil(beforeId);
  count = Math.ceil(count);

  console.log(`Fetching data before ID: ${beforeId}, count: ${count}`);

  const currentTableName = selectedTableStore.selectedTable
  const encodedTableName = encodeURIComponent(currentTableName)
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase || 'http://localhost:8080'
  const url = `${backendBase}/api/betweendata?table=${encodedTableName}&id1=${beforeId - count}&id2=${beforeId}`
  console.log(`Fetching data from: ${url}`)

  // Use $fetch instead of useFetch
  const data = await $fetch(url, {
    credentials: 'include'
  })

  console.log("Fetched data:", data)

  const chartData = (data || []).map(item => ({
    id: item.id,
    time: item.timestamp,
    open: item.open,
    high: item.high,
    low: item.low,
    close: item.close
  })).sort((a, b) => a.time - b.time)

  return chartData
}

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable
console.log('Selected table:', tableName)

const chartContainer = ref(null)

const ch = new Chart(chartContainer)

let lastIndex = "0";

async function getTableLastIndex(tableName) {
  if (tableName) {
    const encodedTableName = encodeURIComponent(tableName)
    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const res = await fetch(`${backendBase}/api/tables/last?tableName=${encodedTableName}`, {
      credentials: 'include'
    })
    lastIndex = await res.text()
    return lastIndex;
  } else {
    console.warn('No table selected, skipping last index fetch.')
    return "0";
  }
}

onMounted(async () => {
  if (chartContainer.value) {

    lastIndex = await getTableLastIndex(tableName)
    console.log('Last index for table:', lastIndex)

    const chartOptions = {
      layout: {
        textColor: 'white',
        background: { type: 'solid', color: '#0f172b' }
      }
    }

    ch.setChartSettings(chartOptions)
    ch.createChart()
    ch.setSeries()

    const datafeed = new Datafeed();
    ch.setDatafeed(datafeed);
    ch.setLastIndex(lastIndex)
    ch.datafeed.setEarliestId(Number(lastIndex))
    ch.setSeriesData(200)

    ch.activateInfinity()




  }
})

watch(() => selectedTableStore.selectedTable, async (newTableName) => {
  if (newTableName) {
    lastIndex = await getTableLastIndex(newTableName)
    console.log('Last index for new table:', lastIndex)
    //const chartData = await fetchChartData()
    //ch.setSeriesData(chartData)
  }
}, { immediate: true })


async function fetchChartData() {
  const currentTableName = selectedTableStore.selectedTable
  if (!currentTableName) return []

  try {
    const encodedTableName = encodeURIComponent(currentTableName)
    console.log(`Fetching data for table: ${encodedTableName}`)

    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const url = `${backendBase}/api/data?table=${encodedTableName}`

    // Use $fetch for client-side requests
    const data = await $fetch(url, {
      credentials: 'include'
    })

    // Transform backend data to chart format
    const chartData = (data || []).map(item => ({
      id: item.id,
      time: item.timestamp,
      open: item.open,
      high: item.high,
      low: item.low,
      close: item.close
    })).sort((a, b) => a.time - b.time)

    return chartData

  } catch (error) {
    console.error(error)
    return []
  }
}


</script>