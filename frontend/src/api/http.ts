import axios from 'axios';
import router from '../router';
import { useAuthStore } from '../stores/auth';

const TOKEN_KEY = 'bojchallenge.accessToken';

function safeAuthStore() {
  try {
    return useAuthStore();
  } catch (error) {
    return null;
  }
}

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const store = safeAuthStore();
  const token = store?.accessToken || (typeof window !== 'undefined' ? window.localStorage.getItem(TOKEN_KEY) : '');
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const store = safeAuthStore();
      store?.clearToken();
      if (typeof window !== 'undefined') {
        window.localStorage.removeItem(TOKEN_KEY);
      }
      router.push({ path: '/login' });
    }
    return Promise.reject(error);
  }
);

export default http;
