<template>
  <div class="stats-page" v-loading="loading">
    <!-- 汇总指标 -->
    <div class="metric-grid">
      <div v-for="m in metrics" :key="m.label" class="metric-card" :class="m.tone">
        <div class="mc-icon">
          <el-icon><component :is="m.icon" /></el-icon>
        </div>
        <div class="mc-body">
          <span class="mc-label">{{ m.label }}</span>
          <div class="mc-value">
            <b>{{ m.value }}</b>
            <em>{{ m.unit }}</em>
          </div>
        </div>
        <div class="mc-bg"></div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="chart-grid">
      <div class="chart-card">
        <div class="cc-head">
          <div>
            <h4>各课程提问量</h4>
            <p>按课程分类统计已发布答疑帖数量</p>
          </div>
          <span class="cc-badge">柱状图</span>
        </div>
        <div ref="courseChart" class="chart chart-md"></div>
      </div>

      <div class="chart-card">
        <div class="cc-head">
          <div>
            <h4>近 7 日提问 / 回复趋势</h4>
            <p>平台每日互动活跃度变化</p>
          </div>
          <span class="cc-badge">折线图</span>
        </div>
        <div ref="trendChart" class="chart chart-md"></div>
      </div>

      <div class="chart-card">
        <div class="cc-head">
          <div>
            <h4>点赞 TOP10 答疑帖</h4>
            <p>最受同学认可的问题排行</p>
          </div>
          <span class="cc-badge">条形图</span>
        </div>
        <div ref="topChart" class="chart chart-lg"></div>
      </div>

      <div class="chart-card">
        <div class="cc-head">
          <div>
            <h4>答疑活跃用户 TOP10</h4>
            <p>按回复数量排名的热心同学</p>
          </div>
          <span class="cc-badge">条形图</span>
        </div>
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

const metrics = ref([
  { label: '课程分类', value: 0, unit: '门', icon: 'Collection', tone: 'tone-blue' },
  { label: '答疑帖总量', value: 0, unit: '条', icon: 'Document', tone: 'tone-mint' },
  { label: '近 7 日提问', value: 0, unit: '条', icon: 'EditPen', tone: 'tone-amber' },
  { label: '近 7 日回复', value: 0, unit: '条', icon: 'ChatDotRound', tone: 'tone-violet' }
])

const chartText = '#98a1ba'
const axisLine = '#eceff8'

const sum = arr => (arr || []).reduce((a, b) => a + Number(b || 0), 0)

// 柱状渐变
const barGradient = (from, to) => new echarts.graphic.LinearGradient(0, 0, 0, 1, [
  { offset: 0, color: from },
  { offset: 1, color: to }
])

// 横向渐变
const hGradient = (from, to) => new echarts.graphic.LinearGradient(0, 0, 1, 0, [
  { offset: 0, color: from },
  { offset: 1, color: to }
])

const baseTooltip = {
  backgroundColor: 'rgba(255,255,255,0.96)',
  borderColor: '#e9edf7',
  borderWidth: 1,
  padding: [10, 14],
  textStyle: { color: '#3c4566', fontSize: 12.5 },
  extraCssText: 'box-shadow: 0 8px 24px -8px rgba(37,47,100,0.22); border-radius: 10px;'
}

const createChart = el => {
  const c = echarts.init(el)
  charts.push(c)
  return c
}

const renderAll = async () => {
  const [courseData, trend, topData, activeData] = await Promise.all([
    getStatsByCourse().then(r => r.data),
    getStatsTrend().then(r => r.data),
    getStatsTopLikes().then(r => r.data),
    getStatsActiveUsers().then(r => r.data)
  ])

  // 汇总指标
  metrics.value[0].value = courseData.length
  metrics.value[1].value = sum(courseData.map(d => d.value))
  metrics.value[2].value = sum(trend.posts)
  metrics.value[3].value = sum(trend.replies)

  // 1. 各课程提问量
  createChart(courseChart.value).setOption({
    tooltip: { ...baseTooltip, trigger: 'axis', axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(91,108,249,0.06)' } } },
    grid: { left: 8, right: 16, bottom: 8, top: 24, containLabel: true },
    xAxis: {
      type: 'category',
      data: courseData.map(d => d.name),
      axisLine: { lineStyle: { color: axisLine } },
      axisTick: { show: false },
      axisLabel: { color: chartText, fontSize: 11.5, interval: 0, rotate: courseData.length > 6 ? 28 : 0 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: axisLine, type: 'dashed' } },
      axisLabel: { color: chartText, fontSize: 11.5 }
    },
    series: [{
      type: 'bar',
      data: courseData.map(d => Number(d.value)),
      barWidth: courseData.length > 8 ? 16 : 26,
      itemStyle: {
        color: barGradient('#7c8bfa', '#5b6cf9'),
        borderRadius: [6, 6, 0, 0]
      },
      emphasis: { itemStyle: { color: barGradient('#5b6cf9', '#4757e8') } }
    }]
  })

  // 2. 近7日趋势
  createChart(trendChart.value).setOption({
    tooltip: {
      ...baseTooltip,
      trigger: 'axis',
      axisPointer: { type: 'line', lineStyle: { color: '#c7cffd', type: 'dashed' } }
    },
    legend: {
      data: ['提问', '回复'],
      right: 0,
      top: 0,
      icon: 'roundRect',
      itemWidth: 8,
      itemHeight: 8,
      itemGap: 16,
      textStyle: { color: '#6c7594', fontSize: 12 }
    },
    grid: { left: 8, right: 16, bottom: 8, top: 40, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trend.dates,
      axisLine: { lineStyle: { color: axisLine } },
      axisTick: { show: false },
      axisLabel: { color: chartText, fontSize: 11.5 }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: axisLine, type: 'dashed' } },
      axisLabel: { color: chartText, fontSize: 11.5 }
    },
    series: [
      {
        name: '提问',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbolSize: 7,
        lineStyle: { width: 2.5, color: '#5b6cf9' },
        itemStyle: { color: '#5b6cf9' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(91,108,249,0.28)' },
            { offset: 1, color: 'rgba(91,108,249,0.01)' }
          ])
        },
        data: trend.posts
      },
      {
        name: '回复',
        type: 'line',
        smooth: true,
        showSymbol: false,
        symbolSize: 7,
        lineStyle: { width: 2.5, color: '#2dd4bf' },
        itemStyle: { color: '#2dd4bf' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(45,212,191,0.26)' },
            { offset: 1, color: 'rgba(45,212,191,0.01)' }
          ])
        },
        data: trend.replies
      }
    ]
  })

  // 3. 点赞TOO10
  const top = topData.slice().reverse()
  createChart(topChart.value).setOption({
    tooltip: {
      ...baseTooltip,
      trigger: 'axis',
      axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(255,171,46,0.08)' } },
      formatter: p => `${p[0].name}<br/>点赞 ${p[0].value} 次`
    },
    grid: { left: 8, right: 40, bottom: 8, top: 12, containLabel: true },
    xAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: axisLine, type: 'dashed' } },
      axisLabel: { color: chartText, fontSize: 11.5 }
    },
    yAxis: {
      type: 'category',
      data: top.map(d => d.title),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6c7594', fontSize: 11.5, width: 190, overflow: 'truncate' }
    },
    series: [{
      type: 'bar',
      data: top.map(d => d.likeCount),
      barWidth: 13,
      itemStyle: {
        color: hGradient('#ffd08a', '#ffab2e'),
        borderRadius: [0, 7, 7, 0]
      },
      label: {
        show: true,
        position: 'right',
        color: '#b98a2e',
        fontSize: 11.5,
        formatter: '{c}'
      }
    }]
  })

  // 4. 活跃用户
  const active = activeData.slice().reverse()
  createChart(activeChart.value).setOption({
    tooltip: {
      ...baseTooltip,
      trigger: 'axis',
      axisPointer: { type: 'shadow', shadowStyle: { color: 'rgba(45,212,191,0.08)' } },
      formatter: p => `${p[0].name}<br/>回复 ${p[0].value} 条`
    },
    grid: { left: 8, right: 40, bottom: 8, top: 12, containLabel: true },
    xAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: axisLine, type: 'dashed' } },
      axisLabel: { color: chartText, fontSize: 11.5 }
    },
    yAxis: {
      type: 'category',
      data: active.map(d => d.name),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6c7594', fontSize: 12 }
    },
    series: [{
      type: 'bar',
      data: active.map(d => Number(d.value)),
      barWidth: 13,
      itemStyle: {
        color: hGradient('#8ce8da', '#2dd4bf'),
        borderRadius: [0, 7, 7, 0]
      },
      label: {
        show: true,
        position: 'right',
        color: '#2a9c8c',
        fontSize: 11.5,
        formatter: '{c}'
      }
    }]
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
.stats-page {
  animation: fade-up 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 指标卡 */
.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 18px;
}

.metric-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 22px;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  box-shadow: var(--shadow-xs);
  overflow: hidden;
  transition: transform 0.24s, box-shadow 0.24s;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 30px -18px rgba(45, 57, 120, 0.35);
}

.mc-icon {
  width: 44px;
  height: 44px;
  flex: none;
  border-radius: 13px;
  display: grid;
  place-items: center;
  font-size: 19px;
  position: relative;
  z-index: 1;
}

.mc-body {
  position: relative;
  z-index: 1;
  min-width: 0;
}

.mc-label {
  font-size: 12.5px;
  color: var(--ink-400);
}

.mc-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-top: 3px;
}

.mc-value b {
  font-size: 25px;
  font-weight: 700;
  letter-spacing: -0.8px;
  color: var(--ink-900);
  line-height: 1.1;
}

.mc-value em {
  font-style: normal;
  font-size: 12px;
  color: var(--ink-400);
}

.mc-bg {
  position: absolute;
  right: -30px;
  bottom: -40px;
  width: 110px;
  height: 110px;
  border-radius: 50%;
  opacity: 0.5;
}

.tone-blue .mc-icon {
  color: var(--brand-600);
  background: var(--brand-50);
}

.tone-blue .mc-bg {
  background: radial-gradient(circle, rgba(91, 108, 249, 0.16), transparent 68%);
}

.tone-mint .mc-icon {
  color: #12977f;
  background: var(--mint-soft);
}

.tone-mint .mc-bg {
  background: radial-gradient(circle, rgba(45, 212, 191, 0.18), transparent 68%);
}

.tone-amber .mc-icon {
  color: #c07c00;
  background: var(--amber-soft);
}

.tone-amber .mc-bg {
  background: radial-gradient(circle, rgba(255, 171, 46, 0.2), transparent 68%);
}

.tone-violet .mc-icon {
  color: #7b4fd6;
  background: #f4efff;
}

.tone-violet .mc-bg {
  background: radial-gradient(circle, rgba(140, 92, 232, 0.16), transparent 68%);
}

/* 图表卡 */
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.chart-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 20px 22px 14px;
  box-shadow: var(--shadow-xs);
}

.cc-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 6px;
}

.cc-head h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--ink-900);
  letter-spacing: -0.2px;
}

.cc-head p {
  margin-top: 3px;
  font-size: 12px;
  color: var(--ink-400);
}

.cc-badge {
  flex: none;
  font-size: 11px;
  color: var(--ink-400);
  background: #f5f7fd;
  padding: 2px 9px;
  border-radius: 999px;
}

.chart-md {
  height: 268px;
}

.chart-lg {
  height: 320px;
}

@media (max-width: 1080px) {
  .metric-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
