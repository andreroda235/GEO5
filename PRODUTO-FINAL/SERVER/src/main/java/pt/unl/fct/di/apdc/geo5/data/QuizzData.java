package pt.unl.fct.di.apdc.geo5.data;

import java.util.HashSet;
import java.util.Set;

public class QuizzData {
	
	public String title;
	public String description;
	public String keywords;
    public Set<QuestionData> questions = new HashSet<QuestionData>();
	
	public QuizzData() {
		
	}
	
	public QuizzData(String title, String description, String keywords, Set<QuestionData> questions) {
		this.title = title;
		this.description = description;
		this.keywords = keywords;
		this.questions = questions;
	}
}
