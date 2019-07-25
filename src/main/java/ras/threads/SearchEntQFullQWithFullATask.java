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

public class SearchEntQFullQWithFullATask implements Runnable {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final int numOfSearches = 10;

    private AtomicReference<String> questionText;
    private AtomicReference<String> firstAnswerText;
    private AtomicReference<String> secondAnswerText;
    private AtomicReference<String> thirdAnswerText;

    private AtomicInteger firstAnswerProb = new AtomicInteger(0);
    private AtomicInteger secAnswerProb = new AtomicInteger(0);
    private AtomicInteger thirdAnswerProb = new AtomicInteger(0);

    private AtomicReference<ArrayList<String>> questionEntities;

    private Semaphore toUISem;
    private Semaphore searchEntQFullASemaphore;

    private AtomicReference<ArrayList<Integer>> resultsEntQFullA;


    public SearchEntQFullQWithFullATask(Semaphore toUISem, Semaphore searchEntQFullASemaphore, AtomicReference<ArrayList<Integer>> resultsEntQFullA, AtomicReference<String> questionText,
                                        AtomicReference<String> firstAnswerText, AtomicReference<String> secondAnswerText,
                                        AtomicReference<String> thirdAnswerText, AtomicReference<ArrayList<String>> questionEntities) {
        this.toUISem = toUISem;
        this.searchEntQFullASemaphore = searchEntQFullASemaphore;
        this.resultsEntQFullA = resultsEntQFullA;
        this.questionText = questionText;
        this.firstAnswerText = firstAnswerText;
        this.secondAnswerText = secondAnswerText;
        this.thirdAnswerText = thirdAnswerText;
        this.questionEntities = questionEntities;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting in SearchEntQFullATask");
            searchEntQFullASemaphore.acquire();
            System.out.println("Executing SearchEntQFullATask");

            System.out.println("Question entities are " + questionEntities.toString());

            String searchTerm1 = questionText.get() + " " + firstAnswerText.get();
            String searchTerm2 = questionText.get() + " " + secondAnswerText.get();
            String searchTerm3 = questionText.get() + " " + thirdAnswerText.get();

            Thread searchTread1 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm1, null, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, null, null, null, null));
            Thread searchTread2 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm2, null, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, null, null, null, null));
            Thread searchTread3 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm3, null, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, null, null, null, null));


            searchTread1.start();
            searchTread2.start();
            searchTread3.start();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
