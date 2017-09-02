package com.example.yueyue.campusapp.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Zj on 2016/1/18.
 */
public class SharedPreferencesHelper {

    public static SharedPreferences getSharePreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


}
