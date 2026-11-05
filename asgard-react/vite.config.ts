import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// /api goes to Quarkus, so the browser stays on one origin during development.
// The port is fixed: the slides embed http://localhost:5173, so fail rather than move to another one.
export default defineConfig({
  plugins: [react()],
  server: { port: 5174, strictPort: true, proxy: { '/api': 'http://localhost:8080' } },
});
