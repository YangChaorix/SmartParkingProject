<template>
  <div class="manager-container">
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/logo.png" alt="">
        <div class="title">停车场管理系统</div>
      </div>
      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item to="/manager/home">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="manager-header-right">
        <div class="header-right-content">
          <el-badge :value="data.unreadCount" :hidden="data.unreadCount === 0" class="notification-icon"
            v-if="data.user.role === 'USER'">
            <el-icon :size="20" @click="router.push('/manager/notificationUser')">
              <BellFilled />
            </el-icon>
          </el-badge>
          <el-dropdown>
            <div class="user-info">
              <img v-if="data.user.avatar" :src="data.user?.avatar" alt="" class="avatar">
              <img v-else src="@/assets/imgs/avatar.png" alt="" class="avatar">
              <span class="username">{{ data.user?.name }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/manager/person')">个人资料</el-dropdown-item>
                <el-dropdown-item @click="router.push('/manager/password')">修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <div style="display: flex">
      <div class="manager-main-left">
        <el-menu :default-active="router.currentRoute.value.path" :default-openeds="['1', '2']" router>
          <el-menu-item index="/manager/home">
            <el-icon>
              <HomeFilled />
            </el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/dashboard" v-if="data.user.role === 'ADMIN'">
            <el-icon>
              <DataLine />
            </el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/manager/map" v-if="data.user.role === 'ADMIN'">
            <el-icon>
              <Monitor />
            </el-icon>
            <span>地图概览</span>
          </el-menu-item>
          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <Management />
              </el-icon>
              <span>信息管理</span>
            </template>
            <el-menu-item index="/manager/parking">
              <el-icon>
                <Van />
              </el-icon>
              <span>停车信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/pay">
              <el-icon>
                <Wallet />
              </el-icon>
              <span>缴费信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/location">
              <el-icon>
                <LocationInformation />
              </el-icon>
              <span>停车区域</span>
            </el-menu-item>
            <el-menu-item index="/manager/parkingLot">
              <el-icon>
                <Place />
              </el-icon>
              <span>车位信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/vehicle">
              <el-icon>
                <Guide />
              </el-icon>
              <span>车辆信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">
              <el-icon>
                <Bell />
              </el-icon>
              <span>系统公告</span>
            </el-menu-item>
            <el-menu-item index="/manager/notification" v-if="data.user.role === 'ADMIN'">
              <el-icon>
                <BellFilled />
              </el-icon>
              <span>通知信息</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon>
                <UserFilled />
              </el-icon>
              <span>用户信息</span>
            </template>
            <el-menu-item index="/manager/admin">
              <el-icon>
                <Avatar />
              </el-icon>
              <span>管理员信息</span>
            </el-menu-item>
            <el-menu-item index="/manager/user">
              <el-icon>
                <User />
              </el-icon>
              <span>用户信息</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <div class="manager-main-right">
        <router-view @updateUser="updateUser" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive, onUnmounted } from "vue";
import router from "@/router";
import { ElMessage } from "element-plus";
import {
  HomeFilled,
  DataLine,
  Management,
  Van,
  Wallet,
  LocationInformation,
  Place,
  Guide,
  Bell,
  UserFilled,
  Avatar,
  User,
  Monitor
} from '@element-plus/icons-vue'
import request from "@/utils/request";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  unreadCount: 0
})

const fetchUnreadCount = async () => {
  if (!data.user.id) return
  try {
    const res = await request.get(`/notification/selectUnreadCount/${data.user.id}`)
    if (res.code === '200') {
      data.unreadCount = res.data
    }
  } catch (error) {
    console.error('Failed to fetch unread notifications:', error)
  }
}

fetchUnreadCount()
const timer = setInterval(fetchUnreadCount, 30000) // 每30秒刷新一次

onUnmounted(() => {
  clearInterval(timer)
})

const logout = () => {
  localStorage.removeItem('xm-user')
  router.push('/login')
}

if (!data.user.id) {
  logout()
  ElMessage.error('请登录!')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('xm-user') || '{}')   // 重新获取下用户的最新信息
}
</script>

<style scoped>
@import '@/assets/css/manager.css';
</style>
