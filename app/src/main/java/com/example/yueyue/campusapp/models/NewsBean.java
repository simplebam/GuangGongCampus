package com.example.yueyue.campusapp.models;

/**
 * Created by yueyue on 2017/9/1.
 */

public class NewsBean {

    public String name;
    public int id;

    public NewsBean(String name, int id) {
        this.name = name;
        this.id = id;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
