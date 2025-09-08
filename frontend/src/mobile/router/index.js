import { createRouter, createWebHashHistory } from 'vue-router'
import MobileLayout from '../components/Layout.vue'

const routes = [
    {
        path: '/',
        redirect: '/home'
    },
    // 独立页面 (这些页面没有底部的 TabBar)
    {
        path: '/login',
        name: 'MobileLogin',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/register',
        name: 'MobileRegister',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/person',
        name: 'MobilePerson',
        component: () => import('../views/Person.vue')
    },
    {
        path: '/password',
        name: 'MobilePassword',
        component: () => import('../views/Password.vue')
    },
    {
        path: '/vehicle',
        name: 'MobileVehicle',
        component: () => import('../views/Vehicle.vue')
    },
    {
        path: '/notice/:id',
        name: 'MobileNoticeDetail',
        component: () => import('../views/NoticeDetail.vue')
    },
    // 使用 TabBar 布局的主页面
    {
        path: '/',
        component: MobileLayout,
        children: [
            { path: 'home', name: 'MobileHome', component: () => import('../views/Home.vue') },
            { path: 'parking', name: 'MobileParking', component: () => import('../views/Parking.vue') },
            { path: 'pay', name: 'MobilePay', component: () => import('../views/Pay.vue') },
            { path: 'me', name: 'MobileMe', component: () => import('../views/Me.vue') }
        ]
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

// 路由守卫 (这段逻辑现在可以正确工作了)
router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (to.path === '/login' || to.path === '/register') {
        next()
        return
    }
    if (user && user.role === 'USER') {
        next()
    } else {
        next({ path: '/login', query: { redirect: to.fullPath } })
    }
})

export default router