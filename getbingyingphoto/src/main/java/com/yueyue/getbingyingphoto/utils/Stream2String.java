package com.yueyue.getbingyingphoto.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Stream转为String类型
 * Created by yueyue on 2017/5/24.
 */

public class Stream2String {
    private static final String TAG = Stream2String.class.getSimpleName();

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


    /**
     * 获得一个网页的源代码
     *
     * @param resourceURL 请求连接
     * @param cookie      网址的cookie-->get请求可以不需要理会
     * @return 页的源代码, null代表获取不成功
     */
    public static String loadDataByGet(String resourceURL, String cookie) {
        String content = null;
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            if (!TextUtils.isEmpty(cookie)) {
                conn.setRequestProperty("Cookie", cookie);// 有网站需要将当前的session id一并上传
            }
            int code = conn.getResponseCode();
            String resCookie = conn.getRequestProperty("Cookie");
            String connCookie = conn.getHeaderField("Set-Cookie");
            Log.i(TAG, "HttpURLConnection返回的connCookie:" + connCookie + "----resCookie:" + resCookie);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                content = Stream2String.readStream(is);
            }

        } catch (IOException e) {
            return content;
        }

        return content;
    }
}
