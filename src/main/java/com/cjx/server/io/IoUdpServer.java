package com.cjx.server.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.util.HexUtil;

class IoUdpServer {
    private static final Logger logger = LoggerFactory.getLogger(IoUdpServer.class);
    
    public static void main(String[] args) throws Exception {
        ExecutorService bizThreadPool = Executors.newCachedThreadPool();

        logger.info("监听9999端口");
        DatagramSocket datagramSocket = new DatagramSocket(9999); // 1) 监听9999端口
        
        while (true) {
            logger.info("等待数据包...");
            byte[] receiveBytes = new byte[100];
            DatagramPacket receivePacket = new DatagramPacket(receiveBytes, receiveBytes.length);
            datagramSocket.receive(receivePacket); // 3) 接收数据（阻塞）
            
            bizThreadPool.execute(() -> {
                try {
                    handle(datagramSocket, receivePacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            logger.info("线程池: {}", bizThreadPool);
        }
    }
    
    public static void handle(DatagramSocket datagramSocket, DatagramPacket receivePacket) throws Exception {
        doBiz(receivePacket.getData(), receivePacket.getLength()); // 4) 业务处理

        byte[] sendBytes = "ok".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBytes,
                                                        sendBytes.length, 
                                                        receivePacket.getAddress(),
                                                        receivePacket.getPort());
        datagramSocket.send(sendPacket); // 5) 应答
    }
    
    public static void doBiz(byte[] bytes, int len) throws Exception {
        logger.info("处理收到的数据包: {}", HexUtil.bytes2hex(bytes, len));
        
        Thread.sleep(100L);
    }
        
}