<template>
    <a-layout-header class="header">
        <div class="logo"/>
        <a-menu
                v-model:selectedKeys="selectedKeys1"
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="1">nav 111</a-menu-item>
            <a-menu-item key="2">nav 2</a-menu-item>
            <a-menu-item key="3">nav 3</a-menu-item>
        </a-menu>
    </a-layout-header>
</template>

<script>
// vue3写法
import {defineComponent, ref} from 'vue';

export default defineComponent({
    name: "header-components",
    setup() {

        // 返回给 Html 调用
        return {
            selectedKeys1: ref(['2']),
        };
    },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only (添加“scoped”属性以将CSS仅限于此组件) -->
<style scoped>

</style>
