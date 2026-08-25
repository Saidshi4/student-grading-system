import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const apiPaths = [
  '/auth',
  '/users',
  '/students',
  '/teachers',
  '/groups',
  '/subjects',
  '/semesters',
  '/course-offerings',
  '/class-schedules',
  '/enrollments',
  '/grades',
  '/reports',
  '/roles',
  '/claims',
  '/menu',
  '/role-claim',
  '/device-token',
  '/v3',
  '/swagger-ui'
];

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: Object.fromEntries(
      apiPaths.map((path) => [path, { target: 'http://localhost:8080', changeOrigin: true }])
    )
  }
});
