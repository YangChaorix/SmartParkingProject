<template>
  <div class="map-only-container">
    <div class="search-box">
      <el-input v-model="name" placeholder="请输入区域名称进行搜索" class="search-input" clearable @clear="onSearch" @keyup.enter="onSearch" />
      <el-button type="primary" @click="onSearch">搜索</el-button>
    </div>

    <div id="gaode-map"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from '@/utils/request';
import { getAMap } from "@/utils/amap"; // 引入新的地图加载器
import { ElMessage } from "element-plus";

let map = null;
let AMapInstance = null; // 存储AMap实例
let markers = [];
const name = ref(null);

// 添加标记点
const addMarkers = (data) => {
  if (!map || !AMapInstance) return;

  // 清除现有的标记点
  markers.forEach(marker => marker.remove());
  markers = [];

  if (data.length === 0) {
    if (name.value) { // 仅当是搜索行为后才提示
      ElMessage.warning('未找到相关区域');
    }
    return;
  }

  data.forEach(point => {
    const color = point.num > 5 ? '#32cd32' : (point.num > 0 ? '#ff8c00' : '#dc143c');
    const markerContent = `
      <div style="
        background-color: ${color};
        color: white;
        padding: 5px 10px;
        border-radius: 5px;
        font-weight: bold;
        border: 2px solid white;
        box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        font-size: 14px;
        white-space: nowrap;
        text-align: center;
      ">
        ${point.name}<br/>余位：${point.num}
      </div>
    `;
    const marker = new AMapInstance.Marker({
      position: [point.longitude, point.latitude],
      content: markerContent,
      offset: new AMapInstance.Pixel(-45, -30)
    });

    const infoWindow = new AMapInstance.InfoWindow({
      content: `
        <div>
          <h3>${point.name}</h3>
          <p>区域：${point.province}${point.city}${point.district}</p>
          <p>地址：${point.address}</p>
          <p>车位总数：<span style="color: blue;">${point.total}</span></p>
          <p>剩余车位：<span style="color: green;">${point.num}</span></p>
        </div>
      `,
      offset: new AMapInstance.Pixel(0, -30)
    });

    marker.on('click', () => {
      infoWindow.open(map, marker.getPosition());
    });

    markers.push(marker);
    map.add(marker);
  });

  // 调整地图视野以包含所有点
  if (markers.length > 0) {
    map.setFitView();
  }
};

// 加载数据并更新地图标记点
const loadData = async () => {
  try {
    const res = await request.get('/location/selectPage', {
      params: {
        name: name.value,
        pageNum: 1, // 加载全部数据用于地图显示
        pageSize: 1000 // 设置一个较大的值
      }
    });
    const data = res.data?.list || [];
    addMarkers(data);
  } catch (error) {
    ElMessage.error('加载数据失败');
    console.error('加载数据失败:', error);
  }
};

// 搜索按钮的触发函数
const onSearch = () => {
  loadData();
}

// 初始化地图
const initMap = async () => {
  try {
    AMapInstance = await getAMap(); // 等待AMap实例加载完成
    map = new AMapInstance.Map('gaode-map', {
      viewMode: '3D',
      zoom: 16,
      center: [121.480074, 31.229857] // 默认中心点
    });
    // 地图初始化完成后立即加载数据
    loadData();
  } catch (error) {
    console.error('地图初始化失败:', error);
    // ElMessage 已经在 getAMap 中处理
  }
};

// 页面挂载时初始化地图
onMounted(() => {
  initMap();
});
</script>

<style scoped>
.map-only-container {
  position: relative;
  width: 100%;
  height: 100vh;
}

#gaode-map {
  width: 100%;
  height: 100%;
}

.search-box {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  display: flex;
  gap: 10px;
  background-color: white;
  padding: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-input {
  width: 300px;
}
</style>