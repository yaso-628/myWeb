import request from './request'

export const blogApi = {
  getById(id) {
    return request.get(`/blog/${id}`)
  },
  list(page = 1, size = 10, status = 1, category) {
    const params = { page, size, status }
    if (category) params.category = category
    return request.get('/blog/list', { params })
  },
  /**
   * 普通用户投稿文章，创建为待审核状态
   */
  submit(data) {
    return request.post('/blog/submit', data)
  }
}
