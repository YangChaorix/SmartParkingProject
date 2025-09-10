import axios from "axios";

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000
})

request.interceptors.request.use(config => {
    // 只有在不是FormData时才设置Content-Type
    if (!(config.data instanceof FormData)) {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    const raw = localStorage.getItem('user')
    let user = null
    try {
        user = raw ? JSON.parse(raw) : {}
    } catch (e) {
        user = {}
    }
    const token = user && typeof user === 'object' ? (user.token || '') : ''
    config.headers['token'] = token
    return config
}, error => {
    return Promise.reject(error)
});

request.interceptors.response.use(
    response => {
        let res = response.data;
        if (response.config.responseType === 'blob') {
            return res
        }
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        if (res.code === '401') {
            window.location.hash = '#/login'
        }
        return res;
    },
    error => {
        return Promise.reject(error)
    }
)

export default request


