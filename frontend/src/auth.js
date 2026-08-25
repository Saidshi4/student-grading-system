export function decodeJwt(token) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function getToken() {
  return localStorage.getItem('accessToken') || '';
}

export function setToken(token) {
  if (token) {
    localStorage.setItem('accessToken', token);
  } else {
    localStorage.removeItem('accessToken');
  }
}

export function getSession() {
  const token = getToken();
  const payload = decodeJwt(token);
  return {
    token,
    username: payload?.sub || '',
    role: payload?.role || '',
    userId: payload?.userId || null
  };
}

export function isLoggedIn() {
  return Boolean(getToken());
}
