package com.example.yueyue.campusapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yueyue on 2017/5/29.
 */

public class BingYingPic {

    @SerializedName("images")
    public List<BaseBingPic> images;

    public class BaseBingPic {
        @SerializedName("url")
        public String bingBasePicUrl;

        @SerializedName("enddate")
        public String endDate;

        @SerializedName("startdate")
        public String startDate;

        @Override
        public String toString() {
            return "BaseBingPic{" +
                    "bingBasePicUrl='" + bingBasePicUrl + '\'' +
                    ", endDate='" + endDate + '\'' +
                    ", startDate='" + startDate + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BingYingPic{" +
                "images=" + images +
                '}';
    }
}
