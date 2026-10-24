import axios from 'axios';

const rawApiBase = import.meta.env.VITE_API_URL || 'http://localhost:8080';
const API_BASE = rawApiBase.replace(/\/+$/, '');

const api = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('rw_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Handle errors gracefully
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('rw_token');
      localStorage.removeItem('rw_user');
      window.location.href = '/login';
    }
    if (!error.response) {
      error.message = `Backend server unreachable (${API_BASE}). Check your VITE_API_URL and server status.`;
    }
    return Promise.reject(error);
  }
);

export default api;

// ── Auth ──────────────────────────────────────────────────────────────
export const authApi = {
  login: (data) => api.post('/api/auth/login', data),
  register: (data) => api.post('/api/auth/register', data),
};

// ── Inventory ─────────────────────────────────────────────────────────
export const inventoryApi = {
  getAll: () => api.get('/api/inventory'),
  getLowStock: () => api.get('/api/inventory/low-stock'),
  create: (data) => api.post('/api/inventory', data),
  update: (id, data) => api.put(`/api/inventory/${id}`, data),
  delete: (id) => api.delete(`/api/inventory/${id}`),
};

// ── Suppliers ─────────────────────────────────────────────────────────
export const supplierApi = {
  getAll: () => api.get('/api/suppliers'),
  create: (data) => api.post('/api/suppliers', data),
  update: (id, data) => api.put(`/api/suppliers/${id}`, data),
  delete: (id) => api.delete(`/api/suppliers/${id}`),
};

// ── Customers ─────────────────────────────────────────────────────────
export const customerApi = {
  getAll: () => api.get('/api/customers'),
  search: (q) => api.get(`/api/customers/search?q=${q}`),
  create: (data) => api.post('/api/customers', data),
  update: (id, data) => api.put(`/api/customers/${id}`, data),
  delete: (id) => api.delete(`/api/customers/${id}`),
};

// ── Menu ──────────────────────────────────────────────────────────────
export const menuApi = {
  getItems: () => api.get('/api/menu/items'),
  getAvailable: () => api.get('/api/menu/items/available'),
  getCategories: () => api.get('/api/menu/categories'),
  createItem: (data) => api.post('/api/menu/items', data),
  updateItem: (id, data) => api.put(`/api/menu/items/${id}`, data),
  deleteItem: (id) => api.delete(`/api/menu/items/${id}`),
  createCategory: (data) => api.post('/api/menu/categories', data),
};

// ── Orders ────────────────────────────────────────────────────────────
export const orderApi = {
  getAll: () => api.get('/api/orders'),
  getById: (id) => api.get(`/api/orders/${id}`),
  create: (data) => api.post('/api/orders', data),
  updateStatus: (id, status) => api.patch(`/api/orders/${id}/status`, { status }),
  getDashboardStats: () => api.get('/api/orders/dashboard/stats'),
};

// ── Tables ────────────────────────────────────────────────────────────
export const tableApi = {
  getAll: () => api.get('/api/tables'),
  getAvailable: () => api.get('/api/tables/available'),
  create: (data) => api.post('/api/tables', data),
  update: (id, data) => api.put(`/api/tables/${id}`, data),
  updateStatus: (id, status) => api.patch(`/api/tables/${id}/status`, { status }),
};
