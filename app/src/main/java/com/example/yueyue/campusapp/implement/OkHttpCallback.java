package com.example.yueyue.campusapp.implement;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yueyue on 2017/9/1.
 */

public interface OkHttpCallback {

    public void onFailure(Call call, IOException e);

    public void onResponse(Call call, Response response);

}
