package com.example.yueyue.campusapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.yueyue.campusapp.models.Course;
import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.activity.WheelActivity;
import com.example.yueyue.campusapp.db.Db;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.example.yueyue.campusapp.utils.ColorUtils;
import com.example.yueyue.campusapp.utils.DateUtil;
import com.example.yueyue.campusapp.utils.GlobalValue;
import com.example.yueyue.campusapp.utils.HandleResponseUtil;
import com.example.yueyue.campusapp.utils.HttpUtil;
import com.example.yueyue.campusapp.widget.CornerTextView;
import com.example.yueyue.campusapp.widget.ProgressDialogHelper;
import com.loonggg.bottomsheetpopupdialoglib.ShareBottomPopupDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.lib.colordialog.ColorDialog;

import static android.app.Activity.RESULT_OK;

/**
 * 课程查询
 */
public class CourseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = CourseFragment.class.getSimpleName();
    private List<List<Course>> courseDatas = HandleResponseUtil.courseDatas;
    private SharedPreferences sharedPreferences;
    // private TextView title_select_week;//选择第几周
    private LinearLayout weekNames;//星期的布局1-7头
    private LinearLayout sections;//显示几月-->到时改为显示第几周
    private LinearLayout weekPanel_1;//第一天显示
    private LinearLayout weekPanel_2;//第二天显示
    private LinearLayout weekPanel_3;//第三天显示
    private LinearLayout weekPanel_4;//第四天显示
    private LinearLayout weekPanel_5;//第五天显示
    private LinearLayout weekPanel_6;//第六天显示
    private LinearLayout weekPanel_7;//第七天显示
    private MaterialRefreshLayout mFreshLayout;//刷新专用
    private FloatingActionButton item_course_show_fab;//悬浮
    private Button import_btn;//导入课程按钮
    //最底层的那个布局
    private CoordinatorLayout cl_fragment_root;

    //显示数据的布局
    private ViewGroup fragment_course_show;
    //数据为空的布局
    private ViewGroup fragment_course_empty;
    //一节课的高度
    private int itemHeight;
    //每天最多12节课
    private int maxSection = 12;

    //添加星期的布局1-7头布局
    private List<LinearLayout> mWeekViews = new ArrayList<>();//星期的布局1-7

    //跳转到WheelActivity的请求码
    private int requestWheelCode = 200;
    private int mCurrWeek;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, null);
        initView(view);
        //获取当前周
        mCurrWeek = DateUtil.getcurrentWeek();
        //从数据库中寻找数据
        findFromDb(mCurrWeek);

        return view;
    }


    /**
     * 找到我们需要的组件
     *
     * @param view
     */
    private void initView(View view) {
        itemHeight = getResources().getDimensionPixelSize(R.dimen.sectionHeight);
        //root布局
        cl_fragment_root = (CoordinatorLayout) view.findViewById(R.id.cl_fragment_root);


        //显示数据的布局
        fragment_course_show = (ViewGroup) view.findViewById(R.id.fragment_course_show);


        //星期的布局1-7头
        weekNames = (LinearLayout) view.findViewById(R.id.weekNames);
        //显示第几个月
        sections = (LinearLayout) view.findViewById(R.id.sections);
        //星期一显示的课程
        weekPanel_1 = (LinearLayout) view.findViewById(R.id.weekPanel_1);
        //星期二显示的课程
        weekPanel_2 = (LinearLayout) view.findViewById(R.id.weekPanel_2);
        //星期三显示的课程
        weekPanel_3 = (LinearLayout) view.findViewById(R.id.weekPanel_3);
        //星期四显示的课程
        weekPanel_4 = (LinearLayout) view.findViewById(R.id.weekPanel_4);
        //星期五显示的课程
        weekPanel_5 = (LinearLayout) view.findViewById(R.id.weekPanel_5);
        //星期六显示的课程
        weekPanel_6 = (LinearLayout) view.findViewById(R.id.weekPanel_6);
        //星期日显示的课程
        weekPanel_7 = (LinearLayout) view.findViewById(R.id.weekPanel_7);

        //添加布局
        mWeekViews.add(weekPanel_1);
        mWeekViews.add(weekPanel_2);
        mWeekViews.add(weekPanel_3);
        mWeekViews.add(weekPanel_4);
        mWeekViews.add(weekPanel_5);
        mWeekViews.add(weekPanel_6);
        mWeekViews.add(weekPanel_7);

        //下拉刷新界面
        mFreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.mFreshLayout);
        //悬浮按钮界面
        item_course_show_fab = (FloatingActionButton) view.findViewById(R.id.item_course_show_fab);
        //课程里面的布局
        initWeekNameView();//初始化周名字的View
        initSectionView();//初始化月份的那个View


        //数据为空的布局
        fragment_course_empty = (ViewGroup) view.findViewById(R.id.fragment_course_empty);
        //导入数据界面
        import_btn = (Button) view.findViewById(R.id.import_btn);


        setHasOptionsMenu(true);
        //设置监听
        initListener();

    }

    /**
     * 顶部周一到周日的布局
     **/
    private void initWeekNameView() {

        //6月+周一~周日-->mWeekViews.size() + 1
        for (int i = 0; i < mWeekViews.size() + 1; i++) {
            TextView tvWeekName = new TextView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置为水平居中样式-->对自己的内部组件而言
            lp.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            if (i != 0) {
                lp.weight = 1;
                tvWeekName.setText("周" + DateUtil.intToZH(i));
                //设置今日的星期为红色,其他的为灰黑色
                if (i == DateUtil.getWeekDay()) {
                    tvWeekName.setTextColor(Color.parseColor("#FF0000"));//红色
                } else {

                    tvWeekName.setTextColor(Color.parseColor("#4A4A4A"));//灰黑色
                }
            } else {
                //设置为几月
                lp.weight = 0.8f;
                tvWeekName.setText(DateUtil.getMonth() + "月");
            }
            tvWeekName.setGravity(Gravity.CENTER_HORIZONTAL);
            tvWeekName.setLayoutParams(lp);
            weekNames.addView(tvWeekName);
        }
    }

    /**
     * 左边节次布局(1-12)，设定每天最多12节课
     */
    private void initSectionView() {
        for (int i = 1; i <= maxSection; i++) {
            TextView tvSection = new TextView(getActivity());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.sectionHeight));
            lp.gravity = Gravity.CENTER;//设置为垂直,对子View而言
            tvSection.setGravity(Gravity.CENTER);
            tvSection.setText(String.valueOf(i));
            tvSection.setLayoutParams(lp);
            sections.addView(tvSection);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Log.i(TAG, "fragment的方法onActivityResult执行了....");

        if (requestCode == requestWheelCode && resultCode == RESULT_OK) {
            //   Log.i(TAG, "fragment的方法onActivityResult执行了...." + requestCode + "---" + requestCode);
            //处理从WheelActivity的数据-->默认是选中当前周的
            int week = data.getIntExtra(GlobalValue.WHEEL_SELECT_CURR_WEEK, mCurrWeek);
            //Toast.makeText(getActivity(), "拿到的week:"+week, Toast.LENGTH_SHORT).show();
            findFromDb(week);
        }
    }

    /**
     * 初始化组件监听
     */
    private void initListener() {
        //给fab悬浮按钮设置监听
        item_course_show_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Snackbar.make(v, "点击了", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), WheelActivity.class);
                startActivityForResult(intent, requestWheelCode);
            }
        });

        //给导入button设置监听
        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里进行联网获取数据
                getCourseFromNet(mCurrWeek);

            }
        });

        //给mFreshLayout设置监听
        mFreshLayout.setLoadMore(false);//都没有更多就不要设置加载更多
        mFreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                clearChildView();
                initWeekCourseView();
                mFreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFreshLayout.finishRefreshing();
                    }
                }, 500);
            }

        });

    }

    /**
     * 根据某周次联网请求获得数据
     *
     * @param week 某周次
     */
    private void getCourseFromNet(int week) {
        HttpUtil.getCourseHtml(new DataCallback() {

            @Override
            public void onStart() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.showProgressDialog(getActivity(),
                                "正在导入，这可能需要一点时间...");
                    }
                });
            }

            @Override
            public void onFail() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                    }
                });
            }

            @Override
            public void onFinshed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshView();
                        ProgressDialogHelper.closeProgressDialog();
                        isShouldShowDialog();
                    }
                });
            }
        }, week);
    }

    /**
     * 弹出提示对话框-->提示是否跳转到系统默认浏览器进行查看课程
     */
    private void isShouldShowDialog() {
        boolean isHaveData = Db.loadCourse(mCurrWeek);
        int result = DataSupport.where("account = ?", HttpUtil.account).count(Course.class);
        if (isHaveData == false && result > 0) {
//                //没有数据的情况下,而且数据库有其他周的数据,及其可能是这一周没有课
            ColorDialog dialog = new ColorDialog(getActivity());
            dialog.setTitle("温馨提示:");
            dialog.setAnimationEnable(true);
            dialog.setContentText("这周没有课,还有疑问的话点击确定按钮跳转到系统默认浏览器" +
                    "进行网页进行查看");
            dialog.setContentImage(getResources().getDrawable(R.drawable.color_dialog_pic_bg));
            dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
                @Override
                public void onClick(ColorDialog dialog) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(GlobalValue.CAMPUS_BASE_URL);
                    intent.setData(content_url);
                    startActivity(Intent.createChooser(intent, "新教务系统"));
                }
            });
            dialog.setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
                @Override
                public void onClick(ColorDialog dialog) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }).show();
        }
    }

    /**
     * 每次刷新前清除每个LinearLayout上的课程view
     */
    private void clearChildView() {
        for (int i = 0; i < mWeekViews.size(); i++) {
            if (mWeekViews.get(i) != null)
                if (mWeekViews.get(i).getChildCount() > 0)
                    mWeekViews.get(i).removeAllViews();
        }
    }


    /**
     * 初始化课程表
     */
    private void initWeekCourseView() {
        //Log.i(TAG, "initWeekCourseView方法中的course:" + courseDatas.get(0).toString());
        //Log.i(TAG,"+++++++++++++++================+++++++++++++++");
        //循环获得7天课程
        //Log.i(TAG,"initWeekCourseView方法中的mWeekViews:"+mWeekViews+",courseDatas:"+ courseDatas);
        for (int i = 0; i < mWeekViews.size(); i++) {
            //一日的课程
            initWeekPanel(mWeekViews.get(i), courseDatas.get(i));
        }
    }

    /**
     * 初始化每日的课程
     *
     * @param ll   布局
     * @param data 数据
     */
    public void initWeekPanel(LinearLayout ll, List<Course> data) {
        //Log.i(TAG, "initWeekPanel方法中的course开始了+++++++++++++++++");
        if (ll == null || data == null || data.size() < 1) {
            return;
        }
        Course firstCourse = data.get(0);
        for (int i = 0; i < data.size(); i++) {
            final Course course = data.get(i);//获得一日课程中的课程安排

            if (course.sectionStart == 0 || course.sectionSpan == 0) {
                Log.i("initWeekPanel", "填入错误,需要检查Bean-->节数为0,跨节为0");
                //说明是填入错误,叫她检查
                return;
            }

            FrameLayout frameLayout = new FrameLayout(getActivity());

            CornerTextView tv = new CornerTextView(getActivity(),
                    ColorUtils.getCourseBgColor((int) (Math.random() * 10)),
                    getResources().getDimensionPixelSize(R.dimen.padding_3_dip));//圆角大小
            //设置课程的布局大小
            LinearLayout.LayoutParams frameLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight * course.sectionSpan);
            LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            if (i == 0) {
                frameLp.setMargins(0, (course.sectionStart - 1) * itemHeight, 0, 0);
            } else {
                //计算出计算当日两节课之前的空白距离
                frameLp.setMargins(0, (course.sectionStart - (firstCourse.sectionStart
                        + firstCourse.sectionSpan)) * itemHeight, 0, 0);
            }
            tv.setLayoutParams(tvLp);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.course_tab_tv_size));
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            tv.setText(course.courseName + "\n @" + course.classRoom);

            frameLayout.setLayoutParams(frameLp);
            frameLayout.addView(tv);
            int padding = getResources().getDimensionPixelSize(R.dimen.padding_2_dip);
            frameLayout.setPadding(padding, padding, padding, padding);
            ll.addView(frameLayout);
            firstCourse = course;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "course.getCourseName()",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    /**
     * 从数据库中获取数据
     */
    private void findFromDb(int week) {
        Db.loadCourse(week);
        // Log.i(TAG, "findFromDb.........." + courseDatas.size() + "获取到的数据:" + courseDatas);
        //初始化相对应的其他布局
        refreshView();

    }


    /**
     * 初始化刷新界面
     */
    private void refreshView() {
        if (Db.isCourseHaveData()) {
            //有数据的情况下
            // Log.i(TAG, "执行了有数据的判断" + courseDatas);
            fragment_course_show.setVisibility(View.VISIBLE);
            fragment_course_empty.setVisibility(View.GONE);
            //初始化相对应的其他布局
            //更新之前移除旧的
            clearChildView();
            if (courseDatas != null && courseDatas.size() > 0) {
                initWeekCourseView();//显示每周课程内容的View
            }
        } else {
            //数据库没有数据,但该周没有数据,说明极大可能是因为该周没课
            // Log.i(TAG, "initViewPage中的方法执行了----------------没有数据");
            fragment_course_show.setVisibility(View.GONE);
            fragment_course_empty.setVisibility(View.VISIBLE);

        }


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.course_right_menu, menu);

    }

    /**
     * 左上角item选择
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == R.id.action_right_pop) {
            showBottomPopupDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_refresh:
                //数据库中获取当前周
                findFromDb(mCurrWeek);
                courseBottomPopupDialog.dismiss();
                break;
            case R.id.ib_net:
                //联网获取当前周
                getCourseFromNet(mCurrWeek);
                courseBottomPopupDialog.dismiss();
                break;
            case R.id.ib_select_week:
                //数据库中获取当前周
                courseBottomPopupDialog.dismiss();
                Intent intent = new Intent(getActivity(), WheelActivity.class);
                startActivityForResult(intent, requestWheelCode);
                break;
            case R.id.ib_exit:
                //退出应用
                courseBottomPopupDialog.dismiss();
                getActivity().finish();
                break;
            case R.id.bt_cancle:
                //取消
                courseBottomPopupDialog.dismiss();
                break;


        }
    }

    private ShareBottomPopupDialog courseBottomPopupDialog;

    /**
     * 底部弹出对话框
     */
    private void showBottomPopupDialog() {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_course_bottom_dialog, null);
        courseBottomPopupDialog = new ShareBottomPopupDialog(getActivity(), dialogView);
        courseBottomPopupDialog.showPopup(cl_fragment_root);
        ImageButton ib_refresh = (ImageButton) dialogView.findViewById(R.id.ib_refresh);
        ImageButton ib_net = (ImageButton) dialogView.findViewById(R.id.ib_net);
        ImageButton ib_select_week = (ImageButton) dialogView.findViewById(R.id.ib_select_week);
        ImageButton ib_exit = (ImageButton) dialogView.findViewById(R.id.ib_exit);
        Button bt_cancle = (Button) dialogView.findViewById(R.id.bt_cancle);
        ib_refresh.setOnClickListener(this);
        ib_net.setOnClickListener(this);
        ib_select_week.setOnClickListener(this);
        ib_exit.setOnClickListener(this);
        bt_cancle.setOnClickListener(this);

    }

}
