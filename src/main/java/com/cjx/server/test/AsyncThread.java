package com.cjx.server.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取异步结果
 */
public class AsyncThread {
    private static final Logger logger = LoggerFactory.getLogger(AsyncThread.class);

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        Future<String> future = executorService.submit(() -> {
            logger.info("耗时任务 begin");
            Thread.sleep(3000);
            return "666";
        });
        executorService.shutdown();
        
        for (int i = 0; i < 5; i++) {
            logger.info("判断结果 ..");
            if (future.isDone() && !future.isCancelled()) {
                logger.info("任务结果: " + future.get());
                break;
            } else {
                logger.info("做其它事" + i);
                Thread.sleep(1000);
            }
        }

        // 做其它事
//        logger.info("做其它事 begin");
//        Thread.sleep(1000);
//        logger.info("做其它事 end");

        // 等待结果
//        logger.info("等待结果");
//        logger.info("任务结果: " + future.get());
    }

}
