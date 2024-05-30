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
                    所有会员总数：{{count}}
                </a-layout-content>
            </a-layout>
        </a-layout>
    </a-layout>
</template>
<script>
// ref 用来声明基本的数据类型
import {defineComponent, ref} from 'vue';
import HeaderComponents from "@/components/Header.vue";
import SiderComponents from "@/components/Sider.vue";
import axios from "axios";
import {notification} from "ant-design-vue";

export default defineComponent({
    components: {
        SiderComponents,
        HeaderComponents,
    },
    setup() {
        // 页面打开的时候, 就查询会员的数量
        const count = ref(0);
        axios.get("/member/member/count").then((response) => {
            let data = response.data;
            if (data.success) {
                count.value = data.content;
            } else {
                notification.error({description: data.message});
            }
        })

        // 返回给html
        return {
            count,
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