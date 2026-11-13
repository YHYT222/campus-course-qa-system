<template>
  <div class="student-shell">
    <header class="topbar">
      <div class="topbar-inner">
        <!-- 品牌 -->
        <div class="brand" @click="$router.push('/')">
          <div class="brand-mark">问</div>
          <div class="brand-text">
            <strong>校园课程互助答疑</strong>
            <span>CAMPUS Q &amp; A</span>
          </div>
        </div>

        <!-- 导航 -->
        <nav class="nav">
          <div class="nav-link" :class="{ active: $route.path === '/' }" @click="$router.push('/')">
            首页
          </div>
          <div class="nav-link" :class="{ active: $route.path === '/publish' }" @click="$router.push('/publish')">
            发布问题
          </div>
          <div class="nav-link" :class="{ active: $route.path === '/profile' }" @click="$router.push('/profile')">
            个人中心
          </div>
        </nav>

        <!-- 右侧 -->
        <div class="nav-right">
          <button class="ask-btn" @click="$router.push('/publish')">
            <el-icon><EditPen /></el-icon>
            <span>我要提问</span>
          </button>

          <el-dropdown v-if="store.user" trigger="click" @command="onCommand">
            <div class="user-chip">
              <el-avatar :size="30" :src="store.user.avatar || undefined" class="chip-avatar">
                {{ (store.user.username || '学').charAt(0) }}
              </el-avatar>
              <span class="chip-name">{{ store.user.username }}</span>
              <el-icon class="chip-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="store.isAdmin" command="admin">
                  <el-icon><Setting /></el-icon>进入管理后台
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
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
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  } else if (cmd === 'admin') {
    router.push('/admin/stats')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.student-shell {
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: saturate(180%) blur(14px);
  -webkit-backdrop-filter: saturate(180%) blur(14px);
  border-bottom: 1px solid rgba(233, 237, 247, 0.9);
}

.topbar-inner {
  max-width: 1120px;
  margin: 0 auto;
  height: 66px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 28px;
}

/* 品牌 */
.brand {
  display: flex;
  align-items: center;
  gap: 11px;
  cursor: pointer;
  user-select: none;
  flex: none;
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 11px;
  display: grid;
  place-items: center;
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  background: var(--grad-brand);
  box-shadow: 0 6px 14px -6px rgba(91, 108, 249, 0.85);
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.brand:hover .brand-mark {
  transform: rotate(-8deg) scale(1.06);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-text strong {
  font-size: 15px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: 0.2px;
}

.brand-text span {
  font-size: 9.5px;
  letter-spacing: 1.6px;
  color: var(--ink-300);
  margin-top: 2px;
}

/* 导航 */
.nav {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.nav-link {
  position: relative;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 14.5px;
  color: var(--ink-500);
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.nav-link:hover {
  color: var(--brand-600);
  background: var(--brand-50);
}

.nav-link.active {
  color: var(--brand-600);
  font-weight: 600;
  background: var(--brand-50);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  left: 50%;
  bottom: -14px;
  width: 18px;
  height: 2.5px;
  border-radius: 2px;
  transform: translateX(-50%);
  background: var(--grad-brand);
}

/* 右侧 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: none;
}

.ask-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border: none;
  border-radius: 10px;
  font-family: inherit;
  font-size: 13.5px;
  font-weight: 500;
  color: #fff;
  cursor: pointer;
  background: var(--grad-brand);
  box-shadow: 0 8px 18px -8px rgba(91, 108, 249, 0.9);
  transition: all 0.22s;
}

.ask-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 22px -8px rgba(91, 108, 249, 1);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  padding: 0 10px 0 4px;
  border-radius: 999px;
  cursor: pointer;
  outline: none;
  transition: background 0.2s;
}

.user-chip:hover {
  background: #f2f5fd;
}

.chip-avatar {
  background: var(--grad-brand);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.chip-name {
  font-size: 13.5px;
  color: var(--ink-700);
  max-width: 90px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chip-arrow {
  font-size: 12px;
  color: var(--ink-300);
}

@media (max-width: 900px) {
  .brand-text,
  .ask-btn span,
  .chip-name {
    display: none;
  }

  .topbar-inner {
    gap: 14px;
  }
}
</style>
