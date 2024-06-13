import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/',
        component: () => import('../views/MainView.vue'),
        // 二级路由（又称为子路由）
        children: [{
            path: 'welcome',
            component: () => import('../views/main/WelcomeView.vue'),
        }, {
            path: 'about',
            component: () => import('../views/main/AboutView.vue'),
        }, {
            path: 'station',
            component: () => import('../views/main/StationView.vue'),
        }, {
            path: 'train',
            component: () => import('../views/main/TrainView.vue'),
        }, {
            path: 'train-station',
            component: () => import('../views/main/Train-stationView.vue'),
        }, {
            path: 'train-carriage',
            component: () => import('../views/main/Train-carriageView.vue'),
        }, {
            path: 'train-seat',
            component: () => import('../views/main/Train-seatView.vue'),
        }]
    }, {
        // 当直接访问域名，重定向到欢迎页
        path: '',
        redirect: '/welcome'
    }];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
