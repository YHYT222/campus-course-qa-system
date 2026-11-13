<template>
  <div class="detail-page" v-if="post">
    <div class="back-line" @click="$router.back()">
      <el-icon><ArrowLeft /></el-icon>
      返回答疑列表
    </div>

    <!-- 帖子主体 -->
    <article class="post-main">
      <div class="post-top">
        <span class="course-chip">{{ post.courseName }}</span>
        <span class="post-time">{{ formatTime(post.createTime) }}</span>
      </div>

      <h1 class="post-title">{{ post.title }}</h1>

      <div class="author-row">
        <el-avatar :size="40" :src="post.authorAvatar || undefined" class="author-avatar">
          {{ (post.authorName || '学').charAt(0) }}
        </el-avatar>
        <div class="author-info">
          <b>{{ post.authorName }}</b>
          <span>发起提问 · {{ formatTime(post.createTime) }}</span>
        </div>
      </div>

      <div class="post-content">{{ post.content }}</div>

      <el-image v-if="post.image" :src="post.image" fit="cover" class="post-image"
                :preview-src-list="[post.image]" hide-on-click-modal />

      <div class="action-bar">
        <div class="action-left">
          <button class="act-btn like" :class="{ on: post.liked }" @click="doLike">
            <el-icon><Star /></el-icon>
            <span>{{ post.liked ? '已点赞' : '点赞' }}</span>
            <em>{{ post.likeCount }}</em>
          </button>
          <button class="act-btn fav" :class="{ on: post.favorited }" @click="doFavorite">
            <el-icon><CollectionTag /></el-icon>
            <span>{{ post.favorited ? '已收藏' : '收藏' }}</span>
          </button>
        </div>
        <button v-if="post.isOwner || post.isAdmin" class="act-btn danger" @click="doDelete">
          <el-icon><Delete /></el-icon>
          <span>删除帖子</span>
        </button>
      </div>
    </article>

    <!-- 回复区 -->
    <section class="reply-block">
      <div class="reply-head">
        <div class="block-title">全部回复</div>
        <span class="reply-badge">{{ replies.length }}</span>
      </div>

      <div v-if="replies.length" class="reply-list">
        <div v-for="(r, i) in replies" :key="r.id" class="reply-item">
          <el-avatar :size="36" :src="r.authorAvatar || undefined" class="reply-avatar">
            {{ (r.authorName || '学').charAt(0) }}
          </el-avatar>

          <div class="reply-body">
            <div class="reply-top">
              <b class="reply-author">{{ r.authorName }}</b>
              <span class="reply-floor">#{{ i + 1 }}</span>
              <span class="reply-time">{{ formatTime(r.createTime) }}</span>
              <button v-if="isMyReply(r)" class="reply-del" @click="doDeleteReply(r)">
                删除
              </button>
            </div>
            <div class="reply-content">{{ r.content }}</div>
          </div>
        </div>
      </div>

      <div v-else class="reply-empty">
        <el-icon><ChatDotRound /></el-icon>
        <p>还没有人回复，来帮帮这位同学吧</p>
      </div>

      <!-- 输入区 -->
      <div class="reply-editor">
        <el-avatar :size="36" :src="userStore.user?.avatar || undefined" class="reply-avatar">
          {{ (userStore.user?.username || '我').charAt(0) }}
        </el-avatar>
        <div class="editor-main">
          <el-input v-model="replyContent" type="textarea" :rows="3" maxlength="500" show-word-limit
                    placeholder="写下你的解答或思路，说明得越具体，越容易被采纳～" />
          <div class="editor-foot">
            <span class="editor-hint">友情帮答，共同进步</span>
            <el-button type="primary" :loading="replying" @click="doReply">发表回复</el-button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail, toggleLike, toggleFavorite, addReply, deletePost, deleteReply } from '../api'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
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

// 只有回复的作者本人才能看到删除按钮
const isMyReply = r => !!userStore.user && r.userId === userStore.user.id

const doDeleteReply = r => {
  ElMessageBox.confirm('确定删除这条回复吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteReply(r.id)
    ElMessage.success('已删除')
    load()
  }).catch(() => {})
}

const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

onMounted(load)
</script>

<style scoped>
.detail-page {
  max-width: 860px;
  margin: 0 auto;
  padding: 24px 20px 70px;
  animation: fade-up 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 主体卡片 */
.post-main {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-xl);
  padding: 32px 36px 24px;
  box-shadow: var(--shadow-md);
}

.post-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.course-chip {
  display: inline-flex;
  align-items: center;
  padding: 3px 13px;
  border-radius: 999px;
  font-size: 12.5px;
  font-weight: 500;
  color: var(--brand-600);
  background: var(--brand-50);
  border: 1px solid #dde2fe;
}

.post-time {
  font-size: 12.5px;
  color: var(--ink-300);
}

.post-title {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.45;
  letter-spacing: -0.6px;
  color: var(--ink-900);
  margin-bottom: 20px;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 22px;
  border-bottom: 1px solid var(--line-soft);
  margin-bottom: 22px;
}

.author-avatar {
  background: var(--grad-brand);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  flex: none;
}

.author-info {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}

.author-info b {
  font-size: 14.5px;
  color: var(--ink-800);
}

.author-info span {
  font-size: 12px;
  color: var(--ink-400);
  margin-top: 2px;
}

.post-content {
  font-size: 15.5px;
  line-height: 2;
  color: var(--ink-700);
  white-space: pre-wrap;
  letter-spacing: 0.1px;
}

.post-image {
  margin-top: 20px;
  max-width: 460px;
  border-radius: var(--r-md);
  box-shadow: var(--shadow-sm);
}

/* 操作条 */
.action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid var(--line-soft);
}

.action-left {
  display: flex;
  gap: 10px;
}

.act-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 38px;
  padding: 0 18px;
  border-radius: 999px;
  border: 1px solid var(--line);
  background: #fff;
  font-family: inherit;
  font-size: 13.5px;
  color: var(--ink-500);
  cursor: pointer;
  transition: all 0.2s;
}

.act-btn:hover {
  border-color: var(--brand-200);
  color: var(--brand-600);
  background: #fbfcff;
}

.act-btn em {
  font-style: normal;
  font-weight: 600;
  padding-left: 8px;
  margin-left: 2px;
  border-left: 1px solid var(--line);
  color: var(--ink-700);
}

.act-btn.like.on {
  color: var(--brand-600);
  background: var(--brand-50);
  border-color: #d3dafd;
}

.act-btn.like.on em {
  color: var(--brand-600);
  border-color: #c7cffd;
}

.act-btn.fav.on {
  color: #d98a00;
  background: var(--amber-soft);
  border-color: #ffe1b0;
}

.act-btn.danger {
  padding: 0 16px;
  color: var(--ink-400);
}

.act-btn.danger:hover {
  color: var(--el-color-danger);
  border-color: #ffd4da;
  background: #fff8f9;
}

/* 回复区 */
.reply-block {
  margin-top: 20px;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-xl);
  padding: 26px 36px 30px;
  box-shadow: var(--shadow-xs);
}

.reply-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 18px;
}

.reply-badge {
  min-width: 24px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--brand-600);
  background: var(--brand-50);
}

.reply-list {
  display: flex;
  flex-direction: column;
}

.reply-item {
  display: flex;
  gap: 13px;
  padding: 16px 12px;
  margin: 0 -12px;
  border-radius: var(--r-md);
  transition: background 0.2s;
}

.reply-item:hover {
  background: #fafbff;
}

.reply-item + .reply-item {
  border-top: 1px solid var(--line-soft);
}

.reply-avatar {
  flex: none;
  background: linear-gradient(135deg, #9aa6fc, #6c7cf8);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.reply-body {
  flex: 1;
  min-width: 0;
}

.reply-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
}

.reply-author {
  font-size: 13.5px;
  color: var(--ink-800);
  font-weight: 600;
}

.reply-floor {
  font-size: 11px;
  color: var(--ink-300);
  background: #f4f6fc;
  padding: 0 6px;
  border-radius: 5px;
}

.reply-time {
  font-size: 12px;
  color: var(--ink-300);
}

.reply-del {
  margin-left: auto;
  border: none;
  background: transparent;
  font-family: inherit;
  font-size: 12px;
  color: var(--ink-300);
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 6px;
  transition: all 0.18s;
  opacity: 0;
}

.reply-item:hover .reply-del {
  opacity: 1;
}

.reply-del:hover {
  color: var(--el-color-danger);
  background: #fff2f4;
}

.reply-content {
  font-size: 14px;
  line-height: 1.8;
  color: var(--ink-700);
  white-space: pre-wrap;
}

.reply-empty {
  padding: 40px 0 30px;
  text-align: center;
  color: var(--ink-300);
}

.reply-empty .el-icon {
  font-size: 34px;
  margin-bottom: 10px;
  color: #d8dff2;
}

.reply-empty p {
  font-size: 13px;
  color: var(--ink-400);
}

/* 输入区 */
.reply-editor {
  display: flex;
  gap: 13px;
  margin-top: 22px;
  padding-top: 22px;
  border-top: 1px dashed var(--line);
}

.editor-main {
  flex: 1;
}

.editor-main :deep(.el-textarea__inner) {
  border-radius: var(--r-md);
  padding: 12px 14px;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.7;
  background: #fafbff;
}

.editor-main :deep(.el-textarea__inner:focus) {
  background: #fff;
}

.editor-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}

.editor-hint {
  font-size: 12px;
  color: var(--ink-300);
}

@media (max-width: 720px) {
  .post-main,
  .reply-block {
    padding: 22px 20px;
  }

  .post-title {
    font-size: 21px;
  }
}
</style>
