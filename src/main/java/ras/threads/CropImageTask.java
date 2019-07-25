package ras.threads;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import ras.utils.Paths;
import ras.utils.ScreenshotUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class CropImageTask implements Runnable {

    private Semaphore sem;
    private String path;
    private String croppedPath;
    private int x;
    private int y;
    private int width;
    private int height;

    public CropImageTask(Semaphore sem, String path, String croppedPath, int x, int y, int width, int height){
        this.sem = sem;
        this.path = path;
        this.croppedPath = croppedPath;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void run() {
        try {
            BufferedImage originalImgage = ImageIO.read(new File(path));
            BufferedImage croppedImage = originalImgage.getSubimage(x, y, width, height);
            BufferedImage blackWhiteImage = convertToBlackWhite(croppedImage);

            File outputfile = new File(croppedPath);
            ImageIO.write(blackWhiteImage, "png", outputfile);
            sem.release();
            //System.out.println("Released semaphore in CropImageTask " + sem.availablePermits() + " for value " + croppedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage convertToBlackWhite(BufferedImage image) {
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);

            Graphics2D graphic = result.createGraphics();
            graphic.drawImage(image, 0, 0, Color.WHITE, null);
            graphic.dispose();

            return result;
    }

    public void changeDPI(BufferedImage image){
        JPEGImageEncoder jpegEncoder = null;
        try {
            jpegEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
        jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
        jpegEncoder.setJPEGEncodeParam(jpegEncodeParam); jpegEncodeParam.setQuality(0.75f, false);
        jpegEncodeParam.setXDensity(300); jpegEncodeParam.setYDensity(300);
        try {
            jpegEncoder.encode(image, jpegEncodeParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.flush();
    }
}
