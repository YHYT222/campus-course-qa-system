<template>
  <div>
    <header class="nav-header">
      <div class="nav-logo" @click="$router.push('/')">📚 校园课程互助答疑</div>
      <div class="nav-menu">
        <div class="nav-item" :class="{ active: $route.path === '/' }" @click="$router.push('/')">首页</div>
        <div class="nav-item" :class="{ active: $route.path === '/publish' }" @click="$router.push('/publish')">发布问题</div>
        <div class="nav-item" :class="{ active: $route.path === '/profile' }" @click="$router.push('/profile')">个人中心</div>
        <el-dropdown v-if="store.user" @command="onCommand">
          <span class="nav-user">
            <el-avatar :size="30" :src="store.user.avatar || undefined">
              {{ (store.user.username || '学').charAt(0) }}
            </el-avatar>
            <span class="nav-username">{{ store.user.username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="store.isAdmin" command="admin">进入管理后台</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>
    <router-view />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()

const onCommand = cmd => {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
      store.logout()
      router.push('/login')
    }).catch(() => {})
  } else if (cmd === 'admin') {
    router.push('/admin/course')
  }
}
</script>

<style scoped>
.nav-user {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}

.nav-username {
  color: #fff;
  font-size: 14px;
}
</style>
