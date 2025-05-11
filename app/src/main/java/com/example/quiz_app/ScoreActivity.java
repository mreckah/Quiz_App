package com.example.quiz_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    private TextView scoreTextView;
    private ProgressBar scoreProgressBar;
    private Button replayButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scoreTextView = findViewById(R.id.scoreTextView);
        scoreProgressBar = findViewById(R.id.scoreProgressBar);
        replayButton = findViewById(R.id.replayButton);
        logoutButton = findViewById(R.id.logoutButton);

        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 5);

        // Calculate percentage
        int percentage = (score * 100) / totalQuestions;

        // Update UI
        scoreTextView.setText(score + "/" + totalQuestions);
        scoreProgressBar.setMax(100);
        scoreProgressBar.setProgress(percentage);

        replayButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        logoutButton.setOnClickListener(v -> {
            // Clear any stored user data here if needed
            Intent intent = new Intent(ScoreActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}