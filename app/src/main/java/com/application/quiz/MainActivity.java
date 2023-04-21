package com.application.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText name;
    Button startQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        startQuizButton = findViewById(R.id.startQuizButton);

        // Set user's name
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("name")) {
            name.setText(extras.getString("name"));
        }

        startQuizButton.setOnClickListener(view -> {
            openQuizActivity();
        });
    }

    public void openQuizActivity() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("name", name.getText().toString());
        startActivity(intent);
    }
}