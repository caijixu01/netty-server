package com.cjx.server.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.cjx.server.util.HexUtil;

public class SimpleIoTcpServer {
    public static void main(String args[]) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888); // 1) 监听8888端口
        
        Socket socket = serverSocket.accept(); // 2) 接收socket连接（阻塞）

        InputStream is = socket.getInputStream(); // 输入流
        OutputStream os = socket.getOutputStream(); // 输出流
        
        byte[] bytes = new byte[100];
        int len = is.read(bytes); // 3) 接收数据（阻塞）
        
        System.out.println("处理收到的数据包: " + HexUtil.bytes2hex(bytes, len)); // 4) 业务处理

        os.write("ok".getBytes()); // 5) 应答
        
        serverSocket.close(); // 6) 关闭socket连接
    }
}