import { useEffect, useState } from 'react';
import { Link, Navigate, Outlet, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { setToken } from '../auth';
import { t } from '../i18n';

export function Login({ lang, setLang }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin');
  const [error, setError] = useState('');

  async function onSubmit(e) {
    e.preventDefault();
    setError('');
    try {
      const data = await api('/auth/login', { method: 'POST', body: { username, password } });
      setToken(data.token);
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login">
      <div className="lang">
        <span>{t(lang, 'language')}</span>
        <select value={lang} onChange={(e) => setLang(e.target.value)}>
          <option value="az">AZ</option>
          <option value="en">EN</option>
        </select>
      </div>
      <h2>{t(lang, 'login')}</h2>
      <p className="hint">{t(lang, 'loginHint')}</p>
      <form onSubmit={onSubmit}>
        <div className="field">
          <label>{t(lang, 'username')}</label>
          <input value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <div className="field">
          <label>{t(lang, 'password')}</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        {error && <div className="error">{error}</div>}
        <button type="submit">{t(lang, 'login')}</button>
      </form>
      <p><Link to="/forgot-password">{t(lang, 'forgotPassword')}</Link></p>
    </div>
  );
}

export function RequireAuth({ children }) {
  const token = localStorage.getItem('accessToken');
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

export function Layout({ lang, setLang, session, navItems, onLogout }) {
  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>{t(lang, 'appName')}</h1>
        {navItems.map((item) => (
          <Link key={item.path} to={item.path}>{t(lang, item.key)}</Link>
        ))}
      </aside>
      <section className="main">
        <div className="topbar">
          <div>{t(lang, 'loggedInAs')}: {session.username} ({session.role})</div>
          <div className="lang">
            <select value={lang} onChange={(e) => setLang(e.target.value)}>
              <option value="az">AZ</option>
              <option value="en">EN</option>
            </select>
            <button onClick={onLogout}>{t(lang, 'logout')}</button>
          </div>
        </div>
        <Outlet />
      </section>
    </div>
  );
}

export function Dashboard({ lang }) {
  const [counts, setCounts] = useState({});
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      setError('');
      const endpoints = [
        ['students', '/students'],
        ['teachers', '/teachers'],
        ['groups', '/groups'],
        ['subjects', '/subjects'],
        ['semesters', '/semesters'],
        ['courseOfferings', '/course-offerings'],
        ['classSchedules', '/class-schedules'],
        ['enrollments', '/enrollments'],
        ['grades', '/grades']
      ];
      const next = {};
      for (const [key, path] of endpoints) {
        try {
          const data = await api(path);
          next[key] = Array.isArray(data) ? data.length : '-';
        } catch (err) {
          next[key] = err.message;
        }
      }
      setCounts(next);
    }
    load();
  }, []);

  return (
    <div className="card">
      <h2>{t(lang, 'dashboard')}</h2>
      {error && <div className="error">{error}</div>}
      <table>
        <tbody>
          {Object.entries(counts).map(([key, value]) => (
            <tr key={key}>
              <td>{t(lang, key)}</td>
              <td>{value}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function CrudPage({ lang, resource }) {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [filters, setFilters] = useState({});
  const [idValue, setIdValue] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  function setField(name, value) {
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function payload(isCreate) {
    const body = {};
    resource.fields.forEach((field) => {
      if (!isCreate && field.createOnly) return;
      let value = form[field.name];
      if (value === '' || value === undefined) return;
      if (field.type === 'number') value = Number(value);
      body[field.name] = value;
    });
    if (!isCreate && resource.updateInBodyId && editingId) {
      body.id = Number(editingId);
    }
    return body;
  }

  async function loadList() {
    setError('');
    setOk('');
    try {
      const data = await api(resource.path, { query: resource.filters ? filters : undefined });
      setRows(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => {
    loadList();
  }, [resource.path]);

  async function onCreate(e) {
    e.preventDefault();
    setError('');
    setOk('');
    try {
      await api(resource.path, { method: 'POST', body: payload(true) });
      setForm({});
      setOk(t(lang, 'success'));
      loadList();
    } catch (err) {
      setError(err.message);
    }
  }

  async function onUpdate(e) {
    e.preventDefault();
    setError('');
    setOk('');
    try {
      const method = 'PUT';
      const path = resource.updateInBodyId ? resource.path : `${resource.path}/${editingId}`;
      await api(path, { method, body: payload(false) });
      setEditingId(null);
      setForm({});
      setOk(t(lang, 'success'));
      loadList();
    } catch (err) {
      setError(err.message);
    }
  }

  async function onDelete(id) {
    setError('');
    setOk('');
    try {
      await api(`${resource.path}/${id}`, { method: 'DELETE' });
      setOk(t(lang, 'success'));
      loadList();
    } catch (err) {
      setError(err.message);
    }
  }

  async function onGetById() {
    setError('');
    setOk('');
    try {
      const data = await api(`${resource.path}/${idValue}`);
      setRows([data]);
    } catch (err) {
      setError(err.message);
    }
  }

  function startEdit(row) {
    setEditingId(row.id);
    const next = {};
    resource.fields.forEach((field) => {
      if (field.createOnly) return;
      next[field.name] = row[field.name] ?? '';
    });
    setForm(next);
  }

  return (
    <div>
      <div className="card">
        <h2>{t(lang, resource.titleKey)}</h2>
        {error && <div className="error">{error}</div>}
        {ok && <div className="ok">{ok}</div>}
        {resource.filters && (
          <div className="row">
            {resource.filters.map((name) => (
              <div className="field" key={name}>
                <label>{t(lang, name)}</label>
                <input value={filters[name] || ''} onChange={(e) => setFilters((p) => ({ ...p, [name]: e.target.value }))} />
              </div>
            ))}
            <button onClick={loadList}>{t(lang, 'load')}</button>
          </div>
        )}
        <div className="row">
          <div className="field">
            <label>{t(lang, 'getById')}</label>
            <input value={idValue} onChange={(e) => setIdValue(e.target.value)} />
          </div>
          <button onClick={onGetById}>{t(lang, 'load')}</button>
          <button onClick={loadList}>{t(lang, 'list')}</button>
        </div>
        <form onSubmit={editingId ? onUpdate : onCreate}>
          <div className="row">
            {resource.fields.filter((f) => editingId ? !f.createOnly : true).map((field) => (
              <div className="field" key={field.name}>
                <label>{t(lang, field.name)}</label>
                {field.type === 'select' ? (
                  <select value={form[field.name] || ''} onChange={(e) => setField(field.name, e.target.value)}>
                    <option value=""></option>
                    {field.options.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
                  </select>
                ) : (
                  <input type={field.type} value={form[field.name] ?? ''} onChange={(e) => setField(field.name, e.target.value)} />
                )}
              </div>
            ))}
            <button type="submit">{editingId ? t(lang, 'update') : t(lang, 'create')}</button>
            {editingId && <button type="button" onClick={() => { setEditingId(null); setForm({}); }}>{t(lang, 'cancel')}</button>}
          </div>
        </form>
      </div>
      <div className="card">
        {rows.length === 0 ? <div>{t(lang, 'empty')}</div> : (
          <table>
            <thead>
              <tr>
                {resource.columns.map((col) => <th key={col}>{t(lang, col)}</th>)}
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.id || JSON.stringify(row)}>
                  {resource.columns.map((col) => <td key={col}>{String(row[col] ?? '')}</td>)}
                  <td>
                    <button onClick={() => startEdit(row)}>{t(lang, 'edit')}</button>
                    <button onClick={() => onDelete(row.id)}>{t(lang, 'delete')}</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export function ClaimsPage({ lang }) {
  const [name, setName] = useState('');
  const [result, setResult] = useState('');
  const [error, setError] = useState('');

  async function onSubmit(e) {
    e.preventDefault();
    setError('');
    try {
      const id = await api('/claims', { method: 'POST', body: { name } });
      setResult(String(id));
      setName('');
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="card">
      <h2>{t(lang, 'claims')}</h2>
      <form onSubmit={onSubmit} className="row">
        <div className="field">
          <label>{t(lang, 'name')}</label>
          <input value={name} onChange={(e) => setName(e.target.value)} />
        </div>
        <button type="submit">{t(lang, 'create')}</button>
      </form>
      {error && <div className="error">{error}</div>}
      {result && <div className="ok">{t(lang, 'id')}: {result}</div>}
    </div>
  );
}

export function MenusPage({ lang }) {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({ name: '', icon: '', path: '', claimId: '' });
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function load() {
    setError('');
    try {
      const data = await api('/menu');
      setRows(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  async function onSubmit(e) {
    e.preventDefault();
    setError('');
    setOk('');
    try {
      await api('/menu', {
        method: 'POST',
        body: { ...form, claimId: form.claimId ? Number(form.claimId) : null }
      });
      setOk(t(lang, 'success'));
      setForm({ name: '', icon: '', path: '', claimId: '' });
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div>
      <div className="card">
        <h2>{t(lang, 'menus')}</h2>
        {error && <div className="error">{error}</div>}
        {ok && <div className="ok">{ok}</div>}
        <form onSubmit={onSubmit} className="row">
          {['name', 'icon', 'path', 'claimId'].map((name) => (
            <div className="field" key={name}>
              <label>{t(lang, name)}</label>
              <input value={form[name]} onChange={(e) => setForm((p) => ({ ...p, [name]: e.target.value }))} />
            </div>
          ))}
          <button type="submit">{t(lang, 'create')}</button>
          <button type="button" onClick={load}>{t(lang, 'load')}</button>
        </form>
      </div>
      <div className="card">
        {rows.length === 0 ? <div>{t(lang, 'empty')}</div> : (
          <table>
            <thead>
              <tr>
                {['id', 'name', 'icon', 'path', 'isVisible', 'createdAt', 'updatedAt'].map((col) => <th key={col}>{t(lang, col)}</th>)}
              </tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.id}>
                  <td>{row.id}</td>
                  <td>{row.name}</td>
                  <td>{row.icon}</td>
                  <td>{row.path}</td>
                  <td>{String(row.isVisible)}</td>
                  <td>{row.createdAt}</td>
                  <td>{row.updatedAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export function MatrixPage({ lang }) {
  const [matrix, setMatrix] = useState(null);
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function load() {
    setError('');
    try {
      setMatrix(await api('/role-claim/matrix'));
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, []);

  function hasPermission(roleId, claimId) {
    return Boolean(matrix?.rolesClaims?.find((rc) => rc.roleId === roleId && rc.claimId === claimId && rc.hasPermission));
  }

  async function toggle(roleId, claimId, checked) {
    setError('');
    setOk('');
    try {
      await api('/role-claim', {
        method: 'PUT',
        body: [{ roleId, claimId, hasPermission: checked }]
      });
      setOk(t(lang, 'success'));
      load();
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="card">
      <h2>{t(lang, 'matrix')}</h2>
      {error && <div className="error">{error}</div>}
      {ok && <div className="ok">{ok}</div>}
      <button onClick={load}>{t(lang, 'load')}</button>
      {!matrix ? <div>{t(lang, 'empty')}</div> : (
        <table>
          <thead>
            <tr>
              <th>{t(lang, 'role')}</th>
              {(matrix.claims || []).map((claim) => <th key={claim.id}>{claim.name}</th>)}
            </tr>
          </thead>
          <tbody>
            {(matrix.roles || []).map((role) => (
              <tr key={role.id}>
                <td>{role.name}</td>
                {(matrix.claims || []).map((claim) => (
                  <td key={claim.id}>
                    <input
                      type="checkbox"
                      checked={hasPermission(role.id, claim.id)}
                      onChange={(e) => toggle(role.id, claim.id, e.target.checked)}
                    />
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export function ProfilePage({ lang, session }) {
  const [user, setUser] = useState(null);
  const [file, setFile] = useState(null);
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function load() {
    setError('');
    try {
      if (session.userId) {
        setUser(await api(`/users/${session.userId}`));
      }
    } catch (err) {
      setError(err.message);
    }
  }

  useEffect(() => { load(); }, [session.userId]);

  async function changeImage(e) {
    e.preventDefault();
    setError('');
    setOk('');
    try {
      const formData = new FormData();
      formData.append('image', file);
      setUser(await api('/users/change-image', { method: 'PATCH', formData }));
      setOk(t(lang, 'success'));
    } catch (err) {
      setError(err.message);
    }
  }

  async function deleteImage() {
    setError('');
    setOk('');
    try {
      setUser(await api('/users/delete-image', { method: 'DELETE' }));
      setOk(t(lang, 'success'));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="card">
      <h2>{t(lang, 'profile')}</h2>
      {error && <div className="error">{error}</div>}
      {ok && <div className="ok">{ok}</div>}
      <pre>{JSON.stringify(session, null, 2)}</pre>
      {user && <pre>{JSON.stringify(user, null, 2)}</pre>}
      <form onSubmit={changeImage} className="row">
        <input type="file" onChange={(e) => setFile(e.target.files[0])} />
        <button type="submit">{t(lang, 'changeImage')}</button>
        <button type="button" onClick={deleteImage}>{t(lang, 'deleteImage')}</button>
      </form>
    </div>
  );
}

export function DeviceTokenPage({ lang }) {
  const [token, setDeviceToken] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function registerToken() {
    setError('');
    setOk('');
    try {
      await api('/device-token', { method: 'POST', body: { token } });
      setOk(t(lang, 'success'));
    } catch (err) {
      setError(err.message);
    }
  }

  async function removeToken() {
    setError('');
    setOk('');
    try {
      await api('/device-token', { method: 'DELETE', body: { token } });
      setOk(t(lang, 'success'));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="card">
      <h2>{t(lang, 'deviceToken')}</h2>
      {error && <div className="error">{error}</div>}
      {ok && <div className="ok">{ok}</div>}
      <div className="field">
        <label>{t(lang, 'token')}</label>
        <input value={token} onChange={(e) => setDeviceToken(e.target.value)} />
      </div>
      <div className="row">
        <button onClick={registerToken}>{t(lang, 'registerToken')}</button>
        <button onClick={removeToken}>{t(lang, 'removeToken')}</button>
      </div>
    </div>
  );
}

export function AuthToolsPage({ lang }) {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [entranceType, setEntranceType] = useState('LOGIN');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');

  async function run(fn) {
    setError('');
    setOk('');
    try {
      const result = await fn();
      setOk(typeof result === 'string' ? result : JSON.stringify(result || t(lang, 'success')));
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="card">
      <h2>{t(lang, 'authTools')}</h2>
      {error && <div className="error">{error}</div>}
      {ok && <div className="ok">{ok}</div>}
      <div className="row">
        <div className="field">
          <label>{t(lang, 'email')}</label>
          <input value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="field">
          <label>{t(lang, 'otp')}</label>
          <input value={otp} onChange={(e) => setOtp(e.target.value)} />
        </div>
        <div className="field">
          <label>{t(lang, 'entranceType')}</label>
          <select value={entranceType} onChange={(e) => setEntranceType(e.target.value)}>
            <option>LOGIN</option>
            <option>SIGNUP</option>
          </select>
        </div>
        <div className="field">
          <label>{t(lang, 'token')}</label>
          <input value={resetToken} onChange={(e) => setResetToken(e.target.value)} />
        </div>
        <div className="field">
          <label>{t(lang, 'newPassword')}</label>
          <input value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
        </div>
      </div>
      <div className="row">
        <button onClick={() => run(() => api('/auth/forgot-password', { method: 'POST', query: { email } }))}>{t(lang, 'forgotPassword')}</button>
        <button onClick={() => run(() => api('/auth/verify/otp', { method: 'POST', body: { email, otp, entranceType } }))}>{t(lang, 'verifyOtp')}</button>
        <button onClick={() => run(() => api('/auth/resend/otp', { method: 'POST', body: { email, entranceType } }))}>{t(lang, 'resendOtp')}</button>
        <button onClick={() => run(() => api('/auth/reset-password', { method: 'POST', body: { token: resetToken, newPassword } }))}>{t(lang, 'resetPassword')}</button>
        <button onClick={() => run(async () => {
          const data = await api('/auth/refresh/token', { method: 'POST' });
          if (data?.token) setToken(data.token);
          return data;
        })}>{t(lang, 'refreshToken')}</button>
      </div>
    </div>
  );
}

export function ForgotPasswordPage({ lang }) {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');
  return (
    <div className="login">
      <h2>{t(lang, 'forgotPassword')}</h2>
      <form onSubmit={async (e) => {
        e.preventDefault();
        setError('');
        try {
          const data = await api('/auth/forgot-password', { method: 'POST', query: { email } });
          setOk(data?.message || t(lang, 'success'));
        } catch (err) {
          setError(err.message);
        }
      }}>
        <div className="field">
          <label>{t(lang, 'email')}</label>
          <input value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        {error && <div className="error">{error}</div>}
        {ok && <div className="ok">{ok}</div>}
        <button type="submit">{t(lang, 'submit')}</button>
      </form>
      <p><Link to="/login">{t(lang, 'login')}</Link></p>
    </div>
  );
}

export function ResetPasswordPage({ lang }) {
  const [token, setResetToken] = useState(new URLSearchParams(window.location.search).get('token') || '');
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState('');
  const [ok, setOk] = useState('');
  const navigate = useNavigate();
  return (
    <div className="login">
      <h2>{t(lang, 'resetPassword')}</h2>
      <form onSubmit={async (e) => {
        e.preventDefault();
        setError('');
        try {
          const data = await api('/auth/reset-password', { method: 'POST', body: { token, newPassword } });
          if (data?.token) setToken(data.token);
          setOk(t(lang, 'success'));
          navigate('/');
        } catch (err) {
          setError(err.message);
        }
      }}>
        <div className="field">
          <label>{t(lang, 'token')}</label>
          <input value={token} onChange={(e) => setResetToken(e.target.value)} />
        </div>
        <div className="field">
          <label>{t(lang, 'newPassword')}</label>
          <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
        </div>
        {error && <div className="error">{error}</div>}
        {ok && <div className="ok">{ok}</div>}
        <button type="submit">{t(lang, 'submit')}</button>
      </form>
    </div>
  );
}
