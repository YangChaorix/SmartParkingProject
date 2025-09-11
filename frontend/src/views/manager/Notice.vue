<template>
  <div class="notice-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入标题查询" class="search-input" />
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <div class="action-buttons">
            <el-button type="success" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
          </div>
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
          <el-table-column type="selection" width="55" />
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <span class="title-text">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip>
            <template #default="scope">
              <div class="notice-content">
                <span class="content-text">{{ scope.row.content }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="发布时间" min-width="160" show-overflow-tooltip>
            <template #default="scope">
              <span class="time-text">{{ scope.row.time }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <div class="operation-buttons">
                <el-button type="primary" link size="small" @click="handleEdit(scope.row)">
                  编辑
                </el-button>
                <el-button type="danger" link size="small" @click="del(scope.row.id)">
                  删除
                </el-button>
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

    <!-- Dialog -->
    <el-dialog 
      :title="data.form.id ? '编辑公告' : '新增公告'" 
      v-model="data.formVisible" 
      :width="dialogWidth"
      :close-on-click-modal="false" 
      destroy-on-close 
      class="custom-dialog"
    >
      <el-form :model="data.form" label-width="80px" class="notice-form" ref="formRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入标题" class="custom-input" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input 
            type="textarea" 
            v-model="data.form.content" 
            placeholder="请输入内容" 
            class="custom-textarea" 
            :rows="4"
            resize="vertical"
          />
        </el-form-item>
        <el-form-item label="发布时间" prop="time">
          <el-date-picker
            v-model="data.form.time"
            type="datetime"
            placeholder="请选择发布时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            class="custom-date-picker"
            style="width: 100%"
          />
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
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  formVisible: false,
  form: {},
  ids: [],
  name: null,
  loading: false,
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
    const res = await request.get('/notice/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        title: data.name
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
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  data.form = {
    time: `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }
  data.formVisible = true
}

// 打开编辑弹窗
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增
const add = async () => {
  try {
    const res = await request.post('/notice/add', data.form)
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('新增成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('新增失败')
  }
}

// 更新
const update = async () => {
  try {
    const res = await request.put('/notice/update', data.form)
    if (res.code === '200') {
      data.formVisible = false
      ElMessage.success('更新成功')
      load()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

// 删除
const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await request.delete('/notice/delete/' + id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => { })
}

// 批量删除
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择要删除的数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '批量删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await request.delete('/notice/delete/batch', {
        data: data.ids
      })
      if (res.code === '200') {
        ElMessage.success('批量删除成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  }).catch(() => { })
}

const save = () => {
  data.form.id ? update() : add()
}

const reset = () => {
  data.name = null
  load()
}

// 初始化
load()
</script>

<style scoped>
@import '@/assets/css/responsive-table.css';

.notice-container {
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

.operation-buttons {
  @apply operation-buttons;
}

.pagination-wrapper {
  @apply pagination-wrapper;
}

.title-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

.time-text {
  font-size: 14px;
  color: #909399;
}

/* 公告内容特殊样式 */
.notice-content {
  max-width: 300px;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.4;
  padding: 4px 0;
}

.content-text {
  font-size: 14px;
  color: #606266;
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

.notice-form {
  @apply custom-form;
}

.custom-input {
  @apply custom-input;
}

.custom-textarea {
  width: 100%;
}

.custom-date-picker {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .notice-content {
    max-width: 200px;
  }
  
  .content-text {
    -webkit-line-clamp: 2;
    max-height: 2.8em;
  }
}

@media (max-width: 480px) {
  .notice-content {
    max-width: 150px;
  }
  
  .content-text {
    -webkit-line-clamp: 1;
    max-height: 1.4em;
  }
}
</style>