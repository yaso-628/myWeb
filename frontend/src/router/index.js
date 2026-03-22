import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
  { path: '/blog', name: 'BlogList', component: () => import('@/views/BlogList.vue') },
  { path: '/blog/:id', name: 'BlogDetail', component: () => import('@/views/BlogDetail.vue') },
  {
    path: '/blog/submit',
    name: 'SubmitBlog',
    component: () => import('@/views/SubmitBlog.vue'),
    meta: { requiresAuth: true }
  },
  { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
  { path: '/comment', name: 'CommentBoard', component: () => import('@/views/CommentBoard.vue') },
  { path: '/link', name: 'Link', component: () => import('@/views/Link.vue') },
  { path: '/profile', name: 'Profile', component: () => import('@/views/Profile.vue') },
  {
    path: '/me',
    name: 'UserProfile',
    component: () => import('@/views/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/profile',
    name: 'AdminProfile',
    component: () => import('@/views/AdminProfile.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/blogs',
    name: 'AdminBlogs',
    component: () => import('@/views/AdminBlogs.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/comments',
    name: 'AdminComments',
    component: () => import('@/views/AdminComments.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/links',
    name: 'AdminLinks',
    component: () => import('@/views/AdminLinks.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/blogs/new',
    name: 'AdminBlogNew',
    component: () => import('@/views/AdminBlogEdit.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/blogs/edit/:id',
    name: 'AdminBlogEdit',
    component: () => import('@/views/AdminBlogEdit.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.meta.requiresAuth && !store.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !store.isAdmin) {
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
