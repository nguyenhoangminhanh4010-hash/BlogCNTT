<template>
  <div class="admin-container container">
    <div class="header-actions">
      <h1>{{ isEdit ? 'Chỉnh sửa bài viết' : 'Viết bài mới' }}</h1>
      <button @click="savePost" class="btn btn-primary" :disabled="saving">
        {{ saving ? 'Đang lưu...' : 'Lưu bài viết' }}
      </button>
    </div>

    <div class="card mt-4 editor-card">
      <div class="form-group">
        <label class="form-label">Tiêu đề bài viết <span class="text-danger">*</span></label>
        <input type="text" v-model="form.title" class="form-input" placeholder="Nhập tiêu đề...">
      </div>
      
      <div class="form-group">
        <label class="form-label">Đường dẫn ảnh Cover (URL)</label>
        <input type="text" v-model="form.imageUrl" class="form-input" placeholder="https://example.com/image.jpg">
      </div>

      <div class="form-group">
        <label class="form-label">Nội dung <span class="text-danger">*</span></label>
        <MarkdownEditor v-model="form.content" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/axios'
import MarkdownEditor from '@/components/MarkdownEditor.vue'

const route = useRoute()
const router = useRouter()

const form = ref({
  title: '',
  content: '',
  imageUrl: ''
})

const saving = ref(false)
const isEdit = computed(() => !!route.params.id)

const loadPost = async (id) => {
  try {
    const res = await api.get(`/posts/${id}`)
    form.value = {
      title: res.data.title,
      content: res.data.content,
      imageUrl: res.data.imageUrl || ''
    }
  } catch (err) {
    alert('Không tìm thấy bài viết!')
    router.push('/admin/posts')
  }
}

const savePost = async () => {
  if (!form.value.title || !form.value.content) {
    alert('Vui lòng nhập tiêu đề và nội dung!')
    return
  }
  
  saving.value = true
  try {
    if (isEdit.value) {
      await api.put(`/posts/${route.params.id}`, form.value)
    } else {
      await api.post('/posts', form.value)
    }
    router.push('/admin/posts')
  } catch (err) {
    alert('Lỗi khi lưu bài viết!')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  if (isEdit.value) {
    loadPost(route.params.id)
  }
})
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-card {
  max-width: 1000px;
  margin: 0 auto;
}

.text-danger {
  color: var(--danger-color);
}

.mt-4 { margin-top: 24px; }
</style>
