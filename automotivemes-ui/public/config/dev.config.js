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
        btnColor: {
            btnUpdateColor: '#71E2A3',
            btnDeleteColor: '#FF929E',
            btnImportColor: '#969A9E',
            btnExportColor: '#FFBA00'
        },
        sidebar: {
            widthExpend: 250,
            widthFold: 64
        }
    }
};