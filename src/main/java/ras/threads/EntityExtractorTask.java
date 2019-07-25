package ras.threads;

import com.google.cloud.language.v1.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class EntityExtractorTask implements Runnable{

    private Semaphore ocrSem;
    private Semaphore searchEntQFullASemaphore;
    private Semaphore searchEntQFullQWithFullATaskSemaphore;
    private Semaphore searchFullQEntASemaphore;
    private AtomicReference<String> input;
    private AtomicReference<ArrayList<String>> entities;
    private boolean isQuestion;

    public EntityExtractorTask(Semaphore ocrSem, Semaphore searchEntQFullASemaphore, Semaphore searchEntQFullQWithFullATaskSemaphore, Semaphore searchFullQEntASemaphore, AtomicReference<String> input, AtomicReference<ArrayList<String>> entities, boolean isQuestion) {
        this.ocrSem = ocrSem;
        this.searchEntQFullASemaphore = searchEntQFullASemaphore;
        this.searchEntQFullQWithFullATaskSemaphore = searchEntQFullQWithFullATaskSemaphore;
        this.searchFullQEntASemaphore = searchFullQEntASemaphore;
        this.input = input;
        this.entities = entities;
        this.isQuestion = isQuestion;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting in EntityExtractorTask");
            ocrSem.acquire();
            System.out.println("EntityExtractorTask released");
            entities.set(googleCloudLanguage(input.get()));
            System.out.println("The entities are " + entities);

            // TODO If Entity extraction from question then release the Full Answers Entity Question Search
            if(isQuestion) {
                searchEntQFullASemaphore.release();
                searchEntQFullQWithFullATaskSemaphore.release();
            }
            else
                searchFullQEntASemaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> googleCloudLanguage(String text) throws Exception {
        // Instantiates a client
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            // Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

            return analyzeSyntaxText(text);
            // System.out.printf("Sentiment: %s, %s%n", sentiment.getScore(),
            // sentiment.getMagnitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> analyzeSyntaxText(String text) throws Exception {
        ArrayList<String> entities = new ArrayList<>();

        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                    .setContent(text).setType(Document.Type.PLAIN_TEXT).build();
            AnalyzeSyntaxRequest request = AnalyzeSyntaxRequest.newBuilder().setDocument(doc)
                    .setEncodingType(EncodingType.UTF16).build();
            // analyze the syntax in the given text
            AnalyzeSyntaxResponse response = language.analyzeSyntax(request);
            // print the response
            //System.out.println(" ---------------------------------------");
            //System.out.println("|\tAnálisis de texto iniciado\t|");
            //System.out.println(" ---------------------------------------");
            for (Token token : response.getTokensList()) {
                //System.out.printf("|\t%s", token.getText().getContent());
                //System.out.println("\t " + token.getPartOfSpeech().getTag() + "\t\t\t|");

                if (token.getText().getContent().equalsIgnoreCase("No")) {
                    // TODO
                    // NEGATIVE_QUESTION = true;
                    System.out.println("Negative question");
                }

                if (token.getPartOfSpeech().getTag().toString().equals("NOUN")
                        || token.getPartOfSpeech().getTag().toString().equals("ADJ") || token.getPartOfSpeech().getTag().toString().equals("NUM"))
                    entities.add(token.getText().getContent());
            }

            return entities;
        }
    }


}
