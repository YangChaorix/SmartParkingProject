<template>
  <div class="page-container">
    <van-nav-bar
        title="我的车辆"
        left-text="返回"
        left-arrow
        @click-left="$router.back()"
        right-text="新增车辆"
        @click-right="onAdd"
    />

    <van-empty v-if="vehicleList.length === 0" description="您还没有添加车辆" />

    <div class="vehicle-list">
      <div v-for="(item, index) in vehicleList" :key="item.id" class="vehicle-card">
        <div class="vehicle-header">
          <div class="vehicle-info">
            <div class="vehicle-name">{{ item.name }}</div>
            <div class="vehicle-time">添加时间：{{ formatTime(item.createdAt) }}</div>
          </div>
    <div class="vehicle-actions">
      <van-button size="mini" type="primary" @click="handleEdit(item)">编辑</van-button>
      <van-button size="mini" type="danger" @click="handleDelete(item.id)">删除</van-button>
    </div>
        </div>
      </div>
    </div>

    <van-dialog v-model:show="showDialog" :title="isEdit ? '编辑车辆' : '新增车辆'" show-cancel-button @confirm="saveVehicle">
      <van-field v-model="form.name" label="车牌号" placeholder="请输入车牌号" />
    </van-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, inject } from 'vue';
import { showSuccessToast, showFailToast, showConfirmDialog } from 'vant';

const $request = inject('$request');
const vehicleList = ref([]);
const showDialog = ref(false);
const isEdit = ref(false);
const form = reactive({ id: null, name: '' });
const user = JSON.parse(localStorage.getItem('user') || '{}');

const loadVehicles = () => {
  $request.get('/vehicle/selectPage')
      .then(res => {
        if (res.code === '200') {
          vehicleList.value = res.data.list;
        }
      });
};

const onAdd = () => {
  form.id = null;
  form.name = '';
  isEdit.value = false;
  showDialog.value = true;
};

const handleEdit = (vehicle) => {
  Object.assign(form, vehicle);
  isEdit.value = true;
  showDialog.value = true;
};

const saveVehicle = () => {
  if (isEdit.value) {
    // 编辑使用PUT方法
    $request.put('/vehicle/update', form).then(res => {
      if (res.code === '200') {
        showSuccessToast('更新成功');
        showDialog.value = false;
        // 延迟重新加载列表，确保toast显示完成
        setTimeout(() => {
          loadVehicles();
        }, 100);
      } else {
        showFailToast(res.msg);
      }
    });
  } else {
    // 新增使用POST方法
    $request.post('/vehicle/add', form).then(res => {
      if (res.code === '200') {
        showSuccessToast('添加成功');
        showDialog.value = false;
        // 延迟重新加载列表，确保toast显示完成
        setTimeout(() => {
          loadVehicles();
        }, 100);
      } else {
        showFailToast(res.msg);
      }
    });
  }
};

const handleDelete = (id) => {
  showConfirmDialog({ title: '确认删除', message: '您确定要删除这辆车吗？' })
      .then(() => {
        $request.delete(`/vehicle/delete/${id}`).then(res => {
          if (res.code === '200') {
            showSuccessToast('删除成功');
            loadVehicles(); // 重新加载列表
          } else {
            showFailToast(res.msg);
          }
        });
      });
};

const formatTime = (timeStr) => {
  if (!timeStr) return '--';
  return timeStr.replace('T', ' ').substring(0, 19);
};

onMounted(loadVehicles);
</script>

<style scoped>
.page-container {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.vehicle-list {
  padding: 15px;
}

.vehicle-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.vehicle-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.vehicle-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

/* 统一卡片背景色 */
.vehicle-header {
  background: var(--van-card-background);
  color: var(--van-text-color);
}

.vehicle-info {
  flex: 1;
}

.vehicle-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.vehicle-time {
  font-size: 14px;
  opacity: 0.7;
  display: flex;
  align-items: center;
}

.vehicle-time::before {
  content: '🕒';
  margin-right: 6px;
  font-size: 12px;
}

.vehicle-actions {
  display: flex;
  gap: 6px;
  margin-left: 15px;
}

.vehicle-actions .van-button {
  min-width: 40px;
  height: 24px;
  font-size: 11px;
  font-weight: 400;
  border-radius: 4px;
  padding: 2px 6px;
}

/* 自定义Toast样式 */

/* 响应式设计 */
@media (max-width: 375px) {
  .vehicle-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .vehicle-actions {
    margin-left: 0;
    width: 100%;
    justify-content: flex-end;
  }
  
  .vehicle-name {
    font-size: 18px;
  }
  
  .vehicle-card {
    margin-bottom: 12px;
  }
}
</style>