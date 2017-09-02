package com.example.yueyue.campusapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.adapter.NewsAdapter;
import com.example.yueyue.campusapp.models.NewsBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 代办事项
 * Created by Administrator on 2015/10/26.
 */
public class NewsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = NewsFragment.class.getSimpleName();
    private RecyclerView rl_list;
    private List<NewsBean> mList;

    private ImageView mIvBingPic;
    private NewsAdapter mNewsAdapter;
    private Dialog mDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_news, null);
        initData();
        initView(view);


        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mList = new ArrayList<>();

        mList.add(new NewsBean("查询成绩单", R.layout.news_chinese_grade_item));
        mList.add(new NewsBean("缓考申请", R.layout.news_delayed_exam_item));
        mList.add(new NewsBean("重修申请", R.layout.news_reset_to_signup_item));
        mList.add(new NewsBean("毕业证遗落补办", R.layout.news_loss_of_graduation_certificate_item));
        mList.add(new NewsBean("补领学生证", R.layout.news_application_for_student_card_item));
        mList.add(new NewsBean("项目个人信息变更", R.layout.news_innovation_project_personnel_change_item));
        mList.add(new NewsBean("双学位(辅修)申请", R.layout.news_double_degree_registration_item));


    }


    /**
     * 初始化组件
     *
     * @param view
     */
    private void initView(View view) {
        mDialog = new Dialog(getActivity(), R.style.Dialog_FS);
        View alertDialogView = View.inflate(getActivity(), R.layout.view_news_alertdialog, null);
        mDialog.setContentView(alertDialogView);

        WindowManager m = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();  //获取对话框当前的参数值、
        params.width = d.getWidth();    //宽度设置全屏宽度
        params.height = d.getHeight();
        mDialog.getWindow().setAttributes(params);     //设置生效

//        final ImageView ivBackGround = (ImageView) alertDialogView.findViewById(R.id.iv_back_ground);
        final LinearLayout lyoItem = (LinearLayout) alertDialogView.findViewById(R.id.lyo_item);
        final TextView btSure = (TextView) alertDialogView.findViewById(R.id.bt_sure);
        btSure.setClickable(true);
        btSure.setOnClickListener(this);


        rl_list = (RecyclerView) view.findViewById(R.id.rl_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rl_list.setLayoutManager(layoutManager);

        mNewsAdapter = new NewsAdapter(mList);
        mNewsAdapter.setOnItemClickLitener(new NewsAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                NewsBean newsBean = mList.get(position);
                if (lyoItem.getChildCount() > 0) {
                    lyoItem.removeAllViews();
                }

                View itemView = View.inflate(getActivity(), newsBean.getId(), null);
                lyoItem.addView(itemView);
                if (mDialog != null) {
                    mDialog.show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        rl_list.setAdapter(mNewsAdapter);


        mIvBingPic = (ImageView) view.findViewById(R.id.iv_bing_pic);

        //在fragment中使用menu菜单必须添加下面这一行代码，需要在onCreate()方法里面添加语
        setHasOptionsMenu(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sure:
                //dialog的确定按钮
                if (mDialog != null) {
                    mDialog.hide();
                }
                break;
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
