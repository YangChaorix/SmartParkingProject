<template>
  <div class="home-container">
    <!-- Welcome Card -->
    <div class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-icon">
          <el-icon>
            <UserFilled />
          </el-icon>
        </div>
        <div class="welcome-text">
          <h1>欢迎回来, <span class="highlight">{{ data.user?.name }}</span></h1>
          <p>祝您今天工作顺利，心情愉快！</p>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
      <!-- Parking Status Card -->
      <div class="status-card">
        <div class="card-header">
          <el-icon>
            <Location />
          </el-icon>
          <h2>停车场实时状态</h2>
        </div>

        <div class="parking-areas">
          <div v-for="(item, index) in data.locationData" :key="index" class="parking-area">
            <div class="area-header">
              <el-icon>
                <Place />
              </el-icon>
              <span>{{ item.name }}</span>
            </div>

            <div class="parking-slots">
              <el-row :gutter="16">
                <el-col :span="4" v-for="(it, index) in item.parkingLots" :key="index">
                  <div class="parking-slot" :class="{ 'occupied': it.status === '占用' }">
                    <div class="slot-icon">
                      <img v-if="it.status === '占用'" src="@/assets/imgs/no.png" alt="占用" class="status-icon">
                      <img v-else src="@/assets/imgs/ok.png" alt="空闲" class="status-icon">
                    </div>
                    <span class="slot-name">{{ it.name }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>

      <!-- Notice Card -->
      <div class="notice-card">
        <div class="card-header">
          <el-icon>
            <Bell />
          </el-icon>
          <h2>系统公告</h2>
        </div>

        <div class="notice-list">
          <el-timeline>
            <el-timeline-item v-for="notice in data.noticeList" :key="notice.id" :type="getNoticeType(notice)"
              :color="getNoticeColor(notice)" :timestamp="notice.time" :hollow="true">
              <div class="notice-item">
                <h3>{{ notice.title }}</h3>
                <p>{{ notice.content }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import {
  UserFilled,
  Location,
  Place,
  Bell,
  Check,
  CloseBold
} from '@element-plus/icons-vue'

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  noticeList: [],
  locationData: []
})

const getNoticeType = (notice) => {
  // 根据公告内容类型返回不同的图标类型
  return 'primary'
}

const getNoticeColor = (notice) => {
  // 根据公告重要程度返回不同的颜色
  return '#409EFF'
}

const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    data.noticeList = res.data
  })
}

const loadLocation = () => {
  request.get('/location/selectAll').then(res => {
    if (res.code === '200') {
      data.locationData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

loadNotice()
loadLocation()
</script>

<style scoped>
.home-container {
  padding: 24px;
  min-height: 100%;
}

/* Welcome Card Styles */
.welcome-card {
  background: linear-gradient(135deg, #4080ff 0%, #2c5edb 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(64, 128, 255, 0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.welcome-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(64, 128, 255, 0.2);
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-icon {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 16px;
  font-size: 24px;
}

.welcome-text h1 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
}

.highlight {
  color: #fff;
  font-weight: 700;
}

/* Content Grid */
.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-top: 24px;
}

/* Card Styles */
.status-card,
.notice-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.status-card:hover,
.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.card-header h2 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.card-header .el-icon {
  font-size: 24px;
  color: #4080ff;
}

/* Parking Area Styles */
.parking-area {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

.area-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #333;
  font-weight: 500;
}

.parking-slots {
  margin-top: 16px;
}

.parking-slot {
  background: white;
  border-radius: 12px;
  padding: 12px 8px;
  text-align: center;
  transition: all 0.3s ease;
  border: 2px solid #e5e7eb;
  cursor: pointer;
}

.parking-slot:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.parking-slot.occupied {
  background: #fff8f8;
  border-color: #ffccc7;
}

.slot-icon {
  margin-bottom: 8px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon {
  width: 40px;
  height: 30px;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.parking-slot:hover .status-icon {
  transform: scale(1.1);
}

.slot-name {
  font-size: 14px;
  color: #333;
  display: block;
  margin-top: 4px;
  font-weight: 500;
}

.parking-slot.occupied .slot-name {
  color: #cf1322;
}

/* Notice List Styles */
.notice-list {
  padding: 0 16px;
}

.notice-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  transition: transform 0.3s ease;
}

.notice-item:hover {
  transform: translateX(4px);
}

.notice-item h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.notice-item p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

/* Timeline Customization */
:deep(.el-timeline-item__node) {
  background-color: #4080ff;
}

:deep(.el-timeline-item__timestamp) {
  color: #666;
  font-size: 14px;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

/* Animation */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.home-container>* {
  animation: fadeIn 0.5s ease-out forwards;
}
</style>
