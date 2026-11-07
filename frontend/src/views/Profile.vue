<template>
  <div class="page-container">
    <!-- 个人资料卡 -->
    <div class="profile-card">
      <div class="profile-top">
        <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                   :on-success="onAvatarSuccess" accept="image/*">
          <el-avatar :size="64" :src="store.user?.avatar || undefined" class="profile-avatar">
            {{ (store.user?.username || '学').charAt(0) }}
          </el-avatar>
        </el-upload>
        <div class="profile-info">
          <div class="profile-name">{{ store.user?.username }}</div>
          <div class="muted">学号：{{ store.user?.studentNo }} · 注册于 {{ formatTime(store.user?.createTime) }}</div>
        </div>
        <el-button type="primary" plain @click="openEdit">编辑资料</el-button>
      </div>
    </div>

    <!-- 三个Tab -->
    <div class="profile-card">
      <el-tabs v-model="tab" @tab-change="onTabChange">
        <el-tab-pane label="我的提问" name="posts">
          <div v-if="myPosts.length" class="record-list">
            <div v-for="p in myPosts" :key="p.id" class="record-item" @click="$router.push(`/post/${p.id}`)">
              <div class="flex-between">
                <span class="record-title">{{ p.title }}</span>
                <el-tag size="small" :type="statusType(p.status)">{{ statusText(p.status) }}</el-tag>
              </div>
              <div class="post-meta" style="margin-top: 8px">
                <el-tag size="small" effect="plain">{{ p.courseName }}</el-tag>
                <span>💬 {{ p.replyCount }}</span>
                <span>👍 {{ p.likeCount }}</span>
                <span>🕐 {{ formatTime(p.createTime) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="还没有发布过问题" :image-size="80" />
        </el-tab-pane>

        <el-tab-pane label="我的回复" name="replies">
          <div v-if="myReplies.length" class="record-list">
            <div v-for="r in myReplies" :key="r.id" class="record-item" @click="$router.push(`/post/${r.postId}`)">
              <div class="record-title">回复：{{ r.postTitle }}</div>
              <div class="record-content">{{ r.content }}</div>
              <div class="muted" style="margin-top: 6px">🕐 {{ formatTime(r.createTime) }}</div>
            </div>
          </div>
          <el-empty v-else description="还没有回复过任何问题" :image-size="80" />
        </el-tab-pane>

        <el-tab-pane label="我的收藏" name="favorites">
          <div v-if="myFavorites.length" class="record-list">
            <div v-for="f in myFavorites" :key="f.id" class="record-item" @click="$router.push(`/post/${f.postId}`)">
              <div class="flex-between">
                <span class="record-title">{{ f.title }}</span>
                <el-tag size="small" effect="plain">{{ f.courseName }}</el-tag>
              </div>
              <div class="post-meta" style="margin-top: 8px">
                <span>👍 {{ f.likeCount }}</span>
                <span>收藏于 {{ formatTime(f.createTime) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="还没有收藏任何帖子" :image-size="80" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editVisible" title="编辑个人资料" width="420px">
      <el-form label-width="70px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.username" maxlength="20" placeholder="输入新昵称" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
                     :on-success="onEditAvatarSuccess" accept="image/*">
            <el-button>选择新头像图片</el-button>
          </el-upload>
          <div v-if="editForm.avatar" class="muted">已选择新头像，保存后生效</div>
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
const statusType = s => ({ 0: 'info', 1: 'success', 2: 'danger' })[s] || 'info'
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

onMounted(loadTab)
</script>

<style scoped>
.profile-card {
  background: #fff;
  border-radius: 10px;
  padding: 22px 28px;
  margin-bottom: 16px;
  border: 1px solid #e8edf3;
}

.profile-top {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-avatar {
  cursor: pointer;
  background: #2b6cb0;
  font-size: 24px;
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: #1a365d;
}

.profile-info {
  flex: 1;
}

.record-list {
  margin-top: 8px;
}

.record-item {
  padding: 14px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.record-item:hover {
  background: #f7fafc;
}

.record-title {
  font-weight: 600;
  color: #1a365d;
  font-size: 15px;
}

.record-content {
  margin-top: 6px;
  color: #718096;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
