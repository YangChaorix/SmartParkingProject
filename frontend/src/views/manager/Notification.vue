<template>
  <div class="notification-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.description" placeholder="请输入通知内容查询" class="search-input" />
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <div class="action-buttons">
            <el-button type="danger" @click="handleBatchDelete" v-if="data.selectedIds.length">批量删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-card">
      <el-table :data="data.tableData" stripe class="custom-table" v-loading="data.loading"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="通知内容">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="阅读状态">
          <template #default="scope">
            <el-tag :type="scope.row.isRead === 1 ? 'success' : 'warning'" effect="light">
              {{ scope.row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deleted" label="用户删除状态">
          <template #default="scope">
            <el-tag :type="scope.row.deleted === 1 ? 'danger' : 'success'" effect="light">
              {{ scope.row.deleted === 1 ? '已删除' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间">
          <template #default="scope">
            <span class="table-cell-text">{{ formatDateTime(scope.row.sendTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button type="primary" link @click="handleEdit(scope.row)">
                编辑
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

    <!-- Edit Dialog -->
    <el-dialog title="编辑通知" v-model="data.formVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="80px" ref="formRef">
        <el-form-item label="通知内容" prop="description">
          <el-input v-model="data.form.description" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="状态" prop="isRead">
          <el-select v-model="data.form.isRead" placeholder="请选择状态">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  loading: false,
  description: null,
  formVisible: false,
  form: {},
  selectedIds: []
})

const formRef = ref()

// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/notification/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        description: data.description
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

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 保存编辑
const save = async () => {
  try {
    const res = await request.put('/notification/update', data.form)
    if (res.code === '200') {
      ElMessage.success('更新成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 重置搜索
const reset = () => {
  data.description = null
  load()
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
  data.selectedIds = selection.map(item => item.id)
}

// 处理单个删除
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条通知吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.post(`/notification/deleteReal/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(() => { })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (!data.selectedIds.length) {
    ElMessage.warning('请选择要删除的通知')
    return
  }

  ElMessageBox.confirm(`确定要删除选中的 ${data.selectedIds.length} 条通知吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.post('/notification/deleteReal/batch', data.selectedIds).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功')
        load()
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

.custom-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.table-cell-text {
  font-size: 14px;
  color: #606266;
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

.table-card {
  position: relative;
  padding-bottom: 60px;
}

.pagination-wrapper {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

.operation-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
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
</style>