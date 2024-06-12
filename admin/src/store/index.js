import {createStore} from 'vuex'

const MEMBER = "MEMBER";

export default createStore({
    // 类似 Java 实体类字段 全局变量
    state: {
        // 优先获取左边缓存里面的数据, 如果缓存是空, 这样就可以保证 member 一定是一个对象.
        // 使用 || {} 方式可以避免空指针异常, 因为 || 是短路求值, 如果左边是空, 就不会执行右边的代码.
        member: window.SessionStorage.get(MEMBER) || {}
    },
    // 类似 Java get方法
    getters: {},
    // 类似 Java set方法
    mutations: {
        setMember(state, _member) {
            state.member = _member;
            // 将数据同步到缓存
            window.SessionStorage.set(MEMBER, _member);
        }
    },
    actions: {},
    modules: {}
})
