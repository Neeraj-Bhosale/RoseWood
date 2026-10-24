import React, { useEffect, useState, useMemo } from 'react';
import { tableApi } from '../api/client';
import toast from 'react-hot-toast';
import { Plus, X, RefreshCw, Search, Users, MapPin, CheckCircle2, Clock, Sparkles, AlertCircle } from 'lucide-react';

const STATUS_CONFIG = {
  AVAILABLE: {
    color: 'green',
    label: 'Available / Empty',
    shortLabel: 'Available',
    dotClass: 'status-dot-green',
    pillClass: 'status-pill-green',
    cellClass: 'table-cell-green',
    activeBtnClass: 'active-green',
    icon: CheckCircle2,
    emoji: '🟢'
  },
  OCCUPIED: {
    color: 'red',
    label: 'Occupied',
    shortLabel: 'Occupied',
    dotClass: 'status-dot-red',
    pillClass: 'status-pill-red',
    cellClass: 'table-cell-red',
    activeBtnClass: 'active-red',
    icon: AlertCircle,
    emoji: '🔴'
  },
  RESERVED: {
    color: 'orange',
    label: 'Reserved',
    shortLabel: 'Reserved',
    dotClass: 'status-dot-orange',
    pillClass: 'status-pill-orange',
    cellClass: 'table-cell-orange',
    activeBtnClass: 'active-orange',
    icon: Clock,
    emoji: '🟡'
  },
  CLEANING: {
    color: 'blue',
    label: 'Cleaning',
    shortLabel: 'Cleaning',
    dotClass: 'status-dot-blue',
    pillClass: 'status-pill-blue',
    cellClass: 'table-cell-blue',
    activeBtnClass: 'active-blue',
    icon: Sparkles,
    emoji: '🔵'
  },
};

export default function TablesPage() {
  const [tables, setTables] = useState([]);
  const [showAdd, setShowAdd] = useState(false);
  const [form, setForm] = useState({ tableNumber: '', capacity: '4', location: 'Main Dining Hall' });
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [search, setSearch] = useState('');
  const [selectedLocation, setSelectedLocation] = useState('ALL');

  const load = () => {
    setLoading(true);
    tableApi.getAll()
      .then(r => setTables(r.data || []))
      .catch(err => {
        console.error('Tables load error:', err);
        toast.error(err.response?.data?.error || err.message || 'Failed to load tables');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      await tableApi.create({
        ...form,
        capacity: parseInt(form.capacity) || 2,
        status: 'AVAILABLE'
      });
      toast.success('Table added successfully!');
      setShowAdd(false);
      setForm({ tableNumber: '', capacity: '4', location: 'Main Dining Hall' });
      load();
    } catch (err) {
      toast.error(err.response?.data?.error || 'Failed to add table');
    }
  };

  const changeStatus = async (id, status, tableNum) => {
    try {
      await tableApi.updateStatus(id, status);
      toast.success(`Table ${tableNum} marked as ${STATUS_CONFIG[status]?.shortLabel || status}`);
      // Optimistic update for instantaneous feedback
      setTables(prev => prev.map(t => t.tableId === id ? { ...t, status } : t));
    } catch (err) {
      toast.error('Status update failed');
      load();
    }
  };

  // Distinct locations for filter dropdown
  const locations = useMemo(() => {
    const locs = new Set(tables.map(t => t.location).filter(Boolean));
    return Array.from(locs);
  }, [tables]);

  // Counts for summary pills
  const counts = useMemo(() => {
    const res = { ALL: tables.length, AVAILABLE: 0, OCCUPIED: 0, RESERVED: 0, CLEANING: 0 };
    tables.forEach(t => {
      const s = (t.status || 'AVAILABLE').toUpperCase();
      if (res[s] !== undefined) res[s]++;
    });
    return res;
  }, [tables]);

  // Filtered tables
  const filteredTables = useMemo(() => {
    return tables.filter(t => {
      const statusMatch = activeFilter === 'ALL' || (t.status || '').toUpperCase() === activeFilter;
      const locationMatch = selectedLocation === 'ALL' || t.location === selectedLocation;
      const cleanNum = (t.tableNumber || '').toLowerCase();
      const cleanLoc = (t.location || '').toLowerCase();
      const searchMatch = !search || cleanNum.includes(search.toLowerCase()) || cleanLoc.includes(search.toLowerCase());
      return statusMatch && locationMatch && searchMatch;
    });
  }, [tables, activeFilter, selectedLocation, search]);

  if (loading && tables.length === 0) {
    return <div className="page-loading"><div className="spinner" /></div>;
  }

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header">
        <div>
          <h1>Restaurant Tables</h1>
          <p>
            <strong>{counts.AVAILABLE}</strong> of <strong>{tables.length}</strong> tables available (
            <span style={{ color: '#4ade80' }}>{counts.AVAILABLE} Empty</span>,{' '}
            <span style={{ color: '#f87171' }}>{counts.OCCUPIED} Occupied</span>,{' '}
            <span style={{ color: '#fb923c' }}>{counts.RESERVED} Reserved</span>,{' '}
            <span style={{ color: '#38bdf8' }}>{counts.CLEANING} Cleaning</span>
            )
          </p>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button className="btn-ghost" onClick={load} title="Refresh Tables" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <RefreshCw size={14} /> Refresh
          </button>
          <button id="add-table-btn" className="btn-primary" onClick={() => setShowAdd(true)}>
            <Plus size={16} /> Add Table
          </button>
        </div>
      </div>

      {/* Interactive Status Summary Pills / Filter Tabs */}
      <div className="status-summary">
        <button
          className={`status-pill status-pill-all ${activeFilter === 'ALL' ? 'active' : ''}`}
          onClick={() => setActiveFilter('ALL')}
        >
          All Tables ({counts.ALL})
        </button>

        <button
          className={`status-pill status-pill-green ${activeFilter === 'AVAILABLE' ? 'active' : ''}`}
          onClick={() => setActiveFilter('AVAILABLE')}
        >
          <span className="status-dot status-dot-green" />
          Available / Empty ({counts.AVAILABLE})
        </button>

        <button
          className={`status-pill status-pill-red ${activeFilter === 'OCCUPIED' ? 'active' : ''}`}
          onClick={() => setActiveFilter('OCCUPIED')}
        >
          <span className="status-dot status-dot-red" />
          Occupied ({counts.OCCUPIED})
        </button>

        <button
          className={`status-pill status-pill-orange ${activeFilter === 'RESERVED' ? 'active' : ''}`}
          onClick={() => setActiveFilter('RESERVED')}
        >
          <span className="status-dot status-dot-orange" />
          Reserved ({counts.RESERVED})
        </button>

        <button
          className={`status-pill status-pill-blue ${activeFilter === 'CLEANING' ? 'active' : ''}`}
          onClick={() => setActiveFilter('CLEANING')}
        >
          <span className="status-dot status-dot-blue" />
          Cleaning ({counts.CLEANING})
        </button>
      </div>

      {/* Toolbar / Search & Location filter */}
      <div className="toolbar" style={{ marginBottom: '24px' }}>
        <div className="search-wrapper" style={{ flex: 1, maxWidth: '380px' }}>
          <Search size={16} className="search-icon" />
          <input
            className="search-input"
            placeholder="Search by table number or room…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>

        {locations.length > 0 && (
          <select
            className="filter-select"
            value={selectedLocation}
            onChange={e => setSelectedLocation(e.target.value)}
          >
            <option value="ALL">All Dining Areas ({tables.length})</option>
            {locations.map(loc => (
              <option key={loc} value={loc}>
                {loc}
              </option>
            ))}
          </select>
        )}
      </div>

      {/* Tables Grid */}
      {filteredTables.length === 0 ? (
        <div className="empty-box" style={{ padding: '48px', textAlign: 'center' }}>
          <h3>No tables match the selected filter</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '6px' }}>
            {activeFilter !== 'ALL' ? `There are currently no tables marked as ${activeFilter}.` : 'No tables found.'}
          </p>
          {activeFilter !== 'ALL' && (
            <button className="btn-ghost" onClick={() => { setActiveFilter('ALL'); setSelectedLocation('ALL'); setSearch(''); }} style={{ marginTop: '12px' }}>
              View All Tables
            </button>
          )}
        </div>
      ) : (
        <div className="tables-grid">
          {filteredTables.map(t => {
            const statusKey = (t.status || 'AVAILABLE').toUpperCase();
            const cfg = STATUS_CONFIG[statusKey] || STATUS_CONFIG.AVAILABLE;
            const Icon = cfg.icon;
            const displayNum = t.tableNumber?.startsWith('T-')
              ? t.tableNumber
              : (t.tableNumber?.startsWith('T') ? t.tableNumber : `T-${t.tableNumber}`);

            return (
              <div key={t.tableId} className={`table-cell ${cfg.cellClass}`}>
                {/* Header: Tag + Seats */}
                <div className="table-cell-header">
                  <span className={`table-status-tag ${cfg.color}`}>
                    <span className={`status-dot ${cfg.dotClass}`} />
                    <Icon size={12} />
                    {cfg.label}
                  </span>
                  <span style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 600 }}>
                    #{t.tableId}
                  </span>
                </div>

                {/* Table Title */}
                <div className="table-number">{displayNum}</div>

                {/* Meta: Capacity & Location */}
                <div className="table-cap">
                  <Users size={14} style={{ color: 'var(--purple-400)' }} />
                  <span>{t.capacity} Guests Capacity</span>
                </div>
                <div className="table-loc">
                  <MapPin size={13} style={{ color: 'var(--text-secondary)' }} />
                  <span>{t.location || 'Main Floor'}</span>
                </div>

                {/* Quick Status Buttons */}
                <div style={{ marginTop: 'auto' }}>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                    Quick Set Status:
                  </div>
                  <div className="table-quick-actions">
                    <button
                      className={`table-quick-btn ${statusKey === 'AVAILABLE' ? `active ${STATUS_CONFIG.AVAILABLE.activeBtnClass}` : ''}`}
                      onClick={() => changeStatus(t.tableId, 'AVAILABLE', displayNum)}
                      title="Set Available / Empty"
                    >
                      Empty
                    </button>
                    <button
                      className={`table-quick-btn ${statusKey === 'OCCUPIED' ? `active ${STATUS_CONFIG.OCCUPIED.activeBtnClass}` : ''}`}
                      onClick={() => changeStatus(t.tableId, 'OCCUPIED', displayNum)}
                      title="Set Occupied"
                    >
                      Occupied
                    </button>
                    <button
                      className={`table-quick-btn ${statusKey === 'RESERVED' ? `active ${STATUS_CONFIG.RESERVED.activeBtnClass}` : ''}`}
                      onClick={() => changeStatus(t.tableId, 'RESERVED', displayNum)}
                      title="Set Reserved"
                    >
                      Reserved
                    </button>
                    <button
                      className={`table-quick-btn ${statusKey === 'CLEANING' ? `active ${STATUS_CONFIG.CLEANING.activeBtnClass}` : ''}`}
                      onClick={() => changeStatus(t.tableId, 'CLEANING', displayNum)}
                      title="Set Cleaning"
                    >
                      Cleaning
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Add Table Modal */}
      {showAdd && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h2>Add New Table</h2>
              <button onClick={() => setShowAdd(false)}><X size={20} /></button>
            </div>
            <form onSubmit={handleAdd} className="modal-body">
              <div className="form-row">
                <div className="form-group">
                  <label>Table Number / Code</label>
                  <input
                    required
                    value={form.tableNumber}
                    onChange={e => setForm(p => ({ ...p, tableNumber: e.target.value }))}
                    placeholder="e.g. T-11"
                  />
                </div>
                <div className="form-group">
                  <label>Seating Capacity</label>
                  <input
                    type="number"
                    min="1"
                    max="30"
                    required
                    value={form.capacity}
                    onChange={e => setForm(p => ({ ...p, capacity: e.target.value }))}
                    placeholder="4"
                  />
                </div>
              </div>
              <div className="form-group">
                <label>Dining Area / Location</label>
                <input
                  value={form.location}
                  onChange={e => setForm(p => ({ ...p, location: e.target.value }))}
                  placeholder="e.g. Window Bay B, Terrace, VIP Lounge"
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn-ghost" onClick={() => setShowAdd(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Add Table</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
