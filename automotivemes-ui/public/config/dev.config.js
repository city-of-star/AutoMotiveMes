window.CONFIG = {
    env: {
        name: 'dev'
    },
    api: {
        baseURL: '/api',  // http://localhost:3000
        timeout: 15000
    },
    websocket: {
        baseURL: 'http://localhost:3000/mes-websocket',
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    },
    theme: {
        sidebar: {
            widthExpend: 250,
            widthFold: 64
        }
    }
};
