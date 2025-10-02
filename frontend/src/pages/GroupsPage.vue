<template>
  <div class="groups-page">
    <div class="card">
      <div class="tabs">
        <button :class="{ active: activeTab === 'create' }" @click="activeTab = 'create'">그룹 생성</button>
        <button :class="{ active: activeTab === 'join' }" @click="activeTab = 'join'">그룹 참가</button>
      </div>
      <form v-if="activeTab === 'create'" @submit.prevent="createGroup" class="form">
        <div class="form-group">
          <label for="group-name">그룹 이름</label>
          <input id="group-name" v-model.trim="createForm.name" required minlength="2" maxlength="40" />
        </div>
        <div class="form-group">
          <label for="group-pin">PIN (4자리)</label>
          <input
            id="group-pin"
            v-model="createForm.pin4"
            @input="onPinInput($event, createForm)"
            required
            pattern="[0-9]{4}"
            maxlength="4"
            inputmode="numeric"
            title="PIN은 숫자 4자리여야 합니다."
          />
        </div>
        <button type="submit">생성</button>
      </form>
      <form v-else @submit.prevent="joinGroup" class="form">
        <div class="form-group">
          <label for="join-pin">그룹 PIN</label>
          <input
            id="join-pin"
            v-model="joinForm.pin4"
            @input="onPinInput($event, joinForm)"
            required
            pattern="[0-9]{4}"
            maxlength="4"
            inputmode="numeric"
            title="PIN은 숫자 4자리여야 합니다."
          />
        </div>
        <button type="submit">참가</button>
      </form>
      <p v-if="message" class="success">{{ message }}</p>
      <p v-if="error" class="error">{{ error }}</p>
    </div>

    <div class="group-list">
      <h2>내 그룹</h2>
      <div class="grid">
        <div v-for="group in groups" :key="group.groupId" class="card group-card" @click="openGroup(group.groupId)">
          <h3>{{ group.name }}</h3>
          <p>방장: {{ group.ownerNickname }}</p>
          <p>멤버: {{ group.memberCount }}명</p>
        </div>
      </div>
      <p v-if="!groups.length" class="empty">가입된 그룹이 없습니다.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import http from '../api/http';

interface GroupCard {
  groupId: number;
  name: string;
  ownerNickname: string;
  memberCount: number;
}

interface PinHolder {
  pin4: string;
}

const router = useRouter();
const activeTab = ref<'create' | 'join'>('create');
const createForm = reactive({ name: '', pin4: '' });
const joinForm = reactive({ pin4: '' });
const groups = ref<GroupCard[]>([]);
const message = ref('');
const error = ref('');

const normalizePin = (value: string) => value.replace(/\D/g, '').slice(0, 4);

const onPinInput = (event: Event, form: PinHolder) => {
  const target = event.target as HTMLInputElement;
  const normalized = normalizePin(target.value);
  if (normalized !== form.pin4) {
    form.pin4 = normalized;
  }
  if (target.value !== normalized) {
    target.value = normalized;
  }
};

const loadGroups = async () => {
  const { data } = await http.get('/groups/my');
  groups.value = data.data ?? [];
};

const createGroup = async () => {
  error.value = '';
  message.value = '';
  if (createForm.pin4.length !== 4) {
    error.value = 'PIN은 숫자 4자리여야 합니다.';
    return;
  }
  try {
    const payload = { name: createForm.name.trim(), pin4: createForm.pin4 };
    const { data } = await http.post('/groups', payload);
    message.value = '그룹이 생성되었습니다.';
    createForm.name = '';
    createForm.pin4 = '';
    await loadGroups();
    router.push(`/groups/${data.data.groupId}`);
  } catch (err: any) {
    const status = err.response?.status;
    const serverMessage: string | undefined = err.response?.data?.message;
    if (status === 409 && serverMessage?.includes('PIN')) {
      error.value = '이미 사용 중인 PIN입니다. 다른 4자리 숫자를 입력해주세요.';
    } else if (status === 409 && serverMessage?.includes('Group name')) {
      error.value = '이미 존재하는 그룹 이름입니다. 다른 이름을 사용해주세요.';
    } else if (status === 400 && serverMessage) {
      error.value = serverMessage;
    } else {
      error.value = '그룹 생성에 실패했습니다.';
    }
  }
};

const joinGroup = async () => {
  error.value = '';
  message.value = '';
  if (joinForm.pin4.length !== 4) {
    error.value = 'PIN은 숫자 4자리여야 합니다.';
    return;
  }
  try {
    const payload = { pin4: joinForm.pin4 };
    await http.post('/groups/join', payload);
    message.value = '그룹에 참가했습니다.';
    joinForm.pin4 = '';
    await loadGroups();
  } catch (err: any) {
    error.value = err.response?.data?.message || '그룹 참가에 실패했습니다.';
  }
};

const openGroup = (id: number) => {
  router.push(`/groups/${id}`);
};

onMounted(() => {
  loadGroups();
});
</script>

<style scoped>
.groups-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 960px;
  margin: 0 auto;
}
.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.tabs button {
  flex: 1;
  padding: 10px;
  background: #e5e7eb;
  border: none;
  cursor: pointer;
}
.tabs button.active {
  background: #38bdf8;
  color: #ffffff;
}
.group-list .grid {
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}
.group-card {
  cursor: pointer;
  transition: transform 0.2s ease;
}
.group-card:hover {
  transform: translateY(-4px);
}
.success {
  color: #16a34a;
}
.error {
  color: #ef4444;
}
.empty {
  color: #6b7280;
}
</style>
