<template>
  <div class="chart-container">
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Chart, registerables } from 'chart.js'

const props = defineProps(['data'])
const chartCanvas = ref(null)
let chartInstance = null

Chart.register(...registerables)

onMounted(() => {
  initChart()
})

watch(() => props.data, (newData) => {
  if (chartInstance) {
    chartInstance.data = newData
    chartInstance.update()
  }
})

const initChart = () => {
  if (chartCanvas.value) {
    chartInstance = new Chart(chartCanvas.value, {
      type: 'line',
      data: props.data,
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    })
  }
}
</script>