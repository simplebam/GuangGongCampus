package com.example.yueyue.campusapp.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.memoryspinner.MemorySpinner;
import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.adapter.ScoreAdapter;
import com.example.yueyue.campusapp.db.Db;
import com.example.yueyue.campusapp.implement.DataCallback;
import com.example.yueyue.campusapp.models.Score;
import com.example.yueyue.campusapp.utils.DateUtil;
import com.example.yueyue.campusapp.utils.HandleResponseUtil;
import com.example.yueyue.campusapp.utils.HttpUtil;
import com.example.yueyue.campusapp.utils.ScorePointutil;
import com.example.yueyue.campusapp.utils.SemesterUtil;
import com.example.yueyue.campusapp.widget.ProgressDialogHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.refactor.lib.colordialog.ColorDialog;


/**
 * Created by Administrator on 2015/10/21.
 */
public class ScoreFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ScoreFragment.class.getSimpleName();
    //装载数据
    private List<Score> scores = HandleResponseUtil.scoreDatas;
    //装载选项类型
    private ArrayList<String> spinnerList = new ArrayList<>();
    private RecyclerView rl_list;
    private TextView tv_semester_point;
    private ImageView iv_back_ground;
    private Button import_btn;
    private ViewGroup showLayout;//数据库有数据,并且score有数据的时候显示
    private ViewGroup emptyLayout;//数据库没有数据时候的显示
    private ViewGroup noSemesterLayout;//数据库有数据,但没有想查的学期数据时候的显示
    private SwipeRefreshLayout refreshLayout;//数据库有数据,但没有想查的学期数据时候的显示
    private MemorySpinner memorySpinner;
    private ScoreAdapter mScoreAdapter;
    private int type = 1;//默认显示学年学期的
    private int pageNumber = 0;//查询全部的时候使用分页查询,这个是分页查询的页码
    private MyOnScrollListener myOnScrollListener;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_score, null);
        //initView(view);
        initView(view);
        //先查数据库
        findFromDb();

        return view;
    }

    /**
     * 初始化组件
     *
     * @param view
     */
    private void initView(View view) {
        //下拉刷新
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sfl_layout);
        iv_back_ground = (ImageView) view.findViewById(R.id.iv_back_ground);

        //数据库没有数据时候的显示
        emptyLayout = (ViewGroup) view.findViewById(R.id.item_score_empty);
        import_btn = (Button) view.findViewById(R.id.import_btn);

        //数据库有数据,并且score有数据的时候显示
        showLayout = (ViewGroup) view.findViewById(R.id.item_score_show);
        tv_semester_point = (TextView) view.findViewById(R.id.tv_semester_point);

        memorySpinner = (MemorySpinner) view.findViewById(R.id.ms_spinner);

        rl_list = (RecyclerView) view.findViewById(R.id.rl_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rl_list.setLayoutManager(layoutManager);
        mScoreAdapter = new ScoreAdapter(scores, type);
        rl_list.setAdapter(mScoreAdapter);
        myOnScrollListener = new MyOnScrollListener();

        //数据库有数据,但没有想查的学期数据时候的显示
        noSemesterLayout = (ViewGroup) view.findViewById(R.id.item_score_no_semester);

        //获得list数据
        //获得当前的学年学期
        int currInSemester = DateUtil.getYear() - 2000;//2017
        //学号的3-4位置是它的入学年份
        int enterYear = Integer.parseInt(HttpUtil.account.substring(2, 4));
        spinnerList.add("查询全部");

        // Log.i(TAG, "currInSemester:" + currInSemester + "..." + "enterYear:" + enterYear);
        // currInSemester:17...enterYear:14
        for (int year = enterYear; year < currInSemester + 1; year++) {
            spinnerList.add(0, "20" + year + "春季");
            spinnerList.add(0, "20" + year + "秋季");
        }

        memorySpinner.setMemoryCount(0);
        memorySpinner.setData(null, spinnerList);



        setHasOptionsMenu(true);
        initListener();
    }

    /**
     * 为相关组件注册监听
     */
    private void initListener() {

//        Glide.with(getActivity()).load(HttpUtil.bing_pic_url).into(iv_back_ground);

        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogHelper.showProgressDialog(getActivity(),
                        "数据正从网络加载中ing");
                getScoreFromNet();

            }
        });


        memorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == spinnerList.size() - 1) {
                    //点击了全部的意思
                    type = 0;
                    mScoreAdapter.setType(type);
                    //分页查询-->//查询全部的时候使用分页查询
                    rl_list.getRecycledViewPool().clear();
                    Db.loadScoreAll(pageNumber);
                } else {
                    //点击了其他的
                    type = 1;
                    mScoreAdapter.setType(type);
                    String semesterCode = SemesterUtil.name2Code(spinnerList.get(position));
                    if (Db.loadScore(Integer.parseInt(semesterCode))) {
                        refreshView();//刷新View
                    } else {
                        refreshView();//刷新View
                        String title = "温馨提示:";
                        String content = "没有当前课程成绩,可以查看其他以往的学期成绩";
                        showTextDialog(title, content);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (Db.loadScoreLast()) {
                    refreshView();//刷新View
                }
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                //加载最新数据
                getScoreFromNet();
            }
        });


    }

    /**
     * 联网获取Score数据表
     */
    private void getScoreFromNet() {
        HttpUtil.getScoreData(new DataCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFail() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshView();
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshView();
                        String title = "错误提示:";
                        String content = "解析数据出错,请登录网页查看一下是否有数据";
                        showTextDialog(title, content);
                    }
                });

            }

            @Override
            public void onFinshed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载最新一期的score
                        loadLastScoreFromDb();
                    }
                });

            }
        });
    }

    /**
     * 弹出提示对话框-->加载失败的对话框
     */
    private void showTextDialog(String title, String content) {
        ColorDialog dialog = new ColorDialog(getActivity());
        dialog.setColor(getResources().getColor(R.color.score_dialog_greeen));
        dialog.setAnimationEnable(true);
        dialog.setTitle(title);
        dialog.setContentText(content);
        dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }).show();
    }


    /**
     * 查询数据库中获取数据
     */
    private void findFromDb() {

        //默认加载最新的学期的课程表
        if (scores != null && scores.size() == 0) {
            loadLastScoreFromDb();
        } else {
            changeSpinnerName();
            refreshView();
        }

    }

    /**
     * 从数据库获取最新的学年学期成绩
     */
    private void loadLastScoreFromDb() {
        if (Db.loadScoreLast()) {
            refreshView();
            changeSpinnerName();
        } else {
            refreshView();
            changeSpinnerName();
            String title = "温馨提示:";
            String content = "没有当前最新的课程成绩,可以查看其他以往的学期成绩";
            showTextDialog(title, content);
        }
    }

    private void changeSpinnerName() {
        if (scores.size() > 0) {
            Collections.sort(scores);
            String semesterName = SemesterUtil.code2Name(scores.get(scores.size() - 1).semesterCode + "");
            int index = spinnerList.indexOf(semesterName);
            memorySpinner.setSelection(index);
        }
    }


    /**
     * 初始化刷新界面
     */
    private void refreshView() {
        //关闭进度条加载
        ProgressDialogHelper.closeProgressDialog();

        //关闭下拉刷新图案
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }

        // rl_list.setOnScrollListener();
        // TODO: 2017/6/6 这里需要修改一下
        //rl_list.removeOnScrollListener(myOnScrollListener);
        //mRecycler.clearOldPositions()
        if (type==0) {
            rl_list.addOnScrollListener(myOnScrollListener);
        } else {
            rl_list.removeOnScrollListener(myOnScrollListener);
        }

        // Log.i(TAG, "refreshView" + (scores == HandleResponseUtil.scoreDatas));
        if (Db.isScoreHaveData()) {
            //有数据的情况下
            Log.i(TAG, "执行了有数据的判断" + scores);

            if (scores != null && scores.size() > 0) {
                showLayout.setVisibility(View.VISIBLE);
                float semesterPoint = ScorePointutil.getScorePoint(scores);
                tv_semester_point.setText(semesterPoint + "");
                emptyLayout.setVisibility(View.GONE);
                noSemesterLayout.setVisibility(View.GONE);
                rl_list.setAdapter(mScoreAdapter);

            } else {
                showLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.GONE);
                noSemesterLayout.setVisibility(View.VISIBLE);
            }
        } else {
            //数据库没有数据
            // Log.i(TAG, "initViewPage中的方法执行了----------------没有数据");
            showLayout.setVisibility(View.GONE);
            noSemesterLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);

        }


    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.score_right_menu, menu);

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
            loadLastScoreFromDb();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //判断一下滑动状态
            switch (newState) {
//                case  RecyclerView.SCROLL_STATE_IDLE :
//                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: //没有滑动的状态
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //判断是当前layoutManager是否为LinearLayoutManager
                    // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                        //获取最后一个可见view的位置
                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
                        //获取第一个可见view的位置
                        int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        Log.i(TAG, "MyOnScrollListener中的onScrollStateChanged:" + lastItemPosition
                                + "..." + firstItemPosition);
                        if (lastItemPosition == scores.size() - 1) {
                            pageNumber++;
                            if (pageNumber * Db.scorePageSize >= Db.getScoreDataCount()) {
                                Snackbar.make(recyclerView, "没有更多的数据", Snackbar.LENGTH_SHORT).show();
                            } else {
                                if (Db.loadScoreAll(pageNumber)) {
                                    // TODO: 2017/6/6

                                    // java.lang.IndexOutOfBoundsException: Inconsistency detected.
                                    // Invalid item position 12(offset:12).state:14
                                    if (mScoreAdapter != null) {
                                        mScoreAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }


                    break;

            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    ;


}
