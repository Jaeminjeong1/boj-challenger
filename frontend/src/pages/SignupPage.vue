<template>
  <div class="page-center">
    <div class="card signup-card">
      <h2>회원가입</h2>
      <form @submit.prevent="onSubmit" class="form">
        <div class="form-group">
          <label for="email">이메일</label>
          <input id="email" v-model="form.email" type="email" required placeholder="you@example.com" />
        </div>
        <div class="form-group">
          <label for="password">비밀번호</label>
          <input id="password" v-model="form.password" type="password" required minlength="8" />
        </div>
        <div class="form-group">
          <label for="handle">BOJ 핸들</label>
          <input id="handle" v-model="form.bojHandle" type="text" required pattern="^[A-Za-z0-9_]{3,20}$" />
        </div>
        <div v-if="message" :class="{ error: isError, success: !isError }">{{ message }}</div>
        <div class="form-actions">
          <button type="submit" :disabled="loading">{{ loading ? '처리 중...' : '가입하기' }}</button>
          <router-link to="/login">로그인</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import http from '../api/http';
import { useRouter } from 'vue-router';

const router = useRouter();
const form = reactive({ email: '', password: '', bojHandle: '' });
const loading = ref(false);
const message = ref('');
const isError = ref(false);

const onSubmit = async () => {
  loading.value = true;
  message.value = '';
  try {
    await http.post('/auth/signup', form);
    isError.value = false;
    message.value = '가입이 완료되었습니다. 로그인 해주세요.';
    setTimeout(() => router.push('/login'), 1000);
  } catch (err: any) {
    isError.value = true;
    message.value = err.response?.data?.message || '가입에 실패했습니다.';
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
.signup-card {
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.success {
  color: #16a34a;
  font-weight: 600;
}
.error {
  color: #ef4444;
  font-weight: 600;
}
</style>
