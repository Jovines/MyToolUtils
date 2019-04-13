package com.tree.mytoolutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class InternetUtils {

    //建立网络链接获取JSON数据
    public static Future<?> Requeest(final String url){
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() {
                InputStream inputStream = null;
                String responseData = null;
                HttpURLConnection httpURLConnection = null;
                try {
                    URL urll = new URL(url);
                    httpURLConnection = (HttpURLConnection) urll.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-javascript->json");
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.connect();
                    responseData = inputStreamToString(httpURLConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    assert httpURLConnection != null;
                    httpURLConnection.disconnect();
                }
                return responseData;
            }
        };
        return ThreadPoolUtils.getThreadPoolUtils().submit(task);
    }

    //输入流转字符串
    public static String inputStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
