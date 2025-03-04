// 定义一个名为 WSManager 的类，用于管理 WebSocket 连接
// eslint-disable-next-line no-unused-vars
export class WSManager {
    /**
     * 构造函数，初始化 WebSocket 连接和事件监听器
     * @param {string} url - WebSocket 服务的连接地址
     */
    constructor(url) {
        // 创建一个新的 WebSocket 实例，连接到指定的 URL
        this.socket = new WebSocket(url);
        // 定义一个对象，用于存储不同类型事件的监听器回调函数
        this.listeners = {
            open: [],  // 存储 WebSocket 连接打开事件的回调函数
            message: [], // 存储接收到消息事件的回调函数
            error: [], // 存储 WebSocket 连接出错事件的回调函数
            close: [] // 存储 WebSocket 连接关闭事件的回调函数
        };

        // 当 WebSocket 连接成功打开时触发
        this.socket.onopen = (event) => {
            // 遍历所有注册的 open 事件回调函数，并依次执行
            this.listeners.open.forEach(cb => cb(event));
            // 连接成功后，启动心跳机制
            this.startHeartbeat();
        };

        // 当 WebSocket 接收到消息时触发
        this.socket.onmessage = (event) => {
            // 过滤掉心跳包（假设心跳包内容为 '❤'）
            if (event.data !== '❤') {
                // 解析接收到的消息数据为 JSON 对象
                const parsedData = JSON.parse(event.data);
                // 遍历所有注册的 message 事件回调函数，并依次执行，传入解析后的数据
                this.listeners.message.forEach(cb => cb(parsedData));
            }
        };

        // 此处可添加其他事件处理逻辑，如 onerror 和 onclose 事件
        // 其他事件处理类似...
    }

    /**
     * 启动心跳机制，定期发送心跳包以保持连接活跃
     */
    startHeartbeat() {
        // 使用 setInterval 方法每隔 9000 毫秒（即 9 秒）执行一次回调函数
        setInterval(() => {
            // 检查 WebSocket 连接的状态是否为 OPEN（即连接已成功建立）
            if (this.socket.readyState === WebSocket.OPEN) {
                // 若连接正常，发送心跳包（内容为 '❤'）
                this.socket.send('❤');
            }
        }, 9000);
    }

    /**
     * 为指定的事件类型注册回调函数
     * @param {string} event - 事件类型，如 'open', 'message', 'error', 'close'
     * @param {function} callback - 事件触发时要执行的回调函数
     * @returns {WSManager} - 返回当前实例，支持链式调用
     */
    on(event, callback) {
        // 将回调函数添加到对应事件类型的监听器数组中
        this.listeners[event].push(callback);
        // 返回当前实例，方便进行链式调用，如 wsManager.on('open', cb1).on('message', cb2);
        return this;
    }

    /**
     * 向 WebSocket 服务发送数据
     * @param {Object} data - 要发送的数据对象，会被转换为 JSON 字符串
     */
    send(data) {
        // 检查 WebSocket 连接的状态是否为 OPEN
        if (this.socket.readyState === WebSocket.OPEN) {
            // 若连接正常，将数据对象转换为 JSON 字符串并发送
            this.socket.send(JSON.stringify(data));
        } else {
            // 若连接未打开，在控制台输出错误信息
            console.error('WebSocket未连接');
        }
    }

    /**
     * 关闭 WebSocket 连接
     */
    close() {
        // 调用 WebSocket 实例的 close 方法关闭连接
        this.socket.close();
    }
}