<template>
  <div class="page-container">
    <van-nav-bar title="我的停车信息" />
    <van-tabs v-model:active="activeTab" @change="onTabChange">
      <van-tab title="当前停车"></van-tab>
      <van-tab title="历史记录"></van-tab>
    </van-tabs>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="loadParkingInfo"
      >
        <van-cell v-for="item in list" :key="item.id">
          <van-card
              :desc="`车位号: ${item.parkingLotName}`"
              :title="`车牌号: ${item.vehicleName}`"
          >
            <template #tags>
              <van-tag :type="item.status === '已入场' ? 'primary' : 'success'">
                {{ item.status === '已入场' ? '停车中' : '已离场' }}
              </van-tag>
            </template>
            <template #price>
              <div v-if="item.status === '已出场' && item.price != null">费用: ¥{{ item.price }}</div>
            </template>
            <template #num>
              <div>入场: {{ formatTime(item.startTime) }}</div>
              <div v-if="item.endTime">离场: {{ formatTime(item.endTime) }}</div>
            </template>
          </van-card>
        </van-cell>
      </van-list>
      <van-empty v-if="list.length === 0 && !loading && !refreshing" description="暂无停车记录" />
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';

const $request = inject('$request');
const activeTab = ref(0); // 0: 当前停车, 1: 历史记录
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);
const user = JSON.parse(localStorage.getItem('user') || '{}');

const loadParkingInfo = () => {
  const status = activeTab.value === 0 ? '已入场' : '已出场';
  $request.get('/parking/selectPage', {
    params: { pageNum: pageNum.value, pageSize: 10, userId: user.id, status: status }
  }).then(res => {
    if (res.code === '200') {
      if (refreshing.value) list.value = [];
      const page = res.data || {};
      const rows = page.list || [];
      list.value.push(...rows);
      pageNum.value++;
      loading.value = false;
      refreshing.value = false;
      const total = page.total || 0;
      if (total <= list.value.length) finished.value = true;
    } else {
      loading.value = false;
      refreshing.value = false;
    }
  }).catch(() => {
    loading.value = false;
    refreshing.value = false;
  });
};

const resetAndLoad = () => {
  pageNum.value = 1;
  list.value = [];
  finished.value = false;
  loading.value = true;
  loadParkingInfo();
};

const onTabChange = () => {
  resetAndLoad();
};

const onRefresh = () => {
  refreshing.value = true;
  resetAndLoad();
};

const formatTime = (timeStr) => timeStr?.replace('T', ' ');
</script>
