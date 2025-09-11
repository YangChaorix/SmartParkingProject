<template>
  <div class="parking-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.userName" placeholder="请输入用户姓名查询" class="search-input" />
          <el-input v-model="data.vehicleName" placeholder="请输入车牌号查询" class="search-input" />
          <el-select v-model="data.locationId" placeholder="请选择所属区域筛选" clearable class="search-select">
            <el-option v-for="item in data.locationList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button v-if="data.user.role === 'ADMIN'" type="success" @click="handleAdd">车辆入场</el-button>
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-card">
      <div class="table-container">
        <el-table 
          :data="data.tableData" 
          stripe 
          class="custom-table" 
          v-loading="data.loading"
          :max-height="600"
          style="width: 100%"
        >
          <el-table-column prop="userName" label="用户" min-width="100" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.userName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="vehicleName" label="车牌号" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="plate-text">{{ scope.row.vehicleName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="locationName" label="区域名称" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="location-text">{{ scope.row.locationName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="parkingLotName" label="车位编号" min-width="100" show-overflow-tooltip />
          <el-table-column prop="startTime" label="入场时间" min-width="160" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.startTime }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="endTime" label="出场时间" min-width="160" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.endTime || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="停车时长" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ calculateDuration(scope.row.startTime, scope.row.endTime || new Date()) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已出场' ? 'success' : 'warning'" effect="light">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" v-if="data.user.role === 'ADMIN'">
            <template #default="scope">
              <div class="operation-buttons">
                <template v-if="scope.row.status !== '已出场'">
                  <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
                    车辆出场
                  </el-button>
                  <el-button type="warning" link size="small" @click="handleNotification(scope.row)">
                    提醒
                  </el-button>
                </template>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination 
          v-model:current-page="data.pageNum" 
          :page-size="data.pageSize" 
          :total="data.total"
          @current-change="load" 
          background 
          layout="total, prev, pager, next, sizes"
          :page-sizes="[5, 10, 20, 50]"
        />
      </div>
    </div>

    <!-- Dialog Section -->
    <el-dialog :title="data.flag ? '车辆入场' : '车辆出场'" v-model="data.formVisible" width="40%" :close-on-click-modal="false"
               destroy-on-close class="custom-dialog">
      <el-form :rules="data.rules" :model="data.form" label-width="80px" ref="formRef" class="parking-form">
        <el-form-item label="用户" prop="userId" v-if="data.flag">
          <el-select v-model="data.form.userId" @change="initVehicle" class="custom-select">
            <el-option v-for="item in data.userList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆" prop="vehicleId" v-if="data.flag">
          <el-select style="width: 100%" v-model="data.form.vehicleId">
            <el-option v-for="item in data.vehicleList" :key="item.id" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="区域" prop="locationId" v-if="data.flag">
          <el-select style="width: 100%" v-model="data.form.locationId" @change="initParkingLot">
            <el-option v-for="item in data.locationList" :key="item.id" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="车位" prop="parkingLotId" v-if="data.flag">
          <el-select style="width: 100%" v-model="data.form.parkingLotId">
            <el-option v-for="item in data.parkingLotList" :key="item.id" :value="item.id"
                       :label="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="入场时间" prop="startTime" v-if="data.flag">
          <el-date-picker placeholder="请选择日期时间" type="datetime" format="YYYY-MM-DD HH:mm:ss"
                          value-format="YYYY-MM-DD HH:mm:ss" v-model="data.form.startTime" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="出场时间" prop="endTime" v-if="!data.flag">
          <el-date-picker placeholder="请选择日期时间" type="datetime" format="YYYY-MM-DD HH:mm:ss"
                          value-format="YYYY-MM-DD HH:mm:ss" v-model="data.form.endTime" style="width: 100%"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- Notification Dialog -->
    <el-dialog v-model="data.notificationVisible" :show-close="false" width="560px" :close-on-click-modal="false"
               destroy-on-close class="notification-dialog apple-style-dialog">
      <div class="notification-content">
        <el-form :model="data.notificationForm" label-width="0" ref="notificationFormRef">
          <el-form-item prop="description">
            <el-input v-model="data.notificationForm.description" type="textarea" :rows="8" placeholder="请输入提醒内容..."
                      resize="none" class="custom-textarea" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.notificationVisible = false" class="cancel-button" plain>
            取消
          </el-button>
          <el-button type="primary" @click="sendNotification" :disabled="!data.notificationForm.description"
                     class="send-button">
            发送
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"

const baseUrl = import.meta.env.VITE_BASE_URL
const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  formVisible: false,
  form: {},
  userName: null,
  vehicleName: null,
  userList: [],
  vehicleList: [],
  locationList: [],
  parkingList: [],
  flag: true,
  notificationVisible: false,
  notificationForm: {
    userId: '',
    description: ''
  },
  rules: {
    userId: [
      { required: true, message: '请选择用户', trigger: 'blur' },
    ],
    vehicleId: [
      { required: true, message: '请选择车辆', trigger: 'blur' },
    ],
    locationId: [
      { required: true, message: '请选择区域', trigger: 'blur' },
    ],
    parkingLotId: [
      { required: true, message: '请选择车位', trigger: 'blur' },
    ],
    startTime: [
      { required: true, message: '请选择入场时间', trigger: 'blur' },
    ],
  }
})

const formRef = ref()

// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/parking/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        userName: data.userName,
        vehicleName: data.vehicleName,
        locationId: data.locationId
      }
    })
    data.tableData = res.data?.list || []
    data.total = res.data?.total
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    data.loading = false
  }
}

// 打开新增弹窗
const handleAdd = () => {
  data.form = {}
  data.flag = true
  data.formVisible = true
}

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.flag = false
  data.formVisible = true
}

// 新增
const add = () => {
  request.post('/parking/add', data.form).then(res => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 更新
const update = () => {
  request.put('/parking/update', data.form).then(res => {
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('操作成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const save = () => {
  formRef.value.validate(valid => {
    if (valid) {
      data.form.id ? update() : add()
    }
  })
}

const reset = () => {
  data.userName = null
  data.vehicleName = null
  data.locationId = null
  load()
}

// 计算停车时长
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return '-'
  const start = new Date(startTime)
  const end = new Date(endTime)
  const diff = end - start

  // 计算天数、小时和分钟
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))

  // 格式化输出
  let duration = ''
  if (days > 0) duration += `${days}天`
  if (hours > 0) duration += `${hours}小时`
  if (minutes > 0) duration += `${minutes}分钟`
  return duration || '0分钟'
}

// 打开提醒弹窗
const handleNotification = (row) => {
  // 计算当前停车时长
  const duration = calculateDuration(row.startTime, new Date())

  // 组织预填充的提醒内容
  const prefilledContent = `尊敬的${row.userName}车主，您好！

您的车辆（车牌号：${row.vehicleName}）目前停放在${row.locationName}区域${row.parkingLotName}车位。自${row.startTime}入场以来，已停车${duration}。

如果您已完成停车，请及时处理。

祝您用车愉快！`

  data.notificationForm = {
    userId: row.userId,
    description: prefilledContent
  }
  data.notificationVisible = true
}

// 发送提醒
const sendNotification = () => {
  if (!data.notificationForm.description) {
    ElMessage.warning('请输入提醒内容')
    return
  }

  request.post('/notification/add', data.notificationForm).then(res => {
    if (res.code === '200') {
      ElMessage.success('提醒发送成功')
      data.notificationVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadUser = () => {
  request.get('/user/selectAll').then(res => {
    if (res.code === '200') {
      data.userList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadUser()
const initVehicle = (userId) => {
  // 修复bug: 切换用户时，清空车辆列表
  data.vehicleList = []
  data.form.vehicleId = null;

  request.get('/vehicle/selectAll', {
    params: {
      userId: userId
    }
  }).then(res => {
    if (res.code === '200') {
      data.vehicleList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
const loadLocation = () => {
  request.get('/location/selectAll').then(res => {
    if (res.code === '200') {
      data.locationList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadLocation()
const initParkingLot = (locationId) => {
  // 修复bug: 切换区域时，清空车位列表
  data.parkingLotList = []
  data.form.parkingLotId = null;

  request.get('/parkingLot/selectAll', {
    params: {
      locationId: locationId,
      status: '空闲'
    }
  }).then(res => {
    if (res.code === '200') {
      data.parkingLotList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}

load()

</script>

<style scoped>
@import '@/assets/css/responsive-table.css';

.parking-container {
  @apply responsive-container;
}

.search-card {
  @apply search-card;
}

.search-wrapper {
  @apply search-wrapper;
}

.search-inputs {
  @apply search-inputs;
}

.search-input {
  @apply search-input;
}

.search-select {
  @apply search-select;
}

.table-card {
  @apply table-card;
}

.table-container {
  @apply table-container;
}

.custom-table {
  @apply custom-table;
}

.table-cell-text {
  @apply table-cell-text;
}

.operation-buttons {
  @apply operation-buttons;
}

.pagination-wrapper {
  @apply pagination-wrapper;
}

.custom-dialog {
  @apply custom-dialog;
}

.parking-form {
  @apply custom-form;
}

.custom-select {
  @apply custom-select;
}

.plate-text {
  @apply plate-text;
}

.location-text {
  @apply location-text;
}

.notification-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
}

.notification-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.notification-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.notification-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.notification-dialog :deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.notification-content {
  padding: 0 0 16px;
}

.custom-textarea :deep(.el-textarea__inner) {
  padding: 12px 16px;
  border-radius: 8px;
  border-color: #dcdfe6;
  font-size: 14px;
  transition: all 0.3s;
}

.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 0 8px;
}

.dialog-footer .el-button {
  min-width: 80px;
  height: 40px;
  padding: 0 20px;
  font-size: 14px;
  border-radius: 8px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dialog-footer .cancel-button.el-button {
  background: #f5f5f5;
  color: #666666;
  border: none !important;
}

.dialog-footer .cancel-button.el-button:hover {
  background-color: #666666;
  color: #ffffff;
}

.dialog-footer .send-button.el-button {
  background: #e6f0ff;
  color: #0066ff;
  border: none !important;
}

.dialog-footer .send-button.el-button:hover:not([disabled]) {
  background: #0066ff;
  color: #ffffff;
  transform: translateY(-1px);
}

.dialog-footer .send-button.el-button:active:not([disabled]) {
  background: #0052cc;
  color: #ffffff;
  transform: translateY(0);
}

.dialog-footer .send-button.el-button[disabled] {
  background: #f5f5f5;
  color: #999999;
  opacity: 1;
  cursor: not-allowed;
}

/* Animation classes */
@keyframes dialogFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

.apple-style-dialog :deep(.el-dialog) {
  animation: dialogFadeIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Apple-style Dialog Customization */
.apple-style-dialog {
  --dialog-radius: 12px;
  --primary-color: #0066ff;
  --border-color: #e4e4e4;
  --background-soft: #f9f9f9;
}

.apple-style-dialog :deep(.el-dialog) {
  border-radius: var(--dialog-radius);
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border: 1px solid var(--border-color);
}

.apple-style-dialog :deep(.el-dialog__header) {
  display: none;
}

.apple-style-dialog :deep(.el-dialog__body) {
  padding: 32px;
  background: #ffffff;
}

.apple-style-dialog :deep(.el-dialog__footer) {
  padding: 20px 32px;
  border-top: 1px solid var(--border-color);
  background: var(--background-soft);
}

.notification-content {
  padding: 0;
}

.custom-textarea :deep(.el-textarea__inner) {
  padding: 20px;
  border-radius: 12px;
  border-color: var(--border-color);
  font-size: 15px;
  line-height: 1.6;
  color: #1d1d1f;
  background: var(--background-soft);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: -0.01em;
}

.custom-textarea :deep(.el-textarea__inner:hover) {
  border-color: #d1d1d1;
  background: #ffffff;
}

.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color);
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(0, 102, 255, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

.dialog-footer .el-button {
  min-width: 80px;
  padding: 10px 20px;
  font-size: 15px;
  border-radius: 8px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: -0.01em;
  font-weight: 500;
}

.dialog-footer .cancel-button {
  border: none;
  background: #f0f0f0;
  color: #1d1d1f;
}

.dialog-footer .cancel-button:hover {
  background-color: #e5e5e5;
  color: #000000;
}

.dialog-footer .send-button {
  background: var(--primary-color);
  border: none;
  color: #ffffff;
  font-weight: 500;
}

.dialog-footer .send-button:hover:not([disabled]) {
  background: #0052cc;
  transform: translateY(-1px);
}

.dialog-footer .send-button:active:not([disabled]) {
  transform: translateY(0);
}

.dialog-footer .send-button[disabled] {
  background: #a3a3a3;
  opacity: 0.8;
  cursor: not-allowed;
}

/* Animation classes */
@keyframes dialogFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

.apple-style-dialog :deep(.el-dialog) {
  animation: dialogFadeIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>
