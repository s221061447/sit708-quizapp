package com.application.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.quiz.entity.QuestionAnswer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class QuizActivity extends AppCompatActivity {

    ObjectMapper objectMapper = new ObjectMapper();

    TextView progress;
    ProgressBar progressBar;
    TextView question;
    Button choice1Button;
    Button choice2Button;
    Button choice3Button;
    Button choice4Button;
    List<Button> choices;
    Button checkButton;
    String choice;

    // Default button color
    int defaultButtonColor;

    String name;
    List<QuestionAnswer> questionAnswers;
    int questionCounter = 0;
    int correctAnswers = 0;

    int lightGray = Color.LTGRAY;
    int green = Color.GREEN;
    int red = Color.RED;
    int purple = Color.parseColor("#3F51B5");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Set user's name
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
        }

        // Get questions
        InputStream inputStream = getResources().openRawResource(R.raw.questions);
        try {
            questionAnswers = objectMapper.readValue(inputStream, new TypeReference<List<QuestionAnswer>>() {});
            questionAnswers = pickNRandomElements(questionAnswers, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Assign elements
        progress = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        question = findViewById(R.id.question);
        choice1Button = findViewById(R.id.choice1Button);
        choice2Button = findViewById(R.id.choice2Button);
        choice3Button = findViewById(R.id.choice3Button);
        choice4Button = findViewById(R.id.choice4Button);
        choices = List.of(choice1Button, choice2Button, choice3Button, choice4Button);
        checkButton = findViewById(R.id.checkButton);

        // Set default button color
        defaultButtonColor = getButtonColor(choice1Button);

        // disable check button
        disableAndHideButton(checkButton);

        // Set progress to total to 5
        progressBar.setMax(5);

        // Set question and choices for question 1 initially
        try {
            setQuestionAndChoices(questionCounter);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        choice1Button.setOnClickListener(view -> {
            selectChoice(0);
        });

        choice2Button.setOnClickListener(view -> {
            selectChoice(1);
        });

        choice3Button.setOnClickListener(view -> {
            selectChoice(2);
        });

        choice4Button.setOnClickListener(view -> {
            selectChoice(3);
        });

        checkButton.setOnClickListener(view -> {
            // if button state is in check -> check answer and change state to next
            if (checkButton.getText().toString().equalsIgnoreCase("check")) {
                // check answer
                checkAnswer(questionCounter);

                // increment question counter
                questionCounter++;
                // update progress
                updateProgress(questionCounter);

                // change state to next or finish
                checkButton.setText(R.string.next);
            } else if (checkButton.getText().toString().equalsIgnoreCase("next")) {
                // if button state is in next -> refresh questions, choices, buttons, and change state to next and disabled
                if (questionCounter == 5) {
                    // Quiz complete
                    // do last changes and shift to final activity
                    openFinalActivity();
                } else {
                    // Set new question and choices, and reset button states if not last question
                    resetAllButtons();
                    try {
                        setQuestionAndChoices(questionCounter);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Do nothing
            }
        });

    }

    private void selectChoice(int choiceNumber) {
        for (int i=0; i<4; i++) {
            if (i == choiceNumber) {
                choices.get(i).setEnabled(false);
                choices.get(i).setBackgroundColor(lightGray);
                choice = (String) choices.get(i).getText();
            } else {
                choices.get(i).setEnabled(true);
                choices.get(i).setBackgroundColor(purple);
            }
        }

        // if the check button is disabled, enable it
        enableAndShowButton(checkButton);
    }

    private void setQuestionAndChoices(int questionNumber) throws JsonProcessingException {
        // Set question
        question.setText(questionAnswers.get(questionNumber).getQuestion());

        System.out.println(objectMapper.writeValueAsString(questionAnswers.get(questionNumber)));

        // Set choices
        choice1Button.setText(questionAnswers.get(questionNumber).getChoices().get(0));
        choice2Button.setText(questionAnswers.get(questionNumber).getChoices().get(1));
        choice3Button.setText(questionAnswers.get(questionNumber).getChoices().get(2));
        choice4Button.setText(questionAnswers.get(questionNumber).getChoices().get(3));
    }

    private String getAnswerForQuestionNumber(int questionNumber) {
        return questionAnswers.get(questionNumber).getAnswer();
    }

    private void checkAnswer(int questionNumber) {
        String answer = getAnswerForQuestionNumber(questionNumber);
        for (int i=0; i<4; i++) {
            if (choices.get(i).getText().toString().equalsIgnoreCase(answer)) {
                // Correct answer
                choices.get(i).setBackgroundColor(green);
                correctAnswers++;
            } else if (!choices.get(i).getText().toString().equalsIgnoreCase(answer) && (getButtonColor(choices.get(i)) == lightGray)) {
                // Wrong answer
                choices.get(i).setBackgroundColor(red);
                correctAnswers--;
            } else {
                choices.get(i).setBackgroundColor(lightGray);
            }

            // Disable selecting choices
            choices.get(i).setEnabled(false);
        }
    }

    private void disableAndHideButton(Button button) {
        // Set the button to be visible and interactable
        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
    }

    private void enableAndShowButton(Button button) {
        // Set the button to be visible and interactable
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
    }

    private void resetAllButtons() {
        for (int i=0; i<4; i++) {
            enableAndShowButton(choices.get(i));
            choices.get(i).setBackgroundColor(purple);
        }

        checkButton.setText(R.string.check);
        disableAndHideButton(checkButton);
    }

    private void updateProgress(int questionsCompleted) {
        // Update progress text
        progress.setText(MessageFormat.format("{0}/5", questionsCompleted));

        // Update progress bar
        progressBar.setProgress(questionsCompleted, true);
    }

    // Get a button's color
    private int getButtonColor(Button button) {
        ColorDrawable colorDrawable = (ColorDrawable)button.getBackground();
        return colorDrawable.getColor();
    }

    public void openFinalActivity() {
        Intent intent = new Intent(this, FinalActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("correct", correctAnswers);
        startActivity(intent);
    }

    private static <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
        int length = list.size();

        if (length < n) return null;

        //We don't need to shuffle the whole list
        for (int i = length - 1; i >= length - n; --i)
        {
            Collections.swap(list, i , r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    private static <E> List<E> pickNRandomElements(List<E> list, int n) {
        return pickNRandomElements(list, n, ThreadLocalRandom.current());
    }

}