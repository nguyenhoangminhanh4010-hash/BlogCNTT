<template>
  <div class="post-detail-container container">
    <div v-if="loading" class="text-center">Đang tải...</div>
    
    <div v-else-if="!post" class="text-center">
      <h2>Bài viết không tồn tại</h2>
      <router-link to="/" class="btn btn-primary mt-4">Về trang chủ</router-link>
    </div>

    <div v-else class="post-detail-wrapper">
      <div class="card main-post-card">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <span>Đăng bởi <strong>{{ post.user?.username }}</strong></span>
          <span>•</span>
          <span>{{ new Date(post.createdAt).toLocaleDateString() }}</span>
          <span>•</span>
          <span class="likes-count">{{ post.likes }} lượt thích</span>
        </div>
        
        <div v-if="post.imageUrl" class="post-image-full">
          <img :src="post.imageUrl" :alt="post.title" />
        </div>

        <div class="post-content markdown-body" v-html="compiledMarkdown"></div>

        <div class="post-actions mt-4 text-center">
          <button @click="likePost" class="btn btn-secondary like-btn-lg" :disabled="!authStore.isAuthenticated">
            <svg xmlns="http://www.w3.org/2001/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" :class="{ 'fill-danger': !!post.hasLiked }">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
            </svg>
            <span>Thích bài viết</span>
          </button>
        </div>
      </div>

      <div class="comments-section mt-4">
        <h2>Bình luận ({{ comments.length }})</h2>
        
        <div v-if="authStore.isAuthenticated" class="card comment-form-card mb-4">
          <div class="form-group">
            <textarea v-model="newComment" class="form-input" rows="3" placeholder="Viết bình luận của bạn..."></textarea>
          </div>
          <button @click="submitComment" class="btn btn-primary" :disabled="!newComment.trim() || submittingComment">
            {{ submittingComment ? 'Đang gửi...' : 'Gửi bình luận' }}
          </button>
        </div>
        <div v-else class="card text-center mb-4">
          <p>Vui lòng <router-link to="/login">đăng nhập</router-link> để bình luận.</p>
        </div>

        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="card comment-item">
            <div class="comment-author">
              <strong>{{ comment.user?.username }}</strong>
              <span class="comment-date">{{ new Date(comment.createdAt).toLocaleString() }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
          <div v-if="comments.length === 0" class="text-center text-secondary">
            Chưa có bình luận nào. Hãy là người đầu tiên!
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'
import api from '../api/axios'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const authStore = useAuthStore()

const post = ref(null)
const comments = ref([])
const loading = ref(true)
const newComment = ref('')
const submittingComment = ref(false)

const compiledMarkdown = computed(() => {
  if (!post.value?.content) return ''
  return DOMPurify.sanitize(marked.parse(post.value.content))
})

const fetchPost = async () => {
  try {
    const res = await api.get(`/posts/${route.params.id}`)
    post.value = res.data
  } catch (err) {
    console.error('Error fetching post', err)
  }
}

const fetchComments = async () => {
  try {
    const res = await api.get(`/comments/post/${route.params.id}`)
    comments.value = res.data
  } catch (err) {
    console.error('Error fetching comments', err)
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  submittingComment.value = true
  try {
    const res = await api.post('/comments', {
      content: newComment.value,
      postId: post.value.id
    })
    newComment.value = ''
    fetchComments()
  } catch (err) {
    alert('Lỗi khi gửi bình luận!')
  } finally {
    submittingComment.value = false
  }
}

const likePost = async () => {
  if (!authStore.isAuthenticated) {
    alert('Vui lòng đăng nhập để like bài viết!')
    return
  }
  
  try {
    await api.post(`/posts/${post.value.id}/like`)
    post.value.likes += 1
    post.value.hasLiked = true
  } catch (err) {
    alert(err.response?.data?.message || 'Bạn đã like bài này rồi!')
  }
}

onMounted(async () => {
  await fetchPost()
  if (post.value) {
    await fetchComments()
  }
  loading.value = false
})
</script>

<style scoped>
.post-detail-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.post-title {
  font-size: 2.2rem;
  margin-bottom: 16px;
  color: var(--primary-color);
}

.post-meta {
  display: flex;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.post-image-full img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: var(--border-radius);
  margin-bottom: 24px;
}

.post-content {
  font-size: 1.05rem;
  line-height: 1.8;
  color: var(--text-primary);
}

.like-btn-lg {
  padding: 12px 24px;
  display: inline-flex;
  gap: 8px;
  align-items: center;
  font-size: 1.1rem;
}

.fill-danger {
  fill: var(--danger-color);
  color: var(--danger-color);
}

.comments-section h2 {
  font-size: 1.5rem;
  margin-bottom: 16px;
}

.comment-item {
  margin-bottom: 16px;
  padding: 16px;
}

.comment-author {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.comment-date {
  color: var(--text-secondary);
}

.mt-4 { margin-top: 24px; }
.mb-4 { margin-bottom: 24px; }
.text-center { text-align: center; }
</style>
