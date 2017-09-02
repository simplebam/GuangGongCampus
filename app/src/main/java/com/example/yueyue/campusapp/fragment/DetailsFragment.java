package com.example.yueyue.campusapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yueyue.campusapp.models.DetailsInfo;
import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.implement.CallBack;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.example.yueyue.campusapp.utils.HttpUtil;
import com.example.yueyue.campusapp.widget.ProgressDialogHelper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * 展示学生个人信息
 * Created by Administrator on 2015/10/26.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = DetailsFragment.class.getSimpleName();
    //学号
    private TextView tv_number;
    //姓名
    private TextView tv_name;
    //性别
    private TextView tv_sex;
    //入学年份
    private TextView tv_enter_year;
    //院系名称
    private TextView tv_faculty;
    //主修专业
    private TextView tv_major;
    //所在班级
    private TextView tv_grade;
    //所属校区
    private TextView tv_address;
    //辅修专业
    private TextView tv_minor;
    //导入数据按钮
    private Button import_btn;
    //空布局的id
    private LinearLayout ll_empty;
    //显示数据布局的id
    private LinearLayout ll_show;
    //显示背景图片的
    private ImageView iv_bing_pic;
    //刷新界面的下拉刷新组件
    private SwipeRefreshLayout swipe_container;
    //装载数据的集合
    private List<DetailsInfo> detailsInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_details, null);
        initView(view);

        findDataFromDb();

        return view;
    }

    /**
     * 从网上获取数据
     */
    private void findDataFromNet() {
        ProgressDialogHelper.showProgressDialog(getActivity(),"数据正从网络加载中ing");

        //假如PersonInfo数据表中没有,联网获取数据
        HttpUtil.getDetailsHtml(new DataCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFail() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                    }
                });
                refreshView();
            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                    }
                });
                refreshView();
            }

            @Override
            public void onFinshed() {
                //说明有数据保存成功了,需要取出
                detailsInfoList.clear();
                detailsInfoList.addAll(DataSupport.limit(1).order("id desc").where(HttpUtil.account)
                        .find(DetailsInfo.class));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                    }
                });

                refreshView();
            }
        }, HttpUtil.cookie);

    }

    /**
     * 从数据库中查询数据
     */
    private void findDataFromDb() {
        //其实就是为了获取最新的一条数据-->设定最下面的一条数据就是最新数据
        detailsInfoList.clear();
        detailsInfoList.addAll(DataSupport.limit(1).order("id desc").where(HttpUtil.account)
                .find(DetailsInfo.class));
        if (detailsInfoList.size() > 0) {
            //有数据的情况下
            refreshView();
        }

        refreshView();
    }

    /**
     * 刷新界面
     */
    private void refreshView() {
        //Log.i(TAG, "initView方法执行了...");
        //Log.i(TAG, "initView执行时候detailsInfoList大小:" + detailsInfoList.size() +
        //       "---" + detailsInfoList.toString());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //使用glide加载图片
                if (TextUtils.isEmpty(HttpUtil.bing_pic_url)) {
                    HttpUtil.loadDailyBingPic(getActivity(), new CallBack() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinsh(String response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //使用glide加载图片
                                    Log.i(TAG,"refreshView:"+HttpUtil.bing_pic_url);
                                    Glide.with(getActivity()).load(HttpUtil.bing_pic_url).into(iv_bing_pic);
                                }
                            });

                        }
                    });

                } else {
                    Glide.with(getActivity()).load(HttpUtil.bing_pic_url).into(iv_bing_pic);
                }

                if (detailsInfoList.size() > 0) {
                    //切换到显示数据的界面
                    ll_empty.setVisibility(View.GONE);
                    ll_show.setVisibility(View.VISIBLE);
                    showDetailsData();
                } else {
                    ll_show.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.VISIBLE);
                }

                if (swipe_container.isRefreshing()) {
                    swipe_container.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 显示数据
     */
    private void showDetailsData() {

        if (detailsInfoList.size() > 0) {
            DetailsInfo detailsInfo = detailsInfoList.get(0);
            //学号
            tv_number.setText(detailsInfo.account);
            //姓名
            tv_name.setText(detailsInfo.name);
            //性别
            tv_sex.setText(detailsInfo.sex);
            //入学年份
            tv_enter_year.setText(detailsInfo.enterYear);
            //院系名称
            tv_faculty.setText(detailsInfo.faculty);
            //主修专业
            tv_major.setText(detailsInfo.major);
            //所在班级
            tv_grade.setText(detailsInfo.grade);
            //所属校区
            tv_address.setText(detailsInfo.address);
            //辅修专业
            tv_minor.setText(detailsInfo.minor);
        }
    }


    /**
     * 初始化组件
     *
     * @param view
     */
    private void initView(View view) {
        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        iv_bing_pic = (ImageView) view.findViewById(R.id.iv_bing_pic);

        //有数据显示界面的组件
        ll_show = (LinearLayout) view.findViewById(R.id.ll_show);
        tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);
        tv_enter_year = (TextView) view.findViewById(R.id.tv_enter_year);
        tv_faculty = (TextView) view.findViewById(R.id.tv_major);
        tv_major = (TextView) view.findViewById(R.id.tv_specialty);
        tv_grade = (TextView) view.findViewById(R.id.tv_grade);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
        tv_minor = (TextView) view.findViewById(R.id.tv_minor);


        //无数据显示界面的组件
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
        import_btn = (Button) view.findViewById(R.id.import_btn);

        import_btn.setOnClickListener(this);
        swipe_container.setColorSchemeColors(getResources().getColor(R.color.mainColor));
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findDataFromNet();
            }
        });

        //在fragment中使用menu菜单必须添加下面这一行代码，需要在onCreate()方法里面添加语
        setHasOptionsMenu(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.import_btn:
                //导入按钮的监听
                findDataFromNet();
                break;
        }


    }


}
