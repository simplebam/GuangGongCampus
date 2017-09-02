package com.example.yueyue.campusapp.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yueyue.campusapp.models.ContactInfo;
import com.example.yueyue.campusapp.MyApplication;
import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.adapter.ContactAdapter;
import com.example.yueyue.campusapp.datacreate.CallCreate;
import com.example.yueyue.campusapp.implement.CallBack;
import com.example.yueyue.campusapp.utils.GlobalValue;
import com.example.yueyue.campusapp.utils.NetStatusUtil;
import com.example.yueyue.campusapp.widget.ProgressDialogHelper;
import com.example.yueyue.campusapp.widget.SideBar;

import org.kymjs.kjframe.http.HttpStatus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.lib.colordialog.ColorDialog;

import static org.litepal.crud.DataSupport.limit;


/**
 * 显示学校相关联系电话
 * Created by Administrator on 2015/10/27.
 */
public class ContactFragment extends android.support.v4.app.Fragment
        implements SideBar.OnTouchingLetterChangedListener {
    private static final String TAG = ContactFragment.class.getSimpleName();

    //导入信息表-->这里待会改为使用webView
    private Button import_btn;
    //数据
    private List<ContactInfo> contactInfoList = new ArrayList<>();
    //联系人列表适配器
    private ContactAdapter contactAdapter;
    //空布局-->没有数据的时候就显示
    private LinearLayout emptyLayout;

    //需要显示数据的时候的布局
    private ViewGroup showLayout;

    //模拟联系人名字
    private String[] modelDatas = {"刘一", "陈二", "张三", "李四", "王五", "赵六", "孙七",
            "周八", "吴九", "郑十", "天问", "鲨齿", "渊虹", "干将", "莫邪", "太阿", "雪霁",
            "水寒", "秋骊", "凌虚", "巨阙", "虎魄", "天照", "含光", "湛卢"};

    //显示联系人列表的
    private ListView phoneListView;


    //为了最后显示多少位联系人-->添加到ListView的最后(脚布局)
    private TextView mFooterView;

    //网页加载html
    private WebView mWebView;
    private FrameLayout rootLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //生成一个布局文件
        View view = inflater.inflate(R.layout.framgment_contact, null);
        //初始化组件以及相关的监听
        initView(view);
        //从数据库中找到我们需要的数据
        findFromDb();
        return view;

    }

    /**
     * 初始化组件
     *
     * @param view
     */
    private void initView(View view) {
        //根布局
        rootLayout = (FrameLayout) view.findViewById(R.id.fl_contact_root);

        //1.这里有数据显示时候的组件
        showLayout = (ViewGroup) view.findViewById(R.id.item_contact_show);

        //选中字母显示
        TextView mDialog = (TextView) view.findViewById(R.id.school_friend_dialog);
        //右侧滑动栏
        SideBar mSideBar = (SideBar) view.findViewById(R.id.school_friend_sidrbar);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mSideBar.setTextView(mDialog);

        //上面的搜索输入框-->使用姓名搜索手机号码-->待会搞定考虑一下使用手机号码搜索名字
        EditText mSearchInput = (EditText) view.findViewById(R.id.school_friend_member_search_input);
        mSearchInput.addTextChangedListener(new MyTextWatcher());
        //主要用来显示联系人
        phoneListView = (ListView) view.findViewById(R.id.school_friend_member);

        // 给listView设置adapter,这个是作为最后显示多少位联系人的
        mFooterView = (TextView) View.inflate(getActivity(),
                R.layout.item_list_contact_count, null);
        phoneListView.addFooterView(mFooterView);
        //这里先不要setAdapter,以免出现item空白(即使有数据的情况下)


        //--------------------------------------------------------
        //2.这里没有数据显示时候的组件
        emptyLayout = (LinearLayout) view.findViewById(R.id.empty_layout);
        import_btn = (Button) view.findViewById(R.id.import_btn);

        //在fragment中使用menu菜单必须添加下面这一行代码，需要在onCreate()方法里面添加语
        setHasOptionsMenu(true);

        //详细化部分按钮监听
        initListener();
    }

    /**
     * 设置相对应的监听
     */
    private void initListener() {
        //给导入按钮设置点击事件
        import_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findFromHttp();
            }
        });
        //设置监听,弹出对话框
        phoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把那个电话详细过对话框显示出来
                Log.i(TAG, "setOnItemClickListener中的位置POS:" + position);
                if (view instanceof TextView) {
                    //如果是脚布局那个,不可以点击
                } else {
                    ContactInfo contactInfo = contactInfoList.get(position);
                    if (contactInfo != null) {
                        showConcactDialog(contactInfo);
                    }
                }

            }
        });

        //对listView设置滚动监听-->为了分页查询
        phoneListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             *监听着ListView的滑动状态改变。
             * 官方的有三种状态:
             * SCROLL_STATE_TOUCH_SCROLL：手指正拖着ListView滑动
             * SCROLL_STATE_FLING：ListView正自由滑动
             * SCROLL_STATE_IDLE：ListView滑动后静止
             * */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        //获取可视的条目
                        int lastVisiblePosition = phoneListView.getLastVisiblePosition();
                        //如果当前条目是最后一个条目,则需要查询更多的数据-->不需要减1愿意是因为footView在
                        if (lastVisiblePosition == contactInfoList.size()) {
                            dataPageNum++;
                            if (pageSize * dataPageNum >= dataTotalNum) {
                                Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_SHORT).show();
                            } else {
                                contactInfoList.addAll(DataSupport.limit(pageSize).offset(dataPageNum).
                                        find(ContactInfo.class));
                                //究竟需要不需要排序,这是一个问题
                                //Collections.sort(contactInfoList);

                                Log.i(TAG, "onScrollStateChanged中的contactInfoList大小" + contactInfoList.size()
                                        + "dataTotalNum:" + dataTotalNum);

                                contactAdapter.notifyDataSetChanged();
                            }
                            //dataTotalNum
                        }

                        break;
                }
            }

            /**
             * firstVisibleItem: 表示在屏幕中第一条显示的数据在adapter中的位置
             * visibleItemCount：则表示屏幕中最后一条数据在adapter中的数据，
             * totalItemCount则是在adapter中的总条数
             * */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });

    }

    /**
     * 显示电话详细信息对话框
     *
     * @param info 电话详细信息
     */
    private void showConcactDialog(final ContactInfo info) {
        ColorDialog dialog = new ColorDialog(getActivity());
        dialog.setColor("#8ECB54");
        dialog.setAnimationEnable(true);
        dialog.setTitle("详细信息:");
        dialog.setContentText("姓名:" + info.name+"\n"+ "电话:" + info.call+"\n"+
                "单位:" + info.property+ "\n"+"校区:" + info.campus+"\n"+
                "Tips:这里列出的电话是常用的,更多的电话请点击左上角访问广工办公电话网页进行" +
                "查看(太小的话可以放大的)...");
        dialog.setPositiveListener("I Kown", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(getActivity(), dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
                if (dialog!=null) {
                    dialog.dismiss();
                }
            }
        }).show();

    }


    //需要查询多少条数据-->为ListView作分页查找准备条件
    private static int dataPageNum = 1;//初始10天
    private static int dataTotalNum = 0;//数据库中共有多少条数据
    private static final int pageSize = 10;//固定每次查询10条

    /**
     * 从数据库中查询数据
     */
    private void findFromDb() {
        if (contactInfoList.size() == 0) {
            //如果电话集合没有数据,先去数据库查询一下
            Log.i("TAG", "进入findFromDb()中,查询之前数据长度是:" + contactInfoList.size());
            //看看数据库的数据量
            dataTotalNum = DataSupport.count(ContactInfo.class);
            contactInfoList = limit(pageSize).offset(dataPageNum).find(ContactInfo.class);
            Log.i("TAG", "进入findFromDb()中,查询之后数据长度是:" + contactInfoList.size());
            if (contactInfoList.size() > 0) {
                //显示数据-->隐藏不要的组件
                initView();
            }
        } else {
            //显示数据-->隐藏不要的组件
            initView();
        }

//        for (int i = 0; i < modelDatas.length; i++) {
//            ContactInfo data = new ContactInfo();
//
//            data.name=modelDatas[i];
//            data.url="http://www.baidu.com";
//            //data.setId(i);
//            data.setPinyin(HanziToPinyin.getPinYin(data.name));
//            contactInfoList.add(data);
//        }

        mFooterView.setText("目前有" + contactInfoList.size() + "位联系人    "
                + "数据库共有" + dataTotalNum + " 人");
        //       mFooterView.setText(contactInfoList.size() + "位联系人");
        //显示数据
        if (contactAdapter != null) {
            contactAdapter.notifyDataSetChanged();
        }

    }

    //是否是第一次进入该界面-->这里是指你在这个应用的时候
    private boolean isFirstEnterHere = true;

    /**
     * 显示数据-->隐藏不必要的组件
     */
    private void initView() {
        Log.i("TAG", "initview");
        Log.i("TAG", "进入initView()中,数据长度是:" + contactInfoList.size());
        if (contactInfoList.size() > 0) {
            //大于0才变为显示数据界面showLayout
            emptyLayout.setVisibility(View.GONE);
            showLayout.setVisibility(View.VISIBLE);
            if (isFirstEnterHere) {
                contactAdapter = new ContactAdapter(contactInfoList);
                isFirstEnterHere = false;
            }
            //设置一个数据适配器
            //    contactAdapter = new PhoneAdapter(phoneListView, contactInfoList);
            phoneListView.setAdapter(contactAdapter);

            if (contactAdapter != null) {
                contactAdapter.notifyDataSetChanged();
            }
        } else {
            //<=0才变为隐藏数据界面emptyLayout
            emptyLayout.setVisibility(View.VISIBLE);
            showLayout.setVisibility(View.GONE);
            if (contactAdapter != null) {
                contactAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_right_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == R.id.action_search) {
            //  SearchActivity.startSearchStatusActivity(getActivity());
           // Toast.makeText(getActivity(), "需要打开搜索按钮", Toast.LENGTH_SHORT).show();
            Snackbar.make(getView(),"支持缩放",Snackbar.LENGTH_SHORT).show();
            findCallFromNet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * webView网页加载Html显示全部电话
     */
    private void findCallFromNet() {
        if (NetStatusUtil.isConnected(MyApplication.getAppContext())) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mWebView = new WebView(MyApplication.getAppContext());
            mWebView.setLayoutParams(params);
            rootLayout.addView(mWebView);

            //声明WebSettings子类-->进行配置
            WebSettings webSettings = mWebView.getSettings();

            //设置自适应屏幕，两者合用
            webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
            webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

            //缩放操作
            webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
            webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
            webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

            //不使用缓存:
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            //需要加载的网页
            mWebView.loadUrl(GlobalValue.CONTACT_CALL_NET);

            //5.1以上默认禁止了https和http混用 这是开启
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }


            mWebView.setWebViewClient(new WebViewClient() {

                //设置不用系统浏览器打开,直接显示在当前Webview
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }


                //设置加载网页错误时候的处理
                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    switch (errorCode) {
                        case HttpStatus.SC_NOT_FOUND:
                            Toast.makeText(getActivity(), "网页不存在",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            });

            //设置WebChromeClient类
            mWebView.setWebChromeClient(new WebChromeClient() {

                //获取网站标题
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    System.out.println("标题在这里" + title);
                }

            });


        }

    }


    /**
     * 侧滑栏字母改变的时候-->划出去的时候默认在#那里
     *
     * @param s
     */
    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (contactAdapter != null) {
            position = contactAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            phoneListView.setSelection(position);
        } else if (s.contains("#")) {
            phoneListView.setSelection(0);
        }
    }

    /**
     * 监听EditText的文字变化
     */
    class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ArrayList<ContactInfo> temp = new ArrayList<>(contactInfoList);
            for (ContactInfo data : contactInfoList) {
                if (data.name.contains(s) || data.pinyin.contains(s)) {
                } else {
                    //去除不必要的元素
                    temp.remove(data);
                }
            }
            if (contactAdapter != null) {
                contactAdapter.refresh(temp);
                mFooterView.setText("符合条件的有" + temp.size() + "位联系人    "
                        + "总共有" + dataTotalNum + " 位人");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    /**
     * 目前是虚拟从网上获得数据-->创建模拟数据
     */
    private void findFromHttp() {

        CallCreate.callDataCreate(false, new CallBack() {
            @Override
            public void onStart() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                        ProgressDialogHelper.showProgressDialog(getActivity(), "正在载入...");
                        //contactInfoList = findAll(ContactInfo.class);
                        contactInfoList = limit(pageSize).offset(dataPageNum)
                                .find(ContactInfo.class);
                    }
                });
            }

            @Override
            public void onFinsh(String response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                        ProgressDialogHelper.closeProgressDialog();

                    }
                });
            }
        });

    }


    /**
     * 销毁时候移除webView以及销毁webView
     */
    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroyView();
    }

}
