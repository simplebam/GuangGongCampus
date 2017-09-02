>自制Android校园APP涉及的内容:
1.课程查询(本学校新教务系统的查询全部课程是以html格式返回的,查询一周的课程是以json数据返回的)
2.个人信息查询-->这个是以html格式返回的
3.绩点查询(成绩查询+绩点换算)
4.电话查询
5.文档下载(暂时没有完成)
6.活动申请流程图(暂时没有完成)
7.设置界面(暂时还没有做)

首先呢?Android校园APP网上很多,但都是比较老旧的代码或者涉及个人隐私太多,所以即使一个很简单的校园APP都很难找到自己喜欢的,之后偶然在一个GitHub看到[zyks的科院助手APP](https://github.com/threezj/KyZs)(一眼就喜欢了,基于Material Design风格的),涉及的知识点也是很多很多,在这个期间真的学会了很多,在此需要感谢很多的源码博主,因为在做这个校园APP的时候,我查询了n+篇博客文章以及Github上面的代码.

>注意:
>1.如果你是广东工业大学学生:请看[simplebam/GuangGongCampus: 广工校园通APP](https://github.com/simplebam/GuangGongCampus) 
>2.如果你不是的话,建议看:[threezj/KyZs: 集校园生活于一体的app](https://github.com/threezj/KyZs) 
>3.APP最理想的版本:[仿南航app开发日记1\]开篇-总体布局分析 - 伯兰的博客 - 博客频道 - CSDN.NET](http://blog.csdn.net/supervictim/article/details/53856827) 

PS:我是仿照kyzs科院APP制作的,我的广工版的APP更为准确是一个非标准的APP,因为本校的新教务系统不涉及验证码问题,所以建议非本校学生看kyzs的APP源码


### 正文开始
#### 登陆
* 搞清楚Cookie的作用先:
   得闲就去看看《图解HTTP》这本书(**网盘搜索就有影印版的观看,不喜欢就去图灵社区买正版pdf观看)+这篇博客:[什么是Cookie？Cookie有什么作用？ - 杰克帝.NET - 博客园](http://www.cnblogs.com/Dlonghow/archive/2008/07/10/1240069.html) 
*  登陆界面还可以这样设计:[Android应用开发-学生信息管理系统 - 许佳佳的博客 - 博客频道 - CSDN.NET](http://blog.csdn.net/double2hao/article/details/52641074) 
![这里写图片描述](http://img.blog.csdn.net/20170611112940733?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

PS:这里加入了一个EditText空的时候会抖动的效果
#### MainActivity
![这里写图片描述](http://img.blog.csdn.net/20170611113630493?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

这里的作用就是一个容器(主要存在Fragment)+一个导航栏

#### **课程界面**
*  zyks使用的是ListView展示,我选择的是仿超级课程表的方式做的,地址:https://github.com/WHuaJian/CourseTable
   ![这里写图片描述](http://img.blog.csdn.net/20170611113336863?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
   
   ![这里写图片描述](http://img.blog.csdn.net/20170611113203720?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

使用的第三方库类:[andyxialm/ColorDialog](https://github.com/andyxialm/ColorDialog)

#### **绩点查询界面**
![这里写图片描述](http://img.blog.csdn.net/20170611113842104?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

知识点:
1.[Java获取必应每日一图教程 - it菜鸟的飞行梦 - 博客频道 - CSDN.NET](http://blog.csdn.net/simplebam/article/details/72819547) 
2.绩点计算看自己学校哈,我个人建议最近写一个工具类出来搞定就好


#### **个人信息界面**
![这里写图片描述](http://img.blog.csdn.net/20170611114242029?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2ltcGxlYmFt/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


#### **电话查询界面**
这里引用了快速索引那个界面:[kymjs/Contacts: Android联系人列表界面](https://github.com/kymjs/Contacts) 


#### **设置界面**
参考这个的:[leonHua/LSettingView: 非常常用的设置界面条目 very useful setting item](https://github.com/leonHua/LSettingView)


####**结语**
最后:建议大家遇到困难的时候可以先查询一下博客,别老是指望别人可以帮助你多少,要懂得利用百度以及google,你做完这个以后,可以提升很快,不需要借账号使用的,建议看别人应用源码的时候可以看一下AS里面的UI渲染就好,如果你觉得这个要求太高了,可以先搞一个安全卫士练练手

APP最理想的版本:
[仿南航app开发日记1\]开篇-总体布局分析 - 伯兰的博客 - 博客频道 - CSDN.NET](http://blog.csdn.net/supervictim/article/details/53856827) 

Git好文章:
[或许是介绍Android Studio使用Git最详细的文章 - 温斯渤 - 博客园 ](http://www.cnblogs.com/ghylzwsb/archive/2017/03/12/GitOnAS.html)