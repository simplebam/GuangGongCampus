package com.example.yueyue.campusapp.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Zj on 2016/1/6.
 */
public class AlertDialogHelper {

    public static void showAlertDialog(Context context, String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("确定", null)
                .setMessage(message)
                .show();
    }
}
