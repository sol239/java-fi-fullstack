<script setup>
import { ref, onMounted, watch } from 'vue'
import * as LightweightCharts from 'lightweight-charts'
import { useSelectedTableStore } from '~/stores/useSelectedTableStore'
const backtestResultStore = useBacktestResultStore()
const { backtestResult } = storeToRefs(backtestResultStore)

const markersVisible = ref(false)

class Datafeed {
  constructor() {
    this._earliestId = 0;
    this._data = [];

    // TODO: Use a reactive store or state management solution
    if (typeof window !== 'undefined') {
      window.lastIndex1Reached = false;
    }
  }

  getData() {
    return this._data;
  }

  removeDuplicates() {
    const seen = new Set();
    this._data = this._data.filter(item => {
      if (seen.has(item.time)) return false;
      seen.add(item.time);
      return true;
    });
  }

  setEarliestId(earliestId) {
    this._earliestId = earliestId;
  }

  async getBars(numberOfExtraBars) {
    // Stop fetching if we've reached the last index
    if (window.lastIndex1Reached) {
      console.log("No more data to fetch (lastIndex1 reached).");
      return this._data;
    }


    console.log("FETCHING: ", this._earliestId, numberOfExtraBars)
    const historicalData = await functionFetchData(this._earliestId, numberOfExtraBars);
    console.log("Historical data length:", historicalData.length);
    /*
    if (historicalData.length === 0) {
      console.log("REACHED 1")
      window.lastIndex1Reached = true;
      console.log("Set lastIndex1Reached = true");
      console.log(this.earliestId)
      return this._data;
    }
      */
    this._data = [...historicalData, ...this._data];
    this.removeDuplicates();
    this._earliestId = historicalData[0].id;
    /*
    if (this._earliestId <= 1) { // replace 1 with your actual lastIndex1 value if needed
      cosole.log("REACHED 2")
      window.lastIndex1Reached = true;
      console.log("Set lastIndex1Reached = true");
      console.log(this.earliestId)

    }*/
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
    this.markers = [];
    this.markerPrimitive = null;
  }

  clearMarkers() {
    this.markers = [];
  }

  addMarkers() {
    if (!backtestResult.value || !backtestResult.value.allTrades) {
      console.warn('No backtest result or trades available.');
      return;
    }
    for (const marker of backtestResult.value.allTrades) {

      const randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0');
      const buyColor = randomColor;
      const sellColor = randomColor;



      this.markers.push({
        time: marker.openTimestamp,
        position: 'belowBar',
        color: buyColor,
        shape: 'arrowUp',
        text: "BUY @ OPEN: " + Math.floor(marker.openPrice)
      });

      this.markers.push({
        time: marker.closeTimestamp,
        position: 'aboveBar',
        color: sellColor,
        shape: 'arrowDown',
        text: "Sell @ PROFIT: " + Math.floor(marker.PnL)
      });
    }
  }

  showMarkers() {
    if (markersVisible.value) {
      this.markerPrimitive.setMarkers([]);
      this.markerPrimitive.detach();

      markersVisible.value = false
    } else {
      // Only show markers that fit in the range of loaded data
      const loadedTimes = new Set(this.datafeed.getData().map(bar => bar.time))
      const visibleMarkers = this.markers.filter(m => loadedTimes.has(m.time))
      console.log("Visible markers count:", visibleMarkers.length)
      console.log("All markers count:", this.markers.length)
      this.markerPrimitive = LightweightCharts.createSeriesMarkers(this.series, visibleMarkers)
      markersVisible.value = true
    }
  }

  activateInfinity() {
    this.chart.timeScale().subscribeVisibleLogicalRangeChange(async logicalRange => {
      // Add null check for logicalRange
      if (logicalRange && logicalRange.from < 10) {
        // load more data
        const numberBarsToLoad = 50 - logicalRange.from;
        const data = await this.datafeed.getBars(numberBarsToLoad);


        setTimeout(() => {
          this.series.setData(data);
        }, 50);
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
      this.series.setData(bars);
    }
    else {
      console.error("Series is not set, cannot set data.");
    }
  }

  clearSeries() {
    if (this.series && this.chart) {
      this.chart.removeSeries(this.series);
    }
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

  const currentTableName = selectedTableStore.selectedTable
  const encodedTableName = encodeURIComponent(currentTableName)
  const config = useRuntimeConfig()
  const backendBase = config.public.backendBase || 'http://localhost:8080'
  
  console.log("ID = ", beforeId, "COUNT = ", count, "TABLE = ", encodedTableName)
  const url = `${backendBase}/api/between?table=${encodedTableName}&id1=${beforeId - count}&id2=${beforeId}`

  try {
    const data = await $fetch(url, {
      credentials: 'include'
    })

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
    console.error('Error fetching data:', error)
    return []
  }
}

const selectedTableStore = useSelectedTableStore()
const tableName = selectedTableStore.selectedTable
const chartContainer = ref(null)
const ch = new Chart(chartContainer)

let lastIndex = "0";

async function getTableLastIndex(tableName) {
  if (tableName) {
    const encodedTableName = encodeURIComponent(tableName)
    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const res = await fetch(`${backendBase}/api/tables/lastId?tableName=${encodedTableName}`, {
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
  window.lastIndex1Reached = false; // Reset flag on mount

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
    ch.activateInfinity()

    await ch.setSeriesData(200) // <-- await here
    ch.addMarkers()

  }
})

watch(() => selectedTableStore.selectedTable, async (newTableName) => {
  if (newTableName) {


    lastIndex = await getTableLastIndex(newTableName)
    window.lastIndex1Reached = false; // reset global flag
    console.log("SWITCHING TABLE TO:", newTableName)
    console.log('Last index for new table:', lastIndex)
    // Vytvoř nový datafeed a nastav ho do grafu
    const datafeed = new Datafeed();
    ch.setDatafeed(datafeed);
    ch.setLastIndex(lastIndex);
    ch.datafeed.setEarliestId(Number(lastIndex));
    ch.clearMarkers(); // Clear previous markers
    ch.clearSeries(); // Clear previous series

    ch.setSeries(); // <-- ADD THIS LINE to create a new series

    ch.addMarkers(); // Add markers for the new table
    // Nastav data do grafu
    await ch.setSeriesData(200);

    // Fetch table data for UTable when chartView is false
    data.value = await fetchChartData()
  }
}, { immediate: true })

const chartView = ref(true)
const data = ref([]) // <-- Add reactive data for table

watch(chartView, async (val) => {
  if (!val) {
    // When switching to table view, fetch data
    data.value = await fetchChartData()
  }
})

async function fetchChartData() {
  const currentTableName = selectedTableStore.selectedTable
  if (!currentTableName) return []

  try {
    const encodedTableName = encodeURIComponent(currentTableName)
    console.log(`Fetching data for table: ${encodedTableName}`)

    const config = useRuntimeConfig()
    const backendBase = config.public.backendBase || 'http://localhost:8080'
    const url = `${backendBase}/api/lastRows?table=${encodedTableName}`

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

<template>
  <UButton
    color="primary"
    @click="ch.showMarkers()"
    :icon="markersVisible ? 'i-heroicons-eye-slash' : 'i-heroicons-eye'"
    :variant="markersVisible ? 'outline' : 'solid'"
    size="md"
    class="mb-2"
  >
    {{ markersVisible ? 'Hide Markers' : 'Show Markers' }}
  </UButton>


  <div v-if="chartView" ref="chartContainer" style="width: auto; height: 600px; margin-top: 20px;"></div>

  <div v-else>
    <UTable :data="data" class="flex-1" />
  </div>

</template>