package com.example.yueyue.campusapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.implement.LoginCallback;
import com.example.yueyue.campusapp.implement.OkHttpCallback;
import com.example.yueyue.campusapp.utils.GlobalValue;
import com.example.yueyue.campusapp.utils.HttpUtil;
import com.example.yueyue.campusapp.utils.SpUtils;
import com.example.yueyue.campusapp.widget.AlertDialogHelper;
import com.example.yueyue.campusapp.widget.ProgressDialogHelper;

import java.io.IOException;

import cn.refactor.lib.colordialog.ColorDialog;
import okhttp3.Call;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText et_login_user;
    private EditText et_login_pwd;
    private EditText et_safecode;
    private ImageView iv_showcode;
    private Button btn_login_ok;
    private TextView btn_login_forgot;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv_showcode.setImageBitmap(bitmap);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
        setContentView(R.layout.activity_login);
        initView();
        //判断是否登陆过
        isCookieGet();
        //获取验证码,教程:http://www.jianshu.com/p/53e8e0eb36b8
        getSafeCode();
    }

    /**
     * 判断之前是否登陆过
     */
    private void isCookieGet() {
        //"cookie_OK"  "cookie" "account"  "pwd"
        if (SpUtils.getBoolean(this, GlobalValue.COOKIE_OK, false)) {
//            HttpUtil.cookie = SpUtils.getString(this, GlobalValue.COOKIE, "");
            HttpUtil.account = SpUtils.getString(this, GlobalValue.ACCOUNT, "");
            HttpUtil.pwd = SpUtils.getString(this, GlobalValue.PWD, "");

            if (TextUtils.isEmpty(HttpUtil.account) || TextUtils.isEmpty(HttpUtil.pwd)) {
                //账号密码都是空的话
                return;
            }

            login();

        }
    }


    private void getSafeCode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.getLoginCode(new OkHttpCallback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "获取验证码失败", Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            try {
                                byte[] byte_image = response.body().bytes();

                                //使用BitmapFactory工厂，把字节数组转化为bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(byte_image, 0,
                                        byte_image.length);
                                Message msg = Message.obtain();
                                msg.what = 1;
                                msg.obj = bitmap;
                                mHandler.sendMessage(msg);
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(), "获取验证码失败", Toast.LENGTH_SHORT);
                                return;
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "获取验证码失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化组件,并且设置监听
     */
    private void initView() {
        et_login_user = (EditText) findViewById(R.id.et_login_user);
        et_login_pwd = (EditText) findViewById(R.id.et_login_pwd);
        et_safecode = (EditText) findViewById(R.id.et_safe_code);
        iv_showcode = (ImageView) findViewById(R.id.iv_showcode);
        btn_login_ok = (Button) findViewById(R.id.btn_login_ok);
        btn_login_forgot = (TextView) findViewById(R.id.btn_login_forgot);


        //为组件设置监听
        btn_login_ok.setOnClickListener(this);
        btn_login_forgot.setOnClickListener(this);
        iv_showcode.setOnClickListener(this);

        //给et_login_id跟et_login_pwd设置空抖动效果
        et_login_user.addTextChangedListener(new MyTextWatcher(0));
        et_login_pwd.addTextChangedListener(new MyTextWatcher(1));

        //给et_login_id跟et_login_pwd设置默认存储的字符串以及光标位置

        if (!TextUtils.isEmpty(HttpUtil.account)) {
            et_login_user.setText(HttpUtil.account);
            et_login_user.setSelection(HttpUtil.account.length());
        }

        if (!TextUtils.isEmpty(HttpUtil.pwd)) {
            et_login_pwd.setText(HttpUtil.pwd);
            et_login_pwd.setSelection(HttpUtil.pwd.length());
        }


        iv_showcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //换验证码
                getSafeCode();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_ok:
                //设置登录按钮监听
                setLoginBtnListener();
                break;
            case R.id.btn_login_forgot:
                showWarmPrompt();
                break;
            case R.id.iv_showcode:
                showWarmPrompt();
                break;
        }
    }

    /**
     * 温馨提示对话框
     */
    private void showWarmPrompt() {
        ColorDialog dialog = new ColorDialog(this);
        dialog.setColor("#8ECB54");
        dialog.setAnimationEnable(true);
        dialog.setTitle("WarmTips:");
        dialog.setContentText("1.忘记密码功能暂时不开放,需要改密码的请登陆网页版进行更改");
        dialog.setPositiveListener("I Know", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                Toast.makeText(LoginActivity.this, dialog.getPositiveText().toString(), Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }).show();

    }

    private String account = "";
    private String pwd = "";
    private String saveCode = "";

    /**
     * 设置登录按钮监听
     */
    private void setLoginBtnListener() {
        account = et_login_user.getText().toString().trim();
        pwd = et_login_pwd.getText().toString().trim();
        saveCode = et_safecode.getText().toString().trim();
        saveUserAndPass();
    }

    private void saveUserAndPass() {

        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(pwd)
                || TextUtils.isEmpty(saveCode)) {
            AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒",
                    "请填写完整的学号,密码和验证码");
        } else {
            //将账号密码验证码都转过去
            HttpUtil.account = account;
            HttpUtil.pwd = pwd;
            HttpUtil.saveCode = saveCode;
            login();
        }
    }


    private void login() {
        HttpUtil.login(new LoginCallback() {

            @Override
            public void beforeStart() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.showProgressDialog(LoginActivity.this, "正在登陆...");
                    }
                });
            }

            @Override
            public void onLoginFinshed() {

                saveCookieAndStartMainActivity();
            }

            @Override
            public void onError(Exception e) {
                String msg = e.getMessage();
                getSafeCode();
                if (msg.contains("您的帐号或密码不正确")) {
                    msg="请检查一下你的账号或者密码";
                } else if (msg.contains("连接已过期")) {
                    msg="验证码输入错误";
                }

                final String finalMsg = msg;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressDialogHelper.closeProgressDialog();
                        AlertDialogHelper.showAlertDialog(LoginActivity.this, "善意的提醒",
                                finalMsg);
                    }
                });

            }

        });


    }

    private void saveCookieAndStartMainActivity() {
        saveCookie();
        ProgressDialogHelper.closeProgressDialog();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 保存Cookie
     */
    private void saveCookie() {

//        SpUtils.putBoolean(LoginActivity.this, GlobalValue.COOKIE_OK, true);
//        SpUtils.putString(LoginActivity.this, GlobalValue.COOKIE, HttpUtil.cookie);
        SpUtils.putString(LoginActivity.this, GlobalValue.ACCOUNT, HttpUtil.account);
        SpUtils.putString(LoginActivity.this, GlobalValue.PWD, HttpUtil.pwd);
    }


    /**
     * 监听EditText内容变化:
     * http://blog.csdn.net/qfanmingyiq/article/details/53443705
     */
    class MyTextWatcher implements TextWatcher {
        private int type;
        private CharSequence temp;

        public MyTextWatcher(int type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() == 0) {
                Animation anim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                if (type == 0) {
                    et_login_user.startAnimation(anim);
                    Toast.makeText(LoginActivity.this, "学号不可以为空", Toast.LENGTH_SHORT).show();
                } else if (type == 1) {
                    et_login_pwd.startAnimation(anim);
                    Toast.makeText(LoginActivity.this, "密码不可以为空", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
