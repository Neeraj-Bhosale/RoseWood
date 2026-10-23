import React, { useEffect, useState } from 'react';
import { inventoryApi, supplierApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2, X } from 'lucide-react';

const EMPTY = { ingredientName: '', category: '', quantity: '', unit: 'kg', minimumStock: '', unitCost: '', supplierId: '' };

export default function InventoryPage() {
  const [items, setItems] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [modal, setModal] = useState(null); // null | { mode: 'add'|'edit', data }
  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  const load = () => {
    inventoryApi.getAll().then(r => setItems(r.data)).catch(console.error).finally(() => setLoading(false));
    supplierApi.getAll().then(r => setSuppliers(r.data)).catch(console.error);
  };
  useEffect(load, []);

  const openAdd = () => { setForm(EMPTY); setModal({ mode: 'add' }); };
  const openEdit = (item) => {
    setForm({ ...item, supplierId: item.supplierId || '' });
    setModal({ mode: 'edit', id: item.inventoryId });
  };

  const handleSave = async () => {
    try {
      if (modal.mode === 'add') await inventoryApi.create(form);
      else await inventoryApi.update(modal.id, form);
      toast.success(modal.mode === 'add' ? 'Item added!' : 'Updated!');
      setModal(null); load();
    } catch (err) { toast.error(err.response?.data?.error || 'Save failed'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this inventory item?')) return;
    try { await inventoryApi.delete(id); toast.success('Deleted'); load(); }
    catch { toast.error('Delete failed'); }
  };

  const filtered = items.filter(i =>
    i.ingredientName?.toLowerCase().includes(search.toLowerCase()) ||
    i.category?.toLowerCase().includes(search.toLowerCase())
  );

  const set = (f) => (e) => setForm(p => ({ ...p, [f]: e.target.value }));

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Inventory</h1><p>{items.length} ingredients tracked</p></div>
        <button id="add-inventory-btn" className="btn-primary" onClick={openAdd}><Plus size={16} /> Add Item</button>
      </div>

      <div className="toolbar">
        <input className="search-input" placeholder="Search ingredients…" value={search} onChange={e => setSearch(e.target.value)} />
        <div className="low-stock-badge">⚠ {items.filter(i => Number(i.quantity) <= Number(i.minimumStock)).length} low stock</div>
      </div>

      <div className="card">
        <table className="data-table">
          <thead>
            <tr><th>Ingredient</th><th>Category</th><th>Stock</th><th>Min Stock</th><th>Unit</th><th>Cost/Unit</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {filtered.length === 0
              ? <tr><td colSpan={8} className="empty-row">No items found</td></tr>
              : filtered.map(item => {
                const low = Number(item.quantity) <= Number(item.minimumStock);
                return (
                  <tr key={item.inventoryId} className={low ? 'row-danger' : ''}>
                    <td className="font-medium">{item.ingredientName}</td>
                    <td>{item.category}</td>
                    <td className={low ? 'text-danger font-bold' : ''}>{item.quantity}</td>
                    <td>{item.minimumStock}</td>
                    <td>{item.unit}</td>
                    <td>₹{Number(item.unitCost).toLocaleString('en-IN')}</td>
                    <td><span className={`status-badge ${low ? 'red' : 'green'}`}>{low ? 'Low' : 'OK'}</span></td>
                    <td className="actions-cell">
                      <button className="btn-icon" onClick={() => openEdit(item)}><Pencil size={14} /></button>
                      <button className="btn-icon-danger" onClick={() => handleDelete(item.inventoryId)}><Trash2 size={14} /></button>
                    </td>
                  </tr>
                );
              })
            }
          </tbody>
        </table>
      </div>

      {modal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{modal.mode === 'add' ? 'Add Inventory Item' : 'Edit Inventory Item'}</h2>
              <button onClick={() => setModal(null)}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Ingredient Name</label>
                  <input value={form.ingredientName} onChange={set('ingredientName')} placeholder="Paneer" />
                </div>
                <div className="form-group">
                  <label>Category</label>
                  <input value={form.category} onChange={set('category')} placeholder="Dairy" />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Current Stock</label>
                  <input type="number" value={form.quantity} onChange={set('quantity')} />
                </div>
                <div className="form-group">
                  <label>Unit</label>
                  <select value={form.unit} onChange={set('unit')}>
                    {['kg','g','litre','ml','units','dozen'].map(u => <option key={u}>{u}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Min Stock Level</label>
                  <input type="number" value={form.minimumStock} onChange={set('minimumStock')} />
                </div>
                <div className="form-group">
                  <label>Cost / Unit (₹)</label>
                  <input type="number" value={form.unitCost} onChange={set('unitCost')} />
                </div>
              </div>
              <div className="form-group">
                <label>Supplier</label>
                <select value={form.supplierId} onChange={set('supplierId')}>
                  <option value="">— None —</option>
                  {suppliers.map(s => <option key={s.supplierId} value={s.supplierId}>{s.name}</option>)}
                </select>
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
