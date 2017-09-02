package com.example.yueyue.campusapp.models;

import org.litepal.crud.DataSupport;

/**
 * 学校办公电话查询Bean
 * Created by yueyue on 2017/5/27.
 */

public class ContactInfo extends DataSupport implements Comparable<ContactInfo> {

    //办公电话名称
    public String name;
    //办公电话号码
    public String call;
    //办公电话所在的校区
    public String campus;
    //属性-->机关,学院或者医院等
    public String property;
    //姓名的拼音
    public String pinyin;
    //姓名的拼音首字母
    public char firstChar;
    //这个URL到时去掉
    public String url;

    public ContactInfo() {
    }

    public ContactInfo(String name, String call, String campus, String property) {
        this.name = name;
        this.call = call;
        this.campus = campus;
        this.property = property;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
        String first = pinyin.substring(0, 1);
        if (first.matches("[A-Za-z]")) {
            firstChar = first.toUpperCase().charAt(0);
        } else {
            firstChar = '#';
        }
    }


    @Override
    public int compareTo(ContactInfo another) {
        return this.pinyin.compareTo(another.pinyin);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ContactInfo) {
            return this.name == ((ContactInfo) o).name;
        } else {
            return super.equals(o);
        }
    }
}
