<template>
  <div class="parking-container">
    <div class="search-card">
      <div class="search-wrapper">
        <div class="search-inputs">
          <el-input v-model="data.name" placeholder="请输入区域名称查询" class="search-input" />
          <el-button type="primary" @click="load">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <div class="action-buttons" v-if="data.user.role === 'ADMIN'">
            <el-button type="success" @click="handleAdd">新增</el-button>
            <el-button type="danger" @click="delBatch">批量删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="data.tableData" stripe class="custom-table" v-loading="data.loading"
                @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN'" />
        <el-table-column prop="name" label="区域名称">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="总车位数">
          <template #default="scope">
            <span class="total-text">{{ scope.row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="num" label="空闲车位">
          <template #default="scope">
            <el-tag :type="scope.row.num === 0 ? 'danger' : scope.row.num < 5 ? 'warning' : 'success'" effect="light">
              {{ scope.row.num }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="地址名称" prop="address" min-width="180">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.address || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="经纬度" prop="latitude" min-width="180">
          <template #default="scope">
            <span class="table-cell-text">{{ scope.row.longitude ? `${scope.row.longitude},${scope.row.latitude}` : '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" v-if="data.user.role === 'ADMIN'">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="success" link @click="handlePricingRules(scope.row)">计费规则</el-button>
              <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" link @click="del(scope.row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="data.total">
        <el-pagination
            v-model:current-page="data.pageNum"
            :page-size="data.pageSize"
            :total="data.total"
            @current-change="load"
            background
            layout="total, prev, pager, next"
        />
      </div>
    </div>

    <el-dialog
        :title="data.form.id ? '编辑区域' : '新增区域'"
        v-model="data.formVisible"
        width="60%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog"
    >
      <el-form :rules="data.rules" :model="data.form" label-width="80px" class="location-form" ref="formRef">
        <el-form-item label="区域名称" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入区域名称" class="custom-input" />
        </el-form-item>
        <el-form-item label="经纬度">
          <div class="location-input-wrapper">
            <el-input v-model="locationText" placeholder="请选择位置获取经纬度" readonly class="custom-input" />
            <el-button type="primary" @click="openMapDialog">选择位置</el-button>
          </div>
        </el-form-item>
        <el-form-item label="地址名称">
          <el-input v-model="data.form.address" placeholder="请选择位置获取地址" readonly class="custom-input" />
        </el-form-item>
        <input type="hidden" v-model="data.form.province" />
        <input type="hidden" v-model="data.form.city" />
        <input type="hidden" v-model="data.form.district" />
        <input type="hidden" v-model="data.form.addressComponent" />
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog
        title="选择地图位置"
        v-model="data.mapDialogVisible"
        width="80%"
        top="8vh"
        :close-on-click-modal="false"
        destroy-on-close
    >
      <div class="map-search-container">
        <el-input
            v-model="searchQuery"
            placeholder="请输入地点名称进行搜索"
            class="map-search-input"
            @keyup.enter="searchLocation"
        >
          <template #append>
            <el-button @click="searchLocation" :loading="isSearching">搜索</el-button>
          </template>
        </el-input>
        <div class="search-results" v-if="searchResults.length > 0">
          <div class="result-item" v-for="item in searchResults" :key="item.id" @click="selectSearchResult(item)">
            <div class="result-title">{{ item.name }}</div>
            <div class="result-address">{{ item.address }}</div>
          </div>
        </div>
      </div>
      <div id="map-container" style="width: 100%; height: 400px;"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.mapDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveLocation">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 配置计费规则弹窗 -->
    <el-dialog title="配置计费规则" v-model="data.pricingRulesVisible" width="40%" destroy-on-close>
      <el-form :model="data.pricingRulesForm" label-width="120px">
        <el-form-item label="免费停车时长">
          <el-input v-model.number="data.pricingRulesForm.freeMinutes" placeholder="请输入免费停车时长">
            <template #append>分钟</template>
          </el-input>
        </el-form-item>
        <el-form-item label="首段时长">
          <el-input v-model.number="data.pricingRulesForm.firstTierMinutes" placeholder="请输入首段计费时长">
            <template #append>分钟</template>
          </el-input>
        </el-form-item>
        <el-form-item label="首段价格">
          <el-input v-model.number="data.pricingRulesForm.firstTierPrice" placeholder="请输入首段每分钟价格">
            <template #append>元/分钟</template>
          </el-input>
        </el-form-item>
        <el-form-item label="后续价格">
          <el-input v-model.number="data.pricingRulesForm.secondTierPrice" placeholder="请输入后续每分钟价格">
            <template #append>元/分钟</template>
          </el-input>
        </el-form-item>
        <el-form-item label="每日上限">
          <el-input v-model.number="data.pricingRulesForm.dailyCap" placeholder="请输入每日收费上限">
            <template #append>元</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.pricingRulesVisible = false">取消</el-button>
          <el-button type="primary" @click="savePricingRules">保存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { reactive, ref, nextTick, computed } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"
import { getAMap } from "@/utils/amap"; // 引入新的地图加载器

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  formVisible: false,
  form: {
    name: '',
    address: '',
    longitude: null,
    latitude: null,
    province: '',
    city: '',
    district: '',
    addressComponent: '',
  },
  ids: [],
  name: null,
  loading: false,
  rules: {
    name: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
  },
  mapDialogVisible: false,
})

const formRef = ref()
let map = null
let marker = null
let placeSearch = null
let geocoder = null

const searchQuery = ref('')
const searchResults = ref([])
const isSearching = ref(false)

const locationText = computed(() => {
  if (data.form.longitude && data.form.latitude) {
    return `${data.form.longitude},${data.form.latitude}`
  }
  return ''
})

const formatAddress = (regeocode) => {
  if (!regeocode) {
    data.form.province = ''
    data.form.city = ''
    data.form.district = ''
    data.form.addressComponent = ''
    return ''
  }
  const ac = regeocode.addressComponent || {}
  data.form.province = ac.province || ''
  data.form.city = ac.city || ac.province || ''
  data.form.district = ac.district || ''

  try {
    data.form.addressComponent = JSON.stringify(ac)
  } catch {
    data.form.addressComponent = ''
  }
  return regeocode.formattedAddress || ''
}

const load = async () => {
  data.loading = true
  try {
    const res = await request.get('/location/selectPage', {
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

const handleAdd = () => {
  data.form = {
    name: '',
    address: '',
    longitude: null,
    latitude: null,
    province: '',
    city: '',
    district: '',
    addressComponent: '',
  }
  data.formVisible = true
}

const handleEdit = (row) => {
  const defaults = {
    province: '',
    city: '',
    district: '',
    addressComponent: '',
  }
  data.form = Object.assign({}, defaults, JSON.parse(JSON.stringify(row)))
  data.formVisible = true
}

// 区域计费规则
const handlePricingRules = (row) => {
  const pricing = {
    freeMinutes: 30,
    firstTierMinutes: 120,
    firstTierPrice: 0.08,
    secondTierPrice: 0.05,
    dailyCap: 50
  };
  data.form = JSON.parse(JSON.stringify(row));
  if (data.form.pricingRules) {
    try {
      data.pricingRulesForm = JSON.parse(data.form.pricingRules);
    } catch (e) {
      console.error("Failed to parse pricingRules JSON:", e);
      data.pricingRulesForm = pricing;
    }
  } else {
    data.pricingRulesForm = pricing;
  }
  data.pricingRulesVisible = true;
};

// 更新计费规则
const savePricingRules = async () => {
  const updatedData = {
    id: data.form.id, // 关键：只传入 id
    pricingRules: JSON.stringify(data.pricingRulesForm) // 关键：只传入 pricingRules
  };
  try {
    // 将精简后的对象发送给后端
    const res = await request.put('/location/update', updatedData);
    if (res.code === '200') {
      ElMessage.success('计费规则保存成功');
      data.pricingRulesVisible = false;
      load(); // 刷新列表
    } else {
      ElMessage.error(res.msg);
    }
  } catch (error) {
    ElMessage.error('保存失败');
  }
};

const add = async () => {
  try {
    const res = await request.post('/location/add', data.form)
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
// 区域计费规则

const update = async () => {
  try {
    const res = await request.put('/location/update', data.form)
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

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗?', '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    try {
      const res = await request.delete('/location/delete/' + id)
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
      const res = await request.delete('/location/delete/batch', {
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

const openMapDialog = () => {
  data.mapDialogVisible = true
  searchQuery.value = ''
  searchResults.value = []
  nextTick(() => {
    initMap()
  })
}

const initMap = () => {
  getAMap().then(AMap => {
    map = new AMap.Map('map-container', {
      zoom: 12,
      center: [data.form.longitude || 121.473018, data.form.latitude || 31.232152],
    });

    if (data.form.longitude && data.form.latitude) {
      marker = new AMap.Marker({
        position: [data.form.longitude, data.form.latitude],
        map: map,
      });
    }

    geocoder = new AMap.Geocoder({
      city: '全国'
    })

    map.on('click', (e) => {
      const { lng, lat } = e.lnglat;
      data.form.longitude = lng;
      data.form.latitude = lat;

      if (marker) {
        marker.setPosition([lng, lat]);
      } else {
        marker = new AMap.Marker({
          position: [lng, lat],
          map: map,
        });
      }

      geocoder.getAddress(e.lnglat, (status, result) => {
        if (status === 'complete' && result.regeocode) {
          data.form.address = formatAddress(result.regeocode)
          ElMessage.success(`已通过点击地图选择位置：${data.form.address}`)
        } else {
          data.form.address = '地址解析失败'
          formatAddress(null); // 清空地址相关字段
          ElMessage.warning('地址解析失败')
        }
      })
    });

    placeSearch = new AMap.PlaceSearch({
      pageSize: 5,
      pageIndex: 1,
      city: '全国',
      map: map,
      panel: '',
    });

  }).catch(e => {
    console.error("地图初始化失败", e);
  });
}

const searchLocation = () => {
  if (!searchQuery.value) {
    ElMessage.warning('请输入搜索内容')
    return
  }
  isSearching.value = true
  placeSearch.search(searchQuery.value, (status, result) => {
    isSearching.value = false
    if (status === 'complete' && result.pois.length > 0) {
      searchResults.value = result.pois
      ElMessage.success(`找到 ${result.pois.length} 个结果`)
    } else {
      searchResults.value = []
      ElMessage.warning('没有找到相关结果')
    }
  })
}

const selectSearchResult = (poi) => {
  const { location, name } = poi
  const { lng, lat } = location

  data.form.longitude = lng
  data.form.latitude = lat

  geocoder.getAddress([lng, lat], (status, result) => {
    if (status === 'complete' && result.regeocode) {
      data.form.address = formatAddress(result.regeocode)
      ElMessage.success(`已选择 ${name}，地址：${data.form.address}`)
    } else {
      data.form.address = poi.address ? `${name} (${poi.address})` : name;
      formatAddress(null); // 清空地址相关字段
      ElMessage.warning('地址解析失败，已使用原始地址')
    }
  });

  map.setCenter([lng, lat])
  if (marker) {
    marker.setPosition([lng, lat])
  } else {
    marker = new AMap.Marker({
      position: [lng, lat],
      map: map,
    })
  }

  searchResults.value = []
}

const saveLocation = () => {
  if (data.form.longitude && data.form.latitude) {
    data.mapDialogVisible = false
    ElMessage.success(`已选择位置：${data.form.address}`)
  } else {
    ElMessage.warning('请在地图上点击或搜索选择一个位置')
  }
}

load()
</script>

<style scoped>
/* 样式部分保持不变 */
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
  box-shadow: 4px 16px 0 rgba(0, 0, 0, 0.08);
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

.table-cell-text {
  font-size: 14px;
  color: #606266;
}

.total-text {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

.custom-dialog {
  border-radius: 16px;
}

.location-form {
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

.el-tag {
  border-radius: 6px;
  padding: 4px 8px;
}

.pagination-wrapper {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

.location-input-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.map-search-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}

.map-search-input {
  width: 100%;
}

.search-results {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.result-item {
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.2s ease;
}

.result-item:hover {
  background-color: #f5f7fa;
}

.result-title {
  font-weight: bold;
  font-size: 14px;
  color: #303133;
}

.result-address {
  font-size: 12px;
  color: #909399;
}
</style>