import service from '@/utils/request'

export default {
    namespaced: true,
    state: {
        equipmentList: [],  // 设备列表
        realtimeData: {},  // 实时数据 {equipmentId: data}
        statusMap: new Map(),  // 设备状态缓存
        wsConnected: false,  // WebSocket连接状态
    },
    getters: {

    },
    mutations: {
        SET_DEVICES(state, { equipmentList }) {
            state.equipmentList = equipmentList
        },
        UPDATE_REALTIME_DATA(state, { equipmentId, data }) {
            state.realtimeData[equipmentId] = data
        },
        SET_WS_STATUS(state, status) {
            state.wsConnected = status
        }
    },
    actions: {
        async fetchEquipments({ commit }) {
            const response = await service.get('/equipment/monitor/list')
            commit('SET_DEVICES', {
                equipmentList: response.data
            })
        },
        initWebSocket({ commit }) {
            const ws = new WebSocket('ws://mes-websocket')

            ws.onmessage = (event) => {
                const data = JSON.parse(event.data)
                commit('UPDATE_REALTIME_DATA', {
                    deviceId: data.deviceId,
                    data: data.metrics
                })
            }

            ws.onopen = () => commit('SET_WS_STATUS', true)
            ws.onclose = () => commit('SET_WS_STATUS', false)
        }
    },
    modules: {
    }
}
