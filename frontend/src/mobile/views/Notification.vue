<template>
  <div class="page-container">
    <van-nav-bar title="我的通知" left-arrow @click-left="$router.back()" />

    <!-- 通知列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        :immediate-check="false"
        :offset="100"
        @load="loadNotifications"
      >
                <van-cell-group v-if="notificationList.length > 0">
                  <van-cell
                    v-for="item in notificationList"
                    :key="item.id"
                    :class="{ 'unread-notification': item.isRead === 0 }"
                    @click="handleView(item)"
                    is-link
                  >
                    <template #title>
                      <div class="notification-item">
                        <div class="notification-content">
                          <span class="notification-text">
                            {{ item.description }}
                          </span>
                          <van-tag
                            :type="item.isRead === 0 ? 'danger' : 'default'"
                            size="mini"
                            class="status-tag"
                          >
                            {{ item.isRead === 0 ? '未读' : '已读' }}
                          </van-tag>
                        </div>
                        <div class="notification-time">
                          {{ formatDateTime(item.sendTime) }}
                        </div>
                      </div>
                    </template>
                  </van-cell>
                </van-cell-group>
        
        <!-- 空状态 -->
        <van-empty v-else description="暂无通知" />
      </van-list>
    </van-pull-refresh>

    <!-- 通知详情弹窗 -->
    <van-popup v-model:show="showDetail" position="bottom" :style="{ height: '60%' }">
      <div class="notification-detail">
        <div class="detail-header">
          <h3>{{ currentNotification.isRead === 0 ? '新通知' : '通知详情' }}</h3>
          <van-icon name="cross" @click="showDetail = false" />
        </div>
        <div class="detail-content">
          <div class="detail-time">
            <van-icon name="clock-o" />
            <span>{{ formatDateTime(currentNotification.sendTime) }}</span>
          </div>
          <div class="detail-text">
            {{ currentNotification.description }}
          </div>
        </div>
        <div class="detail-actions">
          <van-button 
            v-if="currentNotification.isRead === 0" 
            type="primary" 
            @click="markAsRead"
            :loading="markingRead"
          >
            标记已读
          </van-button>
          <van-button 
            type="danger" 
            @click="deleteNotification"
            :loading="deleting"
          >
            删除通知
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, nextTick } from 'vue';
import { showSuccessToast, showFailToast, showConfirmDialog } from 'vant';

const $request = inject('$request');

// 响应式数据
const notificationList = ref([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const showDetail = ref(false);
const currentNotification = ref({});
const markingRead = ref(false);
const deleting = ref(false);
const isLoading = ref(false); // 防重复加载标志

// 获取用户信息
const user = JSON.parse(localStorage.getItem('user') || '{}');

// 重置分页状态
const resetPagination = () => {
  pageNum.value = 1;
  finished.value = false;
  loading.value = false;
  isLoading.value = false;
  notificationList.value = [];
};

// 下拉刷新
const onRefresh = async () => {
  resetPagination();
  await loadNotifications();
  refreshing.value = false;
};

// 加载通知列表
const loadNotifications = async () => {
  if (finished.value) {
    return;
  }
  
  if (isLoading.value) {
    return;
  }
  
  isLoading.value = true;
  
        try {
          const res = await $request.get('/notification/selectUserPage', {
            params: {
              pageNum: pageNum.value,
              pageSize: pageSize.value
            }
          });
    
    if (res && res.code === '200' && res.data && res.data.list) {
      const newList = res.data.list;
      
      // 如果是第一页，直接替换数据
      if (pageNum.value === 1) {
        notificationList.value = newList;
      } else {
        // 如果不是第一页，追加数据
        notificationList.value.push(...newList);
      }
      
      // 判断是否还有更多数据
      if (newList.length < pageSize.value) {
        finished.value = true;
      } else {
        // 如果还有更多数据，页码递增
        pageNum.value++;
      }
    } else {
      // 如果没有数据，标记为已完成
      if (pageNum.value === 1) {
        notificationList.value = [];
      }
      finished.value = true;
    }
  } catch (error) {
    console.error('加载通知失败:', error);
    showFailToast('加载通知失败');
    finished.value = true;
  } finally {
    // 确保loading状态被重置，让van-list知道加载完成
    loading.value = false;
    isLoading.value = false;
  }
};

// 查看通知详情
const handleView = async (item) => {
  currentNotification.value = item;
  showDetail.value = true;
  
  // 如果是未读通知，自动标记为已读
  if (item.isRead === 0) {
    await markAsRead();
  }
};

// 标记为已读
const markAsRead = async () => {
  if (markingRead.value) return;
  
  markingRead.value = true;
  try {
    const res = await $request.post(`/notification/markAsRead/${currentNotification.value.id}`);
    if (res && res.code === '200') {
      currentNotification.value.isRead = 1;
      // 更新列表中的状态
      const index = notificationList.value.findIndex(item => item.id === currentNotification.value.id);
      if (index !== -1) {
        notificationList.value[index].isRead = 1;
      }
      showSuccessToast('已标记为已读');
    }
  } catch (error) {
    showFailToast('标记已读失败');
  } finally {
    markingRead.value = false;
  }
};

// 删除通知
const deleteNotification = async () => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这条通知吗？',
    });
    
    deleting.value = true;
    const res = await $request.post(`/notification/delete/${currentNotification.value.id}`);
    if (res && res.code === '200') {
      showSuccessToast('删除成功');
      showDetail.value = false;
      // 从列表中移除
      const index = notificationList.value.findIndex(item => item.id === currentNotification.value.id);
      if (index !== -1) {
        notificationList.value.splice(index, 1);
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      showFailToast('删除失败');
    }
  } finally {
    deleting.value = false;
  }
};

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-';
  const date = new Date(dateTime);
  const now = new Date();
  const diff = now - date;
  
  // 如果是今天
  if (diff < 24 * 60 * 60 * 1000 && date.getDate() === now.getDate()) {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `今天 ${hours}:${minutes}`;
  }
  
  // 如果是昨天
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);
  if (date.getDate() === yesterday.getDate() && 
      date.getMonth() === yesterday.getMonth() && 
      date.getFullYear() === yesterday.getFullYear()) {
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `昨天 ${hours}:${minutes}`;
  }
  
  // 其他情况显示完整日期
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

onMounted(async () => {
  // 等待DOM完全渲染后再加载数据
  await nextTick();
  loadNotifications();
});
</script>

<style scoped>
.page-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.notification-item {
  padding: 4px 0;
  width: 100%;
  box-sizing: border-box;
}

.notification-content {
  display: flex;
  align-items: flex-start;
  margin-bottom: 6px;
  gap: 8px;
  width: 100%;
}

.notification-text {
  flex: 1;
  font-size: 15px;
  line-height: 1.4;
  color: #323233;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
  width: 0; /* 强制flex收缩 */
}

.status-tag {
  font-size: 11px;
  flex-shrink: 0;
  margin-top: 1px;
}

.notification-time {
  font-size: 12px;
  color: #969799;
  margin-top: 2px;
}

.unread-notification {
  background-color: #f7f8fa;
}

.notification-detail {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebedf0;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.detail-content {
  flex: 1;
  margin-bottom: 20px;
}

.detail-time {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #969799;
  font-size: 14px;
}

.detail-time .van-icon {
  margin-right: 6px;
}

.detail-text {
  font-size: 16px;
  line-height: 1.6;
  color: #323233;
  white-space: pre-wrap;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

.detail-actions .van-button {
  flex: 1;
}
</style>
