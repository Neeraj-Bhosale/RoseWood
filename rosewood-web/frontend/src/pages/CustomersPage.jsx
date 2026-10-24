import React, { useEffect, useState } from 'react';
import { customerApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2, X, RefreshCw } from 'lucide-react';

const EMPTY = { name: '', phone: '', email: '', address: '' };

export default function CustomersPage() {
  const [customers, setCustomers] = useState([]);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => {
    setLoading(true);
    customerApi.getAll()
      .then(r => setCustomers(r.data || []))
      .catch(err => {
        console.error('Customers load error:', err);
        toast.error(err.response?.data?.error || err.message || 'Failed to load customers');
      })
      .finally(() => setLoading(false));
  };
  useEffect(load, []);

  const openAdd = () => { setForm(EMPTY); setModal({ mode: 'add' }); };
  const openEdit = (c) => { setForm(c); setModal({ mode: 'edit', id: c.customerId }); };
  const set = (f) => (e) => setForm(p => ({ ...p, [f]: e.target.value }));

  const handleSave = async () => {
    try {
      if (modal.mode === 'add') await customerApi.create(form);
      else await customerApi.update(modal.id, form);
      toast.success('Saved!'); setModal(null); load();
    } catch (err) { toast.error(err.response?.data?.error || 'Save failed'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete customer?')) return;
    try { await customerApi.delete(id); toast.success('Deleted'); load(); }
    catch { toast.error('Delete failed'); }
  };

  const filtered = customers.filter(c =>
    c.name?.toLowerCase().includes(search.toLowerCase()) ||
    c.phone?.includes(search)
  );

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Customers</h1><p>{customers.length} registered customers</p></div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button className="btn-ghost" onClick={load} title="Refresh Customers" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <RefreshCw size={14} /> Refresh
          </button>
          <button id="add-customer-btn" className="btn-primary" onClick={openAdd}><Plus size={16} /> Add Customer</button>
        </div>
      </div>

      <div className="toolbar">
        <input className="search-input" placeholder="Search by name or phone…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      <div className="card">
        <table className="data-table">
          <thead>
            <tr><th>Name</th><th>Phone</th><th>Email</th><th>Loyalty Pts</th><th>Visits</th><th>Total Spent</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {filtered.length === 0
              ? <tr><td colSpan={7} className="empty-row">No customers found</td></tr>
              : filtered.map(c => (
                <tr key={c.customerId}>
                  <td className="font-medium">{c.name}</td>
                  <td>{c.phone}</td>
                  <td>{c.email || '—'}</td>
                  <td><span className="badge-points">⭐ {c.loyaltyPoints}</span></td>
                  <td>{c.totalVisits}</td>
                  <td>₹{Number(c.totalSpent).toLocaleString('en-IN')}</td>
                  <td className="actions-cell">
                    <button className="btn-icon" onClick={() => openEdit(c)}><Pencil size={14} /></button>
                    <button className="btn-icon-danger" onClick={() => handleDelete(c.customerId)}><Trash2 size={14} /></button>
                  </td>
                </tr>
              ))
            }
          </tbody>
        </table>
      </div>

      {modal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{modal.mode === 'add' ? 'Add Customer' : 'Edit Customer'}</h2>
              <button onClick={() => setModal(null)}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Name</label>
                  <input value={form.name} onChange={set('name')} placeholder="Arjun Sharma" />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input value={form.phone} onChange={set('phone')} placeholder="9876543210" />
                </div>
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" value={form.email} onChange={set('email')} />
              </div>
              <div className="form-group">
                <label>Address</label>
                <textarea value={form.address} onChange={set('address')} rows={2} />
              </div>
              <div className="modal-actions">
                <button className="btn-ghost" onClick={() => setModal(null)}>Cancel</button>
                <button className="btn-primary" onClick={handleSave}>Save</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
