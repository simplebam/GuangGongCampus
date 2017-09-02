package com.example.yueyue.campusapp.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * Course表的字段,LitePal框架提供了CRUD
 * Created by yueyue on 2017/5/24.
 */

public class Course extends DataSupport implements Cloneable {
    //    public int id; //课程ID
//    public String courseName;//课程名称
//    public int section; //从第几节课开始
//    public int sectionSpan; //跨几节课
//    public int week; //周几
//    public String classRoom; //教室
//    public int courseFlag; //课程背景颜色

    // models_1.add(new CourseModel(1, "PHP", 6, 3, 1, "A483",
    // (int) (Math.random() * 10)));


    //学生学号-->作为一种凭证
    public String account;
    //课程名称
    @SerializedName("kcmc")
    public String courseName;//courseName;//课程名称
    //周次(第几周)
    @SerializedName("zc")
    public int week;
    //该课程上的全部周次
    @SerializedName("zcs")
    public String weekAll;
    //任课老师
    @SerializedName("teaxms")
    public String teacher;
    //教师
    @SerializedName("jxcdmc")
    public String classRoom;//classRoom; //教室-->单周课程的json是这个的

    @SerializedName("jxcdmcs")
    public String classRooms;//classRoom; //教室-->全部课程的json中是这个的

    //上课时间 1-2还是3-4
    @SerializedName("jcdm2")
    public String sectionTime;

    //jxcdmcs

    //星期几
    @SerializedName("xq")
    public int weekDay;//week; //周几


    //从第几节课开始-->section; //从第几节课开始
    public int sectionStart;//下载数据时候需要好好切割存储
    //跨几节课
    public int sectionSpan; //下载到数据的时候好好切割存储


    @Override
    public String toString() {
        return "Course{" +
                "account='" + account + '\'' +
                ", courseName='" + courseName + '\'' +
                ", week=" + week +
                ", weekAll='" + weekAll + '\'' +
                ", teacher='" + teacher + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", classRooms='" + classRooms + '\'' +
                ", sectionTime='" + sectionTime + '\'' +
                ", weekDay=" + weekDay +
                ", sectionStart=" + sectionStart +
                ", sectionSpan=" + sectionSpan +
                '}';
    }

    /**
     * 浅复制
     * @return  新对象
     */
    @Override
    public Object clone() {
        Course course = null;
        try{
            course = (Course)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return course;
    }

    /**
     * 给数值进行排序
     *
     * @param o
     * @return
     */
    public int compareTo(@NonNull Course o) {
        String[] thisSections = separateStr(this.sectionTime);
        String[] oSections = separateStr(o.sectionTime);
        int i = 0;
        if (thisSections.length > 0 && oSections.length > 0) {
            //先按照第一个数字来排序
            i = Integer.parseInt(thisSections[0]) - Integer.parseInt(oSections[0]);
            //如果第一个数字相等再按照后面的那个数字进行排序
            return i != 0 ? i : (Integer.parseInt(thisSections[1]) -
                    Integer.parseInt(oSections[1]));
//            if(i == 0){
//                //如果第一个数字相等再按照后面的那个数字进行排序
//                return Integer.parseInt(thisSections[1]) - Integer.parseInt(oSections[1]);
//            }
//            return i;
        }
        return i;
    }

    /**
     * 1-2,10-12
     * 切割字符串-->这里是为了sectionTime准备的
     *
     * @param separationStr
     */
    private String[] separateStr(String separationStr) {
        if (separationStr.contains("-")) {
            String[] splits = separationStr.split("-");
            return splits;
        }
        return new String[]{};
    }
}