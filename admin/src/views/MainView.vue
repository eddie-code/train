<template>
    <a-layout id="components-layout-demo-top-side-2">
        <HeaderComponents></HeaderComponents>
        <a-layout>
            <SiderComponents></SiderComponents>
            <a-layout-content
                    :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
            >
                <router-view></router-view>
            </a-layout-content>
        </a-layout>
    </a-layout>
</template>

<script>
// ref 用来声明基本的数据类型
import {defineComponent} from 'vue';
import HeaderComponents from "@/components/Header.vue";
import SiderComponents from "@/components/Sider.vue";

export default defineComponent({
    components: {
        SiderComponents,
        HeaderComponents,
    },
    setup() {
        // 返回给html
        return {};
    },
});
</script>

<style>

</style>