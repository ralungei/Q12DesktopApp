package ras.threads;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import ras.models.ProbabilitySet;
import ras.models.QuestionSet;
import ras.utils.FileQS;
import ras.utils.Paths;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public class MainRunnerService extends Service<ProbabilitySet> {

    private final FileQS fileQS = new FileQS();

    private AtomicReference<String> questionText = new AtomicReference<>();
    private AtomicReference<String> firstAnswerText = new AtomicReference<>();
    private AtomicReference<String> secondAnswerText = new AtomicReference<>();
    private AtomicReference<String> thirdAnswerText = new AtomicReference<>();

    private AtomicReference<ArrayList<String>> questionEntities = new AtomicReference<>();
    private AtomicReference<ArrayList<String>> firstAnswerEntities = new AtomicReference<>();
    private AtomicReference<ArrayList<String>> secAnswerEntities = new AtomicReference<>();
    private AtomicReference<ArrayList<String>> thirdAnswerEntities = new AtomicReference<>();

    private AtomicReference<ArrayList<Integer>> resultsFullQFullA = new AtomicReference<>();
    private AtomicReference<ArrayList<Integer>> resultsFullQEntA = new AtomicReference<>();
    private AtomicReference<ArrayList<Integer>> resultsEntQFullA = new AtomicReference<>();
    private AtomicReference<ArrayList<Integer>> resultsEntQFullQWithFullA = new AtomicReference<>();

    private Semaphore toUISemaphore = new Semaphore(-4);

    private Semaphore searchFullQFullASemaphore = new Semaphore(-3);
    private Semaphore searchFullQEntASemaphore = new Semaphore(-3);
    private Semaphore searchEntQFullASemaphore = new Semaphore(-3);
    private Semaphore searchEntQFullQWithFullATaskSemaphore = new Semaphore(-3);

    private Semaphore crop1Sem = new Semaphore(0);
    private Semaphore crop2Sem = new Semaphore(0);
    private Semaphore crop3Sem = new Semaphore(0);
    private Semaphore crop4Sem = new Semaphore(0);

    private Semaphore ocr1Sem = new Semaphore(0);
    private Semaphore ocr2Sem = new Semaphore(0);
    private Semaphore ocr3Sem = new Semaphore(0);
    private Semaphore ocr4Sem = new Semaphore(0);

    int firstAnswerProb;
    int secAnswerProb;
    int thirdAnswerProb;

    int position;

    public MainRunnerService(int position){
        this.position = position;
    }

    protected Task<ProbabilitySet> createTask() {
        return new Task<ProbabilitySet>() {
            @Override
            protected ProbabilitySet call() throws Exception {

                /*
                ArrayList<QuestionSet> fileQSArray = fileQS.getQSArray();

                System.out.println("The file array for position is " + fileQSArray.get(position).getQuestionText());

                AtomicReference<String> FILEquestionText = new AtomicReference<>(fileQSArray.get(position).getQuestionText());
                AtomicReference<String> FILEfirstAnswerText = new AtomicReference<>(fileQSArray.get(position).getFirstAnswerText());
                AtomicReference<String> FILEsecondAnswerText = new AtomicReference<>(fileQSArray.get(position).getSecondAnswerText());
                AtomicReference<String> FILEthirdAnswerText = new AtomicReference<>(fileQSArray.get(position).getThirdAnswerText());
                */

                resultsFullQFullA.set(new ArrayList<>());
                resultsEntQFullA.set(new ArrayList<>());
                resultsEntQFullQWithFullA.set(new ArrayList<>());
                resultsFullQEntA.set(new ArrayList<>());

                Thread screencaptureThread = new Thread(new ScreencaptureTask(Paths.path));

                // MACBOOK PRO 2012 SETTINGS
                /*
                Thread cropImageThread1 = new Thread(new CropImageTask(crop1Sem, Paths.path, Paths.questionPath, 2060, 932, 732, 305));
                Thread cropImageThread2 = new Thread(new CropImageTask(crop2Sem, Paths.path, Paths.firstAnswerPath, 2108, 1266, 638, 90));
                Thread cropImageThread3 = new Thread(new CropImageTask(crop3Sem, Paths.path, Paths.secondAnswerPath, 2108, 1408, 638, 90));
                Thread cropImageThread4 = new Thread(new CropImageTask(crop4Sem, Paths.path, Paths.thirdAnswerPath, 2108, 1552, 638, 90));
                */

                Thread cropImageThread1 = new Thread(new CropImageTask(crop1Sem, Paths.path, Paths.questionPath, 4020, 1400, 957, 384));
                Thread cropImageThread2 = new Thread(new CropImageTask(crop2Sem, Paths.path, Paths.firstAnswerPath, 4098, 1802, 820, 110));
                Thread cropImageThread3 = new Thread(new CropImageTask(crop3Sem, Paths.path, Paths.secondAnswerPath, 4098, 1982, 820, 110));
                Thread cropImageThread4 = new Thread(new CropImageTask(crop4Sem, Paths.path, Paths.thirdAnswerPath, 4098, 2162, 820, 110));


                screencaptureThread.start();
                try {
                    // Wait for the screenscapture
                    screencaptureThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Launch screenshot crops
                cropImageThread1.start();
                cropImageThread2.start();
                cropImageThread3.start();
                cropImageThread4.start();

                Thread ocrThread1 = new Thread(new OCRRecognitionTask(ocr1Sem, crop1Sem, searchFullQFullASemaphore, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore,  questionText, Paths.questionPath));
                Thread ocrThread2 = new Thread(new OCRRecognitionTask(ocr2Sem, crop2Sem, searchFullQFullASemaphore, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, firstAnswerText, Paths.firstAnswerPath));
                Thread ocrThread3 = new Thread(new OCRRecognitionTask(ocr3Sem, crop3Sem, searchFullQFullASemaphore, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, secondAnswerText, Paths.secondAnswerPath));
                Thread ocrThread4 = new Thread(new OCRRecognitionTask(ocr4Sem, crop4Sem, searchFullQFullASemaphore, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, thirdAnswerText, Paths.thirdAnswerPath));


                // Launch OCR analysis
                ocrThread1.start();
                ocrThread2.start();
                ocrThread3.start();
                ocrThread4.start();

                // UNCOMMENT FOR NORMAL WORKING
                Thread entThread1 = new Thread(new EntityExtractorTask(ocr1Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, questionText, questionEntities, true));
                Thread entThread2 = new Thread(new EntityExtractorTask(ocr2Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, firstAnswerText, firstAnswerEntities, false));
                Thread entThread3 = new Thread(new EntityExtractorTask(ocr3Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, secondAnswerText, secAnswerEntities, false));
                Thread entThread4 = new Thread(new EntityExtractorTask(ocr4Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, thirdAnswerText, thirdAnswerEntities, false));


                // UNCOMMENT FOR FILE LOADING PURPOSE
                /*
                Thread entThread1 = new Thread(new EntityExtractorTask(ocr1Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, FILEquestionText, questionEntities, true));
                Thread entThread2 = new Thread(new EntityExtractorTask(ocr2Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, FILEfirstAnswerText, firstAnswerEntities, false));
                Thread entThread3 = new Thread(new EntityExtractorTask(ocr3Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, FILEsecondAnswerText, secAnswerEntities, false));
                Thread entThread4 = new Thread(new EntityExtractorTask(ocr4Sem, searchEntQFullASemaphore, searchEntQFullQWithFullATaskSemaphore, searchFullQEntASemaphore, FILEthirdAnswerText, thirdAnswerEntities, false));
                */


                // Launch entities analysis


                entThread1.start();
                entThread2.start();
                entThread3.start();
                entThread4.start();

                // Launch Full Questions & Full Answers search
                // UNCOMMENT FOR NORMAL WORKING

                Thread searchFullQFullAThread = new Thread(new SearchFullQFullATask(toUISemaphore, searchFullQFullASemaphore, resultsFullQFullA, questionText, firstAnswerText, secondAnswerText, thirdAnswerText));
                searchFullQFullAThread.start();

                // UNCOMMENT FOR FILE LOADING PURPOSE
                /*
                Thread searchFullQFullAThread = new Thread(new SearchFullQFullATask(toUISemaphore, searchFullQFullASemaphore, resultsFullQFullA, FILEquestionText, FILEfirstAnswerText, FILEsecondAnswerText, FILEthirdAnswerText));
                searchFullQFullAThread.start();
                */


                // Launch Full Questions & Entity Answers search
                // UNCOMMENT FOR NORMAL WORKING

                Thread searchFullQEntATaskThread = new Thread(new SearchFullQEntATask(toUISemaphore, searchFullQEntASemaphore, resultsFullQEntA, questionText, firstAnswerText, secondAnswerText, thirdAnswerText, firstAnswerEntities, secAnswerEntities, thirdAnswerEntities));
                searchFullQEntATaskThread.start();

                // UNCOMMENT FOR FILE LOADING PURPOSE
                /*
                Thread searchFullQEntATaskThread = new Thread(new SearchFullQEntATask(toUISemaphore, searchFullQEntASemaphore, resultsFullQEntA, FILEquestionText, FILEfirstAnswerText, FILEsecondAnswerText, FILEthirdAnswerText, firstAnswerEntities, secAnswerEntities, thirdAnswerEntities));
                searchFullQEntATaskThread.start();
                */

                // Launch Entity Questions & Full Answers search
                // UNCOMMENT FOR NORMAL WORKING

                // TODO ANALYZE IF THIS SEARCH SHOULD BE INCLUDED OR NOT | FOR NOW IT'S BEING DISABLED
                //Thread searchEntQFullA = new Thread(new SearchEntQFullATask(toUISemaphore, searchEntQFullASemaphore, resultsEntQFullA, questionText, firstAnswerText, secondAnswerText, thirdAnswerText, questionEntities));
                //searchEntQFullA.start();

                // UNCOMMENT FOR FILE LOADING PURPOSE
                /*
                Thread searchEntQFullA = new Thread(new SearchEntQFullATask(toUISemaphore, searchEntQFullASemaphore, resultsEntQFullA, FILEquestionText, FILEfirstAnswerText, FILEsecondAnswerText, FILEthirdAnswerText, questionEntities));
                searchEntQFullA.start();
                */

                // Launch Search by Entity Questions searching each answer within the question
                // UNCOMMENT FOR NORMAL WORKING

                Thread searchEntQFullQWithFullATask = new Thread(new SearchEntQFullQWithFullATask(toUISemaphore, searchEntQFullQWithFullATaskSemaphore, resultsEntQFullQWithFullA, questionText, firstAnswerText, secondAnswerText, thirdAnswerText, questionEntities));
                searchEntQFullQWithFullATask.start();

                /*
                // UNCOMMENT FOR FILE LOADING PURPOSE
                Thread searchEntQFullQWithFullATask = new Thread(new SearchEntQFullQWithFullATask(toUISemaphore, searchEntQFullQWithFullATaskSemaphore, resultsEntQFullQWithFullA, FILEquestionText, FILEfirstAnswerText, FILEsecondAnswerText, FILEthirdAnswerText, questionEntities));
                searchEntQFullQWithFullATask.start();
                */




                toUISemaphore.acquire();
                System.out.println("toUISemaphore was acquired in MainRunnerService");
                System.out.println("ResultsFullQfULLA " + resultsFullQFullA.get());
                System.out.println("ResultsEntQFullA " + resultsEntQFullA.get());
                System.out.println("ResultsEntQFullQWithFullA " + resultsEntQFullQWithFullA.get());
                System.out.println("ResultsFullQEntA " + resultsFullQEntA.get());



                System.out.println("------------------------------------------------------------------------------");
                /*
                int firstAnswerProbOriginal = (resultsFullQFullA.get().get(0) + resultsEntQFullA.get().get(0) + resultsEntQFullQWithFullA.get().get(0)) / 3;
                int secAnswerProbOriginal = (resultsFullQFullA.get().get(1) + resultsEntQFullA.get().get(1)+ resultsEntQFullQWithFullA.get().get(1)) / 3;
                int thirdAnswerProbOriginal = (resultsFullQFullA.get().get(2) + resultsEntQFullA.get().get(2)+ resultsEntQFullQWithFullA.get().get(2)) / 3;
                ArrayList<Integer> originalValuesSumandoTodoAntes = new ArrayList<>();
                originalValuesSumandoTodoAntes.add(firstAnswerProbOriginal);
                originalValuesSumandoTodoAntes.add(secAnswerProbOriginal);
                originalValuesSumandoTodoAntes.add(thirdAnswerProbOriginal);
                ArrayList<Integer> originalValuesSumandoTodoAntesOverTen = convertResultOverTen(originalValuesSumandoTodoAntes);
                System.out.println("Todos los valores sumados y puestos despues sobre 10" + originalValuesSumandoTodoAntesOverTen);
                */
                System.out.println("------------------------------------------------------------------------------");

                ArrayList<Integer> resultsFullQFullAOverTen = convertResultOverTen(resultsFullQFullA.get());
                ArrayList<Integer> resultsEntQFullAOverTen = convertResultOverTen(resultsEntQFullA.get());
                ArrayList<Integer> resultsEntQFullQWithFullAOverTen = convertResultOverTen(resultsEntQFullQWithFullA.get());
                ArrayList<Integer> resultsFullQEntAOverTen = convertResultOverTen(resultsFullQEntA.get());


                System.out.println("ResultsFullQfULLA Over Ten " + resultsFullQFullAOverTen);
                System.out.println("ResultsEntQFullA Over Ten " + resultsEntQFullAOverTen);
                System.out.println("ResultsEntQFullQWithFullA Over Ten " + resultsEntQFullQWithFullAOverTen);
                System.out.println("ResultsFullQEntAOverTen Over Ten " + resultsFullQEntAOverTen);


                //System.out.println("resultsFullQFullAOverTen " + resultsFullQFullAOverTen);
                //System.out.println("resultsEntQFullAOverTen " + resultsEntQFullAOverTen);

                //ASIGNAR

                ArrayList<Integer> firstAnswerProbArray = new ArrayList<>();
                ArrayList<Integer> secAnswerProbArray = new ArrayList<>();
                ArrayList<Integer> thirdAnswerProbArray = new ArrayList<>();

                ArrayList<ArrayList<Integer>> masterResults = new ArrayList<>();
                masterResults.add(resultsFullQFullAOverTen);
                masterResults.add(resultsEntQFullAOverTen);
                masterResults.add(resultsEntQFullQWithFullAOverTen);
                masterResults.add(resultsFullQEntAOverTen);

                ArrayList<Integer> averagesArray = getAverageArray(masterResults);

                firstAnswerProb = averagesArray.get(0);
                secAnswerProb = averagesArray.get(1);
                thirdAnswerProb = averagesArray.get(2);

                //firstAnswerProb = (resultsFullQFullAOverTen.get(0) + resultsEntQFullAOverTen.get(0) + resultsEntQFullQWithFullAOverTen.get(0)) / 3;
                //secAnswerProb = (resultsFullQFullAOverTen.get(1) + resultsEntQFullAOverTen.get(1)+ resultsEntQFullQWithFullAOverTen.get(1)) / 3;
                //thirdAnswerProb = (resultsFullQFullAOverTen.get(2) + resultsEntQFullAOverTen.get(2)+ resultsEntQFullQWithFullAOverTen.get(2)) / 3;

                // UNCOMMENT FOR NORMAL WORKING
                /*
                if(questionText.get().contains("NO")) {
                    revertResults();
                }
                */

                // UNCOMMENT FOR FILE LOADING PURPOSE
                System.out.println("Looking for NO in " + questionText.get());
                if(questionText.get().contains("NO")) {
                    System.out.println("Negative question in MainRunnerService");
                    revertResults();
                }

                //convertResultsOverTen();
                // UNCOMMENT FOR NORMAL WORKING
                //QuestionSet qs = new QuestionSet(questionText.get(), firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get());

                // UNCOMMENT FOR FILE LOADING PURPOSE
                QuestionSet qs = new QuestionSet(questionText.get(), firstAnswerText.get(), secondAnswerText.get(), thirdAnswerText.get());

                return new ProbabilitySet(qs, firstAnswerProb, secAnswerProb, thirdAnswerProb);
            }
        };
    }

    public ArrayList<Integer> getAverageArray(ArrayList<ArrayList<Integer>> arraysOfArrays){
        int total = arraysOfArrays.size();

        ArrayList<Integer> averageArray = new ArrayList<Integer>();

        int[] valoresSumados = new int[3];

        for(int i=0; i<arraysOfArrays.size(); i++){
            ArrayList<Integer> anArray = arraysOfArrays.get(i);

            if(anArray.size() > 0){
                if(Collections.max(anArray) == 0)
                    total--;

                for(int j=0; j<anArray.size(); j++)
                    valoresSumados[j] += anArray.get(j);
            }
            else
                total--;

        }

        System.out.println("Los valores sumados son " + valoresSumados[0] + " " + valoresSumados[1] + " " + valoresSumados[2] + " con un total de " + total);

        for(int x=0; x<valoresSumados.length; x++)
            averageArray.add(valoresSumados[x]/total);

        return averageArray;
    }


    public void revertResults() {
        firstAnswerProb = 10 - firstAnswerProb;
        secAnswerProb = 10 - secAnswerProb;
        thirdAnswerProb = 10 - thirdAnswerProb;
    }

    public ArrayList<Integer> revertResults(ArrayList<Integer> results) {
        ArrayList<Integer> revertedResults = new ArrayList<>();
        int reverted;
        for(int i=0; i<results.size(); i++) {
            reverted = 10 - results.get(i);
            revertedResults.add(reverted);
        }
        return revertedResults;
    }

    private ArrayList<Integer> convertResultOverTen(ArrayList<Integer> results) {
        ArrayList<Integer> overTenArray = new ArrayList<>();
        int total = 0;
        for(int i=0; i<results.size(); i++){
            total += results.get(i);
        }

        int overTenValue;

        for(int j=0; j<results.size(); j++){
            if(results.get(j) != 0)
                overTenValue = overTenValue(results.get(j), total);
            else
                overTenValue = 0;
            overTenArray.add(overTenValue);
        }

        return overTenArray;
    }

    private static int overTenValue(int value, int total) {
        return value * 10 / total;
    }

}
