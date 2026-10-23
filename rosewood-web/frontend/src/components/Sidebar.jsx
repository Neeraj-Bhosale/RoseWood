import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import {
  LayoutDashboard, ShoppingCart, Package, Users, UtensilsCrossed,
  Truck, TableProperties, CreditCard, BarChart2, LogOut, ChefHat
} from 'lucide-react';

const NAV = [
  { path: '/',           label: 'Dashboard',   icon: LayoutDashboard },
  { path: '/orders',     label: 'Orders',      icon: ShoppingCart },
  { path: '/menu',       label: 'Menu',        icon: UtensilsCrossed },
  { path: '/inventory',  label: 'Inventory',   icon: Package },
  { path: '/suppliers',  label: 'Suppliers',   icon: Truck },
  { path: '/customers',  label: 'Customers',   icon: Users },
  { path: '/tables',     label: 'Tables',      icon: TableProperties },
  { path: '/kitchen',    label: 'Kitchen',     icon: ChefHat },
  { path: '/billing',    label: 'Billing',     icon: CreditCard },
  { path: '/reports',    label: 'Reports',     icon: BarChart2 },
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
        {NAV.map(({ path, label, icon: Icon }) => (
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
