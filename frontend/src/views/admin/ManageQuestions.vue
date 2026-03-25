<template>
  <div class="admin-container container">
    <div class="header-actions">
      <h1>Duyệt Câu hỏi (Q&A)</h1>
    </div>

    <div class="card mt-4 table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tiêu đề</th>
            <th>Người hỏi</th>
            <th>Trạng thái</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center py-4">Đang tải...</td>
          </tr>
          <tr v-else-if="questions.length === 0">
            <td colspan="6" class="text-center py-4 text-secondary">Không có câu hỏi nào đang chờ duyệt</td>
          </tr>
          <tr v-for="q in questions" :key="q.id" v-else>
            <td>#{{ q.id }}</td>
            <td class="font-medium max-w-xs truncate" :title="q.title">{{ q.title }}</td>
            <td>{{ q.user?.username }}</td>
            <td>
              <span class="badge" :class="q.status === 'APPROVED' ? 'badge-success' : 'badge-warning'">
                {{ q.status }}
              </span>
            </td>
            <td>{{ new Date(q.createdAt).toLocaleDateString() }}</td>
            <td class="actions-cell">
              <button v-if="q.status !== 'APPROVED'" @click="approveQuestion(q.id)" class="btn btn-sm btn-primary mx-1">Duyệt</button>
              <router-link :to="`/qa/${q.id}`" target="_blank" class="btn btn-sm btn-secondary mx-1">Xem</router-link>
              <button @click="deleteQuestion(q.id)" class="btn btn-sm btn-danger mx-1">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="header-actions mt-5">
      <h2>Câu hỏi đã duyệt gần đây</h2>
    </div>
    <div class="card mt-4 table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tiêu đề</th>
            <th>Người hỏi</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loadingApproved">
            <td colspan="5" class="text-center py-4">Đang tải...</td>
          </tr>
          <tr v-for="q in approvedQuestions.slice(0, 10)" :key="q.id" v-else>
            <td>#{{ q.id }}</td>
            <td class="font-medium max-w-xs truncate">{{ q.title }}</td>
            <td>{{ q.user?.username }}</td>
            <td>{{ new Date(q.createdAt).toLocaleDateString() }}</td>
            <td class="actions-cell">
              <button @click="deleteQuestion(q.id, true)" class="btn btn-sm btn-danger mx-1">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/axios'

const questions = ref([])
const approvedQuestions = ref([])
const loading = ref(true)
const loadingApproved = ref(true)

const fetchPending = async () => {
  try {
    const res = await api.get('/questions/pending')
    questions.value = res.data
  } catch (error) {
    console.error('Lỗi fetch pending questions', error)
  } finally {
    loading.value = false
  }
}

const fetchApproved = async () => {
  try {
    const res = await api.get('/questions')
    approvedQuestions.value = res.data
  } catch (error) {
    console.error('Lỗi fetch approved questions', error)
  } finally {
    loadingApproved.value = false
  }
}

const approveQuestion = async (id) => {
  try {
    await api.put(`/questions/${id}/approve`)
    
    // Move to approved list
    const qIndex = questions.value.findIndex(q => q.id === id)
    if(qIndex > -1){
      const q = questions.value[qIndex]
      q.status = 'APPROVED'
      approvedQuestions.value.unshift(q)
      questions.value.splice(qIndex, 1)
    }
  } catch (err) {
    alert('Lỗi khi duyệt câu hỏi')
  }
}

const deleteQuestion = async (id, isApproved = false) => {
  if (!confirm('Bạn có chắc chắn muốn xóa câu hỏi này không?')) return

  try {
    await api.delete(`/questions/${id}`)
    if (isApproved) {
      approvedQuestions.value = approvedQuestions.value.filter(q => q.id !== id)
    } else {
      questions.value = questions.value.filter(q => q.id !== id)
    }
  } catch (err) {
    alert('Lỗi khi xóa câu hỏi')
  }
}

onMounted(() => {
  fetchPending()
  fetchApproved()
})
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-responsive {
  overflow-x: auto;
  padding: 0;
}

.table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.table th, .table td {
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.table th {
  background-color: #F9FAFB;
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 0.9rem;
  text-transform: uppercase;
}

.table tbody tr:hover {
  background-color: #F9FAFB;
}

.table tbody tr:last-child td {
  border-bottom: none;
}

.max-w-xs {
  max-width: 300px;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.font-medium {
  font-weight: 500;
}

.actions-cell {
  white-space: nowrap;
}

.mx-1 { margin-left: 4px; margin-right: 4px; }
.mt-4 { margin-top: 24px; }
.mt-5 { margin-top: 40px; }
.py-4 { padding-top: 16px; padding-bottom: 16px; }
.text-center { text-align: center; }
.text-secondary { color: var(--text-secondary); }
</style>
