<template>
  <div class="page-container">
    <van-nav-bar title="注册新用户" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit" style="margin-top: 20px;">
      <van-cell-group inset>
        <van-field
            v-model="form.email"
            name="邮箱"
            label="邮箱"
            placeholder="请输入邮箱"
            :rules="[{ required: true, message: '请填写邮箱' }]"
        />
        <van-field
            v-model="form.password"
            type="password"
            name="密码"
            label="密码"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
        <van-field
            v-model="confirmPassword"
            type="password"
            name="确认密码"
            label="确认密码"
            placeholder="请再次输入密码"
            :rules="[{ required: true, message: '请确认密码' }]"
        />
        <van-field
            v-model="form.verifyCode"
            name="邮箱验证码"
            label="邮箱验证码"
            placeholder="请输入邮箱验证码"
            :rules="[{ required: true, message: '请填写邮箱验证码' }]"
        >
          <template #button>
            <van-button size="small" type="primary" :disabled="counting" @click="sendVerifyCode">
              {{ counting ? countdown + 's' : '获取验证码' }}
            </van-button>
          </template>
        </van-field>
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">注 册</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { reactive, ref, inject } from 'vue';
import { useRouter } from 'vue-router';
import { showSuccessToast, showFailToast } from 'vant';

const $request = inject('$request');
const router = useRouter();
const confirmPassword = ref('');
const form = reactive({
  email: '',
  password: '',
  verifyCode: ''
});

const counting = ref(false);
const countdown = ref(30);
let timer = null;

const sendVerifyCode = () => {
  if (!form.email) {
    showFailToast('请先填写邮箱');
    return;
  }
  if (counting.value) return;
  counting.value = true;
  countdown.value = 30;
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      counting.value = false;
    }
  }, 1000);
  $request.post('/api/sendVerifyCode', { email: form.email }).then(res => {
    if (res.code === '200') {
      showSuccessToast('验证码已发送');
    } else {
      showFailToast(res.msg || '发送失败');
      clearInterval(timer);
      counting.value = false;
      countdown.value = 30;
    }
  }).catch(() => {
    showFailToast('网络异常，请稍后重试');
    clearInterval(timer);
    counting.value = false;
    countdown.value = 30;
  });
};

const onSubmit = () => {
  if (form.password !== confirmPassword.value) {
    showFailToast('两次输入的密码不一致');
    return;
  }
  const finalForm = {
    username: form.email,
    password: form.password,
    verifyCode: form.verifyCode,
    role: 'USER'
  };
  $request.post('/register', finalForm).then(res => {
    if (res.code === '200') {
      showSuccessToast('注册成功，请登录');
      router.push('/login');
    } else {
      showFailToast(res.msg);
    }
  });
};
</script>
