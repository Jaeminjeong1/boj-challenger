import { defineStore } from 'pinia';

const TOKEN_KEY = 'bojchallenge.accessToken';

function readToken(): string {
  if (typeof window === 'undefined') {
    return '';
  }
  return window.localStorage.getItem(TOKEN_KEY) ?? '';
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: readToken()
  }),
  actions: {
    setToken(token: string) {
      this.accessToken = token;
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(TOKEN_KEY, token);
      }
    },
    clearToken() {
      this.accessToken = '';
      if (typeof window !== 'undefined') {
        window.localStorage.removeItem(TOKEN_KEY);
      }
    }
  }
});
