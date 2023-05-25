package pt.unl.fct.di.apdc.geo5.data;

public class QuestionData {

	public String number;
	public String question;
	public String rightAnswer;
	public String wrongAnswer1;
	public String wrongAnswer2;
	public String wrongAnswer3;
	
	public QuestionData() {
		
	}
	
	public QuestionData(String number, String question, String rightAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
		this.number = number;
		this.question = question;
		this.rightAnswer = rightAnswer;
		this.wrongAnswer1 = wrongAnswer1;
		this.wrongAnswer2 = wrongAnswer2;
		this.wrongAnswer3 = wrongAnswer3;
	}
}
