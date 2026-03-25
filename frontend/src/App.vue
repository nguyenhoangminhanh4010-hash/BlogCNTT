<template>
  <div class="app-layout">
    <!-- Navbar -->
    <nav class="navbar glass">
      <div class="container nav-container">
        <router-link to="/" class="nav-brand">BlogCNTT</router-link>
        
        <div class="nav-links">
          <router-link to="/" class="nav-link">Trang chủ</router-link>
          <router-link to="/qa" class="nav-link">Hỏi đáp</router-link>
          <router-link to="/documents" class="nav-link">Tài liệu</router-link>

          <!-- Admin Menu -->
          <div class="dropdown" v-if="authStore.isAdmin">
            <span class="nav-link dropdown-toggle">Quản lý ▾</span>
            <div class="dropdown-menu">
              <router-link to="/admin/posts" class="dropdown-item">Quản lý bài viết</router-link>
              <router-link to="/admin/questions" class="dropdown-item">Quản lý câu hỏi</router-link>
              <router-link to="/admin/documents" class="dropdown-item">Quản lý tài liệu</router-link>
            </div>
          </div>
        </div>

        <div class="nav-auth">
          <template v-if="authStore.isAuthenticated">
            <span class="welcome-text">Hi, {{ authStore.user?.username }}</span>
            <button @click="logout" class="btn btn-secondary btn-sm">Đăng xuất</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-secondary">Đăng nhập</router-link>
            <router-link to="/register" class="btn btn-primary" style="margin-left: 10px;">Đăng ký</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  position: sticky;
  top: 0;
  z-index: 50;
  padding: 15px 0;
  box-shadow: var(--shadow-sm);
}

.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  gap: 24px;
  align-items: center;
}

.nav-link {
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 0.95rem;
  transition: var(--transition);
  cursor: pointer;
}

.nav-link:hover, .nav-link.router-link-active {
  color: var(--primary-color);
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  background-color: var(--surface-color);
  min-width: 180px;
  box-shadow: var(--shadow-lg);
  border-radius: 8px;
  padding: 8px 0;
  z-index: 100;
  border: 1px solid var(--border-color);
}

.dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-item {
  color: var(--text-secondary);
  padding: 10px 16px;
  text-decoration: none;
  display: block;
  font-size: 0.9rem;
  transition: var(--transition);
}

.dropdown-item:hover {
  background-color: #F3F4F6;
  color: var(--primary-color);
}

.nav-auth {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-text {
  font-weight: 500;
  color: var(--text-secondary);
}

.btn-sm {
  padding: 6px 12px;
  font-size: 0.85rem;
}

.main-content {
  flex: 1;
  padding: 40px 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
