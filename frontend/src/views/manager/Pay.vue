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
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-card">
      <el-table :data="data.tableData" stripe class="custom-table" v-loading="data.loading"
        @selection-change="handleSelectionChange">
        <el-table-column prop="userName" label="用户">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.userName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="vehicleName" label="车牌号" width="100">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.vehicleName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="locationName" label="区域名称" width="100">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.locationName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="parkingLotName" label="车位编号" width="100" />
        <el-table-column prop="startTime" label="入场时间">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.startTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="出场时间">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minutes" label="停车时间(分钟)">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.minutes }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="停车费用">
          <template #default="scope">
            <span class="price-text">￥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已缴费' ? 'success' : 'danger'" effect="light">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" v-if="data.user.role === 'USER'">
          <template #default="scope">
            <el-button type="primary" link @click="pay(scope.row)" :disabled="scope.row.status === '已缴费'">
              缴费
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination v-model:current-page="data.pageNum" :page-size="data.pageSize" :total="data.total"
          @current-change="load" background layout="total, prev, pager, next" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import request from "@/utils/request"
import { ElMessage } from "element-plus"

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  formVisible: false,
  form: {},
  ids: [],
  userName: null,
  vehicleName: null,
  userList: [],
  vehicleList: [],
  locationList: [],
  parkingList: [],
  flag: true,
  loading: false
})

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
  request.get('/payLot/selectAll', {
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
// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/pay/selectPage', {
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

// 更新
const pay = async (row) => {
  try {
    const res = await request.put('/pay/update', row)
    if (res.code === '200') {
      ElMessage.success('缴费成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('缴费失败')
  }
}

const reset = () => {
  data.userName = null
  data.vehicleName = null
  data.locationId = null
  load()
}

// 初始化加载
load()
</script>

<style scoped>
.parking-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.search-card,
.table-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.search-card:hover,
.table-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
}

.search-wrapper {
  display: flex;
  align-items: center;
}

.search-inputs {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-input {
  width: 240px;
}

.table-card {
  position: relative;
  padding-bottom: 60px;
}

.custom-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.table-cell-text {
  font-size: 14px;
  color: #606266;
}

.price-text {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 500;
}

.el-button {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
}

.el-tag {
  border-radius: 6px;
  padding: 4px 8px;
}

.pagination-wrapper {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

/* Animation classes */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@keyframes slideIn {
  from {
    transform: translateY(20px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.table-card {
  animation: slideIn 0.4s ease;
}
</style>