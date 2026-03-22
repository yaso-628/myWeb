import request from './request'

export const adminApi = {
  stats() {
    return request.get('/admin/stats')
  },
  getBlog(id) {
    return request.get(`/admin/blogs/${id}`)
  },
  listBlogs(page = 1, size = 10, status) {
    return request.get('/admin/blogs', { params: { page, size, status } })
  },
  createBlog(data) {
    return request.post('/admin/blogs', data)
  },
  updateBlog(id, data) {
    return request.put(`/admin/blogs/${id}`, data)
  },
  deleteBlog(id) {
    return request.delete(`/admin/blogs/${id}`)
  },
  listComments(page = 1, size = 10, blogId, guestbookOnly, status) {
    return request.get('/admin/comments', { params: { page, size, blogId, guestbookOnly, status } })
  },
  updateCommentStatus(id, status) {
    return request.put(`/admin/comments/${id}/status`, null, { params: { status } })
  },
  deleteComment(id) {
    return request.delete(`/admin/comments/${id}`)
  },
  listLinks(page = 1, size = 10, status) {
    return request.get('/admin/links', { params: { page, size, status } })
  },
  createLink(data) {
    return request.post('/admin/links', data)
  },
  updateLink(id, data) {
    return request.put(`/admin/links/${id}`, data)
  },
  updateLinkStatus(id, status) {
    return request.put(`/admin/links/${id}/status`, null, { params: { status } })
  },
  deleteLink(id) {
    return request.delete(`/admin/links/${id}`)
  },
  listProfile() {
    return request.get('/admin/profile')
  },
  saveProfile(data) {
    return request.post('/admin/profile', data)
  },
  /** 保存单条配置（key-value） */
  saveProfileKey(keyName, value) {
    return request.post('/admin/profile', { keyName, value, type: 'text' })
  },
  /** 上传本地背景图，返回可直接使用的 URL，并自动更新 bg_image */
  uploadBgImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/upload/bg-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  /** 上传本地 BGM 音乐文件，返回可直接使用的 URL，并自动更新 bgm_url */
  uploadBgm(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/upload/bgm', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  listUsers(page = 1, size = 10) {
    return request.get('/admin/users', { params: { page, size } })
  },
  listMusic() {
    return request.get('/admin/music')
  },
  addMusic(data) {
    return request.post('/admin/music', data)
  },
  updateMusic(id, data) {
    return request.put(`/admin/music/${id}`, data)
  },
  deleteMusic(id) {
    return request.delete(`/admin/music/${id}`)
  },
  uploadMusic(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/admin/music/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
