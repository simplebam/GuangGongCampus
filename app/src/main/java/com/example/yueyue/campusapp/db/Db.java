package com.example.yueyue.campusapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.yueyue.campusapp.models.Course;
import com.example.yueyue.campusapp.models.DetailsInfo;
import com.example.yueyue.campusapp.models.News;
import com.example.yueyue.campusapp.models.Score;
import com.example.yueyue.campusapp.utils.HandleResponseUtil;
import com.example.yueyue.campusapp.utils.HttpUtil;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by Administrator on 2015/10/20.
 */
public class Db {

    private static final String TAG = Db.class.getSimpleName();

    public static final String DB_NAME = "school_info.db";
    public static final int VERSION = 2;
    private static Db db;
    private SQLiteDatabase sqlDb;

    private Db(Context context) {
        DbOpenHelper dbHelper = new DbOpenHelper(context, DB_NAME, null, VERSION);
        sqlDb = dbHelper.getWritableDatabase();
    }

    public synchronized static Db getInstance(Context context) {
        if (db == null) {
            db = new Db(context);
        }
        return db;
    }

    public void saveNews(News news) {

        if (news != null) {
            ContentValues values = new ContentValues();
            values.put("path", news.getPath());
            values.put("title", news.getTitle());
            values.put("content", news.getContent());
            sqlDb.insert("News", null, values);
        }

    }


    /**
     * 根据学期代码查询成绩表
     *
     * @param semesterCode 学期代码
     * @return
     */
    public static boolean loadScore(int semesterCode) {

        boolean result = false;
        List<Score> courseList = DataSupport.where("account = ? and semesterCode=?",
                HttpUtil.account, semesterCode + "").find(Score.class);
        //Log.i(TAG,"从数据库中查询到的数据");
        //集合先清空数据再使用
        HandleResponseUtil.clearScoreList();

        if (courseList != null && courseList.size() > 0) {
//            HandleResponseUtil.scoreDatas = courseList;
            HandleResponseUtil.scoreDatas.addAll(courseList);
            Log.i(TAG, "loadScore中的从数据库中查询到的数据" + HandleResponseUtil.scoreDatas);
            result = true;
        }
        return result;
    }


    /**
     * 获取数据库中最新学期的成绩表
     *
     * @return
     */
    public static boolean loadScoreLast() {

        boolean result = false;
        int maxCode = DataSupport.max(Score.class, "semesterCode", int.class);

        if (maxCode > 0) {
            //集合先清空数据再使用
            HandleResponseUtil.clearScoreList();

            //说明有数据
            List<Score> courseList = DataSupport.where("account = ? and semesterCode=?",
                    HttpUtil.account, maxCode + "").find(Score.class);

            //Log.i(TAG,"loadScoreLast从数据库中查询到的数据"+courseList);

            if (courseList != null && courseList.size() > 0) {
                HandleResponseUtil.scoreDatas.addAll(courseList);
                // Log.i(TAG, "loadScore中的从数据库中查询到的数据hahahah" + HandleResponseUtil.scoreDatas);
                result = true;
            }
        }

        return result;
    }


    //分页查询固定一页查询10条
    public static int scorePageSize = 10;

    /**
     * 根据加载全部成绩表-->分页查询
     * @param pageNumber   第几页,页码从第0页开始
     * @return  每一页的数据
     */
    public static boolean loadScoreAll(int pageNumber) {  //ASC

        boolean result = false;
        List<Score> courseList = DataSupport.where("account = ?", HttpUtil.account)
                .order("semestercode asc")
                .limit(10).offset(pageNumber * scorePageSize)
                .find(Score.class);

        //集合先清空数据再使用
        if (pageNumber==0) {
            HandleResponseUtil.clearScoreList();
        }
        if (courseList != null && courseList.size() > 0) {
            HandleResponseUtil.scoreDatas.addAll(courseList);
           // Log.i(TAG, "loadScoreAll中的从数据库中查询到的数据" + HandleResponseUtil.scoreDatas);
            result = true;
        }
        return result;
    }

    /**
     * 判断db中的Score数据表是否有数据
     *
     * @return
     */
    public static boolean isScoreHaveData() {
        int result = DataSupport.where("account = ?", HttpUtil.account).count(Score.class);
        return result > 0;
    }
    /**
     * 判断db中的Score数据表的条目
     *
     * @return
     */
    public static int getScoreDataCount() {
        return DataSupport.where("account = ?", HttpUtil.account).count(Score.class);

    }

    /**
     * 根据周数查询课表
     *
     * @param week
     * @return
     */
    public static boolean loadCourse(int week) {
        boolean result = false;
        List<Course> courseList = DataSupport.where("account = ? and week=?",
                HttpUtil.account, week + "").find(Course.class);
        //Log.i(TAG,"从数据库中查询到的数据");
        //集合先清空数据再使用
        HandleResponseUtil.clearCourseListAll();

        if (courseList != null && courseList.size() > 0) {
            for (Course course : courseList) {
                HandleResponseUtil.addToList(course.weekDay, course);
            }
            result = true;
        }
        HandleResponseUtil.addToCourseData();
        return result;
    }

    /**
     * 判断db中的Course数据表是否有数据
     */
    public static boolean isCourseHaveData() {
        int result = DataSupport.where("account = ?", HttpUtil.account).count(Course.class);
        return result > 0;
    }


    /**
     * 获得当前数据库最大的周
     */
    public static int getDbCourseMaxWeek() {
        int maxWeek = 20;
        int result = DataSupport.max(Course.class, "week", int.class);
        if (result > 15) {
            maxWeek = result;
        }
        return maxWeek;
    }


    /**
     * 返回navigationView头布局所需的数据
     *
     * @return
     */
    public static DetailsInfo loadNvHeaderDetails() {
        //在detailsinfo数据表中查询
        DetailsInfo detailsInfo = DataSupport.select("name", "sex", "grade")
                .where("account=?", HttpUtil.account).findLast(DetailsInfo.class);
        return detailsInfo;
    }

    public void clear(String tableName) {
        sqlDb.execSQL("DELETE FROM " + tableName);
    }

}
