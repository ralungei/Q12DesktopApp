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

public class SearchEntQFullATask implements Runnable {

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


    public SearchEntQFullATask(Semaphore toUISem, Semaphore searchEntQFullASemaphore,  AtomicReference<ArrayList<Integer>> resultsEntQFullA, AtomicReference<String> questionText,
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

            String searchTerm1 = firstAnswerText.get();
            String searchTerm2 = secondAnswerText.get();
            String searchTerm3 = thirdAnswerText.get();

            Thread searchTread1 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm1, firstAnswerProb, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, questionEntities, null, null, null));
            Thread searchTread2 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm2, secAnswerProb, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, questionEntities, null, null, null));
            Thread searchTread3 = new Thread(new GoogleSearchTask(toUISem, resultsEntQFullA, searchTerm3, thirdAnswerProb, firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get(), firstAnswerProb, secAnswerProb, thirdAnswerProb, questionEntities, null, null, null));


            searchTread1.start();
            searchTread2.start();
            searchTread3.start();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Perform the search in google and analyze the results
     */
    public void googleEntQFullASearch(String searchTerm, AtomicInteger answerProb) {
        System.out.println("Iniciando búsqueda de pregunta en Google.");

        String searchURL = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num=" + numOfSearches;


        System.out.println(searchURL);
        Document doc;
        try {
//			String userAgent = "Mozilla/5.0 Chrome/26.0.1410.64 Safari/537.31";
            String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";

            doc = Jsoup.connect(searchURL).userAgent(userAgent).followRedirects(true).get();

            System.out.println("Búsqueda en Google finalizada.");
            // below will print HTML data, save it to a file and open in browser to compare
//			 System.out.println(doc.html());

            // If google search results HTML change the <h3 class="r" to <h3 class="r1"
            // we need to change below accordingly
            // System.out.println(doc.toString());

//			Elements results = doc.select("h3.r > a");
//			Elements results = doc.select("div.s > span");
            Elements results = doc.select(".st");

//			System.out.println(results.toString());

            for (int i = 0; i < results.size(); i++) {
                Element result = results.get(i);
                String content = result.text();
                countEntireAnswersInSearchResult(content, i, answerProb);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void countEntireAnswersInSearchResult(String searchContent, int numResultado, AtomicInteger answerProb) {
        List<String> tokens = questionEntities.get();

        String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";

        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(searchContent);

        while (matcher.find()) {
            int weighing = ProbabilityUtils.weigh(numResultado);
            answerProb.set(answerProb.get() + weighing);

            // System.out.println(matcher.group());

            /*
            if (matcher.group().equals(firstAnswerText.get()))
                firstAnswerProb.set(firstAnswerProb.get() + weighing);
            else if (matcher.group().equals(secondAnswerText.get()))
                secAnswerProb.set(secAnswerProb.get() + weighing);
            else if (matcher.group().equalsIgnoreCase(thirdAnswerText.get()))
                thirdAnswerProb.set(thirdAnswerProb.get() + weighing);
            */

        }
    }






}
