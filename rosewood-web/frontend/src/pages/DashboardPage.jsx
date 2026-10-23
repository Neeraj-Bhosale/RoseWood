import React, { useEffect, useState } from 'react';
import { orderApi, inventoryApi, customerApi, menuApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from 'recharts';
import { ShoppingCart, Package, Users, UtensilsCrossed, TrendingUp, AlertTriangle } from 'lucide-react';

const COLORS = ['#c084fc', '#818cf8', '#34d399', '#fb923c', '#f472b6'];

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ totalOrders: 0, totalRevenue: 0, activeOrders: 0 });
  const [orders, setOrders] = useState([]);
  const [lowStock, setLowStock] = useState([]);
  const [menuCount, setMenuCount] = useState(0);
  const [customerCount, setCustomerCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      orderApi.getDashboardStats(),
      orderApi.getAll(),
      inventoryApi.getLowStock(),
      menuApi.getItems(),
      customerApi.getAll(),
    ]).then(([statsRes, ordersRes, lowStockRes, menuRes, custRes]) => {
      setStats(statsRes.data);
      setOrders(ordersRes.data.slice(0, 6));
      setLowStock(lowStockRes.data.slice(0, 5));
      setMenuCount(menuRes.data.length);
      setCustomerCount(custRes.data.length);
    }).catch(console.error).finally(() => setLoading(false));
  }, []);

  // Build chart data from recent orders
  const revenueData = orders.slice().reverse().map((o, i) => ({
    name: `ORD-${i + 1}`,
    amount: parseFloat(o.totalAmount || 0),
  }));

  const statusData = ['CONFIRMED', 'PREPARING', 'COMPLETED', 'CANCELLED'].map(s => ({
    name: s,
    value: orders.filter(o => o.status === s).length,
  })).filter(d => d.value > 0);

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Welcome back, <strong>{user?.fullName}</strong> 👋</p>
        </div>
        <div className="badge-role">{user?.role?.replace('_', ' ')}</div>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <StatCard icon={<ShoppingCart />} label="Total Orders" value={stats.totalOrders} color="purple" />
        <StatCard icon={<TrendingUp />} label="Total Revenue" value={`₹${Number(stats.totalRevenue || 0).toLocaleString('en-IN')}`} color="green" />
        <StatCard icon={<ShoppingCart />} label="Active Orders" value={stats.activeOrders} color="orange" />
        <StatCard icon={<UtensilsCrossed />} label="Menu Items" value={menuCount} color="blue" />
        <StatCard icon={<Users />} label="Customers" value={customerCount} color="pink" />
        <StatCard icon={<AlertTriangle />} label="Low Stock Alerts" value={lowStock.length} color="red" />
      </div>

      {/* Charts Row */}
      <div className="charts-row">
        <div className="chart-card">
          <h3>Recent Order Revenue (₹)</h3>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={revenueData}>
              <XAxis dataKey="name" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <Tooltip contentStyle={{ background: '#1e293b', border: 'none', borderRadius: 8 }} />
              <Bar dataKey="amount" fill="#c084fc" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
          <h3>Order Status Breakdown</h3>
          {statusData.length > 0 ? (
            <ResponsiveContainer width="100%" height={200}>
              <PieChart>
                <Pie data={statusData} cx="50%" cy="50%" outerRadius={70} dataKey="value" label>
                  {statusData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                </Pie>
                <Legend />
                <Tooltip contentStyle={{ background: '#1e293b', border: 'none', borderRadius: 8 }} />
              </PieChart>
            </ResponsiveContainer>
          ) : <div className="empty-chart">No order data yet</div>}
        </div>
      </div>

      {/* Tables Row */}
      <div className="tables-row">
        {/* Recent Orders */}
        <div className="table-card">
          <h3>Recent Orders</h3>
          <table className="data-table">
            <thead><tr><th>Order #</th><th>Status</th><th>Total</th><th>Date</th></tr></thead>
            <tbody>
              {orders.length === 0
                ? <tr><td colSpan={4} className="empty-row">No orders yet</td></tr>
                : orders.map(o => (
                  <tr key={o.orderId}>
                    <td>{o.orderNumber}</td>
                    <td><span className={`status-badge ${o.status?.toLowerCase()}`}>{o.status}</span></td>
                    <td>₹{Number(o.totalAmount).toLocaleString('en-IN')}</td>
                    <td>{new Date(o.orderDateTime).toLocaleDateString('en-IN')}</td>
                  </tr>
                ))
              }
            </tbody>
          </table>
        </div>

        {/* Low Stock Alert */}
        <div className="table-card">
          <h3>⚠ Low Stock Alerts</h3>
          {lowStock.length === 0
            ? <div className="empty-box">All inventory levels are healthy ✅</div>
            : <table className="data-table">
                <thead><tr><th>Ingredient</th><th>Stock</th><th>Min</th><th>Unit</th></tr></thead>
                <tbody>
                  {lowStock.map(item => (
                    <tr key={item.inventoryId} className="low-stock-row">
                      <td>{item.ingredientName}</td>
                      <td className="text-danger">{item.quantity}</td>
                      <td>{item.minimumStock}</td>
                      <td>{item.unit}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
          }
        </div>
      </div>
    </div>
  );
}

function StatCard({ icon, label, value, color }) {
  return (
    <div className={`stat-card stat-${color}`}>
      <div className="stat-icon">{icon}</div>
      <div className="stat-info">
        <div className="stat-value">{value}</div>
        <div className="stat-label">{label}</div>
      </div>
    </div>
  );
}
