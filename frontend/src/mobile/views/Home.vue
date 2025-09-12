<template>
  <div class="page-container">
    <van-nav-bar title="智慧停车首页" />

    <!-- 顶部统计卡片 -->
    <div class="parking-stats-card">
      <div class="stats-header">
        <van-icon name="location-o" size="20" color="#1989fa" />
        <span class="stats-title">停车数据统计</span>
      </div>
      <div class="stats-content">
        <div class="stat-item">
          <div class="stat-label">空闲车位</div>
          <div class="stat-value available">{{ parkingStats.availableLots }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">总车位</div>
          <div class="stat-value total">{{ parkingStats.totalLots }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">停车区域</div>
          <div class="stat-value regions">{{ locations.length }}</div>
        </div>
      </div>
    </div>


    <!-- 系统公告 -->
    <van-cell-group inset title="系统公告" style="margin-top: 15px;">
      <van-notice-bar
          v-if="noticeList.length > 0"
          left-icon="volume-o"
          text="系统公告"
          style="margin-bottom: 10px;"
      />
      <template v-if="noticeList.length > 0">
        <van-cell v-for="item in noticeList" :key="item.id" :title="item.title" is-link :to="'/notice/' + item.id" />
      </template>
      <div v-else class="no-notice">
        <van-icon name="info-o" size="16" color="#999" />
        <span class="no-notice-text">暂无公告信息</span>
      </div>
    </van-cell-group>


    <!-- 停车区域展示 -->
    <van-cell-group inset title="停车区域" style="margin-top: 15px;">
      <!-- 地图切换按钮 -->
      <div class="map-toggle-container">
        <van-button 
          :type="showMap ? 'primary' : 'default'" 
          size="small" 
          @click="toggleMap"
          :icon="showMap ? 'location' : 'location-o'"
        >
          {{ showMap ? '隐藏地图' : '查看地图' }}
        </van-button>
        <van-button 
          v-if="showMap" 
          type="success" 
          size="small" 
          @click="refreshMap"
          icon="refresh"
          style="margin-left: 8px;"
        >
          刷新
        </van-button>
      </div>
      
      <!-- 地图容器 -->
      <div v-if="showMap" class="map-container">
        <div id="mobile-parking-map" class="parking-map"></div>
        <div v-if="mapLoading" class="map-loading">
          <van-loading type="spinner" size="24px" />
          <span style="margin-left: 8px;">地图加载中...</span>
        </div>
      </div>
      
      <!-- 停车区域列表 -->
      <div v-if="locations.length > 0" class="location-list-container">
        <van-cell 
          v-for="loc in locations.slice(0, 3)" 
          :key="loc.id"
          :title="loc.name"
          :label="`总数: ${(locationStats[loc.id]?.total)||0}，空闲: ${(locationStats[loc.id]?.free)||0}，占用: ${(locationStats[loc.id]?.occupied)||0}`"
          @click="onLocationClick(loc)"
          class="location-cell"
        />
        <van-cell 
          v-if="locations.length > 3" 
          title="查看更多区域" 
          is-link 
          @click="showAllLocations = true"
          class="more-locations-cell"
        />
      </div>
      <van-empty v-else description="暂无停车区域信息" />
    </van-cell-group>
    
    <!-- 附近停车位弹窗 -->
    <van-popup 
      v-model:show="showAllLocations" 
      position="bottom" 
      :style="{ height: '70%' }"
      round
    >
      <div class="nearby-parking-popup">
        <div class="popup-header">
          <h3>附近停车位</h3>
          <van-button 
            type="primary" 
            size="small" 
            @click="getNearbyParking"
            :loading="nearbyLoading"
            icon="location"
          >
            获取附近车位
          </van-button>
        </div>
        
        <div class="nearby-list">
          <van-cell 
            v-for="location in nearbyLocations" 
            :key="location.id"
            :title="location.name"
            :label="`距离: ${formatDistance(location.distance)} | 余位: ${location.availableCount || 0}`"
            @click="onLocationClick(location)"
          />
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, nextTick } from 'vue';
import { showFailToast } from 'vant';
import { createParkingMap, getCurrentLocation, sortByDistance } from '@/mobile/utils/amap';

const $request = inject('$request');
const dashboard = ref({});
const noticeList = ref([]);
const locations = ref([]);
const locationStats = ref({});
const user = JSON.parse(localStorage.getItem('user') || '{}');

// 地图相关状态
const showMap = ref(false);
const mapLoading = ref(false);
const mapInstance = ref(null);
const showAllLocations = ref(false);
const nearbyLocations = ref([]);
const nearbyLoading = ref(false);
const userLocation = ref(null);

// 车位统计数据
const parkingStats = ref({
  totalLots: 0,
  availableLots: 0,
  occupiedLots: 0
});


// 更新车位统计数据
const updateParkingStats = () => {
  $request.get('/parkingLot/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      const totalLots = res.data.length;
      const availableLots = res.data.filter(lot => lot.status === '空闲').length;
      const occupiedLots = totalLots - availableLots;
      
      parkingStats.value = {
        totalLots,
        availableLots,
        occupiedLots
      };
    }
  }).catch(() => {});
};

// 切换地图显示
const toggleMap = async () => {
  if (showMap.value) {
    // 隐藏地图
    showMap.value = false;
    // 延迟销毁，确保DOM更新完成
    setTimeout(() => {
      destroyMap();
    }, 100);
  } else {
    // 显示地图
    showMap.value = true;
    await nextTick();
    await initMap();
  }
};

// 初始化地图
const initMap = async () => {
  if (mapInstance.value) {
    return; // 地图已初始化
  }
  
  mapLoading.value = true;
  
  try {
    // 等待DOM更新
    await nextTick();
    
    // 准备地图数据
    const mapData = locations.value.map(loc => ({
      ...loc,
      availableCount: locationStats.value[loc.id]?.free || 0,
      totalCount: locationStats.value[loc.id]?.total || 0
    }));
    
    // 创建地图 - 平衡性能和功能
    const result = await createParkingMap('mobile-parking-map', mapData, {
      zoom: 14,
      center: [121.480074, 31.229857],
      // 只关闭对性能影响大且非必要的功能
      showBuildingBlock: false,
      showIndoorMap: false,
      isHotspot: false
    });
    
    mapInstance.value = result;
    mapLoading.value = false;
    // 移除成功提示，避免白色弹框
    
  } catch (error) {
    console.error('地图初始化失败:', error);
    mapLoading.value = false;
    showFailToast('地图加载失败');
  }
};

// 销毁地图
const destroyMap = () => {
  if (mapInstance.value) {
    try {
      if (mapInstance.value.map) {
        // 清除所有标记点
        if (mapInstance.value.markers) {
          mapInstance.value.markers.forEach(marker => {
            if (marker && marker.remove) {
              marker.remove();
            }
          });
        }
        
        // 清除用户位置标记
        if (mapInstance.value.userLocationMarker) {
          mapInstance.value.userLocationMarker.remove();
        }
        
        // 销毁地图实例
        mapInstance.value.map.destroy();
      }
    } catch (error) {
      console.error('销毁地图时出错:', error);
    } finally {
      mapInstance.value = null;
    }
  }
};

// 刷新地图
const refreshMap = async () => {
  if (!showMap.value) return;
  
  mapLoading.value = true;
  destroyMap();
  await nextTick();
  await initMap();
};

// 格式化距离显示
const formatDistance = (distance) => {
  if (!distance) return '未知';
  
  if (distance < 1000) {
    // 小于1公里显示米
    return Math.round(distance) + 'm';
  } else {
    // 大于等于1公里显示公里，保留1位小数
    return (distance / 1000).toFixed(1) + 'km';
  }
};

// 获取附近停车位
const getNearbyParking = async () => {
  nearbyLoading.value = true;
  try {
    // 获取用户位置
    const location = await getCurrentLocation();
    userLocation.value = location;
    
    // 准备停车位数据
    const parkingData = locations.value.map(loc => ({
      ...loc,
      availableCount: locationStats.value[loc.id]?.free || 0,
      totalCount: locationStats.value[loc.id]?.total || 0
    }));
    
    // 按距离排序
    const sortedData = sortByDistance(parkingData, location.longitude, location.latitude);
    nearbyLocations.value = sortedData.slice(0, 10); // 只显示前10个
    
    // 移除成功提示，避免白色弹框
  } catch (error) {
    console.error('获取附近停车位失败:', error);
    showFailToast('获取位置失败，请检查定位权限');
  } finally {
    nearbyLoading.value = false;
  }
};



// 等待地图就绪并定位
const waitForMapAndLocate = (location, maxAttempts = 50) => {
  let attempts = 0;
  
  const checkMapReady = () => {
    attempts++;
    
    if (mapInstance.value && mapInstance.value.map) {
      // 地图已就绪，执行定位
      mapInstance.value.map.setCenter([location.longitude, location.latitude]);
      mapInstance.value.map.setZoom(16);
      console.log('地图定位成功:', location.name);
    } else if (attempts < maxAttempts) {
      // 继续等待，最多等待5秒
      setTimeout(checkMapReady, 100);
    } else {
      console.warn('地图初始化超时，无法定位');
    }
  };
  
  checkMapReady();
};

// 点击停车区域
const onLocationClick = async (location) => {
  showAllLocations.value = false;
  
  // 如果地图未显示，先显示地图
  if (!showMap.value) {
    showMap.value = true;
    await nextTick();
    await initMap();
  }
  
  // 定位到该区域
  if (location.longitude && location.latitude) {
    // 如果地图已初始化，直接定位
    if (mapInstance.value && mapInstance.value.map) {
      mapInstance.value.map.setCenter([location.longitude, location.latitude]);
      mapInstance.value.map.setZoom(16);
    } else {
      // 等待地图初始化完成
      waitForMapAndLocate(location);
    }
  }
  
  console.log('点击了停车区域:', location);
};


// 加载所有数据
const loadAllData = () => {
  // 更新车位统计
  updateParkingStats();
  
  // 公告 - 使用分页接口获取前三个，按发布时间倒序
  $request.get('/notice/selectPage', { 
    params: { 
      pageNum: 1, 
      pageSize: 3 
    } 
  }).then(res => {
    if (res && res.code === '200' && res.data && res.data.list) {
      noticeList.value = res.data.list;
    }
  }).catch(() => {});
  
  // 停车区域
  $request.get('/location/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      locations.value = res.data;
    } else if (res && res.msg) {
      showFailToast(res.msg);
    }
  }).catch(() => {});
  
  // 区域车位统计
  $request.get('/parkingLot/selectAll').then(res => {
    if (res && res.code === '200' && Array.isArray(res.data)) {
      const stats = {};
      res.data.forEach(lot => {
        const locId = lot.locationId;
        if (!stats[locId]) {
          stats[locId] = { total: 0, free: 0, occupied: 0 };
        }
        stats[locId].total += 1;
        if (lot.status === '空闲') {
          stats[locId].free += 1;
        } else {
          stats[locId].occupied += 1;
        }
      });
      locationStats.value = stats;
    }
  }).catch(() => {});
};

onMounted(() => {
  loadAllData();
  
  // 每30秒自动刷新车位数据
  setInterval(() => {
    updateParkingStats();
  }, 30000);
});
</script>

<style scoped>
.parking-stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 10px 15px;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  color: white;
}

.stats-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  margin-left: 8px;
}

.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.stat-value.total {
  color: #ffffff;
}

.stat-value.available {
  color: #52c41a;
}

.stat-value.regions {
  color: #1890ff;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
  margin: 0 8px;
}

.stats-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  opacity: 0.8;
}

.update-time {
  margin-left: 4px;
}

/* 无公告时的样式 */
.no-notice {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

.no-notice-text {
  margin-left: 6px;
}

/* 地图相关样式 */
.map-toggle-container {
  padding: 12px 16px;
  display: flex;
  align-items: center;
  background: #f7f8fa;
  border-bottom: 1px solid #ebedf0;
  margin: 0;
  border-radius: 0;
}

.map-toggle-container .van-button {
  border-radius: 6px;
  font-size: 13px;
  height: 32px;
  padding: 0 12px;
}

.map-container {
  position: relative;
  margin: 0;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
  background: #f5f5f5;
  border-left: 1px solid #ebedf0;
  border-right: 1px solid #ebedf0;
  border-bottom: 1px solid #ebedf0;
}

.parking-map {
  width: 100%;
  height: 300px;
  background: transparent;
  border: none;
}

.map-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(245, 245, 245, 0.9);
  font-size: 14px;
  color: #666;
  z-index: 10;
}

/* 附近停车位弹窗样式 */
.nearby-parking-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebedf0;
  background: #fff;
}

.popup-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #323233;
}

.nearby-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px 16px 16px;
}

.nearby-list .van-cell {
  margin-bottom: 8px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.nearby-list .van-cell:active {
  background: #f2f3f5;
}

/* 地图标记点样式优化 */
:deep(.amap-marker) {
  cursor: pointer;
}

:deep(.amap-marker-content) {
  transition: transform 0.2s ease;
}

:deep(.amap-marker-content:hover) {
  transform: scale(1.05);
}

/* 停车区域列表样式 */
.location-list-container {
  margin-top: 0;
}

.location-cell {
  border-bottom: 1px solid #f0f0f0;
}

.location-cell:last-child {
  border-bottom: none;
}

.more-locations-cell {
  background: #f8f9fa;
  color: #1989fa;
  font-weight: 500;
}

/* 平衡性能和功能的停车位标记点样式 */
:deep(.parking-marker-balanced) {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: transform 0.2s ease;
}

:deep(.parking-marker-balanced:hover) {
  transform: scale(1.05);
}

:deep(.parking-marker-balanced.parking-icon-green) {
  border: 2px solid #32cd32;
}

:deep(.parking-marker-balanced.parking-icon-orange) {
  border: 2px solid #ff8c00;
}

:deep(.parking-marker-balanced.parking-icon-red) {
  border: 2px solid #dc143c;
}

:deep(.marker-icon) {
  margin-right: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.parking-marker-balanced.parking-icon-green .marker-icon) {
  color: #32cd32;
}

:deep(.parking-marker-balanced.parking-icon-orange .marker-icon) {
  color: #ff8c00;
}

:deep(.parking-marker-balanced.parking-icon-red .marker-icon) {
  color: #dc143c;
}

:deep(.marker-count) {
  font-size: 12px;
  font-weight: 600;
  color: #333;
}


/* 紧凑信息窗口样式 */
:deep(.parking-info-compact) {
  padding: 12px;
  min-width: 200px;
  max-width: 240px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.info-header-compact) {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

:deep(.info-icon-compact) {
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
}

:deep(.info-icon-compact.parking-icon-green) {
  background: #f0fff4;
  color: #32cd32;
}

:deep(.info-icon-compact.parking-icon-orange) {
  background: #fff7e6;
  color: #ff8c00;
}

:deep(.info-icon-compact.parking-icon-red) {
  background: #ffe6e6;
  color: #dc143c;
}

:deep(.info-title-compact h4) {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.2;
}

:deep(.info-status-compact) {
  font-size: 10px;
  font-weight: 500;
  padding: 1px 6px;
  border-radius: 3px;
  margin-left: 6px;
}

:deep(.info-status-compact.parking-icon-green) {
  background: #f0fff4;
  color: #32cd32;
}

:deep(.info-status-compact.parking-icon-orange) {
  background: #fff7e6;
  color: #ff8c00;
}

:deep(.info-status-compact.parking-icon-red) {
  background: #ffe6e6;
  color: #dc143c;
}

:deep(.info-stats-compact) {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

:deep(.stat-compact) {
  flex: 1;
  text-align: center;
  padding: 4px 0;
}

:deep(.stat-label-compact) {
  display: block;
  font-size: 10px;
  color: #666;
  margin-bottom: 2px;
  font-weight: 500;
}

:deep(.stat-value-compact) {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1;
}

/* 隐藏高德地图logo和版权信息 */
:deep(.amap-logo),
:deep(.amap-copyright),
:deep(.amap-copyright-logo) {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
}

/* 信息窗口样式优化 */
:deep(.amap-info-content) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.amap-info-close) {
  color: #999;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 375px) {
  .stat-value {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 11px;
  }
  
  .no-notice {
    padding: 15px;
    font-size: 13px;
  }
  
  .parking-map {
    height: 250px;
  }
  
  .popup-header {
    padding: 12px;
  }
  
  .popup-header h3 {
    font-size: 16px;
  }
  
  .nearby-list {
    padding: 0 12px 12px 12px;
  }
}

@media (max-width: 320px) {
  .parking-map {
    height: 200px;
  }
  
  .map-toggle-container {
    padding: 8px 12px;
  }
  
  .map-container {
    margin: 0 12px 12px 12px;
  }
}
</style>
