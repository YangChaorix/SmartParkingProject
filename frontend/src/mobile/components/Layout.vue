<!-- src/mobile/components/Layout.vue -->
<template>
  <div>
    <!-- 页面内容会在这里渲染 -->
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>

    <!-- 底部导航栏 -->
    <van-tabbar route>
      <van-tabbar-item replace to="/home" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item replace to="/parking" icon="records-o">停车</van-tabbar-item>
      <van-tabbar-item replace to="/pay" icon="bill-o">缴费</van-tabbar-item>
      <van-tabbar-item replace to="/me" icon="user-o" :badge="unreadCount > 0 ? unreadCount : ''">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';

const $request = inject('$request');
const unreadCount = ref(0);

// 获取未读通知数量
const getUnreadCount = async () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    if (user && user.id) {
      const res = await $request.get(`/notification/selectUnreadCount/${user.id}`);
      if (res && res.code === '200') {
        unreadCount.value = res.data || 0;
      }
    }
  } catch (error) {
    console.error('获取未读通知数量失败:', error);
  }
};

onMounted(() => {
  getUnreadCount();
});
</script>

<style>
/* 预留出底部导航栏的高度，防止内容被遮挡 */
.page-container {
  padding-bottom: 55px; /* 比tabbar高度稍大一些 */
}
</style>