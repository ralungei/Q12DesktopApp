package ras.threads;

import com.sun.deploy.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ras.utils.ProbabilityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFullQFullATask implements Runnable {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final int numOfSearches = 10;

    private AtomicReference<String> questionText;
    private AtomicReference<String> firstAnswerText;
    private AtomicReference<String> secondAnswerText;
    private AtomicReference<String> thirdAnswerText;

    private AtomicInteger firstAnswerProb = new AtomicInteger(0);
    private AtomicInteger secAnswerProb = new AtomicInteger(0);
    private AtomicInteger thirdAnswerProb = new AtomicInteger(0);

    private AtomicReference<ArrayList<Integer>> resultsFullQFullA;

    private Semaphore toUISem;
    private Semaphore fullSearchSem;

    public SearchFullQFullATask(Semaphore toUISem, Semaphore fullSearchSem,
                                AtomicReference<ArrayList<Integer>> resultsFullQFullA , AtomicReference<String> questionText,
                          AtomicReference<String> firstAnswerText, AtomicReference<String> secondAnswerText,
                          AtomicReference<String> thirdAnswerText) {
        this.toUISem = toUISem;
        this.fullSearchSem = fullSearchSem;
        this.resultsFullQFullA = resultsFullQFullA;
        this.questionText = questionText;
        this.firstAnswerText = firstAnswerText;
        this.secondAnswerText = secondAnswerText;
        this.thirdAnswerText = thirdAnswerText;
    }

    @Override
    public void run() {
        try {
            fullSearchSem.acquire();
            System.out.println("Test in FullSearchTask is " + questionText);
            System.out.println("Executing SearchFullQFullATask");

            String searchTerm = questionText.get();

            Thread searchTread = new Thread(new GoogleSearchTask(toUISem, resultsFullQFullA, searchTerm, null, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, null, null, null, null));
            searchTread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
