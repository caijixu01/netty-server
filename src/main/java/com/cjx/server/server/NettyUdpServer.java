package com.cjx.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.handler.UdpServerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NettyUdpServer {    
    private static final Logger logger = LoggerFactory.getLogger(NettyUdpServer.class);

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                  .group(group)
                  .channel(NioDatagramChannel.class) // 指定为UDP
                  .handler(new ChannelInitializer<NioDatagramChannel>() {
                      protected void initChannel(NioDatagramChannel channel) throws Exception {
                          ChannelPipeline pipeline = channel.pipeline();
                          pipeline.addLast(new UdpServerHandler());
                      }
                  })
                  .option(ChannelOption.SO_BROADCAST, true);
            
            logger.info("监听9999端口");
            ChannelFuture future = bootstrap.bind(9999).sync();
            future.channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            logger.info("shutdownGracefully");
            group.shutdownGracefully();
        }
    
    }
}
