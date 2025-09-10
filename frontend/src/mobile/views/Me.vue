<template>
  <div class="page-container">
    <van-nav-bar title="我的" />

    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div class="user-avatar-section">
        <van-uploader
            :after-read="handleAvatarUpload"
            :before-read="beforeRead"
            accept="image/*"
            :max-count="1"
            :show-upload="false"
        >
          <van-image round width="60px" height="60px" :src="user.avatar || '/src/assets/imgs/avatar.png'" />
        </van-uploader>
        <div class="user-details">
          <div class="user-name">{{ user.name || user.username }}</div>
          <div class="user-id">ID: {{ user.id }}</div>
        </div>
      </div>
      <div class="balance-section">
        <div class="balance-label">账户余额</div>
        <div class="balance-amount">¥{{ user.account?.toFixed(2) || '0.00' }}</div>
        <van-button type="primary" size="mini" round @click="showRecharge = true">
          <van-icon name="plus" />
          充值
        </van-button>
      </div>
    </div>

    <!-- 功能列表 -->
    <van-cell-group inset style="margin-top: 15px;">
      <van-cell title="个人信息" icon="user-circle-o" is-link to="/person" />
      <van-cell title="我的车辆" icon="logistics" is-link to="/vehicle" />
      <van-cell title="修改密码" icon="setting-o" is-link to="/password" />
    </van-cell-group>

    <van-cell-group inset style="margin-top: 15px;">
      <van-cell
        title="我的通知" 
        icon="bell-o" 
        is-link 
        :to="'/notification'"
      >
        <template #right-icon>
          <van-icon name="arrow" />
        </template>
      </van-cell>
    </van-cell-group>

    <van-cell-group inset style="margin-top: 15px;">
      <van-cell title="缴费记录" icon="records" is-link @click="goToPayHistory" />
    </van-cell-group>

    <div style="padding: 20px;">
      <van-button type="danger" block round @click="logout">退出登录</van-button>
    </div>

    <!-- 充值弹窗 -->
    <van-dialog v-model:show="showRecharge" title="用户充值" show-cancel-button @confirm="handleRecharge">
      <div class="recharge-content">
        <van-field v-model="rechargeAmount" type="number" label="充值金额" placeholder="请输入充值金额" />
        
        <!-- 支付方式选择 -->
        <div class="payment-methods">
          <div class="payment-title">选择支付方式</div>
          <div class="payment-options">
            <div 
              class="payment-option" 
              :class="{ active: selectedPaymentMethod === 'wechat' }"
              @click="selectPaymentMethod('wechat')"
            >
              <div class="payment-icon wechat-icon">
                <van-icon name="wechat" size="24" />
              </div>
              <div class="payment-name">微信支付</div>
            </div>
            <div 
              class="payment-option" 
              :class="{ active: selectedPaymentMethod === 'alipay' }"
              @click="selectPaymentMethod('alipay')"
            >
              <div class="payment-icon alipay-icon">
                <van-icon name="alipay" size="24" />
              </div>
              <div class="payment-name">支付宝</div>
            </div>
          </div>
        </div>
      </div>
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
const selectedPaymentMethod = ref('');

const logout = () => {
  showConfirmDialog({ title: '提示', message: '您确定要退出登录吗？' })
      .then(() => {
        localStorage.removeItem('user');
        router.push('/login');
      });
};

const goToPayHistory = () => {
  router.push('/pay');
};

const beforeRead = (file) => {
  if (file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/jpg') {
    showFailToast('请选择 JPG/PNG 格式的图片');
    return false;
  }
  if (file.size > 2 * 1024 * 1024) {
    showFailToast('图片大小不能超过 2MB');
    return false;
  }
  return true;
};

const handleAvatarUpload = (file) => {
  console.log('上传文件对象:', file); // 调试用
  
  // 确保正确获取文件对象
  const fileObj = file.file || file;
  if (!fileObj) {
    showFailToast('文件获取失败');
    return;
  }
  
  const formData = new FormData();
  formData.append('file', fileObj);
  
  // 使用axios直接上传，不设置Content-Type让浏览器自动设置
  $request.post('/files/upload', formData).then(res => {
    console.log('上传响应:', res); // 调试用
    if (res.code === '200') {
      // 更新用户头像到后端
      const updatedUser = { ...user.value, avatar: res.data };
      $request.put('/user/update', updatedUser).then(updateRes => {
        if (updateRes.code === '200') {
          showSuccessToast('头像更新成功');
          localStorage.setItem('user', JSON.stringify(updateRes.data));
          user.value = updateRes.data;
        } else {
          showFailToast(updateRes.msg || '头像更新失败');
        }
      }).catch(error => {
        console.error('用户信息更新失败:', error);
        showFailToast('头像更新失败，请重试');
      });
    } else {
      showFailToast(res.msg || '头像上传失败');
    }
  }).catch(error => {
    console.error('头像上传失败:', error);
    showFailToast('头像上传失败，请重试');
  });
};

const selectPaymentMethod = (method) => {
  selectedPaymentMethod.value = method;
};

const handleRecharge = () => {
  if(!rechargeAmount.value || rechargeAmount.value <= 0) {
    showFailToast('请输入正确的充值金额');
    return;
  }
  
  if(!selectedPaymentMethod.value) {
    showFailToast('请选择支付方式');
    return;
  }
  
  // 模拟支付过程
  showSuccessToast(`正在使用${selectedPaymentMethod.value === 'wechat' ? '微信' : '支付宝'}支付...`);
  
  // 延迟1秒模拟支付过程
  setTimeout(() => {
    const updatedUser = { ...user.value, account: parseFloat(user.value.account) + parseFloat(rechargeAmount.value) };

    $request.put('/user/update', updatedUser).then(res => {
      if(res.code === '200') {
        showSuccessToast('充值成功');
        localStorage.setItem('user', JSON.stringify(res.data));
        user.value = res.data;
        rechargeAmount.value = '';
        selectedPaymentMethod.value = '';
        showRecharge.value = false;
      } else {
        showFailToast(res.msg);
      }
    }).catch(error => {
      console.error('充值请求失败:', error);
      showFailToast('充值失败，请重试');
    });
  }, 1000);
};


// 每次激活"我的"页面时，都重新从服务器获取最新的用户信息
onMounted(() => {
  $request.get(`/user/selectById/${user.value.id}`).then(res => {
    if (res.code === '200') {
      localStorage.setItem('user', JSON.stringify(res.data));
      user.value = res.data;
    }
  }).catch(error => {
    console.error('获取用户信息失败:', error);
    showFailToast('获取用户信息失败');
  });
});
</script>

<style scoped>
.page-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.user-info-card {
  background: linear-gradient(135deg, #1989fa 0%, #36a3f7 100%);
  margin: 15px;
  border-radius: 12px;
  padding: 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.3);
}

.user-avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.user-details {
  margin-left: 15px;
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-id {
  font-size: 12px;
  opacity: 0.8;
}

/* 充值弹窗样式 */
.recharge-content {
  padding: 20px;
}

.payment-methods {
  margin-top: 20px;
}

.payment-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.payment-options {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.payment-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
  background: white;
}

.payment-option:hover {
  border-color: #1989fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.15);
}

.payment-option.active {
  border-color: #1989fa;
  background: linear-gradient(135deg, #1989fa 0%, #36a3f7 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 137, 250, 0.3);
}

.payment-icon {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
}

.payment-option.active .payment-icon {
  background: rgba(255, 255, 255, 0.3);
}

.wechat-icon {
  color: #07c160;
}

.payment-option.active .wechat-icon {
  color: white;
}

.alipay-icon {
  color: #1677ff;
}

.payment-option.active .alipay-icon {
  color: white;
}

.payment-name {
  font-size: 14px;
  font-weight: 500;
  text-align: center;
}

.balance-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
}

.balance-amount {
  font-size: 24px;
  font-weight: bold;
  margin: 0 10px;
}

.notification-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.unread-count-tag {
  margin-left: 8px;
  font-size: 11px;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .balance-amount {
    font-size: 20px;
  }
  
  .user-name {
    font-size: 16px;
  }
  
  .user-info-card {
    margin: 10px;
    padding: 15px;
  }
}
</style>