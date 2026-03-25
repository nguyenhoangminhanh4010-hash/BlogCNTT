<template>
  <div class="auth-wrapper container">
    <div class="auth-card card">
      <h2 class="auth-title">Chào mừng trở lại</h2>
      <p class="auth-subtitle">Đăng nhập vào BlogCNTT để tiếp tục</p>

      <div class="form-group">
        <label class="form-label">Tài khoản</label>
        <input type="text" v-model="username" class="form-input" placeholder="Nhập tên đăng nhập" @keyup.enter="handleLogin">
      </div>

      <div class="form-group">
        <label class="form-label">Mật khẩu</label>
        <input type="password" v-model="password" class="form-input" placeholder="Nhập mật khẩu" @keyup.enter="handleLogin">
      </div>

      <div v-if="errorMsg" class="error-msg">
        {{ errorMsg }}
      </div>

      <button @click="handleLogin" class="btn btn-primary btn-block" :disabled="loading">
        {{ loading ? 'Đang xử lý...' : 'Đăng nhập' }}
      </button>

      <div class="auth-footer">
        Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
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
const loading = ref(false)

const handleLogin = async () => {
  if(!username.value || !password.value) {
    errorMsg.value = 'Vui lòng nhập đầy đủ thông tin'
    return
  }

  loading.value = true
  errorMsg.value = ''
  
  try {
    await authStore.login(username.value, password.value)
    router.push('/')
  } catch (err) {
    errorMsg.value = 'Sai tài khoản hoặc mật khẩu!'
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

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-secondary);
}
</style>
