<template>
  <div class="page-container">
    <van-nav-bar title="智慧停车首页" />

    <!-- 顶部统计卡片 -->
    <div class="parking-stats-card">
      <div class="stats-header">
        <van-icon name="location-o" size="20" color="#1989fa" />
        <span class="stats-title">停车数据统计</span>
      </div>
      <div class="stats-content">
        <div class="stat-item">
          <div class="stat-label">空闲车位</div>
          <div class="stat-value available">{{ parkingStats.availableLots }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">总车位</div>
          <div class="stat-value total">{{ parkingStats.totalLots }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">停车区域</div>
          <div class="stat-value regions">{{ locations.length }}</div>
        </div>
      </div>
    </div>


    <!-- 系统公告 -->
    <van-cell-group inset title="系统公告" style="margin-top: 15px;">
      <van-notice-bar
          v-if="noticeList.length > 0"
          left-icon="volume-o"
          text="系统公告"
          style="margin-bottom: 10px;"
      />
      <template v-if="noticeList.length > 0">
        <van-cell v-for="item in noticeList" :key="item.id" :title="item.title" is-link :to="'/notice/' + item.id" />
      </template>
      <div v-else class="no-notice">
        <van-icon name="info-o" size="16" color="#999" />
        <span class="no-notice-text">暂无公告信息</span>
      </div>
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
const dashboard = ref({});
const noticeList = ref([]);
const locations = ref([]);
const locationStats = ref({});
const user = JSON.parse(localStorage.getItem('user') || '{}');

// 车位统计数据
const parkingStats = ref({
  totalLots: 0,
  availableLots: 0,
  occupiedLots: 0
});


// 更新车位统计数据
const updateParkingStats = () => {
  $request.get('/parkingLot/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      const totalLots = res.data.length;
      const availableLots = res.data.filter(lot => lot.status === '空闲').length;
      const occupiedLots = totalLots - availableLots;
      
      parkingStats.value = {
        totalLots,
        availableLots,
        occupiedLots
      };
    }
  }).catch(() => {});
};


// 加载所有数据
const loadAllData = () => {
  // 更新车位统计
  updateParkingStats();
  
  // 公告 - 使用分页接口获取前三个，按发布时间倒序
  $request.get('/notice/selectPage', { 
    params: { 
      pageNum: 1, 
      pageSize: 3 
    } 
  }).then(res => {
    if (res && res.code === '200' && res.data && res.data.list) {
      noticeList.value = res.data.list;
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
};

onMounted(() => {
  loadAllData();
  
  // 每30秒自动刷新车位数据
  setInterval(() => {
    updateParkingStats();
  }, 30000);
});
</script>

<style scoped>
.parking-stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 10px 15px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  color: white;
}

.stats-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  margin-left: 8px;
}

.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.stat-value.total {
  color: #ffffff;
}

.stat-value.available {
  color: #52c41a;
}

.stat-value.regions {
  color: #1890ff;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
  margin: 0 8px;
}

.stats-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  opacity: 0.8;
}

.update-time {
  margin-left: 4px;
}

/* 无公告时的样式 */
.no-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

.no-notice-text {
  margin-left: 6px;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .stat-value {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .no-notice {
    padding: 15px;
    font-size: 13px;
  }
}
</style>
