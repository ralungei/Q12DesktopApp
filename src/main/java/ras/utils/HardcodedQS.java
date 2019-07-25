package ras.utils;

import java.util.concurrent.atomic.AtomicReference;

public class HardcodedQS {
    private AtomicReference<String> questionText = new AtomicReference<>();
    private AtomicReference<String> firstAnswerText = new AtomicReference<>();
    private AtomicReference<String> secondAnswerText = new AtomicReference<>();
    private AtomicReference<String> thirdAnswerText = new AtomicReference<>();

    public void setQuestionText(AtomicReference<String> questionText) {
        this.questionText = questionText;
    }

    public void setFirstAnswerText(AtomicReference<String> firstAnswerText) {
        this.firstAnswerText = firstAnswerText;
    }

    public void setSecondAnswerText(AtomicReference<String> secondAnswerText) {
        this.secondAnswerText = secondAnswerText;
    }

    public void setThirdAnswerText(AtomicReference<String> thirdAnswerText) {
        this.thirdAnswerText = thirdAnswerText;
    }
}
