<template>
    <a-layout-header class="header">
        <div class="logo"/>
        <div style="float: right; color: white;"> <!-- 白色, 居右显示 -->
            您好：{{ member.mobile }} &nbsp;&nbsp;&nbsp; <!-- 显示登陆手机号 -->
            <!-- 使用 router-link标签 + to来跳转到 login 页面, 相当于 a 标签的 href -->
            <router-link to="/login" style="color: white;">
                退出登录 <!-- 显示退出登录按钮 -->
            </router-link>
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
            <a-menu-item key="/passenger">
                <router-link to="/passenger">
                    <user-outlined/> &nbsp; 乘车人管理
                </router-link>
            </a-menu-item>
        </a-menu>

    </a-layout-header>
</template>

<script>
// vue3写法
import {defineComponent, ref, watch} from 'vue';
import store from "@/store";
import router from '@/router'

export default defineComponent({
    name: "header-components",
    setup() {
        // 因为header只是显示，不会修改member, 所以声明称普通变量就可以，不需要响应式变量
        let member = store.state.member;
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
            member,
            selectedKeys
        };
    },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only (添加“scoped”属性以将CSS仅限于此组件) -->
<style scoped>

</style>
