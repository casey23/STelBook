# 安全通讯录
> 基于Android的通讯录备份/恢复系统，学校的中期设计选题

## 主要功能

- 备份/恢复通讯录
 
   1.备份:读取用户通讯录备份通讯录到[云端][1](Bmob云)，用户需使用用户名以及密码确认身份(MD5加密以后上传)，采用**版本库**的备份形式，每一次备份都保存为一个版本，在通讯录还原的时候**可以选择指定版本还原**，类似于git的每一次commit。
 
   2.恢复:使用**时光轴显示用户备份的历史记录**，用户点击相应版本库，显示该版本详细的联系人信息，用户可以直接点击具体的联系人发送短信或直接拨打电话或者将该版本**全部或者部分**同步到本地。

   3.用户个人设置:设置用户名和密码(头像上传1.0版本不可使用)

- 软件截图

  1.登陆注册界面
 
  ![登陆界面][2]
  
  ![注册界面][3]
  
  2.软件主界面
主功能界面包括上传到云端，以及备份到本地，点击上传到云端即可备份联系人姓名和手机号到云端，点击还原到本地即可显示备份版本

![软件主界面][4]
侧边栏

侧边栏菜单功能项包括密码修改，注销登陆，退出，关于我，以及意见反馈等功能。

![侧边菜单栏][5]

使用时光轴形式展示版本信息，点击相应版本即可进入详细查看页面，长按相应版本即可删除/还原相应版本库。

![版本信息界面][6]

![版本信息页面功能项选择][7]

点击相应版本进入联系人界面，点击相应联系人可以直接拔打电话，发送短信，以及在相应版本库中删除联系人，也可以选择直接还原单个联系人

![联系人详细信息界面][8]

- 版本信息

#### 1.0 V
大致完成整体功能，下一版本优化方向:个人信息中支持修改头像，主界面背景支持换肤，联系人详情界面支持搜索以及侧边索引。


 - 关于我
[我的网址][9]


  [1]: http://www.bmob.cn/
  [2]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/Screenshot_2016-07-19-22-31-13_com.jay.stelbook.png?raw=true
  [3]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/Screenshot_2016-07-19-22-31-38_com.jay.stelbook.png?raw=true
  [4]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/Screenshot_2016-07-19-22-31-38_com.jay.stelbook.png?raw=true
  [5]: ./images/1468976977020.jpg "1468976977020.jpg"
  [6]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/Screenshot_2016-07-19-22-31-52_com.jay.stelbook.png?raw=true
  [7]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/Screenshot_2016-07-19-22-31-58_com.jay.stelbook.png?raw=true
  [8]: https://github.com/CB2Git/ImageBed/blob/master/STelBook/07.png?raw=true
  [9]: http://www.27house.cn