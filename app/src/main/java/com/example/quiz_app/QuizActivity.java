package com.example.quiz_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private TextView questionTextView;
    private TextView usernameTextView;
    private TextView timerTextView;
    private Button[] optionButtons;
    private Button nextButton;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswerSelected = false;
    private long startTime;
    private CountDownTimer countDownTimer;
    private static final long QUIZ_TIME_MILLIS = 60000; // 1 minute in milliseconds
    private String username;
    private DatabaseReference scoresRef;

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

        try {
            // Initialize Firebase Database reference
            scoresRef = FirebaseDatabase.getInstance().getReference("scores");

            // Initialize views
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

            // Set username from intent
            username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            usernameTextView.setText("Welcome, " + username);
        }

            // Start quiz
            startTime = SystemClock.elapsedRealtime();
        startTimer();
        displayQuestion();

            // Set up option button click listeners
            for (int i = 0; i < optionButtons.length; i++) {
                final int buttonIndex = i;
                optionButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!isAnswerSelected) {
                                handleOptionClick(v);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in option click: " + e.getMessage());
                            Toast.makeText(QuizActivity.this, "Error selecting answer. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            // Set up next button click listener
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        handleNextClick();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in next click: " + e.getMessage());
                        Toast.makeText(QuizActivity.this, "Error moving to next question. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing quiz. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void handleOptionClick(View v) {
                Button clickedButton = (Button) v;
                String selectedAnswer = clickedButton.getText().toString();
                int correctIndex = Integer.parseInt(questions[currentQuestionIndex][5]) - 1;
                String correctAnswer = questions[currentQuestionIndex][correctIndex + 1];

        // Disable all option buttons
                for (Button b : optionButtons) {
                    b.setEnabled(false);
                }

        // Show feedback
                if (selectedAnswer.equals(correctAnswer)) {
                    score++;
                    clickedButton.setBackgroundColor(Color.GREEN);
                    showFeedbackAnimation("Correct!", Color.GREEN);
                } else {
                    clickedButton.setBackgroundColor(Color.RED);
            // Show correct answer
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
        }

    private void handleNextClick() {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
                displayQuestion();
            startTimer();
            } else {
            // Quiz completed
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            long timeElapsed = SystemClock.elapsedRealtime() - startTime;
            saveScore();
                Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
                intent.putExtra("SCORE", score);
                intent.putExtra("TOTAL_QUESTIONS", questions.length);
                intent.putExtra("TIME_ELAPSED", timeElapsed);
                startActivity(intent);
                finish();
            }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(QUIZ_TIME_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format("%02d:%02d",
                        millisUntilFinished / 1000 / 60,
                        millisUntilFinished / 1000 % 60));
            }

            @Override
            public void onFinish() {
                if (!isAnswerSelected) {
                    moveToNextQuestion();
                }
            }
        }.start();
    }

    private void moveToNextQuestion() {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            displayQuestion();
            startTimer();
        } else {
            // Quiz completed
            saveScore();
        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questions.length);
        startActivity(intent);
        finish();
        }
    }

    private void saveScore() {
        if (username != null) {
            DatabaseReference userScoreRef = scoresRef.child(username);
            userScoreRef.child("username").setValue(username);
            userScoreRef.child("score").setValue(score);
        }
    }

    private void displayQuestion() {
        // Reset state
        isAnswerSelected = false;
        nextButton.setEnabled(false);

        // Set question text
        questionTextView.setText(questions[currentQuestionIndex][0]);

        // Set option buttons
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questions[currentQuestionIndex][i + 1]);
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackgroundColor(Color.parseColor("#6200EE")); // Reset to default color
        }
    }

    private void showFeedbackAnimation(String message, int color) {
        TextView feedbackText = new TextView(this);
        feedbackText.setText(message);
        feedbackText.setTextColor(color);
        feedbackText.setTextSize(24);
        feedbackText.setAlpha(0f);

        ViewGroup parent = (ViewGroup) questionTextView.getParent();
        parent.addView(feedbackText);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(500);
        fadeOut.setStartOffset(1000);
        fadeOut.setFillAfter(true);

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
