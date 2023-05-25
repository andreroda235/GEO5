package pt.unl.fct.di.www.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizzActivity extends AppCompatActivity {

    private static final String TAG = "QuizzActivity";
    private final String red = "#FF0000";
    private final String green = "#46C47E";
    private final String blue = "#a2e4e7";

    private List<QuestionData> questions;
    private QuizData quiz;
    private RelativeLayout quizLayout;

    private TextView title;

    private TextView question;

    private TextView answer1;
    private TextView answer2;
    private TextView answer3;
    private TextView answer4;

    private RelativeLayout answer1Layout;
    private RelativeLayout answer2Layout;
    private RelativeLayout answer3Layout;
    private RelativeLayout answer4Layout;


    private String currentRightAnswer;
    private RelativeLayout currentRightLayout;
    private Button nextBtn;

    private int correctAnswers;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);
        questions = null;
        quiz = null;
        quizLayout = findViewById(R.id.quiz_layout);

        title = findViewById(R.id.quiz_title);

        question = findViewById(R.id.quiz_question);

        answer1 = findViewById(R.id.quiz_answer1);
        answer2 = findViewById(R.id.quiz_answer2);
        answer3 = findViewById(R.id.quiz_answer3);
        answer4 = findViewById(R.id.quiz_answer4);

        currentRightAnswer = null;
        currentRightLayout = null;

        answer1Layout = findViewById(R.id.quiz_answer1_layout);
        answer2Layout = findViewById(R.id.quiz_answer2_layout);
        answer3Layout = findViewById(R.id.quiz_answer3_layout);
        answer4Layout = findViewById(R.id.quiz_answer4_layout);

        nextBtn = findViewById(R.id.quiz_next_button);

        correctAnswers = 0;
        totalQuestions = 0;

        loadQuiz();
    }

    private void createContent(QuestionData data){

        title.setText(quiz.getTitle());
        question.setText("Question: " + data.getQuestion());

        List<String> answers = new ArrayList<>();
        answers.add(data.getRightAnswer());
        answers.add(data.getWrongAnswer1());
        answers.add(data.getWrongAnswer2());
        answers.add(data.getWrongAnswer3());

        currentRightAnswer = data.getRightAnswer();

        Random r = new Random();
        int i = r.nextInt(answers.size());
        if(answers.get(i).equals(currentRightAnswer))
            currentRightLayout = answer1Layout;
        answer1.setText(answers.remove(i));

        i = r.nextInt(answers.size());
        if(answers.get(i).equals(currentRightAnswer))
            currentRightLayout = answer2Layout;
        answer2.setText(answers.remove(i));

        i = r.nextInt(answers.size());
        if(answers.get(i).equals(currentRightAnswer))
            currentRightLayout = answer3Layout;
        answer3.setText(answers.remove(i));

        if(answers.get(0).equals(currentRightAnswer))
            currentRightLayout = answer4Layout;
        answer4.setText(answers.remove(0));
    }

    public void checkAnswer(View view){
        int id = view.getId();
        TextView answer = (TextView) view;
        switch (id){
            case R.id.quiz_answer1:{
                if(answer.getText().toString().equals(currentRightAnswer)){
                    answer1Layout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayCorrectMsg();
                } else {
                    answer1Layout.getBackground().setColorFilter(Color.parseColor(red), PorterDuff.Mode.SRC_ATOP);
                    currentRightLayout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayWrongMsg();
                }
                break;
            }
            case R.id.quiz_answer2:{
                if(answer.getText().toString().equals(currentRightAnswer)){
                    answer2Layout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayCorrectMsg();
                } else {
                    answer2Layout.getBackground().setColorFilter(Color.parseColor(red), PorterDuff.Mode.SRC_ATOP);
                    currentRightLayout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayWrongMsg();
                }
                break;
            }
            case R.id.quiz_answer3:{
                if(answer.getText().toString().equals(currentRightAnswer)){
                    answer3Layout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayCorrectMsg();
                } else {
                    answer3Layout.getBackground().setColorFilter(Color.parseColor(red), PorterDuff.Mode.SRC_ATOP);
                    currentRightLayout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayWrongMsg();
                }
                break;
            }
            case R.id.quiz_answer4:{
                if(answer.getText().toString().equals(currentRightAnswer)){
                    answer4Layout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayCorrectMsg();
                } else {
                    answer4Layout.getBackground().setColorFilter(Color.parseColor(red), PorterDuff.Mode.SRC_ATOP);
                    currentRightLayout.getBackground().setColorFilter(Color.parseColor(green), PorterDuff.Mode.SRC_ATOP);
                    displayWrongMsg();
                }
                break;
            }
        }
        showNextBtn();
    }

    public void nextQuestion(View btn){
        if(questions.size() > 0){
            currentRightAnswer = null;
            currentRightLayout = null;
            resetColors();
            hideNextBtn();
            QuestionData nextQuestion = questions.remove(0);
            createContent(nextQuestion);

        }else{
            Toast.makeText(QuizzActivity.this, "Done! You got " + correctAnswers + " out of " + totalQuestions, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showNextBtn(){
        nextBtn.setVisibility(View.VISIBLE);
    }

    private void hideNextBtn(){
        nextBtn.setVisibility(View.GONE);
    }

    private void resetColors() {
        answer1Layout.getBackground().setColorFilter(Color.parseColor(blue), PorterDuff.Mode.SRC_ATOP);
        answer2Layout.getBackground().setColorFilter(Color.parseColor(blue), PorterDuff.Mode.SRC_ATOP);
        answer3Layout.getBackground().setColorFilter(Color.parseColor(blue), PorterDuff.Mode.SRC_ATOP);
        answer4Layout.getBackground().setColorFilter(Color.parseColor(blue), PorterDuff.Mode.SRC_ATOP);
    }

    private void displayCorrectMsg() {
        Toast.makeText(QuizzActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
        correctAnswers++;
    }

    private void displayWrongMsg() {
        Toast.makeText(QuizzActivity.this, "Oops! Someone needs to study...", Toast.LENGTH_SHORT).show();
    }

    private void loadQuiz(){

        Intent intent = getIntent();
        String tag = intent.getStringExtra("quizTag");

        Toast.makeText(QuizzActivity.this, "Fetching Quiz...", Toast.LENGTH_SHORT).show();
        GetQuizTask getQuizTask = new GetQuizTask(tag);
        getQuizTask.execute((Void) null);
    }

    public class GetQuizTask extends AsyncTask<Void, Void, String> {
        private String tag;

        GetQuizTask(String tag) {
            this.tag = tag;
            Log.d(TAG, "GetQuizTask: " + tag);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                SharedPreferences authentication = getSharedPreferences("AUTHENTICATION", 0);
                String token = authentication.getString("token",null);

                JSONObject jsonTag = new JSONObject();
                jsonTag.accumulate("search", tag);

                return RequestsREST.doPOST(new URL("https://apdc-geoproj.ey.r.appspot.com/rest/quizz/getRandom"), jsonTag, token);
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String result) {

            if (result != null && !result.equals("{}")) {

                Log.d(TAG, "onPostExecute: " + result);

                Gson gson = new Gson();

                quiz = gson.fromJson(result, QuizData.class);
                questions = new ArrayList<>(quiz.getQuestions());
                totalQuestions = questions.size();
                createContent(questions.remove(0));
                quizLayout.setVisibility(View.VISIBLE);

                Toast.makeText(QuizzActivity.this, "Quiz Ready!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(QuizzActivity.this, "Quiz failed to Load.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}