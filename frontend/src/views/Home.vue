<template>
  <div class="home">
    <!-- 欢迎横幅 -->
    <section class="hero">
      <div class="hero-left">
        <div class="hero-eyebrow">
          <span class="dot"></span>
          {{ greeting }}，{{ store.user?.username || '同学' }}
        </div>
        <h1 class="hero-title">
          有问题，<em>问同学</em>。
        </h1>
        <p class="hero-desc">
          按课程分类提问，同学跟帖解答。每一次答疑都会沉淀下来，成为下一届同学能搜到的答案。
        </p>
        <div class="hero-actions">
          <button class="btn-solid" @click="$router.push('/publish')">
            发布问题
            <el-icon><ArrowRight /></el-icon>
          </button>
          <button class="btn-line" @click="scrollToFeed">浏览答疑</button>
        </div>
      </div>

      <div class="hero-stats">
        <div class="stat-cell">
          <b>{{ courses.length }}</b>
          <span>门课程分类</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-cell">
          <b>{{ total }}</b>
          <span>条公开答疑</span>
        </div>
        <div class="stat-note">
          <el-icon><TrendCharts /></el-icon>
          数据实时统计
        </div>
      </div>
    </section>

    <!-- 主体 -->
    <div class="home-body">
      <!-- 侧栏 -->
      <aside class="side">
        <div class="side-card">
          <div class="side-head">课程分类</div>
          <div class="course-nav">
            <div class="course-row" :class="{ active: !query.courseId }" @click="pickCourse(null)">
              <span class="row-dot"></span>
              <span class="row-name">全部课程</span>
            </div>
            <div v-for="c in courses" :key="c.id" class="course-row"
                 :class="{ active: query.courseId === c.id }" @click="pickCourse(c.id)">
              <span class="row-dot"></span>
              <span class="row-name">{{ c.name }}</span>
            </div>
          </div>
        </div>

        <div class="tip-card">
          <div class="tip-title">
            <el-icon><MagicStick /></el-icon>
            怎样提问更容易被回答
          </div>
          <ol class="tip-list">
            <li>写清题目出处与已知条件</li>
            <li>说明自己卡在哪一步</li>
            <li>配一张题目截图，减少来回确认</li>
          </ol>
          <div class="tip-foot">帖子需管理员审核后公开展示</div>
        </div>
      </aside>

      <!-- 信息流 -->
      <main class="feed" ref="feedRef">
        <div class="feed-bar">
          <el-input v-model="keyword" class="search-input" placeholder="搜索问题标题或内容"
                    clearable @keyup.enter="load(1)" @clear="load(1)">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="load(1)">搜索</el-button>
            </template>
          </el-input>
          <div class="feed-count">
            共 <b>{{ total }}</b> 条
          </div>
        </div>

        <div v-if="list.length" class="feed-list">
          <article v-for="p in list" :key="p.id" class="qa-card"
                   @click="$router.push(`/post/${p.id}`)">
            <div class="qa-head">
              <span class="qa-course">{{ p.courseName }}</span>
              <span class="qa-time">{{ formatTime(p.createTime) }}</span>
            </div>

            <h3 class="qa-title">{{ p.title }}</h3>
            <p class="qa-excerpt">{{ p.content }}</p>

            <div class="qa-foot">
              <div class="qa-author">
                <el-avatar :size="24" :src="p.authorAvatar || undefined" class="mini-avatar">
                  {{ (p.authorName || '学').charAt(0) }}
                </el-avatar>
                <span>{{ p.authorName }}</span>
              </div>
              <div class="qa-metrics">
                <span class="metric">
                  <el-icon><ChatDotRound /></el-icon>{{ p.replyCount }}
                </span>
                <span class="metric">
                  <el-icon><Star /></el-icon>{{ p.likeCount }}
                </span>
              </div>
            </div>
          </article>

          <div class="pager">
            <el-pagination background layout="prev, pager, next" :total="total"
                           :page-size="query.size" :current-page="query.page"
                           @current-change="onPage" />
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">
            <el-icon><ChatLineSquare /></el-icon>
          </div>
          <h4>{{ keyword ? '没有找到相关问题' : '还没有答疑帖' }}</h4>
          <p>{{ keyword ? '换个关键词试试，或者直接发布你的问题' : '成为第一个提问的人，让同学来帮你' }}</p>
          <button class="btn-solid small" @click="$router.push('/publish')">发布问题</button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getCourseList, getPostPage } from '../api'
import { useUserStore } from '../stores/user'

const store = useUserStore()
const courses = ref([])
const list = ref([])
const total = ref(0)
const keyword = ref('')
const query = reactive({ page: 1, size: 10, courseId: null })
const feedRef = ref(null)

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 11) return '早上好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const load = async (page = query.page) => {
  query.page = page
  const res = await getPostPage({
    page: query.page,
    size: query.size,
    courseId: query.courseId || undefined,
    keyword: keyword.value.trim() || undefined
  })
  list.value = res.data.records
  total.value = Number(res.data.total)
}

const pickCourse = id => {
  query.courseId = id
  load(1)
}

const onPage = page => {
  load(page)
  scrollToFeed()
}

const scrollToFeed = () => {
  feedRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

onMounted(async () => {
  const res = await getCourseList()
  courses.value = res.data
  load(1)
})
</script>

<style scoped>
.home {
  max-width: 1120px;
  margin: 0 auto;
  padding: 28px 20px 70px;
  animation: fade-up 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

/* ========== Hero ========== */
.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  padding: 40px 44px;
  border-radius: var(--r-xl);
  overflow: hidden;
  color: #fff;
  background: linear-gradient(120deg, #4655e8 0%, #5b6cf9 46%, #7a5cf0 100%);
  box-shadow: 0 26px 50px -28px rgba(70, 85, 232, 0.85);
  margin-bottom: 26px;
}

.hero::before {
  content: '';
  position: absolute;
  width: 420px;
  height: 420px;
  right: -120px;
  top: -180px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.24), transparent 66%);
}

.hero::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  left: 34%;
  bottom: -180px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(45, 212, 191, 0.4), transparent 68%);
}

.hero-left {
  position: relative;
  z-index: 1;
  max-width: 560px;
}

.hero-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 5px 14px 5px 10px;
  border-radius: 999px;
  font-size: 12.5px;
  letter-spacing: 0.3px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.24);
  margin-bottom: 18px;
}

.hero-eyebrow .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #5ef0d4;
  box-shadow: 0 0 0 3px rgba(94, 240, 212, 0.28);
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  letter-spacing: -1px;
  line-height: 1.3;
  margin-bottom: 14px;
}

.hero-title em {
  font-style: normal;
  background: linear-gradient(100deg, #ffffff 0%, #b9fff1 65%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-desc {
  font-size: 14.5px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.8);
  max-width: 470px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  margin-top: 26px;
}

.btn-solid {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 42px;
  padding: 0 22px;
  border: none;
  border-radius: 11px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  color: var(--brand-600);
  background: #fff;
  cursor: pointer;
  box-shadow: 0 10px 22px -12px rgba(0, 0, 0, 0.6);
  transition: all 0.22s;
}

.btn-solid:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 28px -14px rgba(0, 0, 0, 0.65);
}

.btn-solid.small {
  height: 38px;
  padding: 0 20px;
  font-size: 13.5px;
}

.btn-line {
  height: 42px;
  padding: 0 22px;
  border-radius: 11px;
  font-family: inherit;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.32);
  cursor: pointer;
  transition: all 0.22s;
}

.btn-line:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* Hero 右侧统计 */
.hero-stats {
  position: relative;
  z-index: 1;
  flex: none;
  width: 250px;
  padding: 22px 24px;
  border-radius: var(--r-lg);
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.24);
  backdrop-filter: blur(8px);
}

.stat-cell {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.stat-cell b {
  font-size: 30px;
  font-weight: 700;
  letter-spacing: -1px;
  line-height: 1.1;
}

.stat-cell span {
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.72);
}

.stat-divider {
  height: 1px;
  margin: 14px 0;
  background: rgba(255, 255, 255, 0.2);
}

.stat-note {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 14px;
  font-size: 11.5px;
  color: rgba(255, 255, 255, 0.6);
}

/* ========== 主体布局 ========== */
.home-body {
  display: grid;
  grid-template-columns: 232px 1fr;
  gap: 22px;
  align-items: start;
}

/* 侧栏 */
.side {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 88px;
}

.side-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 8px;
  box-shadow: var(--shadow-sm);
}

.side-head {
  padding: 12px 14px 10px;
  font-size: 11.5px;
  letter-spacing: 1.6px;
  text-transform: uppercase;
  color: var(--ink-300);
  font-weight: 600;
}

.course-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 14px;
  border-radius: 10px;
  font-size: 14px;
  color: var(--ink-700);
  cursor: pointer;
  transition: all 0.18s;
  user-select: none;
}

.course-row:hover {
  background: #f6f8fe;
  color: var(--brand-600);
}

.course-row.active {
  background: var(--brand-50);
  color: var(--brand-600);
  font-weight: 600;
}

.row-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d5dcef;
  flex: none;
  transition: all 0.18s;
}

.course-row.active .row-dot {
  background: var(--brand-500);
  box-shadow: 0 0 0 3px rgba(91, 108, 249, 0.18);
}

.row-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tip-card {
  padding: 18px 18px 16px;
  border-radius: var(--r-lg);
  background: linear-gradient(160deg, #f4f6ff 0%, #f0fbf8 100%);
  border: 1px solid #e6ecfb;
}

.tip-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: var(--ink-800);
  margin-bottom: 12px;
}

.tip-list {
  margin: 0 0 0 16px;
  padding: 0;
  font-size: 12.5px;
  line-height: 2;
  color: var(--ink-500);
}

.tip-foot {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #dde6f6;
  font-size: 11.5px;
  color: var(--ink-400);
}

/* 信息流 */
.feed {
  min-width: 0;
  scroll-margin-top: 88px;
}

.feed-bar {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  max-width: 460px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  background: #fff;
  padding-left: 14px;
  box-shadow: 0 1px 3px rgba(23, 28, 48, 0.05), 0 0 0 1px var(--line) inset;
}

.search-input :deep(.el-input-group__append) {
  border-radius: 0 12px 12px 0;
  background: var(--brand-500);
  border: none;
  color: #fff;
  box-shadow: none;
}

.search-input :deep(.el-input-group__append .el-button) {
  color: #fff;
  font-weight: 500;
}

.feed-count {
  font-size: 13px;
  color: var(--ink-400);
  margin-left: auto;
}

.feed-count b {
  color: var(--brand-600);
  font-weight: 700;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 答疑卡片 */
.qa-card {
  position: relative;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 20px 22px 16px;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
  transition: transform 0.24s cubic-bezier(0.22, 1, 0.36, 1),
              box-shadow 0.24s, border-color 0.24s;
  overflow: hidden;
  animation: fade-up 0.4s cubic-bezier(0.22, 1, 0.36, 1) backwards;
}

.qa-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 12px;
  bottom: 12px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: var(--grad-brand);
  opacity: 0;
  transform: scaleY(0.4);
  transition: all 0.24s;
}

.qa-card:hover {
  transform: translateY(-2px);
  border-color: #dbe2fb;
  box-shadow: 0 16px 34px -18px rgba(45, 57, 120, 0.35);
}

.qa-card:hover::before {
  opacity: 1;
  transform: scaleY(1);
}

.qa-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 9px;
}

.qa-course {
  display: inline-flex;
  align-items: center;
  padding: 2px 11px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
  color: var(--brand-600);
  background: var(--brand-50);
  border: 1px solid #dde2fe;
}

.qa-time {
  font-size: 12px;
  color: var(--ink-300);
}

.qa-title {
  font-size: 16.5px;
  font-weight: 600;
  line-height: 1.5;
  color: var(--ink-900);
  letter-spacing: -0.2px;
  margin-bottom: 7px;
  transition: color 0.2s;
}

.qa-card:hover .qa-title {
  color: var(--brand-600);
}

.qa-excerpt {
  font-size: 13.5px;
  line-height: 1.75;
  color: var(--ink-500);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 14px;
}

.qa-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid var(--line-soft);
}

.qa-author {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12.5px;
  color: var(--ink-500);
}

.mini-avatar {
  background: linear-gradient(135deg, #8b98fb, #5b6cf9);
  color: #fff;
  font-size: 11px;
  font-weight: 600;
}

.qa-metrics {
  display: flex;
  align-items: center;
  gap: 16px;
}

.metric {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12.5px;
  color: var(--ink-400);
}

.metric .el-icon {
  font-size: 14px;
}

/* 分页 */
.pager {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 空状态 */
.empty-state {
  padding: 70px 20px;
  text-align: center;
  background: #fff;
  border: 1px dashed var(--line);
  border-radius: var(--r-lg);
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 18px;
  border-radius: 20px;
  display: grid;
  place-items: center;
  font-size: 26px;
  color: var(--brand-400);
  background: var(--brand-50);
}

.empty-state h4 {
  font-size: 16px;
  color: var(--ink-800);
  margin-bottom: 6px;
}

.empty-state p {
  font-size: 13px;
  color: var(--ink-400);
  margin-bottom: 20px;
}

.empty-state .btn-solid {
  color: #fff;
  background: var(--grad-brand);
  box-shadow: var(--shadow-brand);
}

/* 响应式 */
@media (max-width: 960px) {
  .home-body {
    grid-template-columns: 1fr;
  }

  .side {
    position: static;
    order: 2;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .side-card {
    flex: 1 1 240px;
  }

  .tip-card {
    flex: 1 1 240px;
  }

  .hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 32px 28px;
  }

  .hero-stats {
    width: 100%;
  }

  .hero-title {
    font-size: 28px;
  }
}
</style>
