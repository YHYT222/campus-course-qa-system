<template>
  <div class="stats-wrap" v-loading="loading">
    <div class="stats-grid">
      <div class="stats-card">
        <h4>📊 各课程提问量</h4>
        <div ref="courseChart" class="chart chart-md"></div>
      </div>
      <div class="stats-card">
        <h4>📈 近7日提问 / 回复趋势</h4>
        <div ref="trendChart" class="chart chart-md"></div>
      </div>
      <div class="stats-card">
        <h4>🔥 点赞TOP10答疑帖</h4>
        <div ref="topChart" class="chart chart-lg"></div>
      </div>
      <div class="stats-card">
        <h4>🏅 答疑活跃用户TOP10（按回复数）</h4>
        <div ref="activeChart" class="chart chart-lg"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getStatsByCourse, getStatsTrend, getStatsTopLikes, getStatsActiveUsers } from '../../api'

const loading = ref(true)
const courseChart = ref(null)
const trendChart = ref(null)
const topChart = ref(null)
const activeChart = ref(null)

const charts = []

const createChart = el => {
  const c = echarts.init(el)
  charts.push(c)
  return c
}

const renderAll = async () => {
  // 1. 各课程提问量
  const courseData = (await getStatsByCourse()).data
  createChart(courseChart.value).setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, bottom: 30, top: 20 },
    xAxis: { type: 'category', data: courseData.map(d => d.name), axisLabel: { rotate: 30 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: courseData.map(d => Number(d.value)), barWidth: 28,
               itemStyle: { color: '#3b82c4', borderRadius: [4, 4, 0, 0] } }]
  })

  // 2. 近7日趋势
  const trend = (await getStatsTrend()).data
  createChart(trendChart.value).setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['提问', '回复'], top: 0 },
    grid: { left: 40, right: 20, bottom: 30, top: 36 },
    xAxis: { type: 'category', data: trend.dates },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '提问', type: 'line', smooth: true, data: trend.posts, itemStyle: { color: '#2b6cb0' } },
      { name: '回复', type: 'line', smooth: true, data: trend.replies, itemStyle: { color: '#48bb78' } }
    ]
  })

  // 3. 点赞TOP10
  const topData = (await getStatsTopLikes()).data.slice().reverse()
  createChart(topChart.value).setOption({
    tooltip: { trigger: 'axis', formatter: p => `${p[0].name}<br/>👍 ${p[0].value} 个赞` },
    grid: { left: 220, right: 40, bottom: 30, top: 10 },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: topData.map(d => d.title.slice(0, 18)),
             axisLabel: { width: 200, overflow: 'truncate' } },
    series: [{ type: 'bar', data: topData.map(d => d.likeCount), barWidth: 14,
               itemStyle: { color: '#ed8936', borderRadius: [0, 4, 4, 0] } }]
  })

  // 4. 活跃用户
  const activeData = (await getStatsActiveUsers()).data.slice().reverse()
  createChart(activeChart.value).setOption({
    tooltip: { trigger: 'axis', formatter: p => `${p[0].name}<br/>💬 ${p[0].value} 条回复` },
    grid: { left: 90, right: 40, bottom: 30, top: 10 },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: activeData.map(d => d.name) },
    series: [{ type: 'bar', data: activeData.map(d => Number(d.value)), barWidth: 14,
               itemStyle: { color: '#2f855a', borderRadius: [0, 4, 4, 0] } }]
  })

  loading.value = false
}

const onResize = () => charts.forEach(c => c.resize())

onMounted(async () => {
  try {
    await renderAll()
  } catch (e) {
    loading.value = false
  }
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  charts.forEach(c => c.dispose())
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stats-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 22px;
}

.stats-card h4 {
  color: #1a365d;
  margin-bottom: 10px;
}

.chart-md {
  height: 300px;
}

.chart-lg {
  height: 360px;
}

@media (max-width: 1000px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
