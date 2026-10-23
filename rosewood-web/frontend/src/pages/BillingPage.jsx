import React, { useEffect, useState } from 'react';
import { orderApi } from '../api/client';
import toast from 'react-hot-toast';
import { Printer, CreditCard } from 'lucide-react';

export default function BillingPage() {
  const [orders, setOrders] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    orderApi.getAll()
      .then(r => setOrders(r.data.filter(o => ['SERVED', 'COMPLETED'].includes(o.status))))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const markPaid = async (id) => {
    try {
      await orderApi.updateStatus(id, 'COMPLETED');
      toast.success('Payment recorded!');
      setSelected(null);
      const res = await orderApi.getAll();
      setOrders(res.data.filter(o => ['SERVED', 'COMPLETED'].includes(o.status)));
    } catch { toast.error('Failed'); }
  };

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Billing</h1><p>Process payments for served orders</p></div>
      </div>

      <div className="billing-layout">
        {/* Order List */}
        <div className="billing-list">
          {orders.length === 0
            ? <div className="empty-box">No orders pending billing</div>
            : orders.map(o => (
              <div
                key={o.orderId}
                className={`billing-item ${selected?.orderId === o.orderId ? 'selected' : ''} ${o.status === 'COMPLETED' ? 'paid' : ''}`}
                onClick={() => setSelected(o)}
              >
                <div className="billing-item-top">
                  <span className="mono">{o.orderNumber}</span>
                  <span className={`status-badge ${o.status === 'COMPLETED' ? 'purple' : 'orange'}`}>{o.status}</span>
                </div>
                <div className="billing-item-bot">
                  <span>{o.tableNumber || 'Takeaway'}</span>
                  <span className="billing-amount">₹{Number(o.totalAmount).toLocaleString('en-IN')}</span>
                </div>
              </div>
            ))
          }
        </div>

        {/* Invoice Panel */}
        <div className="invoice-panel">
          {!selected ? (
            <div className="empty-box">Select an order to view invoice</div>
          ) : (
            <>
              <div className="invoice-header">
                <div className="invoice-brand">🌹 RoseWood Fine Dining</div>
                <div className="invoice-number">Invoice #{selected.orderNumber}</div>
                <div className="invoice-date">{new Date(selected.orderDateTime).toLocaleString('en-IN')}</div>
              </div>

              <div className="invoice-meta">
                <div>Table: <strong>{selected.tableNumber || 'Takeaway'}</strong></div>
                <div>Customer: <strong>{selected.customerName || 'Walk-in'}</strong></div>
              </div>

              <table className="invoice-table">
                <thead><tr><th>Item</th><th>Qty</th><th>Price</th><th>Total</th></tr></thead>
                <tbody>
                  {(selected.items || []).map(item => (
                    <tr key={item.orderItemId}>
                      <td>{item.itemName}</td>
                      <td>{item.quantity}</td>
                      <td>₹{Number(item.unitPrice).toLocaleString('en-IN')}</td>
                      <td>₹{Number(item.subtotal).toLocaleString('en-IN')}</td>
                    </tr>
                  ))}
                </tbody>
              </table>

              <div className="invoice-totals">
                <div><span>Subtotal</span><span>₹{Number(selected.subtotal).toLocaleString('en-IN')}</span></div>
                <div><span>Tax (5%)</span><span>₹{Number(selected.taxAmount).toLocaleString('en-IN')}</span></div>
                <div><span>Service (2.5%)</span><span>₹{Number(selected.serviceCharge).toLocaleString('en-IN')}</span></div>
                <div className="invoice-grand"><span>Grand Total</span><span>₹{Number(selected.totalAmount).toLocaleString('en-IN')}</span></div>
              </div>

              {selected.status !== 'COMPLETED' && (
                <div className="invoice-actions">
                  <button className="btn-ghost" onClick={() => window.print()}><Printer size={16} /> Print</button>
                  <button className="btn-primary" onClick={() => markPaid(selected.orderId)}>
                    <CreditCard size={16} /> Mark as Paid
                  </button>
                </div>
              )}
              {selected.status === 'COMPLETED' && (
                <div className="paid-stamp">✅ PAID</div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
