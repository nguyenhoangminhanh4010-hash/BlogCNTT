<template>
  <div class="qa-container container">
    <div class="section-header flex-between mb-4">
      <div>
        <h1>Hỏi đáp (Q&A)</h1>
        <p class="text-secondary">Nơi thảo luận, giải đáp thắc mắc về lập trình và công nghệ</p>
      </div>
      <button v-if="authStore.isAuthenticated" @click="showAskModal = true" class="btn btn-primary">
        + Đặt câu hỏi
      </button>
    </div>

    <div v-if="loading" class="text-center">Đang tải...</div>
    
    <div v-else class="questions-list">
      <div v-for="q in questions" :key="q.id" class="card question-card mb-3">
        <div class="q-content">
          <h3 class="q-title">
            <router-link :to="`/qa/${q.id}`">{{ q.title }}</router-link>
          </h3>
          <div class="q-meta">
            <span>Đăng bởi <strong>{{ q.user?.username }}</strong></span>
            <span>-</span>
            <span>{{ new Date(q.createdAt).toLocaleDateString() }}</span>
          </div>
        </div>
        <div class="q-stats">
          <div class="q-badge">Đã duyệt</div>
        </div>
      </div>
      
      <div v-if="questions.length === 0" class="text-center text-secondary py-5">
        Chưa có câu hỏi nào.
      </div>
    </div>

    <!-- Modal -> should extract to component but simplified here -->
    <div v-if="showAskModal" class="modal-overlay">
      <div class="modal-content card">
        <h3>Đặt câu hỏi mới</h3>
        <p class="text-secondary mb-3">Câu hỏi sẽ được Admin kiểm duyệt trước khi hiển thị công khai.</p>
        
        <div class="form-group">
          <label class="form-label">Tiêu đề</label>
          <input v-model="newQ.title" class="form-input" placeholder="Tóm tắt câu hỏi của bạn..." />
        </div>
        
        <div class="form-group">
          <label class="form-label">Nội dung chi tiết (hỗ trợ Markdown)</label>
          <MarkdownEditor v-model="newQ.content" />
        </div>
        
        <div class="modal-actions mt-4 flex-end gap-2">
          <button @click="showAskModal = false" class="btn btn-secondary">Hủy</button>
          <button @click="submitQuestion" class="btn btn-primary" :disabled="!newQ.title || !newQ.content">Gửi câu hỏi</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'
import { useAuthStore } from '../store/auth'
import MarkdownEditor from '../components/MarkdownEditor.vue'

const questions = ref([])
const loading = ref(true)
const authStore = useAuthStore()

const showAskModal = ref(false)
const newQ = ref({ title: '', content: '' })

const fetchQuestions = async () => {
  try {
    const res = await api.get('/questions')
    questions.value = res.data
  } catch (err) {
    console.error('Fetch QA error', err)
  } finally {
    loading.value = false
  }
}

const submitQuestion = async () => {
  try {
    const res = await api.post('/questions', newQ.value)
    alert(res.data.message)
    showAskModal.value = false
    newQ.value = { title: '', content: '' }
    fetchQuestions()
  } catch (err) {
    alert('Có lỗi xảy ra khi gửi câu hỏi!')
  }
}

onMounted(() => {
  fetchQuestions()
})
</script>

<style scoped>
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-end {
  display: flex;
  justify-content: flex-end;
}

.gap-2 { gap: 12px; }

.question-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
}

.q-title {
  font-size: 1.2rem;
  margin-bottom: 6px;
}

.q-meta {
  font-size: 0.85rem;
  color: var(--text-secondary);
  display: flex;
  gap: 8px;
}

.q-badge {
  background: var(--secondary-color);
  color: white;
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 0.8rem;
  font-weight: 500;
}

/* Modal simple styling */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}

.mb-3 { margin-bottom: 16px; }
.mb-4 { margin-bottom: 24px; }
.mt-4 { margin-top: 24px; }
.py-5 { padding-top: 40px; padding-bottom: 40px; }
.text-secondary { color: var(--text-secondary); }
</style>
