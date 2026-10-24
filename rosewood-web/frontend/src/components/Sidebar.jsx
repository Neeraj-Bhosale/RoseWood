import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import {
  LayoutDashboard, ShoppingCart, Package, Users, UtensilsCrossed,
  Truck, TableProperties, CreditCard, BarChart2, LogOut, ChefHat
} from 'lucide-react';

const NAV = [
  { path: '/',           label: 'Dashboard',   icon: LayoutDashboard, roles: ['ADMIN', 'MANAGER', 'WAITER', 'CASHIER'] },
  { path: '/orders',     label: 'Orders',      icon: ShoppingCart,    roles: ['ADMIN', 'MANAGER', 'WAITER', 'CASHIER', 'KITCHEN_STAFF'] },
  { path: '/menu',       label: 'Menu',        icon: UtensilsCrossed, roles: ['ADMIN', 'MANAGER', 'WAITER'] },
  { path: '/tables',     label: 'Tables',      icon: TableProperties, roles: ['ADMIN', 'MANAGER', 'WAITER'] },
  { path: '/kitchen',    label: 'Kitchen',     icon: ChefHat,         roles: ['ADMIN', 'MANAGER', 'KITCHEN_STAFF'] },
  { path: '/billing',    label: 'Billing',     icon: CreditCard,      roles: ['ADMIN', 'MANAGER', 'CASHIER'] },
  { path: '/customers',  label: 'Customers',   icon: Users,           roles: ['ADMIN', 'MANAGER', 'WAITER', 'CASHIER'] },
  { path: '/inventory',  label: 'Inventory',   icon: Package,         roles: ['ADMIN', 'MANAGER'] },
  { path: '/suppliers',  label: 'Suppliers',   icon: Truck,           roles: ['ADMIN', 'MANAGER'] },
  { path: '/reports',    label: 'Reports',     icon: BarChart2,       roles: ['ADMIN', 'MANAGER', 'CASHIER'] },
];

export default function Sidebar() {
  const { pathname } = useLocation();
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    toast.success('Logged out');
    navigate('/login');
  };

  const userRole = user?.role || 'WAITER';
  const visibleNav = NAV.filter(item => !item.roles || item.roles.includes(userRole));

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-logo">🌹</span>
        <div>
          <div className="brand-name">RoseWood</div>
          <div className="brand-sub">Fine Dining</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        {visibleNav.map(({ path, label, icon: Icon }) => (
          <Link
            key={path}
            to={path}
            className={`nav-item ${pathname === path ? 'active' : ''}`}
          >
            <Icon size={18} />
            <span>{label}</span>
          </Link>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="user-avatar">{user?.fullName?.[0] || 'U'}</div>
          <div>
            <div className="user-name">{user?.fullName}</div>
            <div className="user-role">{user?.role?.replace('_', ' ')}</div>
          </div>
        </div>
        <button id="logout-btn" className="logout-btn" onClick={handleLogout}>
          <LogOut size={16} />
        </button>
      </div>
    </aside>
  );
}
