<template>
  <div class="page-container">
    <van-nav-bar title="个人信息" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSave" style="margin-top: 10px;">
      <van-cell-group inset>
        <van-field v-model="formData.username" name="用户名" label="用户名" readonly />
        <van-field v-model="formData.name" name="昵称" label="昵称" placeholder="请输入昵称" />
        <van-field v-model="formData.phone" name="手机号" label="手机号" placeholder="请输入手机号" />
        <van-field v-model="formData.email" name="邮箱" label="邮箱" placeholder="请输入邮箱" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">保 存</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { reactive, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showSuccessToast, showFailToast } from 'vant';

const $request = inject('$request');
const router = useRouter();
const user = JSON.parse(localStorage.getItem('user') || '{}');
const formData = reactive({});

onMounted(() => {
  // 进入页面时，加载当前用户信息
  Object.assign(formData, user);
});

const onSave = () => {
  $request.put('/user/update', formData).then(res => {
    if (res.code === '200') {
      // 更新成功后，将新信息保存到 localStorage
      localStorage.setItem('user', JSON.stringify(res.data));
      showSuccessToast('保存成功');
      router.back();
    } else {
      showFailToast(res.msg);
    }
  });
};
</script>
