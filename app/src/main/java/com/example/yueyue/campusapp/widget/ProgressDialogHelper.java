package com.example.yueyue.campusapp.widget;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2015/10/26.
 */
public class ProgressDialogHelper {

    private static ProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
