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
      <van-tabbar-item replace to="/me" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
// 该组件作为布局，通常不需要复杂的脚本逻辑
</script>

<style>
/* 预留出底部导航栏的高度，防止内容被遮挡 */
.page-container {
  padding-bottom: 55px; /* 比tabbar高度稍大一些 */
}
</style>