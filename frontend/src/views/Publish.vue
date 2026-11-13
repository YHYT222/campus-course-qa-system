<template>
  <div class="publish-page">
    <div class="page-head">
      <div class="back-line" @click="$router.push('/')">
        <el-icon><ArrowLeft /></el-icon>
        返回答疑列表
      </div>
      <h1>发布答疑帖</h1>
      <p>把问题说清楚，同学才帮得上忙。提交后需管理员审核通过才会公开展示。</p>
    </div>

    <div class="publish-grid">
      <!-- 表单 -->
      <div class="form-card">
        <el-form :model="form" label-position="top" size="large">
          <el-form-item label="课程分类" required>
            <el-select v-model="form.courseId" placeholder="选择问题所属课程" style="width: 100%">
              <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="问题标题" required>
            <el-input v-model="form.title" maxlength="100" show-word-limit
                      placeholder="一句话概括，例如：极限的等价无穷小替换什么时候能用？" />
            <div class="field-hint">标题写清楚知识点，比"求助""急"更容易被搜到</div>
          </el-form-item>

          <el-form-item label="问题描述" required>
            <el-input v-model="form.content" type="textarea" :rows="9" maxlength="2000" show-word-limit
                      placeholder="建议写清楚：
① 题目出处与完整条件
② 你已经尝试过的思路
③ 具体卡在哪一步" />
          </el-form-item>

          <el-form-item label="题目配图">
            <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                       :on-success="onUploadSuccess" :on-error="onUploadError" accept="image/*">
              <div class="upload-box" :class="{ filled: !!form.image }">
                <el-image v-if="form.image" :src="form.image" fit="cover" class="upload-preview" />
                <template v-else>
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>点击上传题目截图</span>
                  <em>支持 JPG / PNG，上传后可预览大图</em>
                </template>
              </div>
            </el-upload>
            <div v-if="form.image" class="field-hint">已上传，发布后同学可点击查看大图</div>
          </el-form-item>

          <div class="form-actions">
            <el-button type="primary" size="large" :loading="submitting" @click="submit">
              提交问题
            </el-button>
            <el-button size="large" text @click="$router.push('/')">取消</el-button>
          </div>
        </el-form>
      </div>

      <!-- 侧栏指南 -->
      <aside class="guide">
        <div class="guide-card">
          <h4>好问题的三个要素</h4>
          <div class="guide-item">
            <span class="gi-num">01</span>
            <div>
              <b>条件完整</b>
              <p>把题目原文或截图放上来，避免"我有一道题不会"。</p>
            </div>
          </div>
          <div class="guide-item">
            <span class="gi-num">02</span>
            <div>
              <b>过程可见</b>
              <p>写出自己算到哪一步，别人才能指出错在哪。</p>
            </div>
          </div>
          <div class="guide-item">
            <span class="gi-num">03</span>
            <div>
              <b>目标明确</b>
              <p>说清是要答案还是要思路，方便同学选择合适的讲法。</p>
            </div>
          </div>
        </div>

        <div class="audit-card">
          <div class="audit-title">
            <el-icon><CircleCheck /></el-icon>
            审核说明
          </div>
          <p>帖子提交后状态为「待审核」，管理员通过后才会在学生端公开展示。</p>
          <p class="audit-note">灌水、广告与无关内容会被屏蔽下架。</p>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCourseList, publishPost } from '../api'

const router = useRouter()
const courses = ref([])
const submitting = ref(false)

const uploadUrl = '/api/file/upload'
const uploadHeaders = { token: localStorage.getItem('token') || '' }

const form = reactive({ courseId: null, title: '', content: '', image: '' })

const onUploadSuccess = res => {
  if (res.code === 200) {
    form.image = res.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const onUploadError = () => ElMessage.error('图片上传失败，请检查后端服务')

const submit = async () => {
  if (!form.courseId) {
    ElMessage.warning('请选择课程分类')
    return
  }
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('标题和问题描述不能为空')
    return
  }
  submitting.value = true
  try {
    await publishPost({ ...form, title: form.title.trim(), content: form.content.trim() })
    ElMessage.success('提交成功，等待管理员审核通过后展示')
    router.push('/')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const res = await getCourseList()
  courses.value = res.data
})
</script>

<style scoped>
.publish-page {
  max-width: 1040px;
  margin: 0 auto;
  padding: 24px 20px 70px;
  animation: fade-up 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

.page-head {
  margin-bottom: 22px;
}

.page-head h1 {
  font-size: 27px;
  font-weight: 700;
  letter-spacing: -0.6px;
  color: var(--ink-900);
}

.page-head p {
  margin-top: 8px;
  font-size: 13.5px;
  color: var(--ink-400);
}

.publish-grid {
  display: grid;
  grid-template-columns: 1fr 288px;
  gap: 20px;
  align-items: start;
}

/* 表单卡 */
.form-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-xl);
  padding: 30px 34px 28px;
  box-shadow: var(--shadow-sm);
}

.form-card :deep(.el-form-item__label) {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--ink-700);
  padding-bottom: 7px;
}

.form-card :deep(.el-form-item) {
  margin-bottom: 22px;
}

.form-card :deep(.el-input__wrapper),
.form-card :deep(.el-select__wrapper) {
  border-radius: 11px;
  padding: 5px 14px;
  background: #fafbff;
}

.form-card :deep(.el-textarea__inner) {
  border-radius: var(--r-md);
  padding: 14px 16px;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.85;
  background: #fafbff;
}

.form-card :deep(.el-textarea__inner:focus),
.form-card :deep(.el-input__wrapper.is-focus),
.form-card :deep(.el-select__wrapper.is-focused) {
  background: #fff;
}

.field-hint {
  margin-top: 6px;
  font-size: 12px;
  color: var(--ink-400);
  line-height: 1.6;
}

/* 上传框 */
.upload-box {
  width: 240px;
  height: 150px;
  border-radius: var(--r-md);
  border: 1.5px dashed #d8dfef;
  background: #fafbff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.22s;
}

.upload-box:hover {
  border-color: var(--brand-400);
  background: var(--brand-50);
}

.upload-box.filled {
  border-style: solid;
  border-color: var(--line);
  padding: 0;
}

.upload-icon {
  font-size: 22px;
  color: var(--brand-400);
}

.upload-box span {
  font-size: 13px;
  color: var(--ink-500);
}

.upload-box em {
  font-style: normal;
  font-size: 11.5px;
  color: var(--ink-300);
}

.upload-preview {
  width: 240px;
  height: 150px;
}

.form-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 6px;
}

.form-actions :deep(.el-button--large) {
  height: 44px;
  padding: 0 28px;
  border-radius: 11px;
  font-weight: 600;
}

/* 侧栏 */
.guide {
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: sticky;
  top: 88px;
}

.guide-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-lg);
  padding: 20px 20px 8px;
  box-shadow: var(--shadow-xs);
}

.guide-card h4 {
  font-size: 14px;
  font-weight: 700;
  color: var(--ink-800);
  margin-bottom: 16px;
}

.guide-item {
  display: flex;
  gap: 11px;
  padding-bottom: 16px;
}

.gi-num {
  flex: none;
  width: 24px;
  height: 24px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  font-size: 11px;
  font-weight: 700;
  color: var(--brand-600);
  background: var(--brand-50);
}

.guide-item b {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--ink-800);
  margin-bottom: 3px;
}

.guide-item p {
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--ink-400);
}

.audit-card {
  padding: 18px 20px;
  border-radius: var(--r-lg);
  background: linear-gradient(160deg, #fffaf0 0%, #fff6e9 100%);
  border: 1px solid #ffe7c2;
}

.audit-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: #a86b00;
  margin-bottom: 8px;
}

.audit-card p {
  font-size: 12.5px;
  line-height: 1.8;
  color: #96703a;
}

.audit-note {
  margin-top: 6px;
  color: #b08a55 !important;
}

@media (max-width: 900px) {
  .publish-grid {
    grid-template-columns: 1fr;
  }

  .guide {
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .guide-card,
  .audit-card {
    flex: 1 1 260px;
  }

  .form-card {
    padding: 22px 20px;
  }
}
</style>
