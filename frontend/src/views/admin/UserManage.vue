<template>
  <div class="admin-panel">
    <div class="panel-header">
      <h3>用户管理</h3>
      <el-input v-model="keyword" placeholder="搜索用户名 / 学号" clearable style="width: 240px"
                @keyup.enter="load(1)" @clear="load(1)">
        <template #append>
          <el-button :icon="Search" @click="load(1)" />
        </template>
      </el-input>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户" min-width="180">
        <template #default="{ row }">
          <div class="user-cell">
            <el-avatar :size="32" :src="row.avatar || undefined">{{ (row.username || '学').charAt(0) }}</el-avatar>
            <span>{{ row.username }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="studentNo" label="学号" width="130" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="row.role === 1 ? 'warning' : 'info'">
            {{ row.role === 1 ? '管理员' : '学生' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <template v-if="row.role !== 1">
            <el-button v-if="row.status !== 0" size="small" type="success" plain @click="setStatus(row, 0)">解除限制</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" plain @click="setStatus(row, 1)">禁言</el-button>
            <el-button v-if="row.status !== 2" size="small" type="danger" plain @click="setStatus(row, 2)">封禁</el-button>
            <el-button size="small" @click="doReset(row)">重置密码</el-button>
          </template>
          <span v-else class="muted">管理员账号不可操作</span>
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
const statusType = s => ({ 0: 'success', 1: 'warning', 2: 'danger' })[s] || 'info'
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
.admin-panel {
  background: #fff;
  border-radius: 10px;
  padding: 20px 24px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.panel-header h3 {
  color: #1a365d;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
