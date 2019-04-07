package com.tree.mytoolutils.image;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.tree.mytoolutils.ThreadPoolUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class StatusMessage {
    private final int UPDATE_IMAGE = 1;

    String uri;
    boolean islocalExists = false;
    boolean isMemoryExists = false;

    ImageCache imageCache;
    Future<?> future;
    Bitmap bitmap;
    Map<String,StatusMessage> statusMessageList;

    ImageView imageView;


    private android.os.Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_IMAGE:
                    imageView.setImageBitmap(bitmap);
            }
        }
    };


    public StatusMessage(String uri,ImageCache imageCache) {
        this.imageCache = imageCache;
        this.uri = uri;
    }

    /**
     *将图片加载进View
     */
    public void into(final ImageView imageView){
        this.imageView = imageView;
        if(isMemoryExists){
            bitmap = imageCache.lruCache.get(uri);
            imageView.setImageBitmap(bitmap);
        }else if (islocalExists){
            try {
                bitmap = imageCache.getFromLocal(uri, imageView);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        Message message = new Message();
                        bitmap = (Bitmap) future.get();
                        imageCache.putBitmap(uri,bitmap);
                        message.what = UPDATE_IMAGE;
                        handler.sendMessage(message);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ThreadPoolUtils.getThreadPoolUtils().execute(task);
        }
        statusMessageList.remove(uri);
        statusMessageList = null;
    }
}
