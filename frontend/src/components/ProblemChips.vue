<template>
  <div class="chips" :class="{ empty: !problems.length }">
    <template v-if="problems.length">
      <a
        v-for="problem in problems"
        :key="problem"
        class="chip"
        :href="`https://www.acmicpc.net/problem/${problem}`"
        target="_blank"
        rel="noopener"
      >
        #{{ problem }}
      </a>
    </template>
    <p v-else class="empty-text">아직 안 풀었어요</p>
  </div>
</template>

<script setup lang="ts">
interface Props {
  problems: number[];
}

defineProps<Props>();
</script>

<style scoped>
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.chips.empty {
  justify-content: flex-start;
}

.empty-text {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}
</style>
