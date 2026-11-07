<template>
  <div class="page-container">
    <div class="publish-card">
      <h2 class="publish-title">📝 发布答疑帖</h2>

      <el-form :model="form" label-width="90px" size="large">
        <el-form-item label="课程分类" required>
          <el-select v-model="form.courseId" placeholder="请选择课程分类" style="width: 100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="问题标题" required>
          <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="一句话概括你的问题，例如：极限的等价无穷小替换什么时候能用？" />
        </el-form-item>

        <el-form-item label="问题描述" required>
          <el-input v-model="form.content" type="textarea" :rows="7" maxlength="2000" show-word-limit
                    placeholder="详细描述你的问题：题目出处、已尝试的思路、卡在哪一步……描述越清楚，越容易得到高质量解答" />
        </el-form-item>

        <el-form-item label="上传配图">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                     :on-success="onUploadSuccess" :on-error="onUploadError" accept="image/*">
            <el-image v-if="form.image" :src="form.image" fit="cover" class="upload-preview" />
            <el-button v-else type="primary" plain>
              <el-icon><Plus /></el-icon>&nbsp;点击上传题目截图
            </el-button>
          </el-upload>
          <div v-if="form.image" class="muted" style="margin-top: 6px">已上传，发布后同学可查看大图</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="submit">发布问题</el-button>
          <el-button size="large" @click="$router.push('/')">取消</el-button>
        </el-form-item>
      </el-form>
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
    ElMessage.success('发布成功，等待同学们解答吧！')
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
.publish-card {
  background: #fff;
  border-radius: 10px;
  padding: 30px 36px;
  border: 1px solid #e8edf3;
  max-width: 760px;
  margin: 0 auto;
}

.publish-title {
  color: #1a365d;
  margin-bottom: 24px;
}

.upload-preview {
  width: 200px;
  height: 140px;
  border-radius: 8px;
  cursor: pointer;
}
</style>
