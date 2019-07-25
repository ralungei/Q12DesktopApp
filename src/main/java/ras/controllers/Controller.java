package ras.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.text.Font;
import ras.models.ProbabilitySet;
import ras.threads.MainRunnerService;

public class Controller {

    private static long inicio;
    private static long fin;

    @FXML
    private Label titleLabel;

    @FXML
    private TextArea qTextArea;

    @FXML
    private TextField a1TextField;

    @FXML
    private TextField a2TextField;

    @FXML
    private TextField a3TextField;

    @FXML
    private Label firstAnswProbLabel;

    @FXML
    private Label secAnswProbLabel;

    @FXML
    private Label thirdAnswProbLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label positionLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    private Service<ProbabilitySet> mainRunnerService;

    int maxValue;
    int minValue;

    int position = 0;

    //@FXML
    //private ProgressBar imagesProgress;

    //@FXML
    //private ProgressBar ocrProgress;

    @FXML
    private void initialize()
    {
        titleLabel.setFont(new Font("Roboto", 22));
    }


    public void increaseValue(){
        position++;
        positionLabel.setText(Integer.toString(position));
    }

    public void decreaseValue(){
        position--;
        positionLabel.setText(Integer.toString(position));
    }

    public void handleRunAction() {
        initTime();


        mainRunnerService = new MainRunnerService(position);

        progressIndicator.setProgress(-1d);
        qTextArea.setText("");
        a1TextField.setText("");
        a2TextField.setText("");
        a3TextField.setText("");
        timeLabel.setText("");
        firstAnswProbLabel.setText("");
        secAnswProbLabel.setText("");
        thirdAnswProbLabel.setText("");
        clearColors();


        mainRunnerService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent event) {
                ProbabilitySet ps = (ProbabilitySet) event.getSource().getValue();
                qTextArea.setText(ps.getQuestionSet().getQuestionText());
                a1TextField.setText(ps.getQuestionSet().getFirstAnswerText());
                a2TextField.setText(ps.getQuestionSet().getSecondAnswerText());
                a3TextField.setText(ps.getQuestionSet().getThirdAnswerText());

                firstAnswProbLabel.setText(Integer.toString(ps.getFirstAnswer()));
                secAnswProbLabel.setText(Integer.toString(ps.getSecondAnswer()));
                thirdAnswProbLabel.setText(Integer.toString(ps.getThirdAnswer()));

                maxValue = Math.max(ps.getFirstAnswer(), Math.max(ps.getSecondAnswer(), ps.getThirdAnswer()));
                minValue = Math.min(ps.getFirstAnswer(), Math.min(ps.getSecondAnswer(), ps.getThirdAnswer()));

                setProbabilityColor(a1TextField, ps.getFirstAnswer());
                setProbabilityColor(a2TextField, ps.getSecondAnswer());
                setProbabilityColor(a3TextField, ps.getThirdAnswer());

                System.out.println(ps.getQuestionSet().getQuestionText());
                System.out.println("Finished!");
                finishTime();
                showTime();
                progressIndicator.setProgress(1);

            }
        });

        mainRunnerService.start();


        //MainRunnerThread mainThread = new MainRunnerThread(questionText, firstAnswerText, secondAnswerText, thirdAnswerText);
        //imagesProgress.progressProperty().unbind();
        //imagesProgress.progressProperty().bind(imagesTask.progressProperty());
        //new Thread(mainThread).start();
    }


    private void setProbabilityColor(TextField textField, int probability){
        /*
        if(probability >= 9)
            textField.setStyle("-fx-background-color:  #34a853;");
        else if (probability >= 6)
            textField.setStyle("-fx-background-color: #FBBC05;");
        else if (probability > 0)
            textField.setStyle("-fx-background-color:  #EA4335;");
        else
            textField.setStyle("-fx-background-color: #FFFFFF;");
            */
        // ORDERED VALUES
        if(probability == maxValue)
            textField.setStyle("-fx-background-color:  #34a853;");
        else if (probability == minValue) {
            if (probability == 0)
                textField.setStyle("-fx-background-color: #FFFFFF;");
            else textField.setStyle("-fx-background-color:  #EA4335;");
        }
        else
            textField.setStyle("-fx-background-color: #FBBC05;");
    }

    private void clearColors(){
        a1TextField.setStyle("-fx-background-color:  #ffffff;");
        a2TextField.setStyle("-fx-background-color:  #ffffff;");
        a3TextField.setStyle("-fx-background-color:  #ffffff;");

    }

    private void initTime(){
        inicio = System.currentTimeMillis();
    }

    private void finishTime(){
        fin = System.currentTimeMillis();
    }

    private void showTime(){
        float tiempoEjecucion = (float) ((fin - inicio) / 1000);
        timeLabel.setText(tiempoEjecucion + " segundos");
    }

}
