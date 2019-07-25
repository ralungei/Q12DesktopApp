package ras.threads;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ScreencaptureTask implements Runnable {

    private String path;

    public ScreencaptureTask(String path){
        this.path = path;
    }

    public void run() {
        String s = null;

        try {

            Process p = Runtime.getRuntime().exec("screencapture " + path);
            p.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
