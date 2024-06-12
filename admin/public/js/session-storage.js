// 所有的session key都在这里统一定义，可以避免多个功能使用同一个key
// SESSION_ORDER = "SESSION_ORDER";
// SESSION_TICKET_PARAMS = "SESSION_TICKET_PARAMS";

/**
 *  封装存储Session:
 *      为什么不直接调用 sessionStorage.getItem(key), 原生这个只能操作字符串,
 *      如果把一个session放到一个对象里面, 就需要转成json, 在放到缓存里面
 *      set的时候就可以直接塞进去, get时候可以直接转换对象
 */
SessionStorage = {
    get: function (key) {
        // sessionStorage 是 h5 内置变量, 关闭浏览器, 保存在里面东西就没了
        // localStorage 是 h5 内置变量, 关闭浏览器, 保存在里面东西还在
        var v = sessionStorage.getItem(key);
        if (v && typeof (v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    },
    set: function (key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
};
