import request from './request'

export const profileApi = {
  getByKey(key) {
    return request.get(`/profile/${key}`)
  },
  listAll() {
    return request.get('/profile/all')
  },
  /** 获取个人信息（头像、简介、联系方式） */
  getInfo() {
    return request.get('/profile/info')
  },
  /** 更新个人信息（需登录） */
  updateInfo(data) {
    return request.put('/profile/info', data)
  }
}
