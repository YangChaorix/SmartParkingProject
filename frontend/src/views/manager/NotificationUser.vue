<template>
  <div class="notification-container">
    <!-- Table Section -->
    <div class="table-card">
      <!-- 添加批量删除按钮 -->
      <div class="table-header" v-if="data.ids.length">
        <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <el-table :data="data.tableData" stripe class="custom-table" v-loading="data.loading"
        @selection-change="handleSelectionChange">
        <!-- 添加选择列 -->
        <el-table-column type="selection" width="55" align="center" />

        <el-table-column prop="description" label="通知内容" align="center">
          <template #default="scope">
            <div class="notification-content" :class="{ 'unread': scope.row.isRead === 0 }">
              <span class="table-cell-text" :class="{ 'text-red': scope.row.isRead === 0 }">{{ scope.row.description
              }}</span>
              <el-tag v-if="scope.row.isRead === 0" size="small" type="danger" class="unread-tag">未读</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180" align="center">
          <template #default="scope">
            <span class="table-cell-text">{{ formatDateTime(scope.row.sendTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button type="primary" link @click="handleView(scope.row)">
                查看详情
              </el-button>
              <el-button type="danger" link @click="handleDelete(scope.row.id)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination v-model:current-page="data.pageNum" :page-size="data.pageSize" :total="data.total"
          @current-change="load" background layout="total, prev, pager, next" />
      </div>
    </div>

    <!-- View Dialog -->
    <el-dialog :title="data.currentNotification.isRead === 0 ? '新通知' : '通知详情'" v-model="data.dialogVisible" width="40%">
      <div class="notification-detail">
        <div class="notification-time">
          发送时间：{{ formatDateTime(data.currentNotification.sendTime) }}
        </div>
        <div class="notification-content-detail">
          {{ data.currentNotification.description }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  loading: false,
  userId: JSON.parse(localStorage.getItem('xm-user'))?.id,
  dialogVisible: false,
  currentNotification: {},
  ids: []  // 添加选中项的ID数组
})

// 加载表格数据
const load = async () => {
  console.log('Current userId:', data.userId)
  data.loading = true
  try {
    const res = await request.get('/notification/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        userId: data.userId,
        deletedAt: null
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

// 查看通知详情
const handleView = async (row) => {
  data.currentNotification = row
  data.dialogVisible = true

  // 如果是未读通知，标记为已读
  if (row.isRead === 0) {
    try {
      const res = await request.post(`/notification/markAsRead/${row.id}`)
      if (res.code === '200') {
        row.isRead = 1
      }
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

// 处理删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.post(`/notification/delete/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(() => { })
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  data.ids = selection.map(item => item.id)
}

// 处理批量删除
const handleBatchDelete = () => {
  if (!data.ids.length) {
    ElMessage.warning('请选择要删除的通知')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的 ${data.ids.length} 条通知吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.post('/notification/deleteReal/batch', data.ids).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功')
        data.ids = []  // 清空选中项
        load()  // 重新加载数据
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(() => { })
}

// 初始加载
load()
</script>

<style scoped>
.notification-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.table-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.table-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.08);
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

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-content.unread {
  font-weight: bold;
}

.unread-tag {
  font-size: 12px;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 通知详情样式 */
.notification-detail {
  padding: 20px;
}

.notification-time {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}

.notification-content-detail {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
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

.text-red {
  color: #F56C6C;
  font-weight: 500;
}

.table-header {
  margin-bottom: 15px;
}
</style>