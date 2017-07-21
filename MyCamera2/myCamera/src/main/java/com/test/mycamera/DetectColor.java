package com.test.mycamera;

/**
 * 检测圆形插头固定白带,剔除其它颜色
 * Created by 13260 on 2017/7/15.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class DetectColor {

    public static double center_x = 0;
    public static double center_y = 0;
    public static double area = 0;
    public static double area_max = 0;
    public static double area_min = 0;
    public static int contour_num = 0;



    // 首先通过HSV空间来提取白色区域
    // 白色受光线的影响太大，因此换成红色黑帽操作
    public static void findWhite(Mat dstImage,Mat imWhite, Mat centers){

        Mat imghsv = new Mat();
        //Mat imghsvW = new Mat();

        Imgproc.cvtColor(dstImage, imghsv, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        Core.inRange(imghsv, new Scalar(0,0,180), new Scalar(180,50,255),imWhite);


        // 定义核
        // 开操作 (去除一些噪点)
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)); //正方形
        //Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5.5,5.5));  // 圆形
        Imgproc.morphologyEx(imWhite, imWhite, Imgproc.MORPH_OPEN, element1);

        //闭操作 (连接一些连通域)
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        //Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(6.5,6.5));  // 圆形
        Imgproc.morphologyEx(imWhite, imWhite, Imgproc.MORPH_CLOSE, element2);


        // 滤波处理:暂时效果较差，之后再调试
        // Imgproc.bilateralFilter(dstImage, dstImage, 25, 25*2, 25/2);
        // Imgproc.medianBlur(dstImage, dstImage,5);
        // Imgproc.blur(imGreen, imGreen, new org.opencv.core.Size(5, 5));    // 均值滤波？提高效率
        // Imgproc.GaussianBlur(dstImage, dstImage, new org.opencv.core.Size(9, 9), 2, 2);
        // Imgproc.Canny(dstImage, dstImage, 50, 200);

        /*
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        //Mat hierarchy = new Mat();

        Imgproc.findContours(imWhite, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);
        // CHAIN_APPROX_SIMPLE： 水平、垂直、对角线方向只保持终点坐标

        //Mat image32S = new Mat();
        //imWhite.convertTo(image32S, CvType.CV_32SC1);

        //Imgproc.findContours(imWhite, contours, new Mat(), Imgproc.RETR_FLOODFILL, Imgproc.CHAIN_APPROX_SIMPLE);
        // Draw all the contours such that they are filled in.
        // Mat contourImg = new Mat(image32S.size(), image32S.type());

        /*
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(imWhite, contours, i, new Scalar(255, 255, 255), -1);
        }

        */

        // 检测imwhite中区域块的质心

    }

    // 通过HSV空间来提取红色线束区域区域, 通过顶帽变换，提取红色区域之间的连接部分
    public static void findRed(Mat dstImage,Mat imRed, Mat centers){

        Mat imghsvR = new Mat();

        Imgproc.cvtColor(dstImage, imghsvR, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        // 红色线束区域效果完美
        Core.inRange(imghsvR, new Scalar(105,100,46), new Scalar(120,255,255),imRed);


        // 定义核
        // 开操作 (去除一些噪点)
        //Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)); //正方形
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5.5,5.5));  // 圆形
        Imgproc.morphologyEx(imRed, imRed, Imgproc.MORPH_OPEN, element1);

        Mat imRedC = new Mat();

        //闭操作 (连接一些连通域)
        //Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(30,30));  // 圆形
        Imgproc.morphologyEx(imRed, imRedC, Imgproc.MORPH_CLOSE, element2);

        // 黑帽操作: 闭操作-原图 = 结果

        Core.subtract(imRedC,imRed,imRed);

        // 再来一遍开操作

        Mat element3 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5.5,5.5));  // 圆形
        Imgproc.morphologyEx(imRed, imRed, Imgproc.MORPH_OPEN, element3);
        // 此时调试的结果中有一个误判，咱时候再调试！！！

        Mat imRedF = imRed.clone();




        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        //Mat hierarchy = new Mat();

        Imgproc.findContours(imRedF, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_NONE);

        contour_num = contours.size();

        // CHAIN_APPROX_SIMPLE： 水平、垂直、对角线方向只保持终点坐标

/*
        // 依旧闪退！！！
        // 通过外接矩形计算区域中心坐标
        List<RotatedRect> rect = new ArrayList<RotatedRect>();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f points = new MatOfPoint2f(imRed);
            RotatedRect recti = Imgproc.minAreaRect(points);
            rect.add(recti);

            // 获取最大最小区域面积
            area = recti.size.area();
            area_min = (area>area_min)? area_min:area;

            // 最大区域中心坐标

            if(area>area_max) {
                center_x = recti.center.x;
                center_y = recti.center.y;
            }
        }*/



        // 检测imwhite中区域块的质心

    }

    // 通过HSV空间来提取绿色区域：效果满分
    public static void findGreen(Mat dstImageG,Mat imGreen){

        Mat imghsvG = new Mat();
        //Mat imghsvW = new Mat();

        Imgproc.cvtColor(dstImageG, imghsvG, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        Core.inRange(imghsvG, new Scalar(35, 43, 46), new Scalar(77, 255, 255),imGreen);

        // 定义核
        // 开操作 (去除一些噪点)
        //Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3)); //正方形
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5.5,5.5));  // 圆形
        Imgproc.morphologyEx(imGreen, imGreen, Imgproc.MORPH_OPEN, element1);

        //闭操作 (连接一些连通域)
        //Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7,7));
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(6.5,6.5));  // 圆形
        Imgproc.morphologyEx(imGreen, imGreen, Imgproc.MORPH_CLOSE, element2);

        // 滤波处理:暂时效果较差，之后再调试
        // Imgproc.bilateralFilter(dstImage, dstImage, 25, 25*2, 25/2);
        // Imgproc.medianBlur(dstImage, dstImage,5);
        // Imgproc.blur(imGreen, imGreen, new org.opencv.core.Size(5, 5));    // 均值滤波？提高效率
        // Imgproc.GaussianBlur(dstImage, dstImage, new org.opencv.core.Size(9, 9), 2, 2);
        // Imgproc.Canny(dstImage, dstImage, 50, 200);


    }


    // 通过HSV空间来提取黄色区域:??黄色有问题？？
    public static void findYellow(Mat dstImageY,Mat imYellow){

        Mat imghsvY = new Mat();
        //Mat imghsvW = new Mat();

        Imgproc.cvtColor(dstImageY, imghsvY, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        Core.inRange(imghsvY, new Scalar(80,43,46), new Scalar(100,255,255),imYellow);

        //Imgproc.cvtColor(imghsvW, imWhite, Imgproc.COLOR_HSV2BGR);

    }



    // 通过HSV空间来提取蓝色区域:??黄色有问题？？
    public static void findBlue(Mat dstImageB,Mat imBlue){

        Mat imghsvB = new Mat();
        //Mat imghsvB = new Mat();

        Imgproc.cvtColor(dstImageB, imghsvB, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        // 蓝色HSV对应橙色HSV
        Core.inRange(imghsvB, new Scalar(10,43,46), new Scalar(20,255,255),imBlue);

        //Imgproc.cvtColor(imghsvW, imWhite, Imgproc.COLOR_HSV2BGR);


    }

    public static double getCenterx() {

        return center_x;
    }

    public static double getCentery(){

        return center_y;

    }

    public static int getContourNum(){

        return contour_num;

    }



}
