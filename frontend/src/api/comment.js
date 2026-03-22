import request from './request'

export const commentApi = {
  listByBlogId(blogId, status = 1) {
    return request.get(`/comment/blog/${blogId}`, { params: { status } })
  },
  guestbook(page = 1, size = 10, status = 1) {
    return request.get('/comment/guestbook', { params: { page, size, status } })
  },
  add(data) {
    return request.post('/comment', data)
  }
}
