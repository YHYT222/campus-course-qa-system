<template>
  <div class="panel">
    <div class="panel-head">
      <div class="ph-text">
        <h3>课程分类管理</h3>
        <p>维护校园课程分类，学生发帖时按此分类归档，便于检索</p>
      </div>
      <el-button type="primary" @click="openAdd">
        <el-icon><Plus /></el-icon>
        新增课程
      </el-button>
    </div>

    <div v-loading="loading" class="course-grid">
      <div v-for="c in list" :key="c.id" class="course-card">
        <div class="cc-top">
          <span class="cc-icon">{{ (c.name || '课').charAt(0) }}</span>
          <div class="cc-actions">
            <button class="mini-btn" @click="openEdit(c)">编辑</button>
            <button class="mini-btn danger" @click="doDelete(c)">删除</button>
          </div>
        </div>
        <h4>{{ c.name }}</h4>
        <p>{{ c.description || '暂无简介' }}</p>
        <div class="cc-foot">
          <el-icon><Clock /></el-icon>
          创建于 {{ formatTime(c.createTime) }}
        </div>
      </div>

      <div class="course-card add-card" @click="openAdd">
        <el-icon><Plus /></el-icon>
        <span>新增课程分类</span>
      </div>
    </div>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑课程' : '新增课程'" width="460px">
      <el-form label-position="top">
        <el-form-item label="课程名称" required>
          <el-input v-model="form.name" maxlength="50" placeholder="例如：高等数学" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="255"
                    placeholder="该课程答疑范围的简要说明" />
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
.panel {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 24px 26px 26px;
  box-shadow: var(--shadow-xs);
  animation: fade-up 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
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

.panel-head :deep(.el-button--primary) {
  height: 38px;
  border-radius: 10px;
  padding: 0 16px;
}

/* 课程网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(248px, 1fr));
  gap: 14px;
  min-height: 120px;
}

.course-card {
  position: relative;
  padding: 18px 20px 16px;
  border: 1px solid var(--line);
  border-radius: var(--r-md);
  background: #fff;
  min-height: 182px;
  display: flex;
  flex-direction: column;
  transition: all 0.24s cubic-bezier(0.22, 1, 0.36, 1);
  overflow: hidden;
}

.course-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--r-md);
  box-shadow: 0 14px 28px -20px rgba(45, 57, 120, 0.5);
  opacity: 0;
  transition: opacity 0.24s;
  pointer-events: none;
}

.course-card:hover {
  transform: translateY(-2px);
  border-color: #dbe2fb;
}

.course-card:hover::after {
  opacity: 1;
}

.cc-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.cc-icon {
  width: 36px;
  height: 36px;
  border-radius: 11px;
  display: grid;
  place-items: center;
  font-size: 15px;
  font-weight: 700;
  color: var(--brand-600);
  background: var(--brand-50);
}

.cc-actions {
  display: flex;
  gap: 5px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.course-card:hover .cc-actions {
  opacity: 1;
}

.mini-btn {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 7px;
  padding: 3px 9px;
  font-family: inherit;
  font-size: 12px;
  color: var(--ink-500);
  cursor: pointer;
  transition: all 0.18s;
  white-space: nowrap;
}

.mini-btn:hover {
  border-color: var(--brand-200);
  color: var(--brand-600);
}

.mini-btn.danger {
  color: #d64357;
}

.mini-btn.danger:hover {
  background: #fff5f6;
  border-color: #ffd0d7;
}

.course-card h4 {
  font-size: 15.5px;
  font-weight: 600;
  color: var(--ink-900);
  margin-bottom: 6px;
}

.course-card p {
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--ink-400);
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.cc-foot {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: auto;
  padding-top: 11px;
  border-top: 1px solid var(--line-soft);
  font-size: 11.5px;
  color: var(--ink-300);
}

/* 新增卡 */
.add-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 182px;
  border-style: dashed;
  border-color: #d8dfef;
  background: #fafbff;
  cursor: pointer;
  color: var(--ink-400);
  font-size: 13px;
}

.add-card .el-icon {
  font-size: 22px;
  color: var(--brand-400);
}

.add-card:hover {
  border-color: var(--brand-400);
  background: var(--brand-50);
  color: var(--brand-600);
}

@media (max-width: 640px) {
  .panel {
    padding: 18px 16px;
  }

  .panel-head {
    flex-direction: column;
  }
}
</style>
