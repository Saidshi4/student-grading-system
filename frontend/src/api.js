import { getToken } from './auth';

async function parseBody(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

function errorMessage(body, status) {
  if (!body) return `HTTP ${status}`;
  if (typeof body === 'string') return body;
  return body.message || body.error || JSON.stringify(body);
}

export async function api(path, { method = 'GET', body, formData, query } = {}) {
  const url = new URL(path, window.location.origin);
  if (query) {
    Object.entries(query).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.set(key, value);
      }
    });
  }

  const headers = {};
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  if (!formData && body !== undefined) headers['Content-Type'] = 'application/json';

  const res = await fetch(url.pathname + url.search, {
    method,
    headers,
    credentials: 'include',
    body: formData ? formData : body !== undefined ? JSON.stringify(body) : undefined
  });

  const data = await parseBody(res);
  if (!res.ok) {
    throw new Error(errorMessage(data, res.status));
  }
  return data;
}
