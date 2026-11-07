<template>
  <div class="page-container">
    <!-- 搜索与筛选 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索问题标题 / 内容" clearable style="width: 280px"
                @keyup.enter="load(1)" @clear="load(1)">
        <template #append>
          <el-button :icon="Search" @click="load(1)" />
        </template>
      </el-input>
      <div class="course-tags">
        <el-tag :type="!query.courseId ? 'primary' : 'info'" class="course-tag" @click="pickCourse(null)">全部</el-tag>
        <el-tag v-for="c in courses" :key="c.id" :type="query.courseId === c.id ? 'primary' : 'info'"
                class="course-tag" @click="pickCourse(c.id)">
          {{ c.name }}
        </el-tag>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div v-if="list.length" class="post-list">
      <div v-for="p in list" :key="p.id" class="post-card" @click="$router.push(`/post/${p.id}`)">
        <div class="flex-between">
          <div class="post-title">{{ p.title }}</div>
          <el-tag size="small" effect="plain">{{ p.courseName }}</el-tag>
        </div>
        <div class="post-excerpt">{{ p.content }}</div>
        <div class="post-meta">
          <span>👤 {{ p.authorName }}</span>
          <span>💬 {{ p.replyCount }} 回复</span>
          <span>👍 {{ p.likeCount }} 点赞</span>
          <span>🕐 {{ formatTime(p.createTime) }}</span>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无答疑帖，快来发布第一个问题吧！" />

    <div class="pager">
      <el-pagination background layout="prev, pager, next, total" :total="total"
                     :page-size="query.size" :current-page="query.page" @current-change="onPage" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getCourseList, getPostPage } from '../api'

const courses = ref([])
const list = ref([])
const total = ref(0)
const keyword = ref('')
const query = reactive({ page: 1, size: 10, courseId: null })

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

const onPage = page => load(page)

const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

onMounted(async () => {
  const res = await getCourseList()
  courses.value = res.data
  load(1)
})
</script>

<style scoped>
.filter-bar {
  background: #fff;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 16px;
  border: 1px solid #e8edf3;
}

.course-tags {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.course-tag {
  cursor: pointer;
}

.pager {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
