<template>
  <el-container class="admin-wrap">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-logo">🛡️ 管理后台</div>
      <el-menu :default-active="$route.path" router background-color="#1a365d"
               text-color="#cbd5e0" active-text-color="#ffffff">
        <el-menu-item index="/admin/course"><el-icon><Collection /></el-icon>课程分类管理</el-menu-item>
        <el-menu-item index="/admin/audit"><el-icon><Document /></el-icon>帖子审核</el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><UserFilled /></el-icon>用户管理</el-menu-item>
        <el-menu-item index="/admin/stats"><el-icon><DataAnalysis /></el-icon>数据统计</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="admin-header-title">校园课程互助答疑系统 · 管理端</div>
        <div class="admin-header-right">
          <span class="muted" style="color:#cbd5e0; margin-right:12px">{{ store.user?.username }}</span>
          <el-button text style="color:#fff" @click="backHome">返回前台</el-button>
          <el-button text style="color:#fff" @click="doLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const store = useUserStore()

const backHome = () => router.push('/')
const doLogout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
    store.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-wrap {
  height: 100vh;
}

.admin-aside {
  background: #1a365d;
}

.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 700;
}

.admin-aside :deep(.el-menu) {
  border-right: none;
}

.admin-aside :deep(.el-menu-item.is-active) {
  background: #2b6cb0 !important;
}

.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 5;
}

.admin-header-title {
  font-weight: 600;
  color: #1a365d;
}

.admin-header-right {
  display: flex;
  align-items: center;
}

.admin-main {
  background: #f0f4f8;
}
</style>
