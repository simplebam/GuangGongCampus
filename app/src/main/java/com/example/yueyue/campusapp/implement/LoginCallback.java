package com.example.yueyue.campusapp.implement;

/**
 * 登陆时候回掉的接口
 * Created by yueyue on 2017/5/22.
 */

public interface LoginCallback {
    /**
     * 在开始之前调用
     */
    void beforeStart();

    /**
     * 在登陆完成的时候调用
     */
    void onLoginFinshed();

    /**
     * 在抛异常的时候调用
     * @param e
     */
    void onError(Exception e);

}