<template>
  <div class="login-container">
    <div class="login-box" :class="{ 'admin-mode': loginType === 'ADMIN' }">
      <div class="login-header">
        <a href="#" class="role-switcher" @click.prevent="toggleLoginType">
          {{ loginType === 'USER' ? '管理员登录' : '返回用户登录' }}
        </a>
        <img src="@/assets/imgs/logo.png" alt="logo" class="logo">

        <div v-if="loginType === 'USER'">
          <h2>停车场管理系统</h2>
          <p class="sub-title">用户登录</p>
        </div>
        <div v-else class="admin-title-container">
          <el-icon :size="32" class="admin-icon"><Setting /></el-icon>
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

          <el-button type="primary" class="login-button" @click="login">
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
import { reactive, ref, watch } from "vue";
import { useRoute, useRouter } from 'vue-router';
import { User, Lock, Setting } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();

const loginType = ref('USER');
const formRef = ref();
const data = reactive({
  form: {},
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
  }
});

watch(() => route.query.loginType, (newTypeQuery) => {
  const newType = newTypeQuery?.toUpperCase() === 'ADMIN' ? 'ADMIN' : 'USER';

  if (loginType.value !== newType) {
    loginType.value = newType;
    if (formRef.value) {
      formRef.value.resetFields();
    }
    data.form = {};
  }
}, { immediate: true });

const toggleLoginType = () => {
  const newType = loginType.value === 'USER' ? 'ADMIN' : 'USER';
  router.replace({
    query: newType === 'ADMIN' ? { loginType: 'ADMIN' } : {}
  });
};

const login = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      data.form.role = loginType.value;
      request.post('/login', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("登录成功");
          localStorage.setItem('xm-user', JSON.stringify(res.data));
          router.replace({ path: '/manager/home', query: {} });
        } else {
          ElMessage.error(res.msg);
        }
      }).catch(error => {
        ElMessage.error("登录失败，请稍后重试");
        console.error(error);
      });
    }
  });
};
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
      margin-bottom: -10px;

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