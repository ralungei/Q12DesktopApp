package ras.threads;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import ras.utils.Paths;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class OCRRecognitionTask implements Runnable {

    private Semaphore ocrReadySem;
    private Semaphore cropReadySem;
    private Semaphore searchFullQFullASemaphore;
    private Semaphore searchEntQFullASemaphore;
    private Semaphore searchEntQFullQWithFullATaskSemaphore;
    private Semaphore searchFullQEntASemaphore;
    private AtomicReference<String> text;
    private String path;

    public OCRRecognitionTask(Semaphore ocrReadySem, Semaphore cropReadySem, Semaphore searchFullQFullASemaphore, Semaphore searchEntQFullASemaphore, Semaphore searchEntQFullQWithFullATaskSemaphore, Semaphore searchFullQEntASemaphore, AtomicReference<String> text, String path) {
        this.ocrReadySem = ocrReadySem;
        this.cropReadySem = cropReadySem;
        this.searchFullQFullASemaphore = searchFullQFullASemaphore;
        this.searchEntQFullASemaphore = searchEntQFullASemaphore;
        this.searchEntQFullQWithFullATaskSemaphore = searchEntQFullQWithFullATaskSemaphore;
        this.searchFullQEntASemaphore = searchFullQEntASemaphore;
        this.text = text;
        this.path = path;
    }

    public void run() {
        try {
            System.out.println("Waiting in OCRRecognitionTask");
            cropReadySem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("OCRRecognitionTask released");
        text.set(getImgText(path));
        textTransform();
        ocrReadySem.release();
        searchFullQFullASemaphore.release();

        // If it's not the question release for Full Answers and Entity Questions
        if(!path.equals(Paths.questionPath)) {
            searchEntQFullASemaphore.release();
            searchEntQFullQWithFullATaskSemaphore.release();
        }
        // If it's the question release for Full Question Entity Answers
        else
            searchFullQEntASemaphore.release();
    }

    public  void textTransform() {
        text.set(text.get().replaceAll("\\n", " "));
        if (!text.get().isEmpty() && (text.get().charAt(text.get().length() - 1) == ' ')) {
            text.set(text.get().substring(0, text.get().length() - 1));
        }

        // If question has question marks
        // questionText = questionText.substring(questionText.indexOf("¿"),
        // questionText.indexOf("?") + 1);

        text.set(text.get().replaceAll("\\n", ""));

        String myText = text.get();
        if(path.equals(Paths.questionPath)) {
            myText = myText.substring(1);
            text.set(myText);
        }
    }

    public static String getImgText(String imageLocation) {
        ITesseract instance = new Tesseract();
        try {
            //instance.setLanguage("spa");
            instance.setDatapath("/usr/local/share/tessdata/");
            String imgText = instance.doOCR(new File(imageLocation));
            return imgText;
        } catch (TesseractException e) {
            e.getMessage();
            return "Error while reading image";
        }
    }
}
