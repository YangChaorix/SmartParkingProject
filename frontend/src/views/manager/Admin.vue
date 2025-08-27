<template>
  <div class="admin-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入姓名查询" class="search-input">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
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
        <el-table-column prop="username" label="账号">
          <template #default="scope">
            <span class="username-text">{{ scope.row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称">
          <template #default="scope">
            <span class="name-text">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="avatar" label="头像">
          <template #default="scope">
            <el-image v-if="scope.row.avatar" class="avatar-image" :src="scope.row.avatar"
              :preview-src-list="[scope.row.avatar]" preview-teleported fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话">
          <template #default="scope">
            <span class="info-text">{{ scope.row.phone }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱">
          <template #default="scope">
            <span class="info-text">{{ scope.row.email }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag type="danger" effect="light">
              {{ scope.row.role }}
            </el-tag>
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
    <el-dialog :title="data.form.id ? '编辑管理员' : '新增管理员'" v-model="data.formVisible" width="40%"
      :close-on-click-modal="false" destroy-on-close class="custom-dialog">
      <el-form ref="formRef" :model="data.form" label-width="80px" class="admin-form">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="data.form.username" placeholder="请输入用户名" class="custom-input" />
        </el-form-item>
        <el-form-item prop="name" label="名称">
          <el-input v-model="data.form.name" placeholder="请输入名称" class="custom-input" />
        </el-form-item>
        <el-form-item prop="phone" label="电话">
          <el-input v-model="data.form.phone" placeholder="请输入电话" class="custom-input" />
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="data.form.email" placeholder="请输入邮箱" class="custom-input" />
        </el-form-item>
        <el-form-item prop="avatar" label="头像">
          <el-upload class="avatar-uploader" :action="baseUrl + '/files/upload'" :on-success="handleFileUpload"
            list-type="picture-card" :limit="1">
            <div v-if="data.form.avatar" class="avatar-preview">
              <el-image :src="data.form.avatar" fit="cover" class="avatar-image" />
            </div>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
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
import { Plus, Search } from '@element-plus/icons-vue'

const baseUrl = import.meta.env.VITE_BASE_URL

const data = reactive({
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
    const res = await request.get('/admin/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        name: data.name
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
    const res = await request.post('/admin/add', data.form)
    if (res.code === '200') {
      ElMessage.success('新增成功')
      data.formVisible = false
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
    const res = await request.put('/admin/update', data.form)
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

// 删除
const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await request.delete('/admin/delete/' + id)
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

// 批量选择id
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

// 批量删除
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
      const res = await request.delete('/admin/delete/batch', {
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

// 保存
const save = () => {
  data.form.id ? update() : add()
}

// 头像上传
const handleFileUpload = (res) => {
  data.form.avatar = res.data
}

// 搜索重置
const reset = () => {
  data.name = ''
  load()
}

// 初始化
load()
</script>

<style scoped>
.admin-container {
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

.username-text {
  font-weight: 500;
  color: #409EFF;
}

.name-text {
  font-weight: 500;
  color: #606266;
}

.info-text {
  color: #606266;
}

.avatar-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.avatar-image:hover {
  transform: scale(1.1);
}

.custom-dialog {
  border-radius: 16px;
}

.admin-form {
  padding: 20px;
}

.custom-input {
  width: 100%;
}

.avatar-uploader {
  text-align: center;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  position: relative;
}

.avatar-preview .avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 6px;
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