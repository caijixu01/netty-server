package com.cjx.server.handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;  
import io.netty.channel.ChannelInboundHandlerAdapter;  
  
public class TcpServerHandler extends ChannelInboundHandlerAdapter{  
    private static final Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override  
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {  
        ByteBuf buf = (ByteBuf) msg;
        
        System.out.println("处理收到的数据包: \n" + ByteBufUtil.prettyHexDump(buf)); // 业务处理  
        
        ctx.write("ok"); // 应答  
        ctx.flush();
    }  
      
    @Override  
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  
        cause.printStackTrace();  
        ctx.close();  
    }  
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("终端连接: {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("终端断开连接: {}", ctx.channel());
        ctx.channel().close();
    }
}  