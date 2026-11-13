<template>
  <div class="panel">
    <div class="panel-head">
      <div class="ph-text">
        <h3>帖子审核</h3>
        <p>学生发布的帖子先进入待审核队列，通过后才会在前台公开展示</p>
      </div>
      <el-input v-model="keyword" class="search" placeholder="搜索标题 / 发布人" clearable
                @keyup.enter="load(1)" @clear="load(1)">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="load(1)">搜索</el-button>
        </template>
      </el-input>
    </div>

    <div class="panel-tabs">
      <el-tabs v-model="statusTab" @tab-change="load(1)">
        <el-tab-pane label="待审核" name="0" />
        <el-tab-pane label="已发布" name="1" />
        <el-tab-pane label="已屏蔽" name="2" />
        <el-tab-pane label="全部" name="all" />
      </el-tabs>
    </div>

    <el-table :data="list" v-loading="loading" class="soft-table">
      <el-table-column prop="id" label="ID" width="70">
        <template #default="{ row }">
          <span class="id-cell">#{{ row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column label="问题标题" min-width="230">
        <template #default="{ row }">
          <div class="title-cell" :title="row.title" @click="view(row)">{{ row.title }}</div>
        </template>
      </el-table-column>

      <el-table-column prop="authorName" label="发布人" width="110" />

      <el-table-column label="课程" width="120">
        <template #default="{ row }">
          <span class="course-chip">{{ row.courseName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span class="status-dot" :class="'s' + row.status">
            <i></i>{{ statusText(row.status) }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="发布时间" width="156">
        <template #default="{ row }">
          <span class="time-cell">{{ formatTime(row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="252" fixed="right">
        <template #default="{ row }">
          <div class="row-actions">
            <button class="mini-btn" @click="view(row)">查看</button>
            <button v-if="row.status !== 1" class="mini-btn ok" @click="audit(row, 1)">通过</button>
            <button v-if="row.status !== 2" class="mini-btn warn" @click="audit(row, 2)">屏蔽</button>
            <button class="mini-btn danger" @click="doDelete(row)">删除</button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total"
                     :page-size="query.size" :current-page="query.page" @current-change="onPage" />
    </div>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewVisible" width="660px" class="view-dialog">
      <template #header>
        <div class="dlg-head">
          <span class="course-chip">{{ viewing?.courseName }}</span>
          <span class="dlg-meta">{{ viewing?.authorName }} · {{ formatTime(viewing?.createTime) }}</span>
        </div>
      </template>

      <h3 class="dlg-title">{{ viewing?.title }}</h3>
      <div class="view-content">{{ viewing?.content }}</div>
      <el-image v-if="viewing?.image" :src="viewing.image" fit="cover" class="view-image"
                :preview-src-list="[viewing.image]" hide-on-click-modal />

      <template #footer>
        <el-button @click="viewVisible = false">关闭</el-button>
        <el-button v-if="viewing?.status !== 2" type="warning" plain
                   @click="audit(viewing, 2); viewVisible = false">屏蔽帖子</el-button>
        <el-button v-if="viewing?.status !== 1" type="primary"
                   @click="audit(viewing, 1); viewVisible = false">通过并发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAdminPostPage, auditPost, adminDeletePost } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const keyword = ref('')
// 默认停在「待审核」，管理员进入后直接处理积压队列
const statusTab = ref('0')
const viewVisible = ref(false)
const viewing = ref(null)

const query = reactive({ page: 1, size: 10 })

const statusText = s => ({ 0: '待审核', 1: '已发布', 2: '已屏蔽' })[s] || '未知'
const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

const load = async (page = query.page) => {
  query.page = page
  loading.value = true
  try {
    const res = await getAdminPostPage({
      page: query.page,
      size: query.size,
      status: statusTab.value === 'all' ? undefined : Number(statusTab.value),
      keyword: keyword.value.trim() || undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

const onPage = page => load(page)

const view = row => {
  viewing.value = row
  viewVisible.value = true
}

const audit = async (row, status) => {
  await auditPost(row.id, status)
  ElMessage.success(status === 1 ? '已通过并发布' : '已屏蔽')
  viewVisible.value = false
  load()
}

const doDelete = row => {
  ElMessageBox.confirm('删除帖子将同时删除其全部回复、点赞与收藏记录，确定删除吗？', '警告', { type: 'warning' })
    .then(async () => {
      await adminDeletePost(row.id)
      ElMessage.success('删除成功')
      load()
    }).catch(() => {})
}

onMounted(() => load(1))
</script>

<style scoped>
.panel {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 24px 26px 18px;
  box-shadow: var(--shadow-xs);
  animation: fade-up 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 8px;
}

.ph-text h3 {
  font-size: 18px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.3px;
}

.ph-text p {
  margin-top: 5px;
  font-size: 12.5px;
  color: var(--ink-400);
}

.search {
  width: 270px;
  flex: none;
}

.search :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding-left: 12px;
  background: #fafbff;
}

.search :deep(.el-input-group__append) {
  border-radius: 0 10px 10px 0;
  background: var(--brand-500);
  border: none;
  color: #fff;
  box-shadow: none;
}

.search :deep(.el-input-group__append .el-button) {
  color: #fff;
  font-weight: 500;
}

.panel-tabs {
  margin: 6px 0 4px;
}

.panel-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.panel-tabs :deep(.el-tabs__item) {
  height: 40px;
  font-size: 14px;
  padding: 0 4px;
  margin-right: 26px;
}

.soft-table {
  --el-table-border-color: transparent;
  border-radius: var(--r-md);
}

.id-cell {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12.5px;
  color: var(--ink-300);
}

.title-cell {
  font-size: 14px;
  font-weight: 500;
  color: var(--ink-800);
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.18s;
}

.title-cell:hover {
  color: var(--brand-600);
}

.course-chip {
  display: inline-flex;
  padding: 1px 10px;
  border-radius: 999px;
  font-size: 11.5px;
  color: var(--brand-600);
  background: var(--brand-50);
}

.status-dot {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
}

.status-dot i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.status-dot.s0 {
  color: #c07c00;
}

.status-dot.s0 i {
  background: #ffab2e;
  box-shadow: 0 0 0 3px rgba(255, 171, 46, 0.18);
}

.status-dot.s1 {
  color: #12977f;
}

.status-dot.s1 i {
  background: #22c08a;
  box-shadow: 0 0 0 3px rgba(34, 192, 138, 0.16);
}

.status-dot.s2 {
  color: #d64357;
}

.status-dot.s2 i {
  background: #ef5468;
  box-shadow: 0 0 0 3px rgba(239, 84, 104, 0.16);
}

.time-cell {
  font-size: 12.5px;
  color: var(--ink-400);
}

.row-actions {
  display: flex;
  gap: 6px;
}

.mini-btn {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 7px;
  padding: 4px 10px;
  font-family: inherit;
  font-size: 12.5px;
  color: var(--ink-500);
  cursor: pointer;
  transition: all 0.18s;
  white-space: nowrap;
  flex: none;
}

.mini-btn:hover {
  border-color: var(--brand-200);
  color: var(--brand-600);
  background: #fbfcff;
}

.mini-btn.ok {
  color: #12977f;
  border-color: #c9f0e5;
  background: #f4fdfa;
}

.mini-btn.ok:hover {
  background: #e8fbf5;
  border-color: #9fe6d4;
}

.mini-btn.warn {
  color: #c07c00;
  border-color: #ffe4b8;
  background: #fffbf3;
}

.mini-btn.warn:hover {
  background: #fff5e3;
  border-color: #ffd28c;
}

.mini-btn.danger {
  color: #d64357;
}

.mini-btn.danger:hover {
  background: #fff5f6;
  border-color: #ffd0d7;
  color: #d64357;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 18px;
}

/* 对话框 */
.dlg-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dlg-meta {
  font-size: 12.5px;
  color: var(--ink-400);
}

.dlg-title {
  font-size: 19px;
  font-weight: 700;
  color: var(--ink-900);
  line-height: 1.5;
  margin-bottom: 12px;
}

.view-content {
  font-size: 14px;
  line-height: 1.9;
  color: var(--ink-700);
  white-space: pre-wrap;
  background: #fafbff;
  border: 1px solid var(--line-soft);
  padding: 16px 18px;
  border-radius: var(--r-md);
}

.view-image {
  margin-top: 12px;
  max-width: 100%;
  border-radius: var(--r-md);
}
</style>
