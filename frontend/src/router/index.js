import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    {
      path: '/manager',
      name: 'Manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/manager/home',
      children: [
        {
          path: 'person',
          meta: { name: '个人资料' },
          component: () => import('@/views/manager/Person.vue'),
        },
        {
          path: 'password',
          meta: { name: '修改密码' },
          component: () => import('@/views/manager/Password.vue'),
        },
        {
          path: 'home',
          meta: { name: '系统首页' },
          component: () => import('@/views/manager/Home.vue'),
        },
        {
          path: 'admin',
          meta: { name: '管理员信息' },
          component: () => import('@/views/manager/Admin.vue'),
        },
        {
          path: 'notice',
          meta: { name: '系统公告' },
          component: () => import('@/views/manager/Notice.vue'),
        },
        // 新路由
        {
          path: 'pay',
          meta: { name: '缴费信息' },
          component: () => import('@/views/manager/Pay.vue'),
        },
        {
          path: 'parking',
          meta: { name: '停车信息' },
          component: () => import('@/views/manager/Parking.vue'),
        },
        {
          path: 'vehicle',
          meta: { name: '车辆信息' },
          component: () => import('@/views/manager/Vehicle.vue'),
        },
        {
          path: 'parkingLot',
          meta: { name: '车位信息' },
          component: () => import('@/views/manager/ParkingLot.vue'),
        },
        {
          path: 'location',
          meta: { name: '停车区域' },
          component: () => import('@/views/manager/Location.vue'),
        },
        {
          path: 'user',
          meta: { name: '用户信息' },
          component: () => import('@/views/manager/User.vue'),
        },
        {
          path: 'dashboard',
          meta: { name: '数据统计' },
          component: () => import('@/views/manager/Dashboard.vue'),
        },
        {
          path: 'map',
          meta: { name: '地图概览' },
          component: () => import('@/views/manager/Map.vue'),
        },
        // 通知信息
        {
          path: 'notification',
          meta: { name: '通知信息' },
          component: () => import('@/views/manager/Notification.vue'),
        },
        // 用户查看通知信息的页面
        {
          path: 'notificationUser',
          meta: { name: '通知信息' },
          component: () => import('@/views/manager/NotificationUser.vue'),
        },
      ],
    },
    {
      path: '/front',
      name: 'Front',
      component: () => import('@/views/Front.vue'),
      redirect: '/front/home',
      children: [
        { path: 'home', component: () => import('@/views/front/Home.vue') },
        { path: 'person', component: () => import('@/views/front/Person.vue') },
      ],
    },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/register', component: () => import('@/views/Register.vue') },
    { path: '/404', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404', hidden: true },
  ],
});

export default router;
