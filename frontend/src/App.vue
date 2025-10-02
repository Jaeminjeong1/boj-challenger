<template>
  <div class="app-shell">
    <header class="app-header">
      <router-link to="/dashboard" class="brand">1일 1백준</router-link>
      <nav class="nav-links">
        <template v-if="isAuthed">
          <router-link to="/dashboard">대시보드</router-link>
          <router-link to="/groups">그룹</router-link>
          <button type="button" class="logout" @click="logout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/login">로그인</router-link>
          <router-link to="/signup">가입</router-link>
        </template>
      </nav>
    </header>
    <main class="view-container">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from './stores/auth';

const auth = useAuthStore();
const router = useRouter();
const isAuthed = computed(() => Boolean(auth.accessToken));

const logout = () => {
  auth.clearToken();
  router.push('/login');
};
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: #1f2937;
  color: #ffffff;
}
.brand {
  font-size: 20px;
  font-weight: 700;
  color: #38bdf8;
  text-decoration: none;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
}
.nav-links a {
  color: #ffffff;
  text-decoration: none;
  font-weight: 500;
}
.logout {
  background: #ef4444;
  color: #ffffff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}
.view-container {
  flex: 1;
  padding: 24px;
}
</style>
