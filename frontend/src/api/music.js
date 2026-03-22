import request from './request'

export const musicApi = {
  /** 获取音乐列表（公开） */
  list() {
    return request.get('/music')
  }
}
