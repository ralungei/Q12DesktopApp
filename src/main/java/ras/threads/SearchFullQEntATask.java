package ras.threads;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class SearchFullQEntATask implements Runnable {

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
    private static final int numOfSearches = 10;

    private AtomicReference<String> questionText;
    private AtomicReference<String> firstAnswerText;
    private AtomicReference<String> secondAnswerText;
    private AtomicReference<String> thirdAnswerText;

    private AtomicInteger firstAnswerProb = new AtomicInteger(0);
    private AtomicInteger secAnswerProb = new AtomicInteger(0);
    private AtomicInteger thirdAnswerProb = new AtomicInteger(0);

    private AtomicReference<ArrayList<String>> firstAnswerEntities;
    private AtomicReference<ArrayList<String>> secondAnswerEntities;
    private AtomicReference<ArrayList<String>> thirdAnswerEntities;


    private AtomicReference<ArrayList<Integer>> resultsFullQFullA;

    private Semaphore toUISem;
    private Semaphore answEntitySearchSem;

    public SearchFullQEntATask(Semaphore toUISem,
                               Semaphore answEntitySearchSem,
                               AtomicReference<ArrayList<Integer>> resultsFullQFullA ,
                               AtomicReference<String> questionText,
                               AtomicReference<String> firstAnswerText,
                               AtomicReference<String> secondAnswerText,
                               AtomicReference<String> thirdAnswerText,
                               AtomicReference<ArrayList<String>> firstAnswerEntities,
                               AtomicReference<ArrayList<String>> secondAnswerEntities,
                               AtomicReference<ArrayList<String>> thirdAnswerEntities
                               ) {
        this.toUISem = toUISem;
        this.answEntitySearchSem = answEntitySearchSem;
        this.resultsFullQFullA = resultsFullQFullA;
        this.questionText = questionText;
        this.firstAnswerText = firstAnswerText;
        this.secondAnswerText = secondAnswerText;
        this.thirdAnswerText = thirdAnswerText;
        this.firstAnswerEntities = firstAnswerEntities;
        this.secondAnswerEntities = secondAnswerEntities;
        this.thirdAnswerEntities = thirdAnswerEntities;
    }

    @Override
    public void run() {
        try {
            answEntitySearchSem.acquire();
            System.out.println("Test in FullSearchTask is " + questionText);
            System.out.println("Executing SearchFullQFullATask");

            // TODO Check if answers have more than one word

            String searchTerm = questionText.get();

            Thread searchTread = new Thread(new GoogleSearchTask(toUISem, resultsFullQFullA, searchTerm, null, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, null, firstAnswerEntities, secondAnswerEntities, thirdAnswerEntities));
            searchTread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
