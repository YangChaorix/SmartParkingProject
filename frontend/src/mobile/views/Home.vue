<template>
  <div class="page-container">
    <van-nav-bar title="智慧停车首页" />

    <!-- 关键数据统计（移除 van-statistic 以避免样式解析问题） -->
    <van-grid :column-num="2" style="margin-top: 10px;">
      <van-grid-item>
        <div style="text-align: center;">
          <div style="font-size: 12px; color: #888;">空闲车位</div>
          <div style="font-size: 22px; font-weight: 600; margin-top: 6px;">{{ dashboard.availableLots }}</div>
        </div>
      </van-grid-item>
      <van-grid-item>
        <div style="text-align: center;">
          <div style="font-size: 12px; color: #888;">总缴费额</div>
          <div style="font-size: 22px; font-weight: 600; margin-top: 6px;">¥{{ dashboard.totalPayment }}</div>
        </div>
      </van-grid-item>
    </van-grid>

    <!-- 系统公告 -->
    <van-cell-group inset title="系统公告" style="margin-top: 15px;">
      <van-notice-bar
          v-if="noticeList.length > 0"
          left-icon="volume-o"
          :text="noticeList[0].title"
          style="margin-bottom: 10px;"
      />
      <template v-if="noticeList.length > 0">
        <van-cell v-for="item in noticeList.slice(0, 3)" :key="item.id" :title="item.title" is-link :to="'/notice/' + item.id" />
      </template>
      <van-empty v-else description="暂无公告信息" />
    </van-cell-group>

    <!-- 停车区域展示 -->
    <van-cell-group inset title="停车区域" style="margin-top: 15px;">
      <template v-if="locations.length > 0">
        <van-cell v-for="loc in locations.slice(0, 3)" :key="loc.id"
                  :title="loc.name"
                  :label="`总数: ${(locationStats[loc.id]?.total)||0}，空闲: ${(locationStats[loc.id]?.free)||0}，占用: ${(locationStats[loc.id]?.occupied)||0}`" />
      </template>
      <van-empty v-else description="暂无停车区域信息" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { showFailToast } from 'vant';

const $request = inject('$request');
const dashboard = ref({ availableLots: 0, totalPayment: 0 });
const noticeList = ref([]);
const locations = ref([]);
const locationStats = ref({});
const user = JSON.parse(localStorage.getItem('user') || '{}');

onMounted(() => {
  // 统计：空闲车位数量
  $request.get('/parkingLot/selectAll', { params: { status: '空闲' } }).then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      dashboard.value.availableLots = res.data.length;
    }
  }).catch(() => {});
  // 统计：总缴费额（汇总 pay 列表的 price）
  $request.get('/pay/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      const total = res.data.reduce((sum, item) => sum + (Number(item.price) || 0), 0);
      dashboard.value.totalPayment = Number(total.toFixed(2));
    }
  }).catch(() => {});
  // 公告
  $request.get('/notice/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      noticeList.value = res.data;
    }
  }).catch(() => {});
  // 停车区域
  $request.get('/location/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      locations.value = res.data;
    } else if (res && res.msg) {
      showFailToast(res.msg);
    }
  }).catch(() => {});
  // 区域车位统计
  $request.get('/parkingLot/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      const stats = {};
      res.data.forEach(lot => {
        const locId = lot.locationId;
        if (!stats[locId]) {
          stats[locId] = { total: 0, free: 0, occupied: 0 };
        }
        stats[locId].total += 1;
        if (lot.status === '空闲') {
          stats[locId].free += 1;
        } else {
          stats[locId].occupied += 1;
        }
      });
      locationStats.value = stats;
    }
  }).catch(() => {});
});
</script>
