<template>
  <div class="admin-panel">
    <div class="panel-header">
      <h3>课程分类管理</h3>
      <el-button type="primary" :icon="Plus" @click="openAdd">新增课程</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="课程名称" min-width="140">
        <template #default="{ row }">
          <el-tag effect="plain">{{ row.name }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="课程简介" min-width="280" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="doDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑课程' : '新增课程'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="课程名称" required>
          <el-input v-model="form.name" maxlength="50" placeholder="例如：高等数学" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="255"
                    placeholder="该课程答疑范围简要说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCourseList, addCourse, updateCourse, deleteCourse } from '../../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editId = ref(null)
const form = reactive({ name: '', description: '' })

const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

const load = async () => {
  loading.value = true
  try {
    list.value = (await getCourseList()).data
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  editId.value = null
  form.name = ''
  form.description = ''
  dialogVisible.value = true
}

const openEdit = row => {
  editId.value = row.id
  form.name = row.name
  form.description = row.description || ''
  dialogVisible.value = true
}

const save = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('课程名称不能为空')
    return
  }
  saving.value = true
  try {
    if (editId.value) {
      await updateCourse({ id: editId.value, name: form.name.trim(), description: form.description })
      ElMessage.success('修改成功')
    } else {
      await addCourse({ name: form.name.trim(), description: form.description })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const doDelete = row => {
  ElMessageBox.confirm(`确定删除课程「${row.name}」吗？若该课程下仍有帖子则无法删除。`, '警告', { type: 'warning' })
    .then(async () => {
      await deleteCourse(row.id)
      ElMessage.success('删除成功')
      load()
    }).catch(() => {})
}

onMounted(load)
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
</style>
