<template>
    <a-layout id="components-layout-demo-top-side-2">
        <HeaderComponents/>
        <a-layout>
            <SiderComponents/>
            <a-layout style="padding: 0 24px 24px">
                <a-breadcrumb style="margin: 16px 0">
                    <a-breadcrumb-item>Home</a-breadcrumb-item>
                    <a-breadcrumb-item>List</a-breadcrumb-item>
                    <a-breadcrumb-item>App</a-breadcrumb-item>
                </a-breadcrumb>
                <a-layout-content
                        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
                >
                    Content
                </a-layout-content>
            </a-layout>
        </a-layout>
    </a-layout>
</template>
<script>
import { defineComponent } from 'vue';
import HeaderComponents from "@/components/Header.vue";
import SiderComponents from "@/components/Sider.vue";
export default defineComponent({
    components: {
        SiderComponents,
        HeaderComponents,
    },
    setup() {
        return {
        };
    },
});
</script>
<style>
#components-layout-demo-top-side-2 .logo {
    float: left;
    width: 120px;
    height: 31px;
    margin: 16px 24px 16px 0;
    background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
    float: right;
    margin: 16px 0 16px 24px;
}

.site-layout-background {
    background: #fff;
}
</style>