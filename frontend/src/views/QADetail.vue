<template>
  <div class="qa-detail-container container">
    <div v-if="loading" class="text-center">Đang tải...</div>
    
    <div v-else-if="!question" class="text-center">
      <h2>Câu hỏi không tồn tại hoặc chưa được duyệt</h2>
      <router-link to="/qa" class="btn btn-primary mt-4">Về danh sách</router-link>
    </div>

    <div v-else class="qa-wrapper">
      <div class="card q-card mb-4">
        <h1 class="q-title">{{ question.title }}</h1>
        <div class="q-meta">
          <span>Hỏi bởi <strong>{{ question.user?.username }}</strong></span>
          <span>•</span>
          <span>{{ new Date(question.createdAt).toLocaleString() }}</span>
        </div>
        
        <div class="q-content markdown-body mt-4" v-html="compiledMarkdown"></div>
      </div>

      <div class="answers-section">
        <h2>Trả lời ({{ answers.length }})</h2>
        
        <div v-if="authStore.isAuthenticated" class="card answer-form-card mb-4">
          <div class="form-group">
            <label class="form-label">Thêm câu trả lời của bạn</label>
            <textarea v-model="newAnswer" class="form-input" rows="4" placeholder="Nhập câu trả lời..."></textarea>
          </div>
          <button @click="submitAnswer" class="btn btn-primary" :disabled="!newAnswer.trim() || submittingAnswer">
            {{ submittingAnswer ? 'Đang gửi...' : 'Gửi câu trả lời' }}
          </button>
        </div>
        <div v-else class="card text-center mb-4">
          <p>Vui lòng <router-link to="/login">đăng nhập</router-link> để trả lời.</p>
        </div>

        <div class="answers-list">
          <div v-for="ans in answers" :key="ans.id" class="card answer-item mb-3">
            <div class="ans-author">
              <strong>{{ ans.user?.username }}</strong>
              <span v-if="ans.user?.role === 'ADMIN'" class="badge badge-warning ml-2">Admin</span>
              <span class="ans-date ml-auto">{{ new Date(ans.createdAt).toLocaleString() }}</span>
            </div>
            <div class="ans-content mt-2">{{ ans.content }}</div>
          </div>
          
          <div v-if="answers.length === 0" class="text-center text-secondary py-4">
            Chưa có câu trả lời nào. Hãy giúp đỡ bạn học của mình!
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

const question = ref(null)
const answers = ref([])
const loading = ref(true)
const newAnswer = ref('')
const submittingAnswer = ref(false)

const compiledMarkdown = computed(() => {
  if (!question.value?.content) return ''
  return DOMPurify.sanitize(marked.parse(question.value.content))
})

const fetchQuestion = async () => {
  try {
    const res = await api.get(`/questions/${route.params.id}`)
    question.value = res.data
  } catch (err) {
    console.error('Lỗi khi tải câu hỏi', err)
  }
}

const fetchAnswers = async () => {
  try {
    const res = await api.get(`/answers/question/${route.params.id}`)
    answers.value = res.data
  } catch (err) {
    console.error('Lỗi khi tải câu trả lời', err)
  }
}

const submitAnswer = async () => {
  if (!newAnswer.value.trim()) return
  
  submittingAnswer.value = true
  try {
    await api.post('/answers', {
      content: newAnswer.value,
      questionId: question.value.id
    })
    newAnswer.value = ''
    fetchAnswers()
  } catch (err) {
    alert('Lỗi khi gửi câu trả lời!')
  } finally {
    submittingAnswer.value = false
  }
}

onMounted(async () => {
  await fetchQuestion()
  if (question.value) {
    await fetchAnswers()
  }
  loading.value = false
})
</script>

<style scoped>
.qa-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.q-title {
  font-size: 1.8rem;
  color: var(--primary-color);
  margin-bottom: 12px;
}

.q-meta {
  display: flex;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 0.9rem;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color);
}

.q-content {
  font-size: 1.05rem;
  line-height: 1.6;
}

.answers-section h2 {
  font-size: 1.4rem;
  margin-bottom: 16px;
}

.answer-item {
  padding: 20px;
}

.ans-author {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.95rem;
}

.ans-date {
  color: var(--text-secondary);
  font-size: 0.85rem;
}

.ans-content {
  line-height: 1.6;
  white-space: pre-wrap;
}

.ml-2 { margin-left: 8px; }
.ml-auto { margin-left: auto; }
.mt-2 { margin-top: 8px; }
.mt-4 { margin-top: 24px; }
.mb-3 { margin-bottom: 16px; }
.mb-4 { margin-bottom: 24px; }
.text-center { text-align: center; }
.text-secondary { color: var(--text-secondary); }
.py-4 { padding-top: 32px; padding-bottom: 32px; }
</style>
