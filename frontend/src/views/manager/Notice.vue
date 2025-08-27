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
      <el-table :data="data.tableData" stripe class="custom-table" v-loading="data.loading"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="标题">
          <template #default="scope">
            <span class="title-text">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容">
          <template #default="scope">
            <span class="content-text">{{ scope.row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="发布时间">
          <template #default="scope">
            <span class="time-text">{{ scope.row.time }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" link @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button type="danger" link @click="del(scope.row.id)">
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

    <!-- Dialog -->
    <el-dialog :title="data.form.id ? '编辑公告' : '新增公告'" v-model="data.formVisible" width="40%"
      :close-on-click-modal="false" destroy-on-close class="custom-dialog">
      <el-form :model="data.form" label-width="80px" class="notice-form" ref="formRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="data.form.title" placeholder="请输入标题" class="custom-input" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="data.form.content" placeholder="请输入内容" class="custom-input" :rows="4" />
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
  data.form = {}
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
.notice-container {
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
  flex-wrap: wrap;
}

.search-input {
  width: 240px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
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

.title-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

.content-text {
  font-size: 14px;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.time-text {
  font-size: 14px;
  color: #909399;
}

.custom-dialog {
  border-radius: 16px;
}

.notice-form {
  padding: 20px;
}

.custom-input {
  width: 100%;
}

.el-button {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
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