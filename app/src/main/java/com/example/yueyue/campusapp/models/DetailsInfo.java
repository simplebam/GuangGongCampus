package com.example.yueyue.campusapp.models;

import org.litepal.crud.DataSupport;

/**
 * 个人信息Bean类-->可能会更新,所以我们需要设置一个更新按钮
 * Created by yueyue on 2017/5/26.
 */

public class DetailsInfo extends DataSupport{
    //id作为自增长使用的
    public int id;
    //姓名
    public String name;
    //学号
    public String account;
    //入学年份
    public String enterYear;
    //院系名称
    public String faculty;
    //专业
    public String major;
    //所在班级
    public String grade;
    //性别
    public String sex;

    //所属校区
    public String address;

    //辅修专业
    public String minor;

    @Override
    public String toString() {
        return "DetailsInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", enterYear='" + enterYear + '\'' +
                ", faculty='" + faculty + '\'' +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", minor='" + minor + '\'' +
                '}';
    }
}
