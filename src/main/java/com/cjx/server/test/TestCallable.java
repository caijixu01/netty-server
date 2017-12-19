package com.cjx.server.test;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCallable {
    private static final Logger logger = LoggerFactory.getLogger(TestCallable.class);

    public static void main(String[] args) throws Exception {
        
        ExecutorService executorService = Executors.newCachedThreadPool();
        CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executorService);
        
        logger.info("1线程池: \n" + executorService);
        // 开始处理5个耗时的task
        for (int i = 0; i < 5; i++) {
            logger.info("submit task " + i);
            completionService.submit(getTask(i));
        }
        logger.info("2线程池: \n" + executorService);

        // 开始处理其它业务
        logger.info("处理其它业务begin");
        Thread.sleep(300);
        logger.info("处理其它业务end");
        
        // 开始等待5个task完成, 并处理task返回的结果
        for (int i = 0; i < 5; i++) {
            int temp = completionService.take().get();
            System.out.println(ZonedDateTime.now() + ": task: " + temp + " 执行完毕");
            logger.info("3线程池: \n" + executorService);
        }

        executorService.shutdown();
        
    }

    private static Callable<Integer> getTask(final int no) {
        return new Callable<Integer>() {
            public Integer call() throws Exception {
                int time = (new Random().nextInt(9) + 1) * 1000;
                logger.info(ZonedDateTime.now() + ": 开始执行task: " + no + ", 将用时: " + time);
                Thread.sleep(time);
                return no;
            }
        };
    }
}
