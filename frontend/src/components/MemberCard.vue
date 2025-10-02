<template>
  <div class="card member-card" :class="{ punished: member.punished }">
    <div class="header">
      <div class="title">
        <h3>{{ member.nickname }}</h3>
        <span v-if="member.owner" class="owner">방장</span>
      </div>
      <span v-if="member.punished" class="badge">벌칙 대상</span>
    </div>
    <p>오늘 푼 문제: <strong>{{ member.todaySolvedCount }}</strong>개</p>
    <ProblemChips :problems="member.todaySolvedProblems" />
  </div>
</template>

<script setup lang="ts">
import ProblemChips from './ProblemChips.vue';

interface MemberInfo {
  userId: number;
  nickname: string;
  todaySolvedCount: number;
  todaySolvedProblems: number[];
  punished: boolean;
  owner: boolean;
}

defineProps<{ member: MemberInfo }>();
</script>

<style scoped>
.member-card {
  border: 1px solid transparent;
  transition: border-color 0.2s ease;
}
.member-card.punished {
  border-color: #ef4444;
  background: #fee2e2;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.title {
  display: flex;
  align-items: center;
  gap: 8px;
}
.owner {
  background: #38bdf8;
  color: #ffffff;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
}
</style>
