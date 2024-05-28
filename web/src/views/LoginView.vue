<template>
    <a-row class="login">
        <a-col :span="8" :offset="8" class="login-main">
            <h1 style="text-align: center"><rocket-two-tone />&nbsp;甲蛙12306售票系统</h1>
            <a-form
                :model="loginForm"
                name="basic"
                autocomplete="off"
            >
                <a-form-item
                    label=""
                    name="mobile"
                    :rules="[{ required: true, message: '请输入手机号!' }]"
                >
<!--                    <a-input v-model:value="loginForm.mobile" placeholder="手机号"/>-->
                    <a-input v-model:value="loginForm.mobile" placeholder="手机号">
                        <template #prefix>
                            <user-outlined />
                        </template>
                        <template #suffix>
                            <a-tooltip title="Extra information">
                                <info-circle-outlined style="color: rgba(0, 0, 0, 0.45)" />
                            </a-tooltip>
                        </template>
                    </a-input>
                </a-form-item>

                <a-form-item
                    label=""
                    name="code"
                    :rules="[{ required: true, message: '请输入验证码!' }]"
                >
                    <a-input v-model:value="loginForm.code">
                        <template #addonAfter>
                            <a @click="sendCode">获取验证码</a>
                        </template>
                    </a-input>
                    <!--<a-input v-model:value="loginForm.code" placeholder="验证码"/>-->
                </a-form-item>

                <a-form-item>
                    <a-button type="primary" block @click="login">登录</a-button>
                </a-form-item>

            </a-form>
        </a-col>
    </a-row>
</template>

<script>
import { defineComponent, reactive } from 'vue';
import axios from 'axios'; // npm install axios
import { notification } from 'ant-design-vue'; // 页面弹框的通知组件
// import { useRouter } from 'vue-router'
// import store from "@/store";

export default defineComponent({
    name: "login-view",
    setup() {
        // const router = useRouter();

        // login 表单提交参数封装
        const loginForm = reactive({
            mobile: '13800138000',
            code: '',
        });

        const sendCode = () => {

            axios.post("http://localhost:8000/member/member/send-code", {
                // axios.post("/member/member/send-code", {
                mobile: loginForm.mobile // 传递参数
            }).then(response => {
                let data = response.data;  // 后端返回的数据, 等于后端的 CommonResp
                if (data.success) {
                    notification.success({description: '发送验证码成功！'});
                    loginForm.code = "8888"; // 双向绑定, Html就会显示
                } else {
                    notification.error({description: data.message});
                }
            });
        };

        const login = () => {
            // 不同的传递参数写法： loginForm 就是上面的函数方法, 与 sendCode 的传递方式不同
            axios.post("http://localhost:8000/member/member/login", loginForm).then((response) => {
            // axios.post("/member/member/login", loginForm).then((response) => {
                let data = response.data;
                if (data.success) {
                    notification.success({ description: '登录成功！' });
                    // console.log("登录成功！")
                    // 登录成功，跳到控台主页
                    // router.push("/welcome");
                    // store.commit("setMember", data.content);
                } else {
                    notification.error({ description: data.message });
                }
            })
        };

        // 返回给 Html 调用
        return {
            loginForm,
            sendCode,
            login
        };
    },
});
</script>

<style>
.login-main h1 {
    font-size: 25px;
    font-weight: bold;
}
.login-main {
    margin-top: 100px;
    padding: 30px 30px 20px;
    border: 2px solid grey;
    border-radius: 10px;
    background-color: #fcfcfc;
}
</style>