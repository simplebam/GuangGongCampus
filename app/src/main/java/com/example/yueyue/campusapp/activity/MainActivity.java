package com.example.yueyue.campusapp.activity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.yueyue.campusapp.fragment.NewsFragment;
import com.example.yueyue.campusapp.models.Course;
import com.example.yueyue.campusapp.models.DetailsInfo;
import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.db.Db;
import com.example.yueyue.campusapp.fragment.ContactFragment;
import com.example.yueyue.campusapp.fragment.CourseFragment;
import com.example.yueyue.campusapp.fragment.DetailsFragment;
import com.example.yueyue.campusapp.fragment.ScoreFragment;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.example.yueyue.campusapp.utils.HttpUtil;

import org.litepal.crud.DataSupport;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar = null;
    private DrawerLayout drawerLayout = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private NavigationView navigationView = null;
    private TextView nv_header_username;
    private TextView nv_header_grade;
    private CircleImageView nv_header_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //安卓5.0以上才支持
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.mainColor));
//        }
        setContentView(R.layout.activity_main);
        initView();

        //获取每日一图链接
        HttpUtil.loadDailyBingPic(this, null);

        //获得你的名字
        HttpUtil.getYourName(new MyCallBack());


        //获取全部课程
        int result = DataSupport.where("account = ?", HttpUtil.account).count(Course.class);
        if (result <= 0) {
            HttpUtil.getAllCourseHtml(null, HttpUtil.cookie);
        }

        //主界面选为课表
        switchToCourse();

        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        Log.i(TAG, "onCreate:" + Thread.currentThread().getName());

       //HttpUtil.getScoreData(null,"");
        //创建电话本
        //CallCreate.callDataCreate(true,null);
    }


    /**
     * 初始化UI
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);

        drawerToggle.syncState();

        drawerLayout.setDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(navigationView);
    }

    //设置NavigationView点击事件
    private void setupDrawerContent(NavigationView navigationView) {
        //设置NavigationView的item布局
        /**设置MenuItem的字体颜色**/
        //R.drawable
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(
                R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);
        /**设置MenuItem默认选中项**/
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.school_news:
                                switchToNews();
                                break;
                            case R.id.school_course:
                                switchToCourse();
                                break;
                            case R.id.school_score:
                                switchToScore();
                                break;
                            case R.id.school_details:
                                switchToDetails();
                                break;
                            case R.id.school_contact:
                                switchToContact();
                                break;
//                            case R.id.about:
//                                switchToAbout();
//                                break;
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });


        //设置NavigationView的header布局
        // 动态添加头布局后要在xml中把app:headerLayou"去掉,不然出现两个头布局
        //获取头像点击事件
        View nvDrawview = navigationView.inflateHeaderView(R.layout.navigation_header);
        nv_header_image = (CircleImageView) nvDrawview.findViewById(R.id.nv_header_image);
        nv_header_username = (TextView) nvDrawview.findViewById(R.id.nv_header_username);
        nv_header_grade = (TextView) nvDrawview.findViewById(R.id.nv_header_grade);

        updateNavigationHeader();
        Log.i(TAG, "setupDrawerContent 尾巴  " + Thread.currentThread().getName());
    }

    /**
     * 更新导航栏
     */
    private void updateNavigationHeader() {


        DetailsInfo detailsInfo = Db.loadNvHeaderDetails();
        if (detailsInfo != null) {
            if (!TextUtils.isEmpty(detailsInfo.name)) {
                nv_header_username.setText(detailsInfo.name);
            }
            if (!TextUtils.isEmpty(detailsInfo.sex)) {
                if ("男".equals(detailsInfo.sex)) {
                    // nv_header_image.setBackgroundResource(R.drawable.nv_header_boy_bg);
                    nv_header_image.setImageResource(R.drawable.nv_header_boy_bg);
                } else if ("女".equals(detailsInfo.sex)) {
                    // nv_header_image.setBackgroundResource(R.drawable.nv_header_gril_bg);
                    nv_header_image.setImageResource(R.drawable.nv_header_gril_bg);
                }
            }
            if (!TextUtils.isEmpty(detailsInfo.grade)) {
                nv_header_grade.setText(detailsInfo.grade);
            }
        }


    }

    private void switchToContact() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactFragment()).commit();

        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //toolbar.setTitle(R.string.school_phone);
        getSupportActionBar().setTitle(R.string.school_phone);

    }

    private void switchToDetails() {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DetailsFragment()).commit();

        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //  toolbar.setTitle(R.string.school_status);
        getSupportActionBar().setTitle(R.string.person_details);

    }

    private void switchToScore() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ScoreFragment()).commit();

        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //toolbar.setTitle(R.string.school_score);
        getSupportActionBar().setTitle(R.string.school_score);

    }

    private void switchToNews() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.container, new NewsFragment()).commit();
//        if(HttpUtil.datamap.values().size()==0){
//            ProgressDialogHelper.showProgressDialog(this, "正在加载...");
//            HttpUtil.getHtmlUtil(this, News.NEWS_INDEX, new CallBack() {
//                @Override
//                public void onStart() {
//                    //ProgressDialogHelper.showProgressDialog(DrawerLayoutActivity.this, "正在加载...");
//                }
//
//                @Override
//                public void onFinsh(String response) {
//                    HandleResponseUtil.parseTitleData(response);
//                    ProgressDialogHelper.closeProgressDialog();
//                }
//            }, Request.Method.GET, null);
//        }
        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //toolbar.setTitle(R.string.school_news);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                new NewsFragment()).commit();

        getSupportActionBar().setTitle(R.string.school_news);

    }

    private void switchToCourse() {

        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                new CourseFragment()).commit();

        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //toolbar.setTitle(R.string.school_course);
        getSupportActionBar().setTitle(R.string.school_course);


    }


    private void switchToAbout() {

        //   getSupportFragmentManager().beginTransaction().replace(R.id.container, new AboutFragment()).commit();
        //注意:toolbar.setTitle()是无效的,因为toolbar已经被getSupportActionBar()装备了
        //所以需要用到 getSupportActionBar().setTitle()
        //toolbar.setTitle(R.string.about);
        getSupportActionBar().setTitle(R.string.about);

    }


    /**
     * HttpUtil.getYourName的回调函数
     */
    private class MyCallBack implements DataCallback {
        @Override
        public void onStart() {

        }

        @Override
        public void onFail() {

        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onFinshed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateNavigationHeader();
                }
            });


        }
    }
}
