package ras.models;

public class QuestionSet {

	private String questionText;
	private String firstAnswerText;
	private String secondAnswerText;
	private String thirdAnswerText;

	public QuestionSet() {
		questionText = "";
		firstAnswerText = "";
		secondAnswerText = "";
		thirdAnswerText = "";
	}

	public QuestionSet(String q, String fa, String sa, String ta) {
		questionText = q;
		firstAnswerText = fa;
		secondAnswerText = sa;
		thirdAnswerText = ta;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getFirstAnswerText() {
		return firstAnswerText;
	}

	public void setFirstAnswerText(String firstAnswerText) {
		this.firstAnswerText = firstAnswerText;
	}

	public String getSecondAnswerText() {
		return secondAnswerText;
	}

	public void setSecondAnswerText(String secondAnswerText) {
		this.secondAnswerText = secondAnswerText;
	}

	public String getThirdAnswerText() {
		return thirdAnswerText;
	}

	public void setThirdAnswerText(String thirdAnswerText) {
		this.thirdAnswerText = thirdAnswerText;
	}

}
