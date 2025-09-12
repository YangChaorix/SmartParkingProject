import AMapLoader from '@amap/amap-jsapi-loader';
import { showFailToast } from 'vant';

// 移动端高德地图配置
const config = {
    key: '662fd0c2e9883677afb2831fc9bd5cf6', // 你的高德地图Web端开发者Key
    version: '2.0',
    plugins: ['AMap.Geocoder', 'AMap.PlaceSearch', 'AMap.Geolocation'], // 移动端需要的插件
};

// 安全密钥配置
window._AMapSecurityConfig = {
    securityJsCode: '79d6a2d11e1643403a361f6cfb999c67', // 你的安全密钥
};

let aMapApi = null; // 用于缓存 AMap API 的加载 Promise

/**
 * 加载并返回高德地图API的函数（移动端专用）
 * @returns {Promise<object>} 返回一个 Promise，resolve 的结果是 AMap 对象
 */
export const getAMap = () => {
    if (!aMapApi) {
        aMapApi = new Promise((resolve, reject) => {
            AMapLoader.load(config)
                .then(AMap => {
                    console.log('移动端高德地图 JSApi 加载成功');
                    resolve(AMap);
                })
                .catch(error => {
                    console.error('移动端高德地图 JSApi 加载失败:', error);
                    showFailToast('地图加载失败，请检查网络连接');
                    reject(error);
                });
        });
    }
    return aMapApi;
};

/**
 * 创建移动端停车位地图
 * @param {string} containerId - 地图容器ID
 * @param {Array} parkingData - 停车位数据
 * @param {Object} options - 地图配置选项
 * @returns {Promise<Object>} 返回地图实例和相关信息
 */
export const createParkingMap = async (containerId, parkingData = [], options = {}) => {
    try {
        const AMap = await getAMap();

        // 平衡性能和功能的配置
        const defaultOptions = {
            zoom: 15,
            center: [121.480074, 31.229857], // 默认中心点（上海）
            viewMode: '2D', // 移动端使用2D模式
            mapStyle: 'amap://styles/normal',
            showLabel: true, // 保持标签显示
            showBuildingBlock: false, // 关闭建筑物，提升性能
            showIndoorMap: false, // 关闭室内地图，提升性能
            resizeEnable: true,
            rotateEnable: false,
            pitchEnable: false,
            zoomEnable: true,
            scrollWheel: false, // 移动端禁用滚轮
            touchZoom: true,
            doubleClickZoom: true, // 保持双击缩放
            keyboardEnable: false,
            dragEnable: true,
            jogEnable: true, // 保持缓动，提升用户体验
            animateEnable: true, // 保持动画，提升用户体验
            autoFitView: true, // 保持自动适应
            isHotspot: false, // 关闭热点，提升性能
            defaultCursor: 'pointer'
        };

        const mapConfig = { ...defaultOptions, ...options };

        // 创建地图实例
        const map = new AMap.Map(containerId, mapConfig);

        // 隐藏高德地图logo
        map.on('complete', () => {
            // 隐藏左下角的高德logo和版权信息
            const hideLogo = () => {
                const logoElements = document.querySelectorAll('.amap-logo, .amap-copyright, .amap-copyright-logo');
                logoElements.forEach(element => {
                    element.style.display = 'none';
                    element.style.visibility = 'hidden';
                    element.style.opacity = '0';
                });
            };

            // 立即执行一次
            hideLogo();

            // 使用MutationObserver监听DOM变化，确保logo被隐藏
            const observer = new MutationObserver(() => {
                hideLogo();
            });

            // 监听地图容器的变化
            const mapContainer = document.getElementById(containerId);
            if (mapContainer) {
                observer.observe(mapContainer, {
                    childList: true,
                    subtree: true
                });
            }
        });


        // 创建标记点数组
        const markers = [];

        // 添加停车位标记
        if (parkingData && parkingData.length > 0) {
            parkingData.forEach(location => {
                const marker = createParkingMarker(AMap, map, location);
                if (marker) {
                    markers.push(marker);
                }
            });

            // 调整地图视野以包含所有标记点
            if (markers.length > 0) {
                map.setFitView(markers, false, [20, 20, 20, 20]);
            }
        }

        return {
            map,
            AMap,
            markers
        };
    } catch (error) {
        console.error('创建停车位地图失败:', error);
        showFailToast('地图初始化失败');
        throw error;
    }
};

/**
 * 创建停车位标记点
 * @param {Object} AMap - 高德地图API实例
 * @param {Object} map - 地图实例
 * @param {Object} location - 停车位位置数据
 * @returns {Object} 标记点实例
 */
const createParkingMarker = (AMap, map, location) => {
    if (!location.longitude || !location.latitude) {
        return null;
    }

    // 根据可用车位数量确定颜色和图标
    const availableCount = location.availableCount || 0;
    const totalCount = location.totalCount || 0;

    let color = '#dc143c'; // 红色 - 无可用车位
    let iconClass = 'parking-icon-red';
    let statusText = '无车位';

    if (availableCount > 5) {
        color = '#32cd32'; // 绿色 - 充足
        iconClass = 'parking-icon-green';
        statusText = '充足';
    } else if (availableCount > 0) {
        color = '#ff8c00'; // 橙色 - 紧张
        iconClass = 'parking-icon-orange';
        statusText = '紧张';
    }

    // 创建平衡性能和功能的标记点
    const markerContent = `
        <div class="parking-marker-balanced ${iconClass}">
            <div class="marker-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
            </div>
            <div class="marker-count">${availableCount}</div>
        </div>
    `;

    // 创建标记点
    const marker = new AMap.Marker({
        position: [location.longitude, location.latitude],
        content: markerContent,
        offset: new AMap.Pixel(-20, -20),
        anchor: 'center'
    });

    // 创建简洁的信息窗口
    const infoWindow = new AMap.InfoWindow({
        content: `
            <div class="parking-info-compact">
                <div class="info-header-compact">
                    <div class="info-icon-compact ${iconClass}">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                        </svg>
                    </div>
                    <div class="info-title-compact">
                        <h4>${location.name}</h4>
                        <span class="info-status-compact ${iconClass}">${statusText}</span>
                    </div>
                </div>
                <div class="info-stats-compact">
                    <div class="stat-compact">
                        <span class="stat-label-compact">总数</span>
                        <span class="stat-value-compact">${totalCount}</span>
                    </div>
                    <div class="stat-compact">
                        <span class="stat-label-compact">余位</span>
                        <span class="stat-value-compact" style="color: ${color};">${availableCount}</span>
                    </div>
                    <div class="stat-compact">
                        <span class="stat-label-compact">占用率</span>
                        <span class="stat-value-compact">${totalCount > 0 ? Math.round(((totalCount - availableCount) / totalCount) * 100) : 0}%</span>
                    </div>
                </div>
            </div>
        `,
        offset: new AMap.Pixel(0, -20),
        closeWhenClickMap: true
    });

    // 点击标记点显示信息窗口
    marker.on('click', () => {
        infoWindow.open(map, marker.getPosition());
    });

    // 将标记点添加到地图
    map.add(marker);

    return marker;
};

/**
 * 获取用户当前位置
 * @returns {Promise<Object>} 返回位置信息
 */
export const getCurrentLocation = () => {
    return new Promise((resolve, reject) => {
        if (!navigator.geolocation) {
            reject(new Error('浏览器不支持地理定位'));
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                resolve({
                    longitude: position.coords.longitude,
                    latitude: position.coords.latitude,
                    accuracy: position.coords.accuracy
                });
            },
            (error) => {
                console.error('获取位置失败:', error);
                reject(error);
            },
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 60000
            }
        );
    });
};

/**
 * 计算两点之间的距离（米）
 * @param {number} lng1 - 经度1
 * @param {number} lat1 - 纬度1
 * @param {number} lng2 - 经度2
 * @param {number} lat2 - 纬度2
 * @returns {number} 距离（米）
 */
export const calculateDistance = (lng1, lat1, lng2, lat2) => {
    const R = 6371000; // 地球半径（米）
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLng / 2) * Math.sin(dLng / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Math.round(R * c); // 四舍五入到整数米
};

/**
 * 按距离排序停车位
 * @param {Array} parkingData - 停车位数据
 * @param {number} userLng - 用户经度
 * @param {number} userLat - 用户纬度
 * @returns {Array} 按距离排序的停车位数据
 */
export const sortByDistance = (parkingData, userLng, userLat) => {
    return parkingData.map(location => ({
        ...location,
        distance: calculateDistance(userLng, userLat, location.longitude, location.latitude)
    })).sort((a, b) => a.distance - b.distance);
};
