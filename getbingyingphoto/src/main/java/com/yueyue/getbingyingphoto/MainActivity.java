package com.yueyue.getbingyingphoto;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yueyue.getbingyingphoto.enity.BingYingPic;
import com.yueyue.getbingyingphoto.utils.Stream2String;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnGet;
    private ImageView mIvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mBtnGet = (Button) findViewById(R.id.btn_get);
        mIvShow = (ImageView) findViewById(R.id.iv_show);

        mBtnGet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                getDailyBingPic();
                break;
        }
    }


    private void getDailyBingPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
                String content = Stream2String.loadDataByGet(url, null);
                BingYingPic bingYingPic = new Gson().fromJson(content, BingYingPic.class);
                System.out.println(bingYingPic + "-------------");
                System.out.println(bingYingPic.images.get(0).bingBasePicUrl + "-------------");
                System.out.println(bingYingPic.images.get(0).endDate + "-------------");
                updateImage(Constants.BING_YING_URL + bingYingPic.images.get(0).bingBasePicUrl);
            }
        }).start();

    }


    private void getDailyBingPic1() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url).build();
                    Response response = client.newCall(request).execute();
                    String content = response.body().string();
                    //response.body().string() 不容许同时出现两次
                    //   System.out.println(response.body().string() + "-------------" + "okhtpp");

                    BingYingPic bingYingPic = new Gson().fromJson(content, BingYingPic.class);
                    System.out.println(bingYingPic + "-------------");
                    System.out.println(bingYingPic.images.get(0).bingBasePicUrl + "-------------");
                    System.out.println(bingYingPic.images.get(0).endDate + "-------------");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void updateImage(final String imageUrl) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Android图片加载框架最全解析（八），带你全面了解Glide 4的用法 - 郭霖的专栏 - CSDN博客
                //           http://blog.csdn.net/guolin_blog/article/details/78582548
                Glide.with(MainActivity.this).load(imageUrl).into(mIvShow);
            }
        });
    }
}
