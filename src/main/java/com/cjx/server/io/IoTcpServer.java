package com.cjx.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.util.HexUtil;

public class IoTcpServer {    
    private static final Logger logger = LoggerFactory.getLogger(IoTcpServer.class);

    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static ExecutorService bizThreadPool = Executors.newCachedThreadPool();
    
    public static void main(String[] args) throws Exception {
        logger.info("监听8888端口");
        ServerSocket server = new ServerSocket(8888); // 1) 监听8888端口
        try {
            while (true) {
                try {
                    logger.info("等待连接...");
                    Socket socket = server.accept(); // 2) 接收socket连接（阻塞）
                    logger.info("收到1个连接. {}", socket);
                    
                    threadPool.execute(() -> {
                        handle(socket); // 处理socket连接
                    });

                    logger.info("线程池: {}", threadPool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            server.close();
        }
    }

    public static void handle(Socket socket) {
        try {
            InputStream is = socket.getInputStream(); // 输入流
            PrintStream os = new PrintStream(socket.getOutputStream()); // 输出流

            byte[] bytes = new byte[100];
            while (true) {
                logger.info("等待数据包...");
                int len = is.read(bytes); // 3) 接收数据（阻塞）
                if (len != -1) {
                    bizThreadPool.execute(() -> {
                        try {
                            doBiz(bytes, len); // 4) 业务处理
                            os.println("ok"); // 5) 应答
                        } catch (Exception e) {
                            e.printStackTrace();
                        } 
                    });
                } else {
                    logger.info("客户端主动断开连接: {}", socket);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                logger.info("关闭socket连接: {}", socket);
                socket.close(); // 6) 关闭socket连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doBiz(byte[] bytes, int len) throws Exception {
        logger.info("处理收到的数据包: {}", HexUtil.bytes2hex(bytes, len));
        
        Thread.sleep(1000L);
    }
}
