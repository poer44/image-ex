package com.meiya.demo.RateSystem;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.opencv.core.Core;

import java.io.File;

import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;


/**
 * 图片评分系统
 *
 *
 */
public class RateSystem {


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("opencv\t" + Core.VERSION);
        String path = "F:\\image\\imgtest";        //要遍历的路径
        File file = new File(path);        //获取其file对象
        File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
        for (File jpegFile : fs) {                    //遍历File[]数组
            if (!jpegFile.isDirectory()) {
                opencv_core.Mat srcImage = opencv_imgcodecs.imread(jpegFile.getAbsolutePath());
                opencv_core.Mat dstImage = new opencv_core.Mat();
                //转化为灰度图
                opencv_imgproc.cvtColor(srcImage, dstImage, COLOR_BGR2GRAY);
                //在gray目录下生成灰度图片
                opencv_imgcodecs.imwrite(path + "gray-" + jpegFile.getName(), dstImage);

                opencv_core.Mat laplacianDstImage = new opencv_core.Mat();
                //阈值太低会导致正常图片被误断为模糊图片，阈值太高会导致模糊图片被误判为正常图片
                opencv_imgproc.Laplacian(dstImage, laplacianDstImage, opencv_core.CV_64F);
                //在laplacian目录下升成经过拉普拉斯掩模做卷积运算的图片
                opencv_imgcodecs.imwrite(path + "laplacian-" + jpegFile.getName(), laplacianDstImage);

                //矩阵标准差
                opencv_core.Mat stddev = new opencv_core.Mat();

                //求矩阵的均值与标准差
                opencv_core.meanStdDev(laplacianDstImage, new opencv_core.Mat(), stddev);
                // ((全部元素的平方)的和)的平方根
                // double norm = Core.norm(laplacianDstImage);
                // System.out.println("\n矩阵的均值：\n" + mean.dump());
                System.out.println(jpegFile.getName() + "矩阵的标准差：\n" + stddev.createIndexer().getDouble());
                // System.out.println(jpegFile.getName()+"平方根：\n" + norm);
            }
        }

    }
}
