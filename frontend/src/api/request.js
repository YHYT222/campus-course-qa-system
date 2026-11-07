import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token
  }
  return config
})

request.interceptors.response.use(
  res => {
    const { code, msg } = res.data
    if (code === 200) {
      return res.data
    }
    if (code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.error(msg || '请先登录')
      router.push('/login')
    } else {
      ElMessage.error(msg || '请求失败')
    }
    return Promise.reject(new Error(msg || '请求失败'))
  },
  err => {
    ElMessage.error('网络异常，请检查后端服务是否已启动')
    return Promise.reject(err)
  }
)

export default request
