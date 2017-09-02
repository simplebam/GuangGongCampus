package com.example.yueyue.campusapp.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 过时的数据库,使用LitePal更新数据库
 * Created by Administrator on 2015/10/20.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private Context mcontext=null;

    public static final String CREATE_NEWS ="create table News ("
            + "id integer primary key autoincrement,"
            + "path text,"
            + "title text,"
            + "content text)";

    public static final String CREATE_COURSE ="create table Course ("
            + "id integer primary key autoincrement,"
            + "Count integer,"
            + "CourseName text,"
            + "Number text,"
            + "Week text,"
            + "Teacher text,"
            + "Classroom text,"
            + "Category text,"
            + "Time text)";

    public static final String CREATE_SCORE ="create table Score ("
            + "id integer primary key autoincrement,"
            + "ScoreName text,"
            + "point text,"
            + "testScore text,"
            + "type text,"
            + "examScore text,"
            + "credit text)";

    public static final String CREATE_PHONE ="create table Phone ("
            + "id integer primary key autoincrement,"
            + "schoolNumber text,"
            + "peopleName text,"
            + "phoneNumber text,"
            + "className text)";

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_COURSE);
        db.execSQL(CREATE_SCORE);
        db.execSQL(CREATE_PHONE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_PHONE);
    }

    public static final String STATUS_TABLE="Status";
    public static final String STATUS_CONTETT="content";
    public static final String STATUS_USER="user";
    public static final String STATUS_COUNT="count";

    public static final String COMMENT_TABLE="Comment";
    public static final String COMMENT_CONTETT="content";
    public static final String COMMENT_SENDUSER="send_user";
    public static final String COMMENT_RECEIVE_USER="receive_user";
    public static final String COMMENT_STATUS = "status";
}
