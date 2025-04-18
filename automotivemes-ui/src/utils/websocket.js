import { ref } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

export const useWebSocket = () => {
    const client = ref(null)

    const connect = (endpoint) => {
        const socket = new SockJS(`${window.CONFIG.api.baseURL}${endpoint}`)
        client.value = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000
        })

        client.value.activate()
    }

    const subscribe = (destination, callback) => {
        if (!client.value) return

        const subscription = client.value.subscribe(destination, message => {
            callback(message)
        })

        return () => subscription.unsubscribe()
    }

    const disconnect = () => {
        if (client.value) {
            client.value.deactivate()
        }
    }

    return {
        connect,
        subscribe,
        disconnect
    }
}