<template>
  <div class="page-container">
    <van-nav-bar title="我的缴费记录" />
    

    <!-- 筛选条件 -->
    <div class="filter-container">
      <van-cell-group>
        <van-field
            v-model="searchKeyword"
            placeholder="搜索订单号"
            left-icon="search"
            @input="onSearch"
            @clear="onSearch"
            clearable
        />
      </van-cell-group>
    </div>

    <!-- 缴费记录列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
      >
        <div v-for="item in filteredList" :key="item.id" class="pay-item">
          <div class="pay-card" @click="toggleExpand(item.id)">
            <!-- 卡片头部 - 主要信息 -->
            <div class="pay-header">
              <div class="main-info">
                <div class="vehicle-section">
                  <van-icon name="logistics" />
                  <span class="vehicle-label">车牌号：</span>
                  <span class="vehicle-name">{{ item.vehicleName || '--' }}</span>
                </div>
                <div class="order-section">
                  <span class="order-label">订单号</span>
                  <span class="order-number">{{ item.serialNumber }}</span>
                </div>
                <div class="duration-section">
                  <van-icon name="clock-o" />
                  <span class="duration-label">停车时长：</span>
                  <span class="duration-text">{{ calcDuration(item.startTime, item.endTime) }}</span>
                </div>
              </div>
              <div class="header-right">
                <div class="cost-section">
                  <span class="cost-label">金额：</span>
                  <span class="cost-amount">¥{{ item.price?.toFixed(2) || '0.00' }}</span>
                </div>
                <div class="status-section">
                  <van-tag :type="getStatusType(item.status)" size="small">
                    {{ item.status || '未缴费' }}
                  </van-tag>
                  <van-button 
                    v-if="item.status === '未缴费'"
                    type="primary" 
                    size="mini" 
                    @click.stop="handlePay(item)"
                    :loading="payingItems.includes(item.id)"
                    class="pay-button"
                  >
                    {{ payingItems.includes(item.id) ? '缴费中...' : '缴费' }}
                  </van-button>
                </div>
              </div>
            </div>

            <!-- 展开触发器 -->
            <div class="expand-trigger" @click.stop="toggleExpand(item.id)">
              <span class="expand-text">{{ expandedItems.includes(item.id) ? '收起' : '展开' }}</span>
              <van-icon 
                :name="expandedItems.includes(item.id) ? 'arrow-up' : 'arrow-down'" 
                class="expand-icon"
              />
            </div>

            <!-- 展开的详细信息 -->
            <div v-show="expandedItems.includes(item.id)" class="expanded-content">

              <!-- 停车信息 -->
              <div class="detail-section">
                <div class="section-title">
                  <van-icon name="location-o" />
                  <span>停车信息</span>
                </div>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="label">停车区域</span>
                    <span class="value">{{ item.locationName || '--' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">停车位</span>
                    <span class="value">{{ item.parkingLotName || '--' }}</span>
                  </div>
                </div>
              </div>

              <!-- 时间信息 -->
              <div class="detail-section">
                <div class="section-title">
                  <van-icon name="clock-o" />
                  <span>时间信息</span>
                </div>
                <div class="time-grid">
                  <div class="time-item">
                    <span class="label">入场时间</span>
                    <span class="value">{{ formatTime(item.startTime) }}</span>
                  </div>
                  <div class="time-item" v-if="item.endTime">
                    <span class="label">出场时间</span>
                    <span class="value">{{ formatTime(item.endTime) }}</span>
                  </div>
                  <div class="time-item" v-if="item.payTime">
                    <span class="label">支付时间</span>
                    <span class="value">{{ formatTime(item.payTime) }}</span>
                  </div>
                </div>
              </div>

              <!-- 费用详情 -->
              <div class="detail-section">
                <div class="section-title">
                  <van-icon name="gold-coin-o" />
                  <span>费用详情</span>
                </div>
                <div class="cost-details">
                  <div class="cost-item">
                    <span class="label">停车费用</span>
                    <span class="value cost-highlight">¥{{ item.price?.toFixed(2) || '0.00' }}</span>
                  </div>
                  <div class="cost-item" v-if="item.minutes">
                    <span class="label">停车时长</span>
                    <span class="value">{{ item.minutes }}分钟</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </van-list>
      <van-empty v-if="filteredList.length === 0 && !loading" description="暂无缴费记录" />
    </van-pull-refresh>

  </div>
</template>

<script setup>
import { ref, inject, onMounted, computed } from 'vue';
import { showFailToast, showSuccessToast, showConfirmDialog } from 'vant';

const $request = inject('$request');
const list = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);
const user = JSON.parse(localStorage.getItem('user') || '{}');
const isInitialized = ref(false); // 添加初始化标志

// 搜索
const searchKeyword = ref('');


// 展开状态管理
const expandedItems = ref([]);

// 缴费状态管理
const payingItems = ref([]);

// 直接使用list，筛选在后端进行
const filteredList = computed(() => list.value);



const loadPayInfo = (isRefresh = false) => {
  if (isRefresh) {
    list.value = [];
    pageNum.value = 1;
    finished.value = false;
  }

  // 构建查询参数
  const params = {
    pageNum: pageNum.value,
    pageSize: 5,
  };

  // 添加搜索条件
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.trim();
    // 只搜索订单号
    params.serialNumber = keyword;
  }
  

  $request.get('/pay/selectPage', {
    params: params
  }).then(res => {
    if (res.code === '200') {
      const newData = res.data.list || [];
      if (isRefresh) {
        list.value = newData;
      } else {
        list.value.push(...newData);
      }
      loading.value = false;
      
      if (res.data.total <= list.value.length) {
        finished.value = true;
      } else {
        // 只有在成功获取数据且还有更多数据时才递增页码
        pageNum.value++;
      }
    } else {
      showFailToast(res.msg || '获取缴费记录失败');
      loading.value = false;
    }
  }).catch(error => {
    console.error('获取缴费记录失败:', error);
    showFailToast('获取缴费记录失败');
    loading.value = false;
  });
};

const onLoad = () => {
  // 只有在初始化完成后才允许van-list自动加载
  if (isInitialized.value) {
    loadPayInfo();
  }
};

const onRefresh = () => {
  refreshing.value = true;
  loading.value = true;
  loadPayInfo(true);
};

const onSearch = () => {
  // 重置分页状态
  pageNum.value = 1;
  finished.value = false;
  // 重新加载数据
  loading.value = true;
  loadPayInfo(true);
};


// 切换展开/收起状态
const toggleExpand = (itemId) => {
  const index = expandedItems.value.indexOf(itemId);
  if (index > -1) {
    expandedItems.value.splice(index, 1);
  } else {
    expandedItems.value.push(itemId);
  }
};

// 处理缴费
const handlePay = (item) => {
  showConfirmDialog({
    title: '确认缴费',
    message: `确定要支付停车费用 ¥${item.price?.toFixed(2) || '0.00'} 吗？`,
    confirmButtonText: '确认缴费',
    cancelButtonText: '取消'
  }).then(() => {
    payForItem(item);
  }).catch(() => {
    // 用户取消缴费
  });
};

// 执行缴费
const payForItem = (item) => {
  // 添加到缴费中列表
  payingItems.value.push(item.id);
  
  $request.put('/pay/update', item).then(res => {
    if (res.code === '200') {
      showSuccessToast('缴费成功');
      // 更新本地数据
      const index = list.value.findIndex(payItem => payItem.id === item.id);
      if (index > -1) {
        list.value[index].status = '已缴费';
        list.value[index].payTime = new Date().toISOString();
      }
      // 更新用户余额
      if (res.data && res.data.account !== undefined) {
        const updatedUser = { ...user, account: res.data.account };
        localStorage.setItem('user', JSON.stringify(updatedUser));
      }
    } else {
      showFailToast(res.msg || '缴费失败');
    }
  }).catch(error => {
    console.error('缴费失败:', error);
    showFailToast('缴费失败，请重试');
  }).finally(() => {
    // 从缴费中列表移除
    const index = payingItems.value.indexOf(item.id);
    if (index > -1) {
      payingItems.value.splice(index, 1);
    }
  });
};

const getStatusType = (status) => {
  switch (status) {
    case '已缴费':
      return 'success';
    case '未缴费':
      return 'warning';
    default:
      return 'warning';
  }
};


const formatTime = (timeStr) => {
  if (!timeStr) return '--';
  return timeStr.replace('T', ' ').substring(0, 19);
};

const calcDuration = (start, end) => {
  if (!start || !end) return '未知';
  const startDate = new Date(start);
  const endDate = new Date(end);
  const diff = endDate.getTime() - startDate.getTime();
  
  if (diff < 0) return '未知';
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
  
  let result = '';
  if (days > 0) result += `${days}天`;
  if (hours > 0) result += `${hours}小时`;
  if (minutes > 0) result += `${minutes}分钟`;
  
  return result || '0分钟';
};

onMounted(() => {
  // 初始化时直接加载第一页
  loading.value = true;
  loadPayInfo();
  // 标记初始化完成，允许van-list自动加载
  isInitialized.value = true;
});
</script>

<style scoped>
.page-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}


.filter-container {
  margin: 0 10px 10px 10px;
}

.pay-item {
  margin: 10px;
  padding: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pay-card {
  background: white;
  padding: 0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pay-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.pay-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.vehicle-section {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.vehicle-label {
  font-size: 12px;
  color: #999;
}

.vehicle-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.order-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-label {
  font-size: 12px;
  color: #999;
}

.order-number {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  font-family: 'Courier New', monospace;
}

.duration-section {
  display: flex;
  align-items: center;
  gap: 4px;
}

.duration-label {
  font-size: 12px;
  color: #999;
}

.duration-text {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.status-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.pay-button {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
  min-width: 60px;
}

.cost-section {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.cost-label {
  font-size: 12px;
  color: #999;
}

.cost-amount {
  font-size: 20px;
  font-weight: bold;
  color: #ff4757;
}

/* 展开触发器 */
.expand-trigger {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-top: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.expand-trigger:hover {
  background-color: #e9ecef;
}

.expand-text {
  font-size: 14px;
  color: #666;
}

.expand-icon {
  font-size: 16px;
  color: #999;
  transition: transform 0.3s ease;
}

/* 展开内容 */
.expanded-content {
  padding: 16px;
  background-color: #fafafa;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
    padding-top: 0;
    padding-bottom: 0;
  }
  to {
    opacity: 1;
    max-height: 1000px;
    padding-top: 16px;
    padding-bottom: 16px;
  }
}

.detail-section {
  margin-bottom: 16px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.section-title .van-icon {
  color: #3f7fe6;
}


.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.time-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.cost-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.cost-highlight {
  font-size: 16px;
  font-weight: bold;
  color: #ff4757;
}

.label {
  font-size: 13px;
  color: #666;
}

.value {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

/* 状态标签样式 */
.van-tag--success {
  background-color: #52c41a;
  color: white;
}

.van-tag--warning {
  background-color: #faad14;
  color: white;
}

.van-tag--danger {
  background-color: #ff4d4f;
  color: white;
}

.van-tag--default {
  background-color: #d9d9d9;
  color: #666;
}

/* 空状态样式 */
.van-empty {
  padding: 40px 20px;
}

/* 加载状态样式 */
.van-list__loading {
  padding: 20px;
  text-align: center;
  color: #999;
}
</style>