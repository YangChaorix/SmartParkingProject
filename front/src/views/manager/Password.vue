<template>
  <div style="width: 50%;">
    <div class="card">
      <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="100px" style="padding: 20px 30px">
        <el-form-item prop="password" label="原密码">
          <el-input v-model="data.form.password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item prop="newPassword" label="新密码">
          <el-input v-model="data.form.newPassword" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认新密码">
          <el-input v-model="data.form.confirmPassword" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updatePassword">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== data.form.newPassword) {
    callback(new Error('确认密码错误'))
  } else {
    callback()
  }
}

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  form: {
    password: '',
    newPassword: '',
    confirmPassword: ''
  },
  rules: {
    password: [
      { required: true, message: '请输入原密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ],
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value === data.form.password) {
            callback(new Error('新密码不能与原密码相同'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    confirmPassword: [
      { required: true, message: '请确认新密码', trigger: 'blur' },
      { validator: validatePassword, trigger: 'blur' }
    ],
  }
})

const formRef = ref()

const resetForm = () => {
  formRef.value.resetFields()
}

const updatePassword = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const params = {
        id: data.user.id,
        username: data.user.username,
        password: data.form.password,
        newPassword: data.form.newPassword,
        role: data.user.role
      }
      try {
        const res = await request.put('/updatePassword', params)
        if (res.code === '200') {
          ElMessage.success('密码修改成功')
          localStorage.removeItem('xm-user')
          await router.push('/login')
        } else {
          ElMessage.error(res.msg)
        }
      } catch (error) {
        ElMessage.error('密码修改失败，请重试')
      }
    }
  })
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(149, 157, 165, 0.1);
  margin-top: 20px;
}

.el-button {
  padding: 12px 24px;
  border-radius: 8px;
}

.el-button+.el-button {
  margin-left: 16px;
}

.el-input {
  border-radius: 8px;
}
</style>