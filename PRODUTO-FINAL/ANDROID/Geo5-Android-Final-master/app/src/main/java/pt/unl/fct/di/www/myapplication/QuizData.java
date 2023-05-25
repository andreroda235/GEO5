package pt.unl.fct.di.www.myapplication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class QuizData implements Serializable {

    private String title;
    private String description;
    private String keywords;
    private Set<QuestionData> questions = new HashSet<>();

    public QuizData(){}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getKeywords() {
        return keywords;
    }

    public Set<QuestionData> getQuestions() {
        return questions;
    }
}
