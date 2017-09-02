package com.example.yueyue.campusapp.implement;

/**
 * 处理数据的回调接口
 * Created by yueyue on 2017/5/26.
 */

public interface DataCallback {
    /**
     * 在开始的时候调用
     */
    public abstract void onStart();

    /**
     * 处理失败的时候调用
     */
    public abstract void onFail();

    /**
     * 在抛异常的时候调用
     * @param e 发生的异常
     */
    public abstract void onError(Exception e);

    /**
     * 在数据处理成功的时候调用
     */
    public abstract void onFinshed();
}
