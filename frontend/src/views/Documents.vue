<template>
  <div class="documents-container container">
    <div class="section-header mb-4">
      <h1>Tài liệu học tập</h1>
      <p class="text-secondary">Tổng hợp các giáo trình, slide bài giảng và tài liệu tham khảo chuyên ngành CNTT</p>
    </div>

    <div v-if="loading" class="text-center">Đang tải...</div>
    
    <div v-else class="documents-grid grid grid-cols-3">
      <div v-for="doc in documents" :key="doc.id" class="card doc-card">
        <div class="doc-icon">
          <svg xmlns="http://www.w3.org/2001/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" class="text-primary">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <h3 class="doc-title">{{ doc.title }}</h3>
        <div class="doc-date mb-3">{{ new Date(doc.createdAt).toLocaleDateString() }}</div>
        
        <a :href="doc.link" target="_blank" v-if="authStore.isAuthenticated" class="btn btn-secondary btn-block text-center">Tải tài liệu</a>
        <button v-else @click="notifyLogin" class="btn btn-secondary btn-block object-disabled text-center">Đăng nhập để tải</button>
      </div>
      
      <div v-if="documents.length === 0" class="col-span-full text-center py-5 text-secondary">
        Hiện tại chưa có tài liệu nào.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const documents = ref([])
const loading = ref(true)
const authStore = useAuthStore()
const router = useRouter()

const fetchDocuments = async () => {
  try {
    const res = await api.get('/documents')
    documents.value = res.data
  } catch (error) {
    console.error('Error fetching documents', error)
  } finally {
    loading.value = false
  }
}

const notifyLogin = () => {
  alert('Bạn cần đăng nhập để tải tài liệu!')
  router.push('/login')
}

onMounted(() => {
  fetchDocuments()
})
</script>

<style scoped>
.doc-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 32px 24px;
}

.doc-icon {
  margin-bottom: 16px;
  background: rgba(79, 70, 229, 0.1);
  padding: 16px;
  border-radius: 50%;
  color: var(--primary-color);
}

.doc-title {
  font-size: 1.1rem;
  margin-bottom: 8px;
  word-break: break-word;
}

.doc-date {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.btn-block {
  width: 100%;
  margin-top: auto;
}

.object-disabled {
  opacity: 0.7;
}

.mb-3 { margin-bottom: 16px; }
.mb-4 { margin-bottom: 24px; }
.py-5 { padding-top: 40px; padding-bottom: 40px; }
.text-center { text-align: center; }
.text-secondary { color: var(--text-secondary); }
.col-span-full { grid-column: 1 / -1; }
</style>
