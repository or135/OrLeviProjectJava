package com.example.orleviprojectjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextGmail, editTextPassword;
    private Button buttonLogin, buttonSingUp;
    private FirebaseAuth mAuth;
    AuthManager authManager;

    @SuppressLint("MissingInflatedId")
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
        buttonSingUp = findViewById(R.id.buttonSignUp);
        buttonSingUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonSingUp)
            registerUser();
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
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(MainActivity.this, "Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
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
        if(password.length()<6){
            editTextPassword.setError("Password should be at least 6 characters ");
            return;
        }


        authManager.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed unknown error" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
