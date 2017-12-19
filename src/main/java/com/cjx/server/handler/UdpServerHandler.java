package com.cjx.server.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;  
  
public class UdpServerHandler extends ChannelInboundHandlerAdapter{  
    private static final Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);

    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        DatagramPacket packet = (DatagramPacket) msg;
        ByteBuf buf = packet.content();
        
        logger.info("处理收到的数据包: \n" + ByteBufUtil.prettyHexDump(buf)); // 业务处理  
        
        ctx.write("ok"); // 应答  
        ctx.flush();  
    }  
      
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace();  
        ctx.close();  
    }  
  
}  