import axios from 'axios';

// Always point to the real Render backend in production.
// VITE_API_URL must be set in Vercel Dashboard → Settings → Environment Variables.
// The fallback hardcodes the Render URL so the app never breaks even without the env var.
const PROD_API = 'https://rosewood-api.onrender.com';
const envApi = import.meta.env.VITE_API_URL || '';
const isPlaceholder = !envApi || envApi.includes('your-backend') || envApi.includes('localhost');
const rawApiBase = import.meta.env.PROD
  ? (isPlaceholder ? PROD_API : envApi)
  : (envApi && !isPlaceholder ? envApi : 'http://localhost:8080');
const API_BASE = rawApiBase.replace(/\/+$/, '');

// Log the API base in dev to help debug
if (import.meta.env.DEV) {
  console.info('[RoseWood] API_BASE =', API_BASE);
}

const api = axios.create({
  baseURL: API_BASE,
  timeout: 75000, // Render free tier cold start can take 50-70 seconds
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
    const isAuthRequest = error.config?.url?.includes('/api/auth/');
    if (error.response?.status === 401 && !isAuthRequest) {
      localStorage.removeItem('rw_token');
      localStorage.removeItem('rw_user');
      if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
        window.location.href = '/login';
      }
    }
    if (!error.response) {
      error.message = `Backend server (${API_BASE}) is waking up or unreachable. Render instances spin down after inactivity and take ~40 seconds to wake up. Please wait a moment and try again!`;
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
