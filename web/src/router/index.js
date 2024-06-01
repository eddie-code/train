import {createRouter, createWebHistory} from 'vue-router'
import store from "@/store";
import {notification} from "ant-design-vue";

const routes = [
    {
        path: '/login',
        component: () => import('../views/LoginView.vue')
    },
    {
        path: '/',
        component: () => import('../views/MainView.vue'),
        // 元数据
        meta: {
            // 是否需要登录校验
            loginRequire: true
        }
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 前端路由登录拦截
router.beforeEach((to, from, next) => {
    // 要不要对meta.loginRequire属性做监控拦截
    if (to.matched.some(function (item) {
        console.log(item, "是否需要登录校验：", item.meta.loginRequire || false);
        return item.meta.loginRequire
    }))
    // item.meta.loginRequire 为true时，进行登录校验, 否则直接放行
    {
        const _member = store.state.member;
        console.log("页面登录校验开始：", _member);
        if (!_member.token) {
            console.log("用户未登录或登录超时！");
            notification.error({ description: "未登录或登录超时" });
            next('/login');
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router
