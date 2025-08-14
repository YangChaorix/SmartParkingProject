<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/imgs/logo.png" alt="logo" class="logo">
        <h2>停车场管理系统</h2>
        <p class="sub-title">用户注册</p>
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
              <el-input v-model="data.form.captchaCode" placeholder="请输入验证码"></el-input>
              <img :src="data.captchaImg" alt="验证码" class="captcha-img" @click="getCaptcha" title="点击刷新">
            </div>
          </el-form-item>

          <el-button type="primary" class="login-button" @click="register">
            注 册
          </el-button>

          <div class="register-link">
            已有账号？<a href="/login">立即登录</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

const data = reactive({
  form: {
    username: '',
    password: '',
    captchaCode: '', // 存储用户输入的验证码
    captchaId: '' // 存储后端返回的验证码ID
  },
  captchaImg: '', // 存储验证码图片的 Base64 数据
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    captchaCode: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
    ]
  }
})

const formRef = ref()

// 获取验证码图片
const getCaptcha = () => {
  request.get('/captcha').then(res => {
    if (res.code === '200' && res.data) {
      data.form.captchaId = res.data.captchaId;
      data.captchaImg = `data:image/png;base64,${res.data.img}`;
    } else {
      ElMessage.error("验证码获取失败");
    }
  }).catch(() => {
    ElMessage.error("验证码获取失败，请检查网络");
  });
};

const register = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      data.form.role = 'USER';

      request.post('/register', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("注册成功");
          setTimeout(() => {
            router.push('/login');
          }, 500);
        } else {
          ElMessage.error(res.msg);
          // 注册失败时，刷新验证码
          getCaptcha();
        }
      });
    }
  });
};

// 组件挂载时自动获取一次验证码
onMounted(() => {
  getCaptcha();
});
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

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(10px);
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

    .login-header {
      text-align: center;
      margin-bottom: 40px;

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
        letter-spacing: -0.5px;
      }

      .sub-title {
        margin: 8px 0 0;
        font-size: 16px;
        color: #64748b;
        font-weight: 500;
      }
    }

    .login-content {
      :deep(.el-input) {
        --el-input-height: 48px;
        margin-bottom: 4px;

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
          padding-left: 12px;
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

      // 新增：验证码区域的样式
      .captcha-wrapper {
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .captcha-img {
        width: 120px;
        height: 48px;
        border-radius: 12px;
        cursor: pointer;
        background-color: #f5f5f5;
        border: 1px solid #dcdfe6;
      }

      .login-button {
        width: 100%;
        height: 48px;
        margin-top: 24px;
        font-size: 16px;
        font-weight: 500;
        letter-spacing: 0.5px;
        border-radius: 12px;
        background: #4f46e5;
        border: none;
        transition: all 0.3s ease;

        &:hover {
          background: #4338ca;
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
        }

        &:active {
          transform: translateY(0);
        }
      }

      .register-link {
        text-align: center;
        margin-top: 20px;
        color: #64748b;
        font-size: 14px;

        a {
          color: #4f46e5;
          text-decoration: none;
          font-weight: 500;
          margin-left: 4px;
          transition: all 0.3s ease;

          &:hover {
            color: #4338ca;
            text-decoration: underline;
          }
        }
      }

      :deep(.el-form-item__error) {
        padding-left: 4px;
        font-size: 13px;
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 480px) {
  .login-container .login-box {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>