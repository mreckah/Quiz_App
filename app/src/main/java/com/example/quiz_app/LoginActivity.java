package com.example.quiz_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Check if we have an email from registration
        String registeredEmail = getIntent().getStringExtra("EMAIL");
        if (registeredEmail != null) {
            emailEditText.setText(registeredEmail);
        }

        loginButton.setOnClickListener(v -> {
            String inputEmail = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (inputEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Here you would typically validate credentials against a backend
            // For now, we'll just proceed to the quiz
            Intent intent = new Intent(LoginActivity.this, QuizActivity.class);
            // Extract username from email (everything before @)
            String username = inputEmail.split("@")[0];
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
}