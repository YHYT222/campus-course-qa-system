<template>
  <div class="login-bg">
    <div class="login-card">
      <div class="login-title">📚 校园课程互助答疑系统</div>
      <div class="login-sub">提问 · 答疑 · 互助</div>

      <el-tabs v-model="tab" stretch>
        <el-tab-pane label="登 录" name="login">
          <el-form :model="loginForm" size="large">
            <el-form-item>
              <el-input v-model="loginForm.studentNo" placeholder="学号" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" show-password
                        placeholder="密码" :prefix-icon="Lock" @keyup.enter="doLogin" />
            </el-form-item>
            <el-button type="primary" class="w100" size="large" :loading="loading" @click="doLogin">
              登 录
            </el-button>
            <div class="login-tip">管理员账号：admin / admin123</div>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注 册" name="register">
          <el-form :model="regForm" size="large">
            <el-form-item>
              <el-input v-model="regForm.studentNo" placeholder="学号（用于登录）" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.username" placeholder="昵称（可选，默认学号）" :prefix-icon="Postcard" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" show-password
                        placeholder="密码（至少6位）" :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password2" type="password" show-password
                        placeholder="确认密码" :prefix-icon="Lock" @keyup.enter="doRegister" />
            </el-form-item>
            <el-button type="primary" class="w100" size="large" :loading="loading" @click="doRegister">
              注 册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
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
.login-bg {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2b6cb0 0%, #4299e1 60%, #90cdf4 100%);
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 14px;
  padding: 36px 40px 30px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  font-size: 21px;
  font-weight: 700;
  color: #1a365d;
}

.login-sub {
  text-align: center;
  color: #90a4b8;
  font-size: 13px;
  margin: 6px 0 20px;
}

.w100 {
  width: 100%;
  margin-top: 4px;
}

.login-tip {
  margin-top: 14px;
  text-align: center;
  color: #a0aec0;
  font-size: 12px;
}
</style>
