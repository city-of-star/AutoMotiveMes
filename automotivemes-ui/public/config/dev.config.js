window.CONFIG = {
    env: {
        name: 'dev'
    },
    api: {
        baseURL: 'http://localhost:3000/api',
        timeout: 15000
    },
    websocket: {
        endpoint: '/ws-endpoint',
        reconnectInterval: 5000,
        heartbeat: {
            enable: true, interval: 30000
        }
    },
    theme: {
        sidebar: {
            widthExpend: 250,
            widthFold: 64
        }
    }
};