package com.example.quiz_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Here you would typically register the user with a backend
            // For now, we'll just show success message and go to login
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();

            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            // Pass the email to pre-fill the login form
            intent.putExtra("EMAIL", email);
            startActivity(intent);
            finish();
        });
    }
}