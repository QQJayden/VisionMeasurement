package com.test.mycamera;

/**
 * 检测圆形插头固定白带,剔除其它颜色
 * Created by 13260 on 2017/7/15.
 */

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class DetectColor {



    // 首先通过HSV空间来提取白色区域
    public static void findWhite(Mat dstImage,Mat imWhite){

        Mat imghsv = new Mat();
        //Mat imghsvW = new Mat();

        Imgproc.cvtColor(dstImage, imghsv, Imgproc.COLOR_BGR2HSV);

        // remember: H ranges 0-180, S and V range 0-255
        Core.inRange(imghsv, new Scalar(0,0,220), new Scalar(175,50,255),imWhite);

        //Imgproc.cvtColor(imghsvW, imWhite, Imgproc.COLOR_HSV2BGR);



    }



}
