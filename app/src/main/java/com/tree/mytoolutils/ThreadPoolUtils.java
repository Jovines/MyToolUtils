package com.tree.mytoolutils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.tree.mytoolutils.image.MyMessage;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;


public class ThreadPoolUtils {

    public final int UPDATE_IMAGE = 1;

    private static ThreadPoolUtils threadPoolUtils;//静态的对象引用
    private ThreadPoolExecutor threadPoolExecutor;//线程池的

    private android.os.Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_IMAGE:
                    ImageView imageView = (ImageView) ((MyMessage) msg.obj).getImageView();
                    Bitmap bitmap = (Bitmap) ((MyMessage) msg.obj).getBitmap();
                    imageView.setImageBitmap(bitmap);
            }
        }
    };


    private ThreadPoolUtils() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }


    public Handler getHandler() {
        return handler;
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
