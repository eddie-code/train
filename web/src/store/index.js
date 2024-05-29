import {createStore} from 'vuex'

export default createStore({
    // 类似 Java 实体类字段 全局变量
    state: {
        member: {}
    },
    // 类似 Java get方法
    getters: {},
    // 类似 Java set方法
    mutations: {
        setMember(state, _member) {
            state.member = _member
        }
    },
    actions: {},
    modules: {}
})
