<template>
  <div className="page-container">
    <van-nav-bar
        :title="notice.title"
        left-text="返回"
        left-arrow
        @click-left="$router.back()"
    />
    <div className="content-wrapper">
      <div className="title">{{ notice.title }}</div>
      <div className="meta-info">
        <!-- <span>发布人: {{ notice.user }}</span> -->
        <span>发布时间: {{ notice.time }}</span>
      </div>
      <van-divider/>
      <div className="content" v-html="notice.content"></div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, inject} from 'vue';
import {useRoute} from 'vue-router';

const $request = inject('$request');
const route = useRoute();
const notice = ref({});

onMounted(() => {
  const noticeId = route.params.id;
  $request.get(`/notice/selectById/${noticeId}`).then(res => {
    if (res.code === '200') {
      notice.value = res.data;
    }
  });
});
</script>

<style scoped>
.content-wrapper {
  padding: 16px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
}

.meta-info {
  display: flex;
  justify-content: space-around;
  font-size: 12px;
  color: #999;
}

.content {
  margin-top: 15px;
  font-size: 16px;
  line-height: 1.8;
}
</style>
