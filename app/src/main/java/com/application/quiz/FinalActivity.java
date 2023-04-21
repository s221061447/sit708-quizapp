package com.application.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.MessageFormat;

public class FinalActivity extends AppCompatActivity {

    TextView congratulationsText;
    TextView score;
    Button newQuizButton;
    Button finishButton;

    String name;
    int correctAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        // Set user's name
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            correctAnswers = extras.getInt("correct");
        }

        congratulationsText = findViewById(R.id.congratulationsText);
        score = findViewById(R.id.score);
        newQuizButton = findViewById(R.id.newQuizButton);
        finishButton = findViewById(R.id.finishButton);

        congratulationsText.setText(MessageFormat.format("Congratulations {0}!", name));
        score.setText(MessageFormat.format("{0}/5", correctAnswers));

        newQuizButton.setOnClickListener(view -> {
            openMainActivity();
        });

        finishButton.setOnClickListener(view -> {
            finishAffinity();
        });

    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
    }
}