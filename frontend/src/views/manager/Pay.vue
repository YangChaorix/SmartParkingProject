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
      <div class="table-container">
        <el-table 
          :data="data.tableData" 
          stripe 
          class="custom-table" 
          v-loading="data.loading"
          @selection-change="handleSelectionChange"
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
          <el-table-column prop="minutes" label="停车时间" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.minutes }}分【{{ calculateDuration(scope.row.startTime, scope.row.endTime || new Date()) }}】</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="停车费用" width="100" align="center">
            <template #default="scope">
              <span class="price-text">￥{{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已缴费' ? 'success' : 'danger'" effect="light">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" v-if="data.user.role === 'USER'">
            <template #default="scope">
              <el-button type="primary" link size="small" @click="pay(scope.row)" :disabled="scope.row.status === '已缴费'">
                缴费
              </el-button>
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

// 初始化加载
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

.pagination-wrapper {
  @apply pagination-wrapper;
}

.price-text {
  @apply price-text;
}

.plate-text {
  @apply plate-text;
}

.location-text {
  @apply location-text;
}
</style>