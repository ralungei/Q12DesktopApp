package ras.models;

public class ProbabilitySet {

    private QuestionSet questionSet;
    private static int firstAnswer;
    private static int secondAnswer;
    private static int thirdAnswer;

    public ProbabilitySet(QuestionSet qs, int fa, int sa, int ta){
        questionSet = qs;
        firstAnswer = fa;
        secondAnswer = sa;
        thirdAnswer = ta;
    }

    public int getFirstAnswer() {
        return firstAnswer;
    }

    public int getSecondAnswer() {
        return secondAnswer;
    }

    public int getThirdAnswer() {
        return thirdAnswer;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }
}
