import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';

const ROLES = ['MANAGER', 'WAITER', 'CASHIER', 'KITCHEN_STAFF'];

export default function RegisterPage() {
  const [form, setForm] = useState({
    fullName: '', username: '', email: '', phone: '', password: '', role: 'WAITER'
  });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await authApi.register(form);
      const { token, username, fullName, role, userId } = res.data;
      login({ username, fullName, role, userId }, token);
      toast.success('Account created! Welcome to RoseWood.');
      navigate('/');
    } catch (err) {
      console.error('Registration error:', err);
      toast.error(err.response?.data?.error || err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const set = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  return (
    <div className="auth-bg">
      <div className="auth-card auth-card-wide">
        <div className="auth-brand">
          <span className="auth-logo">🌹</span>
          <h1>RoseWood</h1>
          <p>Create Staff Account</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <h2>Register</h2>

          <div className="form-row">
            <div className="form-group">
              <label>Full Name</label>
              <input id="reg-fullname" type="text" placeholder="Raj Kumar" value={form.fullName} onChange={set('fullName')} required />
            </div>
            <div className="form-group">
              <label>Username</label>
              <input id="reg-username" type="text" placeholder="rajkumar" value={form.username} onChange={set('username')} required />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Email</label>
              <input id="reg-email" type="email" placeholder="raj@rosewood.com" value={form.email} onChange={set('email')} required />
            </div>
            <div className="form-group">
              <label>Phone</label>
              <input id="reg-phone" type="text" placeholder="9876543210" value={form.phone} onChange={set('phone')} required />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Password</label>
              <input id="reg-password" type="password" placeholder="••••••••" value={form.password} onChange={set('password')} required />
            </div>
            <div className="form-group">
              <label>Role</label>
              <select id="reg-role" value={form.role} onChange={set('role')}>
                {ROLES.map(r => <option key={r} value={r}>{r.replace('_', ' ')}</option>)}
              </select>
            </div>
          </div>

          <button id="reg-btn" type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Creating Account…' : 'Create Account'}
          </button>

          <p className="auth-switch">
            Already have an account? <Link to="/login">Sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
