package com.tree.mytoolutils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;


public class ThreadPoolUtils {
    private static ThreadPoolUtils threadPoolUtils;//静态的对象引用
    private ThreadPoolExecutor threadPoolExecutor;//线程池的


    private ThreadPoolUtils() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }


    //创建单列对象
    public static ThreadPoolUtils getThreadPoolUtils() {
        if (threadPoolUtils == null) {
            synchronized (ThreadPoolUtils.class) {
                if (threadPoolUtils == null) {
                    threadPoolUtils = new ThreadPoolUtils();
                }
            }
        }
        return threadPoolUtils;
    }



    public void execute(Runnable task) {
        threadPoolExecutor.execute(task);
    }

    //一个有返回值的执行方法
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<T>(task);
        threadPoolExecutor.submit(futureTask);
        return futureTask;
    }
    //一个可以更新UI的执行方法
}
