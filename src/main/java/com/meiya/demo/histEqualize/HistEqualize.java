package com.meiya.demo.histEqualize;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *需要安装opencv环境，并且在项目右击选择open module settings中将opencv.dll文件放入到library中，将opencv.jar放入到global libraries中
 */


public class HistEqualize {

    /**
     * 增强对比度
     *
     * @param src BGR格式图像
     * @return
     */

    static{
       System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("opencv\t"+Core.VERSION);
    }


    //直方图均衡
    public static Mat histEqualize(Mat src) {
        Mat dst = src.clone();
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> list1 = new ArrayList<>();
        Core.split(dst, list1);
        Imgproc.equalizeHist(list1.get(0), list1.get(0));
        Core.normalize(list1.get(0), list1.get(0), 0, 255, Core.NORM_MINMAX);
        Core.merge(list1, dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_YCrCb2BGR);
        return dst;
    }

    //对比度受限直方图均衡化CLAHE
    public static Mat autoHistEqualize(Mat src) {
        Mat dst = src.clone();
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> list1 = new ArrayList<>();
        Core.split(dst, list1);
        CLAHE clahe = Imgproc.createCLAHE();
        clahe.setClipLimit(4);
        clahe.apply(list1.get(0), list1.get(0));
//        Core.normalize(list1.get(0),list1.get(0),0,255,Core.NORM_MINMAX);
        Core.merge(list1, dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_YCrCb2BGR);
        return dst;
    }

    /**
     * 用于整体偏暗图像的增强,变亮,拉普拉斯算子增强
     *
     * @param src
     * @return
     */
    public static Mat laplaceEnhance(Mat src) {
        Mat srcClone = src.clone();
        float[] kernel = {0, 0, 0, -1, 5f, -1, 0, 0, 0};
        Mat kernelMat = new Mat(3, 3, CvType.CV_32FC1);
        kernelMat.put(0, 0, kernel);
        Imgproc.filter2D(srcClone, srcClone, CvType.CV_8UC3, kernelMat);
        return srcClone;
    }

    /**
     * 对数变换可以将图像的低灰度值部分扩展，显示出低灰度部分更多的细节，
     * 将其高灰度值部分压缩，减少高灰度值部分的细节，从而达到强调图像低灰度部分的目的。
     *
     * @param src
     * @return
     */
    public static Mat logEnhance(Mat src) {
        Mat srcClone = src.clone();
        Mat imageResult = new Mat(srcClone.size(), CvType.CV_32FC3);
        Core.add(srcClone, new Scalar(5, 5, 5), srcClone);
        srcClone.convertTo(srcClone, CvType.CV_32F);
        Core.log(srcClone, imageResult);
        //Core.multiply(imageLog, new Scalar(3,3,3), imageLog);
        Core.normalize(imageResult, imageResult, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(imageResult, imageResult);
        return imageResult;
    }

    //伽马变换
    public static Mat gammaEnhance(Mat src) {
        Mat srcClone = src.clone();
        srcClone.convertTo(srcClone, CvType.CV_32F);
        Core.pow(srcClone, 4, srcClone);
        Core.normalize(srcClone, srcClone, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(srcClone, srcClone);
        return srcClone;
    }

    //图像锐化处理，清晰度增强第一种方法
    public static BufferedImage sharpen(BufferedImage image) {
        float[] elements = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0, 0f };
        BufferedImage bimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        cop.filter(image, bimg);
        return bimg;
    }

    //图像钝化处理
    public static BufferedImage blur(BufferedImage image) {
        float[] elements = { 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f, 0.11111f };
        BufferedImage bimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        cop.filter(image, bimg);
        return bimg;
    }


    public static void main(String[] args) throws IOException {
        //直方图均衡
        Mat src = Imgcodecs.imread("e:\\2.jpg");
        Mat mat1 = histEqualize(src);
        Imgcodecs.imwrite("e:\\1.png", mat1);

        //对比度受限直方图均衡化CLAHE
        Mat mat2 = autoHistEqualize(src);
        Imgcodecs.imwrite("e:\\2.png", mat2);

        //拉普拉斯算子增强-偏暗图像的增强变亮-增强对比度
        Mat mat3 = laplaceEnhance(src);
        Imgcodecs.imwrite("e:\\3.png", mat3) ;

        //对数变换-低灰度部分更多的细节
        Mat mat4 = logEnhance(src);
        Imgcodecs.imwrite("e:\\4.png", mat4) ;

        //伽马变换-图片去雾
        Mat mat5 = gammaEnhance(src);
        Imgcodecs.imwrite("e:\\5.png", mat5) ;


        //清晰度算法
        BufferedImage img = ImageIO.read(new File("e:\\2.jpg"));
        if(img.getType()==BufferedImage.TYPE_3BYTE_BGR){
            img=convertType(img,BufferedImage.TYPE_INT_RGB);
        }
        BufferedImage img2 = sharpen(img);
        ImageIO.write(img2, "jpeg", new File("e:\\清晰度-图片锐化.jpg"));
        BufferedImage img3 = blur(img);
        ImageIO.write(img3, "jpeg", new File("e:\\清晰度-图片钝化.jpg"));


    }

    private static BufferedImage convertType(BufferedImage img, int typeIntRgb) {
        ColorConvertOp cco = new ColorConvertOp(null);
        BufferedImage dest = new BufferedImage(img.getWidth(),img.getHeight(),typeIntRgb);
        cco.filter(img,dest);
        return dest;
    }


}
