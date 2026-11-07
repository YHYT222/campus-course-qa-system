<template>
  <div class="page-container" v-if="post">
    <!-- 帖子主体 -->
    <div class="detail-card">
      <div class="flex-between">
        <el-tag effect="plain">{{ post.courseName }}</el-tag>
        <span class="muted">{{ formatTime(post.createTime) }}</span>
      </div>
      <h1 class="detail-title">{{ post.title }}</h1>
      <div class="detail-author">
        <el-avatar :size="28" :src="post.authorAvatar || undefined">{{ (post.authorName || '学').charAt(0) }}</el-avatar>
        <span>{{ post.authorName }}</span>
      </div>
      <div class="detail-content">{{ post.content }}</div>
      <el-image v-if="post.image" :src="post.image" fit="cover" class="detail-image"
                :preview-src-list="[post.image]" hide-on-click-modal />

      <div class="detail-actions">
        <el-button :type="post.liked ? 'primary' : 'default'" round @click="doLike">
          👍 点赞 {{ post.likeCount }}
        </el-button>
        <el-button :type="post.favorited ? 'warning' : 'default'" round @click="doFavorite">
          ⭐ {{ post.favorited ? '已收藏' : '收藏' }}
        </el-button>
        <el-button v-if="post.isOwner || post.isAdmin" type="danger" plain round @click="doDelete">
          删除
        </el-button>
      </div>
    </div>

    <!-- 回复区 -->
    <div class="detail-card">
      <h3 class="section-title">💬 全部回复（{{ replies.length }}）</h3>

      <div v-if="replies.length" class="reply-list">
        <div v-for="(r, i) in replies" :key="r.id" class="reply-item">
          <el-avatar :size="32" :src="r.authorAvatar || undefined">{{ (r.authorName || '学').charAt(0) }}</el-avatar>
          <div class="reply-body">
            <div class="flex-between">
              <span class="reply-author">{{ r.authorName }}</span>
              <span class="muted">#{{ i + 1 }} · {{ formatTime(r.createTime) }}</span>
            </div>
            <div class="reply-content">{{ r.content }}</div>
          </div>
        </div>
      </div>
      <el-empty v-else description="还没有回复，来帮帮他吧！" :image-size="80" />

      <div class="reply-input">
        <el-input v-model="replyContent" type="textarea" :rows="3" maxlength="500" show-word-limit
                  placeholder="写下你的解答或建议（友情帮答，共同进步）" />
        <el-button type="primary" style="margin-top: 10px" :loading="replying" @click="doReply">
          发表回复
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, toggleLike, toggleFavorite, addReply, deletePost } from '../api'

const route = useRoute()
const router = useRouter()
const postId = route.params.id

const post = ref(null)
const replies = ref([])
const replyContent = ref('')
const replying = ref(false)

const load = async () => {
  const res = await getPostDetail(postId)
  post.value = res.data
  replies.value = res.data.replies || []
}

const doLike = async () => {
  const res = await toggleLike(postId)
  post.value.liked = res.data.liked
  post.value.likeCount = res.data.likeCount
}

const doFavorite = async () => {
  const res = await toggleFavorite(postId)
  post.value.favorited = res.data.favorited
  ElMessage.success(res.data.favorited ? '已加入收藏' : '已取消收藏')
}

const doReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  replying.value = true
  try {
    await addReply({ postId, content: replyContent.value.trim() })
    ElMessage.success('回复成功')
    replyContent.value = ''
    load()
  } finally {
    replying.value = false
  }
}

const doDelete = () => {
  ElMessageBox.confirm('确定删除该帖子吗？删除后不可恢复。', '警告', { type: 'warning' }).then(async () => {
    await deletePost(postId)
    ElMessage.success('删除成功')
    router.push('/')
  }).catch(() => {})
}

const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

onMounted(load)
</script>

<style scoped>
.detail-card {
  background: #fff;
  border-radius: 10px;
  padding: 24px 28px;
  margin-bottom: 16px;
  border: 1px solid #e8edf3;
}

.detail-title {
  font-size: 22px;
  color: #1a365d;
  margin: 14px 0 10px;
}

.detail-author {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #718096;
  font-size: 14px;
  margin-bottom: 14px;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #2d3748;
  white-space: pre-wrap;
}

.detail-image {
  margin-top: 14px;
  max-width: 420px;
  border-radius: 8px;
}

.detail-actions {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed #e2e8f0;
}

.section-title {
  margin-bottom: 16px;
  color: #1a365d;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #edf2f7;
}

.reply-author {
  font-weight: 600;
  color: #2b6cb0;
  font-size: 14px;
}

.reply-content {
  margin-top: 6px;
  font-size: 14px;
  line-height: 1.7;
  color: #2d3748;
  white-space: pre-wrap;
}

.reply-input {
  margin-top: 18px;
}
</style>
