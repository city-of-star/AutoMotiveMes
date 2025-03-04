<template>
  <div class="chat-container" id="chat-container">
    <div v-for="(msg, index) in messages" :key="index" class="message-bubble">
      {{ msg.content }}
    </div>
    <input v-model="inputMsg" @keyup.enter="sendMessage" />
    <button @click="sendMessage">发送</button>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount, nextTick} from 'vue';
import { WSManager } from '@/utils/websocket';

// 存储聊天消息
const messages = ref([]);
// 存储用户输入的消息
const inputMsg = ref('');
// 声明 WebSocket 实例变量，初始值为 null
let ws = null;

onMounted(() => {
  // 创建 WSManager 实例，连接到指定的 WebSocket 地址
  ws = new WSManager('ws://localhost:8080/mes-websocket')
      // 监听 WebSocket 连接成功事件，连接成功时在控制台输出提示信息
      .on('open', () => console.log('连接成功'))
      // 监听 WebSocket 接收到消息事件
      .on('message', (msg) => {
        // 将接收到的消息添加到 messages 数组中
        messages.value.push(msg);
        // 自动滚动到底部
        nextTick(() => {
          // 获取聊天容器元素
          const container = document.getElementById('chat-container');
          // 将滚动条滚动到容器的最底部
          container.scrollTop = container.scrollHeight;
        });
      })
      // 监听 WebSocket 连接错误事件，连接错误时在控制台输出错误信息
      .on('error', (err) => console.error('连接错误:', err));
});

// 发送消息的方法
const sendMessage = () => {
  // 使用 trim() 方法去除输入消息首尾的空白字符，判断输入是否为空
  // trim() 是 JavaScript 字符串的一个方法，用于去除字符串首尾的空白字符（包括空格、制表符、换行符等）
  // 如果输入的字符串只有空白字符，trim() 后会得到一个空字符串，通过判断其长度是否大于 0 来确定输入是否有实际内容
  if (inputMsg.value.trim()) {
    // 发送消息，消息格式为一个对象，包含消息类型、内容和时间戳
    ws.send({
      type: 'text',
      content: inputMsg.value,
      timestamp: Date.now()
    });
    // 清空输入框
    inputMsg.value = '';
  }
};

// 组件卸载前执行的钩子函数
onBeforeUnmount(() => {
  // 关闭 WebSocket 连接
  ws.close();
});
</script>