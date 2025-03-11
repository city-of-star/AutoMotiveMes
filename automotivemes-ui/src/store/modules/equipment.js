import service from '@/utils/request'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

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
        UPDATE_REALTIME_DATA(state, payload) {
            const { equipmentId, ...metrics } = payload
            state.realtimeData = {
                ...state.realtimeData,
                [equipmentId]: metrics
            }
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
            const socket = new SockJS('http://localhost:3000/mes-websocket')
            const stompClient = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            })

            stompClient.onConnect = () => {
                stompClient.subscribe('/topic/equipment/realtime', (message) => {
                    const data = JSON.parse(message.body)
                    commit('UPDATE_REALTIME_DATA', data)
                })
            }

            stompClient.activate()
        }
    },
    modules: {
    }
}
