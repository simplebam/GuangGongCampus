package com.example.yueyue.campusapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.yueyue.campusapp.R;
import com.example.yueyue.campusapp.db.Db;
import com.example.yueyue.campusapp.utils.DateUtil;
import com.example.yueyue.campusapp.utils.GlobalValue;
import com.example.yueyue.campusapp.widget.PickerView;

import java.util.ArrayList;
import java.util.List;

public class WheelActivity extends AppCompatActivity {
    private PickerView pickerView;
    private List<String> mDatas;
    private TextView tv_click;

    private static int num = DateUtil.getcurrentWeek();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        pickerView = (PickerView) findViewById(R.id.pickerview);
        tv_click = (TextView) findViewById(R.id.tv_click);
        mDatas = new ArrayList<>();
        int maxWeek = Db.getDbCourseMaxWeek();
        for (int i = 1; i <= maxWeek; i++) {
            mDatas.add(i + "");
        }
        pickerView.setData(mDatas);
        pickerView.setSelected(num - 1);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                num = Integer.parseInt(text);
            }
        });

        tv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(GlobalValue.WHEEL_SELECT_CURR_WEEK, num);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

}
