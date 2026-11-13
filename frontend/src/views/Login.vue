<template>
  <div class="login-page">
    <div class="login-shell">
      <!-- 左侧品牌区 -->
      <aside class="brand-side">
        <div class="brand-glow brand-glow-a"></div>
        <div class="brand-glow brand-glow-b"></div>

        <div class="brand-top">
          <div class="brand-mark">问</div>
          <div class="brand-name">
            <strong>校园课程互助答疑</strong>
            <span>Course Q &amp; A</span>
          </div>
        </div>

        <div class="brand-body">
          <h1>把疑问留在这里<br />把答案留给后来人</h1>
          <p class="brand-desc">
            按课程组织提问与解答，让每一次答疑都沉淀成可检索的知识，而不是在群聊里被刷走。
          </p>

          <ul class="brand-points">
            <li>
              <span class="pt-icon">01</span>
              <div>
                <b>按课程分类</b>
                <em>高数、英语、专业课，问题各归其位</em>
              </div>
            </li>
            <li>
              <span class="pt-icon">02</span>
              <div>
                <b>内容审核</b>
                <em>发帖先审后发，社区氛围干净</em>
              </div>
            </li>
            <li>
              <span class="pt-icon">03</span>
              <div>
                <b>知识沉淀</b>
                <em>优质解答能被下一届同学检索到</em>
              </div>
            </li>
          </ul>
        </div>

        <div class="brand-foot">软件工程课程设计 · 双人小组项目</div>
      </aside>

      <!-- 右侧表单区 -->
      <section class="form-side">
        <div class="form-head">
          <h2>{{ tab === 'login' ? '欢迎回来' : '创建账号' }}</h2>
          <p>{{ tab === 'login' ? '登录后即可提问、答疑与收藏' : '用学号注册，随时管理你的提问记录' }}</p>
        </div>

        <!-- 切换 -->
        <div class="tab-switch">
          <span class="thumb" :class="{ right: tab === 'register' }"></span>
          <button :class="{ active: tab === 'login' }" @click="tab = 'login'">登 录</button>
          <button :class="{ active: tab === 'register' }" @click="tab = 'register'">注 册</button>
        </div>

        <!-- 登录表单 -->
        <div v-if="tab === 'login'" class="form-body">
          <el-form :model="loginForm" size="large">
            <el-form-item>
              <el-input v-model="loginForm.studentNo" placeholder="学号 / 管理员账号" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" show-password
                        placeholder="密码" :prefix-icon="Lock" @keyup.enter="doLogin" />
            </el-form-item>
            <el-button type="primary" class="w100" size="large" :loading="loading" @click="doLogin">
              登 录
            </el-button>
          </el-form>

          <div class="demo-box">
            <span class="demo-label">演示账号</span>
            <div class="demo-row">
              <code>admin / admin123</code>
              <em>管理员</em>
            </div>
            <div class="demo-row">
              <code>20230001 / 123456</code>
              <em>学生</em>
            </div>
          </div>
        </div>

        <!-- 注册表单 -->
        <div v-else class="form-body">
          <el-form :model="regForm" size="large">
            <el-form-item>
              <el-input v-model="regForm.studentNo" placeholder="学号（用于登录）" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.username" placeholder="昵称（可选，默认学号）" :prefix-icon="Postcard" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" show-password
                        placeholder="密码（至少 6 位）" :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password2" type="password" show-password
                        placeholder="确认密码" :prefix-icon="Lock" @keyup.enter="doRegister" />
            </el-form-item>
            <el-button type="primary" class="w100" size="large" :loading="loading" @click="doRegister">
              注 册
            </el-button>
          </el-form>
          <p class="agree-tip">注册即表示同意遵守校园社区内容规范，违规内容将被下架</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Postcard } from '@element-plus/icons-vue'
import { login, register } from '../api'
import { useUserStore } from '../stores/user'

const router = useRouter()
const store = useUserStore()
const tab = ref('login')
const loading = ref(false)

const loginForm = reactive({ studentNo: '', password: '' })
const regForm = reactive({ studentNo: '', username: '', password: '', password2: '' })

const doLogin = async () => {
  if (!loginForm.studentNo || !loginForm.password) {
    ElMessage.warning('请输入学号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login({ studentNo: loginForm.studentNo.trim(), password: loginForm.password })
    store.setLogin(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    router.push(res.data.user.role === 1 ? '/admin/stats' : '/')
  } finally {
    loading.value = false
  }
}

const doRegister = async () => {
  if (!regForm.studentNo || !regForm.password) {
    ElMessage.warning('学号和密码不能为空')
    return
  }
  if (regForm.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  if (regForm.password !== regForm.password2) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    await register({
      studentNo: regForm.studentNo.trim(),
      username: regForm.username.trim(),
      password: regForm.password
    })
    ElMessage.success('注册成功，请登录')
    tab.value = 'login'
    loginForm.studentNo = regForm.studentNo
    loginForm.password = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background:
    radial-gradient(1100px 700px at 12% 8%, #e8ecff 0%, transparent 58%),
    radial-gradient(900px 620px at 92% 92%, #e2faf5 0%, transparent 58%),
    #f4f6fd;
}

.login-shell {
  width: 100%;
  max-width: 1000px;
  min-height: 610px;
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  background: #fff;
  border-radius: 28px;
  overflow: hidden;
  box-shadow:
    0 40px 80px -40px rgba(34, 44, 90, 0.35),
    0 2px 8px rgba(23, 28, 48, 0.04);
  animation: shell-in 0.6s cubic-bezier(0.22, 1, 0.36, 1);
}

@keyframes shell-in {
  from {
    opacity: 0;
    transform: translateY(22px) scale(0.985);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* ========== 左侧品牌 ========== */
.brand-side {
  position: relative;
  overflow: hidden;
  padding: 40px 46px 34px;
  display: flex;
  flex-direction: column;
  color: #fff;
  background: linear-gradient(158deg, #3f4ee0 0%, #5b6cf9 42%, #7b5cf0 100%);
}

.brand-glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.brand-glow-a {
  width: 360px;
  height: 360px;
  top: -140px;
  right: -110px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.28), transparent 66%);
}

.brand-glow-b {
  width: 320px;
  height: 320px;
  bottom: -150px;
  left: -110px;
  background: radial-gradient(circle, rgba(45, 212, 191, 0.45), transparent 68%);
}

.brand-side::after {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.06) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at 70% 20%, #000 0%, transparent 78%);
  pointer-events: none;
}

.brand-top {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 13px;
  display: grid;
  place-items: center;
  font-size: 20px;
  font-weight: 700;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.32);
  backdrop-filter: blur(6px);
}

.brand-name {
  display: flex;
  flex-direction: column;
  line-height: 1.25;
}

.brand-name strong {
  font-size: 16px;
  letter-spacing: 0.4px;
}

.brand-name span {
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  opacity: 0.62;
}

.brand-body {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 34px 0 24px;
}

.brand-body h1 {
  font-size: 33px;
  line-height: 1.42;
  font-weight: 700;
  letter-spacing: -0.6px;
  margin-bottom: 16px;
}

.brand-desc {
  font-size: 14px;
  line-height: 1.85;
  color: rgba(255, 255, 255, 0.78);
  max-width: 340px;
}

.brand-points {
  list-style: none;
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.brand-points li {
  display: flex;
  align-items: center;
  gap: 13px;
}

.pt-icon {
  width: 34px;
  height: 34px;
  flex: none;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  color: rgba(255, 255, 255, 0.92);
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.brand-points b {
  display: block;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.brand-points em {
  font-style: normal;
  font-size: 12.5px;
  color: rgba(255, 255, 255, 0.66);
}

.brand-foot {
  position: relative;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.4px;
}

/* ========== 右侧表单 ========== */
.form-side {
  padding: 54px 52px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-head h2 {
  font-size: 26px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.5px;
}

.form-head p {
  margin-top: 8px;
  font-size: 13.5px;
  color: var(--ink-400);
}

.tab-switch {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  margin: 26px 0 22px;
  padding: 4px;
  border-radius: 12px;
  background: #f2f4fb;
}

.tab-switch .thumb {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  border-radius: 9px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(37, 47, 100, 0.1);
  transition: transform 0.28s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-switch .thumb.right {
  transform: translateX(100%);
}

.tab-switch button {
  position: relative;
  z-index: 1;
  border: none;
  background: transparent;
  padding: 10px 0;
  font-size: 14.5px;
  font-weight: 500;
  color: var(--ink-500);
  cursor: pointer;
  font-family: inherit;
  transition: color 0.2s;
}

.tab-switch button.active {
  color: var(--brand-600);
  font-weight: 600;
}

.form-body :deep(.el-form-item) {
  margin-bottom: 16px;
}

.form-body :deep(.el-input__wrapper) {
  border-radius: 11px;
  padding: 4px 14px;
  background: #fafbff;
}

.form-body :deep(.el-input__wrapper.is-focus) {
  background: #fff;
}

.w100 {
  width: 100%;
  margin-top: 4px;
  border-radius: 11px;
  height: 46px;
  font-size: 15px;
  letter-spacing: 2px;
}

.demo-box {
  margin-top: 26px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8faff;
  border: 1px dashed #dbe2f6;
}

.demo-label {
  display: block;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--ink-300);
  margin-bottom: 8px;
}

.demo-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 3px 0;
}

.demo-row code {
  font-family: 'SFMono-Regular', Consolas, monospace;
  font-size: 12.5px;
  color: var(--ink-700);
}

.demo-row em {
  font-style: normal;
  font-size: 11.5px;
  color: var(--ink-400);
  background: #fff;
  padding: 1px 8px;
  border-radius: 999px;
  border: 1px solid var(--line);
}

.agree-tip {
  margin-top: 18px;
  font-size: 12px;
  color: var(--ink-400);
  line-height: 1.7;
}

@media (max-width: 860px) {
  .login-shell {
    grid-template-columns: 1fr;
    max-width: 460px;
    min-height: auto;
  }

  .brand-side {
    display: none;
  }

  .form-side {
    padding: 40px 32px;
  }
}
</style>
