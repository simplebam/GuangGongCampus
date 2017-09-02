package com.example.yueyue.campusapp.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Stream转为String类型
 * Created by yueyue on 2017/5/24.
 */

public class Stream2String {
    public static String readStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String content = baos.toString();
        return content;
    }

    public static InputStream getUrlNetStream(String resourceURL) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            int code = conn.getResponseCode();
            if (code == 200) {
                return conn.getInputStream();
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }


    /**
     * get方式Url联网得到的结果转为String
     *
     * @param resourceURL 请求连接
     * @param cookie      网址的cookie
     * @return 页的源代码, null代表获取不成功
     */
    public static String getUrlNet2Str(String resourceURL, String cookie) {
        String content = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("Cookie", cookie);// 有网站需要将当前的session id一并上传
            int code = conn.getResponseCode();
            String resCookie = conn.getRequestProperty("Cookie");//就是上面的那个cookie
            String connCookie = conn.getHeaderField("Set-Cookie");//这是空的
            // Log.i(TAG, "HttpURLConnection返回的cookie:" + cookie + "----resCookie:" + resCookie);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                content = Stream2String.readStream(is);
                //Log.i(TAG, "HttpURLConnection返回的result:" + content);
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Log.i(TAG, "HttpURLConnection返回的result:" + content);
            return content;
        }
        return content;
    }


    /**
     * post方式Url联网得到的结果转为String
     *
     * @param resourceURL 请求连接
     * @param cookie      网址的cookie
     * @param data        请求体
     * @return 页的源代码, null代表获取不成功
     */
    public static String postUrlNet2Str(String resourceURL, String cookie, String data) {
        String content = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            //Log.i(TAG, "获取数据前的cook:++++++++++++++" + cookie);
            conn.setRequestProperty("Cookie", cookie);// 有网站需要将当前的session id一并上传
            //Content-Type:application/x-www-form-urlencoded; charset=UTF-8
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");// 有网站需要将当前的session id一并上传
            //Content-Length:51
            conn.setRequestProperty("Content-Length", data.length() + "");// 有网站需要将当前的session id一并上传

            conn.setDoOutput(true);
            conn.getOutputStream().write(data.getBytes());

            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                content = Stream2String.readStream(is);
                Log.i(TAG, "postUrlNet2Str返回的result:" + content);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "异常:" + e.getMessage());
            return content;
        }

        return content;
    }


    /**
     * 使用OkHttp 联网进行请求数据-->少用
     *
     * @param url
     */
    public static String getHtmlStrByOkHttp(String url, String cookie) {
        String content = null;
        OkHttpClient client = new OkHttpClient();
        try {
            Log.i(TAG, "请求Cookie:" + cookie);
            Request request = new Request.Builder().url(url).header("Cookie", cookie).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                //Log.i(TAG,"请求结果:"+response.body().string());
                content = response.body().string();
                Log.i(TAG, "请求结果:" + content);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return content;
        }

        return content;
    }


}