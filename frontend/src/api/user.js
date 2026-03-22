import request from './request'

export const userApi = {
  /**
   * 获取当前登录用户的信息
   */
  me() {
    return request.get('/user/me')
  },

  /**
   * 更新当前登录用户的个人资料（昵称、邮箱、头像）
   */
  updateProfile(data) {
    return request.put('/user/profile', data)
  },

  /**
   * 上传头像图片文件，返回 URL
   */
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/user/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

