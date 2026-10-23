import React, { useEffect, useState } from 'react';
import { orderApi, menuApi, customerApi, tableApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, X, ChevronDown } from 'lucide-react';

const STATUS_COLORS = {
  CREATED: 'gray', CONFIRMED: 'blue', PREPARING: 'orange',
  READY: 'green', SERVED: 'teal', COMPLETED: 'purple', CANCELLED: 'red'
};

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [showCreate, setShowCreate] = useState(false);
  const [menuItems, setMenuItems] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [tables, setTables] = useState([]);
  const [cart, setCart] = useState([]);
  const [form, setForm] = useState({ customerId: '', tableId: '', specialNotes: '' });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const load = () => {
    orderApi.getAll().then(r => setOrders(r.data)).catch(console.error).finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    menuApi.getAvailable().then(r => setMenuItems(r.data));
    customerApi.getAll().then(r => setCustomers(r.data));
    tableApi.getAvailable().then(r => setTables(r.data));
  }, []);

  const addToCart = (item) => {
    setCart(prev => {
      const ex = prev.find(c => c.itemId === item.itemId);
      if (ex) return prev.map(c => c.itemId === item.itemId ? { ...c, quantity: c.quantity + 1 } : c);
      return [...prev, { itemId: item.itemId, name: item.name, price: item.price, quantity: 1 }];
    });
  };

  const removeFromCart = (itemId) => setCart(prev => prev.filter(c => c.itemId !== itemId));

  const cartTotal = cart.reduce((sum, c) => sum + c.price * c.quantity, 0);

  const handleCreateOrder = async () => {
    if (cart.length === 0) { toast.error('Add at least one item'); return; }
    setSubmitting(true);
    try {
      const payload = {
        customerId: form.customerId || null,
        tableId: form.tableId || null,
        specialNotes: form.specialNotes,
        items: cart.map(c => ({ itemId: c.itemId, quantity: c.quantity })),
      };
      await orderApi.create(payload);
      toast.success('Order placed! Inventory updated.');
      setCart([]); setForm({ customerId: '', tableId: '', specialNotes: '' });
      setShowCreate(false); load();
    } catch (err) {
      toast.error(err.response?.data?.error || 'Failed to create order');
    } finally { setSubmitting(false); }
  };

  const updateStatus = async (id, status) => {
    try {
      await orderApi.updateStatus(id, status);
      toast.success('Status updated');
      load();
    } catch { toast.error('Update failed'); }
  };

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Orders</h1><p>Manage all restaurant orders</p></div>
        <button id="create-order-btn" className="btn-primary" onClick={() => setShowCreate(true)}>
          <Plus size={16} /> New Order
        </button>
      </div>

      {/* Create Order Modal */}
      {showCreate && (
        <div className="modal-overlay">
          <div className="modal modal-xl">
            <div className="modal-header">
              <h2>Create New Order</h2>
              <button onClick={() => setShowCreate(false)}><X size={20} /></button>
            </div>
            <div className="modal-body order-create-layout">
              {/* Menu Panel */}
              <div className="menu-panel">
                <h3>Select Items</h3>
                <div className="menu-grid">
                  {menuItems.map(item => (
                    <div key={item.itemId} className="menu-card" onClick={() => addToCart(item)}>
                      <div className="menu-card-name">{item.name}</div>
                      <div className="menu-card-cat">{item.categoryName}</div>
                      <div className="menu-card-price">₹{Number(item.price).toLocaleString('en-IN')}</div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Cart Panel */}
              <div className="cart-panel">
                <h3>Order Summary</h3>
                <div className="form-row">
                  <div className="form-group">
                    <label>Customer</label>
                    <select value={form.customerId} onChange={e => setForm({ ...form, customerId: e.target.value })}>
                      <option value="">Walk-in</option>
                      {customers.map(c => <option key={c.customerId} value={c.customerId}>{c.name}</option>)}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Table</label>
                    <select value={form.tableId} onChange={e => setForm({ ...form, tableId: e.target.value })}>
                      <option value="">Takeaway</option>
                      {tables.map(t => <option key={t.tableId} value={t.tableId}>Table {t.tableNumber} ({t.capacity}p)</option>)}
                    </select>
                  </div>
                </div>

                <div className="cart-items">
                  {cart.length === 0
                    ? <div className="empty-box">Click menu items to add</div>
                    : cart.map(c => (
                      <div key={c.itemId} className="cart-item">
                        <div>
                          <div className="cart-item-name">{c.name}</div>
                          <div className="cart-item-price">₹{(c.price * c.quantity).toLocaleString('en-IN')} × {c.quantity}</div>
                        </div>
                        <button className="btn-icon-danger" onClick={() => removeFromCart(c.itemId)}><X size={14} /></button>
                      </div>
                    ))
                  }
                </div>

                <div className="form-group">
                  <label>Notes</label>
                  <input type="text" placeholder="Special instructions…" value={form.specialNotes}
                    onChange={e => setForm({ ...form, specialNotes: e.target.value })} />
                </div>

                <div className="cart-total">
                  <span>Subtotal</span><span>₹{cartTotal.toLocaleString('en-IN')}</span>
                </div>
                <div className="cart-total">
                  <span>Tax (5%)</span><span>₹{(cartTotal * 0.05).toFixed(2)}</span>
                </div>
                <div className="cart-total cart-total-final">
                  <span>Total</span><span>₹{(cartTotal * 1.075).toFixed(2)}</span>
                </div>

                <button id="place-order-btn" className="btn-primary w-full" onClick={handleCreateOrder} disabled={submitting}>
                  {submitting ? 'Placing…' : '✓ Place Order'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Orders Table */}
      <div className="card">
        <table className="data-table">
          <thead>
            <tr>
              <th>Order #</th><th>Customer</th><th>Table</th>
              <th>Items</th><th>Total</th><th>Status</th><th>Date</th><th>Action</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0
              ? <tr><td colSpan={8} className="empty-row">No orders found</td></tr>
              : orders.map(o => (
                <tr key={o.orderId}>
                  <td className="mono">{o.orderNumber}</td>
                  <td>{o.customerName || 'Walk-in'}</td>
                  <td>{o.tableNumber || 'Takeaway'}</td>
                  <td>{o.items?.length || 0} items</td>
                  <td>₹{Number(o.totalAmount).toLocaleString('en-IN')}</td>
                  <td><span className={`status-badge ${STATUS_COLORS[o.status]}`}>{o.status}</span></td>
                  <td>{new Date(o.orderDateTime).toLocaleString('en-IN')}</td>
                  <td>
                    <select className="status-select" value={o.status}
                      onChange={e => updateStatus(o.orderId, e.target.value)}>
                      {['CONFIRMED','PREPARING','READY','SERVED','COMPLETED','CANCELLED'].map(s =>
                        <option key={s} value={s}>{s}</option>)}
                    </select>
                  </td>
                </tr>
              ))
            }
          </tbody>
        </table>
      </div>
    </div>
  );
}
