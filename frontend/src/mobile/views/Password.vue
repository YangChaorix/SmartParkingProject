<template>
  <div class="page-container">
    <van-nav-bar title="修改密码" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSave" style="margin-top: 10px;">
      <van-cell-group inset>
        <van-field v-model="form.password" type="password" name="原密码" label="原密码" placeholder="请输入原密码" :rules="[{ required: true }]" />
        <van-field v-model="form.newPassword" type="password" name="新密码" label="新密码" placeholder="请输入新密码" :rules="[{ required: true }]" />
        <van-field v-model="form.confirmPassword" type="password" name="确认新密码" label="确认新密码" placeholder="请再次输入新密码" :rules="[{ required: true }]" />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">确 定</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { reactive, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showSuccessToast, showFailToast } from 'vant';

const $request = inject('$request');
const router = useRouter();
const user = JSON.parse(localStorage.getItem('user') || '{}');
const form = reactive({
  username: user.username,
  password: '',
  newPassword: '',
  confirmPassword: ''
});

const onSave = () => {
  if (form.newPassword !== form.confirmPassword) {
    showFailToast('两次输入的新密码不一致');
    return;
  }
  $request.put('/updatePassword', form).then(res => {
    if (res.code === '200') {
      showSuccessToast('修改成功，请重新登录');
      // 清除本地登录信息，并跳转到登录页
      localStorage.removeItem('user');
      router.push('/login');
    } else {
      showFailToast(res.msg);
    }
  });
};
</script>
