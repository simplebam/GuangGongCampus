package com.example.yueyue.campusapp.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * Created by yueyue on 2017/5/25.
 */

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();
    /**
     * 计算当前的周数-使用地方
     * 1.获取当前周课程表那里
     * @return
     */
    public static int getcurrentWeek() {
        int finalWeeks = -1;
        try {
            // 获得今天时间的 的Date对象-年月日
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            String s1 = df1.format(new Date());
            System.out.println(s1);// 2017-05-25
            Date currDate = df1.parse(s1);

            System.out.println(currDate);// Thu May 25 00:00:00 CST 2017

            // 获得初始时间的Date对象-年月日
            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = df2.parse(GlobalValue.FIRST_WEEK_DAY_DATE);
            System.out.println(beginDate);// Mon Feb 20 00:00:00 CST 2017

            // Date myDate3= df2.parse("2017-05-28");

            //得到初始时间的的ms
            long beginTime = beginDate.getTime();
            //得到当前时间的ms
            long currTime = currDate.getTime();
            //获得未处理的商值
            double initWeeks = ((double) (currTime - beginTime)) / (1000 * 60 * 60 * 24 * 7);
            finalWeeks= (int)initWeeks+1;
            System.out.println("最后的值finalWeeks:" + finalWeeks);// 商是14.0

        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(TAG,"单位换算有问题,应该回来检查一下");
            return -1;
        }
        return finalWeeks;
    }


    /**
     * 获得当前星期几
     */
    public static int getWeekDay() {
        //Calendar.DAY_OF_WEEK:1 = Monday, ..., 7 = Sunday
        //Calendar.Sunday:1, ..., SATURDAY = 7
        //Calendar.getInstance中的get(index)中的index是返回年月日等-->自查源码了解
        int w = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (w <= 0) {
            w = 7;//这里的意思是星期日
        }
//        System.out.println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));//返回的是星期几+1
//        boolean isFirstSunday = (Calendar.getInstance().getFirstDayOfWeek() == Calendar.SUNDAY);
//        System.out.println(isFirstSunday);//true
//        System.out.println(Calendar.getInstance().getFirstDayOfWeek());//1
//        System.out.println(Calendar.SUNDAY);//1
//        System.out.println(Calendar.MONDAY);//2
//        System.out.println(Calendar.SATURDAY);//7

        return w;
    }

    /**
     * 获取的当前年份
     * @return
     */
    public static int getYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 当前月份
     */
    public static int getMonth() {
        int w = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return w;
    }

    /**
     * 获取当前月的当前日
     * @return
     */
    public static int getDayInMonth(){
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 数字转换中文
     */
    public static String intToZH(int i) {
        String[] zh = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};

        String str = "";
        StringBuffer sb = new StringBuffer(String.valueOf(i));
        sb = sb.reverse();//倒叙输出
        int r = 0;
        int l = 0;
        for (int j = 0; j < sb.length(); j++) {
            r = Integer.valueOf(sb.substring(j, j + 1));
            if (j != 0)
                l = Integer.valueOf(sb.substring(j - 1, j));
            if (j == 0) {
                if (r != 0 || sb.length() == 1)
                    str = zh[r];
                continue;
            }
            if (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 9) {
                if (r != 0)
                    str = zh[r] + unit[j] + str;
                else if (l != 0)
                    str = zh[r] + str;
                continue;
            }
            if (j == 4 || j == 8) {
                str = unit[j] + str;
                if ((l != 0 && r == 0) || r != 0)
                    str = zh[r] + str;
                continue;
            }
        }
        if (str.equals("七"))
            str = "日";
        return str;
    }

    /**
     * 根据星期几选定课程表
     */
    public static void whatDayIs() {
        Calendar calendar = Calendar.getInstance();
        int date = 0;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                date = 0;
                break;
            case Calendar.TUESDAY:
                date = 1;
                break;
            case Calendar.WEDNESDAY:
                date = 2;
                break;
            case Calendar.THURSDAY:
                date = 3;
                break;
            case Calendar.FRIDAY:
                date = 4;
                break;
            case Calendar.SATURDAY:
                date = 5;
                break;
            case Calendar.SUNDAY:
                date = 6;
                break;
        }
//        pager.setCurrentItem(date);
    }

}
