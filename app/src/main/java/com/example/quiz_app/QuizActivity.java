package com.example.quiz_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    private TextView questionTextView;
    private TextView usernameTextView;
    private TextView timerTextView;
    private Button[] optionButtons;
    private Button nextButton;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswerSelected = false;
    private long timeElapsed = 0;
    private CountDownTimer countDownTimer;
    private static final long QUIZ_TIME_MILLIS = 60000; // 1 minute in milliseconds

    // Questions: question, option1, option2, option3, option4, correctOptionIndex
    // (1-based)
    private String[][] questions = {
            { "Which muscle group do squats primarily target?", "Chest", "Back", "Legs", "Arms", "3" },
            { "How many calories are in 1 gram of protein?", "4", "9", "7", "2", "1" },
            { "What is the recommended daily water intake?", "1 liter", "2 liters", "3 liters", "4 liters", "2" },
            { "Which exercise is best for building chest muscles?", "Squats", "Bench Press", "Deadlifts", "Pull-ups",
                    "2" },
            { "What is BMI short for?", "Body Muscle Index", "Body Mass Index", "Bone Mass Indicator",
                    "Basic Metabolic Index", "2" }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.questionTextView);
        usernameTextView = findViewById(R.id.usernameTextView);
        timerTextView = findViewById(R.id.chronometer);
        optionButtons = new Button[] {
                findViewById(R.id.option1Button),
                findViewById(R.id.option2Button),
                findViewById(R.id.option3Button),
                findViewById(R.id.option4Button)
        };
        nextButton = findViewById(R.id.nextButton);

        // Set usernam from intent
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            usernameTextView.setText("Welcome, " + username);
        }

        startTimer();

        displayQuestion();

        for (Button button : optionButtons) {
            button.setOnClickListener(v -> {
                if (isAnswerSelected)
                    return;

                Button clickedButton = (Button) v;
                String selectedAnswer = clickedButton.getText().toString();
                int correctIndex = Integer.parseInt(questions[currentQuestionIndex][5]) - 1;
                String correctAnswer = questions[currentQuestionIndex][correctIndex + 1];

                // Disable all buttons
                for (Button b : optionButtons) {
                    b.setEnabled(false);
                }

                // Show animation and color feedback
                if (selectedAnswer.equals(correctAnswer)) {
                    score++;
                    clickedButton.setBackgroundColor(Color.GREEN);
                    showFeedbackAnimation("Correct!", Color.GREEN);
                } else {
                    clickedButton.setBackgroundColor(Color.RED);
                    // Find and highlight correct answer
                    for (Button b : optionButtons) {
                        if (b.getText().toString().equals(correctAnswer)) {
                            b.setBackgroundColor(Color.GREEN);
                            break;
                        }
                    }
                    showFeedbackAnimation("Incorrect!", Color.RED);
                }

                isAnswerSelected = true;
                nextButton.setEnabled(true);
            });
        }

        nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                displayQuestion();
            } else {
                // Stop countdown timer and get elapsed time
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                timeElapsed = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis();

                Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("TOTAL_QUESTIONS", questions.length);
                intent.putExtra("TIME_ELAPSED", timeElapsed);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(QUIZ_TIME_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update timer text
                int seconds = (int) (millisUntilFinished / 1000);
                timerTextView.setText(String.format("Time: %02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                // Time's up! Go to score screen
                goToScoreScreen();
            }
        }.start();
    }

    private void goToScoreScreen() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questions.length);
        intent.putExtra("TIME_ELAPSED", timeElapsed);
        startActivity(intent);
        finish();
    }

    private void displayQuestion() {
        questionTextView.setText(questions[currentQuestionIndex][0]);
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questions[currentQuestionIndex][i + 1]);
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackgroundColor(Color.parseColor("#6200EE")); // Reset to default color
        }
        nextButton.setEnabled(false);
        isAnswerSelected = false;
    }

    private void showFeedbackAnimation(String message, int color) {
        TextView feedbackText = new TextView(this);
        feedbackText.setText(message);
        feedbackText.setTextColor(color);
        feedbackText.setTextSize(24);
        feedbackText.setAlpha(0f);

        // Get the parent ViewGroup (LinearLayout)
        ViewGroup parent = (ViewGroup) questionTextView.getParent();

        // Add to layout
        parent.addView(feedbackText);

        // Create fade in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        // Create fade out animation
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(1000);
        fadeOut.setFillAfter(true);

        // Start animations
        feedbackText.startAnimation(fadeIn);
        new Handler().postDelayed(() -> {
            feedbackText.startAnimation(fadeOut);
            new Handler().postDelayed(() -> {
                parent.removeView(feedbackText);
            }, 500);
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
