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

    <van-list>
      <van-cell v-for="item in vehicleList" :key="item.id">
        <van-card :title="item.name">
          <template #footer>
            <van-button size="mini" @click="handleEdit(item)">编辑</van-button>
            <van-button size="mini" type="danger" @click="handleDelete(item.id)">删除</van-button>
          </template>
        </van-card>
      </van-cell>
    </van-list>

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
  $request.get('/vehicle/selectPage', { params: { userId: user.id } })
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
  form.userId = user.id;
  const url = isEdit.value ? '/vehicle/update' : '/vehicle/add';
  $request.post(url, form).then(res => {
    if (res.code === '200') {
      showSuccessToast('保存成功');
      loadVehicles(); // 重新加载列表
    } else {
      showFailToast(res.msg);
    }
  });
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

onMounted(loadVehicles);
</script>