import { useMemo, useState } from 'react';
import { Navigate, Route, Routes, useNavigate } from 'react-router-dom';
import { api } from './api';
import { getSession, setToken } from './auth';
import { NAV_ITEMS, RESOURCES } from './resources';
import {
  AuthToolsPage,
  ClaimsPage,
  CrudPage,
  Dashboard,
  DeviceTokenPage,
  ForgotPasswordPage,
  Layout,
  Login,
  MatrixPage,
  MenusPage,
  ProfilePage,
  RequireAuth,
  ResetPasswordPage
} from './pages/Pages';

export default function App() {
  const navigate = useNavigate();
  const [lang, setLang] = useState(localStorage.getItem('lang') || 'az');
  const session = getSession();

  function changeLang(next) {
    localStorage.setItem('lang', next);
    setLang(next);
  }

  async function onLogout() {
    try {
      await api('/auth/logout', { method: 'POST', query: { accessToken: session.token } });
    } catch {
      // still clear local session
    }
    setToken('');
    navigate('/login');
  }

  const navItems = useMemo(
    () => NAV_ITEMS.filter((item) => !session.role || item.roles.includes(session.role)),
    [session.role]
  );

  return (
    <Routes>
      <Route path="/login" element={<Login lang={lang} setLang={changeLang} />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage lang={lang} />} />
      <Route path="/reset-password" element={<ResetPasswordPage lang={lang} />} />
      <Route
        element={
          <RequireAuth>
            <Layout lang={lang} setLang={changeLang} session={session} navItems={navItems} onLogout={onLogout} />
          </RequireAuth>
        }
      >
        <Route path="/" element={<Dashboard lang={lang} />} />
        <Route path="/students" element={<CrudPage lang={lang} resource={RESOURCES.students} />} />
        <Route path="/teachers" element={<CrudPage lang={lang} resource={RESOURCES.teachers} />} />
        <Route path="/users" element={<CrudPage lang={lang} resource={RESOURCES.users} />} />
        <Route path="/groups" element={<CrudPage lang={lang} resource={RESOURCES.groups} />} />
        <Route path="/subjects" element={<CrudPage lang={lang} resource={RESOURCES.subjects} />} />
        <Route path="/semesters" element={<CrudPage lang={lang} resource={RESOURCES.semesters} />} />
        <Route path="/course-offerings" element={<CrudPage lang={lang} resource={RESOURCES.courseOfferings} />} />
        <Route path="/class-schedules" element={<CrudPage lang={lang} resource={RESOURCES.classSchedules} />} />
        <Route path="/enrollments" element={<CrudPage lang={lang} resource={RESOURCES.enrollments} />} />
        <Route path="/grades" element={<CrudPage lang={lang} resource={RESOURCES.grades} />} />
        <Route path="/roles" element={<CrudPage lang={lang} resource={RESOURCES.roles} />} />
        <Route path="/claims" element={<ClaimsPage lang={lang} />} />
        <Route path="/menus" element={<MenusPage lang={lang} />} />
        <Route path="/matrix" element={<MatrixPage lang={lang} />} />
        <Route path="/profile" element={<ProfilePage lang={lang} session={session} />} />
        <Route path="/device-token" element={<DeviceTokenPage lang={lang} />} />
        <Route path="/auth-tools" element={<AuthToolsPage lang={lang} />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
