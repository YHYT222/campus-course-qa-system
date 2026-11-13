<template>
  <el-container class="admin-shell">
    <!-- 侧栏 -->
    <el-aside width="236px" class="aside">
      <div class="aside-glow"></div>

      <div class="aside-brand">
        <div class="ab-mark">
          <el-icon><Setting /></el-icon>
        </div>
        <div class="ab-text">
          <b>管理后台</b>
          <span>ADMIN CONSOLE</span>
        </div>
      </div>

      <div class="menu-label">内容与用户</div>
      <nav class="menu">
        <div v-for="m in menus" :key="m.path" class="menu-item"
             :class="{ active: $route.path === m.path }" @click="router.push(m.path)">
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.label }}</span>
          <i class="active-bar"></i>
        </div>
      </nav>

      <div class="aside-foot">
        <div class="foot-user">
          <el-avatar :size="34" :src="store.user?.avatar || undefined" class="foot-avatar">
            {{ (store.user?.username || '管').charAt(0) }}
          </el-avatar>
          <div class="foot-info">
            <b>{{ store.user?.username }}</b>
            <span>管理员</span>
          </div>
        </div>
        <div class="foot-links">
          <span @click="backHome">返回前台</span>
          <i></i>
          <span @click="doLogout">退出登录</span>
        </div>
      </div>
    </el-aside>

    <!-- 主区 -->
    <el-container class="main-wrap">
      <el-header class="admin-header">
        <div class="crumb">
          <span>管理后台</span>
          <em>/</em>
          <b>{{ currentTitle }}</b>
        </div>
        <div class="header-right">
          <div class="date-chip">
            <el-icon><Calendar /></el-icon>
            {{ today }}
          </div>
          <el-button text class="head-btn" @click="backHome">
            <el-icon><HomeFilled /></el-icon>前台首页
          </el-button>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const menus = [
  { path: '/admin/stats', label: '数据统计', icon: 'DataAnalysis' },
  { path: '/admin/audit', label: '帖子审核', icon: 'Document' },
  { path: '/admin/course', label: '课程分类管理', icon: 'Collection' },
  { path: '/admin/users', label: '用户管理', icon: 'UserFilled' }
]

const currentTitle = computed(() => {
  const found = menus.find(m => route.path === m.path)
  return found ? found.label : '管理后台'
})

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
})

const backHome = () => router.push('/')

const doLogout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
    store.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-shell {
  height: 100vh;
  background: var(--bg);
}

/* ===== 侧栏 ===== */
.aside {
  position: relative;
  display: flex;
  flex-direction: column;
  background: var(--grad-dark);
  overflow: hidden;
}

.aside-glow {
  position: absolute;
  width: 320px;
  height: 320px;
  top: -150px;
  left: -120px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(91, 108, 249, 0.55), transparent 66%);
  pointer-events: none;
}

.aside-brand {
  position: relative;
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 24px 22px 26px;
}

.ab-mark {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: grid;
  place-items: center;
  font-size: 17px;
  color: #fff;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.ab-text {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
}

.ab-text b {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  letter-spacing: 0.5px;
}

.ab-text span {
  font-size: 9px;
  letter-spacing: 1.8px;
  color: rgba(255, 255, 255, 0.36);
  margin-top: 2px;
}

.menu-label {
  padding: 0 24px 10px;
  font-size: 10.5px;
  letter-spacing: 1.6px;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.28);
}

.menu {
  position: relative;
  flex: 1;
  padding: 0 12px;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 11px;
  height: 44px;
  padding: 0 14px;
  border-radius: 11px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.62);
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.menu-item .el-icon {
  font-size: 17px;
  transition: transform 0.2s;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.08);
}

.menu-item:hover .el-icon {
  transform: translateX(1px);
}

.menu-item.active {
  color: #fff;
  font-weight: 500;
  background: linear-gradient(100deg, rgba(91, 108, 249, 0.85), rgba(122, 92, 240, 0.6));
  box-shadow: 0 8px 20px -10px rgba(91, 108, 249, 1);
}

.active-bar {
  position: absolute;
  right: 10px;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #7df5db;
  opacity: 0;
  transition: opacity 0.2s;
}

.menu-item.active .active-bar {
  opacity: 1;
}

/* 侧栏底部 */
.aside-foot {
  padding: 16px 18px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.foot-user {
  display: flex;
  align-items: center;
  gap: 10px;
}

.foot-avatar {
  background: linear-gradient(135deg, #7c8bfa, #5b6cf9);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.foot-info {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.foot-info b {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.foot-info span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);
}

.foot-links {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.45);
}

.foot-links span {
  cursor: pointer;
  transition: color 0.2s;
}

.foot-links span:hover {
  color: #fff;
}

.foot-links i {
  width: 1px;
  height: 9px;
  background: rgba(255, 255, 255, 0.16);
}

/* ===== 主区 ===== */
.main-wrap {
  min-width: 0;
}

.admin-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: saturate(180%) blur(12px);
  border-bottom: 1px solid var(--line);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  z-index: 5;
}

.crumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
}

.crumb span {
  color: var(--ink-400);
}

.crumb em {
  font-style: normal;
  color: var(--ink-300);
}

.crumb b {
  color: var(--ink-900);
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.date-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12.5px;
  color: var(--ink-400);
  background: #f5f7fd;
  padding: 5px 12px;
  border-radius: 999px;
}

.head-btn {
  color: var(--ink-500);
  font-size: 13px;
}

.head-btn:hover {
  color: var(--brand-600);
}

.admin-main {
  padding: 24px 28px 40px;
  background: var(--bg);
  overflow-y: auto;
}

@media (max-width: 860px) {
  .aside {
    width: 68px !important;
  }

  .ab-text,
  .menu-label,
  .menu-item span,
  .foot-info,
  .foot-links,
  .active-bar {
    display: none;
  }

  .aside-brand {
    padding: 20px 0;
    justify-content: center;
  }

  .menu-item {
    justify-content: center;
    padding: 0;
  }

  .foot-user {
    justify-content: center;
  }

  .date-chip {
    display: none;
  }
}
</style>
