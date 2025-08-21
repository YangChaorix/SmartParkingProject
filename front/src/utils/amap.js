import AMapLoader from '@amap/amap-jsapi-loader';
import { ElMessage } from 'element-plus';

// 统一配置
const config = {
    key: '662fd0c2e9883677afb2831fc9bd5cf6', // 你的高德地图Web端开发者Key
    version: '2.0',
    plugins: ['AMap.Geocoder', 'AMap.PlaceSearch'], // 将所有需要用到的插件统一加载
};

// 安全密钥配置 (如果你的Key需要)
window._AMapSecurityConfig = {
    securityJsCode: '79d6a2d11e1643403a361f6cfb999c67', // 你的安全密钥
};

let aMapApi = null; // 用于缓存 AMap API 的加载 Promise

/**
 * 加载并返回高德地图API的函数
 * 使用单例模式，确保整个应用只加载一次 JSApi
 * @returns {Promise<object>} 返回一个 Promise，resolve 的结果是 AMap 对象
 */
export const getAMap = () => {
    if (!aMapApi) {
        aMapApi = new Promise((resolve, reject) => {
            AMapLoader.load(config)
                .then(AMap => {
                    console.log('Gaode Map JSApi loaded successfully.');
                    resolve(AMap);
                })
                .catch(error => {
                    console.error('Gaode Map JSApi failed to load:', error);
                    ElMessage.error('高德地图加载失败，请检查网络或配置');
                    reject(error);
                });
        });
    }
    return aMapApi;
};