<template>
  <div class="page-container">
    <van-nav-bar title="登录智慧停车系统" />
    <div style="text-align: center; margin-top: 40px; margin-bottom: 20px;">
      <van-image round width="8rem" height="8rem" src="/src/assets/imgs/logo.png" />
    </div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
            v-model="form.username"
            name="用户名"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请填写用户名' }]"
        />
        <van-field
            v-model="form.password"
            type="password"
            name="密码"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
        <!-- 验证码输入框 -->
        <van-field
            v-model="form.captchaCode"
            name="验证码"
            label="验证码"
            placeholder="请输入验证码"
            :rules="[{ required: true, message: '请填写验证码' }]"
        >
          <!-- 验证码图片插槽 -->
          <template #button>
            <img :src="captchaUrl" @click="refreshCaptcha" style="width: 100px; height: 30px; border-radius: 4px; cursor: pointer;" alt="验证码"/>
          </template>
        </van-field>
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">登 录</van-button>
        <div style="margin-top: 10px; text-align: center;">
          <router-link to="/register">还没有账号？前往注册</router-link>
        </div>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showSuccessToast, showFailToast } from 'vant';

const $request = inject('$request');
const router = useRouter();
const captchaUrl = ref('');

// 1. 统一表单数据结构
const form = reactive({
  username: '',
  password: '',
  role: 'USER',
  captchaId: '',   // 修改：与PC端保持一致
  captchaCode: ''  // 修改：与PC端保持一致
});

// 2. 修正获取验证码的方法
const refreshCaptcha = () => {
  $request.get('/api/captcha').then(res => {
    if (res.code === '200') {
      captchaUrl.value = 'data:image/jpeg;base64,' + res.data.img;
      form.captchaId = res.data.captchaId;
    }
  });
};

// 提交登录的方法
const onSubmit = () => {
  $request.post('/login', form).then(res => {
    if (res.code === '200') {
      localStorage.setItem('user', JSON.stringify(res.data));
      showSuccessToast('登录成功');
      const redirect = router.currentRoute.value.query.redirect;
      router.push(redirect || '/home');
    } else {
      showFailToast(res.msg);
      refreshCaptcha(); // 登录失败后，刷新验证码
    }
  });
};

// 组件加载时，自动获取一次验证码
onMounted(() => {
  refreshCaptcha();
});
</script>

