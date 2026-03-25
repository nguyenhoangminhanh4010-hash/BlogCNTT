<template>
  <div class="auth-wrapper container">
    <div class="auth-card card">
      <h2 class="auth-title">Đăng ký tài khoản</h2>
      <p class="auth-subtitle">Tham gia cộng đồng BlogCNTT ngay hôm nay</p>

      <div class="form-group">
        <label class="form-label">Tài khoản</label>
        <input type="text" v-model="username" class="form-input" placeholder="Nhập tên đăng nhập" @keyup.enter="handleRegister">
      </div>

      <div class="form-group">
        <label class="form-label">Mật khẩu</label>
        <input type="password" v-model="password" class="form-input" placeholder="Nhập mật khẩu" @keyup.enter="handleRegister">
      </div>

      <div v-if="errorMsg" class="error-msg">
        {{ errorMsg }}
      </div>
      
      <div v-if="successMsg" class="success-msg">
        {{ successMsg }}
      </div>

      <button @click="handleRegister" class="btn btn-primary btn-block" :disabled="loading">
        {{ loading ? 'Đang xử lý...' : 'Đăng ký' }}
      </button>

      <div class="auth-footer">
        Đã có tài khoản? <router-link to="/login">Đăng nhập</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const errorMsg = ref('')
const successMsg = ref('')
const loading = ref(false)

const handleRegister = async () => {
  if(!username.value || !password.value) {
    errorMsg.value = 'Vui lòng nhập đầy đủ thông tin'
    return
  }

  loading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  
  try {
    await authStore.register(username.value, password.value)
    successMsg.value = 'Đăng ký thành công! Đang chuyển hướng...'
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (err) {
    errorMsg.value = err.response?.data?.message || 'Có lỗi xảy ra, tên tài khoản có thể đã tồn tại!'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 40px 32px;
}

.auth-title {
  text-align: center;
  font-size: 1.5rem;
  margin-bottom: 8px;
}

.auth-subtitle {
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.95rem;
  margin-bottom: 32px;
}

.btn-block {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
}

.error-msg {
  color: var(--danger-color);
  font-size: 0.9rem;
  margin-bottom: 16px;
  text-align: center;
}

.success-msg {
  color: var(--secondary-color);
  font-size: 0.9rem;
  margin-bottom: 16px;
  text-align: center;
  font-weight: 500;
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
}
</style>
