package ras.utils;

import ras.models.QuestionSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



public class FileQS {

	private final ArrayList<QuestionSet> qsArray = new ArrayList<QuestionSet>();
	private static final String path = "/Users/ras/Q12/questions.txt";
	
	public FileQS () {
		readQSFile();
	}
	
	public void readQSFile() {
		QuestionSet qa = new QuestionSet();
		
		int i = 0;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String st;
			while ((st = br.readLine()) != null) {
				switch(i) {
				case 0:
					qa.setQuestionText(st);
					break;
				case 1:
					qa.setFirstAnswerText(st);
					break;
				case 2:
					qa.setSecondAnswerText(st);
					break;
				case 3:
					qa.setThirdAnswerText(st);
					qsArray.add(qa);
					break;
				default:
					System.err.println("Invalid i value " + i);
				}
				
				if (i != 3)
					i++;
				else {
					i = 0;
					qa = new QuestionSet();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public ArrayList<QuestionSet> getQSArray() {
		return qsArray;
	}

	public void printResults() { System.out.println("Number of questions sets in array: " + qsArray.size()); }
	
}