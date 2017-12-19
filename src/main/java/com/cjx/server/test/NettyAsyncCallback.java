package com.cjx.server.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 异步回调 
 */
public class NettyAsyncCallback {
    private static final Logger logger = LoggerFactory.getLogger(NettyAsyncCallback.class);

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        
        Future<String> future = bossGroup.submit(new java.util.concurrent.Callable<String>() {
            @Override
            public String call() throws Exception {
                logger.info("耗时任务 begin");
                Thread.sleep(3000);
                return "666";
            }
        });

        future.addListener(new GenericFutureListener<Future<String>>() {
            @Override
            public void operationComplete(Future<String> future) throws Exception {
                //处理返回的结果
                String str =  future.get();
                logger.info("处理耗时任务返回的结果: " + str);
            }
        });
        
        for (int i = 0; i < 5; i++) {
            logger.info("做其它事" + i);
            Thread.sleep(1000);
        }
    }
}
