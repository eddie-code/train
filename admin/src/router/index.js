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
            path: 'base/',
            children: [{
                path: 'station',
                component: () => import('../views/main/base/StationView.vue'),
            }, {
                path: 'train',
                component: () => import('../views/main/base/TrainView.vue'),
            }, {
                path: 'train-station',
                component: () => import('../views/main/base/Train-stationView.vue'),
            }, {
                path: 'train-carriage',
                component: () => import('../views/main/base/Train-carriageView.vue'),
            }, {
                path: 'train-seat',
                component: () => import('../views/main/base/Train-seatView.vue'),
            }]
        }, {
            path: 'business/',
            children: [{
                path: 'sk-token',
                component: () => import('../views/main/business/Sk-tokenView.vue'),
            }, {
                path: 'confirm-order',
                component: () => import('../views/main/business/Confirm-orderView.vue'),
            }, {
                path: 'daily-train',
                component: () => import('../views/main/business/Daily-trainView.vue'),
            }, {
                path: 'daily-train-station',
                component: () => import('../views/main/business/Daily-train-stationView.vue'),
            }, {
                path: 'daily-train-carriage',
                component: () => import('../views/main/business/Daily-train-carriageView.vue'),
            }, {
                path: 'daily-train-seat',
                component: () => import('../views/main/business/Daily-train-seatView.vue'),
            }, {
                path: 'daily-train-ticket',
                component: () => import('../views/main/business/Daily-train-ticketView.vue'),
            }]
        }, {
            path: 'batch/',
            children: [{
                path: 'job',
                component: () => import('../views/main/batch/Job.vue')
            }]
        }, {
            path: 'member/',
            children: [{
                path: 'ticket',
                component: () => import('../views/main/member/TicketView.vue')
            }]
        }]
    }, {
        path: '',
        redirect: '/welcome'
    }];


const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
