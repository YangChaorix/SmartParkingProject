<template>
  <div class="parking-container">
    <!-- Search Section -->
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入车位编号查询" class="search-input" />
          <el-select v-model="data.locationId" placeholder="请选择所属区域筛选" clearable class="search-select">
            <el-option v-for="item in data.locationList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <div class="action-buttons" v-if="data.user.role === 'ADMIN'">
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
          <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN'" />
          <el-table-column prop="name" label="车位编号" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="locationName" label="所属区域" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <span class="location-text">{{ scope.row.locationName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="车位状态" width="120" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '空闲' ? 'success' : 'danger'" effect="light">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" v-if="data.user.role === 'ADMIN'">
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
          :page-sizes="[10, 20, 50, 100]"
        />
      </div>
    </div>

    <!-- Dialog -->
    <el-dialog :title="data.form.id ? '编辑车位' : '新增车位'" v-model="data.formVisible" width="40%"
      :close-on-click-modal="false" destroy-on-close class="custom-dialog">
      <el-form :rules="data.rules" :model="data.form" label-width="80px" class="parking-form" ref="formRef">
        <el-form-item label="车位编号" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入车位编号" class="custom-input" />
        </el-form-item>
        <el-form-item label="所属区域" prop="locationId">
          <el-select v-model="data.form.locationId" class="custom-select" placeholder="请选择所属区域">
            <el-option v-for="item in data.locationList" :key="item.id" :value="item.id" :label="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="车位状态" prop="status">
          <el-radio-group v-model="data.form.status" size="large" class="custom-radio-group">
            <el-radio-button label="空闲">空闲</el-radio-button>
            <el-radio-button label="占用">占用</el-radio-button>
          </el-radio-group>
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
  pageSize: 10,
  formVisible: false,
  form: {},
  ids: [],
  name: null,
  locationList: [],
  loading: false,
  rules: {
    name: [
      { required: true, message: '请输入车位编号', trigger: 'blur' },
    ],
    locationId: [
      { required: true, message: '请选择车位区域', trigger: 'blur' },
    ],
  }
})

const formRef = ref()

const loadLocation = async () => {
  try {
    const res = await request.get('/location/selectAll')
    if (res.code === '200') {
      data.locationList = res.data
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    ElMessage.error('加载区域数据失败')
  }
}

// 加载表格数据
const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/parkingLot/selectPage', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        name: data.name,
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

// 打开新增弹窗
const handleAdd = () => {
  data.form = {
    status: '空闲'
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
    const res = await request.post('/parkingLot/add', data.form)
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
    const res = await request.put('/parkingLot/update', data.form)
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
      const res = await request.delete('/parkingLot/delete/' + id)
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
      const res = await request.delete('/parkingLot/delete/batch', {
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
  data.locationId = null
  load()
}

// 初始化
loadLocation()
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

.custom-dialog {
  @apply custom-dialog;
}

.parking-form {
  @apply custom-form;
}

.custom-input,
.custom-select {
  @apply custom-input;
}

.custom-radio-group {
  margin-top: 8px;
}

.location-text {
  @apply location-text;
}
</style>