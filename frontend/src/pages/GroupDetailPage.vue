<template>
  <div class="group-detail" v-if="group">
    <section class="card hero">
      <div class="hero-text">
        <p class="hero-subtitle">그룹 대시보드</p>
        <h1 class="hero-title">{{ group.groupName }}</h1>
        <p class="hero-owner">그룹장 {{ group.ownerNickname }}</p>
      </div>
      <div class="hero-actions">
        <p class="hero-updated">마지막 새로고침 · {{ lastRefreshedText }}</p>
        <button type="button" class="refresh-btn" @click="refresh" :disabled="refreshing">
          {{ refreshing ? '새로고침 중...' : '새로고침' }}
        </button>
      </div>
    </section>

    <section class="card stats">
      <div class="stat">
        <span class="label">그룹원 수</span>
        <span class="value">{{ memberCount }}명</span>
      </div>
      <div class="divider" aria-hidden="true"></div>
      <div class="stat">
        <span class="label">벌칙 대상</span>
        <span class="value" :class="{ danger: punishedMembers.length }">
          {{ punishedMembers.length ? punishedMembers.length + '명' : '없음' }}
        </span>
      </div>
      <div class="divider" aria-hidden="true"></div>
      <div class="stat">
        <span class="label">총 풀이 수(오늘)</span>
        <span class="value">{{ totalSolved }}개</span>
      </div>
      <div class="divider" aria-hidden="true"></div>
      <div class="stat">
        <span class="label">오늘의 MVP</span>
        <span class="value">{{ topSolverLabel }}</span>
      </div>
    </section>

    <section class="two-column">
      <article class="card highlight">
        <header class="section-header">
          <h2>벌칙 현황</h2>
          <span class="subtitle">오늘 기준</span>
        </header>
        <div class="punishment-list" v-if="punishedMembers.length">
          <div v-for="member in punishedMembers" :key="member.userId" class="punishment-item">
            <span class="name">{{ member.nickname }}</span>
            <span class="reason">오늘 목표 미달</span>
          </div>
        </div>
        <p v-else class="empty">벌칙 대상이 없습니다. 모두 열심히 하고 있어요!</p>
      </article>

      <article class="card highlight">
        <header class="section-header">
          <h2>오늘 푼 문제</h2>
          <span class="subtitle">최근 풀이 현황</span>
        </header>
        <div class="today-grid">
          <div v-for="member in group.members" :key="`today-${member.userId}`" class="today-item">
            <div class="today-title">
              <span class="nickname">{{ member.nickname }}</span>
              <span class="count">{{ member.todaySolvedCount }}개</span>
            </div>
            <ProblemChips :problems="member.todaySolvedProblems" />
          </div>
        </div>
      </article>
    </section>

    <section class="card members-card">
      <header class="section-header">
        <h2>그룹원 목록</h2>
        <span class="subtitle">누가 어떻게 하고 있는지 한눈에 확인하세요</span>
      </header>
      <div class="members-grid">
        <MemberCard v-for="member in group.members" :key="member.userId" :member="member" />
      </div>
    </section>
  </div>
  <p v-else class="empty">그룹 정보를 불러오는 중입니다...</p>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import http from '../api/http';
import MemberCard from '../components/MemberCard.vue';
import ProblemChips from '../components/ProblemChips.vue';

interface Member {
  userId: number;
  nickname: string;
  todaySolvedCount: number;
  todaySolvedProblems: number[];
  punished: boolean;
  owner: boolean;
}

interface GroupDashboard {
  groupId: number;
  groupName: string;
  ownerNickname: string;
  members: Member[];
}

const route = useRoute();
const group = ref<GroupDashboard | null>(null);
const refreshing = ref(false);
const lastRefreshedAt = ref<Date | null>(null);

const memberCount = computed(() => group.value?.members.length ?? 0);
const punishedMembers = computed(() => group.value?.members.filter((member) => member.punished) ?? []);
const totalSolved = computed(() =>
  group.value?.members.reduce((sum, member) => sum + member.todaySolvedCount, 0) ?? 0
);
const topSolver = computed(() => {
  if (!group.value || !group.value.members.length) return null;
  return group.value.members.reduce((best, member) => {
    if (!best) return member;
    if (member.todaySolvedCount > best.todaySolvedCount) return member;
    if (member.todaySolvedCount === best.todaySolvedCount && member.todaySolvedCount > 0) {
      return member.nickname.localeCompare(best.nickname, 'ko') < 0 ? member : best;
    }
    return best;
  }, null as Member | null);
});
const topSolverLabel = computed(() => {
  if (!topSolver.value || topSolver.value.todaySolvedCount === 0) return '아직 없음';
  return `${topSolver.value.nickname} · ${topSolver.value.todaySolvedCount}개`;
});
const lastRefreshedText = computed(() => {
  if (!lastRefreshedAt.value) return '아직 새로고침 전';
  return lastRefreshedAt.value.toLocaleString('ko-KR', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  });
});

const updateRefreshTimestamp = () => {
  lastRefreshedAt.value = new Date();
};

const load = async () => {
  const id = Number(route.params.id);
  const { data } = await http.get(`/groups/${id}`);
  group.value = data.data;
  updateRefreshTimestamp();
};

const refresh = async () => {
  if (!group.value) return;
  refreshing.value = true;
  try {
    const { data } = await http.post(`/groups/${group.value.groupId}/refresh`);
    group.value = data.data;
    updateRefreshTimestamp();
  } finally {
    refreshing.value = false;
  }
};

onMounted(load);
watch(() => route.params.id, load);
</script>

<style scoped>
.group-detail {
  max-width: 960px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding-bottom: 40px;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  background: linear-gradient(135deg, #1d4ed8, #38bdf8);
  color: #ffffff;
}

.hero-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.hero-subtitle {
  font-weight: 600;
  opacity: 0.85;
}

.hero-title {
  margin: 0;
  font-size: 28px;
}

.hero-owner {
  margin: 0;
  font-weight: 500;
  opacity: 0.85;
}

.hero-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}

.hero-updated {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.refresh-btn {
  background: #ffffff;
  color: #1d4ed8;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease;
}

.refresh-btn:disabled {
  opacity: 0.7;
  cursor: progress;
}

.refresh-btn:not(:disabled):hover {
  transform: translateY(-1px);
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  align-items: center;
  gap: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 600;
}

.value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.value.danger {
  color: #ef4444;
}

.divider {
  width: 1px;
  height: 36px;
  background: #e5e7eb;
  justify-self: center;
}

.two-column {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 16px;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
}

.subtitle {
  font-size: 13px;
  color: #6b7280;
}

.highlight {
  min-height: 220px;
}

.punishment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.punishment-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 10px;
  background: #fee2e2;
  color: #b91c1c;
  font-weight: 600;
}

.punishment-item .reason {
  font-size: 13px;
  font-weight: 500;
  opacity: 0.8;
}

.today-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.today-item {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
  background: #f8fafc;
}

.today-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  margin-bottom: 8px;
}

.nickname {
  color: #0f172a;
}

.count {
  color: #2563eb;
}

.members-card {
  padding-top: 24px;
}

.members-grid {
  display: grid;
  gap: 20px;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
}

.empty {
  text-align: center;
  color: #6b7280;
}

@media (max-width: 640px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    align-items: flex-start;
  }

  .divider {
    display: none;
  }
}
</style>
