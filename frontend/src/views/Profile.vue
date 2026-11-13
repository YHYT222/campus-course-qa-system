<template>
  <div class="profile-page">
    <!-- 资料横幅 -->
    <section class="profile-hero">
      <div class="hero-bg"></div>
      <div class="hero-main">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                   :on-success="onAvatarSuccess" accept="image/*">
          <div class="avatar-shell" title="点击更换头像">
            <el-avatar :size="88" :src="store.user?.avatar || undefined" class="profile-avatar">
              {{ (store.user?.username || '学').charAt(0) }}
            </el-avatar>
            <span class="avatar-mask">更换</span>
          </div>
        </el-upload>

        <div class="hero-meta">
          <div class="hero-name-row">
            <h2>{{ store.user?.username }}</h2>
            <span class="role-chip">{{ store.user?.role === 1 ? '管理员' : '学生' }}</span>
          </div>
          <p class="hero-sub">
            学号 {{ store.user?.studentNo }}
            <i></i>
            注册于 {{ formatTime(store.user?.createTime) }}
          </p>
        </div>

        <el-button class="edit-btn" @click="openEdit">
          <el-icon><Edit /></el-icon>
          编辑资料
        </el-button>
      </div>

      <div class="hero-stats">
        <div class="hs-cell">
          <b>{{ myPosts.length }}</b>
          <span>我的提问</span>
        </div>
        <div class="hs-line"></div>
        <div class="hs-cell">
          <b>{{ myReplies.length }}</b>
          <span>我的回复</span>
        </div>
        <div class="hs-line"></div>
        <div class="hs-cell">
          <b>{{ myFavorites.length }}</b>
          <span>我的收藏</span>
        </div>
      </div>
    </section>

    <!-- 内容区 -->
    <section class="content-card">
      <el-tabs v-model="tab" @tab-change="onTabChange">
        <el-tab-pane name="posts">
          <template #label>
            我的提问<span class="tab-count">{{ myPosts.length }}</span>
          </template>

          <div v-if="myPosts.length" class="record-list">
            <div v-for="p in myPosts" :key="p.id" class="record-item"
                 @click="$router.push(`/post/${p.id}`)">
              <div class="ri-main">
                <div class="ri-title">{{ p.title }}</div>
                <div class="ri-meta">
                  <span class="chip">{{ p.courseName }}</span>
                  <span class="m"><el-icon><ChatDotRound /></el-icon>{{ p.replyCount }}</span>
                  <span class="m"><el-icon><Star /></el-icon>{{ p.likeCount }}</span>
                  <span class="m"><el-icon><Clock /></el-icon>{{ formatTime(p.createTime) }}</span>
                </div>
              </div>
              <el-tag size="small" :type="statusType(p.status)" effect="light" round>
                {{ statusText(p.status) }}
              </el-tag>
            </div>
          </div>
          <div v-else class="mini-empty">
            <el-icon><DocumentAdd /></el-icon>
            <p>还没有发布过问题</p>
            <el-button type="primary" plain size="small" @click="$router.push('/publish')">去提问</el-button>
          </div>
        </el-tab-pane>

        <el-tab-pane name="replies">
          <template #label>
            我的回复<span class="tab-count">{{ myReplies.length }}</span>
          </template>

          <div v-if="myReplies.length" class="record-list">
            <div v-for="r in myReplies" :key="r.id" class="record-item"
                 @click="$router.push(`/post/${r.postId}`)">
              <div class="ri-main">
                <div class="ri-title reply-title">
                  <el-icon class="rt-icon"><ChatLineRound /></el-icon>
                  回复了「{{ r.postTitle }}」
                </div>
                <div class="ri-snippet">{{ r.content }}</div>
                <div class="ri-meta">
                  <span class="m"><el-icon><Clock /></el-icon>{{ formatTime(r.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="mini-empty">
            <el-icon><ChatDotRound /></el-icon>
            <p>还没有回复过任何问题</p>
          </div>
        </el-tab-pane>

        <el-tab-pane name="favorites">
          <template #label>
            我的收藏<span class="tab-count">{{ myFavorites.length }}</span>
          </template>

          <div v-if="myFavorites.length" class="record-list">
            <div v-for="f in myFavorites" :key="f.id" class="record-item"
                 @click="$router.push(`/post/${f.postId}`)">
              <div class="ri-main">
                <div class="ri-title">{{ f.title }}</div>
                <div class="ri-meta">
                  <span class="chip">{{ f.courseName }}</span>
                  <span class="m"><el-icon><Star /></el-icon>{{ f.likeCount }}</span>
                  <span class="m"><el-icon><Clock /></el-icon>收藏于 {{ formatTime(f.createTime) }}</span>
                </div>
              </div>
              <el-icon class="fav-mark"><CollectionTag /></el-icon>
            </div>
          </div>
          <div v-else class="mini-empty">
            <el-icon><CollectionTag /></el-icon>
            <p>还没有收藏任何帖子</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>

    <!-- 编辑资料 -->
    <el-dialog v-model="editVisible" title="编辑个人资料" width="440px">
      <el-form label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.username" maxlength="20" placeholder="输入新昵称" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                     :on-success="onEditAvatarSuccess" accept="image/*">
            <div class="dialog-avatar-row">
              <el-avatar :size="52" :src="editForm.avatar || store.user?.avatar || undefined"
                         class="profile-avatar">
                {{ (store.user?.username || '学').charAt(0) }}
              </el-avatar>
              <span class="upload-link">选择新头像图片</span>
            </div>
          </el-upload>
          <div v-if="editForm.avatar" class="muted" style="margin-top: 6px">已选择新头像，保存后生效</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyPosts, getMyReplies, getMyFavorites, updateProfile } from '../api'
import { useUserStore } from '../stores/user'

const store = useUserStore()
const tab = ref('posts')
const myPosts = ref([])
const myReplies = ref([])
const myFavorites = ref([])
const editVisible = ref(false)
const saving = ref(false)

const uploadUrl = '/api/file/upload'
const uploadHeaders = { token: localStorage.getItem('token') || '' }

const editForm = reactive({ username: '', avatar: '' })

const statusText = s => ({ 0: '待审核', 1: '已发布', 2: '已屏蔽' })[s] || '未知'
const statusType = s => ({ 0: 'warning', 1: 'success', 2: 'danger' })[s] || 'info'
const formatTime = t => (t || '').replace('T', ' ').slice(0, 16)

const loadTab = async () => {
  if (tab.value === 'posts') {
    myPosts.value = (await getMyPosts()).data
  } else if (tab.value === 'replies') {
    myReplies.value = (await getMyReplies()).data
  } else {
    myFavorites.value = (await getMyFavorites()).data
  }
}

const onTabChange = loadTab

const openEdit = () => {
  editForm.username = store.user?.username || ''
  editForm.avatar = ''
  editVisible.value = true
}

const onEditAvatarSuccess = res => {
  if (res.code === 200) {
    editForm.avatar = res.data.url
    ElMessage.success('头像上传成功，请点击保存')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const onAvatarSuccess = async res => {
  if (res.code === 200) {
    const updated = (await updateProfile({ avatar: res.data.url })).data
    store.setUser(updated)
    ElMessage.success('头像已更新')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const saveProfile = async () => {
  if (!editForm.username.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const data = { username: editForm.username.trim() }
    if (editForm.avatar) data.avatar = editForm.avatar
    const updated = (await updateProfile(data)).data
    store.setUser(updated)
    ElMessage.success('资料已更新')
    editVisible.value = false
  } finally {
    saving.value = false
  }
}

// 首屏一次性拉取三类数据，保证顶部统计数字准确
onMounted(async () => {
  const [posts, replies, favorites] = await Promise.all([
    getMyPosts(),
    getMyReplies(),
    getMyFavorites()
  ])
  myPosts.value = posts.data
  myReplies.value = replies.data
  myFavorites.value = favorites.data
})
</script>

<style scoped>
.profile-page {
  max-width: 980px;
  margin: 0 auto;
  padding: 24px 20px 70px;
  animation: fade-up 0.45s cubic-bezier(0.22, 1, 0.36, 1);
}

/* 资料横幅 */
.profile-hero {
  position: relative;
  border-radius: var(--r-xl);
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--line);
  box-shadow: var(--shadow-md);
  margin-bottom: 20px;
}

.hero-bg {
  height: 116px;
  background: linear-gradient(120deg, #4655e8 0%, #5b6cf9 48%, #7a5cf0 100%);
  position: relative;
}

.hero-bg::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  right: -80px;
  top: -160px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.25), transparent 66%);
}

.hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 1px 1px, rgba(255, 255, 255, 0.18) 1px, transparent 0);
  background-size: 18px 18px;
  opacity: 0.5;
}

.hero-main {
  position: relative;
  display: grid;
  grid-template-columns: 96px 1fr auto;
  align-items: end;
  gap: 20px;
  padding: 0 30px;
  margin-top: -50px;
}

.hero-main :deep(.el-upload) {
  line-height: 0;
}

.avatar-shell {
  position: relative;
  border-radius: 50%;
  padding: 4px;
  background: #fff;
  box-shadow: 0 8px 20px -8px rgba(45, 57, 120, 0.4);
  cursor: pointer;
  width: 96px;
  height: 96px;
}

.profile-avatar {
  background: var(--grad-brand);
  color: #fff;
  font-size: 32px;
  font-weight: 600;
  width: 100%;
  height: 100%;
}

.avatar-mask {
  position: absolute;
  inset: 4px;
  border-radius: 50%;
  background: rgba(23, 28, 48, 0.55);
  color: #fff;
  font-size: 12px;
  display: grid;
  place-items: center;
  opacity: 0;
  transition: opacity 0.22s;
}

.avatar-shell:hover .avatar-mask {
  opacity: 1;
}

.hero-meta {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding-bottom: 4px;
  min-width: 0;
}

.hero-name-row {
  display: flex;
  align-items: center;
  gap: 9px;
}

.hero-meta h2 {
  font-size: 21px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.3px;
}

.role-chip {
  font-size: 11px;
  padding: 1px 9px;
  border-radius: 999px;
  color: var(--brand-600);
  background: var(--brand-50);
  border: 1px solid #dde2fe;
}

.hero-sub {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 5px;
  font-size: 12.5px;
  color: var(--ink-400);
}

.hero-sub i {
  width: 1px;
  height: 10px;
  background: var(--line);
}

.edit-btn {
  margin-bottom: 8px;
  border-radius: 999px;
  border-color: var(--line);
  color: var(--ink-500);
}

.edit-btn:hover {
  color: var(--brand-600);
  border-color: var(--brand-200);
  background: var(--brand-50);
}

.hero-stats {
  display: flex;
  align-items: center;
  padding: 18px 30px 22px;
  margin-top: 14px;
  border-top: 1px solid var(--line-soft);
}

.hs-cell {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}

.hs-cell b {
  font-size: 22px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.5px;
}

.hs-cell span {
  font-size: 12px;
  color: var(--ink-400);
}

.hs-line {
  width: 1px;
  height: 26px;
  background: var(--line);
}

/* 内容卡 */
.content-card {
  background: #fff;
  border: 1px solid var(--line);
  border-radius: var(--r-xl);
  padding: 10px 28px 16px;
  box-shadow: var(--shadow-xs);
}

.content-card :deep(.el-tabs__header) {
  margin-bottom: 8px;
}

.content-card :deep(.el-tabs__item) {
  height: 50px;
  font-size: 14.5px;
}

.tab-count {
  display: inline-grid;
  place-items: center;
  min-width: 19px;
  height: 18px;
  padding: 0 5px;
  margin-left: 6px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  color: var(--ink-500);
  background: #f1f4fc;
}

.content-card :deep(.el-tabs__item.is-active) .tab-count {
  color: var(--brand-600);
  background: var(--brand-50);
}

/* 记录项 */
.record-list {
  display: flex;
  flex-direction: column;
  padding: 6px 0 10px;
}

.record-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 15px 14px;
  margin: 0 -14px;
  border-radius: var(--r-md);
  cursor: pointer;
  transition: background 0.2s;
}

.record-item:hover {
  background: #fafbff;
}

.record-item + .record-item {
  border-top: 1px solid var(--line-soft);
}

.ri-main {
  flex: 1;
  min-width: 0;
}

.ri-title {
  font-size: 14.5px;
  font-weight: 600;
  color: var(--ink-800);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s;
}

.record-item:hover .ri-title {
  color: var(--brand-600);
}

.reply-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.rt-icon {
  color: var(--brand-400);
  font-size: 15px;
}

.ri-snippet {
  margin-top: 5px;
  font-size: 13px;
  line-height: 1.7;
  color: var(--ink-500);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ri-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 7px;
}

.chip {
  display: inline-flex;
  padding: 1px 9px;
  border-radius: 999px;
  font-size: 11.5px;
  color: var(--brand-600);
  background: var(--brand-50);
}

.m {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--ink-400);
}

.m .el-icon {
  font-size: 13px;
}

.fav-mark {
  font-size: 18px;
  color: #f0c274;
}

/* 空状态 */
.mini-empty {
  padding: 54px 0 44px;
  text-align: center;
}

.mini-empty .el-icon {
  font-size: 34px;
  color: #d8dff2;
  margin-bottom: 10px;
}

.mini-empty p {
  font-size: 13px;
  color: var(--ink-400);
  margin-bottom: 14px;
}

/* 弹窗头像 */
.dialog-avatar-row {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
}

.upload-link {
  font-size: 13px;
  color: var(--brand-500);
}

@media (max-width: 720px) {
  .hero-main {
    padding: 0 18px;
    flex-wrap: wrap;
    gap: 12px;
  }

  .hero-stats {
    padding: 16px 18px 20px;
  }

  .content-card {
    padding: 6px 16px 12px;
  }
}
</style>
