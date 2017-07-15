package com.test.mycamera;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class DetectSquares {
	//检测正方形 工具类

	private static double maxArea = 0, area = 0;

	public static double lengthMax = 0,line4;

	//利用余弦定理， 计算夹角余弦
	public static double angle(Point pt1, Point pt2, Point pt0) {
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		return (dx1 * dx2 + dy1 * dy2)
				/ Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2)
						+ 1e-10);
	}

    //计算两边长度之差
	public static double isEqual(Point pt1, Point pt2, Point pt0) {
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		return Math.sqrt((dx1 * dx1 + dy1 * dy1))
				- Math.sqrt((dx2 * dx2 + dy2 * dy2));
	}

	public static double getLength() {
		//由面积计算边长
		lengthMax = Math.sqrt(maxArea);
		return lengthMax;

	}

	public static void changeLength2zero() {
		lengthMax = 0;
		
	}

	public static void findSquares(Mat image, List<MatOfPoint> squares) {

		maxArea = 0;

		Mat gray = new Mat(image.size(), CvType.CV_8U);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		// threshold(gray0, gray, (l+1)*255/N, 255, Imgproc.THRESH_BINARY);
		// Imgproc.GaussianBlur(gray0, gray0, new org.opencv.core.Size(9,9),
		// 2,2);
		// Imgproc.medianBlur(gray0, gray0, 9);
		// Imgproc.Canny(image, image, 0, 50);
		// 转化为二值图像
		Imgproc.threshold(image, gray, 100, 255, Imgproc.THRESH_BINARY);
		// Imgproc.adaptiveThreshold(image, gray, 255,
		// Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 5, 1);
		// return gray;
		//寻找所有轮廓
		Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		
		MatOfPoint approx;
		MatOfPoint2f contouri;
		MatOfPoint2f approx2f = new MatOfPoint2f();

		//对寻找到的轮廓遍历
		for (int i = 0; i < contours.size(); i++) {
			//由于approxPolyDP参数类型的原因͵对MatOfPoint进行转化
			contouri = new MatOfPoint2f(contours.get(i).toArray());
			//从轮廓逼近多边形曲线
			Imgproc.approxPolyDP(contouri, approx2f,
					Imgproc.arcLength(contouri, true) * 0.02, true);
			// 将类型转换回来
			approx = new MatOfPoint(approx2f.toArray());

			// 对于生成的逼近多边形 应该有四个顶点， 对最小面积限制 防止检测无用四边形
			if (approx.toList().size() == 4
					& Math.abs(Imgproc.contourArea(approx)) > 30) {
				double maxCosine = 0;
				double maxGap = 0;

				for (int j = 2; j < 5; j++) {
					//角度计算
					double cosine = Math.abs(angle(approx.toArray()[j % 4],
							approx.toArray()[j - 2], approx.toArray()[j - 1]));
					//边长差计算
					double gap = Math.abs(isEqual(approx.toArray()[j % 4],
							approx.toArray()[j - 2], approx.toArray()[j - 1]));
					maxCosine = Math.max(maxCosine, cosine);
					maxGap = Math.max(maxGap, gap);
				}
				//  对最大的cos和最大的边长差进行限制
				if (maxCosine < 0.3 & maxGap < 20) {
					area = Math.abs(Imgproc.contourArea(approx));
					maxArea = Math.max(maxArea, area);
					//将检测好的方形加入squares
					squares.add(approx);
				}
			}
		}
	}

	public static void drawSquares(Mat image, List<MatOfPoint> squares) {

		for (int i = 0; i < squares.size(); i++) {
			//�Է���ÿ���߷ֱ���
			for (int j = 0; j < 4; j++) {
				Point pt1 = squares.get(i).toArray()[j];
				Point pt2 = squares.get(i).toArray()[(j + 1) % 4];
				Core.line(image, pt1, pt2, new Scalar(255, 0, 0), 3);
//				double dx = pt1.x - pt2.x;
//				double dy = pt1.y - pt2.y;
				//line4 += Math.sqrt((dx * dx + dy * dy));
				
			}

//			lengthMax = Math.max(lengthMax,line4/4);
//			line4 =0;
		}


	}


}
