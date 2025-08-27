<template>
  <div class="person-container">
    <div class="card">
      <el-form ref="formRef" :model="data.user" :rules="rules" label-width="80px">
        <div class="avatar-container">
          <h2 class="title">个人信息</h2>
          <el-upload class="avatar-uploader" :action="baseUrl + '/files/upload'" :on-success="handleFileUpload"
            :show-file-list="false">
            <div class="avatar-wrapper">
              <img v-if="data.user.avatar" alt="" :src="data.user.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
              <div class="avatar-hover">
                <el-icon>
                  <Edit />
                </el-icon>
                <span>更换头像</span>
              </div>
            </div>
          </el-upload>
        </div>

        <div class="form-content">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="data.user.username" disabled placeholder="用户名" />
          </el-form-item>
          <el-form-item prop="name" label="姓名">
            <el-input v-model="data.user.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item prop="phone" label="电话">
            <el-input v-model="data.user.phone" placeholder="请输入电话号码">
              <template #prefix>
                <el-icon>
                  <Phone />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="data.user.email" placeholder="请输入邮箱地址">
              <template #prefix>
                <el-icon>
                  <Message />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="account" label="余额" v-if="data.user.role === 'USER'">
            <div class="balance-container">
              <span class="balance-amount">￥{{ data.user.account }}</span>
              <el-button type="primary" :icon="Wallet" @click="rechargeInit" class="recharge-btn">充值</el-button>
            </div>
          </el-form-item>
          <div class="form-actions">
            <el-button type="primary" :icon="Check" @click="update">保存修改</el-button>
            <el-button :icon="RefreshLeft" @click="reset">重置</el-button>
          </div>
        </div>
      </el-form>
    </div>

    <!-- 充值对话框 -->
    <el-dialog v-model="data.formVisible" title="账户充值" width="460px" :close-on-click-modal="false" destroy-on-close
      class="recharge-dialog">
      <el-form label-width="80px" class="recharge-form">
        <el-form-item label="充值金额" prop="account">
          <el-input v-model="data.account" placeholder="请输入充值金额">
            <template #prefix>￥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="支付方式" prop="type">
          <el-radio-group v-model="data.type" size="large">
            <el-radio-button label="wei">微信支付</el-radio-button>
            <el-radio-button label="ali">支付宝</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="recharge">确认充值</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Edit, Phone, Message, Check, RefreshLeft, Wallet } from '@element-plus/icons-vue'

const baseUrl = import.meta.env.VITE_BASE_URL
const formRef = ref()

// 邮箱验证规则
const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
  if (!value) {
    callback(new Error('请输入邮箱地址'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

// 手机号验证规则
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的11位手机号'))
  } else {
    callback()
  }
}

const rules = reactive({
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ]
})

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  originalUser: null,
  account: 100,
  formVisible: false,
  type: 'wei'
})

// 保存初始数据用于重置
data.originalUser = { ...data.user }

const emit = defineEmits(["updateUser"])

const loadPerson = () => {
  const url = data.user.role === 'ADMIN' ? '/admin/selectById/' + data.user.id : '/user/selectById/' + data.user.id
  request.get(url).then(res => {
    if (res.code === '200') {
      data.user = res.data
      data.originalUser = { ...res.data }
      localStorage.setItem('xm-user', JSON.stringify(res.data))
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const url = data.user.role === 'ADMIN' ? '/admin/update' : '/user/update'
      request.put(url, data.user).then(res => {
        if (res.code === '200') {
          ElMessage.success('更新成功')
          localStorage.setItem('xm-user', JSON.stringify(data.user))
          emit('updateUser')
        } else {
          ElMessage.error(res.msg)
        }
      })
    } else {
      ElMessage.warning('请正确填写所有必填项')
      return false
    }
  })
}
const reset = () => {
  data.user = { ...data.originalUser }
  ElMessage.info('已重置为原始信息')
}

const rechargeInit = () => {
  data.formVisible = true
}

const recharge = () => {
  if (!data.account || data.account <= 0) {
    ElMessage.error('请输入正确的充值金额')
    return
  }
  data.user.account = parseFloat(data.user.account) + parseFloat(data.account)
  update()
  data.formVisible = false
  ElMessage.success('充值成功')
}

const handleFileUpload = (res) => {
  if (res.code === '200') {
    data.user.avatar = res.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error('头像上传失败')
  }
}

// 初始加载个人信息
loadPerson()
</script>

<style scoped>
.person-container {
  padding: 24px;
  background-color: #f8f9fd;
  min-height: calc(100vh - 48px);
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(149, 157, 165, 0.1);
  width: 100%;
  max-width: 560px;
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 12px 32px rgba(149, 157, 165, 0.15);
}

.avatar-container {
  padding: 32px;
  text-align: center;
  background: linear-gradient(to right, #f0f5ff, #e6f7ff);
  border-radius: 16px 16px 0 0;
}

.title {
  margin: 0 0 24px;
  color: #333;
  font-size: 24px;
  font-weight: 500;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover .avatar-hover {
  opacity: 1;
}

.avatar-hover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: all 0.3s ease;
  color: white;
}

.avatar-hover .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.form-content {
  padding: 32px;
}

.form-actions {
  margin-top: 32px;
  text-align: center;
}

.el-button {
  padding: 12px 24px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-button+.el-button {
  margin-left: 16px;
}

.el-input {
  border-radius: 8px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

.avatar-uploader :deep(.el-upload) {
  border: none;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  border: 2px dashed #e9ecef;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.balance-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.balance-amount {
  font-size: 24px;
  font-weight: 500;
  color: #f56c6c;
}

.recharge-btn {
  background: linear-gradient(135deg, #67C23A 0%, #4CAF50 100%);
  border: none;
  padding: 8px 16px;
}

.recharge-btn:hover {
  background: linear-gradient(135deg, #5daf34 0%, #43a047 100%);
  transform: translateY(-1px);
}

.recharge-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  margin: 0;
  border-bottom: 1px solid #f0f0f0;
}

.recharge-form {
  padding: 24px;
}

.dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-radio-button__inner) {
  padding: 12px 24px;
}
</style>