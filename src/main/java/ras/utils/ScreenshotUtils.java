package ras.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class ScreenshotUtils {

	public static void screenCapture(String path) {
		String s = null;
		try {
			Process p = Runtime.getRuntime().exec("screencapture " + path);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void cropImage(String path, String croppedPath, int x, int y, int width, int height) {
		try {
			BufferedImage originalImgage = ImageIO.read(new File(path));
			BufferedImage SubImgage = originalImgage.getSubimage(x, y, width, height);
			File outputfile = new File(croppedPath);
			ImageIO.write(SubImgage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
