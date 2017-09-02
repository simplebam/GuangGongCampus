package com.example.yueyue.campusapp.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Score的bean类,LitePal框架提供了CRUD
 * Created by Administrator on 2015/10/21.
 */

public class Score extends DataSupport implements Comparable<Score> {

    @SerializedName("cjjd")
    public float point;//成绩绩点

    @SerializedName("kcdlmc")
    public String category;//课程大类

    @SerializedName("kcmc")
    public String courseName;//课程名称

    @SerializedName("xnxqdm")
    public Integer semesterCode;//学年学期代码

    @SerializedName("xf")
    public float credit;//学分

    @SerializedName("xnxqmc")
    public String semesterName;//学年学期名称

    @SerializedName("xsbh")
    public String account;//学生别号-->即是学生学号

    @SerializedName("zcj")
    public String actualScore;//总成绩-->实际得分


    @Override
    public String toString() {
        return "Score{" +
                "point=" + point +
                ", category='" + category + '\'' +
                ", courseName='" + courseName + '\'' +
                ", semesterCode='" + semesterCode + '\'' +
                ", credit=" + credit +
                ", semesterName='" + semesterName + '\'' +
                ", account='" + account + '\'' +
                ", actualScore='" + actualScore + '\'' +
                '}';
    }

    /**
     * 集合ScoreBean排序,主要根据学期代码-->课程名称长度-->课程名称
     *
     * @param o 比较对象
     * @return
     */
    @Override
    public int compareTo(@NonNull Score o) {
        int num = this.semesterCode - o.semesterCode;
        // num = num == 0 ? this.courseName.length() - o.courseName.length() : num;
        num = num == 0 ? this.courseName.compareTo(o.courseName) : num;
        return num;
    }
}

