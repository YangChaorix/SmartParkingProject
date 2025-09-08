<template>
  <div class="page-container" style="background: #f7f8fa; min-height: 100vh;">
    <!-- 用户信息头部 -->
    <div style="background-color: #3f7fe6; padding: 20px; color: white; display: flex; align-items: center;">
      <van-image round width="60px" height="60px" :src="user.avatar || '/src/assets/imgs/avatar.png'" />
      <div style="margin-left: 15px;">
        <div style="font-size: 18px; font-weight: bold;">{{ user.name || user.username }}</div>
        <div style="font-size: 14px; margin-top: 5px;">账户余额: ¥{{ user.account?.toFixed(2) }}</div>
      </div>
    </div>

    <!-- 功能列表 -->
    <van-cell-group inset style="margin-top: 15px;">
      <van-cell title="个人信息" icon="user-circle-o" is-link to="/person" />
      <van-cell title="我的车辆" icon="logistics" is-link to="/vehicle" />
      <van-cell title="修改密码" icon="setting-o" is-link to="/password" />
    </van-cell-group>

    <van-cell-group inset style="margin-top: 15px;">
      <van-cell title="模拟充值" icon="gold-coin-o" is-link @click="showRecharge = true" />
    </van-cell-group>

    <div style="padding: 20px;">
      <van-button type="danger" block round @click="logout">退出登录</van-button>
    </div>

    <!-- 充值弹窗 -->
    <van-dialog v-model:show="showRecharge" title="用户充值" show-cancel-button @confirm="handleRecharge">
      <van-field v-model="rechargeAmount" type="number" label="充值金额" placeholder="请输入充值金额" />
    </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showSuccessToast, showFailToast, showConfirmDialog } from 'vant';

const router = useRouter();
const $request = inject('$request');
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'));
const showRecharge = ref(false);
const rechargeAmount = ref('');

const logout = () => {
  showConfirmDialog({ title: '提示', message: '您确定要退出登录吗？' })
      .then(() => {
        localStorage.removeItem('user');
        router.push('/login');
      });
};

const handleRecharge = () => {
  if(!rechargeAmount.value || rechargeAmount.value <= 0) {
    showFailToast('请输入正确的充值金额');
    return;
  }
  const updatedUser = { ...user.value, account: parseFloat(user.value.account) + parseFloat(rechargeAmount.value) };

  $request.put('/user/update', updatedUser).then(res => {
    if(res.code === '200') {
      showSuccessToast('充值成功');
      localStorage.setItem('user', JSON.stringify(res.data));
      user.value = res.data;
      rechargeAmount.value = '';
    } else {
      showFailToast(res.msg);
    }
  });
};

// 每次激活"我的"页面时，都重新从服务器获取最新的用户信息
onMounted(() => {
  $request.get(`/user/selectById/${user.value.id}`).then(res => {
    if (res.code === '200') {
      localStorage.setItem('user', JSON.stringify(res.data));
      user.value = res.data;
    }
  });
});
</script>