package com.tree.mytoolutils.image;

import android.graphics.Bitmap;
import android.os.Message;
import android.widget.ImageView;

import com.tree.mytoolutils.ThreadPoolUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class StatusMessage {

    String uri;
    boolean islocalExists = false;
    boolean isMemoryExists = false;

    ImageCache imageCache;
    Future<?> future;
    Bitmap bitmap;
    //更新完UI后从列表中删除该图片的消息状态，以便于jvm回收
    Map<String,StatusMessage> statusMessageList;






    public StatusMessage(String uri,ImageCache imageCache) {
        this.imageCache = imageCache;
        this.uri = uri;
    }

    /**
     *将图片加载进View
     */
    public void into(final ImageView imageView){
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
                        imageCache.putToLocal(uri,bitmap);
                        bitmap = imageCache.getFromLocal(uri, imageView);
                        imageCache.putBitmap(uri,bitmap);
                        message.what = ThreadPoolUtils.getThreadPoolUtils().UPDATE_IMAGE;
                        MyMessage<ImageView, Bitmap> myMessage = new MyMessage<>(imageView, bitmap);
                        message.obj = myMessage;
                        ThreadPoolUtils.getThreadPoolUtils().getHandler().sendMessage(message);
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
