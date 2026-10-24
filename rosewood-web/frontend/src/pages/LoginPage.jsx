import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

export default function LoginPage() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    setErrorMessage('');
    try {
      const res = await authApi.login(form);
      const { token, username, fullName, role, userId } = res.data;
      login({ username, fullName, role, userId }, token);
      toast.success(`Welcome back, ${fullName}!`);
      navigate('/');
    } catch (err) {
      console.error('Login error:', err);
      const msg = err.response?.data?.error || err.message || 'Login failed. Please check credentials or wait for server startup.';
      setErrorMessage(msg);
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const fillCredentials = (username, password) => {
    setForm({ username, password });
    setErrorMessage('');
  };

  return (
    <div className="auth-bg">
      <div className="auth-card">
        <div className="auth-brand">
          <span className="auth-logo">🌹</span>
          <h1>RoseWood</h1>
          <p>Fine Dining Management</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <h2>Sign In</h2>

          {errorMessage && (
            <div style={{
              background: 'rgba(239, 68, 68, 0.15)',
              border: '1px solid rgba(239, 68, 68, 0.4)',
              color: '#fca5a5',
              padding: '10px 14px',
              borderRadius: '8px',
              fontSize: '0.85rem',
              lineHeight: 1.4,
              marginBottom: '1rem'
            }}>
              <strong>Error: </strong> {errorMessage}
            </div>
          )}

          <div className="form-group">
            <label>Username or Email</label>
            <input
              id="login-username"
              type="text"
              placeholder="Enter username or email"
              value={form.username}
              onChange={e => setForm({ ...form, username: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              id="login-password"
              type="password"
              placeholder="Enter password"
              value={form.password}
              onChange={e => setForm({ ...form, password: e.target.value })}
              required
            />
          </div>

          <button id="login-btn" type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', padding: '0.75rem' }}>
            {loading ? 'Connecting & Signing In…' : 'Sign In'}
          </button>

          <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid var(--border, #334155)' }}>
            <p style={{ fontSize: '0.78rem', color: '#94a3b8', marginBottom: '0.5rem', textAlign: 'center' }}>
              ⚡ Quick Fill Demo Accounts:
            </p>
            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'center', flexWrap: 'wrap' }}>
              <button
                type="button"
                onClick={() => fillCredentials('admin', 'admin123')}
                style={{
                  background: 'rgba(99, 102, 241, 0.15)',
                  border: '1px solid rgba(99, 102, 241, 0.35)',
                  color: '#a5b4fc',
                  padding: '4px 10px',
                  borderRadius: '6px',
                  fontSize: '0.75rem',
                  cursor: 'pointer'
                }}>
                👑 Admin
              </button>
              <button
                type="button"
                onClick={() => fillCredentials('manager', 'manager123')}
                style={{
                  background: 'rgba(16, 185, 129, 0.15)',
                  border: '1px solid rgba(16, 185, 129, 0.35)',
                  color: '#6ee7b7',
                  padding: '4px 10px',
                  borderRadius: '6px',
                  fontSize: '0.75rem',
                  cursor: 'pointer'
                }}>
                👔 Manager
              </button>
              <button
                type="button"
                onClick={() => fillCredentials('waiter1', 'waiter123')}
                style={{
                  background: 'rgba(245, 158, 11, 0.15)',
                  border: '1px solid rgba(245, 158, 11, 0.35)',
                  color: '#fcd34d',
                  padding: '4px 10px',
                  borderRadius: '6px',
                  fontSize: '0.75rem',
                  cursor: 'pointer'
                }}>
                🍽️ Waiter
              </button>
            </div>
          </div>

          <p className="auth-switch" style={{ marginTop: '1.25rem' }}>
            Don't have an account? <Link to="/register">Register</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
