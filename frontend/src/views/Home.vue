<template>
  <div class="home-container container">
    <div class="hero-section text-center">
      <h1 class="hero-title">Cộng đồng sinh viên CNTT</h1>
      <p class="hero-subtitle">Nơi chia sẻ kiến thức, trao đổi học tập và phát triển kỹ năng</p>
    </div>

    <div class="section-header">
      <h2>Bài viết mới nhất</h2>
    </div>

    <div v-if="loading" class="text-center">Đang tải...</div>
    
    <div v-else class="grid grid-cols-2">
      <div v-for="post in posts" :key="post.id" class="card post-card">
        <div v-if="post.imageUrl" class="post-image" :style="{ backgroundImage: `url(${post.imageUrl})` }"></div>
        <div class="post-content">
          <h3 class="post-title">
            <router-link :to="`/post/${post.id}`">{{ post.title }}</router-link>
          </h3>
          <div class="post-meta">
            <span>Đăng bởi <strong>{{ post.user?.username }}</strong></span> • 
            <span>{{ new Date(post.createdAt).toLocaleDateString() }}</span>
          </div>
          <p class="post-excerpt">{{ getExcerpt(post.content) }}</p>
          
          <div class="post-footer">
            <button @click="likePost(post.id)" class="like-btn" :class="{ 'liked': post.hasLiked }" :disabled="!authStore.isAuthenticated">
              <svg xmlns="http://www.w3.org/2001/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
              </svg>
              <span>{{ post.likes }}</span>
            </button>
            <router-link :to="`/post/${post.id}`" class="read-more">Đọc tiếp →</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'
import { useAuthStore } from '../store/auth'

const posts = ref([])
const loading = ref(true)
const authStore = useAuthStore()

const fetchPosts = async () => {
  try {
    const res = await api.get('/posts')
    posts.value = res.data
  } catch (error) {
    console.error('Error fetching posts:', error)
  } finally {
    loading.value = false
  }
}

const likePost = async (id) => {
  if (!authStore.isAuthenticated) {
    alert('Vui lòng đăng nhập để like bài viết!')
    return
  }
  
  try {
    await api.post(`/posts/${id}/like`)
    const post = posts.value.find(p => p.id === id)
    if (post) {
      post.likes += 1
      post.hasLiked = true
    }
  } catch (error) {
    alert(error.response?.data?.message || 'Bạn đã like bài này rồi!')
  }
}

const getExcerpt = (content) => {
  if (!content) return ''
  const plainText = content.replace(/[#*`_\[\]]/g, '')
  return plainText.length > 120 ? plainText.substring(0, 120) + '...' : plainText
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.hero-section {
  padding: 60px 0;
  margin-bottom: 40px;
  background: linear-gradient(to right, rgba(79, 70, 229, 0.05), rgba(16, 185, 129, 0.05));
  border-radius: var(--border-radius);
}

.text-center {
  text-align: center;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: var(--text-secondary);
}

.section-header {
  margin-bottom: 24px;
  border-bottom: 2px solid var(--border-color);
  padding-bottom: 12px;
}

.post-card {
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.post-image {
  height: 200px;
  background-size: cover;
  background-position: center;
}

.post-content {
  padding: 24px;
  display: flex;
  flex-direction: column;
  flex: 1;
}

.post-title {
  font-size: 1.25rem;
  margin-bottom: 8px;
}

.post-title a {
  color: var(--text-primary);
}

.post-title a:hover {
  color: var(--primary-color);
}

.post-meta {
  font-size: 0.85rem;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.post-excerpt {
  color: var(--text-secondary);
  flex: 1;
  margin-bottom: 20px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--border-color);
  padding-top: 16px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-weight: 600;
  transition: var(--transition);
}

.like-btn:hover:not(:disabled) {
  color: var(--danger-color);
}

.like-btn.liked svg {
  fill: var(--danger-color);
  color: var(--danger-color);
}

.like-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.read-more {
  font-weight: 600;
  font-size: 0.9rem;
}
</style>
