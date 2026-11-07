import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/StudentLayout.vue'),
    children: [
      { path: '', component: () => import('../views/Home.vue') },
      { path: 'post/:id', component: () => import('../views/PostDetail.vue') },
      { path: 'publish', component: () => import('../views/Publish.vue') },
      { path: 'profile', component: () => import('../views/Profile.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    children: [
      { path: '', redirect: '/admin/course' },
      { path: 'course', component: () => import('../views/admin/CourseManage.vue') },
      { path: 'audit', component: () => import('../views/admin/PostAudit.vue') },
      { path: 'users', component: () => import('../views/admin/UserManage.vue') },
      { path: 'stats', component: () => import('../views/admin/Stats.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(to => {
  const store = useUserStore()
  if (to.path !== '/login' && !store.isLogin) {
    return '/login'
  }
  // 管理端路由仅管理员可进入
  if (to.path.startsWith('/admin') && !store.isAdmin) {
    return '/'
  }
  return true
})

export default router
