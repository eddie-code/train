<template>
    <a-layout-header class="header">
        <div class="logo"/>
        <div style="float: right; color: white;">
            欢迎使用管理控台
        </div>

        <a-menu
                v-model:selectedKeys="selectedKeys"
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
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
        </a-menu>

    </a-layout-header>
</template>

<script>
// vue3写法
import {defineComponent, ref, watch} from 'vue';
import router from '@/router'

export default defineComponent({
    name: "header-components",
    setup() {

        // 响应式变量：选中的菜单项
        const selectedKeys = ref([]);

        // 监听路由变化，将路由的path赋值给selectedKeys
        watch(() => router.currentRoute.value.path, (newValue) => {
            console.log('watch', newValue);
            selectedKeys.value = [];
            selectedKeys.value.push(newValue);
        }, {immediate: true});

        // 返回给 Html 调用
        return {
            selectedKeys
        };
    },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only (添加“scoped”属性以将CSS仅限于此组件) -->
<style scoped>

</style>
