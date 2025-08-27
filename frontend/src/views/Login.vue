<template>
  <div class="login-container">
    <div class="login-box" :class="{ 'admin-mode': loginType === 'ADMIN' }">
      <div class="login-header">
        <a href="#" class="role-switcher" @click.prevent="toggleLoginType">
          {{ loginType === 'USER' ? '管理员登录' : '返回用户登录' }}
        </a>
        <img :src="getLogoSrc" alt="logo" class="logo">

        <div v-if="loginType === 'USER'">
          <h2>停车场管理系统</h2>
          <p class="sub-title">用户登录</p>
        </div>
        <div v-else class="admin-title-container">
          <h2>管理后台</h2>
          <p class="sub-title">管理员登录</p>
        </div>
      </div>

      <div class="login-content">
        <el-form :model="data.form" ref="formRef" :rules="data.rules">
          <el-form-item prop="username">
            <el-input :prefix-icon="User" v-model="data.form.username" placeholder="请输入账号" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input :prefix-icon="Lock" v-model="data.form.password" placeholder="请输入密码" show-password />
          </el-form-item>

          <el-form-item prop="captchaCode">
            <div class="captcha-wrapper">
              <el-input v-model="data.form.captchaCode" placeholder="请输入验证码" class="captcha-input"></el-input>
              <img v-if="data.captchaImg" :src="data.captchaImg" alt="验证码" class="captcha-image" @click="getCaptcha">
              <div v-else class="captcha-loading" @click="getCaptcha">加载中...</div>
            </div>
          </el-form-item>

          <el-button
              type="primary"
              class="login-button"
              @click="login"
              :disabled="!data.form.captchaId"
          >
            登 录
          </el-button>

          <div class="register-link" v-if="loginType === 'USER'">
            还没有账号？<a href="/register">立即注册</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch, onBeforeMount, computed } from "vue";
import { useRoute, useRouter } from 'vue-router';
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import logo from '@/assets/imgs/logo.png';
import adminLogo from '@/assets/imgs/logo-admin.png';

const route = useRoute();
const router = useRouter();

const loginType = ref('USER');
const formRef = ref();
const data = reactive({
  form: {
    captchaId: '',
    captchaCode: ''
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    captchaCode: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { min: 4, max: 4, message: '验证码为4位', trigger: 'blur' }
    ]
  },
  captchaImg: ''
});

// 获取验证码
const getCaptcha = () => {
  request.get('/api/captcha').then(res => {
    if (res.code === '200') {
      data.captchaImg = 'data:image/jpeg;base64,' + res.data.img;
      data.form.captchaId = res.data.captchaId;
    } else {
      ElMessage.error(res.msg);
    }
  }).catch(error => {
    ElMessage.error("获取验证码失败");
    console.error(error);
  });
};

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const loginForm = { ...data.form, role: loginType.value };
      request.post('/login', loginForm).then(res => {
        if (res.code === '200') {
          ElMessage.success("登录成功");
          localStorage.setItem('xm-user', JSON.stringify(res.data));
          router.replace({ path: '/manager/home', query: {} });
        } else {
          ElMessage.error(res.msg);
          getCaptcha();
        }
      }).catch(error => {
        ElMessage.error("登录失败，请稍后重试");
        console.error(error);
        getCaptcha();
      });
    }
  });
};

const toggleLoginType = () => {
  const newType = loginType.value === 'USER' ? 'ADMIN' : 'USER';
  router.replace({
    query: newType === 'ADMIN' ? { loginType: 'ADMIN' } : {}
  });
};

const getLogoSrc = computed(() => {
  return loginType.value === 'USER' ? logo : adminLogo;
});


// 将 watch 和 onBeforeMount 放到函数的声明之后
onBeforeMount(() => {
  getCaptcha();
});

watch(() => route.query.loginType, (newTypeQuery) => {
  const newType = newTypeQuery?.toUpperCase() === 'ADMIN' ? 'ADMIN' : 'USER';

  if (loginType.value !== newType) {
    loginType.value = newType;
    if (formRef.value) {
      formRef.value.resetFields();
    }
    data.form = {};
    getCaptcha();
  }
}, { immediate: true });
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/imgs/bg.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  transition: background-color 0.5s ease;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(10px);
    transition: background-color 0.5s ease;
  }
}

.login-box {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.4s ease-in-out;

  .login-header {
    text-align: center;
    margin-bottom: 40px;
    position: relative;

    .role-switcher {
      position: absolute;
      top: -15px;
      right: -15px;
      padding: 6px 12px;
      font-size: 13px;
      color: #4f46e5;
      background-color: rgba(255, 255, 255, 0.9);
      border-radius: 20px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      &:hover {
        background-color: #ffffff;
        color: #4338ca;
        transform: translateY(-2px);
      }
    }

    .logo {
      width: 64px;
      height: 64px;
      margin-bottom: 20px;
      border-radius: 16px;
    }

    h2 {
      margin: 0;
      font-size: 28px;
      color: #1e293b;
      font-weight: 600;
    }

    .sub-title {
      margin: 8px 0 0;
      font-size: 16px;
      color: #64748b;
      font-weight: 500;
    }

    .admin-title-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 10px;

      .admin-icon {
        color: #334155;
      }
    }
  }

  .login-content {
    :deep(.el-input) {
      --el-input-height: 48px;

      .el-input__wrapper {
        background: rgba(255, 255, 255, 0.9);
        border: none;
        border-radius: 12px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.95);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }

        &.is-focus {
          background: #ffffff;
          box-shadow: 0 4px 16px rgba(79, 70, 229, 0.15);
        }
      }

      .el-input__prefix {
        font-size: 18px;
        color: #64748b;
      }

      input {
        height: 48px;
        font-size: 15px;

        &::placeholder {
          color: #94a3b8;
        }
      }
    }

    .captcha-wrapper {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .captcha-input {
      flex: 1;
    }

    .captcha-image {
      width: 120px;
      height: 48px;
      cursor: pointer;
      border-radius: 12px;
      border: 1px solid #dcdfe6;
    }

    .captcha-loading {
      width: 120px;
      height: 48px;
      line-height: 48px;
      text-align: center;
      color: #94a3b8;
      font-size: 14px;
      background-color: #f0f2f5;
      border-radius: 12px;
      border: 1px dashed #dcdfe6;
      cursor: pointer;
    }

    .login-button {
      width: 100%;
      height: 48px;
      margin-top: 24px;
      font-size: 16px;
      border-radius: 12px;
      background: #4f46e5;
      border: none;
      transition: all 0.3s ease;

      &:hover {
        background: #4338ca;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
      }

      &:disabled {
        background: #c6c6c6;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
      }
    }

    .register-link {
      text-align: center;
      margin-top: 20px;
      font-size: 14px;
      color: #64748b;

      a {
        color: #4f46e5;
        text-decoration: none;
        font-weight: 500;
        transition: all 0.3s ease;

        &:hover {
          color: #4338ca;
          text-decoration: underline;
        }
      }
    }
  }
}

.login-box.admin-mode {
  border-color: rgba(71, 85, 105, 0.3);
  background: rgba(241, 245, 249, 0.85);

  .role-switcher {
    color: #475569;
    background-color: rgba(255, 255, 255, 0.95);
    &:hover {
      color: #1e293b;
      background: #fff;
    }
  }

  .login-content {
    :deep(.el-input .el-input__wrapper.is-focus) {
      box-shadow: 0 4px 16px rgba(71, 85, 105, 0.2);
    }

    .login-button {
      background: #475569;
      &:hover {
        background: #334155;
        box-shadow: 0 4px 12px rgba(71, 85, 105, 0.25);
      }
      &:disabled {
        background: #c6c6c6;
        cursor: not-allowed;
        transform: none;
        box-shadow: none;
      }
    }
  }
}

@media (max-width: 480px) {
  .login-container .login-box {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>