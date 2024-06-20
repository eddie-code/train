<template>
    <a-layout-sider width="200" style="background: #fff">

        <a-menu
                v-model:selectedKeys="selectedKeys"
                mode="inline"
                :style="{ height: '100%', borderRight: 0 }"
        >
            <a-menu-item key="/welcome">
                <router-link to="/welcome">
                    <coffee-outlined/> &nbsp; 欢迎
                </router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">
                    <user-outlined/> &nbsp; 关于
                </router-link>
            </a-menu-item>
            <a-menu-item key="/batch/job">
                <router-link to="/batch/job">
                    <MenuUnfoldOutlined /> &nbsp; 任务管理
                </router-link>
            </a-menu-item>
            <a-menu-item key="/station">
                <router-link to="/station">
                    <user-outlined/> &nbsp; 车站管理
                </router-link>
            </a-menu-item>
            <a-menu-item key="/train">
                <router-link to="/train">
                    <user-outlined/> &nbsp; 火车管理
                </router-link>
            </a-menu-item>
            <a-menu-item key="/train-station">
                <router-link to="/train-station">
                    <user-outlined/> &nbsp; 火车车站
                </router-link>
            </a-menu-item>
            <a-menu-item key="/train-carriage">
                <router-link to="/train-carriage">
                    <user-outlined/> &nbsp; 火车车厢
                </router-link>
            </a-menu-item>
            <a-menu-item key="/train-seat">
                <router-link to="/train-seat">
                    <user-outlined/> &nbsp; 火车座位
                </router-link>
            </a-menu-item>
        </a-menu>

    </a-layout-sider>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";

export default defineComponent({
    name: "sider-components",
    setup() {

        // 响应式变量：选中的菜单项
        const selectedKeys = ref([]);

        // 监听路由变化，将路由的path赋值给selectedKeys
        watch(() => router.currentRoute.value.path, (newValue) => {
            console.log('watch', newValue);
            selectedKeys.value = [];
            selectedKeys.value.push(newValue);
        }, {immediate: true});

        // 返回响应式变量
        return {
            selectedKeys
        };
    },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
