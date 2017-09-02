package com.example.yueyue.campusapp.implement;

/**
 * 一般处理事务的回调函数
 * Created by Administrator on 2015/10/9.
 */
public interface CallBack {
    /**
     * 在开始的时候调用
     */
    public abstract  void onStart();

    /**
     * 在处理完成的时候调用
     * @param response  数据
     */
    public abstract   void onFinsh(String response);

}
