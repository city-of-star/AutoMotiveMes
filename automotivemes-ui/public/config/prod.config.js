window.CONFIG = {
    env: {
        name: 'prod'
    },
    api: {
        baseURL: 'https://111.229.150.28:3000/api',
        timeout: 10000
    },
    websocket: {
        endpoint: '/prod-ws-endpoint',
        reconnectInterval: 10000,
        heartbeat: {
            enable: true, interval: 45000
        }
    },
    theme: {
        btnColor: {
            btnUpdateColor: '#52C41A',
            btnDeleteColor: '#FF4D4F',
            btnImportColor: '#8C8C8C',
            btnExportColor: '#FAAD14'
        },
        sidebar: {
            widthExpend: 200,
            widthFold: 60
        }
    }
};