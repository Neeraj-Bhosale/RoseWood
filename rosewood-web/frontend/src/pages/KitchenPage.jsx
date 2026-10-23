import React, { useEffect, useState } from 'react';
import { orderApi } from '../api/client';
import toast from 'react-hot-toast';

const KITCHEN_STATUSES = ['CONFIRMED', 'PREPARING', 'READY', 'SERVED'];

export default function KitchenPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    orderApi.getAll()
      .then(r => setOrders(r.data.filter(o => ['CONFIRMED','PREPARING','READY'].includes(o.status))))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); const iv = setInterval(load, 15000); return () => clearInterval(iv); }, []);

  const advance = async (id, current) => {
    const next = { CONFIRMED: 'PREPARING', PREPARING: 'READY', READY: 'SERVED' }[current];
    if (!next) return;
    try { await orderApi.updateStatus(id, next); toast.success(`Order → ${next}`); load(); }
    catch { toast.error('Update failed'); }
  };

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Kitchen View</h1><p>Live orders — auto-refreshes every 15s</p></div>
        <div className="live-badge">🟢 LIVE</div>
      </div>

      <div className="kitchen-lanes">
        {KITCHEN_STATUSES.slice(0, 3).map(status => (
          <div key={status} className={`kitchen-lane lane-${status.toLowerCase()}`}>
            <div className="lane-header">{status} ({orders.filter(o => o.status === status).length})</div>
            {orders.filter(o => o.status === status).map(order => (
              <div key={order.orderId} className="kitchen-ticket">
                <div className="ticket-header">
                  <span className="ticket-number">{order.orderNumber}</span>
                  <span className="ticket-table">{order.tableNumber || 'Takeaway'}</span>
                </div>
                <ul className="ticket-items">
                  {(order.items || []).map(item => (
                    <li key={item.orderItemId}>
                      <span className="item-qty">{item.quantity}×</span> {item.itemName}
                      {item.specialInstructions && <span className="item-note"> ({item.specialInstructions})</span>}
                    </li>
                  ))}
                </ul>
                {order.specialNotes && <div className="ticket-note">📝 {order.specialNotes}</div>}
                <button className="btn-advance" onClick={() => advance(order.orderId, status)}>
                  {status === 'CONFIRMED' ? '▶ Start Cooking' : status === 'PREPARING' ? '✓ Mark Ready' : '✅ Served'}
                </button>
              </div>
            ))}
            {orders.filter(o => o.status === status).length === 0 && (
              <div className="lane-empty">No orders</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
