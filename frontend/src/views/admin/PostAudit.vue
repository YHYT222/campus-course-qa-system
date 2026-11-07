<template>
  <div class="admin-panel">
    <div class="panel-header">
      <h3>帖子审核</h3>
      <el-input v-model="keyword" placeholder="搜索标题 / 发布人" clearable style="width: 240px"
                @keyup.enter="load(1)" @clear="load(1)">
        <template #append>
          <el-button :icon="Search" @click="load(1)" />
        </template>
      </el-input>
    </div>

    <el-tabs v-model="statusTab" @tab-change="load(1)">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="已发布" name="1" />
      <el-tab-pane label="已屏蔽" name="2" />
    </el-tabs>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="问题标题" min-width="220" show-overflow-tooltip />
      <el-table-column prop="authorName" label="发布人" width="110" />
      <el-table-column prop="courseName" label="课程" width="110">
        <template #default="{ row }">
          <el-tag size="small" effect="plain">{{ row.courseName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="view(row)">查看</el-button>
          <el-button v-if="row.status !== 1" size="small" type="success" plain @click="audit(row, 1)">发布</el-button>
          <el-button v-if="row.status !== 2" size="small" type="warning" plain @click="audit(row, 2)">屏蔽</el-button>
          <el-button size="small" type="danger" plain @click="doDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total"
                     :page-size="query.size" :current-page="query.page" @current-change="onPage" />
    </div>

    <!-- 内容查看对话框 -->
    <el-dialog v-model="viewVisible" :title="viewing?.title" width="640px">
      <div class="view-meta">
        <el-tag size="small" effect="plain">{{ viewing?.courseName }}</el-tag>
        <span class="muted">发布人：{{ viewing?.authorName }} · {{ formatTime(viewing?.createTime) }}</span>
      </div>
      <div class="view-content">{{ viewing?.content }}</div>
      <el-image v-if="viewing?.image" :src="viewing.image" fit="cover" style="max-width: 100%; border-radius: 8px; margin-top: 12px"
                :preview-src-list="[viewing.image]" hide-on-click-modal />
      <template #footer>
        <el-button v-if="viewing?.status !== 1" type="success" @click="audit(viewing, 1); viewVisible = false">通过发布</el-button>
        <el-button v-if="viewing?.status !== 2" type="warning" @click="audit(viewing, 2); viewVisible = false">屏蔽帖子</el-button>
        <el-button @click="viewVisible = false">关闭</el-button>
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
const statusTab = ref('all')
const viewVisible = ref(false)
const viewing = ref(null)

const query = reactive({ page: 1, size: 10 })

const statusText = s => ({ 0: '待审核', 1: '已发布', 2: '已屏蔽' })[s] || '未知'
const statusType = s => ({ 0: 'info', 1: 'success', 2: 'danger' })[s] || 'info'
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
  ElMessage.success(status === 1 ? '已发布' : '已屏蔽')
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
.admin-panel {
  background: #fff;
  border-radius: 10px;
  padding: 20px 24px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.panel-header h3 {
  color: #1a365d;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.view-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.view-content {
  font-size: 14px;
  line-height: 1.8;
  color: #2d3748;
  white-space: pre-wrap;
  background: #f7fafc;
  padding: 14px;
  border-radius: 8px;
}
</style>
