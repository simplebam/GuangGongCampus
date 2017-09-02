package com.example.yueyue.campusapp.utils;

import com.example.yueyue.campusapp.models.Score;

import java.util.List;

/**
 * 绩点计算的工具类
 * Created by yueyue on 2017/6/6.
 */

public class ScorePointutil {

    /**
     * 获得绩点
     *
     * @return
     */
    public static float getScorePoint(List<Score> scores) {
        float totalScore = 0;//总绩点
        float totalCredit = 0;//总学分
        if (scores != null && scores.size() > 0) {
            for (int i = 0; i < scores.size(); i++) {

                Score score = scores.get(i);
                totalScore += score.point * score.credit;
                totalCredit += score.credit;
            }
        }
        return totalScore / totalCredit;
    }
}
