package com.meiya.demo;

import com.meiya.demo.histEqualize.HistEqualize;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static com.meiya.demo.histEqualize.HistEqualize.*;
import static com.meiya.demo.imageResolution.ImageResolution.mathProcess;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("opencv\t"+Core.VERSION);
    }

}
