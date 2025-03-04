package com.automotivemes.config.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 聊天 WebSocket 处理器类，继承自 TextWebSocketHandler，用于处理 WebSocket 的文本消息
 */
public class ChatWebSocketHandler extends TextWebSocketHandler {
    // 使用 CopyOnWriteArrayList 存储所有连接的 WebSocket 会话，该列表是线程安全的
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * 当 WebSocket 连接建立后调用此方法
     * @param session 新建立的 WebSocket 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 将新的会话添加到会话列表中
        sessions.add(session);
        // 打印新连接建立的信息，包含会话 ID
        System.out.println("新连接建立: " + session.getId());
        // 启动心跳线程，定期向该会话发送心跳包
        sendPeriodicUpdates(session);
    }

    /**
     * 定期向指定的 WebSocket 会话发送心跳包
     * @param session 要发送心跳包的 WebSocket 会话
     */
    private void sendPeriodicUpdates(WebSocketSession session) {
        // 创建一个单线程的定时任务执行器
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // 安排一个定时任务，以固定的速率执行
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 检查会话是否处于打开状态
                if (session.isOpen()) {
                    // 若会话打开，则发送心跳包，心跳包内容为 "❤"
                    session.sendMessage(new TextMessage("❤"));
                }
            } catch (IOException e) {
                // 若发送心跳包时出现异常，打印错误信息
                System.err.println("心跳发送失败: " + e.getMessage());
            }
        }, 10, 10, TimeUnit.SECONDS); // 初始延迟 10 秒，之后每 10 秒执行一次
    }

    /**
     * 处理接收到的文本消息
     * @param session 发送消息的 WebSocket 会话
     * @param message 接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获取消息的有效负载（即消息内容）
        String payload = message.getPayload();
        // 打印接收到的消息内容
        System.out.println("收到消息: " + payload);

        // 消息广播逻辑：将消息发送给所有连接的会话
        sessions.forEach(s -> {
            try {
                // 检查会话是否处于打开状态
                if (s.isOpen()) {
                    // 若会话打开，则将消息广播给该会话，同时显示发送者的会话 ID
                    s.sendMessage(new TextMessage("用户" + session.getId() + "说: " + payload));
                }
            } catch (IOException e) {
                // 若广播消息时出现异常，打印错误信息
                System.err.println("消息广播失败: " + e.getMessage());
            }
        });
    }

    /**
     * 当 WebSocket 连接关闭后调用此方法
     * @param session 关闭的 WebSocket 会话
     * @param status 连接关闭的状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 从会话列表中移除关闭的会话
        sessions.remove(session);
        // 打印连接关闭的信息，包含会话 ID 和关闭状态
        System.out.println("连接关闭: " + session.getId() + ", 状态: " + status);
    }
}