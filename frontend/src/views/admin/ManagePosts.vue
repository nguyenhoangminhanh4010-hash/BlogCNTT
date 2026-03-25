<template>
  <div class="admin-container container">
    <div class="header-actions">
      <h1>Quản lý Bài viết</h1>
      <router-link to="/admin/posts/new" class="btn btn-primary">+ Viết bài mới</router-link>
    </div>

    <div class="card mt-4 table-responsive">
      <table class="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tiêu đề</th>
            <th>Tác giả</th>
            <th>Lượt thích</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="text-center py-4">Đang tải...</td>
          </tr>
          <tr v-else-if="posts.length === 0">
            <td colspan="6" class="text-center py-4 text-secondary">Chưa có bài viết nào</td>
          </tr>
          <tr v-for="post in posts" :key="post.id" v-else>
            <td>#{{ post.id }}</td>
            <td class="font-medium max-w-xs truncate">{{ post.title }}</td>
            <td>{{ post.user?.username }}</td>
            <td>{{ post.likes }}</td>
            <td>{{ new Date(post.createdAt).toLocaleDateString() }}</td>
            <td class="actions-cell">
              <router-link :to="`/post/${post.id}`" target="_blank" class="btn btn-sm btn-secondary mx-1">Xem</router-link>
              <router-link :to="`/admin/posts/edit/${post.id}`" class="btn btn-sm btn-secondary mx-1">Sửa</router-link>
              <button @click="deletePost(post.id)" class="btn btn-sm btn-danger mx-1">Xóa</button>
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

const posts = ref([])
const loading = ref(true)

const fetchPosts = async () => {
  try {
    const res = await api.get('/posts')
    posts.value = res.data
  } catch (error) {
    console.error('Error fetching posts', error)
  } finally {
    loading.value = false
  }
}

const deletePost = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn xóa bài viết này không?')) return

  try {
    await api.delete(`/posts/${id}`)
    posts.value = posts.value.filter(p => p.id !== id)
  } catch (err) {
    alert('Lỗi khi xóa bài viết')
  }
}

onMounted(() => {
  fetchPosts()
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
.py-4 { padding-top: 16px; padding-bottom: 16px; }
.text-center { text-align: center; }
.text-secondary { color: var(--text-secondary); }
</style>
