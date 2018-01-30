### Java爬取必应图片教程
实在话,java爬取数据也是挺好的,虽然不如python等,但简单的还是可以的,下面我就介绍一下必应每日一图的爬取哈

参考博客:
>1. [分享几个必应每日图片抓取的方法 - 幻杀博客](https://ihuan.me/2645.html)
>2. <font color=red>这里提供现成的链接!!! </font>[5种方法获取Bing每日图片 附送高清API接口及网站背景调用-缙哥哥的博客](https://www.dujin.org/fenxiang/jiaocheng/3618.html)
>3. 郭霖先生提供的(小心谨慎调用哈):http://guolin.tech/api/bing_pic

###**正文**

* 1. 其实简单的就是获取[微软必应搜索 - 全球搜索，有问必应 (Bing)](http://cn.bing.com/) 的json数据,这里使用浏览器开发工具可以拿到(不介绍哈,需要的可以自定百度)

json数据链接:http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1 (n这个是返回数据的条数,返回最新的几条)

![这里写图片描述](http://img.blog.csdn.net/20170531133449761?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

下面是我的主要代码:
1.Bean类-->使用Gson进行解析

```
public class BingYingPic {

    @SerializedName("images")
    public List<BaseBingPic> images;

    public class BaseBingPic {
        @SerializedName("url")
        public String bingBasePicUrl;//这里的链接还需要加上微软的http://cn.bing.com在前面

        @SerializedName("enddate")
        public String endDate;//最后更新的时间

        @SerializedName("startdate")
        public String startDate;

    }

}

```

* 2.Stream2String(流转字符串)-->使用原生的HttpUrlConnection
(Ps:这里的代码有点多,觉得烦的话可以不看,因为下面一步会介绍okhttp进行获取)

```
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
     * @param resourceURL  请求连接
     * @param cookie  网址的cookie-->get请求可以不需要理会
     * @return  页的源代码, null代表获取不成功
     */
    public static String getHtmlStr(String resourceURL, String cookie) {
        String content = null;
        try {
            //    Document document = Jsoup.connect(resourceURL).cookie("Cookie", cookie).get();

            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("Cookie", cookie);// 有网站需要将当前的session id一并上传
            int code = conn.getResponseCode();
            String resCookie = conn.getRequestProperty("Cookie");
            String connCookie = conn.getHeaderField("Set-Cookie");
            Log.i(TAG, "HttpURLConnection返回的connCookie:" + connCookie + "----resCookie:" + resCookie);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                content = Stream2String.readStream(is);
                Log.i(TAG, "HttpURLConnection返回的result:" + content);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "HttpURLConnection返回的result:" + content);
            return content;
        }

        return content;
    }
}
```

这里感觉麻烦的话建议使用okhttp等框架

* 3.进行联网
[1].使用1+2+3步骤的话

```
    private void getDailyBingPic() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
                String content = getHtmlStr1(url);
                BingYingPic bingYingPic = new Gson().fromJson(content, BingYingPic.class);
                System.out.println(bingYingPic + "-------------");
                System.out.println(bingYingPic.images.get(0).bingBasePicUrl + "-------------");
                System.out.println(bingYingPic.images.get(0).endDate + "-------------");

            }
        }).start();


    }
```

[2] 不要2步骤的话

```

    private void getDailyBingPic1() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    String content=response.body().string() ;
                    //response.body().string() 不容许同时出现两次
                 //   System.out.println(response.body().string() + "-------------" + "okhtpp");

                    BingYingPic bingYingPic = new Gson().fromJson(content, BingYingPic.class);
                    System.out.println(bingYingPic + "-------------");
                    System.out.println(bingYingPic.images.get(0).bingBasePicUrl + "-------------");
                    System.out.println(bingYingPic.images.get(0).endDate + "-------------");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
```



### 源码地址
源码:[GuangGongCampus/simplebam](https://github.com/simplebam/GuangGongCampus)
项目下面的getbingyingphoto的module就是啦