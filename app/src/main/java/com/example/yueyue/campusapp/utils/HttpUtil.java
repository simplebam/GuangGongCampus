package com.example.yueyue.campusapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.yueyue.campusapp.MyApplication;
import com.example.yueyue.campusapp.implement.CallBack;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.example.yueyue.campusapp.implement.LoginCallback;
import com.example.yueyue.campusapp.implement.OkHttpCallback;
import com.example.yueyue.campusapp.models.BingYingPic;
import com.example.yueyue.campusapp.models.DetailsInfo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.x;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yueyue on 2017/5/22.
 */

public class HttpUtil {
    private static final String TAG = HttpUtil.class.getSimpleName();

    // TODO: 2017/8/31 改回使用okHttp联网,随便让它管理session,看看行不行
    public static String cookie;//cookie信息
    public static String account;//用户id
    public static String pwd;//密码
    public static String saveCode = "";//验证码
    public static String name;//用户名字
    public static String bing_pic_url;//用户名字


    public static void login(final LoginCallback loginCallback) {
        //开启线程获取cookie，验证密码是否正确
        getLoginCookie(loginCallback);

    }

    /**
     * xutils获得登录的Cookie-->因为这框架cookie问题处理不好,所以建议不要使用
     *
     * @param loginCallback
     */
    private static void getLoginCookie1(final LoginCallback loginCallback) {
        //请求网址
        String LOGIN_POST_URL = GlobalValue.LOGIN_BASE_URL;

        RequestParams params = new RequestParams(LOGIN_POST_URL);
        //请求体的内容
        params.addBodyParameter("account", account);
        params.addBodyParameter("pwd", pwd);
        params.addBodyParameter("verifycode", saveCode);

        params.setUseCookie(true);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    String msg = jsonObject.getString("msg");

                    if ("y".equals(status)) {
                        //在请求成功之后，获取cookies信息
                        DbCookieStore instance = DbCookieStore.INSTANCE;
                        List<HttpCookie> cookies = instance.getCookies();
                        for (HttpCookie httpCookie : cookies) {
                            String cookieKey = httpCookie.getName();
                            String cookieValue = httpCookie.getValue();

                            Log.i(TAG, "cookie数据+ cookieKey:" + cookieKey + "---cookieValue:" + cookieValue);
                            Log.i(TAG, "httpCookie数据:" + httpCookie);

                            //cookies:[JSESSIONID=47A84A2989D2526BE984C47CEE4123CF]

                            if ("JSESSIONID".equals(cookieKey)) {
//                                //JSESSIONID=EAC252C73983A45101FE8C42891B34CC
                                cookie = httpCookie.toString();
                                SpUtils.putString(MyApplication.getAppContext(),
                                        "cookie", cookie);

                                Log.i(TAG, "cookie:---------" + cookie);
                                SpUtils.putBoolean(MyApplication.getAppContext(),
                                        GlobalValue.COOKIE_OK, true);

                            }
                        }

                        //Log.i(TAG, "======================1");
                        Log.i(TAG, "线程名字:" + Thread.currentThread().getName());
                        loginCallback.onLoginFinshed();


                    }

                    loginCallback.onError(new RuntimeException("getLoginCookie运行异常"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    loginCallback.onError(new RuntimeException("getLoginCookie运行异常"));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                loginCallback.onError(new RuntimeException("getLoginCookie运行异常"));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                loginCallback.onError(new RuntimeException("getLoginCookie运行异常"));
            }

            @Override
            public void onFinished() {
                loginCallback.onError(new RuntimeException("getLoginCookie运行异常"));
            }
        });
    }

    /**
     * 使用OkHttp获得登录的Cookie
     *
     * @param loginCallback
     */
    private static void getLoginCookie(final LoginCallback loginCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求网址
                String LOGIN_POST_URL = GlobalValue.LOGIN_BASE_URL;

                //设置账号密码
                Log.i(TAG, "account:" + account + "---pwd:" + pwd + "---verifycode" + saveCode);
                FormBody formBody = new FormBody.Builder()
                        .add("account", account)
                        .add("pwd", pwd)
                        .add("verifycode", saveCode)
                        .build();
                //设置cookie信息
                OkHttpClient.Builder builder = new OkHttpClient.Builder();

                builder.cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);

                        for (Cookie httpCookie : cookies) {

//                            Log.i(TAG,"拿到的cookie:"+httpCookie+"--------");
//                            Log.i(TAG,"拿到的cookie:"+httpCookie.name()+"--------");
//                            Log.i(TAG,"拿到的cookie:"+httpCookie.value()+"--------");
//                            Log.i(TAG,"拿到的cookie:"+httpCookie.hostOnly()+"--------");
//                            Log.i(TAG,"拿到的cookie:"+url+"--------");
                            //获得cookie,但可能是登陆不成功返回的cookie,所以谨慎使用哈
                            cookie = httpCookie.name() + "=" + httpCookie.value();

                        }

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                });
                OkHttpClient okHttpClient = builder.build();
                Request request = new Request.Builder().addHeader("cookie", cookie)
                        .url(LOGIN_POST_URL).post(formBody).build();

                okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loginCallback.onError(new RuntimeException(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //   Log.d(TAG, "onResponse: " + response.body().string());
                        //这里不可以写response.body().string();否则会引起内存泄漏
                        //因为它几乎会同时两次进行联网,报出致命的调度错误
                        String result = new String(response.body().string());
                        System.out.println("result:" + result);
                        handleLoginResult(result, loginCallback);//处理结果
                    }
                });
            }
        }).start();

    }

    /**
     * 处理登陆返回的结果{"status":"y","msg":"/login!welcome.action"}
     *
     * @param result        登陆结果--> 成功"status":"y","msg":"/login!welcome.action"
     *                      失败"status":"n","msg":"您的帐号或密码不正确"
     *                           {"status":"n","msg":"连接已过期"}  --验证码没有更新
     * @param loginCallback 回掉函数
     */
    private static void handleLoginResult(String result, final LoginCallback loginCallback) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            String msg = jsonObject.getString("msg");
//            Log.i(TAG, "msg:" + msg + "--------status:" + status);
            if ("y".equals(status)) {
                //说明登陆成功
//                SpUtils.putString(MyApplication.getAppContext(),
//                        "cookie", cookie);
                //获得名字
                getYourName(null);

                Log.i(TAG, "cookie:---------" + cookie);
//                SpUtils.putBoolean(MyApplication.getAppContext(), GlobalValue.COOKIE_OK, true);
                SpUtils.putString(MyApplication.getAppContext(), GlobalValue.ACCOUNT, account);
                SpUtils.putString(MyApplication.getAppContext(), GlobalValue.PWD, pwd);
                loginCallback.onLoginFinshed();


            } else {
                //登陆失败
//                cookie = "";
//                SpUtils.putString(MyApplication.getAppContext(),
//                        "cookie", cookie);
//                SpUtils.putBoolean(MyApplication.getAppContext(),
//                        GlobalValue.COOKIE_OK, false);
                loginCallback.onError(new RuntimeException(msg));

            }

        } catch (JSONException e) {
            e.printStackTrace();
            loginCallback.onError(e);
        }

    }


    /**
     * 获得某一周的Course链接返回的Json数据
     *
     * @param dataCallback
     */
    public static void getCourseHtml(final DataCallback dataCallback, final int week) {

        new AsyncTask<Void, Void, String>() {
            //在联网请求开始之前
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //显示进行条
                dataCallback.onStart();
            }

            //在后台(子线程进行联网请求)
            @Override
            protected String doInBackground(Void... params) {
                Log.i(TAG, "getCourseHtml开始了....");
                //final String CouseUrl = "http://222.200.98.147/xsgrkbcx!getKbRq.action?xnxqdm=201602&zc=14";
//                final String CouseUrl = "http://222.200.98.147/xsgrkbcx!getKbRq.action?" +
//                        "xnxqdm=" + GlobalValue.XNXQDM + "&zc=" + DateUtil.getcurrentWeek();
                final String CouseUrl = GlobalValue.COUSE_URL_ZC + week;
                // TODO: 2017/6/2 这里修改了链接 -->需要改回哈
                //final String CouseUrl = GlobalValue.COUSE_URL_ZC + 10;
                String resContent = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(CouseUrl)
                            .addHeader("Cookie", cookie)
                            .build();
                    Response response = client.newCall(request).execute();
                    resContent = response.body().string();
                    Log.i(TAG, "OkHttpClient返回的result:" + resContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    dataCallback.onError(new RuntimeException("getCourseHtml这里出错了"));
                }
                return resContent;
            }

            //在联网请求完成之后
            @Override
            protected void onPostExecute(String jsonStr) {
                super.onPostExecute(jsonStr);
                if (!TextUtils.isEmpty(jsonStr)) {
                    //数据不为空的情况下后续
                    HandleResponseUtil.handleCourseJsonStr(jsonStr, dataCallback);
                }
            }
        }.execute();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    HttpURLConnection conn = (HttpURLConnection) new URL(CouseUrl).openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setConnectTimeout(5000);
//                    conn.setReadTimeout(5000);
//                    conn.setRequestProperty("Cookie", cookie);// 有网站需要将当前的session id一并上传
//                    int code = conn.getResponseCode();
//                    if (code==200) {
//                        InputStream is = conn.getInputStream();
//                        String content = Stream2String.readStream(is);
//                        Log.i(TAG, "HttpURLConnection返回的result:" + content);
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();


    }


    /**
     * 处理网上下载回来的全部课程的html-->全部课程嵌入在html代码中,但单一课程有json数据返回的
     *
     * @param dataCallback 回调接口
     * @param cookie       cookie
     */
    public static void getAllCourseHtml(final DataCallback dataCallback, final String cookie) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("-----------------------------");
                String allCourseHtmlStr = Stream2String.getUrlNet2Str(GlobalValue.COUSE_URL_ALL,
                        cookie);
                if (!TextUtils.isEmpty(allCourseHtmlStr)) {
                    HandleResponseUtil.handleCourseAllHtmlStr(allCourseHtmlStr, dataCallback);
                }
//                System.out.println("-----------------------------");

            }
        }).start();


    }


    /**
     * 从学生个人信息数据表找-->找不到就联网获取学生信息个人表找
     *
     * @param dataCallback
     */
    public static void getYourName(DataCallback dataCallback) {
        //在detailsinfo数据表中查询
        DetailsInfo detailsInfo = DataSupport.select("name").where("account=?", account).
                findLast(DetailsInfo.class);
        if (detailsInfo != null && !TextUtils.isEmpty(detailsInfo.name)) {
            HttpUtil.name = detailsInfo.name;
            return;
        }
        //加入PersonInfo数据表中没有,联网获取数据
        getDetailsHtml(dataCallback, cookie);
    }

    /**
     * 处理网上下载回来的学生个人信息的html
     */
    public static void getDetailsHtml(final DataCallback dataCallback,
                                      final String cookie) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("-----------------------------");
                String detailsHtmlStr = Stream2String.getUrlNet2Str(
                        GlobalValue.PER_INFO_URL, cookie);
                if (!TextUtils.isEmpty(detailsHtmlStr)) {
                    HandleResponseUtil.handleDetailsHtmlStr(detailsHtmlStr, dataCallback);
                }
//                System.out.println("-----------------------------");


            }
        }).start();


    }


    /**
     * 获取必应搜索的每日一图
     *
     * @return String url链接
     */
    public static void loadDailyBingPic(final Context context, final CallBack callBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //String url = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
                String content = Stream2String.getUrlNet2Str(GlobalValue.BING_YING_SEARCH_JSON_URL, "");
                BingYingPic bingYingPic = new Gson().fromJson(content, BingYingPic.class);
//                System.out.println(bingYingPic + "-------------");
//                System.out.println(bingYingPic.images.get(0).bingBasePicUrl + "-------------");
//                System.out.println(bingYingPic.images.get(0).endDate + "-------------");
                if (bingYingPic != null && !TextUtils.isEmpty(bingYingPic.images.get(0).bingBasePicUrl)) {
                    bing_pic_url = GlobalValue.BING_YING_BASE_URL + bingYingPic.images.get(0).bingBasePicUrl;
                    // Log.i(TAG,"sjhdasjhdska获取到的bing_pic_url链接:"+bing_pic_url);
                } else {
                    bing_pic_url = GlobalValue.BING_YING_SEARCH_PIC_URL_GL;
                }

                //存储URL地址
                SpUtils.putString(context, GlobalValue.BING_YING_BASE_URL, bing_pic_url);

//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    String url="http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
//                    Request request = new Request.Builder().url(url).build();
//                    Response response = client.newCall(request).execute();
//                    System.out.println(response.body().string()+"-------------"+"okhtpp");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                if (callBack != null) {
                    callBack.onFinsh("");
                }

            }
        }).start();

    }

    /**
     * 获得教务系统的成绩Json数据-->post请求
     * 不可以使用get请求,因为会缺少最新一期的
     */
    public static void getScoreData(final DataCallback dataCallback) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String SCORE_URL = GlobalValue.SCORE_URL;
                String semesterCode = "";
                //默认显示3页
                int page = 3;
                //默认显示150条
                int rows = 150;
                //{"total":0,"rows":[]}
//                if (SemesterUtil.isOutOfRange(semesterCode)) {
//                    //越界了,代表数据全部请求完成-->{"total":64,"rows":[]}
//                    if (dataCallback != null) {
//                        dataCallback.onFinshed();
//                    }
//
//                }


                //想获取全部的页数
                int len = 1;
                ArrayList<String> list = new ArrayList<>();
                for (; page >= len; page--) {
                    //xnxqdm=201601&jhlxdm=&page=1&rows=50&sort=xnxqdm&order=asc
                    //      xnxqdm=&jhlxdm=&page=1&rows=50&sort=xnxqdm&order=asc
                    String data = "xnxqdm=" + semesterCode + "&jhlxdm=&page=" + page +
                            "&rows=" + rows + "&sort=xnxqdm&order=asc";
                    String str = Stream2String.postUrlNet2Str(SCORE_URL, cookie, data);
                    try {
                        if (!TextUtils.isEmpty(str)) {
                            // TODO: 2017/6/5 这里报空指针异常-->点击按钮获取数据的时候
                            JSONObject jsonObject = new JSONObject(str);
                            int total = jsonObject.getInt("total");
                            if (total <= 0) {
                                continue;
                            }
                            list.add(str);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (dataCallback != null) {
                            dataCallback.onError(new RuntimeException("getScoreData处理JSON数据出错"));
                        }
                    }

                }


                if (list != null && list.size() > 0) {
                    HandleResponseUtil.handleScoreStr(list, dataCallback);
                } else {
                    //说明没有数据
                    if (dataCallback != null) {
                        dataCallback.onFinshed();
                    }

                }


            }

        }).start();


    }


    /**
     * 获得图片流
     * http://www.jianshu.com/p/53e8e0eb36b8
     *
     * @param callback
     * @return
     */
    public static void getLoginCode(final OkHttpCallback callback) {
//        try {
//            HttpURLConnection conn = (HttpURLConnection) new URL(resourceURL).openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(8000);
//            conn.setReadTimeout(8000);
//            int code = conn.getResponseCode();
//            if (code == 200) {
//                return conn.getInputStream();
//            }
//        } catch (IOException e) {
//            return null;
//        }
//        return null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(GlobalValue.LOGIN_CODE_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Log.i(TAG, "getLoginCode中的onFailure中的线程:" + Thread.currentThread().getName());
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.i(TAG,"getLoginCode中的onResponse中的线程:"+Thread.currentThread().getName());
                Headers headers = response.headers();
                cookie = headers.get("Set-Cookie");
                callback.onResponse(call, response);
            }
        });
    }

}