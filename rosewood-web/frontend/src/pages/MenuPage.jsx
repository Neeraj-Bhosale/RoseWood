import React, { useEffect, useState } from 'react';
import { menuApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, Pencil, Trash2, X, Search, RefreshCw } from 'lucide-react';

const EMPTY = { name: '', description: '', price: '', preparationTime: 15, categoryId: '', available: true };

export default function MenuPage() {
  const [items, setItems] = useState([]);
  const [categories, setCategories] = useState([]);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filterCat, setFilterCat] = useState('');

  const load = () => {
    setLoading(true);
    Promise.allSettled([menuApi.getItems(), menuApi.getCategories()])
      .then(([itemsRes, catsRes]) => {
        let anyLoaded = false;
        if (itemsRes.status === 'fulfilled') {
          setItems(itemsRes.value.data || []);
          anyLoaded = true;
        } else {
          console.error('Menu items failed:', itemsRes.reason);
        }

        if (catsRes.status === 'fulfilled') {
          setCategories(catsRes.value.data || []);
          anyLoaded = true;
        } else {
          console.error('Menu categories failed:', catsRes.reason);
        }

        if (!anyLoaded) {
          const err = itemsRes.reason || catsRes.reason;
          toast.error(err?.response?.data?.error || err?.message || 'Failed to load menu items');
        }
      })
      .catch(err => {
        console.error(err);
        toast.error(err.message || 'Error loading menu');
      })
      .finally(() => setLoading(false));
  };
  useEffect(load, []);

  const openAdd = () => { setForm(EMPTY); setModal({ mode: 'add' }); };
  const openEdit = (item) => { setForm({ ...item, categoryId: item.categoryId || '' }); setModal({ mode: 'edit', id: item.itemId }); };
  const set = (f) => (e) => setForm(p => ({ ...p, [f]: e.target.type === 'checkbox' ? e.target.checked : e.target.value }));

  const handleSave = async () => {
    try {
      if (modal.mode === 'add') await menuApi.createItem(form);
      else await menuApi.updateItem(modal.id, form);
      toast.success('Saved!'); setModal(null); load();
    } catch (err) { toast.error(err.response?.data?.error || 'Save failed'); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete menu item?')) return;
    try { await menuApi.deleteItem(id); toast.success('Deleted'); load(); }
    catch { toast.error('Cannot delete — may be linked to orders'); }
  };

  const filtered = items.filter(i =>
    i.name?.toLowerCase().includes(search.toLowerCase()) &&
    (filterCat === '' || String(i.categoryId) === filterCat)
  );

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Menu</h1><p>{items.length} items across {categories.length} categories</p></div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button className="btn-ghost" onClick={load} title="Refresh Menu" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <RefreshCw size={14} /> Refresh
          </button>
          <button id="add-menu-btn" className="btn-primary" onClick={openAdd}><Plus size={16} /> Add Item</button>
        </div>
      </div>

      <div className="toolbar">
        <div className="search-wrapper">
          <Search size={16} className="search-icon" />
          <input className="search-input" placeholder="Search menu…" value={search} onChange={e => setSearch(e.target.value)} />
        </div>
        <select className="filter-select" value={filterCat} onChange={e => setFilterCat(e.target.value)}>
          <option value="">All Categories</option>
          {categories.map(c => <option key={c.categoryId} value={String(c.categoryId)}>{c.name}</option>)}
        </select>
      </div>

      <div className="menu-items-grid">
        {filtered.length === 0
          ? <div className="empty-box">No menu items found</div>
          : filtered.map(item => (
            <div key={item.itemId} className={`menu-item-card ${!item.available ? 'unavailable' : ''}`}>
              <div className="menu-item-body">
                <div className="menu-item-cat">{item.categoryName}</div>
                <div className="menu-item-name">{item.name}</div>
                <div className="menu-item-desc">{item.description}</div>
              </div>
              <div className="menu-item-footer">
                <div className="menu-item-price">₹{Number(item.price).toLocaleString('en-IN')}</div>
                <div className="menu-item-time">⏱ {item.preparationTime}m</div>
                <span className={`status-badge ${item.available ? 'green' : 'red'}`}>{item.available ? 'Available' : 'Unavailable'}</span>
                <div className="menu-item-actions">
                  <button className="btn-icon" onClick={() => openEdit(item)}><Pencil size={13} /></button>
                  <button className="btn-icon-danger" onClick={() => handleDelete(item.itemId)}><Trash2 size={13} /></button>
                </div>
              </div>
            </div>
          ))
        }
      </div>

      {modal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>{modal.mode === 'add' ? 'Add Menu Item' : 'Edit Menu Item'}</h2>
              <button onClick={() => setModal(null)}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Name</label>
                  <input value={form.name} onChange={set('name')} placeholder="Butter Chicken" />
                </div>
                <div className="form-group">
                  <label>Category</label>
                  <select value={form.categoryId} onChange={set('categoryId')}>
                    <option value="">Select…</option>
                    {categories.map(c => <option key={c.categoryId} value={c.categoryId}>{c.name}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea value={form.description} onChange={set('description')} rows={2} />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Price (₹)</label>
                  <input type="number" value={form.price} onChange={set('price')} />
                </div>
                <div className="form-group">
                  <label>Prep Time (min)</label>
                  <input type="number" value={form.preparationTime} onChange={set('preparationTime')} />
                </div>
              </div>
              <div className="form-group checkbox-group">
                <label>
                  <input type="checkbox" checked={form.available} onChange={set('available')} />
                  &nbsp;Available for ordering
                </label>
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
