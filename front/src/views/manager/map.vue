<template>
  <div id="gaode-map"><title>gaode</title></div>
</template>

<script setup>
import request from '@/utils/request';
import AMapLoader from '@amap/amap-jsapi-loader';

// 高德地图 Web 端开发者 Key
const apiKey = '33db54a7c97834abfcc4331c062a06fe';
let map = null;
let markers = [];

// 添加标记点
const addMarkers = (data) => {
  if (!map) return;

  // 清除现有的标记点
  markers.forEach(marker => {
    marker.remove();
  });
  markers = [];

  // 添加新的标记点
  data.forEach(point => {
    const marker = new AMap.Marker({
      position: [point.longitude, point.latitude],
      title: point.name,
      map: map
    });

    // 添加信息窗体
    const infoWindow = new AMap.InfoWindow({
      content: `
        <div>
          <h3>${point.name}</h3>
          <p>区域：${point.province}${point.city}${point.district}</p>
          <p>地址：${point.address}</p>
        </div>
      `,
      offset: new AMap.Pixel(0, -30)
    });

    // 点击标记时显示信息窗体
    marker.on('click', () => {
      infoWindow.open(map, marker.getPosition());
    });

    markers.push(marker);
  });
};

// 初始化地图函数
const initMap = async () => {
  try {
    const AMap = await AMapLoader.load({
      key: apiKey,
      version: '2.0'
    });

    // 创建地图实例
    map = new AMap.Map('gaode-map', {
      viewMode: '3D',
      zoom: 16,
      center: [121.480074, 31.229857] // 人民广场经纬度
    });

    // 初始化完成后立即获取并显示标记点
    const res = await request.get('/location/map');
    console.log(JSON.stringify(res.data, null, 2));
    addMarkers(res.data);
  } catch (error) {
    console.error('Error:', error);
  }
};

// 初始化地图
initMap();
</script>

<style scoped>
#gaode-map {
  width: 100%;
  height: 100%;
  padding: 0px;
  margin: 0px;
}
</style>