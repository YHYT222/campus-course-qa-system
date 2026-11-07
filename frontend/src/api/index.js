import request from './request'

// ==================== 认证 ====================
export const register = data => request.post('/auth/register', data)
export const login = data => request.post('/auth/login', data)
export const getMe = () => request.get('/auth/me')
export const updateProfile = data => request.put('/auth/profile', data)

// ==================== 课程 ====================
export const getCourseList = () => request.get('/course/list')

// ==================== 帖子 ====================
export const getPostPage = params => request.get('/post/page', { params })
export const getPostDetail = id => request.get(`/post/${id}`)
export const publishPost = data => request.post('/post', data)
export const getMyPosts = () => request.get('/post/my')
export const deletePost = id => request.delete(`/post/${id}`)

// ==================== 互动 ====================
export const toggleLike = postId => request.post(`/interact/like/${postId}`)
export const toggleFavorite = postId => request.post(`/interact/favorite/${postId}`)
export const getMyFavorites = () => request.get('/interact/favorites')
export const addReply = data => request.post('/interact/reply', data)
export const getReplies = postId => request.get(`/interact/replies/${postId}`)
export const getMyReplies = () => request.get('/interact/my-replies')

// ==================== 管理端 ====================
export const addCourse = data => request.post('/admin/course', data)
export const updateCourse = data => request.put('/admin/course', data)
export const deleteCourse = id => request.delete(`/admin/course/${id}`)

export const getAdminPostPage = params => request.get('/admin/post/page', { params })
export const auditPost = (id, status) => request.put(`/admin/post/${id}/status/${status}`)
export const adminDeletePost = id => request.delete(`/admin/post/${id}`)

export const getUserPage = params => request.get('/admin/user/page', { params })
export const changeUserStatus = (id, status) => request.put(`/admin/user/${id}/status/${status}`)
export const resetUserPassword = id => request.put(`/admin/user/${id}/reset-password`)

// ==================== 统计 ====================
export const getStatsByCourse = () => request.get('/stats/course')
export const getStatsTrend = () => request.get('/stats/trend')
export const getStatsTopLikes = () => request.get('/stats/top-likes')
export const getStatsActiveUsers = () => request.get('/stats/active-users')
