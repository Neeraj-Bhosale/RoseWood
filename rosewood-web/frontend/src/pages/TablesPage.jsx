import React, { useEffect, useState } from 'react';
import { tableApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, X } from 'lucide-react';

const STATUS_CONFIG = {
  AVAILABLE: { color: 'green', emoji: '✅' },
  OCCUPIED:  { color: 'red',   emoji: '🔴' },
  RESERVED:  { color: 'orange', emoji: '🟡' },
  CLEANING:  { color: 'blue',  emoji: '🔵' },
};

export default function TablesPage() {
  const [tables, setTables] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [form, setForm] = useState({ tableNumber: '', capacity: '', location: 'Main Dining Hall' });
  const [loading, setLoading] = useState(true);

  const load = () => tableApi.getAll().then(r => setTables(r.data)).catch(console.error).finally(() => setLoading(false));
  useEffect(load, []);

  const handleAdd = async () => {
    try { await tableApi.create(form); toast.success('Table added!'); setShowAdd(false); load(); }
    catch (err) { toast.error(err.response?.data?.error || 'Failed'); }
  };

  const changeStatus = async (id, status) => {
    try { await tableApi.updateStatus(id, status); load(); }
    catch { toast.error('Update failed'); }
  };

  if (loading) return <div className="page-loading"><div className="spinner" /></div>;

  const summary = Object.keys(STATUS_CONFIG).map(s => ({ status: s, count: tables.filter(t => t.status === s).length }));

  return (
    <div className="page">
      <div className="page-header">
        <div><h1>Tables</h1><p>{tables.length} tables total</p></div>
        <button id="add-table-btn" className="btn-primary" onClick={() => setShowAdd(true)}><Plus size={16} /> Add Table</button>
      </div>

      {/* Summary Pills */}
      <div className="status-summary">
        {summary.map(s => (
          <div key={s.status} className={`status-pill status-pill-${STATUS_CONFIG[s.status].color}`}>
            {STATUS_CONFIG[s.status].emoji} {s.status}: {s.count}
          </div>
        ))}
      </div>

      {/* Table Grid */}
      <div className="tables-grid">
        {tables.map(t => {
          const cfg = STATUS_CONFIG[t.status] || STATUS_CONFIG.AVAILABLE;
          return (
            <div key={t.tableId} className={`table-cell table-cell-${cfg.color}`}>
              <div className="table-number">T{t.tableNumber}</div>
              <div className="table-cap">👥 {t.capacity} seats</div>
              <div className="table-loc">{t.location}</div>
              <div className={`status-badge ${cfg.color}`}>{t.status}</div>
              <select className="status-select mt-2" value={t.status} onChange={e => changeStatus(t.tableId, e.target.value)}>
                {Object.keys(STATUS_CONFIG).map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          );
        })}
      </div>

      {showAdd && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Add Table</h2>
              <button onClick={() => setShowAdd(false)}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Table Number</label>
                  <input value={form.tableNumber} onChange={e => setForm(p => ({ ...p, tableNumber: e.target.value }))} placeholder="T01" />
                </div>
                <div className="form-group">
                  <label>Capacity</label>
                  <input type="number" value={form.capacity} onChange={e => setForm(p => ({ ...p, capacity: e.target.value }))} />
                </div>
              </div>
              <div className="form-group">
                <label>Location</label>
                <input value={form.location} onChange={e => setForm(p => ({ ...p, location: e.target.value }))} />
              </div>
              <div className="modal-actions">
                <button className="btn-ghost" onClick={() => setShowAdd(false)}>Cancel</button>
                <button className="btn-primary" onClick={handleAdd}>Add Table</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
