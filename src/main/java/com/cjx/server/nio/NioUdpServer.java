package com.cjx.server.nio;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.util.HexUtil;

public class NioUdpServer {       
    private static final Logger logger = LoggerFactory.getLogger(NioUdpServer.class);
 
    private static DatagramChannel channel = null;
    
    public static void main(String[] args) throws Exception {
        ExecutorService bizThreadPool = Executors.newCachedThreadPool();

        channel = DatagramChannel.open();
        logger.info("监听9999端口");
        channel.socket().bind(new InetSocketAddress(9999)); // 1) 监听9999端口
        
        while (true) {
            logger.info("等待数据包...");
            ByteBuffer receiveBuf = ByteBuffer.allocate(100);
            SocketAddress socketAddress = channel.receive(receiveBuf); // 3) 接收数据（阻塞）
            
            bizThreadPool.execute(() -> {
                try {
                    handle(receiveBuf, socketAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            logger.info("线程池: {}", bizThreadPool);
        }
    }
    
    public static void handle(ByteBuffer receiveBuf, SocketAddress socketAddress) throws Exception {
        doBiz(receiveBuf.array(), receiveBuf.position()); // 4) 业务处理

        ByteBuffer sendBuf = ByteBuffer.wrap("ok".getBytes());
        channel.send(sendBuf, socketAddress); // 5) 应答
    }
    
    public static void doBiz(byte[] bytes, int len) throws Exception {
        logger.info("处理收到的数据包: {}", HexUtil.bytes2hex(bytes, len));

        Thread.sleep(1000L);
    }
}