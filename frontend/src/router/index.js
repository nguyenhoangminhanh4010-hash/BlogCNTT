import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
  { path: '/post/:id', name: 'PostDetail', component: () => import('../views/PostDetail.vue') },
  { path: '/qa', name: 'QA', component: () => import('../views/QA.vue') },
  { path: '/qa/:id', name: 'QADetail', component: () => import('../views/QADetail.vue') },
  { path: '/documents', name: 'Documents', component: () => import('../views/Documents.vue') },
  
  // Admin Routes
  { path: '/admin/posts', name: 'AdminPosts', component: () => import('../views/admin/ManagePosts.vue'), meta: { requiresAdmin: true } },
  { path: '/admin/posts/new', name: 'AdminNewPost', component: () => import('../views/admin/PostEditor.vue'), meta: { requiresAdmin: true } },
  { path: '/admin/posts/edit/:id', name: 'AdminEditPost', component: () => import('../views/admin/PostEditor.vue'), meta: { requiresAdmin: true } },
  { path: '/admin/questions', name: 'AdminQuestions', component: () => import('../views/admin/ManageQuestions.vue'), meta: { requiresAdmin: true } },
  { path: '/admin/documents', name: 'AdminDocuments', component: () => import('../views/admin/ManageDocuments.vue'), meta: { requiresAdmin: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAdmin && (!authStore.isAuthenticated || authStore.user?.role !== 'ADMIN')) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
