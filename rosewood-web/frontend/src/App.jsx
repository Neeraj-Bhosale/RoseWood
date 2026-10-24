import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import OrdersPage from './pages/OrdersPage';
import InventoryPage from './pages/InventoryPage';
import SuppliersPage from './pages/SuppliersPage';
import CustomersPage from './pages/CustomersPage';
import MenuPage from './pages/MenuPage';
import TablesPage from './pages/TablesPage';
import KitchenPage from './pages/KitchenPage';
import BillingPage from './pages/BillingPage';
import ReportsPage from './pages/ReportsPage';

import { useAuth } from './context/AuthContext';

function RoleRoute({ roles, children }) {
  const { user } = useAuth();
  const role = user?.role || 'WAITER';
  if (roles && !roles.includes(role)) {
    const fallback = role === 'KITCHEN_STAFF' ? '/kitchen' : (role === 'WAITER' ? '/orders' : '/');
    return <Navigate to={fallback} replace />;
  }
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster position="top-right" toastOptions={{
          style: { background: '#1e293b', color: '#f1f5f9', border: '1px solid #334155' }
        }} />
        <Routes>
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route element={<Layout />}>
            <Route path="/"          element={<RoleRoute roles={['ADMIN', 'MANAGER', 'WAITER', 'CASHIER']}><DashboardPage /></RoleRoute>} />
            <Route path="/orders"    element={<RoleRoute roles={['ADMIN', 'MANAGER', 'WAITER', 'CASHIER', 'KITCHEN_STAFF']}><OrdersPage /></RoleRoute>} />
            <Route path="/menu"      element={<RoleRoute roles={['ADMIN', 'MANAGER', 'WAITER']}><MenuPage /></RoleRoute>} />
            <Route path="/inventory" element={<RoleRoute roles={['ADMIN', 'MANAGER']}><InventoryPage /></RoleRoute>} />
            <Route path="/suppliers" element={<RoleRoute roles={['ADMIN', 'MANAGER']}><SuppliersPage /></RoleRoute>} />
            <Route path="/customers" element={<RoleRoute roles={['ADMIN', 'MANAGER', 'WAITER', 'CASHIER']}><CustomersPage /></RoleRoute>} />
            <Route path="/tables"    element={<RoleRoute roles={['ADMIN', 'MANAGER', 'WAITER']}><TablesPage /></RoleRoute>} />
            <Route path="/kitchen"   element={<RoleRoute roles={['ADMIN', 'MANAGER', 'KITCHEN_STAFF']}><KitchenPage /></RoleRoute>} />
            <Route path="/billing"   element={<RoleRoute roles={['ADMIN', 'MANAGER', 'CASHIER']}><BillingPage /></RoleRoute>} />
            <Route path="/reports"   element={<RoleRoute roles={['ADMIN', 'MANAGER', 'CASHIER']}><ReportsPage /></RoleRoute>} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
