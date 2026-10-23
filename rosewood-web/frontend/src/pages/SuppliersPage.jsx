import React, { useEffect, useState } from 'react';
import { supplierApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2, X } from 'lucide-react';

const EMPTY = { name: '', contactPerson: '', phone: '', email: '', address: '', suppliedCategory: '' };

export default function SuppliersPage() {
  const [suppliers, setSuppliers] = useState([]);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => supplierApi.getAll().then(r => setSuppliers(r.data)).catch(console.error).finally(() => setLoading(false));
  useEffect(load, []);

  const openAdd = () => { setForm(EMPTY); setModal({ mode: 'add' }); };
  const openEdit = (s) => { setForm(s); setModal({ mode: 'edit', id: s.supplierId }); };
  const set = (f) => (e) => setForm(p => ({ ...p, [f]: e.target.value }));

  const handleSave = async () => {
    try {
      if (modal.mode === 'add') await supplierApi.create(form);
      else await supplierApi.update(modal.id, form);
      toast.success('Saved!'); setModal(null); load();
    } catch (err) { toast.error(err.response?.data?.error || 'Save failed'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete supplier?')) return;
    try { await supplierApi.delete(id); toast.success('Deleted'); load(); }
    catch { toast.error('Delete failed'); }
  };

  const filtered = suppliers.filter(s =>
    s.name?.toLowerCase().includes(search.toLowerCase()) ||
    s.suppliedCategory?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Suppliers</h1><p>{suppliers.length} suppliers registered</p></div>
        <button id="add-supplier-btn" className="btn-primary" onClick={openAdd}><Plus size={16} /> Add Supplier</button>
      </div>

      <div className="toolbar">
        <input className="search-input" placeholder="Search suppliers…" value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      <div className="card-grid">
        {filtered.length === 0
          ? <div className="empty-box">No suppliers found</div>
          : filtered.map(s => (
            <div key={s.supplierId} className="supplier-card">
              <div className="supplier-header">
                <div className="supplier-avatar">{s.name[0]}</div>
                <div>
                  <div className="supplier-name">{s.name}</div>
                  <div className="supplier-cat">{s.suppliedCategory}</div>
                </div>
                <div className="supplier-actions">
                  <button className="btn-icon" onClick={() => openEdit(s)}><Pencil size={14} /></button>
                  <button className="btn-icon-danger" onClick={() => handleDelete(s.supplierId)}><Trash2 size={14} /></button>
                </div>
              </div>
              <div className="supplier-details">
                <div>👤 {s.contactPerson}</div>
                <div>📞 {s.phone}</div>
                {s.email && <div>✉️ {s.email}</div>}
              </div>
            </div>
          ))
        }
      </div>

      {modal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{modal.mode === 'add' ? 'Add Supplier' : 'Edit Supplier'}</h2>
              <button onClick={() => setModal(null)}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Supplier Name</label>
                  <input value={form.name} onChange={set('name')} placeholder="Shree Dairy Farm" />
                </div>
                <div className="form-group">
                  <label>Category</label>
                  <input value={form.suppliedCategory} onChange={set('suppliedCategory')} placeholder="Dairy products" />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Contact Person</label>
                  <input value={form.contactPerson} onChange={set('contactPerson')} />
                </div>
                <div className="form-group">
                  <label>Phone</label>
                  <input value={form.phone} onChange={set('phone')} />
                </div>
              </div>
              <div className="form-group">
                <label>Email</label>
                <input value={form.email} onChange={set('email')} type="email" />
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
