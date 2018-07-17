# 项目总结：基于视觉的自动线束安装

标签（空格分隔）： 

---

Android4opencv & bluetooth

> 参考博客：
[【OpenCV入门教程之十一】 形态学图像处理（二）：开运算、闭运算、形态学梯度、顶帽、黑帽合辑](https://blog.csdn.net/poem_qianmo/article/details/24599073)
[【OpenCV入门教程之十四】OpenCV霍夫变换：霍夫线变换，霍夫圆变换合辑](https://blog.csdn.net/poem_qianmo/article/details/26977557)

## 项目简介
+ 汽车加工生产线中一个自动化线束安装的项目
 + 大插孔插头安装
 + 圆孔插件安装
 + 微型插孔插头安装
+ 如下图所示
![image_1c8atsubbe4n1hvu1e92lgfhr09.png-42.8kB][1] ![image_1c8attqlvf041ssh199tdt6umam.png-61.8kB][2]

# 算法部分
## 相机标定
+ 张友正相机标定算法
+ 理论原理？？？
+ 标定结果对精度的影响

## 测距算法思路
![image_1c8auhgvp1a4e1ahc16tk154njdo13.png-106.1kB][3]

+ 如何推导？？？
+ 霍夫圆检测的算法
+ 如何检测矩形、三角形？

## 视觉分割算法


![image_1c8b0d94meaqpsq1bvja7a1g7j1g.png-57.6kB][4]

+ 利用HSV转换颜色空间？？详细原理，为何RGB空间不行
![image_1c8b0hcb314gm3h5mgmu61mus1t.png-90kB][5]

## 实际检测效果
![image_1c8b0kor3e0c16cg1cdo7dt19gr2a.png-283.3kB][6]

## 存在的问题——如何解决？？

![image_1c8b0rv03pn2j91mrb1k8g1dtb9.png-47.9kB][7]

# 软件部分
+ Android 架构的搭建
+ opencv 视觉算法的实现


  [1]: http://static.zybuluo.com/QQJayden/tubert805669nv6jypz1u55u/image_1c8atsubbe4n1hvu1e92lgfhr09.png
  [2]: http://static.zybuluo.com/QQJayden/bronk81dpy2p4vvng1jghcng/image_1c8attqlvf041ssh199tdt6umam.png
  [3]: http://static.zybuluo.com/QQJayden/2toxx5ejcw8eagllmme4bdjp/image_1c8auhgvp1a4e1ahc16tk154njdo13.png
  [4]: http://static.zybuluo.com/QQJayden/0xoz3xbh5bt9pzhb5wljilwa/image_1c8b0d94meaqpsq1bvja7a1g7j1g.png
  [5]: http://static.zybuluo.com/QQJayden/awx92o0k6yqf912m91njvcfg/image_1c8b0hcb314gm3h5mgmu61mus1t.png
  [6]: http://static.zybuluo.com/QQJayden/bpa971eyxdv7gs7ed3ds3rsf/image_1c8b0kor3e0c16cg1cdo7dt19gr2a.png
  [7]: http://static.zybuluo.com/QQJayden/sr054p7jqplqymxmhtcm29j1/image_1c8b0rv03pn2j91mrb1k8g1dtb9.png
