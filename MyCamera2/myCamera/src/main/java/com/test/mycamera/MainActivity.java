package com.test.mycamera;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.R.integer;
import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements CvCameraViewListener2 {

	private CameraBridgeViewBase mCameraView;

	private double d = 0, coor_x = 0, coor_y = 0, numc = 0, Distance = 0,
            center_x = 0, center_y = 0;
    private int circle_num = 0, contour_num = 0;
	private double dSum = 0, dsSum = 0, ddSum = 0;
	private boolean isCalculate = false;
	private boolean mIsWhite = false;
    private boolean mIsGreen = false;
    private boolean mIsYellow = false;
    private boolean mIsBlue = false;
    private boolean mIsRed = false;

	private boolean mIsDetectStartC = false;
    private boolean mIsImgGray = false;
	private boolean isWorking = false;
	private int counter = 0;
	private long exitTime = 0;
	
	
	Mat mBgr;
	TextView showMeasureC,showMeasureCS,showCoordinate, showCircleNum;
	TextView catchDistance;
	TextView catchYuan, jieguo, jieguoS, jieguoD, catchCenter, catchContourNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		this.setTitle(R.string.biaoti);

        //摄像头初始化
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
		//mCameraView.setMaxFrameSize(960,720);
		mCameraView.setVisibility(SurfaceView.VISIBLE);
		mCameraView.setCvCameraViewListener(this);
		// mCameraView.enableView();

		showMeasureC = (TextView) findViewById(R.id.showMeasureC);    //测量长度，圆
        showMeasureCS = (TextView)findViewById(R.id.showMeasureCS);  // 测量面积，圆
        showCoordinate = (TextView)findViewById(R.id.showCoordinate);  // 显示圆心坐标
        showCircleNum = (TextView)findViewById(R.id.showCircleNum);  // 显示检测圆的数目
		catchDistance = (TextView) findViewById(R.id.catchDistance);   // 获取距离

		catchYuan = (TextView) findViewById(R.id.catchYuan);   // 半径平均值
        catchCenter = (TextView) findViewById(R.id.catchCenter); // 区域质心
        catchContourNum = (TextView) findViewById(R.id.catchContourNum); //连通区域数量
		jieguo = (TextView) findViewById(R.id.jieguo);      // 边长平均值？？？
		jieguoS = (TextView)findViewById(R.id.jieguoS);      //面积平均值
        jieguoD = (TextView)findViewById(R.id.jieguoD);      //距离平均值
		//获取文本信息

        // 获取比例尺：按钮点击事件
		catchDistance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

                // 删除标签，点击按钮即相应

                //获取半径平均值？？？getRad()函数需要修改
                // 必须先调用circles
                double radnow = DetectCircles.getRad();  // 返回最小半径
                double Sp = 3.14159 * radnow * radnow;
                Distance = Math.sqrt(1190.234 * 1183.102 * 78.54 / Sp); // 计算距离公式
                catchDistance.setText((double) Math.round(Distance)*100 / 100 + "点击获取距离");                              // 显示比例尺
            }

//					detectFirst = false;
//				} else
//					//��ʼ�����ټ���  ��ֹ��0 scale = NaN;
//					Toast.makeText(MainActivity.this, "�뿪ʼ�����ٵ��", Toast.LENGTH_SHORT).show();

		});

		// 用于显示计算所得的数据
        final Handler myHandler = new Handler() {

            @Override
			public void handleMessage(Message msg) {

                if (msg.what == 0x1234) {                //判断消息是否来自于子线程,0.3s周期定时器

                    // 图像中最小圆的直径，圆心坐标
                    d = DetectCircles.getRad()*2;
                    coor_x = DetectCircles.getCoorx();
                    coor_y = DetectCircles.getCoory();
                    double Sp = 3.14159 * d * d * 1.0 / 4;
                    Distance = Math.sqrt(1190.234 * 1183.102 * 78.54 / Sp); // 计算距离公式
                    circle_num = DetectCircles.getCircleNum();

                    if (isCalculate) {                        //如果开启平均值计算功能

                        catchYuan.setVisibility(View.VISIBLE);
                        jieguo.setVisibility(View.VISIBLE);
                        jieguoS.setVisibility(View.VISIBLE);
                        jieguoD.setVisibility(View.VISIBLE);

                       // l d为0时不计算平均值
                        if (d != 0) {

                            // 第11次输出结果，取10次平均值
                            // catchYuan用来显示第几次
                            if (counter % 11 == 0) {
                                catchYuan.setText("ֱ直径平均值"
                                        + (double) Math.round(dSum / 10 * 100)
                                        / 100);

                                jieguo.setText("10帧 ֱ直径平均值ֵ"
                                        + (double) Math.round(dSum / 10 * 100)
                                        / 100 + "pixel\n" );
                                jieguoS.setText("10帧 面积平均值ֵ"
                                        + (double) Math.round(dsSum / 10 * 100)
                                        / 100 + "pixel2\n" );
                                jieguoD.setText("10帧 距离平均值"
                                        + (double) Math.round(ddSum / 10 * 100)
                                        /100 + "mm");
                                counter = 0;
                                dSum = 0;
                                dsSum = 0;
                                ddSum = 0;
                            }

                            else {       //     显示正在计算第几列数据
                                catchYuan.setText("计算直径平均值(" + (counter % 11) + ")");

                                dSum += d ;     // 直径*比例尺，实际尺寸
                                dsSum += 3.1415 *d*d/4.0 ;
                                ddSum += Distance;
                            }
                            counter++;
                        }
                    }
                    else {           // 关闭计算平均值功能
                        catchYuan.setVisibility(View.INVISIBLE);
                        jieguo.setVisibility(View.INVISIBLE);
                        jieguoS.setVisibility(View.INVISIBLE);
                        jieguoD.setVisibility(View.INVISIBLE);
                    }

                    // 如过检测白色区域，则显示其质心
                    center_x = DetectColor.getCenterx(); // 获取规定范围内最大区域质心
                    center_y = DetectColor.getCentery();
                    contour_num = DetectColor.getContourNum();


                    if(mIsRed) {

                        catchCenter.setVisibility(View.VISIBLE);
                        catchContourNum.setVisibility(View.VISIBLE);
                        catchCenter.setText("最大白色块的质心坐标为：x="+ center_x + ", y=" + center_y );
                        catchContourNum.setText("连通区域数目：" + contour_num);

                    }

                    else {
                        catchCenter.setVisibility(View.INVISIBLE);
                        catchContourNum.setVisibility(View.INVISIBLE);
                    }
			
                    //实时显示测量结果
                    showMeasureC.setText("最小圆直径为"
                            + (double) Math.round(d * 100) / 100+"pixel");

                    //DetectCircles.changeRad2zero();

                    showMeasureCS.setText("最小圆面积为"
                            + (double) Math.round(3.14159 *d*d/4.0 * 100) / 100+"pixel^2");

                    showCoordinate.setText("图像x=" + coor_x + ", y=" + coor_y);
                    showCircleNum.setText("检测圆数目:" + circle_num);

                }
            }
        };

        new Timer().schedule(new TimerTask() {
			//对应上方子线程？？？  定时器， 初始时间0， 每300ms执行一次
			@Override
			public void run() {
                // TODO Auto-generated method stub
                myHandler.sendEmptyMessage(0x1234);
			}
        }, 0, 300);

    }

    @SuppressLint("NewApi")   //屏蔽android lint错误

    @Override
	public void onPause() {
		if (mCameraView != null) {
            mCameraView.disableView();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this,
				mLoaderCallback);

	}

	@Override
	public void onDestroy() {
		if (mCameraView != null) {
			mCameraView.disableView();
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        // 找出菜单？？
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        // 当用户点击menu item，将会触发onOptionItemSelected()回调函数
        // 获取菜单项目的id
		int id = item.getItemId();

        // 开始检测
		if (id == R.id.detect) {

            // 如果正在运行，则将其停止
			if (isWorking) {
                isWorking = false;             // 运行标签置零
                mIsDetectStartC = false;       // 检测圆标签置零
				item.setTitle("开始检测");
				d = 0;
                // 输出停止检测提示
                Toast.makeText(this, "Stop Successfully", Toast.LENGTH_SHORT).show();
			}

            else    // 如果未在运行，则将标签均置1
            {
				mIsDetectStartC = true;
				isWorking = true;
				item.setTitle("开始检测e");
			}
		}

        // 灰度图像选项，显示当前画面的灰度图像！！！

        if (id == R.id.imgGray)
        {
			if (mIsImgGray)
            {
				mIsImgGray = false;
				item.setTitle("显示灰度图像测试");
				Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
			}
            else
            {
				mIsImgGray =true;
				item.setTitle("退出灰度图像测试e");
			}

			return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		}

        // 检测白色，绿色，黄色区域
        if (id == R.id.detectWhite)
        {
            if (mIsWhite)
            {
                mIsWhite = false;
                item.setTitle("提取图像白色");
                Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mIsWhite =true;
                item.setTitle("退出白色模式e");
            }

            return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        }


        if (id == R.id.detectGreen)
        {
            if (mIsGreen)
            {
                mIsGreen = false;
                item.setTitle("提取图像绿色");
                Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mIsGreen =true;
                item.setTitle("退出绿色模式e");
            }

            return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        }


        if (id == R.id.detectYellow)
        {
            if (mIsYellow)
            {
                mIsYellow = false;
                item.setTitle("提取图像黄色");
                Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mIsYellow =true;
                item.setTitle("退出黄色模式e");
            }

            return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        }


        if (id == R.id.detectBlue)
        {
            if (mIsBlue)
            {
                mIsBlue = false;
                item.setTitle("提取图像蓝色");
                Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mIsBlue =true;
                item.setTitle("退出蓝色模式e");
            }

            return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        }


        if (id == R.id.detectRed)
        {
            if (mIsRed)
            {
                mIsRed = false;
                item.setTitle("提取红色区域");
                Toast.makeText(this, "Stoped", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mIsRed =true;
                item.setTitle("退出红色模式e");
            }

            return true;    //  //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        }



        /*
        // 切换参照物， 默认是圆参照
		if (id == R.id.changeScale) {
			if (usingScale1) {
				usingScale1 = false;
				item.setTitle("40mm changeScale");  // ？？？神马鬼意思
			} else {
				usingScale1 = true;
				item.setTitle("60mm changeScale");
			}
			return true;
		}
		*/

        // 开启平均值功能
		if (id == R.id.calculate) {
			if (isCalculate) {
				isCalculate = false;
				item.setTitle("calculate");
			} else {
				isCalculate = true;
				item.setTitle("calculate ");
			}
			return true;

		}
        //返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
        return super.onOptionsItemSelected(item);
	}

	//  opencvManager ？？？
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				mCameraView.enableView();
				mBgr = new Mat();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}

		}
	};

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	//  视像头界面相关处理，主函数，图像处理相关算法均在此处完成
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub

        // 直接返回输入视频预览图的RGBA数据并存在Mat数据中，提取了其中一帧？？
		Mat image = inputFrame.rgba();


        // 绘制矩形区域：ROI

        Core.rectangle(image, new Point(150, 10), new Point(1100, 710), new Scalar(255,128,64));

        //image = imgRectROI.clone();


        /*
        // 遍历图像，将ROI区域之外置0: 运算速度太慢
        for(int i = 0; i < image.rows(); i++)
        {
            for(int j = 0; j<image.cols(); j++)
            {
                if(i<150 || i>1100 || j<10 || j>710)
                {
                    image.row(i).col(j).setTo(new Scalar(0,0,0));
                }
            }
        }
        */




        // 通过各种标签来判断做何种图像处理
		if (isWorking) {
            //如果正在运行检测 复制原图
			Mat dstImage = image.clone();

            //检测的帧数较慢， 在检测前的预处理进行优化的取舍
            // 直接threshhold进行二值化？？？
            Imgproc.cvtColor(image, dstImage, Imgproc.COLOR_BGR2GRAY);         // 原图转化为灰度图

            // 滤波处理
			// Imgproc.bilateralFilter(dstImage, dstImage, 25, 25*2, 25/2);
			// Imgproc.medianBlur(dstImage, dstImage,5);
			Imgproc.blur(dstImage, dstImage, new org.opencv.core.Size(5, 5));    // 均值滤波？提高效率
			// Imgproc.GaussianBlur(dstImage, dstImage, new org.opencv.core.Size(9, 9), 2, 2);
			// Imgproc.Canny(dstImage, dstImage, 50, 200);

			if (mIsDetectStartC) {
				Mat circles = new Mat();      // 新建动态圆结构，存放圆心坐标与半径
                DetectCircles.findCircles(dstImage, circles);         // 检测圆
                DetectCircles.drawCircles(image, circles);            // 绘制圆
			}
		}


        // 显示灰度图像测试
        if(mIsImgGray&!isWorking)
        {

            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);         // 原图转化为灰度图
            Imgproc.blur(image, image, new org.opencv.core.Size(5, 5));    // 均值滤波？提高效率



        }

        // 提取图像中的白色，未知原因得闪退？？？
        if(mIsWhite&!isWorking)
        {
            //如果正在运行检测 复制原图
            Mat dstImageW = image.clone();

            /*
            // 提取感兴趣区域
            Rect rect = new Rect(150, 10, 950, 700); // 设置矩形ROI的位置
            Mat dstImageW= new Mat(image, rect);      // 从原图中截取图片
            */

            Mat centers = new Mat();      // 新建白色区域结构，存放质心坐标与面积

            DetectColor.findWhite(dstImageW, dstImageW, centers);

            image = dstImageW.clone();

        }


        // 提取图像红色
        if(mIsRed&!isWorking)
        {
            //如果正在运行检测 复制原图
            Mat dstImageR = image.clone();

            Mat centers = new Mat();      // 新建白色区域结构，存放质心坐标与面积

            DetectColor.findRed(dstImageR, dstImageR, centers);

            // 检测圆形

            image = dstImageR.clone();

            //Core.inRange(dstImageW, new Scalar(0,0,0), new Scalar(255,255,255),image);  //直接RGB

        }


        // 提取图像绿色
        if(mIsGreen&!isWorking)
        {
            //如果正在运行检测 复制原图
            Mat dstImageG = image.clone();

            DetectColor.findGreen(dstImageG, dstImageG);

            image = dstImageG.clone();

            //Core.inRange(dstImageW, new Scalar(0,0,0), new Scalar(255,255,255),image);  //直接RGB


        }


        // 提取图像黄色
        if(mIsYellow&!isWorking)
        {
            //如果正在运行检测 复制原图
            Mat dstImageY = image.clone();

            DetectColor.findYellow(dstImageY, dstImageY);

            image = dstImageY.clone();

            //Core.inRange(dstImageW, new Scalar(0,0,0), new Scalar(255,255,255),image);  //直接RGB

        }


        // 提取图像蓝色
        if(mIsBlue&!isWorking)
        {
            //如果正在运行检测 复制原图
            Mat dstImageB = image.clone();

            DetectColor.findBlue(dstImageB, dstImageB);

            // 检测圆形

            image = dstImageB.clone();

            //Core.inRange(dstImageW, new Scalar(0,0,0), new Scalar(255,255,255),image);  //直接RGB

        }




		return image;      // 返回绘制圆后的图像image

	}

	// 按键操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "???", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}

            else {
				finish();
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
