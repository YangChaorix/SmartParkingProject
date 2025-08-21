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
          <el-form-item prop="email">
            <el-input :prefix-icon="User" v-model="data.form.email" placeholder="请输入您的邮箱" />
          </el-form-item>

          <el-form-item prop="password">
            <el-input :prefix-icon="Lock" v-model="data.form.password" placeholder="请输入密码" show-password />
          </el-form-item>

          <el-form-item prop="verifyCode">
            <div class="code-wrapper">
              <el-input v-model="data.form.verifyCode" placeholder="请输入邮箱验证码" class="code-input"></el-input>
              <el-button @click="sendverifyCode" :disabled="data.isCounting" class="send-code-button">
                {{ data.isCounting ? `${data.countdown}s后重试` : '获取验证码' }}
              </el-button>
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
import { reactive, ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

const data = reactive({
  form: {
    email: '', // 新增：用户邮箱，同时作为用户名
    password: '',
    verifyCode: ''
  },
  rules: {
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    ],
    verifyCode: [
      { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
    ]
  },
  countdown: 30, // 倒计时调整为30秒
  isCounting: false
})

const formRef = ref()
let timer = null;

// 发送邮箱验证码
const sendverifyCode = () => {
  formRef.value.validateField('email', (valid) => {
    if (valid) {
      if (data.isCounting) return;

      data.isCounting = true;
      data.countdown = 30; // 倒计时调整为30秒
      timer = setInterval(() => {
        data.countdown--;
        if (data.countdown <= 0) {
          clearInterval(timer);
          data.isCounting = false;
        }
      }, 1000);

      // 调用后端接口发送验证码
      request.post('/api/sendVerifyCode', { email: data.form.email }).then(res => {
        if (res.code === '200') {
          ElMessage.success("验证码已发送至您的邮箱，请注意查收");
        } else {
          ElMessage.error(res.msg);
          // 如果发送失败，停止倒计时
          clearInterval(timer);
          data.isCounting = false;
          data.countdown = 30;
        }
      }).catch(() => {
        ElMessage.error("验证码发送失败，请检查网络");
        // 如果请求失败，停止倒计时
        clearInterval(timer);
        data.isCounting = false;
        data.countdown = 30;
      });
    }
  });
};

const register = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 注册时将 email 字段赋值给 username，以满足后端接口要求
      const finalForm = {
        ...data.form,
        username: data.form.email,
        role: 'USER'
      };

      request.post('/register', finalForm).then(res => {
        if (res.code === '200') {
          ElMessage.success("注册成功");
          clearInterval(timer);
          data.isCounting = false;
          setTimeout(() => {
            router.push('/login');
          }, 500);
        } else {
          ElMessage.error(res.msg);
        }
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

      .code-wrapper {
        display: flex;
        align-items: center;
        gap: 10px;

        // 让输入框自动填充剩余空间
        .code-input {
          flex: 1;
        }
      }

      .send-code-button {
        height: 48px;
        font-size: 14px;
        min-width: 120px;
        border-radius: 12px;
        transition: all 0.3s ease;

        &.is-disabled {
          background-color: #e4e7ed;
          color: #a8abb2;
          border-color: #e4e7ed;
        }
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