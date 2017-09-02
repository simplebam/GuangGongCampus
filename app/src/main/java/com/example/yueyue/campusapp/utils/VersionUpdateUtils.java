package com.example.yueyue.campusapp.utils;

/**
 * Created by yueyue on 2017/4/21.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.yueyue.campusapp.models.VersionEntity;
import com.example.yueyue.campusapp.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

/**
 * 版本更新工具类
 */
public class VersionUpdateUtils {


    private static final String TAG = VersionUpdateUtils.class.getSimpleName();
    private static final int MESSAGE_INSTALL_APK = 100;//安装APK
    private static final int MESSAGE_NET_ERROR = 101;//网络异常消息
    private static final int MESSAGE_IO_ERROR = 102;//io异常消息
    private static final int MESSAGE_JSON_ERROR = 103;//json异常消息
    private static final int MESSAGE_SHOW_DIALOG = 104;//显示对话框下载消息
    private static final int MESSAGE_ENTERLOGIN = 105;//进入HomeActivity界面
    /**
     * 用于更新UI
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_INSTALL_APK:
                    ApkUtils.installApk(mActivity);
                    break;
                case MESSAGE_NET_ERROR:
                    Toast.makeText(mActivity, "网络请求错误", Toast.LENGTH_SHORT).show();
                    enterLogin();
                    break;
                case MESSAGE_IO_ERROR:
                    Toast.makeText(mActivity, "网络io错误", Toast.LENGTH_SHORT).show();
                    enterLogin();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(mActivity, "json数据错误", Toast.LENGTH_SHORT).show();
                    enterLogin();
                    break;
                case MESSAGE_SHOW_DIALOG:
                    showUpdateDialog(mVersionEntity);
                    break;
                case MESSAGE_ENTERLOGIN:
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();//销毁当前的activity
                    break;
                default:
                    throw new RuntimeException("SplashActivity的消息机制出了异常");
            }

        }
    };

    /**
     * 弹出更新提示的对话框
     *
     * @param versionEntity
     */
    private void showUpdateDialog(final VersionEntity versionEntity) {
        new AlertDialog.Builder(mActivity)
                .setTitle("检测到新版本号:" + versionEntity.versionCode)
                .setCancelable(false)
                .setMessage(versionEntity.des)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadNewApk(versionEntity.apkUrl);
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        enterLogin();
                    }
                }).show();

    }

    /**
     * 构造方法:需要传入VersionEntity以及activity
     */
    public VersionUpdateUtils(String version, Activity activity) {
        this.mVersion = version;
        this.mActivity = activity;
        mVersionEntity = new VersionEntity();
    }
        // TODO: 2017/4/22 3.0版本到时下载等需要友好提示一下用户是否在移动网络的情况下下载apk安装包

    /**
     * 联网下载Apk
     *
     * @param apkUrl
     */
    private void downloadNewApk(String apkUrl) {
        Log.i(TAG, "downloadNew()方法的apkUrl" + apkUrl);

        //apkUrl=http://192.168.207.2:8080/campusapp.apk
       // final String filePath = GlobalValue.UPDATE_APK_PATH;
        String[] split = apkUrl.split("/");
        String fileName=null;
        if (split.length>0) {
            //说明切割成功
            fileName=split[split.length-1];
        } else {
            fileName="default_campusapp.apk";
        }

        final String filePath=getDiskCacheDirPath(mActivity,fileName);

        Log.i(TAG, "downloadNewApk()方法的filePath:" + filePath);
        File targetFile = new File(filePath);
        //判断一下需要安装的版本是否已经存在了
        if (!targetFile.exists()) {
            RequestParams params = new RequestParams(apkUrl);
            params.setSaveFilePath(filePath);//设置文件的下载位置
            mProgressDialog = new ProgressDialog(mActivity);

            RequestParams requestParams = new RequestParams(apkUrl);
            requestParams.setSaveFilePath(filePath);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onWaiting() {
                }

                @Override
                public void onStarted() {
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setMessage("亲，努力下载中。。。");
                    mProgressDialog.show();
                    mProgressDialog.setMax((int) total);
                    mProgressDialog.setProgress((int) current);
                }

                @Override
                public void onSuccess(File result) {
                    Toast.makeText(mActivity, "下载成功", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    //安装APK
                    //  ApkUtils.installApk(mActivity);
                    //   enterLogin();
                    mHandler.sendEmptyMessage(MESSAGE_INSTALL_APK);//安装APK
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                    Log.i(TAG,"ProgressCallback中的onError方法走了,检查联网失败和SD卡");
                    Toast.makeText(mActivity, "下载失败，请检查网络和SD卡", Toast.LENGTH_SHORT).show();
                    //  mProgressDialog.setMessage("下载失败...");
                    mProgressDialog.dismiss();
                    mHandler.sendEmptyMessage(MESSAGE_NET_ERROR);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    // 用户主动取消下载,那么就应该进入Home界面
                    mProgressDialog.dismiss();
                    mHandler.sendEmptyMessage(MESSAGE_ENTERLOGIN);
                }

                @Override
                public void onFinished() {
                    //   mHandler.removeCallbacksAndMessages(null);//防止内存溢出

                }
            });
        } else {
            //如果已经下载完毕了,那么直接进入安装界面
            ApkUtils.installApk(mActivity);
        }





    }

    /**
     * 设置存储的文件路径位置,外部Cache-->内部Cache
     * @param context  上下文环境
     * @param uniqueName  数字名字
     * @return  存储的文件路径
     */
    public String getDiskCacheDirPath(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath + File.separator + uniqueName;
    }


    /**
     * 进入主界面
     */
    public void enterLogin() {

        mHandler.sendEmptyMessageDelayed(MESSAGE_ENTERLOGIN, 1000);

    }

    private String mVersion;//本地版本号
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private VersionEntity mVersionEntity;


    /**
     * 联网下载apk进行安装
     */
    public void getCloudVersion() {
        //子线程获得服务器返回的数据
        RequestParams params = new RequestParams(GlobalValue.UPDATE_APK_URL);
        //RequestParams params = new RequestParams("http://192.168.207.2:8080/campusupdate.json");

        //设置链接的时间
        // params.setConnectTimeout(5000);
        //设置读的时间
        //params.setReadTimeout(8000);

        x.http().get(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //获得服务器json数据的相关信息
                    mVersionEntity.versionCode = result.getString("code");
                    mVersionEntity.des = result.getString("des");
                    mVersionEntity.apkUrl = result.getString("apkurl");

                    if (!mVersion.equals(mVersionEntity.versionCode)) {
                        Log.i(TAG, "getCloudVersion()当前版本:" + mVersion + "  ,更新的版本:" + mVersionEntity.versionCode);
                        Log.i(TAG, "getCloudVersion()当前下载链接:" + mVersionEntity.apkUrl);
                        //版本号不一致的话,需要更新

                        mHandler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG, "getCloudVersion中的Json数据发生错误!!!");
                    //发送json数据问题的消息
                    mHandler.sendEmptyMessage(MESSAGE_JSON_ERROR);
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG,"CommonCallback中的onError方法走了"+ex.getMessage());
                //走这方法时候查看一下是否加了联网权限
                mHandler.sendEmptyMessage(MESSAGE_NET_ERROR);//发送联网错误的消息

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    /**
     * 移除hanlder所有的消息以及回掉
     */
    public void removeHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }


}
