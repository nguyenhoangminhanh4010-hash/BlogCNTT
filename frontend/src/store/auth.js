import { defineStore } from 'pinia'
import api from '../api/axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },
  actions: {
    async login(username, password) {
      try {
        const response = await api.post('/auth/signin', { username, password })
        this.token = response.data.token
        this.user = { id: response.data.id, username: response.data.username, role: response.data.role }
        
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
        return true
      } catch (error) {
        throw error
      }
    },
    async register(username, password) {
      try {
        await api.post('/auth/signup', { username, password })
        return true
      } catch (error) {
        throw error
      }
    },
    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
