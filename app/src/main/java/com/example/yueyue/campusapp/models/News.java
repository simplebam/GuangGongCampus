package com.example.yueyue.campusapp.models;

/**
 * Created by Administrator on 2015/10/8.
 */
public class News  {

    private String path="";
    private String title="";
    private String content="";
    private int Id;

    public  static String INDEX="http://www.kyren.net";

    public  static String NEWS_INDEX="http://www.kyren.net/Category_17/Index.aspx";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
