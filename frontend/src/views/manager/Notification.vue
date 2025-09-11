<template>
  <div class="notification-container">
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.username" placeholder="用户名" class="search-input" />
          <el-input v-model="data.description" placeholder="通知内容" class="search-input" />
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <div class="action-buttons">
            <el-button type="danger" @click="handleBatchDelete" v-if="data.selectedIds.length">批量删除</el-button>
          </div>
        </div>
      </div>
    </div>

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
          <el-table-column type="selection" width="55" />
          <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="username-text">{{ scope.row.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="通知内容" min-width="300" show-overflow-tooltip>
            <template #default="scope">
              <div class="notification-content">
                <span class="table-cell-text">{{ scope.row.description }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="isRead" label="阅读状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.isRead === 1 ? 'success' : 'warning'" effect="light">
                {{ scope.row.isRead === 1 ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deletedAt" label="删除状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.deletedAt ? 'danger' : 'success'" effect="light">
                {{ scope.row.deletedAt ? '已删除' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sendTime" label="发送时间" min-width="160" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ formatDateTime(scope.row.sendTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="handleDelete(scope.row.id)">
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination 
          v-model:current-page="data.pageNum" 
          :page-size="data.pageSize" 
          :total="data.total"
          @current-change="load" 
          background 
          layout="total, prev, pager, next, sizes"
          :page-sizes="[10, 20, 50, 100]"
        />
      </div>
    </div>

    <el-dialog 
      title="编辑通知" 
      v-model="data.formVisible" 
      :width="dialogWidth" 
      :close-on-click-modal="false" 
      destroy-on-close
      class="custom-dialog"
    >
      <el-form :model="data.form" label-width="80px" ref="formRef" class="notification-form">
        <el-form-item label="通知内容" prop="description">
          <el-input 
            v-model="data.form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入通知内容"
            class="custom-textarea"
            resize="vertical"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isRead">
          <el-select v-model="data.form.isRead" placeholder="请选择状态" class="custom-select">
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
import { reactive, ref, computed } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  loading: false,
  description: null,
  username: null,
  formVisible: false,
  form: {},
  selectedIds: []
})

const formRef = ref()

// 响应式对话框宽度
const dialogWidth = computed(() => {
  if (window.innerWidth <= 480) return '95%'
  if (window.innerWidth <= 768) return '80%'
  return '40%'
})

// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/notification/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        description: data.description,
        username: data.username
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
  data.username = null
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
@import '@/assets/css/responsive-table.css';

.notification-container {
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

.action-buttons {
  @apply action-buttons;
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

.username-text {
  @apply username-text;
}

/* 通知内容特殊样式 */
.notification-content {
  max-width: 300px;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.4;
  padding: 4px 0;
}

.notification-content .table-cell-text {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-height: 4.2em; /* 3行的高度 */
}

/* 对话框样式 */
.custom-dialog {
  @apply custom-dialog;
}

.notification-form {
  @apply custom-form;
}

.custom-textarea {
  width: 100%;
}

.custom-select {
  @apply custom-input;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .notification-content {
    max-width: 200px;
  }
  
  .notification-content .table-cell-text {
    -webkit-line-clamp: 2;
    max-height: 2.8em;
  }
}

@media (max-width: 480px) {
  .notification-content {
    max-width: 150px;
  }
  
  .notification-content .table-cell-text {
    -webkit-line-clamp: 1;
    max-height: 1.4em;
  }
}
</style>