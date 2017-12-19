package com.cjx.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cjx.server.handler.TcpServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

public class NettyTcpServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyTcpServer.class);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 指定为TCP
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            p.addLast(new TcpServerHandler());
                        };

                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 最多的积压连接数。 默认值50。
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持连接, 检测对方是否崩溃
            
            logger.info("监听8888端口");
            ChannelFuture future = serverBootstrap.bind(8888).sync(); // 绑定8888端口，开始接收进来的连接
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();  
        } finally {
            logger.info("shutdownGracefully");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}