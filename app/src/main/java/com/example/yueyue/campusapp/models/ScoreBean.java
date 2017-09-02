package com.example.yueyue.campusapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Score的bean类,LitePal框架提供了CRUD
 * Created by yueyue on 2017/5/24.
 */

public class ScoreBean {


    @SerializedName("total")
    public int total;

    public List<Score> rows;

    @Override
    public String toString() {
        return "ScoreBean{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}