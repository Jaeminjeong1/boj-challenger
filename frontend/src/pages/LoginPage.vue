<template>
  <div class="page-center">
    <div class="card login-card">
      <h2>로그인</h2>
      <form @submit.prevent="onSubmit" class="form">
        <div class="form-group">
          <label for="email">이메일</label>
          <input id="email" v-model="email" type="email" required placeholder="you@example.com" />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input id="password" v-model="password" type="password" required minlength="8" />
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <div class="form-actions">
          <button type="submit" :disabled="loading">{{ loading ? '로그인 중...' : '로그인' }}</button>
          <router-link to="/signup">회원가입</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import http from '../api/http';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const route = useRoute();
const auth = useAuthStore();
const email = ref('');
const password = ref('');
const loading = ref(false);
const error = ref('');

const onSubmit = async () => {
  error.value = '';
  loading.value = true;
  try {
    const { data } = await http.post('/auth/login', { email: email.value, password: password.value });
    auth.setToken(data.data.accessToken);
    const redirect = (route.query.redirect as string) || '/dashboard';
    router.push(redirect);
  } catch (err: any) {
    error.value = err.response?.data?.message || '로그인에 실패했습니다.';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.page-center {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 80px);
}
.login-card {
  width: 360px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.error {
  color: #ef4444;
  font-weight: 600;
}
</style>
