<template>
    <a-form
            :model="formState"
            name="basic"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }"
            autocomplete="off"
            @finish="onFinish"
            @finishFailed="onFinishFailed"
    >
        <a-form-item
                label="Username"
                name="username"
                :rules="[{ required: true, message: 'Please input your username!' }]"
        >
            <a-input v-model:value="formState.username" />
        </a-form-item>

        <a-form-item
                label="Password"
                name="password"
                :rules="[{ required: true, message: 'Please input your password!' }]"
        >
            <a-input-password v-model:value="formState.password" />
        </a-form-item>

        <a-form-item name="remember" :wrapper-col="{ offset: 8, span: 16 }">
            <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
        </a-form-item>

        <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
            <a-button type="primary" html-type="submit">Submit</a-button>
        </a-form-item>
    </a-form>
</template>

<script setup>
    import { reactive } from 'vue';
    // 表单数据绑定
    const formState = reactive({
        username: '',
        password: '',
        remember: true, // 初始值： 双向绑定：修改变量值，则元素展现的值也会变化; 反过来, 用户操作元素, 则script中的变量值也会变化
    });
    // 对应页面的处理成功的方法
    const onFinish = values => {
        console.log('Success:', values);
    };
    // 对应页面的处理失败的方法
    const onFinishFailed = errorInfo => {
        console.log('Failed:', errorInfo);
    };
</script>

<style>

</style>