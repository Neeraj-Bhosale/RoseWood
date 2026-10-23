import React, { useEffect, useState } from 'react';
import { orderApi } from '../api/client';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, LineChart, Line, CartesianGrid } from 'recharts';

export default function ReportsPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    orderApi.getAll().then(r => setOrders(r.data)).catch(console.error).finally(() => setLoading(false));
  }, []);

  const completed = orders.filter(o => o.status === 'COMPLETED');
  const totalRevenue = completed.reduce((s, o) => s + Number(o.totalAmount || 0), 0);
  const avgOrder = completed.length > 0 ? totalRevenue / completed.length : 0;

  // Revenue by day (last 7 days)
  const last7 = Array.from({ length: 7 }, (_, i) => {
    const d = new Date(); d.setDate(d.getDate() - (6 - i));
    const dateStr = d.toLocaleDateString('en-IN', { month: 'short', day: 'numeric' });
    const dayRevenue = completed
      .filter(o => new Date(o.orderDateTime).toDateString() === d.toDateString())
      .reduce((s, o) => s + Number(o.totalAmount || 0), 0);
    return { date: dateStr, revenue: dayRevenue };
  });

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Reports</h1><p>Sales analytics and insights</p></div>
      </div>

      <div className="stats-grid">
        <div className="stat-card stat-purple">
          <div className="stat-info">
            <div className="stat-value">{completed.length}</div>
            <div className="stat-label">Completed Orders</div>
          </div>
        </div>
        <div className="stat-card stat-green">
          <div className="stat-info">
            <div className="stat-value">₹{totalRevenue.toLocaleString('en-IN')}</div>
            <div className="stat-label">Total Revenue</div>
          </div>
        </div>
        <div className="stat-card stat-orange">
          <div className="stat-info">
            <div className="stat-value">₹{avgOrder.toFixed(0)}</div>
            <div className="stat-label">Avg Order Value</div>
          </div>
        </div>
        <div className="stat-card stat-blue">
          <div className="stat-info">
            <div className="stat-value">{orders.filter(o => o.status === 'CANCELLED').length}</div>
            <div className="stat-label">Cancelled Orders</div>
          </div>
        </div>
      </div>

      <div className="charts-row">
        <div className="chart-card">
          <h3>Revenue — Last 7 Days</h3>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={last7}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="date" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <Tooltip contentStyle={{ background: '#1e293b', border: 'none', borderRadius: 8 }} />
              <Line type="monotone" dataKey="revenue" stroke="#c084fc" strokeWidth={2} dot={{ fill: '#c084fc' }} />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
          <h3>Orders by Day</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={last7}>
              <XAxis dataKey="date" tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} />
              <Tooltip contentStyle={{ background: '#1e293b', border: 'none', borderRadius: 8 }} />
              <Bar dataKey="revenue" fill="#818cf8" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="card">
        <h3 style={{ padding: '16px 20px', margin: 0 }}>Order History</h3>
        <table className="data-table">
          <thead><tr><th>Order #</th><th>Date</th><th>Status</th><th>Items</th><th>Total</th></tr></thead>
          <tbody>
            {orders.slice(0, 20).map(o => (
              <tr key={o.orderId}>
                <td className="mono">{o.orderNumber}</td>
                <td>{new Date(o.orderDateTime).toLocaleString('en-IN')}</td>
                <td><span className={`status-badge ${o.status?.toLowerCase()}`}>{o.status}</span></td>
                <td>{o.items?.length || 0}</td>
                <td>₹{Number(o.totalAmount).toLocaleString('en-IN')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
