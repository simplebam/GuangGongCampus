package com.example.yueyue.campusapp.utils;

/**
 * Created by yueyue on 2017/4/21.
 */

import android.os.Environment;

import java.io.File;

/**
 * 存放应用的全局变量
 */
public class GlobalValue {
    //http://222.200.98.147/xsgrkbcx!xsAllKbList.action?xnxqdm=201602

    /**
     * 校园APP更新安装包存储的位置
     */
    //Environment.getExternalStorageDirectory() + File.separator + "campusupdate.apk"
    public static final String UPDATE_APK_PATH = Environment.getExternalStorageDirectory()
            + File.separator + "campusupdate.apk";

    /**
     * 校园APP版本获得服务器版本的链接
     */
    public static final String UPDATE_APK_URL = "http://119.29.214.111:8080/campusupdate.json";


    /**
     * 是否更新APK-->这里我们默认先不要更新,节约时间,代码已经搞好的啦
     */
    public static final String ISUPDATE = "IS_UPDATE";

    /**
     * 新教务系统的基址地址
     */
    public static final String CAMPUS_BASE_URL = "http://222.200.98.147";


    /**
     * 获取登陆验证码地址
     */
    // http://222.200.98.147/yzm?d=1500715270845   ?d=1500715270845
    public static final String LOGIN_CODE_URL = CAMPUS_BASE_URL + "/yzm?d="+System.currentTimeMillis();

    /**
     * login登录界面的url地址
     */
    //"http://119.29.214.111:8080/Login/LoginServlet"
    // http://119.29.214.111:8080/Login/LoginServlet?username=abc&password=123
    public static final String LOGIN_BASE_URL = CAMPUS_BASE_URL + "/new/login";


//    final String CouseUrl = "http://222.200.98.147/xsgrkbcx!getKbRq.action?" +
//            "xnxqdm=" + GlobalValue.XNXQDM + "&zc=" + DateUtil.getcurrentWeek();

    /**
     * 查询某一周的课程表
     */
    public static final String COUSE_URL_ZC = CAMPUS_BASE_URL + "/xsgrkbcx!getKbRq.action?" +
            "xnxqdm=" + GlobalValue.XNXQDM + "&zc=";
    // + DateUtil.getcurrentWeek()


    /**
     * 查询全部的课程表
     */
    //http://222.200.98.147/xsgrkbcx!xsAllKbList.action?xnxqdm=201602
    public static final String COUSE_URL_ALL = CAMPUS_BASE_URL + "/xsgrkbcx!xsAllKbList.action?" +
            "xnxqdm=" + GlobalValue.XNXQDM;


    /**
     * 课程安排设计完成的时间
     * 查询课表时候链接里面的xnxqdm
     */
    public static final String XNXQDM = "201701";


    /**
     * 第一周的第一日的日期-->新学期开始的第一天
     */
    public static final String FIRST_WEEK_DAY_DATE = "2017-09-04";


    /**
     * 获取学生个人信息的链接-->get请求
     */
    public static final String PER_INFO_URL = CAMPUS_BASE_URL + "/xjkpxx!xjkpList.action";


    /**
     * 表示获得登陆成功的Cookie是否成功的key
     */
    public static final String COOKIE_OK = "cookie_OK";


    /**
     * 用户账号key--->右边一定不可以修改,因为很多类中还保留"account"写法
     */
    public static final String ACCOUNT = "account";


    /**
     * 用户密码key--->右边一定不可以修改,因为很多类中还保留"pwd"写法
     */
    public static final String PWD = "pwd";


    /**
     * cookie保存的key--->右边一定不可以修改,因为很多类中还保留"cookie"写法
     */
    public static final String COOKIE = "cookie";


    /**
     * 联系人电话网页请求链接
     */
    public static final String CONTACT_CALL_NET = "http://www2.gdut.edu.cn/newgdut/gdutdh.htm";

    /**
     * 必应搜索官网首页
     */
    public static final String BING_YING_BASE_URL = "http://cn.bing.com";

    /**
     * 访问必应搜索首页的json数据连接
     */
    public static final String BING_YING_SEARCH_JSON_URL = BING_YING_BASE_URL + "/HPImageArchive.aspx?format=js&idx=0&n=1";


    /**
     * 郭霖先生爬取必应每日一图提供的免费接口
     * 作为备用链接1-->返回的是抓取到的图片链接
     */
    public static final String BING_YING_SEARCH_PIC_URL_GL = "http://guolin.tech/api/bing_pic";

    /**
     * 缙哥哥爬取必应每日一图提供的免费接口
     * 作为备用链接2->返回的是抓取到的图片链接
     * 其博客:https://www.dujin.org/fenxiang/jiaocheng/3618.html
     */
    //1366*768分辨率图片地址:https://www.dujin.org/sys/bing/1366.php
    //1920*1080分辨率图片地址:https://www.dujin.org/sys/bing/1920.php
    public static final String BING_YING_SEARCH_JSON_JGG = "https://www.dujin.org/sys/bing/1366.php";


    /**
     * 保存必应每日一图的key
     */
    public static final String BING_YING_PIC_URL = "BING_YING_PIC_URL";


    /**
     * WheelActivity的返回数据的key
     */
    public static final String WHEEL_SELECT_CURR_WEEK = "WHEEL_SELECT_CURR_WEEK";


    /**
     * 获取学生成绩的链接-->get跟post请求都行
     */
    public static final String SCORE_URL = CAMPUS_BASE_URL + "/xskccjxx!getDataList.action";


}
