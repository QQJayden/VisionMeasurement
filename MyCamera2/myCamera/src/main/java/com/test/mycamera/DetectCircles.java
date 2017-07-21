package com.test.mycamera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class DetectCircles {

    // 定义半径相关参数
	public static double radMax=0;
	public static double radMin=0;
	public static double rad=0;
    public static double coorx=0;
    public static double coory=0;
    public static int circle_num=0;


    // 霍夫检测检测圆函数
    /*HoughCircles( src_gray, circles, CV_HOUGH_GRADIENT, 1, src_gray.rows/8, 200, 100, 0, 0 );

    CV_HOUGH_GRADIENT：指定检测方法. 现在OpenCV中只有霍夫梯度法
    1.5： 累加器图像的反比分辨率

    src_gray: 输入图像 (灰度图)
    circles: 存储下面三个参数:  集合的容器来表示每个检测到的圆.
    CV_HOUGH_GRADIENT: 指定检测方法. 现在OpenCV中只有霍夫梯度法
    dp = 1: 累加器图像的反比分辨率
    min_dist = src_gray.rows/8: 检测到圆心之间的最小距离
    param_1 = 200: Canny边缘函数的高阈值
    param_2 = 100: 圆心检测阈值.
    min_radius = 0: 能检测到的最小圆半径, 默认为0.
    max_radius = 0: 能检测到的最大圆半径, 默认为0
    */
    public static void findCircles(Mat dstImage,Mat circles){

        //Imgproc.HoughCircles(dstImage,circles,Imgproc.CV_HOUGH_GRADIENT,1.5,50);
        //Imgproc.HoughCircles(dstImage,circles,Imgproc.CV_HOUGH_GRADIENT,2,500); // 参数调节1
        Imgproc.HoughCircles(dstImage,circles,Imgproc.CV_HOUGH_GRADIENT,2,300,100,100,0,30); // 参数调节2

    }

    // 绘制圆函数
	public static void drawCircles(Mat image,Mat circles){

        radMax = 0;
		radMin = 0;
		rad = 0;
        coorx = 0;
        coory = 0;
        circle_num =circles.cols();

        for (int i = 0; i < circles.cols(); i++)
        {
            // 获取circles第i列 ， 每一列存储一个圆的信息
            double vCircle[] = circles.get(0,i);
            double x = vCircle[0];
            double y = vCircle[1];
            double radius = vCircle[2];

            //cvCircle(CvArr* img, CvPoint center, int radius, CvScalar color,
            // int thickness=1, int lineType=8, int shift=0)
            Core.circle(image, new Point(x,y), (int) radius, new Scalar(0,255,0), 3);

            radMax = (radius>radMax)? radius:radMax;
            if (i == 0){
                radMin = radius;
                coorx = x;
                coory = y;
            }


            if(radius < radMin){
                radMin = radius;
                coorx = x;
                coory = y;

            }
            //radMin = (radius < radMin) ? radius : radMin;
        }
    }

    // 获取半径
    public static double getRad()
    {

        //rad = 0.5 * (radMax + radMin);
        rad = radMin + 1.0;

        /*
        double[] rad0 = new double[circles0.cols()];

        for (int i = 0; i < circles0.cols(); i++)
        {
            // 获取circles第i列 ， 每一列存储一个圆的信息
            double vCircle[] = circles0.get(0, i);
            rad0[i] = vCircle[2];
        }
        */

        return rad;
    }

    /*  // 获取坐标数组
    public static double[][] getCoordinate()
    {

        //rad = 0.5 * (radMax + radMin);

        double[][] coordinate0 = new double[2][circles0.cols()];

        for (int i = 0; i < circles0.cols(); i++)
        {
            // 获取circles第i列 ， 每一列存储一个圆的信息
            double vCircle[] = circles0.get(0, i);
            coordinate0[0][i] = vCircle[0];
            coordinate0[1][i] = vCircle[1];
        }

        return coordinate0;
    }
    */

    // 返回最小圆x坐标
    public static double getCoorx()
    {
        return coorx;
    }

    // 返回最小圆y坐标
    public static double getCoory()
    {
        return coory;
    }

    // 霍夫检测到的圆数目
    public static int getCircleNum()
    {
        return circle_num;
    }

    // 半径置零
    public static void changeRad2zero()
	{
		radMax = 0;
		radMin = 0;
		rad = 0;
	}
}
