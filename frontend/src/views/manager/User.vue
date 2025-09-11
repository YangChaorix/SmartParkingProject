<template>
  <div class="user-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入关键字查询" class="search-input" />
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
          <el-table-column prop="username" label="账号" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="username-text">{{ scope.row.username }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" min-width="100" show-overflow-tooltip>
            <template #default="scope">
              <span class="name-text">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="avatar" label="头像" width="80" align="center">
            <template #default="scope">
              <el-image v-if="scope.row.avatar" class="avatar-image" :src="getAvatarUrl(scope.row.avatar)"
                :preview-src-list="[getAvatarUrl(scope.row.avatar)]" preview-teleported fit="cover" />
              <el-icon v-else class="avatar-placeholder">
                <User />
              </el-icon>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="角色" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'" effect="light">
                {{ scope.row.role }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="电话" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="info-text">{{ scope.row.phone }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip>
            <template #default="scope">
              <span class="info-text">{{ scope.row.email }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="account" label="余额" width="100" align="center">
            <template #default="scope">
              <span class="account-text">¥{{ scope.row.account }}</span>
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
    <el-dialog :title="data.form.id ? '编辑用户' : '新增用户'" v-model="data.formVisible" width="40%"
      :close-on-click-modal="false" destroy-on-close class="custom-dialog">
      <el-form :model="data.form" label-width="80px" class="user-form" ref="formRef">
        <el-form-item label="账号" prop="username">
          <el-input v-model="data.form.username" placeholder="请输入账号" class="custom-input" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入姓名" class="custom-input" />
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
        <el-form-item label="电话" prop="phone">
          <el-input v-model="data.form.phone" placeholder="请输入电话" class="custom-input" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="data.form.email" placeholder="请输入邮箱" class="custom-input" />
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
import { Plus, User } from '@element-plus/icons-vue'

const baseUrl = import.meta.env.VITE_BASE_URL
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
    const res = await request.get('/user/selectPage', {
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
    const res = await request.post('/user/add', data.form)
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
    const res = await request.put('/user/update', data.form)
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
      const res = await request.delete('/user/delete/' + id)
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
      const res = await request.delete('/user/delete/batch', {
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

// 头像上传
const handleFileUpload = (res) => {
  data.form.avatar = res.data
}

const reset = () => {
  data.name = null
  load()
}

// 获取头像URL，确保路径正确
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  
  // 如果已经是完整URL，直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  
  // 如果是相对路径，添加基础URL
  const baseUrl = import.meta.env.VITE_BASE_URL || ''
  return baseUrl + avatar
}

// 初始化
load()
</script>

<style scoped>
@import '@/assets/css/responsive-table.css';

.user-container {
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

.custom-dialog {
  @apply custom-dialog;
}

.user-form {
  @apply custom-form;
}

.custom-input {
  @apply custom-input;
}

.username-text {
  @apply username-text;
}

.name-text {
  @apply name-text;
}

.info-text {
  @apply info-text;
}

.account-text {
  @apply account-text;
}

.avatar-image {
  @apply avatar-image;
}

.avatar-placeholder {
  font-size: 24px;
  color: #c0c4cc;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f5f7fa;
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
</style>