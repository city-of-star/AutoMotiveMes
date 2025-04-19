import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const createWebSocket = () => {
    let stompClient = null;
    let subscriptions = [];  // 存储结构：{ topic, handler, subscription }
    let isInitialized = false;

    const init = () => {
        if (isInitialized) return;

        const socket = new SockJS(window.CONFIG.websocket.baseURL);
        stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: window.CONFIG.websocket.reconnectDelay,
            heartbeatIncoming: window.CONFIG.websocket.heartbeatIncoming,
            heartbeatOutgoing: window.CONFIG.websocket.heartbeatOutgoing,
            onStompError: (frame) => {
                console.error('WebSocket错误:', frame.headers.message);
            },
        });

        stompClient.onConnect = () => {
            // 重新订阅所有已注册的订阅
            subscriptions.forEach(sub => {
                if (!sub.subscription || sub.subscription.closed) {
                    sub.subscription = stompClient.subscribe(sub.topic, sub.handler);
                }
            });
        };

        stompClient.activate();
        isInitialized = true;
    };

    const subscribe = (topic, handler) => {
        let subscription = null;
        const subscriptionId = Symbol('subscriptionId');

        // 立即订阅（如果已连接）
        if (stompClient && stompClient.connected) {
            subscription = stompClient.subscribe(topic, handler);
        }

        // 存储订阅信息
        subscriptions.push({
            topic,
            handler,
            subscriptionId,
            subscription
        });

        // 返回取消订阅函数
        return () => unsubscribe(subscriptionId);
    };

    // 根据subscriptionId取消订阅
    const unsubscribe = (subscriptionId) => {
        const index = subscriptions.findIndex(sub => sub.subscriptionId === subscriptionId);
        if (index === -1) return;

        const sub = subscriptions[index];
        if (sub.subscription) {
            try {
                sub.subscription.unsubscribe();
            } catch (e) {
                console.warn('取消订阅失败:', e);
            }
        }
        subscriptions.splice(index, 1);
    };

    // 按topic取消所有订阅
    const unsubscribeAllByTopic = (topic) => {
        subscriptions = subscriptions.filter(sub => {
            if (sub.topic === topic) {
                if (sub.subscription) {
                    try {
                        sub.subscription.unsubscribe();
                    } catch (e) {
                        console.warn('取消订阅失败:', e);
                    }
                }
                return false;
            }
            return true;
        });
    };

    // 取消所有订阅
    const unsubscribeAll = () => {
        subscriptions.forEach(sub => {
            if (sub.subscription) {
                try {
                    sub.subscription.unsubscribe();
                } catch (e) {
                    console.warn('取消订阅失败:', e);
                }
            }
        });
        subscriptions = [];
    };

    return {
        init,
        subscribe,
        unsubscribe,
        unsubscribeAllByTopic,
        unsubscribeAll
    };
};

export const websocket = createWebSocket();