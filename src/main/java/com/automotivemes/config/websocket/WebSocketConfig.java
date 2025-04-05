package com.automotivemes.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 实现功能【WebSocket 配置类】
 *
 * @author li.hongyu
 * @date 2025-03-04 19:46:37
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理，用于处理消息的路由和分发
     *
     * @param config 消息代理配置对象，用于设置消息代理的相关属性
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用一个简单的消息代理，客户端可以订阅以 /topic 开头的目的地来接收广播消息
        // 当服务端向以 /topic 开头的目的地发送消息时，消息代理会将消息广播给所有订阅该目的地的客户端
        config.enableSimpleBroker("/topic");

        // 设置应用程序的目的地前缀，客户端发送的消息如果以 /app 开头，
        // 会被路由到 @MessageMapping 注解标注的控制器方法进行处理
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 注册 STOMP 端点，客户端可以通过这些端点建立 WebSocket 连接
     *
     * @param registry STOMP 端点注册对象，用于注册 STOMP 端点并配置相关属性
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 添加一个 STOMP 端点，客户端可以通过 /mes-websocket 路径建立 WebSocket 连接
        registry.addEndpoint("/mes-websocket")
                // 允许所有来源的跨域请求，确保不同域名下的客户端都可以连接到该端点
                .setAllowedOriginPatterns("http://localhost:8080")
                // 启用 SockJS 支持，SockJS 是一个用于在不支持原生 WebSocket 的浏览器中模拟 WebSocket 功能的库
                // 它提供了一种向后兼容的方式，确保在各种浏览器环境下都能正常使用 WebSocket 功能
                .withSockJS();
    }
}