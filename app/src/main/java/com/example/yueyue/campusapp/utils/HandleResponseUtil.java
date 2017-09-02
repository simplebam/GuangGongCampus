package com.example.yueyue.campusapp.utils;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.yueyue.campusapp.models.Course;
import com.example.yueyue.campusapp.models.DetailsInfo;
import com.example.yueyue.campusapp.models.News;
import com.example.yueyue.campusapp.models.Score;
import com.example.yueyue.campusapp.db.Db;
import com.example.yueyue.campusapp.implement.CallBack;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/10/20.
 */
public class HandleResponseUtil {
    private static final String TAG = HandleResponseUtil.class.getSimpleName();

    public static Db db = null;

    //解析新闻标题
    public static void parseTitleData(String response) {
//
//        if (response!=null&&!"".equals(response)) {
//            Document doc = Jsoup.parse(response);
//            Elements linksElements = doc.select("div[class=article_list mtop10]>ul>li>a");
//            // class为“article_list mtop10”的div里面ul里面li里面a标签
//            for (Element ele:linksElements) {
//                String href = ele.attr("href");
//                String title = ele.text();
//                News news =new News();
//                news.setPath(href);
//                news.setTitle(title);
//                HttpUtil.datamap.put(news.getPath(), news);
//            }
//            NewsFragment.adapter=new NewsAdapter(new ArrayList<News>(HttpUtil.datamap.values()), NewsAdapter.context);
//            NewsFragment.recyclerView.setAdapter(NewsFragment.adapter);
//            NewsFragment.adapter.notifyDataSetChanged();
//        }
    }

    //解析新闻内容
    public static void parseContentData(String response) {

//        if (response!=null&&!"".equals(response)) {
//            Document document = Jsoup.parse(response);
//            Elements pElements  = document.select("p");
//
//            StringBuilder sb = new StringBuilder();
//            for (Element e : pElements) {
//                String str = e.text();
//                sb.append(str + "\n");
//            }
//            HttpUtil.datamap.get(HttpUtil.path).setContent(sb.toString());
//        }
    }

    //解析webView使用的Html内容
    public static void handleNewsHtmlStr(String htmlStr, final CallBack callback) {

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {

                if (db == null)
                    callback.onStart();

                String result = "";
                try {
                    Document document = Jsoup.parse(params[0]);
                    Elements pElements = document.select("div[class=article_content]");

                    Elements pngs = document.select("img[src]");
                    for (Element element : pngs) {
                        String imgUrl = element.attr("src");
                        if (imgUrl.trim().startsWith("/")) {
                            imgUrl = News.INDEX + imgUrl;
                            element.attr("src", imgUrl);
                        }
                    }

                    Elements iElements = null;
                    StringBuilder sb = new StringBuilder();
                    for (Element e : pElements) {
                        String str = e.html();
                        sb.append(str + "\n");
                    }
                    result = sb.toString();

                } catch (Exception e) {
                    result = "";
                    Log.d("winson", "解析错误： " + e);
                }

                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callback.onFinsh(s);
            }
        }.execute(htmlStr);
    }


    /**
     * 处理全部课程的html代码-->教务系统查看全部课程是嵌入html中,需要解析
     *
     * @param htmlStr      查看全部课程的html代码
     * @param dataCallback 回掉函数
     */
    public static void handleCourseAllHtmlStr(String htmlStr, DataCallback dataCallback) {
        Elements titleEle = Jsoup.parse(htmlStr).getElementsByTag("title");
        String titleContent = titleEle.get(0).text();
        Log.i(TAG, "hanldeCourseAllHtmlStr元素:" + titleContent);
        if (titleContent.contains("学生个人学期课表")) {
            //etElementsByTag（“script”）
            Document document = Jsoup.parse(titleContent);
            Elements pElements = document.select("div[class=article_content]");

            Elements elements = Jsoup.parse(htmlStr).getElementsByTag("script");
            //String str = "(?<=<script\\s*type=\"text\\/javascript\">)[^<]*(?=<\\/script>)";
            Element scriptElement = elements.get(elements.size() - 1);
            String[] split = scriptElement.toString().split("var kbxx = ");
            int pos = split[1].indexOf("}];");
            String jsonSourceCourse = split[1].substring(0, pos) + "}]";
            Log.i(TAG, "hanldeCourseAllHtmlStr元素111:" + jsonSourceCourse);
//
//            for (org.jsoup.nodes.Element element : elements) {
//                Log.i(TAG, "hanldeCourseAllHtmlStr元素:" + element);
//                Log.i(TAG, "=================+++++++++++++++++-----------------");
//            }

            //处理数据
            handleCourseAllJsonStr(jsonSourceCourse, dataCallback);
        } else {
            if (dataCallback != null) {
                dataCallback.onError(new RuntimeException("handleCourseAllHtmlStr处理数据" +
                        "出错,标题对不上号"));
            }
        }


    }

    /**
     * 处理全部课程返回的json数据-->教务系统查看当前周的数据是以json数组返回
     *
     * @param jsonStr
     * @param dataCallback
     */
    private static void handleCourseAllJsonStr(String jsonStr, DataCallback dataCallback) {
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length() - 1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Course course = new Gson().fromJson(jsonObject.toString(), Course.class);

                //Log.i(TAG, "hanldeCourseAllHtmlStr:---" + i + "+++++++++++++" + course);


                handleCourseTime(course.sectionTime, course);
                course.account = HttpUtil.account;
                course.classRoom = course.classRooms;

                //删除数据库中的该数据再存储
                if (i == 0) {
                    DataSupport.deleteAll(Course.class, "account = ?", HttpUtil.account);
                }

                if (!TextUtils.isEmpty(course.weekAll)) {

                    String[] splitStrs = course.weekAll.split(",");
                    for (int j = 0; j < splitStrs.length; j++) {
                        Course cloneCourse = (Course) course.clone();
                        //java.lang.ArrayIndexOutOfBoundsException: length=1; index=5
                        cloneCourse.week = Integer.parseInt(splitStrs[j]);
                        // Log.i(TAG, "hanldeCourseAllHtmlStr:---cloneCourse:" + cloneCourse);
                        cloneCourse.save();//存储
                    }

                }

//                //使用LitePal将数据保存到course数据库中
//                if (i==0) {
//                    course.save();
//                }
                //完成时候回掉一下
                if (dataCallback != null) {
                    dataCallback.onFinshed();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (dataCallback != null) {
                dataCallback.onError(new RuntimeException("handleCourseAllJsonStr中解析出现异常"));
            }
        }
    }

    //一周课程的总集合
    public static ArrayList<List<Course>> courseDatas = new ArrayList<>();
    public static List<Course> list1 = new ArrayList<>();//星期一上课的课程
    public static List<Course> list2 = new ArrayList<>();//星期二上课的课程
    public static List<Course> list3 = new ArrayList<>();//星期三上课的课程
    public static List<Course> list4 = new ArrayList<>();//星期四上课的课程
    public static List<Course> list5 = new ArrayList<>();//星期五上课的课程
    public static List<Course> list6 = new ArrayList<>();//星期六上课的课程
    public static List<Course> list7 = new ArrayList<>();//星期日上课的课程

    /**
     * 处理一周返回的数据-->教务系统查看当前周的数据是以json数据返回
     *
     * @param jsonStr      jsonString的字符串
     * @param dataCallback 回调函数
     */
    public static void handleCourseJsonStr(final String jsonStr, final DataCallback dataCallback) {

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {

                //使用集合之前全部清空
                clearCourseListAll();

                if (!TextUtils.isEmpty(jsonStr)) {
                    try {
                        //Json数据中有两个不同类型的数组,所以我们需要分开进行解析
                        JSONArray jsonArrTotal = new JSONArray(jsonStr);
                        JSONArray jsonArrayGroup1 = jsonArrTotal.getJSONArray(0);
                        //JSONArray jsonArrayGroup2 = jsonArrTotal.getJSONArray(1);
                        //                      Log.i(TAG, "Gson解析的数据jsonArrTotal:" + jsonArrTotal.toString());
                        //                      Log.i(TAG, "Gson解析的数据jsonObject10:" + jsonArrayGroup1.toString());
                        //Log.i(TAG, "Gson解析的数据jsonObject11:" + jsonArrayGroup2.toString());

                        for (int i = 0; i < jsonArrayGroup1.length(); i++) {
                            JSONObject jsonObject = jsonArrayGroup1.getJSONObject(i);
                            Course course = new Gson().fromJson(jsonObject.toString(), Course.class);
                            course.account = HttpUtil.account;
                            //处理课程时间
                            handleCourseTime(course.sectionTime, course);

                            //根据account跟week字段来删除那一周存储在数据库的数据再存储
                            if (i == 0) {
                                DataSupport.deleteAll(Course.class, "account = ? and week = ?",
                                        HttpUtil.account, course.week + "");
                            }

                            int result = DataSupport.where("account = ? and week = ? " +
                                            "and weekDay=? and sectionStart=?",
                                    HttpUtil.account, course.week + "",
                                    course.weekDay + "", course.sectionStart + "").count(Course.class);
                            if (result <= 0) {
                                course.save();
                            } else {
                                Course udateCourse = new Course();
                                udateCourse.sectionSpan = course.sectionSpan;
                                udateCourse.teacher = course.teacher;
                                udateCourse.updateAll("account = ? and week = ? " +
                                                "and weekDay=? and sectionStart=?",
                                        HttpUtil.account, course.week + "",
                                        course.weekDay + "", course.sectionStart + "");
                            }

                            //使用LitePal将数据保存到course数据库中
//                            Log.i(TAG,"handleCourseJsonStr数据存储进入之前:"+course);
//                            if(i==0) {
//                                course.save();
//                            }
                            //根据星期把课程添加到对应的集合中
                            addToList(course.weekDay, course);

                        }


                        Log.i(TAG, "=====================================================");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "handleCourseJsonStr解析错误： " + e);
                        if (dataCallback != null) {
                            dataCallback.onFail();
                        }
                    }

                    //将list1-7的数据添加到courseData中
                    addToCourseData();

                    if (dataCallback != null) {
                        dataCallback.onFinshed();
                    }
                }


                return null;
            }


        }.execute();

    }

    /**
     * 将list1-7的数据添加到courseData中
     */
    public static void addToCourseData() {
        if (courseDatas.size() == 0) {

            //先对list1-7进行排序先,list集合的Course课程的排序要求已经写好
//            Collections.sort(list1);
//            Collections.sort(list2);
//            Collections.sort(list3);
//            Collections.sort(list4);
//            Collections.sort(list5);
//            Collections.sort(list6);
//            Collections.sort(list7);

            courseDatas.add(list1);
            courseDatas.add(list2);
            courseDatas.add(list3);
            courseDatas.add(list4);
            courseDatas.add(list5);
            courseDatas.add(list6);
            courseDatas.add(list7);
        }

    }

    /**
     * 清空某一个集合的数据
     */
    public static void clearList(List list) {
        if (list.size() > 0) {
            list.clear();
        }
    }

    /**
     * 清空课程中集合存储的所有数据
     */
    public static void clearCourseListAll() {
        if (list1.size() > 0) {
            list1.clear();
        }
        if (list2.size() > 0) {
            list2.clear();
        }
        if (list3.size() > 0) {
            list3.clear();
        }
        if (list4.size() > 0) {
            list4.clear();
        }
        if (list5.size() > 0) {
            list5.clear();
        }
        if (list6.size() > 0) {
            list6.clear();
        }
        if (list7.size() > 0) {
            list6.clear();
        }
        if (courseDatas.size() > 0) {
            courseDatas.clear();
        }

    }


    /**
     * 根据星期把课程添加到对应的集合中
     *
     * @param weekDay 星期几
     * @param course  课程
     */
    public static void addToList(int weekDay, Course course) {
        switch (weekDay) {
            case 1:
                list1.add(course);
                break;
            case 2:
                list2.add(course);
                break;
            case 3:
                list3.add(course);
                break;
            case 4:
                list4.add(course);
                break;
            case 5:
                list5.add(course);
                break;
            case 6:
                list6.add(course);
                break;
            case 0:
                list7.add(course);
                break;
        }
    }


    /**
     * //获得上课时间
     *
     * @param sectionTime 节点数
     * @param course      课程
     */
    private static void handleCourseTime(String sectionTime, Course course) {
        if (sectionTime.length() == 5) {
            //01,02
            switch (sectionTime) {
                case "01,02":
                    course.sectionStart = 1;
                    course.sectionSpan = 2;
                    break;
                case "03,04":
                    course.sectionStart = 3;
                    course.sectionSpan = 2;
                    break;
                case "05,06":
                    course.sectionStart = 5;
                    course.sectionSpan = 2;
                    break;
                case "06,07":
                    course.sectionStart = 6;
                    course.sectionSpan = 2;
                    break;
                case "07,08":
                    course.sectionStart = 7;
                    course.sectionSpan = 2;
                    break;
                case "08,09":
                    course.sectionStart = 8;
                    course.sectionSpan = 2;
                    break;
                case "09,10":
                    course.sectionStart = 9;
                    course.sectionSpan = 2;
                    break;
                case "10,11":
                    course.sectionStart = 10;
                    course.sectionSpan = 2;
                    break;
                case "11,12":
                    course.sectionStart = 11;
                    course.sectionSpan = 2;
                    break;
                default:
                    course.sectionStart = 0;
                    course.sectionSpan = 0;
                    break;
            }
        } else if (sectionTime.length() > 5) {
            switch (sectionTime) {
                case "01,02,03":
                    course.sectionStart = 1;
                    course.sectionSpan = 3;
                    break;
                case "01,02,03,04":
                    course.sectionStart = 1;
                    course.sectionSpan = 4;
                    break;
                case "05,06,07":
                    course.sectionStart = 5;
                    course.sectionSpan = 3;
                    break;
                case "06,07,08,09":
                    course.sectionStart = 6;
                    course.sectionSpan = 4;
                    break;
                case "10,11,12":
                    course.sectionStart = 10;
                    course.sectionSpan = 3;
                    break;
                default:
                    course.sectionStart = 0;
                    course.sectionSpan = 0;
                    break;
            }
        }

    }


    /**
     * 解析学生基本信息
     *
     * @param detailsHtmlStr 学生个人信息的网页
     * @param dataCallback   回掉函数
     */
    public static void handleDetailsHtmlStr(String detailsHtmlStr,
                                            DataCallback dataCallback) {
        Log.d(TAG, "解析html中...");
        Document doc = Jsoup.parse(detailsHtmlStr);
        Elements tileElem = doc.getElementsByTag("title");
        String title = tileElem.text();
        Log.d(TAG, "解析得到的标题title:" + title);
        if (!"学生基本信息".equals(title)) {
            //如果出现的网页不是他自己,那么说明还没有登陆成功
            if (dataCallback != null) {
                dataCallback.onError(new RuntimeException("handlePerInfoHtmlStr中的" +
                        "dataCallback为空!!!"));
//                Log.i(TAG, "handleDetailsHtmlStr解析不到的时候,当前的线程:" + Thread.currentThread().getName());
            }
        } else {
           // Log.i(TAG, "handleDetailsHtmlStr解析到的时候,当前的线程:" + Thread.currentThread().getName());
            //说明获取学生基本信息html源代码成功,获取目标HTML代码
            Elements elements1 = doc.select("[class=easyui-panel][id=\"p\"]");
            //Log.d(TAG, "解析得到的标题elements1:"+elements1);
            String elements1Title = elements1.attr("title");
            //Log.d(TAG, "解析得到的标题elements1Title:"+elements1Title);


            Elements trs = elements1.select("table").select("tr");
            //    Log.d(TAG, "解析得到的标题elements2:" + trs);
            //    Log.d(TAG, "解析得到的标题elements2中的size:" + trs.size());
            //根据<tr>标签切割字符串,最后一个信息是没用的,理应抛弃
            String[] splitTrs = trs.toString().split("</tr>", 5);
            //第一个切割好像是"",应当注意
            Log.d(TAG, splitTrs.length + "=====++++++++=========");
            for (int i = 0; i < splitTrs.length; i++) {
                //Log.d(TAG, "===========================开始................");
                splitTrs[i] = "<table>" + splitTrs[i] + "</tr>" + "</table>";
                //Log.d(TAG, "str:" + strTrs);
                //Log.d(TAG, "===========================结束");
            }

            //用于保存学生个人信息
            HashMap<String, String> stuMap = new HashMap<>();
            //用于保存学生信息中的key
            List<String> stuKeyList = new ArrayList<>();
            //主要作为解析过程中的行数
            int currPos = 0;
            //判断双数是否为key,默认是双数是key,但遇到空行就需要变换一次
            boolean isEvenKey = true;
            //根据<tr>标签切割字符串,最后一个信息是没用的,理应抛弃
            //System.out.println("+++++++开始+++++++");
            for (int i = 0; i < splitTrs.length - 1; i++) {
                Document doc1 = Jsoup.parse(splitTrs[i]);
                Elements trs1 = doc1.select("table").select("tr");
                for (int j = 0; j < trs1.size(); j++) {
                    Elements tds = trs1.get(j).select("td");
                    for (int k = 0; k < tds.size(); k++) {
                        String text = tds.get(k).text();
                       // System.out.println(text);//这里应该判断一下
                        //|| " ".equals(text)
                        if (TextUtils.isEmpty(text) || text.contains(" ")) {
                            //如果是null,""以及" "都应该不要(包括一个空格,两个空格以及三个空格)
                            currPos++;
                            isEvenKey = !isEvenKey;
                        } else {
                            //大学城校区、龙洞校区、东风路校区、番禺校区、沙河校区。
                            if (text.contains("龙洞")) {
                                stuMap.put("所属校区", "龙洞校区");
                            } else if (text.contains("大学城")) {
                                stuMap.put("所属校区", "大学城校区");
                            } else if (text.contains("东风路")) {
                                stuMap.put("所属校区", "东风路校区");
                            } else if (text.contains("番禺")) {
                                stuMap.put("所属校区", "番禺校区");
                            } else if (text.contains("沙河")) {
                                stuMap.put("所属校区", "沙河校区");
                            }

                            if (isEvenKey) {
                                //如果双速是key的话
                                if (currPos % 2 == 0) {
                                    stuKeyList.add(text);
                                    //   Log.d(TAG, "双数换行的时候key:" + text);
                                } else {
                                    String keyPos = stuKeyList.get(stuKeyList.size() - 1);
                                    stuMap.put(keyPos, text);
                                    //   Log.d(TAG, "双数换行的时候value:" + text);
                                }

                            } else {
                                //如果单数是key的话
                                if (currPos % 2 != 0) {
                                    stuKeyList.add(text);
                                    //  Log.d(TAG, "单数换行的时候key:" + text);
                                } else {
                                    String keyPos = stuKeyList.get(stuKeyList.size() - 1);
                                    stuMap.put(keyPos, text);
                                    //  Log.d(TAG, "单数换行的时候value:" + text);
                                    //  Log.d(TAG, "+++++++++++++++++++++++++++++");
                                }
                            }
                            currPos++;
                        }

                    }
                }
            }

//            System.out.println("-----------------结束" + stuMap.toString());
            SaveDetailsToTable(stuMap, dataCallback);
        }


    }

    /**
     * 把stuMap中的
     *
     * @param stuMap       学生的信息类
     * @param dataCallback 回调函数
     */
    private static void SaveDetailsToTable(HashMap<String, String> stuMap,
                                           DataCallback dataCallback) {
        DetailsInfo detailsInfo = new DetailsInfo();

        if (stuMap != null) {
            //使用迭代器，获取key
            Iterator<Map.Entry<String, String>> iterator = stuMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                //Log.i(TAG, "iterator走了............");

                //{姓名：=simple, 在读=学籍状态：, 大学城校区=学生状态：, 入学年份：=2015,
                // 所在年级：=2015, 有学籍= , 专业：=信息工程,
                // 班级：=信息工程15(1), 院系名称：=信息工程学院,  =所在校区：,
                // 外语语种：=无, 辅修专业：=计算机科学与技术, 学号：=3215009999}
                if (key.contains("姓名")) {
                    detailsInfo.name = entry.getValue();
                    HttpUtil.name = detailsInfo.name;//保存名字到HttpUtil那里去
                  //  Log.i(TAG, "detailsInfo.name:" + detailsInfo.name);
                } else if (key.contains("入学")) {
                    detailsInfo.enterYear = entry.getValue();
                } else if (key.contains("班级")) {
                    detailsInfo.grade = entry.getValue();
                } else if (key.contains("专业") && !key.contains("辅修")) {
                    detailsInfo.major = entry.getValue();
                } else if (key.contains("院系")) {
                    detailsInfo.faculty = entry.getValue();
                } else if (key.contains("所属校区")) {
                    detailsInfo.address = entry.getValue();
                } else if (key.contains("辅修")) {
                    String value = entry.getValue();
                   // Log.i(TAG, "detailsInfo.faculty:" + value);
                    if (TextUtils.isEmpty(value) || value.contains("null")) {
                        value = "无";
                    }
                    detailsInfo.minor = value;

                } else if (key.contains("学号")) {
                    detailsInfo.account = entry.getValue();
                  //  Log.i(TAG, "detailsInfo.account:" + detailsInfo.account);
                    //确定性别
                    confirmSex(detailsInfo);
                }

            }

            DataSupport.deleteAll(DetailsInfo.class, "account = ? ", detailsInfo.account);
            detailsInfo.save();
            // DataSupport.deleteAll()

//            boolean isSaveToDb = true;
//            //查询一条即可,减少时间
//            String sql = "select * from detailsinfo where account = ? limit 1";
//            Cursor cursor = DataSupport.findBySQL(sql, detailsInfo.account);
//            if (cursor.moveToNext()) {
//                //可以移动说明有数据,更新数据库
//                detailsInfo.updateAll("account = ?", detailsInfo.account);
//                Log.i(TAG, "updateAll走了......");
//            } else {
//                detailsInfo.save();
//                Log.i(TAG, "保存方法走了......");
//            }
            //Log.i(TAG, "在HandleResponseUtil类中的SaveStuInfoToTable:" + detailsInfo);
            //处理成功之后回掉此方法
            if (dataCallback != null) {
              //  Log.i(TAG, "SaveDetailsToTable当前的线程:" + Thread.currentThread().getName());
                dataCallback.onFinshed();
            }
        }

    }

    /**
     * 根据学号确定性别 学号第二位是1的话是男,2的话是女
     *
     * @param personInfo
     */
    private static void confirmSex(DetailsInfo personInfo) {
        //学号
        String numStr = personInfo.account + "";
        //切割学号字符串,获得判断性别的字符
        String str = numStr.substring(1, 2);
        if ("1".equals(str)) {
            personInfo.sex = "男";
        } else if ("2".equals(str)) {
            personInfo.sex = "女";
        } else {
            personInfo.sex = "保密";
        }

    }


    public static List<Score> scoreDatas = new ArrayList<>();

    /**
     * 处理Score返回的json数据
     *
     * @param list         json数组
     * @param dataCallback
     */
    public static void handleScoreStr(List<String> list, final DataCallback dataCallback) {
        if (list != null && list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject(list.get(i));
                    JSONArray rows = jsonObject.getJSONArray("rows");
                    if (rows != null && rows.length() > 0) {
                        for (int j = 0; j < rows.length(); j++) {
                            Score score = new Gson().fromJson(rows.getString(j), Score.class);
                            int result = DataSupport.where("account = ? and courseName=? and " +
                                            "semesterCode=?", HttpUtil.account, score.courseName,
                                    score.semesterCode + "").count(Score.class);
                            if (result <= 0) {
                                score.save();
                            } else {
                                Score udateScore = new Score();
                                udateScore.point = score.point;
                                udateScore.actualScore = score.actualScore;
                                udateScore.updateAll("account = ? and courseName=? and " +
                                                "semesterCode=?", HttpUtil.account, score.courseName,
                                        score.semesterCode + "");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dataCallback != null) {
                        dataCallback.onError(new RuntimeException("handleScoreStr解析JSON出错"));
                    }
                }

            }


        }

        if (dataCallback != null) {
            dataCallback.onFinshed();
        }

    }

    /**
     * 清空成绩表中集合存储的所有数据
     */
    public static void clearScoreList() {
        if (scoreDatas.size() > 0) {
            scoreDatas.clear();
        }

    }

}
