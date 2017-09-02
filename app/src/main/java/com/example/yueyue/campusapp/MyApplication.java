package com.example.yueyue.campusapp;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;
import org.xutils.x;

/**
 * 全局Applicanton
 * Created by yueyue on 2017/5/21.
 */

public class MyApplication extends Application {

    /**
     * 全局上下文
     */
    private static Context sContext;

    /**
     * 选择了当前的周
     */
    public static int selectWeek;


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils3
        x.Ext.init(this);
        // 是否输出debug日志, 开启debug会影响性能.
        x.Ext.setDebug(false);

        //初始化LitePal
        LitePal.initialize(this);

        //获取全局的上下文环境
        sContext = getApplicationContext();

        //计算周数
    }

    /**
     * 获得全局上下文
     *
     * @return app的全局上下文
     */
    public static Context getAppContext() {
        return sContext;
    }


}
