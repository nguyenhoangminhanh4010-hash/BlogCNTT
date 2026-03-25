<template>
  <div class="admin-container container">
    <div class="header-actions">
      <h1>Quản lý Tài liệu</h1>
      <button @click="showAddModal = true" class="btn btn-primary">+ Thêm tài liệu mới</button>
    </div>

    <div class="card mt-4 table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên tài liệu</th>
            <th>Liên kết</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="5" class="text-center py-4">Đang tải...</td>
          </tr>
          <tr v-else-if="documents.length === 0">
            <td colspan="5" class="text-center py-4 text-secondary">Chưa có tài liệu nào</td>
          </tr>
          <tr v-for="doc in documents" :key="doc.id" v-else>
            <td>#{{ doc.id }}</td>
            <td class="font-medium max-w-xs truncate" :title="doc.title">{{ doc.title }}</td>
            <td class="max-w-xs truncate"><a :href="doc.link" target="_blank">{{ doc.link }}</a></td>
            <td>{{ new Date(doc.createdAt).toLocaleDateString() }}</td>
            <td class="actions-cell">
              <button @click="deleteDocument(doc.id)" class="btn btn-sm btn-danger mx-1">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add Document Modal -->
    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-content card">
        <h3>Thêm tài liệu mới</h3>
        
        <div class="form-group mt-4">
          <label class="form-label">Tên tài liệu <span class="text-danger">*</span></label>
          <input v-model="newDoc.title" class="form-input" placeholder="Ví dụ: Giáo trình C++ Căn bản" />
        </div>
        
        <div class="form-group">
          <label class="form-label">Liên kết tải xuống (URL) <span class="text-danger">*</span></label>
          <input v-model="newDoc.link" class="form-input" placeholder="https://drive.google.com/..." />
        </div>
        
        <div class="modal-actions mt-4 flex-end gap-2">
          <button @click="showAddModal = false" class="btn btn-secondary">Hủy</button>
          <button @click="submitDocument" class="btn btn-primary" :disabled="!newDoc.title || !newDoc.link || saving">
            {{ saving ? 'Đang lưu...' : 'Lưu tài liệu' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/axios'

const documents = ref([])
const loading = ref(true)
const showAddModal = ref(false)
const saving = ref(false)

const newDoc = ref({ title: '', link: '' })

const fetchDocuments = async () => {
  try {
    const res = await api.get('/documents')
    documents.value = res.data
  } catch (error) {
    console.error('Lỗi fetch documents', error)
  } finally {
    loading.value = false
  }
}

const submitDocument = async () => {
  saving.value = true
  try {
    const res = await api.post('/documents', newDoc.value)
    alert(res.data.message)
    showAddModal.value = false
    newDoc.value = { title: '', link: '' }
    fetchDocuments()
  } catch (err) {
    alert('Lỗi khi thêm tài liệu!')
  } finally {
    saving.value = false
  }
}

const deleteDocument = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa tài liệu này không?')) return

  try {
    await api.delete(`/documents/${id}`)
    documents.value = documents.value.filter(doc => doc.id !== id)
  } catch (err) {
    alert('Lỗi khi xóa tài liệu')
  }
}

onMounted(() => {
  fetchDocuments()
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
  max-width: 250px;
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

/* Modal styling */
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
  max-width: 500px;
}

.flex-end {
  display: flex;
  justify-content: flex-end;
}

.gap-2 { gap: 12px; }
.mx-1 { margin-left: 4px; margin-right: 4px; }
.mt-4 { margin-top: 24px; }
.py-4 { padding-top: 16px; padding-bottom: 16px; }
.text-center { text-align: center; }
.text-secondary { color: var(--text-secondary); }
.text-danger { color: var(--danger-color); }
</style>
