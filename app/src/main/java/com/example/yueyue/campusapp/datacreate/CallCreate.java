package com.example.yueyue.campusapp.datacreate;

import com.example.yueyue.campusapp.models.ContactInfo;
import com.example.yueyue.campusapp.implement.CallBack;
import com.example.yueyue.campusapp.utils.HanziToPinyin;


/**
 * Created by yueyue on 2017/5/27.
 */

public class CallCreate {
    /**
     * 创建电话本-->不完整
     *
     * @param isNeed
     */
    public static void callDataCreate(boolean isNeed, final CallBack callBack) {
        //data.setPinyin(HanziToPinyin.getPinYin(data.name));
        if (isNeed) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (callBack != null) {
                        callBack.onStart();
                    }

                    ContactInfo info1 = new ContactInfo("党委办公室", "39322180", "大学城", "机关");
                    info1.setPinyin(HanziToPinyin.getPinYin(info1.name));
                    info1.save();
                    ContactInfo info2 = new ContactInfo("统战部党派科", "39322569", "大学城", "机关");
                    info2.setPinyin(HanziToPinyin.getPinYin(info2.name));
                    info2.save();
                    ContactInfo info3 = new ContactInfo("校长办公室", "39322320", "大学城", "机关");
                    info3.setPinyin(HanziToPinyin.getPinYin(info3.name));
                    info3.save();
                    ContactInfo info4 = new ContactInfo("校友工作及社会联络办公室", "39322316", "大学城", "机关");
                    info4.setPinyin(HanziToPinyin.getPinYin(info4.name));
                    info4.save();
                    ContactInfo info5 = new ContactInfo("纪委办公室、监察处", "39322690", "大学城", "机关");
                    info5.setPinyin(HanziToPinyin.getPinYin(info5.name));
                    info5.save();
                    ContactInfo info6 = new ContactInfo("组织部", "39318072", "大学城", "机关");
                    info6.setPinyin(HanziToPinyin.getPinYin(info6.name));
                    info6.save();
                    ContactInfo info7 = new ContactInfo("宣传部宣传文化科", "39322655", "大学城", "机关");
                    info7.setPinyin(HanziToPinyin.getPinYin(info7.name));
                    info7.save();
                    ContactInfo info8 = new ContactInfo("新闻中心", "39322655", "大学城", "机关");
                    info8.setPinyin(HanziToPinyin.getPinYin(info8.name));
                    info8.save();
                    ContactInfo info9 = new ContactInfo("工会办公室", "39322162", "大学城", "机关");
                    info9.setPinyin(HanziToPinyin.getPinYin(info9.name));
                    info9.save();
                    ContactInfo info10 = new ContactInfo("校报编辑部", "39322685", "大学城", "机关");
                    info10.setPinyin(HanziToPinyin.getPinYin(info10.name));
                    info10.save();
                    ContactInfo info11 = new ContactInfo("发展规划处", "39322729", "大学城", "机关");
                    info11.setPinyin(HanziToPinyin.getPinYin(info11.name));
                    info11.save();
                    ContactInfo info12 = new ContactInfo("教务处综合科", "39322771", "大学城", "机关");
                    info12.setPinyin(HanziToPinyin.getPinYin(info12.name));
                    info12.save();
                    ContactInfo info13 = new ContactInfo("评估中心", "39322771", "大学城", "机关");
                    info13.setPinyin(HanziToPinyin.getPinYin(info13.name));
                    info13.save();
                    ContactInfo info14 = new ContactInfo("招生办公室", "39322680", "大学城", "机关");
                    info14.setPinyin(HanziToPinyin.getPinYin(info14.name));
                    info14.save();
                    ContactInfo info15 = new ContactInfo("科技处", "39322717", "大学城", "机关");
                    info15.setPinyin(HanziToPinyin.getPinYin(info15.name));
                    info15.save();
                    ContactInfo info16 = new ContactInfo("学生处学生管理科", "39322608", "大学城", "机关");
                    info16.setPinyin(HanziToPinyin.getPinYin(info16.name));
                    info16.save();
                    ContactInfo info17 = new ContactInfo("学生就业指导中心", "39322612", "大学城", "机关");
                    info17.setPinyin(HanziToPinyin.getPinYin(info17.name));
                    info17.save();
                    ContactInfo info18 = new ContactInfo("国防生管理办公室", "39322617", "大学城", "机关");
                    info18.setPinyin(HanziToPinyin.getPinYin(info18.name));
                    info18.save();
                    ContactInfo info19 = new ContactInfo("研究生院", "39322722", "大学城", "机关");
                    info19.setPinyin(HanziToPinyin.getPinYin(info19.name));
                    info19.save();
                    ContactInfo info20 = new ContactInfo("人事处综合科", "39322208", "大学城", "机关");
                    info20.setPinyin(HanziToPinyin.getPinYin(info20.name));
                    info20.save();
                    ContactInfo info21 = new ContactInfo("计划生育办公室", "39322651", "大学城", "机关");
                    info21.setPinyin(HanziToPinyin.getPinYin(info21.name));
                    info21.save();
                    ContactInfo info22 = new ContactInfo("财务处小总机", "39322745", "大学城", "机关");
                    info22.setPinyin(HanziToPinyin.getPinYin(info22.name));
                    info22.save();
                    ContactInfo info23 = new ContactInfo("国有资产管理办公室", "39322758", "大学城", "机关");
                    info23.setPinyin(HanziToPinyin.getPinYin(info23.name));
                    info23.save();
                    ContactInfo info24 = new ContactInfo("审计处资料室", "39322755", "大学城", "机关");
                    info24.setPinyin(HanziToPinyin.getPinYin(info24.name));
                    info24.save();
                    ContactInfo info25 = new ContactInfo("保卫处值班报警电话", "39322110", "大学城", "机关");
                    info25.setPinyin(HanziToPinyin.getPinYin(info25.name));
                    info25.save();
                    ContactInfo info26 = new ContactInfo("机电工程学院办公室", "39322212", "大学城", "学院");
                    info26.setPinyin(HanziToPinyin.getPinYin(info26.name));
                    info26.save();
                    ContactInfo info27 = new ContactInfo("自动化学院办公室", "39322552", "大学城", "学院");
                    info27.setPinyin(HanziToPinyin.getPinYin(info27.name));
                    info27.save();
                    ContactInfo info28 = new ContactInfo("轻工化工学院办公室", "39322231", "大学城", "学院");
                    info28.setPinyin(HanziToPinyin.getPinYin(info28.name));
                    info28.save();
                    ContactInfo info29 = new ContactInfo("信息工程学院办公室", "39322527", "大学城", "学院");
                    info29.setPinyin(HanziToPinyin.getPinYin(info29.name));
                    info29.save();
                    ContactInfo info30 = new ContactInfo("土木与交通工程学院办公室", "39322527", "大学城", "学院");
                    info30.setPinyin(HanziToPinyin.getPinYin(info30.name));
                    info30.save();
                    ContactInfo info31 = new ContactInfo("计算机学院办公室", "39322279", "大学城", "学院");
                    info31.setPinyin(HanziToPinyin.getPinYin(info31.name));
                    info31.save();
                    ContactInfo info32 = new ContactInfo("材料与能源学院办公室", "39322570", "大学城", "学院");
                    info32.setPinyin(HanziToPinyin.getPinYin(info32.name));
                    info32.save();
                    ContactInfo info33 = new ContactInfo("外国语学院办公室", "39322187", "大学城", "学院");
                    info33.setPinyin(HanziToPinyin.getPinYin(info33.name));
                    info33.save();
                    ContactInfo info34 = new ContactInfo("物理与光电工程学院办公室", "39322265", "大学城", "学院");
                    info34.setPinyin(HanziToPinyin.getPinYin(info34.name));
                    info34.save();
                    ContactInfo info35 = new ContactInfo("艺术与设计学院办公室", "37628060", "东风路", "学院");
                    info35.setPinyin(HanziToPinyin.getPinYin(info35.name));
                    info35.save();
                    ContactInfo info36 = new ContactInfo("政法学院办公室", "37626602", "东风路", "学院");
                    info36.setPinyin(HanziToPinyin.getPinYin(info36.name));
                    info36.save();
                    ContactInfo info37 = new ContactInfo("建筑与城市规划学院办公室", "37627791", "东风路", "学院");
                    info37.setPinyin(HanziToPinyin.getPinYin(info37.name));
                    info37.save();
                    ContactInfo info38 = new ContactInfo("建筑设计院", "37628060", "东风路", "学院");
                    info38.setPinyin(HanziToPinyin.getPinYin(info38.name));
                    info38.save();
                    ContactInfo info39 = new ContactInfo("继续教育学院办公室", "37627789", "东风路", "学院");
                    info39.setPinyin(HanziToPinyin.getPinYin(info39.name));
                    info39.save();
                    ContactInfo info40 = new ContactInfo("管理学院办公室", "87080366", "龙洞", "学院");
                    info40.setPinyin(HanziToPinyin.getPinYin(info40.name));
                    info40.save();
                    ContactInfo info41 = new ContactInfo("应用数学学院办公室", "87084696", "龙洞", "学院");
                    info41.setPinyin(HanziToPinyin.getPinYin(info41.name));
                    info41.save();
                    ContactInfo info42 = new ContactInfo("政法学院", "87083100", "龙洞", "学院");
                    info42.setPinyin(HanziToPinyin.getPinYin(info42.name));
                    info42.save();
                    ContactInfo info43 = new ContactInfo("经济与贸易学院办公室", "87080256", "龙洞", "学院");
                    info43.setPinyin(HanziToPinyin.getPinYin(info43.name));
                    info43.save();
                    ContactInfo info44 = new ContactInfo("管理学院办公室", "87080366", "龙洞", "学院");
                    info44.setPinyin(HanziToPinyin.getPinYin(info44.name));
                    info44.save();

                    if (callBack != null) {
                        callBack.onFinsh("");
                    }

                }
            }).start();
        } else {
            if (callBack != null) {
                callBack.onFinsh("");
            }
        }


    }
}
