package com.example.orleviprojectjava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextGmail, editTextPassword;
    private Button buttonLogin, buttonSingUp;
    private FirebaseAuth mAuth;
    AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        authManager = new AuthManager();

        editTextGmail = findViewById(R.id.editTextGmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        buttonSingUp = findViewById(R.id.buttonSingUp);
        buttonSingUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSingUp)// Navigate to RegisterActivity
            startActivity(new Intent(MainActivity.this, CreateUserActivity.class));
        if (v == buttonLogin)
            loginUser();
    }

    private void loginUser() {
        String email = editTextGmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextGmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }
        authManager.loginUser(email, password, task -> {
            if (task.isSuccessful()) {
                // Sign-in success
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(MainActivity.this, "Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            } else {
                // If sign-in fails
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
