import request from './request'

export const linkApi = {
  list(status = 1) {
    return request.get('/link/list', { params: { status } })
  },
  getById(id) {
    return request.get(`/link/${id}`)
  },
  /**
   * 普通用户申请友链，创建为待审核状态
   */
  apply(data) {
    return request.post('/link/apply', data)
  }
}
