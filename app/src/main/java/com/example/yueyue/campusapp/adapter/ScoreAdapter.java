package com.example.yueyue.campusapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yueyue.campusapp.models.Score;
import com.example.yueyue.campusapp.R;

import java.util.List;

/**
 * Created by yueyue on 2017/6/5.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {


    private static final String TAG=ScoreAdapter.class.getSimpleName();
    private final List<Score> scores;
    //默认显示全部的  0显示全部  非0显示学期
    private  int type=1;//用于查看是否需要显示题目(显示全部时候需要)
    private ScoreAdapter.ViewHolder preHolder;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_semester_name;//显示题目-->2016春季
        TextView tv_course_name;//显示课程名称-->21世纪高新技术
        TextView tv_actual_score;//显示课程的成绩-->81分
        TextView tv_category;//显示课程性质-->公共选修课
        TextView tv_point;//显示绩点-->1.5
        View view_line;//底部的划线

        public ViewHolder(View itemView) {
            super(itemView);
            tv_semester_name = (TextView) itemView.findViewById(R.id.tv_semester_name);
            tv_course_name = (TextView) itemView.findViewById(R.id.tv_course_name);
            tv_actual_score = (TextView) itemView.findViewById(R.id.tv_actual_score);
            tv_category = (TextView) itemView.findViewById(R.id.tv_category);
            tv_point = (TextView) itemView.findViewById(R.id.tv_point);
            view_line = itemView.findViewById(R.id.view_line);

        }
    }

    public ScoreAdapter(List<Score> scores, int type) {
        this.scores = scores;
        this.type = type;
    }

    @Override
    public ScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score_adapter_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ScoreAdapter.ViewHolder holder, int position) {

        Score score = scores.get(position);
        if (type == 0) {
            //需要显示全部
            holder.view_line.setVisibility(View.GONE);
            if (position == 0) {
                holder.tv_semester_name.setVisibility(View.VISIBLE);
            } else {
                if (preHolder.tv_semester_name.equals(holder.tv_semester_name)) {
                    holder.tv_semester_name.setVisibility(View.GONE);
                } else {
                    holder.tv_semester_name.setVisibility(View.VISIBLE);
                }
            }


        } else {
            //只需要显示学年学期-->题目不需要显示
            holder.tv_semester_name.setVisibility(View.GONE);
            holder.view_line.setVisibility(View.VISIBLE);

        }

        holder.tv_course_name.setText(score.courseName);//显示课程名称-->21世纪高新技术
        holder.tv_actual_score.setText(score.actualScore);//显示课程的成绩-->81分
        holder.tv_category.setText(score.category);//显示课程性质-->公共选修课
        holder.tv_point.setText(score.point + "");//显示绩点-->1.5
        preHolder = holder;
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
