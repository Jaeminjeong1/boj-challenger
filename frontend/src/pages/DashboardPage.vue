<template>
  <div class="dashboard">
    <section class="grid">
      <div class="card">
        <h2>내 정보</h2>
        <p>핸들: <strong>{{ data?.bojHandle }}</strong></p>
        <p>스트릭: <strong>{{ data?.streak }}</strong>일</p>
      </div>
      <div class="card">
        <h2>오늘 푼 문제</h2>
        <p v-if="data?.todaySolvedCount">총 {{ data?.todaySolvedCount }}문제</p>
        <p v-else>아직 문제를 풀지 않았습니다.</p>
        <ProblemChips :problems="data?.todaySolvedProblems ?? []" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import http from '../api/http';
import ProblemChips from '../components/ProblemChips.vue';

interface DashboardResponse {
  bojHandle: string;
  streak: number;
  todaySolvedProblems: number[];
  todaySolvedCount: number;
}

const data = ref<DashboardResponse | null>(null);

onMounted(async () => {
  const response = await http.get('/me/dashboard');
  data.value = response.data.data;
});
</script>

<style scoped>
.dashboard {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}
</style>
