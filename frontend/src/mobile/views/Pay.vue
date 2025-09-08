<template>
  <div class="page-container">
    <van-nav-bar title="我的缴费记录" />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
      >
        <van-cell v-for="item in list" :key="item.id">
          <van-card
              :desc="`车牌: ${item.vehicleName} | 停车时长: ${calcDuration(item.startTime, item.endTime)}`"
              :title="`订单号: ${item.orderId}`"
          >
            <template #price>
              <span style="font-size: 16px; color: red;">¥{{ item.cost }}</span>
            </template>
            <template #num>
              <div>支付时间: {{ formatTime(item.payTime) }}</div>
            </template>
          </van-card>
        </van-cell>
      </van-list>
      <van-empty v-if="list.length === 0 && !loading" description="暂无缴费记录" />
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';

const $request = inject('$request');
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);
const user = JSON.parse(localStorage.getItem('user') || '{}');

const loadPayInfo = () => {
  if (refreshing.value) {
    list.value = [];
    pageNum.value = 1;
    refreshing.value = false;
  }

  $request.get('/pay/selectPage', {
    params: {
      pageNum: pageNum.value,
      pageSize: 10,
      userId: user.id,
    }
  }).then(res => {
    if (res.code === '200') {
      list.value.push(...res.data.list);
      loading.value = false;
      if (res.data.total <= list.value.length) {
        finished.value = true;
      }
      pageNum.value++;
    }
  });
};

const onLoad = () => {
  loadPayInfo();
};

const onRefresh = () => {
  finished.value = false;
  loading.value = true;
  onLoad();
};

const formatTime = (timeStr) => {
  if (!timeStr) return '';
  return timeStr.replace('T', ' ');
}

const calcDuration = (start, end) => {
  if (!start || !end) return '未知';
  const startDate = new Date(start);
  const endDate = new Date(end);
  const diff = endDate.getTime() - startDate.getTime();
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  return `${hours}小时${minutes}分钟`;
}
</script>