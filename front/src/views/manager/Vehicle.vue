<template>
  <div class="parking-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入车牌号查询" class="search-input" />
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
        <el-table-column prop="name" label="车牌号">
          <template #default="scope">
            <span class="plate-text">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="所属用户">
          <template #default="scope">
            <span class="user-text">{{ scope.row.userName }}</span>
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
    <el-dialog :title="data.form.id ? '编辑车辆' : '新增车辆'" v-model="data.formVisible" width="40%"
      :close-on-click-modal="false" destroy-on-close class="custom-dialog">
      <el-form :rules="data.rules" :model="data.form" label-width="80px" class="vehicle-form" ref="formRef">
        <el-form-item label="车牌号" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入车牌号" class="custom-input" />
        </el-form-item>
        <el-form-item label="所属用户" prop="userId" v-if="data.user.role === 'ADMIN'">
          <el-select v-model="data.form.userId" class="custom-select" placeholder="请选择所属用户">
            <el-option v-for="item in data.userList" :key="item.id" :value="item.id" :label="item.name" />
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
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 5,
  formVisible: false,
  form: {},
  ids: [],
  name: null,
  userList: [],
  loading: false,
  rules: {
    name: [
      { required: true, message: '请输入车牌号', trigger: 'blur' },
    ],
    userId: [
      { required: true, message: '请选择用户', trigger: 'blur' },
    ]
  }
})

const formRef = ref()

const loadUser = async () => {
  try {
    const res = await request.get('/user/selectAll')
    if (res.code === '200') {
      data.userList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('加载用户数据失败')
  }
}

// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/vehicle/selectPage', {
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
  if (data.user.role === 'USER') {
    data.form.userId = data.user.id
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
    const res = await request.post('/vehicle/add', data.form)
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
    const res = await request.put('/vehicle/update', data.form)
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
      const res = await request.delete('/vehicle/delete/' + id)
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
      const res = await request.delete('/vehicle/delete/batch', {
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
  formRef.value.validate(valid => {
    if (valid) {
      data.form.id ? update() : add()
    }
  })
}

const reset = () => {
  data.name = null
  load()
}

// 初始化
loadUser()
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

.plate-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

.user-text {
  font-size: 14px;
  color: #606266;
}

.custom-dialog {
  border-radius: 16px;
}

.vehicle-form {
  padding: 20px;
}

.custom-input,
.custom-select {
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