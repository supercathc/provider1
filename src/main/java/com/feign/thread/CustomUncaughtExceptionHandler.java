package com.feign.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Logger log = LoggerFactory.getLogger(CustomUncaughtExceptionHandler.class);
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //自定义异常处理逻辑
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(10));
        log.info("开始提交任务");
        threadPool.execute(CustomUncaughtExceptionHandler::doSomeThing);
        log.info("提交任务完成");
    }
    private static void doSomeThing() {
        try {
            int value = 10 / 0;
        } catch (RuntimeException e) {
            log.error("执行异常:", e);
        }
    }
}

