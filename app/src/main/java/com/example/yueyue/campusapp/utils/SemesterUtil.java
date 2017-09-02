package com.example.yueyue.campusapp.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yueyue on 2017/6/4.
 */

public class SemesterUtil {


    private  static final String TAG=SemesterUtil.class.getSimpleName();
    /**
     * 由学年学期名称-->学年学期代码
     *
     * @param str 学年学期名称:形式如2016春季,2016秋季
     * @return
     */
    public static String name2Code(String str) {
        String result = null;
        //使用正则表达式进行检验先
        if (!TextUtils.isEmpty(str)) {
            //通配符验证规则 ^\d{4}[\u4e00-\u9fa5]{2}$
            String regEx = "^\\d{4}[\\u4e00-\\u9fa5]{2}$";//只适配前四位数字,后两位汉字
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regEx);
            // 忽略大小写的写法
            // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            // 字符串是否与正则表达式相匹配
            boolean rs = matcher.matches();
            if (rs) {
                //说明匹配成功,传入的是正确的
                int year = Integer.parseInt(str.substring(0, 4));//前四位是数字
                String season = str.substring(4, str.length());//春季或者秋季
                // TODO: 2017/6/4 这里到时应该加入年份判断一下的 但由于现在想测试一下web那边年份是2020会如何先
                if (season.contains("秋季")) {
                    result = year + "01";              // "201601"<---"2016秋季",
                } else if (season.contains("春季")) {
                    result = year - 1 + "02";             //"201502"<---"2016春季",
                }
//                if(year<=DateUtil.getYear()+1) {
//
//                }

            }
        }

        return result;
    }

    /**
     * 由学年学期代码-->学年学期名称
     *
     * @param str 学年学期代码:形式如201601,201602
     * @return
     */
    public static String code2Name(String str) {
        String result = null;
        //使用正则表达式进行检验先
        if (!TextUtils.isEmpty(str)) {
            //通配符验证规则  ^2\d{3}0[1-2]$  --匹配2[3位数字]0[1或者2]
            String regEx = "^2\\d{3}0[1-2]$";//只适配前四位数字,后两位汉字
            // 编译正则表达式
            Pattern pattern = Pattern.compile(regEx);
            // 忽略大小写的写法
            // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            // 字符串是否与正则表达式相匹配
            boolean rs = matcher.matches();
            if (rs) {
                //说明匹配成功,传入的是正确的
                int year = Integer.parseInt(str.substring(0, 4));//前四位是数字
                String season = str.substring(4, str.length());//春季或者秋季
                if (season.contains("01")) {
                    result = year + "秋季";              // "201601"<---"2016秋季",
                } else if (season.contains("02")) {
                    result = year + 1 + "春季";             //"201502"<---"2016春季",
                }

            }
        }

        return result;
    }

    /**
     * 判断学年学期代码是否超前了
     *
     * @param semesterCode
     * @return
     */
    public static boolean isOutOfRange(String semesterCode) {
        boolean result = false;
        int currCode = getCurrInSemester();
        result = currCode < Integer.parseInt(semesterCode) ? true : false;
        return result;
    }

    /**
     * 获得当前时间所在的学年学期
     *
     * @return  当前的学年学期代码
     */
    public static int getCurrInSemester() {
        int year = DateUtil.getYear();
        int month = DateUtil.getMonth();
        String currCode = "";
        if (month < 6) {
            currCode = year - 1 + "02";////"201502"<---"2016春季",
        } else {
            currCode = year + "01"; // "201601"<---"2016秋季",
        }

        //Log.i(TAG,Integer.parseInt(currCode)+"");
        return Integer.parseInt(currCode);
    }

}
