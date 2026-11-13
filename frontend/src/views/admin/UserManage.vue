<template>
  <div class="panel">
    <div class="panel-head">
      <div class="ph-text">
        <h3>用户管理</h3>
        <p>查看注册用户信息，对发布违规内容的账号进行禁言、封禁或重置密码</p>
      </div>
      <el-input v-model="keyword" class="search" placeholder="搜索用户名 / 学号" clearable
                @keyup.enter="load(1)" @clear="load(1)">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #append>
          <el-button @click="load(1)">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="list" v-loading="loading" class="soft-table">
      <el-table-column prop="id" label="ID" width="70">
        <template #default="{ row }">
          <span class="id-cell">#{{ row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column label="用户" min-width="200">
        <template #default="{ row }">
          <div class="user-cell">
            <el-avatar :size="34" :src="row.avatar || undefined" class="u-avatar">
              {{ (row.username || '学').charAt(0) }}
            </el-avatar>
            <div class="u-text">
              <b>{{ row.username }}</b>
              <span>{{ row.studentNo }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="角色" width="110">
        <template #default="{ row }">
          <span class="role-chip" :class="{ admin: row.role === 1 }">
            {{ row.role === 1 ? '管理员' : '学生' }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <span class="status-dot" :class="'s' + row.status">
            <i></i>{{ statusText(row.status) }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="注册时间" width="166">
        <template #default="{ row }">
          <span class="time-cell">{{ formatTime(row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <div v-if="row.role !== 1" class="row-actions">
            <button v-if="row.status !== 0" class="mini-btn ok" @click="setStatus(row, 0)">解除限制</button>
            <button v-if="row.status !== 1" class="mini-btn warn" @click="setStatus(row, 1)">禁言</button>
            <button v-if="row.status !== 2" class="mini-btn danger" @click="setStatus(row, 2)">封禁</button>
            <button class="mini-btn" @click="doReset(row)">重置密码</button>
          </div>
          <span v-else class="muted">管理员账号受保护</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total"
                     :page-size="query.size" :current-page="query.page" @current-change="onPage" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserPage, changeUserStatus, resetUserPassword } from '../../api'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const keyword = ref('')
const query = reactive({ page: 1, size: 10 })

const statusText = s => ({ 0: '正常', 1: '禁言', 2: '封禁' })[s] || '未知'
const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

const load = async (page = query.page) => {
  query.page = page
  loading.value = true
  try {
    const res = await getUserPage({
      page: query.page,
      size: query.size,
      keyword: keyword.value.trim() || undefined
    })
    list.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
  }
}

const onPage = page => load(page)

const setStatus = (row, status) => {
  const actionText = { 0: '解除限制', 1: '禁言', 2: '封禁' }[status]
  ElMessageBox.confirm(`确定对用户「${row.username}」执行「${actionText}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await changeUserStatus(row.id, status)
      ElMessage.success('操作成功')
      load()
    }).catch(() => {})
}

const doReset = row => {
  ElMessageBox.confirm(`确定将「${row.username}」的密码重置为 123456 吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await resetUserPassword(row.id)
      ElMessage.success('密码已重置为 123456')
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
  margin-bottom: 20px;
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

.soft-table {
  --el-table-border-color: transparent;
}

.id-cell {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12.5px;
  color: var(--ink-300);
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 11px;
}

.u-avatar {
  flex: none;
  background: linear-gradient(135deg, #9aa6fc, #6c7cf8);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.u-text {
  display: flex;
  flex-direction: column;
  line-height: 1.35;
  min-width: 0;
}

.u-text b {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink-800);
}

.u-text span {
  font-size: 11.5px;
  color: var(--ink-400);
  font-family: 'SFMono-Regular', Consolas, monospace;
}

.role-chip {
  display: inline-flex;
  padding: 1px 10px;
  border-radius: 999px;
  font-size: 11.5px;
  color: var(--ink-500);
  background: #f3f5fb;
}

.role-chip.admin {
  color: #a86b00;
  background: var(--amber-soft);
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
  color: #12977f;
}

.status-dot.s0 i {
  background: #22c08a;
  box-shadow: 0 0 0 3px rgba(34, 192, 138, 0.16);
}

.status-dot.s1 {
  color: #c07c00;
}

.status-dot.s1 i {
  background: #ffab2e;
  box-shadow: 0 0 0 3px rgba(255, 171, 46, 0.18);
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

.mini-btn.warn {
  color: #c07c00;
  border-color: #ffe4b8;
  background: #fffbf3;
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

@media (max-width: 640px) {
  .panel {
    padding: 18px 16px;
  }

  .panel-head {
    flex-direction: column;
  }

  .search {
    width: 100%;
  }
}
</style>
