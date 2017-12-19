package com.cjx.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.util.HexUtil;

public class NioTcpServer {
    private static final Logger logger = LoggerFactory.getLogger(NioTcpServer.class);

    private static ExecutorService bizThreadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {

        // 获取一个ServerSocket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到8888端口
        logger.info("监听8888端口");
        serverSocketChannel.socket().bind(new InetSocketAddress(8888)); // 1) 监听8888端口
        
        // 获取通道管理器
        Selector selector = Selector.open();
        // 将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        // 只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        logger.info("服务器端启动成功");
        // 轮询访问selector
        while (true) {
            logger.info("等待事件发生...");
            selector.select(); // 2) 等待事件发生（阻塞）
//            selector.select(1000); // 阻塞1秒, 自动返回
//            selector.selectNow(); // 也可以立马返回
//            selector.wakeup(); // 可以唤醒selector
            logger.info("发生了事件...");
            
            // 获取selector中的迭代器，选中项为注册的事件
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            
            Iterator<SelectionKey> itr = selectedKeys.iterator();
            while (itr.hasNext()) {
                SelectionKey selectionKey = itr.next();
                // 删除已选key，防止重复处理
                itr.remove();
                
                if (selectionKey.isAcceptable()) { // 客户端请求连接事件
                    ServerSocketChannel serverSocketChannel2 = (ServerSocketChannel) selectionKey.channel();
                    // 获得客户端连接通道
                    SocketChannel socketChannel = serverSocketChannel2.accept();
                    logger.info("收到1个连接. {}", socketChannel);
                    socketChannel.configureBlocking(false);
                    // 向客户端发消息
                    socketChannel.write(ByteBuffer.wrap("ok".getBytes()));
                    // 在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                    // 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) { // 有可读数据事件
                    handle(selectionKey); // 处理数据
                }
                logger.info("线程池: {}", bizThreadPool);
            }
        }
    }
    
    public static void handle(SelectionKey key) throws IOException {
        // 获取客户端传输数据可读取消息通道。
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // 创建读取数据缓冲器
        ByteBuffer receiveBuf = ByteBuffer.allocate(100);
        int readCount = socketChannel.read(receiveBuf); // 3) 接收数据（非阻塞）
        if(readCount > 0){
            logger.info("收到1个数据包");
            bizThreadPool.execute(() -> {
                try {
                    doBiz(receiveBuf.array(), receiveBuf.position()); // 4) 业务处理
                    if (socketChannel.isOpen()) {
                        socketChannel.write(ByteBuffer.wrap("ok".getBytes())); // 5) 应答
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            logger.info("客户端关闭");
            key.cancel();
            socketChannel.close(); // 6) 关闭socket连接
        }
    }
    
    public static void doBiz(byte[] bytes, int len) throws Exception {
        logger.info("处理收到的数据包: {}", HexUtil.bytes2hex(bytes, len));

        Thread.sleep(1000L);
    }

}